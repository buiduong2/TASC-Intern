import { Component, OnInit } from '@angular/core';
import { AppLayoutStateService } from './app-layout-state.service';

@Component({
  selector: 'app-layout',
  templateUrl: './app-layout.component.html',
  providers: [AppLayoutStateService],
})
export class AppLayoutComponent {
  constructor() {}
}
