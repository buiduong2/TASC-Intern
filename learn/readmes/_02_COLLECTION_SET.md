## 4. Set Interface

## Nêu ra các đặc điểm Set Interface

-   **khái niệm**

-   `Set` làm một `interface` trong `java.util` mở rộng từ `Collection`
-   Đại diện cho `tập hợp các phần tử` mà `không cho phép trùng lặp`
-   Không duy trì thứ tự phần tử, trừ khi sử dụng triển khai đặc biệt như `LinkedHashSet`

-   **Đặc điểm**

-   Không cho phép phần tử trùng lặp
-   Không có chỉ số index
-   Thường dùng hàm hash / equals

    -   Các triển khai phổ biến như `LinkedHashSet` hay `HashSet` dựa trên `hashCode()` và `equals()` để xác định trùng lặp

-   Có thể duy trì thứ tự
    -   `HashSet` -> không duy trì thứ tự
    -   `LinkedHashSet` di trì thứ tự giảm dần
    -   `TreeSet`-> duy trì thứ tự tự nhiên . hoặc theo `Comparator`
    -

## Kể ra các class triển khai từ Set Interface

- **HashSet**
- Lưu trữ phần tử không theo thứ tự
- CHo phép `null` 1 phần tử
- Tìm kiếm thêm xóa nhanh O(1) trung bình

- **LinkedHashSet**
- Giống `HashSet` nhưng `giữ thứ tự chèn`
- Duyệt theo thứ tự phần tử được thêm vào

- **TreeSet**
- Sắp xếp phần tử `theo thứ tự tự nhiên` hoặc theo `Comparator`
- Không cho `null` (trừ khi Comparator hỗ trợ)
- Thao tác như tìm kiếm xóa log O(n)


## Phân biệt rõ trường hợp sử dụng của từng class đó

- **HashSet**
- Không quan tâm đến thứ tự
- Cần tìm kiếm trùng lặp `nhanh`
- VD: Khi chúng ta chỉnh sửa một bản ghi. Mà bản ghi đó có các thuộc tính có sẵn . Chúng ta ko muốn xóa nó đi. Chúng ta sẽ tạo Set từ các bản ghi đó. Và kiểm tra với bản ghi sắp được sửa. Loại bỏ các bản ghi trùng lặp ko cần thiết

- **LinkedHashSet**

- Khi cần loại bỏ trùng lặp + duy trì thứ tự chèn
- VD ta cần lưu lại lịch sử xem các sản phẩm của một User. Ta cần kiểm tra sự độc nhất thông qua Id của sản phẩm. các sản phẩm cũ hơn sẽ tự động bị xóa

- **TreeSet**

- Khi cần `Set sắp xếp tự động`
- Cần các thao tác `range` (`SubSet` ,`headSet`, `tailSet`)
- VD: Khi ta cần lợi dụng tính tự động sắp xếp tối ưu của chúng . và tính độc nhất. Vậy ta sẽ sử dụng cho top N sản phẩm bán chạy nhất 

