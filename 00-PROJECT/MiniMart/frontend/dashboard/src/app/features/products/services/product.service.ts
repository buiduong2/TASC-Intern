import { Injectable } from '@angular/core';
import { ProductDataSource } from '../data/product.datasource';
import { ProductQuery } from '@products/models/product-query.model';
import { Pageable } from '@shared/models/page.model';

@Injectable({
  providedIn: 'root',
})
export class ProductService {
  constructor(private datasource: ProductDataSource) {}

  findAll(query: ProductQuery, pageable: Pageable<Product>) {
    return this.datasource.findAll(query, {
      page: pageable.page ?? 0,
      size: pageable.size ?? 10,
      sort: [],
    });
  }
}
