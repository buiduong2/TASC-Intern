import { Component, OnInit } from '@angular/core';
import { MatButtonModule } from '@angular/material/button';
import { MatIconModule, MatIcon } from '@angular/material/icon';
import { MatListModule, MatNavList } from '@angular/material/list';
import { MatCardModule } from '@angular/material/card';
import { AuthService } from '../../services/auth.service';
@Component({
  selector: 'app-account-profile-page',
  templateUrl: './account-profile-page.html',
  imports: [MatIcon, MatIconModule, MatButtonModule, MatListModule, MatCardModule],
})
export class AccountProfilePage implements OnInit {
  constructor(public auth: AuthService) {}

  ngOnInit() {}
}
