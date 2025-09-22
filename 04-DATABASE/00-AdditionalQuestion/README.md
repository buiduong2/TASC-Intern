## Additional Question

## Khi mà có nhiều cột như một tham số để tìm kiếm trong một Table chúng ta sẽ thực hiện index thế nào

-   Bảng có `tập hợp các cột cố định`
-   Khi query ta có thể lọc theo bất cứ tập con nào

-   **Vấn đề khi đánh Composite**
-   Nếu ta đánh `cho mọi tổ hợp composite` có thể xảy ra -> cực kì tốn kém (số lượng index nổ tung / việc ghi các bản ghi sẽ rất chậm)
-   Nếu tạo `index đơn lẻ cho từng cột` Postgrle có thể sử dụng `BitMap index scan` -> đây là chiến lược phổ biến và an toàn

-   **Vấn đề khi có quá nhiều Column**

-   nếu ta `tạo index riêng lẻ cho từng column` PostGreSQL có thể kết hợp một phần (bitmap index Scan) nhưng không hiệu quả cao khi query lọc trên nhiều cột

-   Nếu chúng ta `tạo index phức hợp` trên nhiều column -> Sẽ nhanh nhưng phải cẩn thận: composite index chỉ phát huy tác dụng tối đa khi query `lọc theo thứ tự từ trái sang phải` của index

```sql
CREATE INDEX idx_users_name_email ON users(name, email);
-- WHERE name = 'A' AND email = 'B' sẽ chạy rất nhanh
-- nhưn nếu WHERE email = 'B' sẽ không tận dụng tốt index này
```

-   **Cách xử lý thường dung**
    `- Chỉ đánh index cho những column thường xuyên xuất hiện trong điều kiện lọc`
-   Nếu query kêt hợp nhiều cột linh hoạt (lúc cột này, lúc cột kia) -> dùng `index riêng lẻ` cho từng cột

-   Nếu query xuât hiện thường xuyên theo một bộ cột cố định theo thứ tự -> dùng `composite index`

-   nếu query có điều kiện `OR` hoặc kiểu so khớp `text search, range SEARCH LIKE %a%` có thể cần GININdex hoặc BRIN index

-   Vây nên `ko phải lúc nào cũng đánh index hêt tất cả column`
-   Chỉ đánh dựa trên pattern truy vấn thực tế

-   `Các cột thường được Query độc lập hoặc kết hợp quá linh hoạt`

    -   Sử dụng bitMap Scan
    -   Đánh index lẻ trên từng cột quan trọng

-   `Pattern query là phổ biến ko quá tràn làn`

    -   Thêm Composite cho các cặp cột hay được dùng chung cố định

-   `Dữ liệu dạng range, timestamp lớn`

    -   Cân nhắc sử dụng `BRIN index` (tiết kiệm dung lượng)

-   Tóm lại: đầu tiên sẽ luôn bắt đầu bằng BitMap Scan. sau khi biết được các Pattern phổ biến cso thể cân nhắc sử dụng Composite index

## tìm hiểu về tìm kiếm tương đối

-   Tìm kiếm theo text (fuzzy search) `ILIKE || LIKE `

-   Sử dụng Trigram similary (pg_trgm extension) `so khớp chữ “gần giống”`

    -   Postgre sẽ tách chuỗi thành các `trigram` (3 kí tự liên tiếp) rồi tính mức độ giống nhau

-   Tìm kiếm tương đối theo FULL-Text_Search `tìm văn bản liên quan`

    -   Xếp hạng mức độ liên quan bằng ts_rank
    -   cơ chế sematic matching

-   một số toán tử
    -   `%`: % : kiểm tra similarity ≥ threshold
    -   `<% `: tìm bản ghi có similarity thấp nhất so với chuỗi
    -   `<->` : trả về khoảng cách giữa hai chuỗi (sử dụng trong ORDER BY để sắp xếp).
        -   Cả 3 cái đều tận dụng chỉ số cuỷa triagram
-   **Fuzzy (mơ hồ , xấp xỉ)**
-   Thay vì kiểm tra tuyêt đối bằng bằng. Ta có thể loại bỏ n từ để bảo nó là chính xác
-   `Levenshtein distance`: số lần cần thêm / xóa/ thay thế kí tự để A thành B
-   `Triagram similarity`: đo mức độ giống nhau dựa trên cụm 3 kí tự

