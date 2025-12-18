import { Component, OnInit } from '@angular/core';
import { MatButtonModule } from '@angular/material/button';
import { RouterLink } from '@angular/router';

@Component({
  selector: 'app-hero',
  templateUrl: './hero.html',
  imports: [MatButtonModule, RouterLink],
})
export class HeroComponent implements OnInit {
  constructor() {}

  ngOnInit() {}
}
