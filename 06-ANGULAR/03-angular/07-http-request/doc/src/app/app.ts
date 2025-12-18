import { Component, signal } from '@angular/core';
import { ProductComponent } from '../components/product/product.component';

@Component({
  selector: 'app-root',
  imports: [ProductComponent],
  templateUrl: './app.html',
  styleUrl: './app.css',
})
export class App {
  protected readonly title = signal('doc');
}
