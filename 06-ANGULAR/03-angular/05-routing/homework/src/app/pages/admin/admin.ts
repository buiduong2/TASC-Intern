import { Component, signal } from '@angular/core';

@Component({
  selector: 'About',
  template: `<h1>Trang admin</h1>`,
  standalone: true,
})
export class AdminPage {
  protected readonly title = signal('homework');
}
