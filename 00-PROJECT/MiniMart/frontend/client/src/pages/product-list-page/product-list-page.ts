import { Component, OnInit } from '@angular/core';
import PRODUCT_PAGE from '../../data/products.json';
import { ProductCardComponent } from '../../components/product-card/product-card';
@Component({
  selector: 'app-product-list-page',
  templateUrl: './product-list-page.html',
  styleUrls: ['./product-list-page.css'],
  imports: [ProductCardComponent],
})
export class ProductListPage implements OnInit {
  page: PageResponseDTO<any>;
  products: ProductSummaryDTO[];
  constructor() {
    this.page = { ...PRODUCT_PAGE, content: [] };
    this.products = PRODUCT_PAGE.content;
  }

  ngOnInit() {}
}

export interface ProductSummaryDTO {
  id: number;
  name: string;
  imageUrl: string;
  salePrice: number | null;
  compareAtPrice: number;
  stock: number;
}
export interface PageResponseDTO<T> {
  content: T[];
  totalElements: number;
  totalPages: number;
  pageNumber: number;
  pageSize: number;
  last: boolean;
  first: boolean;
  empty: boolean;
}
