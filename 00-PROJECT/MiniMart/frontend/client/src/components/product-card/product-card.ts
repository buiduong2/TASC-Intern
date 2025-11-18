import { Component, computed, Input, OnInit } from '@angular/core';
import { ProductSummaryDTO } from '../../pages/product-list-page/product-list-page';
import { StarIconsComponent } from '../star-icons/star-icons';
import { RouterLink } from '@angular/router';
import { BagIconComponent } from '../bag-icon/bag-icon';

@Component({
  selector: 'app-product-card',
  templateUrl: './product-card.html',
  styleUrls: ['./product-card.css'],
  imports: [StarIconsComponent, RouterLink, BagIconComponent],
})
export class ProductCardComponent implements OnInit {
  @Input({ required: true }) product!: ProductSummaryDTO;

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
