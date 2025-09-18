# Báo cáo dự án thực tập - Mini Mart

## Câu hỏi

-   Tại khi đăng nhập vào facebook tại sao 1 năm sau nó vẫn lưu đăng nhập

## 1. Giới thiệu

Mini Mart là ứng dụng web E-commerce dạng demo, được xây dựng trong thời gian thực tập với mục tiêu áp dụng các kiến thức đã học về **Java, Spring Boot, JPA, Spring Security** và thiết kế cơ sở dữ liệu.

-   Tập trung vào quản lý sản phẩm, đơn hàng và kho, có tính năng quản lý nhập hàng để phục vụ tính toán lợi nhuận

## 2. Các chức năng

### Product

-   Tạo cập nhật xóa, xem chi tiết
-   Mỗi sản phảm chỉ có một hình ảnh đại diện
-   Mõi sản phẩm thuộc 1 danh mục (1 cấp)
-   Không có biến thể hay thuộc tính động để lọc
-   Có các trạng thái (ACTIVE, DRAFT, ARCHIVED)

-   Quản lý thẻ (Tag) để gán cho sản phẩm

### Category

-   Quản lý danh mục sản phẩm 1 cấp
-   Danh mục có thể kèm theo hình ảnh địa diện
-   Trạng thái danh mục (ACTIVE, DRAFT, ARCHIVED)

### Inventory

-   Purchase/ purchaseItem: quản lý các lần nhập hàng từ nhà cung cấp
-   FIFO: sử dụng remaining_quantity để xuất kho theo lô hàng
-   Stock: lưu tổng số lượng hiện tại trong kho (1 kho duy nhất)
-   Tính `giá vốn hàng bán` (cost_price) dựa trên các lần nhập
-   Từ đó tính ra `lợi nhuận` khi so sánh với giá bán

### Order

-   Quản lý đơn hàng với nhiều sản phẩm
-   quản lý trạng thái đơn hàng
-   Quản lý thanh toán
-   Khi tạo đơn hàng sẽ allocate stock (trừ dần PurchaseItem FIFO)

### User

-   Custtomer: thông tin khác hàng
-   User: thông tin tài khoản người dùng trong hệ thống
-   Quản lý phân quyền role . User- role
-   ADdress: qunar lý địa chỉ của khách có nhiều address

## 2.1. Ứng dụng Query theo nhiều cách

-   projection

    -   Sử dụng với User

-   JdbcTemplate Dùng cho

## 3. Luồng nghiệp vụ chính

### nhập hàng vào kho

-   0. Tạo các thông tin cơ bản về sản phẩm nếu chưa có

    -   Tạo ra một Stock với quantity = 0 tương ứng;

-   1. Tạo phiếu nhập

    -   Nhập vào là hoạt động không phê duyệt

-   2. Thêm các sản phẩm (PurchaseItem)

    -   `product_id`
    -   `quantity`
    -   `cost_price` -> cơ sở tính toán lợi nhuận
    -   Từ đó ta có `remainingQuantity`

-   3. Cập nhật tồn kho Stock (cache)
    -   Với mỗi sản phẩm vừa thay đổi số lượng -> Cập nhật tồn kho Stock `SUM(purchaseItem.remainingQuantity)`

### Luồng tạo order (PENDING)

-   1. Nhận thông tin Order từ Khách

    -   Customer
    -   OrderAddress
    -   ShippingMethod
    -   PaymentMethod
    -   List OrderItems

-   2. Tạo các bản ghi tương ứng

    -   status = PENDING
    -   payment.status = PENDING
    -   lưu total snapshot

-   3. Tạo các OrderItem

    -   Tìm các sản phẩm tương ứng (phải có Status = ACTIVE)
    -   Lấy ra giá sell (bằng giá niêm yết trên web)

-   4. Ghi nhận Stock Allocation

    -   Tìm các PurchaseItem còn sản phẩm và tiến hành FIFO
    -   Phân bổ số lượng
    -   Ghi nhận StockAllocation để dễ dàng truy vết và hoàn trả

-   5. Tính ra avg Cost cho OrderItem để theo dõi lợi nhuận tạm thời

-   6. cập nhật Stock

    -

-   Như vậy tránh được oversell. Nhung lại có thể dính Order ảo . Ma quái phá hoại

### Luồng CANCLED order user

-   Của user

    -   Quyền sở hữu phải thuộc về User
    -   Status === PENDING mới cho hủy
    -   CARD + amounPaid > 0 . Không được hủy
    -   CASH chưa thành toán cho phép hủy

