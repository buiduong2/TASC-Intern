## JAva Collection

### 1. Khái niệm

-   Mảng là một cấu trúc dữ liệu dùng để lưu trữ nhiều phần tử `cùng kiểu dữ liệu` trong một biến duy nhất
-   các phần tử trong mảng có địa chỉ trong bộ nhớ liên tiếp nhau
-   mảng trong Java có kích thước cố định (không thể thay đổi sau khi tạo)
-   Kiểu dữ liệu trong mảng có thể là nguyên thủy (primitive) hoặc là kiểu đối tượng (object)
-   Mảng trong Java là dựa trên chỉ mục (index) , bắt đầu từ chỉ mục `0`

-   Phần tử : Mỗi mục được lưu trong một mảng gọi là một phần tử
-   Chỉ mục (index): mỗi vị trí của một phần tử trong một mảng có một chỉ mục số được sử dụng để nhận diện phần tử

### 2. Cách khai báo mảng

```java
int[] a;     // cách thường dùng
```

### 3. Cấp phát bộ nhớ (khởi tạo)

-   Khi khai báo mảng cần xác định số lượng phần tử

```java
int[] arr = new int[3]; // mảng 3 phần tử
// Hoặc gán giá trị ngay
int[] arr = {1,2,3,4,5,6};
```

#### 4. Truy cập phần tử

-   Chỉ số `INdex của mảng bắt đầu từ số 0` và kết thúc ở `length - 1`

```java
int[] arr = {10,20,30};
System.out.println(arr[0]); // in ra 10
System.out.println(arr[2]); // in ra 30
```

#### 5. Thuộc tính và phương thức quan trọng

-   `arr.length` -> trả về độ dài của mảng
-   Không có nhiều phương thức như `ArrayList` nhưng ta có thể dùng các `Arrays` utitlity class

```java
import java.util.Arrays;

int[] arr = {3,2,1};
Arrays.sort(arr); // Sắp xếp tăng dần
System.out.println(Arrays.toString(arr)) //[1,2,3]
```

#### 6. Mảng nhiều chiều

-   Java hỗ trợ mảng `2 chiều`, `3 chiều` (n chiều)

```java
int[][] matrix ={{1,2,3},{4,5,6}};
System.out.println(matrix[1][2])
```

#### 7. Mảng và bộ nhớ

-   Mảng là `đối tượng` trong Java (ngay cả khi lưu kiểu biến nguyên thủy)

-   Khi ta khai báo

```java
int[] arr = new int[5];
// arr là biến tham chiếu trỏ tới một vùng nhớ trên heap chứa 5 phần tử
```

#### 8.Ưu và nhược điểm

-   Ưu điểm : vì các phần tử nằm liền kề trong bộ nhớ, nên việc truy cập bằng chỉ số rất nhânh

    -   Phần tử truy cập với thời gian hằng số O(1)
    -   Sử dụng bộ nhớ hiệu quả
    -   Tính cục bộ về bộ nhớ
    -   Có thể chứa các kiểu dữ liệu nguyên thủy

-   Nhược điểm
    -   Kích thước cố định
    -   Không cung cấp sẵn các phương thức tiện ích. Nhưng ta có thể sử dụng thông qua thư viện như `Arrays`
    -   Không kiểm soát tốt dữ liệu .
        -   Vì nếu không biết trước kích thước dễ cấp phát thừa hoặc thiếu chỗ trong array
