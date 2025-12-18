## Tạo 3 trang:

🎯 Mục tiêu

Ứng dụng gồm 3 trang chính:

Route Trang Mô tả
/students Danh sách sinh viên Hiển thị danh sách + link chi tiết
/students/:id Chi tiết sinh viên Lấy param từ URL
/students/:id/edit?mode=quick Form sửa sinh viên Đọc query param
/admin Trang quản trị Có guard chặn nếu chưa đăng nhập

## 🚀 BÀI 5 (PHẦN 2) – ROUTING NÂNG CAO TRONG ANGULAR 17+

- { path: 'user/:id', component: UserDetailComponent }, // 👈 dynamic param

```ts
export class UserDetailComponent {
  userId!: string;

  // Giống useRoute lấy ra Route
  // CÓ thể truyền của router
  constructor(private route: ActivatedRoute) {
    this.userId = this.route.snapshot.paramMap.get('id')!;
  }
}
```

```ts
export const routes: Routes = [
  { path: '', component: HomeComponent },
  {
    path: 'user/:id',
    // Guard
    component: UserDetailComponent,
    canActivate: [authGuard], // 👈 thêm guard vào route
  },
];
```

```ts
import { Router, Event, NavigationStart, NavigationEnd } from '@angular/router';

constructor(private router: Router) {
  this.router.events.subscribe((event: Event) => {
    // lifecycle
    if (event instanceof NavigationStart) console.log('👉 Bắt đầu chuyển trang...');
    if (event instanceof NavigationEnd) console.log('✅ Chuyển trang hoàn tất');
  });
}
```

```ts
export const routes: Routes = [
  {
    path: 'admin',
    loadComponent: () => import('./admin/admin').then((m) => m.AdminComponent),
    children: [
      // Children
      { path: 'users', loadComponent: () => import('./admin/users').then((m) => m.UsersComponent) },
      {
        path: 'settings',
        loadComponent: () => import('./admin/settings').then((m) => m.SettingsComponent),
      },
    ],
  },
];
```
