## 1. Dóc

## 2. Understanding communicating with backend services using HTTP

- Front-end Angular muốn lấy dữ liệu từ backend → cần gửi HTTP request: GET, POST, PUT, DELETE…

- Angular cung cấp service: `HttpClient` Nằm trong: `@angular/common/http`

- HttpClient giúp ta:
  - Gửi request + nhận response
  - Typed response (có kiểu)
  - Error handling
  - Interceptor (chèn token…)
  - Testing utilities

## 3. Setting up HTTPclient

### 3.1. Setting up HttpClient

- Trước khi dùng HttpClient, bạn phải cấu hình nó thông qua dependency injection.

- _Giải thích_
  - HttpClient là 1 service Angular cung cấp

### 3.2. Providing HttpClient through dependency injection

  - HttpClient được cung cấp thông qua hàm provideHttpClient() trong file `app.config.ts`
```js
export const appConfig: ApplicationConfig = {
  providers: [
    provideHttpClient(),
  ]
};
```

- _Giải thích_
  - `provideHttpClient()` giúp Angular tạo ra HttpClient và đưa nó vào DI system.
  - Sau khi thêm, bạn có thể inject HttpClient ở bất cứ đâu.

### 3.3. Injecting HttpClient

```js
@Injectable({ providedIn: 'root' })
export class ApiService {
  private http = inject(HttpClient);

  // Now this service can make HTTP requests via `this.http`.
}
```

- _Giải thích:_
  - `inject(HttpClient)` là cách mới (không cần constructor).

```js
constructor(private http: HttpClient) {}
```

- Cả 2 đều hợp lệ.
### 3.4.  Configuring features of HttpClient

- `withFetch()`

```js
providers: [
  provideHttpClient(
    withFetch(),
  ),
]
```

- Theo mặc định HttpClient dùng XMLHttpRequest (XHR). `withFetch()` đổi HttpClient sang dùng Fetch API.

- _Giải thích_
  - Nhưng fetch không hỗ trợ upload progress

- `withInterceptors(...)`
 
  - cấu hình danh sách interceptors theo kiểu functional.

```js
provideHttpClient(
  withInterceptors([authInterceptor])
)
```

- _Giải thích_
  - Interceptor = chặn request/response.
  - Thêm token
  - Log request
  - Bắt lỗi chung

- `withInterceptorsFromDi()`
  - bao gồm các interceptor dạng class từ DI.

```js
provideHttpClient(
  withInterceptorsFromDi(),
)
```

- _Giải thích_
  - Dành cho interceptor dạng class:
```js
@Injectable()
export class AuthInterceptor implements HttpInterceptor {}
```
  - Angular khuyên dùng functional interceptors (withInterceptors).

- `withRequestsMadeViaParent()`
  - cấu hình HttpClient trong injector hiện tại đẩy request lên parent injector để dùng chung interceptor.

- _Giải thích_
  - Trường hợp này dùng khi:
    - Bạn có nhiều injector (ví dụ lazy module)
    - Bạn muốn mọi HttpClient đều dùng cùng interceptor của parent
  - Nếu không hiểu sâu về DI → bạn chưa cần dùng.

## 4. Making request 

- HttpClient có nhiều phương thức để gửi các loại HTTP request.
- GET, POST, PUT, DELETE, PATCH, HEAD, JSONP, OPTIONS, v.v…

### 4.1. GET requests

```js
this.http.get('/api/items');
```

- _Giải thích_
  - get() dùng để lấy dữ liệu
  - Không gửi body
  - Angular tự parse JSON → trả về object

- `GET với kiểu dữ liệu (type)`

```js
interface Item { id: number; name: string; }

this.http.get<Item[]>('/api/items');
```

- `GET với httpParams`

```js
this.http.get('/api/items', {
  params: new HttpParams().set('page', 1).set('limit', 10)
});

```

### 4.2. POST requests

```js
this.http.post('/api/items', { name: 'Book' });
```

- Gửi JSON body lên server
- Thường dùng cho tạo mới

- `POST với options`

```js
this.http.post('/api/items', body, {
  headers: new HttpHeaders().set('Authorization', 'Bearer token')
});

```
### 4.3. Request Body Options

- Nếu bạn muốn lấy full response (headers, status):

```js
this.http.get('/api/items', { observe: 'response' });

```

- Mặc định Angular chỉ trả về body
- observe: 'response' trả về HttpResponse đầy đủ

### 4.4 Strongly typed responses

```js
this.http.get<Product[]>('/api/products');

```
- Angular không thực sự “parse” sang class
- Nó chỉ gán type để bạn code dễ hơn
- Server trả JSON → Angular convert sang object

### 4.5. Handling errors

```js
this.http.get('/api/items').pipe(
  catchError(error => {
    console.error(error);
    return throwError(() => error);
  })
)

```

- Sử dụng `catchError()` để bắt lỗi trong request
- Phải return throwError() để Observable không bị treo
