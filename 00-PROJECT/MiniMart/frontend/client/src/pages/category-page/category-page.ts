import { Component, OnInit } from '@angular/core';
import CATEGORIES from '../../data/categories.json';
import { RouterLink } from '@angular/router';
@Component({
  selector: 'app-category-page',
  templateUrl: './category-page.html',
  styleUrls: ['./category-page.css'],
  imports: [RouterLink],
})
export class CategoryPage implements OnInit {
  categories: Category[];

  constructor() {
    this.categories = CATEGORIES;
  }

  ngOnInit() {}
}

type Category = {
  id: number;
  name: string;
  imageUrl: string;
  description: string;
};
