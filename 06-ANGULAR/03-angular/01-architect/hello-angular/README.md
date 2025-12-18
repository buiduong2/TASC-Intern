## Template & Data Binding

- `B1: src/app/app.ts`

- Đây là component gốc tương tư như `App.vue`

- `B2: 🔹 2. Hiển thị dữ liệu trong HTML (Interpolation)`

```html
<h1>Xin chào {{ title }}!</h1>
<p>Đây là ứng dụng Angular đầu tiên của tôi 🎉</p>
<!-- { ... }} là Interpolation — tương tự như {{ }} trong Vue hoặc {} trong React JSX. -->
```

- `🔹 3. Property Binding (ràng buộc thuộc tính HTML)`

- Dùng cú pháp` [ ]` để gán giá trị TypeScript → HTML.

```html
<img [src]="imageUrl" alt="Logo" />
```

- 🧠 Cú pháp `[prop]="value"` giúp Angular hiểu đây là binding, không phải chuỗi tĩnh.

- `🔹 4. Event Binding (bắt sự kiện)`

- Cú pháp `( ) `để bắt sự kiện từ HTML → gọi hàm trong TypeScript.

```html
<button (click)="sayHello()">Bấm vào tôi</button>
```

- Trong app.ts

```js
export class AppComponent {
  sayHello() {
    alert('Xin chào từ Angular 🚀');
  }
}
```

- `🔹 5. Two-way Binding (ràng buộc 2 chiều)`

- Để đồng bộ giá trị giữa input và biến trong component, Angular dùng `[(ngModel)].`
- 👉 Nhưng mặc định bạn cần `import FormsModule.`F

- Bước 1: mở app.config.ts

```ts
import { ApplicationConfig, importProvidersFrom } from '@angular/core';
import { provideRouter } from '@angular/router';
import { FormsModule } from '@angular/forms'; // import thêm vào
import { routes } from './app.routes';

export const appConfig: ApplicationConfig = {
  providers: [provideRouter(routes), importProvidersFrom(FormsModule)],
};
```

## 🧩 BÀI 3 – DIRECTIVE CƠ BẢN

- `🔹 1. @if – Hiển thị có điều kiện`

- Cần import `import { CommonModule } from '@angular/common';` // 👈 thêm dòng này

```js
import { Component } from '@angular/core';
import { CommonModule } from '@angular/common'; // 👈 thêm dòng này

@Component({
  selector: 'app-root',
  standalone: true,
  imports: [CommonModule],
  templateUrl: './app.html',
})
export class AppComponent {
  showMessage = true;

  toggleMessage() {
    this.showMessage = !this.showMessage;
  }
}
```

```html
<button (click)="toggleMessage()">Ẩn/Hiện</button>

<p *ngIf="showMessage">👋 Xin chào, đây là Angular Directive!</p>
```

- `🔹 2. @for – Lặp danh sách`

- app,.ts

```js
export class AppComponent {
  users = ['An', 'Bình', 'Chi', 'Dung'];
}
```

```html
<h3>Danh sách người dùng</h3>

@for (user of users; track user) {
<li>{{ user }}</li>
}
```

- `track user` tương tự như trackBy trong Angular cũ — nó giúp Angular chỉ re-render phần tử thay đổi, tối ưu hiệu năng. Nếu bạn không cần track, có thể bỏ luôn:

- `🔹 4. [ngClass] – Class động`

```js
//app.ts
export class AppComponent {
  isActive = false;

  toggle() {
    this.isActive = !this.isActive;
  }
}
```

```html
<!-- app.html -->
<button (click)="toggle()">Đổi trạng thái</button>

<p [ngClass]="{ 'active': isActive, 'inactive': !isActive }">
  Trạng thái: {{ isActive ? 'Hoạt động' : 'Không hoạt động' }}
</p>
```

```css
// app.css
.active {
  color: green;
}
.inactive {
  color: red;
}
```

- `🔹 5. [ngStyle] – Style động (vẫn giữ nguyên cú pháp cũ)`


```ts
// app.ts
export class AppComponent {
  fontSize = 16;
}
```

```html
<!-- app.html -->
 <p [ngStyle]="{ 'font-size.px': fontSize }">
  Cỡ chữ: {{ fontSize }}px
</p>

<button (click)="fontSize = fontSize + 2">+</button>
<button (click)="fontSize = fontSize - 2">-</button>
```

- `🔹 6. @switch, @case, @default`

```html
@switch (role) {
  @case ('admin') { <p>Quản trị viên</p> }
  @case ('user') { <p>Người dùng</p> }
  @default { <p>Khách</p> }
}

```

```ts
export class AppComponent {
  role = 'user';
}
```