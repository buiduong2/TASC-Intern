import { Component, computed, inject, Input, OnInit, Signal } from '@angular/core';
import { AppLayoutStateService } from './app-layout-state.service';

@Component({
  selector: 'app-layout-show-on-compact',
  template: `
    <ng-container>
      @if (isShow()) {
        <ng-content>Toggle Button</ng-content>
      }
    </ng-container>
  `,
  styles: `
    :host {
      display: contents;
    }
  `,
})
export class AppLayoutShowOnCompact {
  private layoutService = inject(AppLayoutStateService);

  isShow: Signal<boolean>;
  constructor() {
    this.isShow = computed(() => this.layoutService.isCompact());
  }
}
