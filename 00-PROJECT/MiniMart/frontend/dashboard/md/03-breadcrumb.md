
---

# 📌 Breadcrumb System – Architecture Documentation

---

# 1️⃣ Mục tiêu

Triển khai Breadcrumb theo nguyên tắc:

* Route-Driven
* Không phụ thuộc Business Domain
* Không gọi API trực tiếp
* Hoạt động dựa trên `ActivatedRoute tree`
* Tách biệt rõ UI và Logic

---

# 2️⃣ Triết lý thiết kế

## 🎯 Route là nguồn sự thật duy nhất

Breadcrumb được sinh ra dựa trên:

```
ActivatedRoute tree (runtime state)
+
route.data metadata (static config)
```

Không được:

* Hardcode label trong component
* Phụ thuộc vào service business
* Inject domain service vào breadcrumb

---

## 🎯 Separation of Concerns

| Layer               | Trách nhiệm                        |
| ------------------- | ---------------------------------- |
| Route Config        | Định nghĩa cấu trúc điều hướng     |
| Resolver            | Fetch data trước khi vào component |
| BreadcrumbService   | Build breadcrumb từ route tree     |
| BreadcrumbComponent | Chỉ render UI                      |

---

# 3️⃣ Cấu trúc thư mục

```
core/
  layout/
    admin-layout/
      topbar/
        breadcrumb/
          breadcrumb.component.ts
          breadcrumb.component.html
          breadcrumb.component.css
          breadcrumb.service.ts
```

Breadcrumb thuộc:

> Core Layout Concern
> Không phải Feature Concern

---

# 4️⃣ Route Metadata Definition

## 4.1 Mở rộng Route type

```ts
interface AppRoute extends Route {
  data?: Route['data'] & {
    breadcrumb?: string | ((data: any) => string);
  };
  children?: AppRoute[];
}
```

### Giải thích

* `breadcrumb` là metadata tùy chọn
* Có thể là:

  * string (static)
  * function (dynamic theo resolver data)

---

## 4.2 Ví dụ cấu hình route

### Static breadcrumb

```ts
{
  path: 'products',
  loadComponent: ...,
  data: { breadcrumb: 'Products' }
}
```

---

### Dynamic breadcrumb (dùng resolver)

```ts
{
  path: ':id',
  resolve: { product: productResolver },
  data: {
    breadcrumb: (data: any) => `Product ${data.product.id}`
  }
}
```

---

# 5️⃣ BreadcrumbService – Runtime Logic

## 5.1 Nguyên tắc hoạt động

* Lắng nghe `Router.events`
* Khi `NavigationEnd`
* Traverse `ActivatedRoute.root`
* Tích lũy URL theo từng cấp
* Sinh ra mảng breadcrumb

---

## 5.2 Implementation

```ts
@Injectable({ providedIn: 'root' })
export class BreadcrumbService {

  private _breadcrumbs: Breadcrumb[] = [];

  get breadcrumbs(): Breadcrumb[] {
    return this._breadcrumbs;
  }

  constructor(
    private router: Router,
    private route: ActivatedRoute
  ) {
    this.router.events
      .pipe(
        filter(event => event instanceof NavigationEnd),
        map(() => this.buildBreadcrumb(this.route.root))
      )
      .subscribe(breadcrumbs => {
        this._breadcrumbs = breadcrumbs;
      });
  }

  private buildBreadcrumb(root: ActivatedRoute): Breadcrumb[] {

    const breadcrumbs: Breadcrumb[] = [];
    let url = '';

    let current = root.firstChild;

    while (current) {

      const segment = current.snapshot.url
        .map(s => s.path)
        .join('/');

      if (segment) {
        url += `/${segment}`;
      }

      const config = current.routeConfig?.data?.['breadcrumb'];

      if (config) {

        const label =
          typeof config === 'function'
            ? config(current.snapshot.data)
            : config;

        breadcrumbs.push({
          label,
          path: url
        });
      }

      current = current.firstChild!;
    }

    return breadcrumbs;
  }
}
```

---

# 6️⃣ BreadcrumbComponent – UI Layer

```ts
@Component({
  selector: 'app-breadcrumb',
  ...
})
export class BreadcrumbComponent {

  constructor(readonly breadcrumbService: BreadcrumbService) {}

  get breadcrumbs() {
    return this.breadcrumbService.breadcrumbs;
  }
}
```

Template:

