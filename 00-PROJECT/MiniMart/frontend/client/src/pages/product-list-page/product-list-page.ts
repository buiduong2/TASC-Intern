import { Component, computed, effect, OnInit, signal, Signal } from '@angular/core';
import { ActivatedRoute, Router } from '@angular/router';
import { PaginatorComponent } from '../../components/paginator/paginator';
import { ProductCardComponent } from '../../components/product-card/product-card';
import PRODUCT_PAGE from '../../data/products.json';
import { ProductService, ProductSummary } from '../../services/product.service';

import { toSignal } from '@angular/core/rxjs-interop';
import { map } from 'rxjs';
import { PageResponse } from '../../types/res.type';

@Component({
  selector: 'app-product-list-page',
  templateUrl: './product-list-page.html',
  imports: [ProductCardComponent, PaginatorComponent],
})
export class ProductListPage implements OnInit {
  page: Signal<PageResponse<ProductSummary>['page'] | undefined>;
  products: Signal<ProductSummary[] | undefined>;
  fallbackImgs: string[];

  categoryId: Signal<number | null>;
  pageNumber: Signal<number> = signal(0);
  size: Signal<number>;

  constructor(
    private productService: ProductService,
    private route: ActivatedRoute,
    private router: Router,
  ) {
    this.categoryId = toSignal(this.route.paramMap.pipe(map((p) => Number(p.get('id')))), {
      initialValue: null,
    });

    this.pageNumber = toSignal(
      this.route.queryParamMap.pipe(map((q) => Number(q.get('page') ?? 0))),
      {
        initialValue: 0,
      },
    );

    this.size = toSignal(this.route.queryParamMap.pipe(map((q) => Number(q.get('size') ?? 12))), {
      initialValue: 12,
    });

    effect(() => {
      const category = this.categoryId();
      if (category !== null) {
        this.productService.categoryId.set(category);
      }

      this.productService.loadSummary(this.pageNumber(), this.size());
    });

    this.page = computed(() => productService.productSummary.value()?.page);
    this.products = computed(() =>
      productService.productSummary.value()?.content.map((p, i) => ({
        ...p,
        imageUrl: p.imageUrl ?? this.fallbackImgs[i % this.fallbackImgs.length],
      })),
    );

    this.fallbackImgs = PRODUCT_PAGE.content.map((p) => p.imageUrl);
  }

  ngOnInit() {}

  handleOnPageChage(page: number) {
    console.log(page);
    this.router.navigate([], {
      relativeTo: this.route,
      queryParams: { page: page, size: this.size() },
    });
  }
}

export interface ProductSummaryDTO {
  id: number;
  name: string;
  imageUrl: string;
  salePrice: number | null;
  compareAtPrice: number;
  stock: number;
}
