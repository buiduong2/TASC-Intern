## Điểm yếu của hệ thống

-   Pre-caching toàn bộ dữ liệu -> cách tiếp cận mạnh tay. an toàn về độ chễ nhưng rủi ro về `memory & consisstency`

-   2 thread chính (Product và CategoryId→ProductId)

-   Batch + pipeline Redis → hiệu năng cao

-   TTL + dirty set (TreeSet) → tránh stale cache, khá có tư duy hệ thống.

-   Cronjob 1p + event listener (sự kiện nội bộ) → logic phức tạp, dễ lỗi race condition.

## Tại sao lại cache toàn bộ thay vì lazy caching

## Nếu có hàng trăm nghìn sản phẩm thì sao

## Nếu một sản phẩm không bao giờ bị truy cập thì việc cache trước có gây phí tài nguyên không

## Vấn đề kĩ thuật tiềm ẩn

-   Ram redis phình to, tốn chi phí
-   Thời gian warn-up lâu

-   Khi deploy mới (pre-cache) toàn bộ có thể làm chậm start up

-   không phù hợp nếu Product tăng trưởng nhanh hoặc phân phối nhiều khu vực

### **giải pháp biện hộ**

-   Bọn em chọn pre-cache vì sản phẩm quy mô vừa (tầm vài nghìn sản phẩm), tốc độ truy cập cần ổn định. wan-up trong vòng 30s - 1p chấp nhận được. Nếu hệ thống tăng trưởng lên 100k+ item , em sẽ chuyển hướng sang lazy + backgroudn refresh hoặc phân vùng cache theo category

# _Cache 2 giờ sáng_ -

### “Nếu 2h sáng job fail (Redis timeout) thì sao?”

### Nếu hệ thống global, 2h sáng ở VN là giờ cao điểm ở châu Âu thì sao

### Có cần lock phân tán để tránh 2 instance refresh cùng lúc

### điểm yếu:

-   Không resilient nếu deploy nhiều instance → job refresh song song dễ “giẫm chân nhau”.

-   Không có retry strategy / checkpoint.

-   Toàn bộ cache invalidation lúc 2h có thể tạo spike load (I/O DB và Redis đều căng).

### _Cải biện_

-   Đúng là chỗ này bọn em còn hạn chế, có thể cải tiến bằng cách refresh theo incremental partittion (VD refresh theo category từng nhóm). Ngoài ra nếu scale nhiều íntance, bọn em sẽ dùng cơ chế khác để chỉ refresh 1 instance

### _Cronjob 1 phút quét DB theo updatedAt_

-   Gây query load lên DB liên tục, nhất là khi bagnr Product lớn

-   Có thể bỏ sót nếu có lỗi về timezone hoặc record update nhanh liên tục.

-   Lặp lại dữ liệu đã sync (inefficient).

-   `Đúng 1p là hơi dày. Ban đầu em đặt như vậy để đảm bảo consitency cao khi có nhiều thay đổi nhỏ. Thực tế có thể chuyển sang 5-10p hoặc dùng CDC (Change Data Capture) thay vì polling DB`

###

-   _bọn em ưu tiên simple và tránh update trùng lặp. nếu cần đảm bảo hơn, em có thể chuyển sang RedisSTream hoặc pub/sub pattern để xử lý queue-based update có ack_

### Số lượng Thread xử lý xong xong

-

### ProductId set random

-   Hiện tại là random ngẫu nheien, em xác định đó là giải pháp tạm thời. sau này có thể thay bằng thuật toán đơn giản hoặc ranking theo lịch sử mua

### Redis SortedSet thay vì TreeSet → rất tốt, nhưng vẫn có rủi ro “race condition”

-   Đúng là scocred Set chỉ lưu 1 score mỗi product, nhưng em chấp nhạn điều này vì cache cần ít nhất 1 lần update cuối cùng. Trong trường hợp update quá gần nhau không gây sai vì dữ liệu mới nhất sẽ luôn được lấy từ DB. Nếu cần đảm bảo EO, em sẽ chuyển dần sang consumer group

