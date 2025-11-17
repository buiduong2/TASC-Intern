## Bài tập

Setup Maven + POI

Bài 1: Tạo workbook, sheet, cell

Bài 2: Write file → export thành công

Bài 3: Read file → import excel

Bài 4: Working with types (String/Number/Date)

Bài 5: Style (border, font, màu)

Bài 6: Merge cell, auto-size

Bài 7: Template (đọc file mẫu)

Nếu muốn → chuyển sang Spring

## Bài 1

✔ Tạo một Workbook mới (bằng XSSFWorkbook)
✔ Tạo một Sheet tên “Students”
✔ Tạo một Row đầu tiên
✔ Tạo 3 Cell trong Row đó
✔ Gán giá trị text cho mỗi cell (VD: "ID", "Name", "Age")
❗ Chưa cần ghi ra file (đó là Bài 2)

### cấu trúc Apache POI

```
Workbook
 └── Sheet
       └── Row
             └── Cell
```

-   Các loại WorkBOok :

```java
Workbook wb = new XSSFWorkbook(); // 99% trường hợp . dùng cho file xlsx

Workbook wb = new HSSFWorkbook(); // .xls (excel cũ) - Chỉ dùng khi làm hệ thống cũ (hiém)

Workbook wb = new SXSSFWorkbook(); // Ghi theo Stream, không tốn nhiều RAM
```

### Start

```java
Workbook workbook = new XSSFWorkbook();  // Tọa file excel mới

Workbook workbook = WorkbookFactory.create(new File("input.xlsx")); // Đọc từ file có sẵn

```

## Bài 2:

#### Workbook hiện tại của bạn ĐANG nằm trong RAM

```java
Workbook workbook = new XSSFWorkbook();
```

-   POI tạo ra một `cấu trúc EXECL ảo` trong bộ nhớ RAM

#### Workbook là một Object cần được ghi ra OutputStream

-   OutputStream là cách Java chuẩn để ghi dữ liệu dạng byte

-   POI dùng OutputStream để đẩy toàn bộ file Excel (dạng ZIP) ra bên ngoài mà không phụ thuộc vào nơi nó được ghi: file, mạng, memory…

-   Excel thực chất là một loại file XML nén dạng zip.

```java
workbook.write(outputStream);

```

-   tạo file zip
-   embed toàn bộ sheet/row/cell vào
-   convert thành chuẩn XLSX
-   ghi ra file thật

#### Bạn cần biết sự khác nhau giữa FileOutputStream và Workbook.write()

-   Quy trình

```
XSSFWorkbook -> generate XML -> compress -> push into OutputStream -> saved file

```

#### Workbook tự quản lý toàn bộ cấu trúc bên trong

```java
Workbook wb = new XSSFWorkbook();
Sheet sheet = wb.createSheet("Students");
Row row = sheet.createRow(0);
Cell cell = row.createCell(0);
cell.setCellValue("ID");


// Bạn không cần làm gì thêm.
```

-   Mỗi create() tự gắn đối tượng vào cha của nó \*

-   Tất cả đều tự lưu vào Map hoặc TreeMap bên trong.

-   createRow(index) gọi trùng index sẽ trả về row CŨ

## Bài 3:

-   POI có thể tự import execl thành các

## Bài 4

Tạo file Excel gồm:

String

Number

Date

Boolean

Viết file ra Excel (export)

Import lại file đó:

readString

readDouble

readDate

readBoolean

### POI cung cấp enum:

```java
CellType
    STRING
    NUMERIC
    BOOLEAN
    FORMULA
    BLANK
    ERROR
    _NONE

```

#### Numeric chưa chắc là số → có thể là ngày!

-   Ta thấy 2024-11-17 -> 45314.0

-   01/03/2021 -> 44287.0

-   Excel lưu dưới dạng số, chỉ format để hiển thị

-   Cách nhận biết ngày:

```java
if (DateUtil.isCellDateFormatted(cell)) {
    Date date = cell.getDateCellValue();
}
```

## Bài 5

-   sử dụng lại Student từ bài 4 (id, name, createdAt, isActive)
-   1. Tạo Header có style đẹp

    -   Border (4 phía)
    -   Font bold
    -   Background màu (chọn màu bất kỳ từ IndexedColors)
    -   Font màu trắng
    -   Căn giữa (horizontal center + vertical center)

