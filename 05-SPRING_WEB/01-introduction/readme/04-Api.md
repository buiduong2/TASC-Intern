## Khái niệm cơ bản về Respones, request, httpMethod, JSON, API

## 1. respones Request

-   Trong mô hình client–server (ví dụ trình duyệt ↔ server):
    -   Request: thông điệp mà client gửi đến server để yêu cầu tài nguyên hoặc dịch vụ.
    -   Response: Là phản hồi từ server trả về sau khi đã xử lý xong request từ client.

## HTTP Request

-   Một HTTP Request thường bao gồm:
    -   Method
        GET, POST, PUT, DELETE,
    -   URL- Endpoint được gọi (e.g. /api/users)
    -   Headers- Metadata như Auth, Content-Type,...
    -   Body- Payload (thường dùng trong POST/PUT)

## HTTP response

-   Server xử lý request, và phản hồi lại với:
-   Status code 200 OK, 404 Not Found, 500 Internal Server Error,...
-   Headers: metadata
-   Body: dữ liêu jtrar về

## 2. HTTP method

-   Trong lập trình web và giao tiếp client-server qua HTTP, httpMethod (hay HTTP Method) là phương thức xác định hành động mà client (ví dụ trình duyệt hoặc frontend app) muốn thực hiện đối với tài nguyên trên server.
-   -   POST : để tạo một tài nguyên trên Server.
    -   GET : Đọc dữ liệu
    -   PUT : Cập nhật toàn bộ
    -   DELETE : Xoá
    -   HEAD: Lấy metadata Giống GET nhưng không trả body (chỉ headers)
    -   PATCH Cập nhật một phần
    -   OPTIONSKiểm tra hỗ trợ - Kiểm tra server hỗ trợ những phương thức nào

## 3. JSON (Javascript Object notation)

-   JSON (viết tắt của JavaScript Object Notation) là một định dạng dữ liệu dạng văn bản (text) được sử dụng phổ biến để trao đổi dữ liệu giữa client và server.
-   Nó rất dễ đọc với con người và dễ phân tích (parse) với máy tính, Nhẹ
-   JSON được xây dựng dựa trên 2 cấu trúc chính:
    -   Object (đối tượng) → gồm các cặp key: value
    -   Array (mảng) → danh sách các giá trị
-   Thực tế hay được sử dụng để
    -   REST API
    -   Config file (package.sjon)
    -   NoSQL Databases

## 4. API

-   API là viết tắt của Application Programming Interface — tức là Giao diện lập trình ứng dụng.
-   API là một "cầu nối" giúp các phần mềm khác nhau có thể giao tiếp với nhau

```
API là tập hợp các quy tắc, phương thức, và định dạng dữ liệu mà một hệ thống (phần mềm, service, app, server, v.v.) cung cấp, để phần mềm khác có thể gửi yêu cầu (request) và nhận phản hồi (response) một cách chuẩn hóa.
```
