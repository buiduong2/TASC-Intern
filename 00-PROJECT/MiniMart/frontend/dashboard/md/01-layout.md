
## Tạo Layout

- Để tạo Layout. Đầu tiên ta sẽ cần tạo ra package chứa layout của nó.
- Chúng ta đang sử dụng thư viện `    "@angular/material": "^20.2.14",`
- Tối giản CSS.
- 

### Triết Lý

- Xây dựng theo mô hình `Route-Driven Layout (nested Router)`
- Layout không phụ thuộc vào global State mà được quyết định bởi Router
- Điều này giúp: 
  - Tách biệt rõ ràng UI Shell và Business Content
  - Trách detroy/ recreate layout khi navigate
  - Dễ mở rộng và maintain
  - Phù hợp với kiến trúc enterprise Angular
  - 

```
app/
  core/
    layout/
      admin-layout/
      auth-layout/
      error-layout/
```

- Layout được đặt trong core vì : 
  - Hạ tầng chung cho toàn ứng dụng
  - Không chứa business Logic
  - Chỉ đóng vai trò UI Shell
- 

### Admin Layout

- `Mục Đích`
  - Admin Layout là layout chính của hệ thống dashboard
  - Được sử dụng cho toàn bộ route yêu cầu xác thực

- `Các route`
- /dashboard
- /users
- /roles
- /products
- /orders
- /payments
- /inventory
- /reporting
- /saga-monitor

- `Cấu trúc`
- Topbar (header)
- Sidebar (navigation)
- Content area (Router -outlet)

- `Nguyên tắc`
- Không chứa business logic
- Không gọi API
- Không phụ thuộc vào Store
- Không quyết định quyền truy cập (việc này do Guard xử lý)

- `Cấu trúc hình học`
```
---------------------------------------------------
| Topbar (64px)                                   |
---------------------------------------------------
| Sidebar (240px) |   Content Area                |
|                  |                              |
|                  |   Page Header                |
|                  |   -------------------------- |
|                  |                              |
|                  |   Cards / Table / Charts     |
|                  |                              |
---------------------------------------------------
```

### Auth Layout
- Cấu trúc hình học
```
---------------------------------------------------
|                                                 |
|                                                 |
|           [   Login Card   ]                    |
|                                                 |
|                                                 |
---------------------------------------------------
```
- `Mục đích`
- Auth Layout được sử dụng cho trang xác thực
- Layout này tối giản tập trung vào FOrm
- `Phạm Vi sử dụng`
- /auth/login
- /auth/forgot-password
- /auth/reset-password

- `Đặc điểm`
  - Không có sidebard
  - Không có navigation
  - Không có breadcrumb
  - layout centered
- `Nguyên tắc`
  - Không phụ thuộc vào trạng thái Auth
  - Layout được quyết định bởi Route
  - Sau khi login thành công -> điều hướng sang route sử dụng AdminLayout
- `Error layout`
  - ErrorLayout đùng cho các trang lỗi hệ thống
- `Phạm vi sử dụng`
- /403
- /404
- /500
- `Đặc điểm`
  - Không có sidebar
  - Không có topbar
  - cÓ nút quay lại trang dashboard
- `Nguyên tắc`
  - Không phụ thuộc ROute State
  - Được truy cập trực tiếp bằng route
  - Không sử dụng layout của Admin

### Global Error Layout

### Thiết lập Layout (chung)

- Wiring Routing

```ts
{
  path: '',
  component: AdminLayoutComponent,
  canActivate: [AuthGuard],
  children: [
    { path: 'dashboard', ... },
    { path: 'products', ... }
  ]
}
```

- Mọi Layout đều chứa
```html
<p>
  <router-outlet> </router-outlet>
</p>

```

- Chính là để hiển thị các component trong nó
- 

### Admin Layout

- ở đây ta sẽ sử dụng 2 Component của MAterial là 
  - Toolbar - Topbar 
  - Sidebar -> Sidebar
  - Viết thêm Service để chuẩn bị cho collapsed và expand mode cho minimum navbar cần custom lại
  - Ở đây ta sử dụng chiến thuật wrapper thay vì hacky vớ vẩn 
    - Vì UI library sinh ra ko phải để cho ta kế thừa

### Topbarr

