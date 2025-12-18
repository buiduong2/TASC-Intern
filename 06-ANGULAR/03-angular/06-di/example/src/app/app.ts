import { Component, computed, inject, WritableSignal } from '@angular/core';
import { RouterOutlet } from '@angular/router';
import TodoService, { Todo } from './services/TodoService';
import { CommonModule } from '@angular/common';

@Component({
  selector: 'app-root',
  imports: [RouterOutlet, CommonModule],
  template: `
    <h1>Todo List</h1>
    <ul></ul>
  `,
})
export class App {
  input = '';
  todos: WritableSignal<Todo[]>;

  constructor(public todoService: TodoService) {
    this.todos = todoService.todos;
  }
}
