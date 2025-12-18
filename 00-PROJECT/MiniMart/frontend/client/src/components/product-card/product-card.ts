import { Component, computed, Input, OnInit } from '@angular/core';
import { RouterLink } from '@angular/router';
import { ProductSummary } from '../../services/product.service';
import { BagIconComponent } from '../bag-icon/bag-icon';
import { StarIconsComponent } from '../star-icons/star-icons';

@Component({
  selector: 'app-product-card',
  templateUrl: './product-card.html',
  imports: [StarIconsComponent, RouterLink, BagIconComponent],
})
export class ProductCardComponent implements OnInit {
  @Input({ required: true }) product!: ProductSummary;

  isOnSale = computed(() => {
    return this.product.salePrice !== null && this.product.salePrice < this.product.compareAtPrice;
  });

  // computed: phần trăm giảm giá
  discountPercent = computed(() => {
    if (!this.isOnSale()) return 0;
    const sale = this.product.salePrice!;
    const original = this.product.compareAtPrice!;
    return Math.round(((original - sale) / original) * 100);
  });

  constructor() {}

  ngOnInit() {}
}