-   _Triagram similarity_
-   Là một chuỗi có 3 từ liên tiếp
-   VD "chat" -> "chat" , "hat"
-   Nếu có cả dấu cách ở đầu hoặc cuối để neo từ: " chat " -> " ch", "cha" , "hat", "at "

-   So sánh chuỗi bằng trigram

    -   "cat" → trigram: " ca", "cat", "at "

    -   "chat" → trigram: " ch", "cha", "hat", "at "

    -   tập trigram chung `{ "at " }`

        -   Số lượng traigram chung / tổng trigram = độ tương đồng

    -   nó sẽ cung cấp các ngưỡng mặc định khi nào là 2 cái gióng nhau. Nếu vượt ngưỡng ta coi đó là kết quả cần tìm (danh sách)

    -   Thường hay sử dụng toán tử `%`

-   **Full text Search**

    -   toán tử `@@`

    -   Xử lý văn bản theo `dạng ngôn ngữ học`
    -   cắt câu thành từ (tokenize)
    -   Chuẩn hóa từ (normalize): thường thành dạng gốc `running -> run`
    -   Bỏ qua stop work VD (the ,is , a)
    -   Lưu thành dạng `tsvector` (một tập hợp các từ khóa + vị trí)

-   Khi tìm kiếm câu truy vấn được biến đổi thành `tsquery` rồi so khớp `tsvector`

-   Cơ chế Sematic Matching trong FTS

    -   Không quan trọng chính xác từng kí tự , và quan trọng `ý nghĩa của từ`
    -   Khác với triagram so khớp mơ hồ - FTS quan tâm đến `nghĩa` trong ngữ cảnh văn bản

    -   "organize" và "organization"
    -   chuyển đổi thành dạng gốc (VD databases (số nhiều) khớp với databas (số ít))

-   Cơ chế ts_rank

    -   Văn bản nào chứa nhiều khớp hơn thì văn bản đó có điểm cao hơn

-   Cần thiết lập chuẩn ngôn ngữ
-   Không có built-in cho tiếng việt. Nhưng có thể thể sử dụng simple để chỉ tách từ

-   **Cách để tìm trên nhiều cột**

-   tạo thêm một cột chứa tất cả các nội dung cần tìm kiếm . Và cần đánh index cho nó .

-   Có từ khóa `GENERATE ALWAYS AS () STORED`
    -   Rủi ro ko đồng bộ giữa cột search và các cột còn lại
    -   Đây là một `computed column` cột sinh ra từ biểu thức -> tự động trigger update lại cột khi phát hiện thay đổi
    -   Và giá trị đó được lưu trữ `STORED` thật trong bảng, nên ta hoàn toàn có thể tạo idnex cho nó (index để GIN hoặc BRIN hoặc cái gì đó mà có thể sử dụng trigram hoặc full-text-search)

```sql
ALTER TABLE articles
ADD COLUMN document tsvector
    GENERATED ALWAYS AS (
        to_tsvector('english', coalesce(title,'') || ' ' || coalesce(body,''))
    ) STORED;
```

```sql
CREATE INDEX users_search_trgm_idx
ON users USING GIN (search_text gin_trgm_ops);

-- Tối ưu cho
SELECT * FROM users WHERE search_text % 'Nguyen Van An';


-- Tôi ưu cho
SELECT * FROM articles
WHERE document @@ to_tsquery('english', 'database & system');

```

# Câu hỏi thêm

## Bitmap indexScan là gì

-   Nếu ta có một `composite index (col1 , col2)` Postgre có thể đi thẳng vào index đó lọc theo cả 2 điều kiện
-   `Index scan tuần tự trên index`: nó đi từ điều kiện đầu tiên, sau đó thu hẹp dần theo col2

-   `BitMapScan index`
    -   Khi ta có nhiều index đơn lẻ trên các cột khác nhau (`index_col1`, `index_col2`) query dùng nhiều cột trong `WHERE` postGreSQL sẽ làm như sau:
        -   Dùng index riêng lẻ để tìm `tâp các row TD` (Tuple Id thỏa mãn điều kiện)
        -   Mỗi tâpj hợp Row sẽ được biểu diễn dưới dạng `bitmap` (một mảng bit, đánh dấu row nào thỏa mãn)
        -   Sau đó PostgreSQL sẽ `kết hơp các Bitmap này` (AND OR) (dùng các toán tử `bitwise`)
            -   nếu WHERE col1 = ... AND col2 -> nó lấy giao (AND)
            -   Nếu WHERE col1 = ... OR col2 -> lấy hợp (OR)
        -   Khi đã có bitmap cuối cùng, nó mới đi vòa heap (dữ liệu gốc) để lấy ra các row thực tế

