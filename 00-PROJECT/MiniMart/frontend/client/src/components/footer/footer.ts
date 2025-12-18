import { Component, OnInit } from '@angular/core';
import { MatAnchor } from '@angular/material/button';
import { MatInputModule } from '@angular/material/input';

@Component({
  selector: 'app-footer',
  templateUrl: './footer.html',
  imports: [MatAnchor, MatInputModule],
})
export class FooterComponent implements OnInit {
  constructor() {}

  ngOnInit() {}
}
