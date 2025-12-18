import { Component, signal, WritableSignal } from '@angular/core';
import { RouterLink, RouterOutlet } from '@angular/router';

@Component({
  selector: 'not-found',
  imports: [RouterOutlet, RouterLink],
  standalone: true,
  template: `
    <h1>Không tìm thấy đường dẫn mời quay về</h1>
    <a routerLink="/">Trang chủ</a>
  `,
})
export class NotFound {}
