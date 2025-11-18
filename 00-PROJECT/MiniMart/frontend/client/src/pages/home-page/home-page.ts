import { Component, OnInit } from '@angular/core';
import { HeroComponent } from '../../components/hero/hero';

@Component({
  selector: 'app-home-page',
  templateUrl: './home-page.html',
  styleUrls: ['./home-page.css'],
  imports: [HeroComponent],
})
export class HomePage implements OnInit {
  constructor() {}

  ngOnInit() {}
}
