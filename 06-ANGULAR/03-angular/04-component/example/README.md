## 🔥 BÀI 4: Component & Truyền dữ liệu giữa Cha – Con trong Angular 17+ (Standalone)

### 1. Tạo component con

- Ta có thể tạo nhanh = CLI

```sh
ng generate component user-item --standalone

```

```ts
import { Component, Input, Output, EventEmitter } from '@angular/core';
import { CommonModule } from '@angular/common';
import { User } from '../models/user'; // nếu bạn có file user.ts, còn không thì tạm bỏ dòng này

@Component({
  selector: 'user-item', // Chú ý tên SELECTOR ta sẽ sử dụng ở parent
  standalone: true,
  imports: [CommonModule],
  templateUrl: './user-item.html',
  styleUrls: ['./user-item.css'],
})
export class UserItemComponent {
  // ✅ Nhận dữ liệu từ component cha
  // Gửi thông qua props
  @Input() user!: User; // hoặc { id: number; name: string } nếu chưa có interface

  // ✅ Gửi sự kiện lên component cha
  // Gửi thông qua props
  @Output() deleteUser = new EventEmitter<number>();

  onDelete() {
    this.deleteUser.emit(this.user.id);
  }
}
```

```html
<!-- truyền vào như attribute v-bind: -->
<!--  Truyền vào  sự kiên v-on: -->
<!-- $event chính là payload tương ứng emiter của vue -->
<user-item [user]="u" (deleteUser)="deleteUser($event)"> </user-item>
```