```
-----------------------------------------------------------
| ☰  Products / Edit                 🔔   👤 Admin ▼     |
-----------------------------------------------------------
```

- Sẽ có 3 section
  - `Left`
    - Breacrumb
    - Toggle sidebar
  - `center`
    - Không nên đặt search toàn cục.
    - Không nên đặt logo to
  - Có thể để trống hoặc chứa title hệ thống nhỏ
  - `Right`
    - Trạng thái hệ thống
    - User Controls

### UserMenuComponent

```
Topbar
   └── UserMenuComponent (avatar + dropdown)
```

- Xuất hiện toàn app
- Phụ thuộc Auth State
- không phụ thuộc business domain cụ thể
- `Ta đưa nó vào core - có lẽ features dành cho pages`
- Cấu trúc

```
app/
  core/
    layout/
      admin-layout/
        topbar/
          topbar.component.ts
          user-menu.component.ts
    auth/
      auth.service.ts
      auth.facade.ts
```

- Trong Auth Component nên inject một AUthService riêng quản lý dữ liệu cho User

### B1 Làm breadcrumb

- `Nguyên tắc`
  - Sinh breadcrumb dựa vào `ActivedRoute tree`
  - Lấy metadata từ `route.data` (Khai báo bên trong file app.routes.ts)
  - hoạt động hoàn toàn độc lập business domain
  - Chỉ là UI + đọc Route state
  - Bản thân Route config cũng phải thể hiện đúng cấu trúc điều hướng của hệ thống

- `Kiến trúc Component`

```
core/
  layout/
    admin-layout/
      topbar/
        breadcrumb/
          breadcrumb.component.ts
          breadcrumb.service.ts
```

- `Các bước thực hiện`

- `1️⃣ Route định nghĩa thêm breadcrumb metadata`
```js
{
  path: 'products',
  component: ProductListComponent,
  data: { breadcrumb: 'Products' }
},
{
  path: 'products/:id',
  component: ProductDetailComponent,
  data: { breadcrumb: 'Product Detail' }
}
```

- Hoặc nâng cao hơn
```js
{
  path: 'products/:id',
  component: ProductDetailComponent,
  data: {
    breadcrumb: (data: any) => `Product #${data['id']}`
  }
}
```

- `Cách hoạt động`
- Breadcrumb Componett
  - lắng nghe `Router.events`
  - Khi `NavigationEnd`
  - Traverse `ActivatedRoute.root`
  - Build ra mảng
```js
[
  { label: 'Dashboard', url: '/dashboard' },
  { label: 'Products', url: '/products' },
  { label: 'Detail', url: '/products/12' }
]
```

- `Resolver (loader)`

- Ta có thẻ triển khai thêm resolver để có thể fetch trước khi Component bắt đầu lifecycle
- Hay nó nằm ở router level nằm trong `router lifecycle.`
- Ở trong các page detail ta sẽ sử dụng resolver còn table filter thì component
- 

- **`Thực hiện: `**
- `1️⃣ Tạo thêm interface để thêm typesafe cho breadcrumb meatada`

```js
interface AppRoute extends Route {
  data?: Route['data'] & {
    breadcrumb?: string | ((data: any) => string);
  };
  children?: AppRoute[];
}


// Cập nhật thêm property cho data. Là bao gồm các property cũ và thêm breadcrumb
// Children đệ quy tiếp tiếp nhận type mới
```

- breadcrumb có thể là string để in ra luôn
- Hoặc là một Function để có thể thêm động dựa trên data
- Insight
  - breadcrumb là optional UI concern
  - Không phải là core routing concernt
  - Đừng biến optional concern thành hard requirement
  - 

- `2️⃣ Thêm các metadata + resolver`
- Triển khai các Resolver để có thể tách logic gọi PRoduct liên quan đến Route 
- resolver cho phép thêm fetch data trước khi vào trong Component. và bind dữ liệu vào Componetn thông qua `activatedRoute`
- `3️⃣ Triển khai Breadcrumb Service`
- triển khai cơ chế lắng nghe Route
- Lắng nghe Route thay đổi
- Khi `NavigtaionEnd`
- Duyệt `ActivatedRoute.root`
- Build breadcrumb array
-  expose obserable/ signal cho Topbar
-  
