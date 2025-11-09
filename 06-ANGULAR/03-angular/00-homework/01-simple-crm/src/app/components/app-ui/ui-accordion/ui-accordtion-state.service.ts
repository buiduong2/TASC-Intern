import { Injectable, signal } from '@angular/core';

@Injectable()
export class UIAccordionStateService {
  public isOpen = signal(false);

  public id = 0;

  toggle() {
    this.isOpen.update((val) => !val);
  }

  
  setOpen(value: boolean) {
    this.isOpen.set(value);
  }
}
