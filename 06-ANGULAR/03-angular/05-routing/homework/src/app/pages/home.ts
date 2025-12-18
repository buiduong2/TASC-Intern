import { Component, signal } from '@angular/core';

@Component({
  selector: 'Home',
  template: ``,
  standalone: true,
})
export class HomeComponent {
  protected readonly title = signal('homework');
}
