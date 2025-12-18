import { Component, computed, effect, OnInit, Signal } from '@angular/core';
import { FormsModule } from '@angular/forms';
import { StarIconsComponent } from '../../components/star-icons/star-icons';
import { MatButtonModule } from '@angular/material/button';
import { ActivatedRoute, RouterLink } from '@angular/router';
import { ProductDetail, ProductService } from '../../services/product.service';
import { toSignal } from '@angular/core/rxjs-interop';
import { map } from 'rxjs';
import { Category, CategoryService } from '../../services/category.service';

@Component({
  selector: 'app-product-detail-page',
  templateUrl: './product-detail-page.html',
  imports: [StarIconsComponent, FormsModule, MatButtonModule, RouterLink],
})
export class ProductDetailPage implements OnInit {
  product: Signal<ProductDetail | undefined>;
  productId: Signal<number>;
  category: Signal<Category | undefined>;
  isInSale: Signal<boolean>;

  constructor(
    private route: ActivatedRoute,
    private productService: ProductService,
    private categoryService: CategoryService,
  ) {
    this.productId = toSignal(this.route.paramMap.pipe(map((p) => Number(p.get('id')))), {
      initialValue: 0,
    });

    effect(() => {
      if (this.productId() == 0) {
        return;
      }

      this.productService.loadDetail(this.productId());
    });

    this.product = computed(() => productService.productDetail.value());

    this.category = computed(() => {
      if (!this.product()) {
        return undefined;
      }

      return categoryService.getByCategoryId(this.product()!?.categoryId);
    });

    this.isInSale = computed(() => Boolean(this.product()?.salePrice));
  }

  ngOnInit() {}
}
