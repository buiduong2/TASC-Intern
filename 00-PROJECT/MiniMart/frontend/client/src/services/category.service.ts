import { httpResource } from '@angular/common/http';
import { Injectable } from '@angular/core';
import { environment } from '../environments/environment';

export interface Category {
  id: 2;
  name: 'Garden';
  imageUrl: null;
}

@Injectable({
  providedIn: 'root',
})
export class CategoryService {
  category = httpResource<Category[]>(() => ({
    url: `${environment.apiUrl}/v1/categories`,
    method: 'GET',
    mode: 'cors',
  }));

  get() {
    return this.category;
  }

  getByCategoryId(id: number) {
    return this.category.value()?.find((cat) => cat.id === id);
  }
}
