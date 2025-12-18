import { Injectable, signal } from '@angular/core';

@Injectable({ providedIn: 'root' })
export default class TodoService {
  todos = signal<Todo[]>([]);
  id: number = 0;

  public addTodo(name: string) {
    this.id++;
    this.todos.update((v) => [...v, { id: this.id, name }]);
  }

  public editTodo(id: number, name: string) {
    this.todos.update((v) =>
      v.map((todo) => {
        if (todo.id === id) {
          return {
            id,
            name,
          };
        }
        return todo;
      })
    );
  }

  public removeTodo(id: number) {
    this.todos.update((v) => v.filter((v) => v.id === id));
  }
}

export interface Todo {
  id: number;
  name: string;
}
