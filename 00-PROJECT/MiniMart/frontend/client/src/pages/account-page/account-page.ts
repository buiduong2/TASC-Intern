import { Component } from '@angular/core';
import { MatButtonModule } from '@angular/material/button';
import { MatIconModule, MatIcon } from '@angular/material/icon';
import { MatListModule, MatNavList } from '@angular/material/list';
import { MatCardModule } from '@angular/material/card';
import { AuthService } from '../../services/auth.service';
import { RouterOutlet, RouterLinkWithHref } from '@angular/router';
@Component({
  selector: 'app-account-page',
  templateUrl: './account-page.html',
  imports: [
    MatIcon,
    MatNavList,
    MatIconModule,
    MatButtonModule,
    MatListModule,
    MatCardModule,
    RouterOutlet,
    RouterLinkWithHref,
  ],
})
export class AccountPage {
  constructor(public auth: AuthService) {}
}