-   `lastSyncAt` có thể mất khả năng đồng bộ (vì lần cập nhật và DB quá gần nhau. Em cho thêm một nguonxg leehcj cho phép)

## **TƯ DUY CỐT LÕI TRONG THIẾT KẾ CỦA BẠN**

-   Hệ thống product không biến động quá nhanh (không phải real-time như stock hay price mỗi giây).

-   Người dùng cần truy cập nhanh ngay cả lần đầu tiên.

-   Bạn muốn hạn chế “cache churn” (liên tục update cache gây load Redis & DB).

-   high-read / low-write optimization

-   `Ưu điểm:`

    -   Truy cập luôn nhanh, ổn định
        -   Vì mọi sản phẩm đều đã ở cache, không có cold start.
    -   Giảm tải DB triệt để
        -   Không cần truy vấn lại DB cho mỗi request, kể cả với traffic spike.
    -   Hiệu suất predictable
        -   Cache hit gần như 100%, dễ dự đoán hiệu năng khi scale traffic.
    -   Cập nhật batch theo giờ/dirty set
        -   Hạn chế cache invalidation liên tục → hệ thống không bị dao động load.
    -   TTL làm lớp an toàn cuối
        -   Dù job fail, cache vẫn tự làm mới sau TTL.
    -   Thiếu nhất quán có kiểm soát
        -   Chỉ lệch vài phút hoặc 1 chu kỳ dirty-update, không ảnh hưởng trải nghiệm.

-   `Nhược điểm`

    -   Dữ liệu có thể lệch vài phút
        -   Vì dữ liệu sản phẩm (tên, giá, hình ảnh) không thay đổi liên tục, nên chấp nhận eventual consistency.
    -   RAM Redis tốn hơn
        -   Đúng, nhưng đổi lại hệ thống phản hồi ổn định và predictable latency
    -   Warm-up khởi động lâu

        -   Được thực hiện trong background thread pool để không ảnh hưởng startup chính

    -   Không phù hợp real-time data
        -   Chiến lược này được chọn vì sản phẩm ít biến động. Nếu có real-time inventory, em sẽ tách cache riêng

-   `Cách nói`

-   _Em chọn chiến lược pre-cache toàn bộ product data vì muốn đảm bảo tốc độ truy cập đồng đều cho tất cả người dùng, kể cả lần đầu tiên_
-   Dữ liệu sản phẩm ít biến động, nên bọn em ưu tiên nhất quán từ từ hơn là nhất quán nghiêm ngặt
-   Việc update cache được gom lại theo batch và dirtySet để tránh hệ thống phải ghi Redis liên tục. Kết quả là cache hit gần như tuyệt dối, latency luôn ổn định dưới 10ms, và DB load giảm mạnh

### Phỏng vấn

    - ✅ Có tư duy hệ thống (biết đánh đổi giữa consistency và performance)
    - ✅ Biết giải thích “tại sao” chứ không chỉ “làm gì”
    - biết đánh giá khi nào mô hình hiện tại hết phù hợp,
    - Nhận thức rõ độ trễ (1 phút) và giải thích vì sao chấp nhận được
    - Đề xuất hướng mở cho trường hợp
    - Nhận ra rủi ro “nhiều update → hệ thống quá tải”

    - ⚠️ Nhưng cần một chút thêm về phạm vi áp dụng & kế hoạch mở rộng, để thể hiện bạn nhìn xa hơn.

-   Sau này nếu dữ liệu sản phẩm quá lớn em nghĩ bọn em sẽ chuyển sang mô hình hybird - chỉ pre-cache top sản phẩm truy cập nhiều, còn lại dùng lazy caching để cân bằng chi phí

### “Giả sử có 2 instance của ProductService cùng chạy cronjob refresh lúc 2 giờ sáng — thì có thể xảy ra điều gì?

### Làm sao để đảm bảo cache không bị refresh dở dang hoặc dữ liệu trong Redis bị trạng thái ‘nửa cũ nửa mới’?”

