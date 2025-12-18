import { Component, signal, WritableSignal } from '@angular/core';
import { RouterLink, RouterOutlet } from '@angular/router';

@Component({
  selector: 'app-root',
  imports: [RouterOutlet, RouterLink],
  standalone: true,
  template: `
    <header>
      <div>
        <h1>Quản lý User online</h1>

        @if(isLogged()) {
        <h3>Xin chào {{ name() }}</h3>
        <button (click)="logout()">Đăng xuất</button>
        } @else {
        <button (click)="login()">Đăng nhập</button>
        }
      </div>

      <nav>
        <a routerLink="/students">Danh sách sinh viên</a>
        &Tab;
        <a routerLink="admin">Xem trang admin</a>
      </nav>
    </header>

    <main>
      <router-outlet />
    </main>
  `,
})
export class App {
  isLogged: WritableSignal<boolean>;
  name = signal('Bùi Đức Dương');

  constructor() {
    const storedLogged = localStorage.getItem('isLogged');
    this.isLogged = signal(Boolean(storedLogged));
  }

  login() {
    this.isLogged.update((v) => true);
    localStorage.setItem('isLogged', 'true');
  }

  logout() {
    this.isLogged.update((v) => false);
    localStorage.setItem('isLogged', 'false');
    location.href = '/';
  }
}

export interface Student {
  id: number;
  name: string;
  desc: string;
}
