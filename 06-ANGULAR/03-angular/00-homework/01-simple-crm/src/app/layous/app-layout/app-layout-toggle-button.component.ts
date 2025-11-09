import { Component, inject, Input, OnInit } from '@angular/core';
import { AppLayoutStateService } from './app-layout-state.service';

@Component({
  selector: 'app-layout-toggle-button',
  template: `
    <button (click)="toggle()" [class]="customClass"><ng-content>Toggle Button</ng-content></button>
  `,
})
export class AppLayoutToggleButton {
  private layoutService = inject(AppLayoutStateService);
  @Input('class') customClass: string = '';

  constructor() {}

  toggle() {
    this.layoutService.toggleCompact();
  }
}
