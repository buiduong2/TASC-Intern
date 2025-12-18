import { Component, signal } from '@angular/core';
import { RouterOutlet } from '@angular/router';
import { FormsModule } from '@angular/forms'; // ✅ phải có
import { CommonModule } from '@angular/common';

@Component({
  selector: 'app-root',
  imports: [RouterOutlet, FormsModule, CommonModule],
  templateUrl: './app.html',
  styleUrl: './app.scss',
})
export class App {
  title = 'my-angular-app';
  imageUrl =
    'https://upload.wikimedia.org/wikipedia/commons/thumb/c/cf/Angular_full_color_logo.svg/768px-Angular_full_color_logo.svg.png?20160527092314';

  name = '';

  isActive = false;

  fontSize = 10;

  toggle() {
    this.isActive = !this.isActive;
  }

  users = ['usser-1', 'user-2', 'user-3', 'user-4'];

  sayHello() {
    alert('Xin chaào');
  }

  showMessage = true;

  toggleMessage() {
    this.showMessage = !this.showMessage;
  }
}
