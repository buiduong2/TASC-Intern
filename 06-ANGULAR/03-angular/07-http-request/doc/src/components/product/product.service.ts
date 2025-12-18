import { HttpClient } from '@angular/common/http';
import { Injectable, signal } from '@angular/core';
export interface Todo {
  userId: number;
  id: number;
  title: string;
  completed: boolean;
}
@Injectable({ providedIn: 'root' })
export class ProductService {
  private todoData = signal<Todo | null>(null);

  constructor(private http: HttpClient) {
    this.http.get<Todo>('https://jsonplaceholder.typicode.com/todos/1').subscribe((data) => {
      console.log(data);
      this.todoData.set(data);
    });
  }

  get() {
    return this.todoData;
  }
}
