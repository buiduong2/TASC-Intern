## Hướng dẫn sử dụng postman : param, body request

- Postman là một công cụ để gửi HTTP request và kiểm tra response
- Nó cho phép bạn cấu hình: URL, method, headers, query params, body, auth
- Tab Params → nhập key/value.
    - Key: role, Value: admin

- Dùng với các method như POST, PUT, PATCH.
    - none → không có body.
    - form-data → gửi nhiều field (có thể kèm file upload). - Dùng trong API upload file.
    - x-www-form-urlencoded → dữ liệu gửi trong body dạng key=value&key2=value2
    - raw → gửi text hoặc JSON/XML/Plain.
    - binary → gửi file nhị phân trực tiếp.
