import { Component, OnInit } from '@angular/core';
import { RouterOutlet } from '@angular/router';

@Component({
  selector: 'app-error-layout',
  templateUrl: './error-layout.component.html',
  styleUrls: ['./error-layout.component.css'],
  imports: [RouterOutlet],
})
export class ErrorLayoutComponent implements OnInit {
  constructor() {}

  ngOnInit() {}
}
