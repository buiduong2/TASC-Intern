Quá tốt 👏
Giờ mình sẽ giúp bạn viết **Documentation chuẩn internal enterprise** cho phần:

> 📘 User Topbar Menu System

Mình sẽ viết theo đúng style tài liệu bạn đang dùng.

---

# 📌 User Topbar Menu – Architecture Documentation

---

# 1️⃣ Mục tiêu

Triển khai User Topbar Menu theo nguyên tắc:

* Reactive
* Layout-driven
* Không phụ thuộc Business Domain
* Phân tách rõ UI và Auth Infrastructure
* Dễ mở rộng cho production

---

# 2️⃣ Vai trò trong hệ thống

UserMenu thuộc:

```
Core Layout Layer
```

Cụ thể:

```
core/
  layout/
    admin-layout/
      topbar/
        user-menu.component.ts
```

Nó:

* Xuất hiện toàn bộ hệ thống sau khi login
* Phụ thuộc Auth state
* Không chứa business logic

---

# 3️⃣ Kiến trúc tổng thể

```text
AuthService
    ↓
user$ (Observable<User>)
    ↓
UserMenuComponent (OnPush-ready)
    ↓
Template | async
    ↓
AvatarComponent + MatMenu
```

---

# 4️⃣ AuthService (Mock Infrastructure Layer)

```ts
@Injectable({ providedIn: 'root' })
export class AuthService {
  user$: Observable<User>;
  isAuthenticated$: Observable<boolean>;

  constructor() {
    this.user$ = of({...});
    this.isAuthenticated$ = of(true);
  }
}
```

## Thiết kế:

* Expose `user$`
* Expose `isAuthenticated$`
* Không expose token
* Không expose internal state

---

# 5️⃣ UserMenuComponent – UI Shell Layer

## 5.1 Trách nhiệm

* Render avatar
* Render dropdown menu
* Điều hướng đến account pages
* Trigger logout dialog

## 5.2 Không được làm

* Không gọi API trực tiếp
* Không giữ auth state nội bộ
* Không chứa business domain logic

---

# 6️⃣ Template Structure

```html
<button mat-icon-button>
  <app-avatar />
</button>

<mat-menu>
  Header (User Info)
  Divider
  Account Actions
  Divider
  Logout
</mat-menu>
```

---

# 7️⃣ AvatarComponent – UI Primitive

Thuộc:

```
shared/ui/avatar/
```

## Thiết kế:

* Stateless
* OnPush
* Fallback initial
* Hash-based background color

## Fallback Strategy

| Trường hợp    | Hành vi        |
| ------------- | -------------- |
| Có src hợp lệ | Render image   |
| Image lỗi     | Render initial |
| Không có name | Render '?'     |

---

# 8️⃣ Account Routing Integration

User menu điều hướng đến:

```ts
{
  path: 'auth',
  data: { breadcrumb: 'Tài khoản' },
  children: [
    { path: 'me', data: { breadcrumb: 'Hồ sơ' } },
    { path: 'change-password', data: { breadcrumb: 'Đổi mật khẩu' } }
  ]
}
```

Kết quả breadcrumb:

```
Dashboard / Tài khoản / Hồ sơ
Dashboard / Tài khoản / Đổi mật khẩu
```

---

# 9️⃣ Separation of Concerns

| Layer             | Trách nhiệm         |
| ----------------- | ------------------- |
| AuthService       | Cung cấp auth state |
| UserMenuComponent | Render + navigate   |
| AvatarComponent   | UI primitive        |
| Router            | Điều hướng          |
| Dialog            | Confirm logout      |

---

# 🔟 Logout Flow

```text
Click logout
    ↓
Open ConfirmDialog
    ↓
afterClosed()
    ↓
Call AuthService.logout()
    ↓
Redirect /auth/login
```

⚠ Hiện tại logout đang mock.

---

# 1️⃣1️⃣ Architectural Guarantees

UserMenu đảm bảo:

* Reactive
* Không coupling domain
* Không phụ thuộc feature layer
* Layout-driven
* Breadcrumb hoạt động tự động

---

# 1️⃣2️⃣ Dependency Direction

```
Core/Auth
     ↑
Layout/UserMenu
     ↑
Feature/AuthProfile
```

Core không phụ thuộc feature.

---

# 1️⃣3️⃣ Design Decisions

## Vì sao route prefix là `auth/` cho account?

Hiện tại bạn dùng:

```
auth/me
auth/change-password
```

Điều này hoạt động.

Nhưng production-level clean hơn sẽ là:

```
account/me
account/change-password
```

Vì:

* Auth flow (login, reset password) ≠ Account management
* Tránh nhầm lẫn domain

---

# 1️⃣4️⃣ Performance Characteristics

| Concern                 | Status |
| ----------------------- | ------ |
| Async pipe              | ✅      |
| Manual subscribe        | ❌      |
| OnPush Avatar           | ✅      |
| Memory leak risk        | ❌      |
| Route-driven breadcrumb | ✅      |

---

# 1️⃣5️⃣ Future Improvements

Có thể nâng cấp:

* AuthFacade thay vì AuthService trực tiếp
* Move logout logic vào facade
* Thêm role-based menu
* Thêm lazy-loaded account feature module
* Thêm signal-based Avatar state

---

# 1️⃣6️⃣ Tổng kết kiến trúc

User Topbar Menu =

```
Auth State
+ Layout Concern
+ Router Integration
+ UI Primitive
=
Account Interaction Layer
```

---

# 🔥 Senior Review Nhận Xét

Bạn đang:

* Thiết kế layout theo route-driven
* Không coupling domain
* Sử dụng Observable đúng cách
* Phân tách core / feature rõ
* Dùng breadcrumb đúng triết lý

Đây là level **mid-level Angular dev mindset rồi**, không còn intern basic nữa.

---

Nếu bạn muốn, mình có thể:

* Viết tiếp 📘 Layout System Documentation chuẩn enterprise
* Hoặc 📘 Auth Architecture Documentation (Guard + Facade + Interceptor)
* Hoặc 📘 Routing Architecture toàn app (rất quan trọng)

Bạn muốn hoàn thiện bộ docs ở mức nào?
