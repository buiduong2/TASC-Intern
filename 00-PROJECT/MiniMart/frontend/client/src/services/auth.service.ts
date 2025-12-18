import { computed, effect, Inject, Injectable, Signal, signal } from '@angular/core';
import SuperTokensLock from 'browser-tabs-lock';
import { AuthRepository, Profile } from '../repositories/auth.repository';
import { Authority, TokenRes } from '../types/oauth.type';
import { AuthTokenService } from './auth-token.service';
import { LocalStorageListner } from './local-storage.listener';
import { ProfileSuplier, TokenExchanger } from '../utils/auth/auth-suplier.utils';

@Injectable({ providedIn: 'root' })
export class AuthService {
  profile = signal<Profile | null>(null);
  roles: Signal<Authority[]>;
  isAuthenticated: Signal<boolean>;
  routerToGo: string | null;

  private readonly tokenService: AuthTokenService;
  private readonly repo: AuthRepository;
  private readonly profileSuplier: ProfileSuplier;
  private readonly tokenExchanger: TokenExchanger;
  private readonly lock: SuperTokensLock;

  constructor(
    localStorageListener: LocalStorageListner,
    tokenService: AuthTokenService,
    repo: AuthRepository,
    @Inject('PROFILE_SUPLIER') profileSuplier: ProfileSuplier,
    @Inject('TOKEN_EXCHANGER') tokenExchanger: TokenExchanger,
    @Inject('DISTRIBUTED_LOCK_INSTANCE') lock: SuperTokensLock,
  ) {
    this.routerToGo = null;
    this.tokenService = tokenService;
    this.repo = repo;
    this.lock = lock;
    this.profileSuplier = profileSuplier;
    this.tokenExchanger = tokenExchanger;
    this.isAuthenticated = computed(() => Boolean(this.profile()));
    this.roles = computed(() => {
      const body = this.tokenService.accessTokenBody();
      if (!body) {
        return [];
      }

      return body.authorities.map((auth) => Authority[auth]);
    });

    effect(() => {
      const keyChange = localStorageListener.storageChangeKey();
      if (keyChange == this.repo.KEY) {
        const profile = this.repo.load();
        if (profile == null) {
          this.profile.set(null);
        } else {
          this.updateMemoryProfile(profile);
        }
      }
    });
  }

  public async login(
    code: string,
    verifier: string,
  ): Promise<{ success: boolean; routeToGo: string | null }> {
    let success = true;

    try {
      const tokenRes = await this.tokenExchanger(code, verifier);
      this.tokenService.setupSession(tokenRes);
      const accessToken = await this.tokenService.getAccessToken();

      const profile = await this.profileSuplier(accessToken);
      this.repo.save(profile);
      this.setupSession(profile);
    } catch (error) {
      console.log(error);
      success = false;
    }

    return { success, routeToGo: this.routerToGo };
  }

  public async logout() {
    this.repo.clear();
    this.clearSession();
    this.tokenService.clearSession();
  }

  private async updateMemoryProfile(profile: Profile) {
    this.profile.set(profile);
  }

  public async intializeProfile(): Promise<void> {
    let cachedProfile = this.repo.load();
    if (cachedProfile != null) {
      this.updateMemoryProfile(cachedProfile);
      return;
    }

    let acquired = await this.lock.acquireLock('AUTH_LOAD_LOCK', 5000);
    let e: any = null;

    cachedProfile = this.repo.load();
    if (cachedProfile != null) {
      this.updateMemoryProfile(cachedProfile);
      return;
    }

    try {
      const accessToken = await this.tokenService.getAccessToken();
      const newProfile = await this.profileSuplier(accessToken);

      this.repo.save(newProfile);
      this.updateMemoryProfile(newProfile);
      return;
    } catch (error) {
      e = error;
      console.warn('Failed to load profile, token issue.');
      this.clearSession();
    } finally {
      if (acquired) {
        await this.lock.releaseLock('AUTH_LOAD_LOCK');
      }
    }
    throw e;
  }

  private setupSession(profile: Profile) {
    this.profile.set(profile);
  }

  private clearSession() {
    this.profile.set(null);
  }
}