## lý do Compoisite index ko đảm bảo nếu số lượng và thứ tự và thự không đảm bảo

-   **Composite Index**
    -   Là `một index duy nhất` nhưng gồm nhiều cột theo thứ tự cố định
    -   Không tạo ra nhiều Index con. Thực tế là đang dùng Prefix

```sql
CREATE INDEX idx_users_name_age ON users(name, age);
-- index này giống như một danh bạ  sắp xếp đàu tiên theo naem, name bằng nhau thì sawpsx ếp theo age
```

-   Nói chung theo kiểu B-Tree nhưng là Comparator.of(name).and(age)

-   **Tại sao Composite Index phát huy tác dụng**

    -   lọc theo `col1` hoặc `(col1,col2)` hoặc `(col1,col2,col3)` tận dụng tốt
        -   Nếu chỉ lọc theo `col2` postgre không thể nhảy thẳng được. Vì index được tổ chức theo thứ tự từ col1

-   VD Composite index cho 3 column (col1, col2, col3)

    -   Nếu bỏ qua col1 Không thể biết cần bỏ qua sub-tree nào cây. Scan toàn bộ
    -   Nếu bỏ qua col-m : lọc được 1 chút từ col1. Nhưng lại phải tự scan tiếp
    -   nếu bỏ qua col-last: đại đa số col1 col-m đã được áp dụng Chỉ còn cây nhỏ hơn so với col mid chưa áp dụng được col3

-   col1 = Tầng → bạn chọn tầng đúng trước.

-   col2 = Giá sách trong tầng → bạn đi đến đúng giá sách.

-   col3 = Vị trí trên giá → bạn lấy đúng quyển sách.

## Điều kiện OR hay Text Search Range Search LIKE thì có ảnh hưởng gì

-   B-tree không phải lúc nào tối ưu, nhất là khi chúng ta sử dụng `OR`, `RANGE`, `LIKE` `full text search`, dữ liệu lớn dạng tuần tự

-   **B-Index**

    -   Tốt cho `=`, `>`, `<=`, `>=`, `BETWEEN`
    -   Cũng hỗ trợ `LIKE abc%` (prefix search)
    -   Nhưng rất kém cho `LIKE %abc` hay `LIKE %abc%` vì không biết nhảy từ đâu trong cây
    -   Dùng tốt với hầu hết query cơ bản

-   **2. GIN index (Generalized Inverted Index) => `map<String, List<Row>>`**

    -   Khuyên dùng cho

        -   `Full-Text Search` `to_tsvector` `@@_tsquery`
        -   ArraySearch `WHERE 5 = ANY(array_olc)`
        -   JSON Search `jsonb @> '{"key": "value"}'`

    -   Cơ chế: thay vì lưu cây B-Tree theo thứ tự GIN tạo "đảo ngược" -> mỗi key biết nó xuât hiện ở row nào
    -   Rất nhanh cho các điều kiện "chứa phần tử này" hoặc "chứa từ này"

-` Đảo ngược quan hệ giữa Row và Key`

-   Thay vì `row -> danh sách phần tử`
-   GIN `word -> danh sách ROw`
-   Hợp `cho mảnh rời rạc`
-   `Map phẳng`

-

```sql
CREATE INDEX idx_doc_content_gin ON documents USING gin(to_tsvector('english', content));
-- Giari thích:

-- Khi cần query
SELECT * FROM documents WHERE to_tsvector('english', content) @@ to_tsquery('database & index');
-- Giải thích:
--sử dụng GIN index:
-- content
-- Tìm ra index của database khả dụng
-- Tìm ra index của index khả dụng
-- Sau đó lấy giao

```

-   Giống hệt cách search engine (Google, Elasticsearch, Lucene) làm inverted index để tìm kiếm văn bản.

-   **3. GiST Index (Generalized Search Tree) -> `Tree<Node<max,min>>`**

    -   Cũng là index linh hoạt nhưng dùng cho

        -   Dữ liệu không gian (Post GIS, GIS query)
        -   Range types (int4Range, tsrange)
        -   Full-text-search (Chậm hơn GIN, nhưng update nhanh hơn)

    -   GiST gần giống GIN nhưng tổng quát hơn

