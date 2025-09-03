## Query

## 1. Trong một câu Query thứ tự thực hiện các thành phần trong đó như thế nào

-   Tóm tắt:

```
FROM → JOIN → WHERE → GROUP BY → HAVING → SELECT →(WINDOW / OVER) →  DISTINCT →  → ORDER BY → LIMIT/OFFSET
```

-   _1. FROM_

    -   Chức năng: Xác định bảng hoặc view nào sẽ lấy dữ liệu
    -   Tạo các `row pointer` đến các bản ghi tiềm tàng

-   _2. JOIN_

    -   Chức năng: Kết hợp dữ liệu từ nhiều bảng
    -   Kết quả là `row pointers kết hợp` từ các bảng.

-   _3. WHERE_

    -   `Lọc row pointers` theo điều kiện.
    -   Chỉ row pointers thỏa điều kiện được giữ lại cho các bước tiếp theo
    -   Chỉ áp dụng cho các bản ghi `chưa nhóm`, trước GROUP BY
    -   Quét bảng hoặc INDEX. Đánh dấu những dòng phù hợp

-   _4. GROUP BY_

    -   Gom nhóm các bản ghi theo 1 hay nhiều cột
    -   Sau bước này mới có thể sử dụng các `Aggregate Function` (tổng hợp) (SUM, COUNT, AVG)

-   _5. HAVING_

    -   Lọc nhóm dữ liệu được tạo ra từ `GROUP BY`
    -   Tương tự như WHERE nhưng chỉ dùng cho các nhóm. Không phải các bản ghi đơn lẻ (trước khi Aggregate Function)

-   _6. SELECT_

    -   Chọn cột dữ liệu cần hiển thị (xác định các cột cần lấy - không nhất thiết phải là các cột thực (cache))
    -   Ở bước này mới tính toán các hoặc alias

-   _7. DISTINCT_

    -   Loại bỏ các bản ghi trùng lặp
    -   Áp dụng sau khi SELECT xong

-   _8. ORDER BY_

    -   Sắp xếp kết quả theo các cột
    -   nếu có LIMIT OFFSET ( Có thêm Index phù hợp: nó sẽ chỉ Scan 1 phần của KQ đã WHERE ra)

-   _9. LIMIT OFFSET_

    -   Chức hạn số lượng kết quả trả về
    -   Thường tối ưu bằng cách dịch chuyển N pointer từ pointer lấy ra từ trong bản ghi cứng để trả về

-   Ngoài ra còn có các thành phần khác

-   _10. SubQuery_

    -   Có thể xuất hiện ở nhiều vị trí
    -   Sub query `được thực hiện trước khi sử dụng như KQ` của câu Query trong bảng chính
    -   Là một Query nằm trong một Query khác

-   _11. Window Over_
    -   Sau khi `FROM` `JOIN` `WHERE` `GROUP BY` nhưng trước khi câu lệnh SELECT vật lý hoàn chỉnh
