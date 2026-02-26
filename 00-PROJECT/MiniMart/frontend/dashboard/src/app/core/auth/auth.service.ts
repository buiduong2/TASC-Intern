import { Injectable } from '@angular/core';
import { Observable, of } from 'rxjs';

export interface User {
  name: string;
  email?: string;
  role: string;
  avatarUrl?: string;
}

@Injectable({
  providedIn: 'root',
})
export class AuthService {
  user$: Observable<User>;
  isAuthenticated$: Observable<boolean>;

  constructor() {
    this.user$ = of({
      name: 'Duong',
      email: 'Duong@gmail.com',
      role: 'ADMIN',
      avatarUrl: undefined,
    });

    this.isAuthenticated$ = of(true);
  }
}