-   Tại sao lại tổng quát hơn . Gần giống nhưng lại có tên `Tree`

    -   Thực sự xây dựng một `cây tìm kiếm đa năng`
    -   Khác với Map phảng nó là một `khung framework` ta định nghĩa cách dữ liệu được chèn và node và cách so sánh
    -   Không chỉ cho text array mà có thể với:
        -   Dữ liệu `hình học`
        -   `Range`
        -   Full text search (nhưng chậm hơn GIN)

-   Cơ chế;

    -   Các Node trong giST `lưu một vùng bao` đại diện cho nhiều giá trị
    -   Con trỏ từ node -> trỏ xuống các node `con nằm trong vùng đó`

    -   Nói chung: Kiểm tra xem giá trị có nằm trong không gian này không

-   _framework_
    -   Gist không tự hiểu string
    -   Cần cung cấp các operator như là cách
        -   so sánh 2 giá trị
        -   bao nhiêu giá trị có trong một node
        -   Cách tách khi node đầy

```md
Root
├── Node1: box (0,0) → (10,10)
│ ├── (2,3)
│ ├── (4,9)
│ └── ...
└── Node2: box (11,0) → (20,10)
├── (12,5)
└── ...
```

-   Full text Searhc với giSt
-   Không làm inverted map
-   Tổ chức các token thành một key phân vùng
-   Mỗi cây chứa một summary bounding box

```md
Root
├── Node1: ['apple'–'dog'] → chứa (apple, banana, cat, dog)
└── Node2: ['postgres'–'zebra'] → chứa (postgres, zebra)

-- Khi chạy query `Postgres` Gists sẽ đi từ node 2(g-m) rồi tiếp tục thu hẹp
```

-   Nó update nhanh hơn GIN vì

    -   GIn cập nhật một list
    -   Còn Gist chỉ thêm node thay đổi tham chiếu

-   **4. BRIN index (Block Range Index) => Trunk**

    -   Rất tiếp kiệm dung lượng
    -   Dùng khi `dữ liệu có tính tuần tự` (VD bảng log có cột `created_at` tăng dần)
    -   Nó không index từng row, mà index theo `block` (một range của dữ liệu)
    -   Rất hiệu quả với bảng cực lwowns (hàng trăm triệu row) nhưng dữ liệu có tính liền mạch

-   Cơ ché:

    -   Chia bảng thành các Block (page, thường là 8Kb)
    -   Mỗi block BRIN chỉ lưu `giá tị min và max` của cột được index
    -   Khi query `WHERE col BETWEEN X AND Y` chỉ cần check min/max của từng block để quyết định block nào `có thể chứa dữ liệu` rồi đọc block đó

    -   So sánh với PARTITION thì :
        -   PARTIION chia logic rõ ràng thành nhiều bảng, cơ chế kiểm soát mạnh mẽ hiown
        -   BRIN: là mức siêu nhẹ và nhỏ hơn của PARTITION

```sql
CREATE INDEX idx_logs_brin ON logs USING brin(created_at);

SELECT * FROM logs WHERE created_at BETWEEN '2023-01-01' AND '2023-01-02';

```

-   **5.1 Partial Index: chỉ đánh index các row thỏa mãn điều kiện**

-   Chỉ có thể áp dụng index khi mà câu query phải thỏa mãn ACTIVE = true trước rồi AND tùy chúng ta
-   Partial index giống như bạn có “bản rút gọn” của bảng, chỉ lưu những row mà bạn quan tâm.

```sql
CREATE INDEX idx_active_users ON users(email) WHERE active = true;
```

-   VD nếu bảng user có 90% active = false 10% = true thì sẽ rất tối ưu là Composite khi mà chúng ta WHERE active = true

-   **5.2. Expression index: index dựa trên biểu thức**
    -   Nếu trong tìm kiếm có sử dụng các hàm thì INdex thông thường ko sử dụng được (vì nó ko tự động convert hay lưu theo dạng convert)
    -   Còn expression thì lưu dạng đã qua convert. Khi chúng ta sử dụng cùng biểu thức trong WHERE thì sẽ tối ưu được

```sql
CREATE INDEX idx_lower_email ON users(LOWER(email));
```

## Về nahf

-   Khi nào store Khi nào dùng Function

-   Tạo các VD về index

    -   Phải có hơn 1m bản ghi chẳng hạn
        composite
    -   Giả sử 3 cái
    -   trộn hết lên có ăn index không. hay chỉ đầu cuối ăn thôi
    -   Lên lớn hơn 2 thì sẽ như thế nào
    -   Rất rõ ràng sẽ ăn rồi. TH 2 index nhưng đảo ngược vẫn ăn. Có cái Reoorder
    -   3 cái nhưng xáo trộn vị trí. Xem các trường hợp để lúc sau dùng
    -   Tại sao lại như vậy. Nhiều tình huống.

