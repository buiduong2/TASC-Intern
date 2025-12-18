import { httpResource } from '@angular/common/http';
import { Injectable, signal } from '@angular/core';
import { PageResponse } from '../types/res.type';

export interface ProductSummary {
  id: number;
  name: string;
  imageUrl: string | null;
  salePrice: number | null;
  compareAtPrice: number;
  stock: number;
}

export interface ProductDetail {
  id: number;
  name: string;
  imageUrl: string | null;
  description: string;
  salePrice: number | null;
  compareAtPrice: number;
  stock: number;
  categoryId: number;
  tags: {
    id: number;
    name: string;
  }[];
  relates: ProductSummary[];
}
@Injectable({ providedIn: 'root' })
export class ProductService {
  public productId = signal<number | null>(null);

  public productDetail = httpResource<ProductDetail>(() => {
    const id = this.productId();
    if (!id) return undefined;
    return {
      url: `/v1/products/${id}`,
      method: 'GET',
    };
  });

  page = signal(0);
  size = signal(10);
  categoryId = signal<number | null>(null);

  public productSummary = httpResource<PageResponse<ProductSummary>>(() => {
    if (this.categoryId() == null) {
      return undefined;
    }

    return {
      url: `/v1/products/category/${this.categoryId()}`,
      method: 'GET',
      params: {
        page: this.page(),
        size: this.size(),
      },
    };
  });

  loadSummary(page: number, size: number) {
    this.page.set(page);
    this.size.set(size);
  }

  loadDetail(id: number) {
    this.productId.set(id);
  }

  clearDetail() {}
}