-   Service:

    -   findBy Id
    -   Kiểm tra trạng thái

    -   Update trạng thái
    -   Release Stock Allocation

## AUth

-   Đăng kí đăng nhập

    -   Cung cấp Token để request các lần tiếp theo

-   Đổi mật khẩu

    -   Ép buộc tất cả các phiên đăng nhập trước đều vo hiêu họa

-   RefreshToken

    -   Làm mới accessToken, Giữ nguyên refreshToken

-   logout
    -   Vô hiệu hóa Token trong phiên hiện tại

## Luồng Update Purchase Item

-   **Kịch bản cần update**

-   Nhập liệu sai

    -   UPDATE quantity || remaining_quantity || cost_price

-   Khi cần điều chỉnh kho so với thực tế

    -   UPDATE remianing_quantity

-   Điều chỉnh chi phí

    -   UPDATE cost_price

-   Hoàn trả bớt hàng cho supplier

    -   UPDATE quantity + remaining_quantity

-   **Kịch bản delete**
-   Nhập liệu saio
    -   Nhập nhầm product_Id hoặc purchase_id . ép buộc xóa đi tạo cái khác
-   Hủy ko nhận một mặt hàng đơn lẻ

-   **Khó khăn**

-   Không thể cập nhật cứng remaining_quantity: bởi vì trên client UI ko phản ánh chính xác số lượng thực tế. Mà nó chỉ phản ánh thời điểm load lên mà thôi không thể xử lý một cách `absolute` được

-   **Hướng giải quyết**

-   Tạo ra 3 API: API 1 cho nhập liệu sai: `PUT /api/purchase-items/{id}`
    -   newQuantity (absolute), newCostPrice
    -   Số lượng Quantity nhập vào ko đứng thực tế -> remaining + quantity
-   Điều chỉnh kho so với thực tế:
    -   `POST /api/purchase-items/{id}/adjustments`
    -   adjustmentDelta (delta) , UPDATE remainingQuantity
    -   Mặt hàng remaining bị thất thoát -> Điều chỉnh remaning -> quantity(đã nhập) giữ nguyên
-   return
    -   returnQuantity: (delta)
    -   Trả hàng lại cho nhà sản xuất, hoặc lấy thêm đơn hàng -> Giảm remaining + quantity

## Luồng về xóa PurchaseItem

-   `DELETE PurchaseItem`
-   Trường hợp có thể xóa

    -   Chưa có StockAllocation nào liên kết → tức là chưa từng được xuất cho đơn hàng nào.
    -   remainingQuantity == quantity (chưa bị dùng một phần nào).

-   Không được xuất nếu

    -   Đã có StockAllocation (vì đã có xuất kho, liên quan đến lịch sử bán hàng).
    -   remainingQuantity < quantity (tức là đã có hàng được sử dụng, không toàn vẹn dữ liệu nếu xóa).

    -   Trong trường hợp cần thì có thể Update PurchaseItem để remaining nó về 0

-   `DELETE Purchase`
-   Được xóa:
    -   Tất cả các `PurchaseItem` thuộc nó đều chưa được sử dụng
    -   không có StockALlocation nào tham chiếu gián tiếp qua `PurchaseItem`
-   Không được xóa nếu:
    -   `PurchaseItem` đã được sử dụng 1 phần (remainingQuantity < quantity).
    -   Có `StockAllocation` tồn tại.

## Luồng về DELETE PRoduct

-   Không được xóa nếu:
    -   Tham chiếu từ PurchaseItem
    -   Tham chiếu từ OrderItem
-   Khi xóa thành công cần
    -   Xóa ProductImage, product_tag (many to Many)

## Luong DELETE category

-   Xóa category.

-   Tách tham chiếu khỏi Product

## Luồng thực hiện Payment

- `Đôi khi việc callback ko được gọi lại vào server của chúng ta`

- User bị trừ tiền, nhưng hệ thống của bạn không nhận được callback
- Payment của bạn vẫn là PENDING.

- Lưu PaymentTransaction khi tạo link
- Nhân viên CSKH có thể nhập mã giao dịch (vnp_TransactionNo) để query trực tiếp từ VNPay và update đơn hàng.

