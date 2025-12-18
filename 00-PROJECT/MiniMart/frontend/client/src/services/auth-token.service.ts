import { effect, Inject, Injectable, signal, WritableSignal } from '@angular/core';
import SuperTokensLock from 'browser-tabs-lock';
import { AuthTokenRepository } from '../repositories/auth-token.repository';
import { AccessTokenBody, TokenRes } from '../types/oauth.type';
import { parseAccessToken } from '../utils/auth/oauth.utils';
import { isExired } from '../utils/commons/time.utils';
import { LocalStorageListner } from './local-storage.listener';
import { TokenSuplier } from '../utils/auth/auth-suplier.utils';

@Injectable({ providedIn: 'root' })
export class AuthTokenService {
  private tokenRes: TokenRes | null;
  public accessTokenBody: WritableSignal<AccessTokenBody | null>;
  private isRefeshing = false;
  private pendingRequests: ((tokenRes?: TokenRes) => void)[];
  private readonly thresHoldMs: number;
  private readonly tokenSuplier: TokenSuplier;
  private readonly repository: AuthTokenRepository;
  private readonly refreshLock: SuperTokensLock;
  private static readonly REFRESH_LOCK_KEY = 'REFRESH_LOCK';

  public readonly KEY: string;

  constructor(
    @Inject('TOKEN_TTL_THRESHOLD') thresHoldMs: number,
    @Inject('TOKEN_SUPLIER') tokenSuplier: TokenSuplier,
    @Inject('DISTRIBUTED_LOCK_INSTANCE') refreshLock: SuperTokensLock,
    localStorageListener: LocalStorageListner,
    repository: AuthTokenRepository,
  ) {
    this.KEY = repository.KEY;
    this.tokenRes = repository.load();

    if (this.tokenRes) {
      this.accessTokenBody = signal(parseAccessToken(this.tokenRes.access_token));
    } else {
      this.accessTokenBody = signal(null);
    }

    this.thresHoldMs = thresHoldMs;
    this.pendingRequests = [];

    this.tokenSuplier = tokenSuplier;
    this.repository = repository;
    this.refreshLock = refreshLock;

    effect(() => {
      const keyChange = localStorageListener.storageChangeKey();
      if (keyChange == this.KEY) {
        const newTokenRes = this.repository.load();
        if (newTokenRes != null) {
          this.updateMemoryToken(newTokenRes);
        } else {
          this.removeMemoryToken();
        }
      }
    });
  }

  public async getAccessToken(): Promise<string> {
    const tokenRes = await this.getTokenRes();
    return tokenRes.access_token;
  }

  public async getRefreshToken(): Promise<string> {
    if (this.tokenRes != null) {
      return this.tokenRes.refresh_token;
    }
    throw new Error('Token Res is null');
  }

  public async getIdToken(): Promise<String> {
    const tokenRes = await this.getTokenRes();
    return tokenRes.id_token;
  }

  private async getTokenRes(): Promise<TokenRes> {
    if (this.tokenRes == null || this.accessTokenBody() == null) {
      throw new Error('Token State is null');
    }

    if (!isTokenAboutToExpire(this.accessTokenBody()!, this.thresHoldMs)) {
      return this.tokenRes;
    }

    let e: any = null;
    let acquired = false;

    try {
      acquired = await this.refreshLock.acquireLock(AuthTokenService.REFRESH_LOCK_KEY, 5000);
      const latestToken = this.repository.load();

      if (latestToken) {
        this.updateMemoryToken(latestToken);
      }

      if (this.tokenRes == null || this.accessTokenBody == null) {
        throw new Error('Token State is null');
      }

      if (!isTokenAboutToExpire(this.accessTokenBody()!, this.thresHoldMs)) {
        return this.tokenRes;
      }

      const tokenRes = await this.refreshTokenIfNeeded();
      return tokenRes;
    } catch (error) {
      e = error;
    } finally {
      if (acquired) {
        await this.refreshLock.releaseLock(AuthTokenService.REFRESH_LOCK_KEY);
      }
    }

    throw e;
  }

  private async refreshTokenIfNeeded(): Promise<TokenRes> {
    if (this.isRefeshing) {
      return new Promise((res, rej) => {
        this.pendingRequests.push((token) => {
          if (token) {
            res(token);
          } else {
            rej(new Error('Failed to refresh token'));
          }
        });
      });
    }

    this.isRefeshing = true;

    try {
      const tokenRes: TokenRes = await this.tokenSuplier(this.tokenRes!.refresh_token);
      this.updateMemoryToken(tokenRes);
      this.repository.save(tokenRes);
      this.processPendingRequests(tokenRes);
    } catch (error) {
      this.handleRefreshTokenFailure(error);
      this.processPendingRequests(undefined);
      throw error;
    } finally {
      this.isRefeshing = false;
    }

    return this.tokenRes!;
  }

  private async handleRefreshTokenFailure(error: any) {
    console.error('Refresh Token Failed. Initiating global logout.', error);
    this.repository.clear();
    this.removeMemoryToken();
  }

  private processPendingRequests(token?: TokenRes) {
    this.pendingRequests.forEach((cb) => cb(token));
    this.pendingRequests = [];
  }

  private updateMemoryToken(tokenRes: TokenRes) {
    this.tokenRes = tokenRes;
    this.accessTokenBody.set(parseAccessToken(tokenRes.access_token));
  }

  private removeMemoryToken() {
    this.tokenRes = null;
    this.accessTokenBody.set(null);
  }

  public setupSession(tokenRes: TokenRes) {
    this.updateMemoryToken(tokenRes);
    this.repository.save(tokenRes);
  }

  public clearSession() {
    this.repository.clear();
    this.removeMemoryToken();
  }
}

function isTokenAboutToExpire(accessToken: AccessTokenBody, thresholdMs: number): boolean {
  const expMs = accessToken.exp * 1000;
  return isExired(expMs, thresholdMs);
}