-   -   Project. lên lịch: chia ra sẽ làm nhuwgn gì. 1 dạng Deadline . Lên kế hoạch
-   Tạo các index của Pattition

-

## Về nhà có thể cho thêm một triệu dữ liêu jvafo để kiểm tra

## Reorder Index

-   trong truow3ngf hợp 2 cái . nó sẽ cố gắng tự động tối ưu hóa lại câu lệnh SQL

-   Reorder : theo chiều thuận và theo chiều nghịc thôi. không phải sắp xếp loạn mà cso thể sdungf được

## Chuyện

-   Tạo ra vài index. Tìm kiếm theo một điều kiện được hay không
-   Thay đổi thứ tự các idnex để tái sử dụng được hay không
-   Hệ thống DB tự động tối ưu hóa.

## Procdure 1 câu queryr duy nahast. CÓ thể viết ở tầng ứng dụng. Tại sao người ta lại cần viết ở DB

-   **Hiệu năng và tối ưu db**

    -   Execution plan cache (người lên kế hoạch tối ưu query - biên dịch) chạy nhiều lần thì nhanh hơn query động
    -   Toàn bộ Logic chạy ngay trong DB engnie , giảm network overhead và tận dụng khả năng tối ưu của DB

-   **XỬ lý nghiệp vụ ngay trong DB**

    -   Có những luật về nghiệp vụ liên quan chặt chẽ đến dữ liệu (tính toán số dư, phân bổ tồn kho, chạy batch xử lý hàng triệu record)

    -   Để ở tầng ứng dụng sẽ phải kéo dữ liệu về update ngược lại -> tốn tài nguyên và chậm

    -   Đặt trong DB thì xử lý 1 lần ngay trong DB, thay vì tự quản lý transaction

-   **Bảo mật phân quyền**
    -   có khi không muốn SELECT/UPDATE tực tiếp vòa bảng'
    -   cấp quyền cho app chỉ có thể gọi `EXECUTE PROCEDURE`
-   **Tái sử dụng và chuẩn hóa**

    -   PROCEDURE dùng chung cho nhiều ứng dụng (JAVa APp, NET APP, Report Tool, script)
    -   Nếu viết query ở tàng ứng dụng mỗi team sẽ tự viết , dễ sai khác
    -   Viết ở DB thì gọi chung một logic

    -   _tận dụng khả năng tối ưu của DB_

-   **Tòm lại . Nếu native Query SELECT + PROCEDURE đều gọi được ở tầng ứng dụng**
    -   Stored Procedure tạo `API ổn định ở tầng DB`, che giấu chi tiết schema.
        -   Schema có thay đổi thế nào thì ta vẫn chỉ làm việc với `API - tên function`
    -   DBA có thể `tối ưu / thay đổi bên trong` mà không cần `build & deploy ứng dụng`.
    -   Cho phép kiểm soát `execution plan` tốt hơn, tránh sự khác biệt do ORM hoặc driver
    -   Có tên rõ ràng → `dễ log (log tên đã gọi), giám sát, đo đạc hiệu năng.`
    -   `Tái sử dụng` cho `nhiều ứng dụng` / report, giống như view có tham số.
    -   Có thể `hot-fix trực tiếp trên DB`khi cần khắc phục sự cố khẩn cấp.
    -   Chuẩn bị cho tương lai: dễ dàng chuyển sang mô hình `phân quyền chỉ EXECUTE`, tăng bảo mật.

## Store 

- Được Compile sẵn nên tốc độ sẽ nhanah hơn chút
- Câu được dùng ở nhiều nơi trong application
- Khi sửa code: rất phức tạp. 

- Khi nào sử dụng Store
    - Khi mà câu Query phức tpaj JOIN từ 3-4 bảng trở lên có nhiều điều kiện khác nhau thì viết Store
    
- Trong trương hợp khi mà có 1 thay đổi phải update hoặc delete nhiều bảng một lúc 
    - Viết trong 1 store. update DELETE . 
    - Quản lý sổ đỏ. Phải update 1 lúc hơn 10 bảng. 

- Kho quản lý khi sử dụng Store
    - Viêt 2 store cùng 1 lúc. Bị rác store


### 

