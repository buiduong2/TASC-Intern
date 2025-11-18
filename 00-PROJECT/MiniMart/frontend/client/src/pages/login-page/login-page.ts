import { Component, OnInit } from '@angular/core';
import { MatFormField, MatInputModule, MatLabel } from '@angular/material/input';
import { MatAnchor } from '@angular/material/button';
import { RouterLink } from "@angular/router";

@Component({
  selector: 'app-login-page',
  templateUrl: './login-page.html',
  styleUrls: ['./login-page.css'],
  imports: [MatFormField, MatLabel, MatInputModule, MatAnchor, RouterLink],
})
export class LoginPage implements OnInit {
  constructor() {}

  ngOnInit() {}
}
