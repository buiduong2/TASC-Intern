## Điều cần làm

- Dự án gần đây nhất về bán sách
- Tổng quan về các cái tech mà em sử dụn trong module là gì

- cần showw công nghệ của dự án.

- Như vậy ta biết về

- Giới thiệu -> mô hình dự án -> các technical sử dụng -> phần tôi quản lý là gì -> toi đã sử dụng cái gì 

## Câu hỏi '

- `Bên em dùng Redis ddeere cache à. Cache thì em cache nhưng thông tin cache như thế nào`

---

- Cache về chi tiết sản phẩm 

- Multi thread xuống DB

- Cache chi tiết sản phẩm, và những sản phẩm liên quan của chính sản phẩm đó

- NGoài ra sản phẩm người ta tìm kiếm rất là nhiều, nên cache thoogn tin tìm kiếm sản phẩm và cache theo dữ liệu đầo vòa

- Thời gian cache để ngắn khoảng 1-2s 

- Cập nhật nhanh nhất từ thời gian DB lên hệ thông cache . Để đảm bảo độ chễ


- `Tại sao cần cache mô tả ngắn gọn đó. Và nó gồm những thông gì gì ?`

- `Gợi ý mua hàng là sao ? `

- `Cache 1 chi tiết sản phẩm ? có full thông tin về sản phẩm đó. tại sao ko lấy thông tin nữa. mà cần thông tin`

- `Cache mấy s thì đồng bộ thông tin cache như nào` . 5-10s gọi lại vào DB thì cập nhật chính xác số lượng sản phẩm

- 5-10s là chậm. 
    - Chỉ có số sản phẩm thay đổi nhiều
    
- `Giả sử sản phẩm bị thay đổi 2 thông tin. Về loại sản phẩm, mo tả sản phẩm/ àlafm sao để cập nhật đúng thông tin mà nó update thì đang xử lý như nào`

- `cache sẩn phẩm liên quan`

- Đừng forcus quá. Bài toán đưa ra cãi nhau thì ko nói lại được đâu 

- Có một crob job để quyets thông tin thay đổi dữ liệu sản phẩm. để ko lệch . Đừng có quá đi vào chi tiết

- `Thay đổi category thì hệ thống làm sao ddoognf bộ được`

- `Hệ thống đang chạy 5-10s thì để làm gì  bị dupliocate`

- Phỉa Mapping với mình. Mình chỉ trả lời những gì đủ. Trả lời ở mức trừu tượng ko đi vào chi tiết

    - MỘt con cronb Job quyets các thay đổi và đẩy lên
    - Khi bọn em update và update luôn
      

- `Trong dự án này có cái gì liên quan đến đa luồng mà em áp dụng vào hay không trong nghiệp vụ gì hay không`

    - chạy từng cái productId để tìm kiếm thông tin sản phẩm

    - Làm sao lấy được thông tin các sản phẩm related

    - Lấy random 5. mỗi 1 cái thread lấy 1 id gọi xuống DB và lấy thông tin tóm tắt của sản phẩm . Random theo category

    - BỎ qua 1 đống thứ

    - Lấy theo batch Size. Push vào 2 queue của Redis. Để xử lý cache thông tin chi tiết sản phẩm

    - Queueu thứ 2 là xử lý Related Product

    - Có các tiến trình bên dưới chạy bất đồng bộ lấy từng sản phẩm cache chi tiết. Và cache sản phẩm

    - Để đảm bảo tốc độ cache dữ liệu từ DB lên redis là nhanh nhất 


- Nói tổng quan. 


- `kafka cơ sở lý thuyết`

    - Một topic có nhiều 

    - Đẩy các message lên topic

    - Consumer đọc các topic event đó

- `Hệ thống của em đang sử dụng mô hình gì của Kafka - Stand alone hay cluster`
- `Em có biết kafka cluster không . So sánh 2 mô hình của chúng giống nhau và khác cnhau như nào`

- `Em hiểu về kafka cluster `
    - MỘt cụm kafka server có nhiều broker trong đấy. Có nhiều replica các pattrtion của các topic

- `Kafka Stand Alone thì sao`

- `Redis Stand a lone hay Redis CLuster`

--- 

- Stand ALone chỉ có một broker . Trong môi trường DEV

- CLsuter: Product .  cấu hình giống nhau không 

- Keyword. phát sinh trong trường hợp nào. xử lý như nào 

- conrrentException: 

    - race conđition

- Cấu hình khác nhau . Cách kết nối khác nhau haofn toàn . Đừng cso đoán , Đừng có theo em. 

- `Race Conđition là gì`

    - Nhiều luồng cùng thao tác trên cùng một đối tượng chung trên Heap
    - Thay đổi tài nguyên chung. gây ra sai lệch dữ liệu thao tác giữa các luồng
    - Trong 1 topic thì có nhiều partition. một consumer thì có thể đọc nhiều parttioin . Và một consumer chỉ có thể đọc 

- Ban đầu có 1 topic. Chia thành 10 partition. TRong group Consumer chỉ có 5 consumer. Tương đương consumer 2 topic. Thời điểm ban đầu. Số lượng Request bình thường. 5 consumer thoải mái. Sau 1 khoảng thời gian số lượng request x10 lần lên. Số lượng Message đổ vào quá nhiều 

- Ông LVT nâng consumer lên. Hệ thống vẫn đáp ứng bình thường.  (tẹt ga). Giữ nugyeen lượng partition = 10. Dữ consumer từ 5 - 10 thì chuyện gì xảy ra . 

- Bây giờ không may 1 điều consumer ban đầu là 20. Nhưng partition chỉ có 10 Nâng số lượng partition từ 10-20 thì 10 thằng rảnh dỗi có lao vào 10 thằng mới hayu không hãy chỉ có 10 thằng đó hay không

- Clsuter, non CLuster, REdis kafka, kafka tăng số lương jparrtition leehcj. Tăng số lượng consumer so với partition

- REdis : trong trường hợp Cluster trong redis bị bỏ đi 1 số thằng và tăng lên 1 số thằng thì cơ chế nó như thế nào. Nó có cơ chế gì hay không. có 10 node. Cắt 5 node thì có chuyện gì xảy ra 

- Cần tái hiện lại

- `1. Cách giới thiệu bản thân`

- `2. Người ta hỏi gì thì trả lời cái đấy. Đừng đi vào chi tiết. Cagnf chi tết càng bị giã và càng bị hỏi sẽ cuống và ko trả lời được cái gì hết`
    - Kiến trúc. Flow, overview. ko đi sâu vào chi tiết triển khai như thế nào
- ``