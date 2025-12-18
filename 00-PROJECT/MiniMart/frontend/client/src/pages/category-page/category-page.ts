import { Component, computed, OnInit } from '@angular/core';
import CATEGORIES from '../../data/categories.json';
import { RouterLink } from '@angular/router';
import { CategoryService } from '../../services/category.service';
@Component({
  selector: 'app-category-page',
  templateUrl: './category-page.html',
  imports: [RouterLink],
})
export class CategoryPage implements OnInit {
  categories;
  isLoading;
  fallbackImgs: string[];

  constructor(private categoryService: CategoryService) {
    this.categories = computed(() => categoryService.category.value());
    this.isLoading = computed(() => categoryService.category.isLoading());
    this.fallbackImgs = CATEGORIES.map((c) => c.imageUrl);
  }

  ngOnInit() {}
}

type Category = {
  id: number;
  name: string;
  imageUrl: string;
  description: string;
};