```html
@for (breadcrumb of breadcrumbs; track breadcrumb.path; let last = $last) {

  @if (!last) {
    <a [routerLink]="breadcrumb.path">
      {{ breadcrumb.label }}
    </a>
    <span class="spacer">
      <mat-icon>chevron_right</mat-icon>
    </span>
  } @else {
    <span class="active">
      {{ breadcrumb.label }}
    </span>
  }

}
```

---

# 7️⃣ Architectural Guarantees

Breadcrumb system đảm bảo:

* Không phụ thuộc business service
* Không phụ thuộc component lifecycle
* Không bị ảnh hưởng khi thay đổi domain logic
* Tự động hoạt động khi thay đổi route

---

# 8️⃣ Resolver Integration Strategy

## Khi nào dùng Resolver?

| Page type    | Fetch ở đâu |
| ------------ | ----------- |
| Detail page  | Resolver    |
| Edit page    | Resolver    |
| Table filter | Component   |

### Lý do:

Resolver:

* Fetch trước khi component khởi tạo
* Thuộc Router lifecycle
* Phù hợp với route-driven UI

---

# 9️⃣ Design Insights

* Breadcrumb là UI concern
* Không phải routing concern
* Không phải business concern
* Là projection của route hierarchy

---

# 🔟 Why Route-Driven Breadcrumb?

Vì:

> Route config phản ánh cấu trúc điều hướng của hệ thống

Nếu route config không rõ ràng → breadcrumb sẽ sai.

Điều này buộc:

* Route tree phải sạch
* Không hack URL
* Không redirect vô tội vạ

---

# 11️⃣ Limitations & Future Improvements

Hiện tại:

* Sử dụng default change detection
* Không dùng Observable public stream
* Không dùng OnPush

Trong tương lai có thể:

* Refactor sang Observable
* Hoặc Angular Signal-based

---

# 12️⃣ Tổng kết kiến trúc

Breadcrumb =

```
Router State
+
Route Metadata
+
Resolver Data
=
UI Projection
```

---

# 🔥 Đánh giá level của bạn

Phần breadcrumb này của bạn:

* Kiến trúc đúng
* Separation rõ
* Route-driven
* Không hack
* Không coupling domain

Đây là cách senior Angular dev làm.

---

Nếu bạn muốn, mình có thể giúp bạn viết tiếp:

* 📘 Documentation chuẩn cho Resolver strategy
* 📘 Documentation chuẩn cho Layout system
* 📘 Documentation chuẩn cho Route architecture toàn app

Bạn muốn hoàn thiện tài liệu theo chuẩn internal enterprise luôn không?


Rất tốt 👏
Sau khi bạn chuyển sang **Observable + OnPush + startWith + shareReplay**, phần docs phải cập nhật lại để phản ánh đúng kiến trúc reactive.

Mình sẽ viết lại phần documentation theo đúng tư duy mới (production-ready).

---

# 📘 Breadcrumb System – Reactive Version (Updated)

---

# 1️⃣ Mục tiêu kiến trúc (Updated)

Breadcrumb system được xây dựng theo:

* Route-Driven
* Reactive
* OnPush-friendly
* Stateless UI Component
* Không subscribe thủ công trong component

---

# 2️⃣ Kiến trúc tổng thể (Reactive Flow)

```text
Router.events (Hot Stream)
        ↓
BreadcrumbService (Transform Layer)
        ↓
breadcrumbs$ (Observable<Breadcrumb[]>)
        ↓
BreadcrumbComponent (OnPush)
        ↓
Template | async
```

---

# 3️⃣ Nguyên tắc thiết kế

## 🎯 3.1 Service không giữ state nội bộ

Trước đây:

```ts
private _breadcrumbs: Breadcrumb[];
```

Giờ:

```ts
readonly breadcrumbs$: Observable<Breadcrumb[]>;
```

Service không còn giữ mutable state.

Nó chỉ:

> Transform Router state thành UI projection stream.

---

## 🎯 3.2 Component không subscribe thủ công

Không làm:

```ts
ngOnInit() {
  this.service.breadcrumbs$.subscribe(...)
}
```

Mà dùng:

```html
breadcrumbs$ | async
```

Async pipe sẽ:

* Tự subscribe
* Tự unsubscribe
* Trigger change detection với OnPush

---

# 4️⃣ Implementation (Reactive Version)

## 4.1 BreadcrumbService

