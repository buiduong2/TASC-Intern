import { HttpClient } from '@angular/common/http';
import { Injectable, signal } from '@angular/core';

@Injectable({
  providedIn: 'root',
})
export class CategoryServiceService {
  categories = signal<any[]>([]);
  loaded = signal(false);

  constructor(private http: HttpClient) {}

  loadCategories() {
    if (this.loaded()) {
      return;
    }

    // this.http.get<>('/api/')
  }
}
