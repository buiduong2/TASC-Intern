import { Injectable } from '@angular/core';
import { TokenRes } from '../types/oauth.type';

@Injectable({ providedIn: 'root' })
export class AuthTokenRepository {
  public readonly KEY = 'auth_token';

  load(): TokenRes | null {
    const raw = localStorage.getItem(this.KEY);
    return raw ? (JSON.parse(raw) as TokenRes) : null;
  }

  save(token: TokenRes): void {
    localStorage.setItem(this.KEY, JSON.stringify(token));
  }

  clear(): void {
    localStorage.removeItem(this.KEY);
  }
}