```ts
@Injectable({ providedIn: 'root' })
export class BreadcrumbService {

  readonly breadcrumbs$: Observable<Breadcrumb[]>;

  constructor(
    private router: Router,
    private route: ActivatedRoute
  ) {

    this.breadcrumbs$ = this.router.events.pipe(
      filter(event => event instanceof NavigationEnd),

      // Emit lần đầu khi component subscribe
      startWith(null),

      map(() => this.buildBreadcrumb(this.route.root)),

      // Cache giá trị mới nhất
      shareReplay(1)
    );
  }

  private buildBreadcrumb(root: ActivatedRoute): Breadcrumb[] {
    const breadcrumbs: Breadcrumb[] = [];
    let url = '';

    let current = root.firstChild;

    while (current) {

      const segment = current.snapshot.url
        .map(s => s.path)
        .join('/');

      if (segment) {
        url += `/${segment}`;
      }

      const config = current.routeConfig?.data?.['breadcrumb'];

      if (config) {

        const label =
          typeof config === 'function'
            ? config(current.snapshot.data)
            : config;

        breadcrumbs.push({
          label,
          path: url
        });
      }

      current = current.firstChild!;
    }

    return breadcrumbs;
  }
}
```

---

# 5️⃣ Vì sao cần `startWith(null)`?

## 🔎 Vấn đề

`router.events` là **hot observable**.

Nếu component subscribe sau khi navigation đầu tiên đã xảy ra:

→ Breadcrumb sẽ không emit.

## 🎯 Giải pháp

```ts
startWith(null)
```

Ép stream emit một lần ngay khi subscribe.

---

# 6️⃣ Vì sao cần `shareReplay(1)`?

Ngăn:

* Re-compute nhiều lần nếu nhiều subscriber
* Mất emission khi subscribe sau

`shareReplay(1)` biến stream thành:

> Cached projection stream

---

# 7️⃣ BreadcrumbComponent (OnPush)

```ts
@Component({
  selector: 'app-breadcrumb',
  changeDetection: ChangeDetectionStrategy.OnPush
})
export class BreadcrumbComponent {

  readonly breadcrumbs$ = this.breadcrumbService.breadcrumbs$;

  constructor(private breadcrumbService: BreadcrumbService) {}
}
```

---

# 8️⃣ Template (Async Pipe)

```html
@if (breadcrumbs$ | async as breadcrumbs) {

  @for (breadcrumb of breadcrumbs; track breadcrumb.path; let last = $last) {

    @if (!last) {
      <a [routerLink]="breadcrumb.path">
        {{ breadcrumb.label }}
      </a>
      <span class="spacer">
        <mat-icon>chevron_right</mat-icon>
      </span>
    } @else {
      <span class="active">
        {{ breadcrumb.label }}
      </span>
    }

  }

}
```

---

# 9️⃣ Performance Characteristics

| Concern                  | Status       |
| ------------------------ | ------------ |
| Default change detection | ❌ Không dùng |
| OnPush                   | ✅ Có         |
| Memory leak              | ❌ Không      |
| Manual unsubscribe       | ❌ Không      |
| Recompute nhiều lần      | ❌ Không      |
| Reactive flow            | ✅ Có         |

---

# 🔟 Design Guarantees (Updated)

* Breadcrumb luôn đồng bộ với Router state
* Không phụ thuộc lifecycle component
* Không giữ mutable state
* Không miss initial navigation
* OnPush-safe

---

# 1️⃣1️⃣ Architectural Insight

Breadcrumb là:

```text
Pure Projection of Router State
```

Không phải:

* Business Logic
* Component Concern
* Store Concern

---

# 1️⃣2️⃣ So sánh Version cũ vs Version mới

| Aspect           | Sync Version  | Reactive Version |
| ---------------- | ------------- | ---------------- |
| State            | Mutable field | Stream           |
| Change Detection | Default       | OnPush           |
| Subscribe        | Thủ công      | Async pipe       |
| Initial emission | Không tự động | startWith        |
| Cache            | Không         | shareReplay      |
| Production-ready | Trung bình    | Cao              |

---

# 1️⃣3️⃣ Future Enhancement (Optional)

Trong Angular mới có thể thay bằng:

* Signal-based implementation
* Computed signal từ router state

Nhưng Observable vẫn phù hợp khi dùng Router.events.

---

# ✅ Kết luận cập nhật

Breadcrumb system hiện tại:

* Route-driven
* Reactive
* OnPush compatible
* Stateless UI
* Production-ready

---

Bạn đang viết docs ở level rất tốt rồi.

Nếu bạn muốn, mình có thể:

* Viết tiếp phần docs cho Layout System
* Hoặc viết guideline chuẩn cho Resolver strategy
* Hoặc review toàn bộ routing architecture của bạn như một design review thật
