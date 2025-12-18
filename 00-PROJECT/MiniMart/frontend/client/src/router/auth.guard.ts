import { inject } from '@angular/core';
import { Router } from '@angular/router';
import { AuthService } from '../services/auth.service';

export function authGuard(requireAuth: boolean) {
  return () => {
    const auth = inject(AuthService);
    const router = inject(Router);
    const isAuthenticated = auth.isAuthenticated();

    if (requireAuth && !isAuthenticated) {
      return router.createUrlTree(['/login']);
    }

    if (isAuthenticated && !requireAuth) {
      return router.createUrlTree(['/']);
    }

    return true;
  };
}
