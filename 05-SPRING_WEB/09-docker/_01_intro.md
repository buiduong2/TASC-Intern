## Intro

### Giới thiệu về Docker 

- Docker là một nền tảng mã nguồn mở giúp các nhà phát triển và quản trị hệ thống đóng gói, phân phối và chạy các ứng dụng trong một môi trường cô lập gọi là `container`. Thay vì phải cài đặt mọi thứ cần thiết (hệ điều hành, thư viện, framework) trên máy chủ, chúng ta chỉ cần đóng gói ứng dụng và các phụ thuộc của nó vào một container duy nhất. Điều này giúp loại bỏ vấn đề. Nó chạy trên máy cảu tôi nhưng không chạy trên máy của bạn


## Khái niệm cơ bản

- `Docker Image`: giống như một bản thiết kế hoặc khuôn mẫu. Docker Image là một tệp tin tĩnh, không thể thay đổi, chứa mọi thứ cần thiết để chạy một ứng dụng : mã nguồn, thư viện, biến môi trường, và cấu hình. Ta có thể coi đó là một bản chụp của ứng dụng tại một thời điểm nào đó

- `Docker Container`:đây là sản phẩm được tạo ra từ Docker Image. Container là một phiên bản sống, có thể chạy được của Image. Ta có thể tạo ra nhieuf COntainer từ cùng một Image. Mỗi container hoạt động độc lập và không ảnh hưởng đến nhau

- `Dockerfile`: là một tệp tin văn bản chứa các lệnh hướng dẫn để xây dựng một DockerImage. Ta sẽ viết các bước như "sử dụng hệ điều hành Ubuntu", "cài đặt Node.js" sao chép mã nguồn của tôi. v... v.. Docker sẽ tự động thuccwj hiện các bước đó để tạo ra Image

- `Dockerhub`: là một kho lưu trữ công cộng (public registry) chứa các DockerImage, giông như Github cho mã nguồn, DockerHub cho phép chúng ta tìm kiếm tải về (pull) và chia sẻ (push) các image đã được tạo sẵn


## Lợi ích của Docker 
- `Đồng nhất môi trường`: Đảm bảo ứng dụng của ta sẽ hoạt động giống nhau trên mọi môi trường, từ máy tính cá nhân của chúng ta, máy chủ phát triển, đến máy chủ sản xuất . Điều này giúp giảm thiểu lỗi do sự khác biệt về môi trường

- `Triển khai nhanh chóng`: việc đóng gói ứng dụng vào Container giúp quá trình triển khai trỏ lên đơn giản và nhanh hơn. Chúng ta chỉ cần chạy một vài lệnh thay vì cài đặt thủ công nhiều phần mềm 

- `Khả năng mở rộng`: dễ nhân bản (scale) ứng dụng bằng cách chạy thêm nhiều container . Docker và các công cụ liên quan như Docker compoase, kubernetes giúp chúng ta quản lý việc này một cách hiệu quả

- `Tiệt kiệm tài nguyên`: Các container chia sẻ nhân (kernel) của hệ điều hành máy chủ, do đó chúng nhẹ hơn nhiều so với máy ảo (VM) truyên thống. Điều này giúp tối ưu hóa việc sử dụng tài nguyên