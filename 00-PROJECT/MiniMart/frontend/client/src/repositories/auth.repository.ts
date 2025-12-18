import { Injectable } from '@angular/core';
import { isExired } from '../utils/commons/time.utils';

export interface Profile {
  id: number;
  fullName: string;
  username: string;
  email?: string;
  avatarUrl: string;
}
type ProfileStore = {
  data: Profile;
  exp: number;
};

@Injectable({ providedIn: 'root' })
export class AuthRepository {
  public readonly KEY = 'auth_profile';
  public readonly TTL = 5000;
  public readonly THRESHOLD = 1000;

  load(): Profile | null {
    try {
      const raw = localStorage.getItem(this.KEY);
      if (!raw) {
        return null;
      }
      const profileStore = JSON.parse(raw) as ProfileStore;
      if (isExired(profileStore.exp, this.THRESHOLD)) {
        return null;
      }

      return profileStore.data;
    } catch (error) {
      return null;
    }
  }

  save(value: Profile): void {
    const profileStore: ProfileStore = {
      data: value,
      exp: this.TTL,
    };
    localStorage.setItem(this.KEY, JSON.stringify(profileStore));
  }

  clear(): void {
    localStorage.removeItem(this.KEY);
  }
}