### cần nâng cấp

-   Thêm khả năng idempotentn refresh

-   thêm khả năng refresh partial
-   Và đọc partial

-   update partial

### câu hỏi

-   `Tại sao em chọn chiến lược pre-cache toàn bộ dữ liệu sản phẩm thay vì lazy caching?`

-   Cả hai chiến lược đều có ưu và nhược điểm.
-   Pre-cache tốn RAM, tốn chi phí refresh, và dữ liệu có thể thiếu nhất quán trong thời gian ngắn.
-   Nhưng đổi lại, bọn em đạt được hiệu suất ổn định, cache hit gần như 100%, và giảm tải gần như toàn bộ cho DB.
-   Dữ liệu sản phẩm ít thay đổi, nên việc thiếu nhất quán trong 1–2 phút là chấp nhận được.
-   Nếu quy mô tăng lên, em sẽ chuyển sang mô hình hybrid – chỉ pre-cache các sản phẩm hot, còn lại dùng lazy caching.

-   `Nếu admin thay đổi giá hoặc hình ảnh sản phẩm, người dùng có thể thấy dữ liệu cũ trong bao lâu? Em xử lý việc cập nhật đó như thế nào?`

-   Hiện tại hệ thống có job định kỳ 1 phút để cập nhật các sản phẩm thay đổi.

-   Vì vậy độ trễ tối đa là khoảng 1 phút — chấp nhận được vì sản phẩm không thay đổi liên tục.

-   Nếu có sản phẩm hot cần cập nhật ngay lập tức, em sẽ tách ra một luồng xử lý riêng, có thể đẩy sự kiện qua Kafka hoặc Redis Stream để cập nhật ngay cache.

-   Như vậy em cân bằng giữa hiệu suất ổn định và độ tươi của dữ liệu.

-   `Nếu có nhiều instance ProductService cùng chạy job refresh cache (ví dụ lúc 2h sáng), em làm sao tránh việc cả hai cùng refresh một lúc?`

-   Em dùng Redis distributed lock để đảm bảo chỉ một instance thực hiện.
-   Cụ thể, sử dụng SETNX (setIfAbsent) kèm TTL.
-   Instance nào set được key lock đầu tiên sẽ đảm nhận việc refresh.
-   Những instance khác trả về false, chờ TTL hết rồi kiểm tra lại.
-   Nếu job fail giữa chừng và TTL hết, instance khác sẽ takeover.
-   Cách này đảm bảo đơn giản, tránh race condition và đảm bảo fault tolerance.

-   `Tại sao em cần cả pre-cache toàn bộ và dirty update theo SortedSet mà không chọn chỉ một cơ chế?`

-   Pre-cache đảm bảo tất cả sản phẩm đều có sẵn trong cache ngay khi người dùng truy cập,
-   còn SortedSet giúp em cập nhật nhanh những sản phẩm thay đổi trong ngày, tránh phải refresh toàn bộ thường xuyên.
-   Hai cơ chế này kết hợp giúp hệ thống luôn có dữ liệu mới mà vẫn giữ được hiệu suất ổn định.
-   Pre-cache chạy lúc 2h sáng, còn dirty update chạy incremental theo từng phút.

-   `Nếu job refresh cache đang chạy mà instance bị deploy lại giữa chừng thì sao?, Làm sao để cache không bị cập nhật dở dang?`

-   Lock Redis có TTL đủ dài để hoàn thành job.

-   Nếu instance bị restart giữa chừng, TTL sẽ hết hạn, lock tự động giải phóng, và instance khác sẽ đọc lastSync để chạy lại job từ đầu.

-   Như vậy đảm bảo consistency và đơn giản hóa logic.

-   Em chấp nhận refresh lại toàn bộ thay vì resume từng phần để tránh lỗi trạng thái “half updated”.

-   `Làm sao để job refresh của em idempotent — tức là chạy lại nhiều lần không gây lỗi hoặc dữ liệu sai?`

