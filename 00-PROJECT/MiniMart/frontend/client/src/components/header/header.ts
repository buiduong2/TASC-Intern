import { Component, OnInit } from '@angular/core';
import { MatButtonModule } from '@angular/material/button';
import { RouterLink } from '@angular/router';

@Component({
  selector: 'app-header',
  templateUrl: './header.html',
  styleUrls: ['./header.css'],
  imports: [MatButtonModule, RouterLink],
})
export class HeaderComponent implements OnInit {
  constructor() {}

  clickNavBar() {
    console.log('Hello wolr');
  }

  ngOnInit() {}
}
