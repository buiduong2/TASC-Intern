import { Injectable, signal } from '@angular/core';

@Injectable()
export class AppLayoutStateService {
  isCompact = signal(false);

  constructor() {}

  toggleCompact() {
    this.isCompact.update((v) => !v);
  }

  setExpanded() {
    this.isCompact.set(false);
  }

  setCompact() {
    this.isCompact.set(true);
  }
}
