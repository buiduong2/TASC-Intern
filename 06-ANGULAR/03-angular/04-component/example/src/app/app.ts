import { Component, signal } from '@angular/core';
import { FormsModule } from '@angular/forms';
import { RouterOutlet } from '@angular/router';
import { UserItem } from './user-item/user-item';

@Component({
  selector: 'app-root',
  imports: [RouterOutlet, FormsModule, UserItem],
  templateUrl: './app.html',
  styleUrl: './app.css',
})
export class App {
  users: User[] = [
    { id: 1, name: 'Duong' },
    { id: 2, name: 'Bui' },
  ];

  inputText = '';
  count = 3;

  addUser() {
    const name = this.inputText.trim();
    if (!name) return;
    this.users.push({ id: this.count++, name });
    this.inputText = '';
  }

  deleteUser(id: number) {
    this.users = this.users.filter((u) => u.id !== id);
  }
}

export interface User {
  id: number;
  name: string;
}
