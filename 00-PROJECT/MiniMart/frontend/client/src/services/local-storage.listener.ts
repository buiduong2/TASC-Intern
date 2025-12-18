import { Injectable, signal } from '@angular/core';

@Injectable({ providedIn: 'root' })
export class LocalStorageListner {
  public storageChangeKey = signal<string | null>(null);

  constructor() {
    window.addEventListener('storage', this.handleOnStorageChange);
  }

  handleOnStorageChange = (ev: StorageEvent) => {
    if (ev.key) {
      this.storageChangeKey.set(ev.key);
    }
  };
}