-   Em có checkpoint (lastSync) và một Redis set lưu các productId đã update.

-   Khi job chạy, mỗi batch sẽ ghi nhận ID đã xử lý.

-   Nếu job bị restart, instance khác sẽ tiếp tục từ batch chưa xong, bỏ qua các ID đã xử lý.

-   Lock Redis có TTL nên các instance không đụng nhau.

-   Như vậy hệ thống có thể retry an toàn mà không ghi đè hoặc duplicate.

-   `Nếu user đọc chi tiết sản phẩm đúng lúc hệ thống đang refresh cache cho sản phẩm đó, làm sao đảm bảo user không thấy dữ liệu “nửa cũ nửa mới”?`

-   Độ trễ nhỏ có thể chấp nhận được vì hệ thống có refresh định kỳ 1p và batch pre-warm.

-   Tuy nhiên nếu cần an toàn hơn, em sẽ thêm cơ chế atomic swap: cache mới được ghi vào key tạm (product:{id}:v2),, sau khi hoàn tất thì rename sang key chính (product:{id}). Redis rename là atomic, nên user chỉ thấy dữ liệu cũ → mới ngay lập tức, không có trạng thái giữa chừng. Nếu job fail, key chính vẫn trỏ về bản cũ.

-   `Nếu hệ thống scale gấp 10 lần traffic và có hàng trăm nghìn sản phẩm, em sẽ thiết kế lại cache như thế nào?`

-   Em sẽ chuyển sang chiến lược hybrid caching.
-   Trước hết đo lường số lượng sản phẩm được truy cập nhiều nhất để xác định Top-N “hot items”.
    = Top-N này sẽ luôn được pre-cache.
-   Phần còn lại dùng lazy cache, đồng thời lưu trong Redis ZSet {productId → score}.
-   Score được tính dựa trên tần suất truy cập và thời gian thêm vào.
-   Cronjob định kỳ giảm điểm các sản phẩm cũ và cắt tỉa cache theo ZSet.
-   Như vậy cache tự động thích nghi theo hành vi người dùng, cân bằng giữa performance và chi phí bộ nhớ.
-   Nếu scale hơn nữa, em sẽ bổ sung two-level cache (local + Redis) để giảm network cost.

### note

-   `Vì sao chọn pre-cache toàn bộ?`

    -   Trade-off giữa lazy vs pre-cache.
    -   Chọn pre-cache vì dữ liệu ít thay đổi → cache hit cao, tốc độ ổn định, giảm tải DB. Chấp nhận tốn RAM & thiếu nhất quán ngắn hạn.

-   `Độ trễ cập nhật & real-time update.`

    -   Độ trễ cập nhật & real-time update.
    -   Job 1p refresh → trễ tối đa 1p, chấp nhận được. Sản phẩm hot thì update ngay qua event (Kafka / Redis Stream).

- `Distributed coordination.`

    - SETNX + TTL
    - TTL hết thì instance khác takeover.

- `Hai chiến lược invalidate có thừa không?`

    - tốc độ ổn định.

    - giữ dữ liệu mới.
- `Job fail mid-run → partial state.`
    - Lock TTL hết → job khác restart từ đầu
    - Chấp nhận làm lại toàn bộ để đảm bảo consistency, tránh half update.

- `Retry gây duplicate update.`
    - Có checkpoint (lastSync) + Redis set lưu ID đã xử lý.
    - Nếu job retry → bỏ qua batch đã xong.

- `Race condition đọc/ghi.?`
    - Có thể xảy ra nhưng chấp nhận trong ngưỡng.
    - Nếu cần an toàn: ghi vào key tạm (v2), rename atomic sang key chính sau khi hoàn tất.

- `Nếu traffic ×10, dữ liệu ×100?`
    - Chuyển sang hybrid cache:
    - Pre-cache TopN hot (theo access count)
    - Lazy cache cho phần còn lại
    - ZSet score = freq + recency → cronjob decay & trim

    - 

