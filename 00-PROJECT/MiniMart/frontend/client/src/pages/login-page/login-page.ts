import { Component, signal } from '@angular/core';
import { MatAnchor } from '@angular/material/button';
import { MatFormField, MatInputModule, MatLabel } from '@angular/material/input';
import { Router, RouterLink } from '@angular/router';
import { AuthService } from '../../services/auth.service';
import {
  generateOAuthUrl,
  generatePKCE,
  getCode,
  openOAuthPopup,
} from '../../utils/auth/oauth.utils';

@Component({
  selector: 'app-login-page',
  templateUrl: './login-page.html',
  imports: [MatFormField, MatLabel, MatInputModule, MatAnchor, RouterLink],
})
export class LoginPage {
  constructor(
    private readonly router: Router,
    private readonly authService: AuthService,
  ) {}

  isAuthenticating = signal<boolean>(false);
  error = signal<boolean>(false);

  async login(ev: Event) {
    ev.preventDefault();
    const pkce = await generatePKCE();

    const popup = openOAuthPopup(generateOAuthUrl(pkce.challenge));
    console.log(generateOAuthUrl(pkce.challenge))
    const interval = setInterval(async () => {
      if (popup?.closed) {
        clearInterval(interval);
        const code = getCode();
        if (code) {
          const { success, routeToGo } = await this.authService.login(code, pkce.verifier);
          this.isAuthenticating.set(true);
          if (success) {
            if (routeToGo) {
              this.router.navigate([routeToGo]);
            } else {
              this.router.navigate(['/']);
            }
          } else {
            this.error.set(false);
          }
        }

        this.isAuthenticating.set(false);
      }
    }, 1000);
  }
}