```java
@Entity
@Getter @Setter
@Table(name = "payments")
public class Payment {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    /** Liên kết với Order */
    @OneToOne
    @JoinColumn(name = "order_id", nullable = false)
    private Order order;

    /** Phương thức mà user chọn cho đơn hàng */
    @Enumerated(EnumType.STRING)
    @Column(nullable = false, length = 20)
    private PaymentMethod method;   // COD, VNPAY, MOMO...

    /** Trạng thái thanh toán tổng quan của đơn hàng */
    @Enumerated(EnumType.STRING)
    @Column(nullable = false, length = 20)
    private PaymentStatus status;   // UNPAID, PARTIAL, PAID

    /** Tổng số tiền cần trả (snapshot từ Order) */
    @Column(nullable = false)
    private BigDecimal totalAmount;

    /** Tổng đã trả (cộng dồn từ transaction thành công) */
    @Column(nullable = false)
    private BigDecimal paidAmount = BigDecimal.ZERO;

    /** Số tiền còn lại */
    @Column(nullable = false)
    private BigDecimal dueAmount;

    /** Lịch sử các transaction gắn với Payment */
    @OneToMany(mappedBy = "payment", cascade = CascadeType.ALL, orphanRemoval = true)
    private List<PaymentTransaction> transactions = new ArrayList<>();

    /** Cập nhật số tiền còn nợ */
    public void updateAmounts(BigDecimal newPaid) {
        this.paidAmount = this.paidAmount.add(newPaid);
        this.dueAmount = this.totalAmount.subtract(this.paidAmount);
        if (dueAmount.compareTo(BigDecimal.ZERO) <= 0) {
            this.status = PaymentStatus.PAID;
            this.dueAmount = BigDecimal.ZERO;
        } else if (paidAmount.compareTo(BigDecimal.ZERO) > 0) {
            this.status = PaymentStatus.PARTIAL;
        } else {
            this.status = PaymentStatus.UNPAID;
        }
    }
}

```

```java
@Entity
@Getter @Setter
@Table(name = "payment_transactions")
public class PaymentTransaction {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @ManyToOne(optional = false)
    @JoinColumn(name = "payment_id")
    private Payment payment;

    /** Số tiền của lần thanh toán này */
    @Column(nullable = false)
    private BigDecimal amount;

    /** Trạng thái lần giao dịch này */
    @Enumerated(EnumType.STRING)
    @Column(nullable = false, length = 20)
    private TransactionStatus status;   // PENDING, SUCCESS, FAILED

    /** Mã tham chiếu bạn sinh ra khi tạo URL gửi VNPay */
    @Column(length = 100, nullable = false, unique = true)
    private String txnRef;

    /** Mã giao dịch bên VNPay trả về */
    @Column(length = 100)
    private String transactionNo;

    private String bankCode;
    private String cardType;

    private LocalDateTime createdAt = LocalDateTime.now();
    private LocalDateTime payAt;
}
```

```java
public enum PaymentMethod {
    COD, VNPAY, MOMO, ZALOPAY
}

public enum PaymentStatus {
    UNPAID, PARTIAL, PAID
}

public enum TransactionStatus {
    PENDING, SUCCESS, FAILED, CANCELLED
}
```

## Luồng

- **B1. Tạo Order**
- Backend nhận request tạo đơn hàng.
- Tạo record `Order`
- Tạo kèm một record `Payment` (chỉ giữ thông tin tổng quan).
    - method = null hoặc method = COD
    - status = UNPAID.
    - totalAmount = order.total
    - paidAmount = 0, dueAmount = total

- **B2. Khi user chọn phương thức thanh toán**
    - FE gửi request `POST /orders/{id}/payment/method`
    - Backend update `Payment.method`.
    - Nếu chọn Online (VNPay/Momo…) → user sẽ cần click thêm lần nữa để “thanh toán ngay”
    - Nếu chọn COD → Payment vẫn ở trạng thái `UNPAID` cho đến khi shipper xác nhận đã thu.

- **B3. Khi user click “Thanh toán online”**

- Backend tạo một bản ghi PaymentTransaction
- status = PENDING.

- amount = payment.dueAmount.

- txnRef = sinh mã unique.
- `Payment`
- Backend build `payUrl` (VNPay/Momo…) dựa trên txnRef, amount, orderId

- **B4: Khi cổng thanh toán redirect về**

- Backend nhận callback với `txnRef`, `amount`, `transactionNo`, `bankCode`, …
- Tìm PaymentTransaction theo `txnRef`.

- Nếu `SUCCESS` → update transaction = SUCCESS, set `payAt = now().`
- Gọi `Payment.updateAmounts(amount)` để cộng vào `paidAmount` và tính lại `dueAmount` + `status`.
- Nếu `FAILED` hoặc user cancel → update `transaction = FAILED`.