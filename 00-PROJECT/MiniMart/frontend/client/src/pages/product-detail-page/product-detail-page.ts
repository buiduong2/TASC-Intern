import { Component, OnInit } from '@angular/core';
import { FormsModule } from '@angular/forms';
import { StarIconsComponent } from '../../components/star-icons/star-icons';
import { MatButtonModule } from "@angular/material/button";
import { RouterLink } from "@angular/router";

@Component({
  selector: 'app-product-detail-page',
  templateUrl: './product-detail-page.html',
  styleUrls: ['./product-detail-page.css'],
  imports: [StarIconsComponent, FormsModule, MatButtonModule, RouterLink],
})
export class ProductDetailPage implements OnInit {
  value = 'Clear me';

  constructor() {}

  ngOnInit() {}
}
