import { Injectable } from '@angular/core';
import { ProductDataSource } from '../../data/product/product.datasource';
import { ProductQuery } from '../../models/product/product-query.model';
import { Pageable } from '../../models/common/page.model';

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
