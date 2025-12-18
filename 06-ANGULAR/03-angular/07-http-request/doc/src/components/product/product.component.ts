import { Component, computed } from '@angular/core';
import { ProductService } from './product.service';

@Component({
  selector: 'app-product',
  templateUrl: './product.component.html',
  styleUrls: ['./product.component.css'],
})
export class ProductComponent {
  todoData: any;

  constructor(private productService: ProductService) {
    this.todoData = computed(() => JSON.stringify(productService.get()()));
  }
}
