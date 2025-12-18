import { Component, signal } from '@angular/core';
import { FormsModule } from '@angular/forms';
import { RouterOutlet } from '@angular/router';

@Component({
  selector: 'app-root',
  imports: [RouterOutlet, FormsModule],
  templateUrl: './app.html',
  styleUrl: './app.scss',
})
export class App {
  users: User[] = [];

  count: number = 1;

  inputText: string = '';

  isUserEmpty() {
    return this.users.length === 0;
  }

  addUser() {
    const newName = this.inputText;
    if (!newName) {
      return;
    }
    this.users.push({ id: this.count, name: newName });

    // refresh
    this.inputText = '';
    this.count++;
  }
}

interface User {
  id: number;
  name: string;
}