-   2. Style cho các dòng data

    -   Border mỏng
    -   Căn trái (trừ date)
    -   Date dùng format: "yyyy-MM-dd HH:mm:ss"
    -   Boolean hiển thị TRUE / FALSE như bình thường

-   3. Auto-size tất cả các cột

-   4. Tạo 2 style khác nhau

-   Bonus (Không bắt buộc nhưng nên làm)
    -   Alignment của header = center
    -   Alignment của data = left
    -   Alignment của date = center
    -   Set column width = 20 đơn vị trước autosize (để đẹp hơn)

## Bài 6

-   `Merge cell`

```java
sheet.addMergedRegion(new CellRangeAddress(
        0, 0, 0, 3
));

// (rowStart, rowEnd, colStart, colEnd)
//chỉ giữ nội dung ô đầu tiên (A1)
// các ô còn lại bị merge vào (bị "chết")

```

-   `Bạn phải set style cho TẤT CẢ ô trong vùng merge, không chỉ ô đầu`

-   YÊU CẦU 1 — Tạo TITLE đẹp

    -   Dòng 0: merge từ A1 → D1 (4 cột)
    -   Text: "STUDENT REPORT"
    -   Font size 16
    -   Bold
    -   Căn giữa cả horizontal + vertical
    -   Background màu nhẹ (tuỳ chọn)
    -   Row height tăng (ít nhất 30pt)
    -   Border bên dưới title (optional)

-   YÊU CẦU 2 — Header (dòng 1)

    -   Không merge
    -   Bold
    -   Màu xanh đậm + font trắng
    -   Căn giữa
    -   Height 20pt

-   YÊU CẦU 3 — Body (dòng 2 trở đi)

    -   Có border
    -   Căn trái
    -   Date style đúng format
    -   Boolean hiển thị Normal
    -   Row height auto hoặc mặc định
    -   Không merge

-   YÊU CẦU 4 — Wrap text cho NAME (optional)

    -   Nếu bạn muốn tên dài hiển thị đẹp:

-   YÊU CẦU 5 — Auto-size column sau khi ghi file
    -   Ít nhất 4 cột ID/Name/Date/Active.

## Bài 7 Template

-   `1. Template = file .xlsx có sẵn layout`

-   Ta thiết kế sẵn file Excel

    -   Title
    -   header
    -   Màu sắc
    -   Logo
    -   Merge
    -   Border
    -   Alignment
    -   Column width
    -   Freeze Pane
    -   Ghi chú (sheet 2)

-   Java sẽ k tạo Execl từ con số 0 mà sẽ:

    -   mơ template
    -   Lấy sheet
    -   Tìm row bắt đầu ghi data
    -   Fill data vào
    -   Ghi ra file mới

-   `2. POI mở template bằng WorkbookFactory`

```java
Workbook wb = WorkbookFactory.create(new File("template/student_template.xlsx"));

```

-   WorkbookFactory

    -   Tự chọn HSSFWorkBook (XSSFWOrkBook)
    -   HỖ trợ password (không dùng ở đây)
    -   Là cách chuẩn để load file thực

-   `3. Giữ nguyên style của Template`

```java
Row row = sheet.createRow(dataRowIndex);
Cell cell = row.createCell(0);
cell.setCellValue(student.getId());

```

-   Excel sẽ `giữ lại style của template nếu`

    -   Row tồn tại trước trong Template
    -   Hoặc ô có style base ở template
    -   Hoặc ta copy style từ một ô mẫu

-   `4. Ghi dữ liệu nhưng ko phá merge cell`
    -   KO được tạo lại row
-   `5. Xác định vị trí row bắt đầu ghi data`

    -   `int dataStartRow = 4`

-   `6. Nên có 1 row mẫu để clone style`

    -   Template thường có 1 dòng trống (row template)

## Bài 7

-   Trong Excel, tạo file như sau:
-   Sheet “Students Report”
-   Row 0 – Title

    -   Merge A1:D1
    -   Text: "STUDENT REPORT"
    -   Font 16, bold
    -   Background Aqua
    -   Center
    -   Row height 30pt

-   Row 1 – Header

    -   A1: ID
    -   B1: Name
    -   C1: Created At
    -   D1: Active
    -   Background xanh
    -   Font trắng
    -   Border
    -   Height 20pt

-   Row 2 – Template data style

    -   Bạn để trống nhưng set border cho toàn row
    -   Cột date set format yyyy-MM-dd
    -   Đây là row mẫu để clone style

-   Row 3 – bắt đầu ghi data

Bạn không ghi gì ở row 3.
