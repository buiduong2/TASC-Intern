import { Component, OnInit } from '@angular/core';
import { MatButtonModule } from '@angular/material/button';
import { RouterLink } from '@angular/router';
import { AuthService } from '../../services/auth.service';
import { MatMenuModule } from '@angular/material/menu';
import { MatIconModule } from '@angular/material/icon';

@Component({
  selector: 'app-header',
  templateUrl: './header.html',
  imports: [MatButtonModule, RouterLink, MatMenuModule, MatIconModule],
})
export class HeaderComponent {
  constructor(public auth: AuthService) {}
}
