## Tutorial ObjectivePermalink

- ứng dụng mục tiêu của chúng ta là một ứng dụng danh mục xe hơi đơn giản. ứng dụng này có 2 chức năng
- Search cars
  - Nhập một từ khóa vào ô nhập lệu, nhấn search và kết quả tìm được sẽ hiển thị trong danh sách xe bên dưới
- View Details: 
  - Nhấn vào một mục trong danh sách xe, khu vực bên dưới dan hsasch xe sẽ hiển thị chi tiets của chiếc xe được chọn, bao gồm cả mẫu xe, mô tả và hình xem trước

- **`Giải thích thêm`**
- `1. Tổng quan kết trúc theo góc nhìn java Spring`
- Tài liệu giới thiệu mô hình `MVVM (model view - View Model)` trong ZK
- Nếu so với Spring bôt : 
  - Model -> tương unwgsg voicws entity / DTO trong Spring
  - View: file `.zul` (Giống như template Thymeleaf hoặc Vue Template)
  - ViewModel -> tương tự một lớp trung gian giữa Controller và Service
- trong Spring MVC
  - Controller nhận request
  - Gọi service
  - Trả về Model cho View
- Trong ZK MVVM: 
  - View (ZUL) bind trực tiếp với ViewModel
  - ViewModel chứa State và logic xử lý
  - Không có khái niệm HTTP request/response như Spring MVC truyền thống
- `2. Phần tích 2 chức năng theo lifecycle`
- (1) Search cars: 
  - Luồng xử lý trong MVVM: 
    - 1. Người dùng nhập key vào input'
    - 2. Click nút search
    - 3. View trigger một command trong ViewModel
    - 4. ViewModel: 
      - Nhận keyword (thông qua binding)
      - Gọi service tìm kiếm
      - Cập nhật danh sách xe
    - 5. Binding tự động update UI
  - So với Spring: 
    - Không cần `@PostMapping`
    - Không cần return Model
    - Không cần reload page
    - Binding engine ZK tự refresh UI
- (2) view details: 
  - luồng xử lý: 
    - 1. Người dùng click vào một item trong danh sách
    - 2. Selected Item được bind vào một thuộc tính trong ViewModel
    - UI bên dưới bind vào thuộc tính đó
    - 4. Khi selectedCar thay đổi -> UI tự động cập nhật
  - So với Spring MVC: 
    - Không phải redirect hoặc load trang chi tiết
    - hoặc dùng AJAX để load fragment
  - Trong ZK MVVM: 
    - Chỉ cần thay đổi state của ViewMode
    - Ui cập nhật tự động
- `3. Lifecycle của ViewModel`
  - Khi trang ZUL được load: 
  - 1. ZK tạo Component tree
  - 2. ZK tạo instance của ViewModel
  - Binding engine liên kết View <-> ViewModel
  - 4. Các Annotation như `@Init` `@Command` `@NotifyChange` sẽ điều khiển lifecyle

```
| ZK MVVM                                | Spring                  |
| -------------------------------------- | ----------------------- |
| ViewModel được tạo theo page lifecycle | Controller là singleton |
| Binding tự động                        | Model phải set thủ công |
| Không cần return view name             | Controller return view  |

```

- `4. Tư duy kiến trúc quan trọng`
- Ứng dụng VD này đang dạy ta: 
  - State-drieven UI
  - Data Binding 2 chiều
  - Không thao tác DOM thủ công
  - Không viets Javascript để update UI

## 2. Tạo một Domain Class 

- Đây là một đối tượng domain đại diện cho một chiếc xe: 
```java
public class Car {
    private Integer id;
    private String model;
    private String make;
    private String preview;
    private String description;
    private Integer price;
    //omit getter and setter for brevity
}
```

- Sau đó chúng ta định nghĩa một lớp service để thực hiện logic nghiệp vụ (tìm kiếm xe) như bên dưới

```java
public interface CarService {

    /**
     * Retrieve all cars in the catalog.
     * @return all cars
     */
    public List<Car> findAll();
    
    /**
     * search cars according to keyword in  model and make.
     * @param keyword for search
     * @return list of car that matches the keyword
     */
    public List<Car> search(String keyword);
}
```

- trong VD này, chúng ta đã định nghĩa một lớp `CarServiceImpl`, triển khai, interface ở trên. Để đơn giản, nó sử dụng một đối tượng danh sách tĩnh (static list) làm mô hình dữ liệu. ta có thể viết lại nó kết nối tới CSDL trong một ứng dụng thực tế.
- Chi tiết triển khai ko quan trọng ta nên copy

## Xây dựng User Interface

- UI là một điểm khởi đầu tốt để xây dựng một ứng dụng vì nó giúp ta xác định pham vi của ứn dụng. ZK cung cấp hằng trăm thành phần UI có sẵn. Vì vậy lập trình viên có thể nhanh chóng xây dựng giao diện người dùng mong muốn bằng cách kết hợp và phối  hợp các thành phần này mà không cần phải tạo một trang từ đầu
- Trong ZK, ta có thể sử dụng ZK User Interface Markup Language (ZUML), một ngôn ngữ định dạng XML, để mô tả UI. Theo quy ước của ZK, các file mô tả giao diện người dùng bằng ZUML sử dụng hậu tố tên là `.zul`. Trong các file Zul, một Component được biểu diễn như một phần tử XML (tagg) và ta có thể cấu hình style hành vi và chức năng của mỗi component bằng cách thiết lập các thuộc tính (attriburte) của phần tử XML (Xem ZK Componetn Reference để biết chi tiết)
- Trong VD này, trước hết chúng ta muốn sử dụng một window với tiêu đề được chỉ định và đường viền bình thường làm khung cho ứng dụng của chúng ta.
- Vì window là COmponent ngoài cùng, nó được gọi là rootComponent. Window là một container được sử dụng phổ biến vì nó làm cho ứng dụng web của chúng ta trông như một ứng dụng desktop. Ngoài ra nó cũng có thể bao bọc các Component khác. Tất cả các Component nằm bên trong windơ được gọi là child component của nó và nên được đặt trong phần body của nó

- trong `search.zul`

```xml
    <window title="Search" border="normal" width="600px">
        <!-- put child components inside a tag's body -->
    </window>
```

- Dòng 1 chỉ định nội dung thanh tiêu đề abwngf `title` và hiển thị đường viền bình thường với `border`. Đối với thuộc tính `width` sử dụng cú pháp giống CSS như `800px` hoặc `60%`
- Giao diện người dùng của ứng dụng ví dụ của chúng ta được chia thành 3 khu vực bên trong 
- search function
- car list
- car details.

- **`Giải thích thêm`**
- `2. Windoww là rootComponent - Ý nghĩa kiến trúc`

```xml
<window title="Search" border="normal" width="600px">
```

- Window đóng vai trò: 
  - container
  - Root component
  - Entry point của Page lifecycle
- Lifecyle khi load `.zul`
  - 1. ZK parse file XML
  - 2. Tạo file COmponent tree ( Service -side)
  - 3. Tạo instance ViewModel (nếu có bind)
  - 4. Link Component <-> ViewModel
  - 5. render ra Browser
- điểm khác Spring MVC: 
  - Không có khái niệm "return view name"
  - `.zul` được xử lý như một Component tree chứ kphair là template text
- `6. So sánh với cách ta từng viết Javascript`
- Nếu không dùng MVVM: 
  - Click -> veiets JS
  - gêtlementById
  - InnerHTML
  - Manual DOM update
- Trong ZK MVVM: 
  - Không đụng vào DOM
  - Không viết JS
  - Chỉ thay đổi state trong Viewmodel
  - binding engine xử lý phần còn lại
### Search Area

- Các Component của ZK giống như các khối xây dựng (build bblocks) , ta có thể kết hơp jvaf phối hợp các Component có sẵn để xây dựng UI mong muốn. Để cho phép người dùng tìm kiếm, chúng ta cần một đoạn văn bản để hướng dẫn người dùng nhập liệu. Một nơi để nhập từ khóa và một nút để kích hoạt việc tìm kiếm, chúng ta có thể sử dụng các componetn ZK sau để đáp ứng điều này: 

- `search.zul`
```xml
Keyword:
<textbox id="keywordBox" />
<button id="searchButton" label="Search" iconSclass="z-icon-search" style="margin: 0 0 5px 5px"/>
```

- Dòng 1-2: chỉ định thuộc tính `id` một số Component cho phép ta điều khiển chúng bằng cách tham chiếu tới `id` của chúng.
- Dòng 3 ta có thể sử dụng icon Font Aweasome tích hợp sẵn tại `iconSclass`. 
- 
### Car List Ảea

- ZK Cung cấp một số Component để hiển thị một tập hợp dữ liệu như `listbox`, `grid` và `tree`. trong VD này, Chusgn ta sử dụng `listbox` để hiển thị dan hsasch xe với 3 cột: Model, Mae, Price. Ở đây chúng ta sử dụng `listcell` với `label` tĩnh để minh họa cấu trúc một `lisitem`. Sau ddos chusng ta sex nois veef casch taoj `listitem` động với một tạp hgopwj dữ liệu
- `Searhc.zul`

```xml
<listbox id="carListbox" emptyMessage="No car found in the result" rows="5">
    <listhead>
        <listheader label="Model" />
        <listheader label="Make" />
        <listheader label="Price" width="20%"/>
    </listhead>
    <listitem>
        <listcell label="car model"></listcell>
        <listcell label="make"></listcell>
        <listcell>$<label value="price" /></listcell>
    </listitem>
</listbox>
```

- Dòng 1: rows: xác định hàng hiển thị tối đa/ `emptyMessage` được dùng để hiển thị một thông báo khi listbox không chứa item nào
- Dòng 2: `listbox` là một Component dạng container, và ta có thể thêm `listhead` để định nghĩa các cột
- Dòng 7: `listitem` được dùng để hiển thị dữ liệu, và số lượng `listcell` trong một listitem thường bằng số lượng `listheader`

### Car Details và Area

- `hlayout` và `vlayout` là các Component layout dùng để sắp xếp các Component con theo thứ tự ngang và dọc

```xml
    <hlayout style="margin-top:20px" width="100%">
        <image id="previewImage" width="250px" />
        <vlayout hflex="1">
            <label id="modelLabel" />
            <label id="makeLabel" />
            <label id="priceLabel" />
            <label id="descriptionLabel" />
        </vlayout>
    </hlayout>
```

- dòng 1: thuộc tính style cho phép ta tùy chỉnh style của Component bằng úc pháp CSS

- **`Giải thích thêm`**
- `1. Về id - so sánh giữa Spring MVC và JS`
```xml
<textbox id="keywordBox" />
```

- Trong cách viết kiểu MVC cũ (hoặc khi không dùng MVVM)
- Ta sẽ dùng `id` để" 
  - Wirte Component vào Controller
  - hoặc thao tác bằng Javascript (geetElementById)
- Trong ZK MVVM:
  - `id ` vẫn tồn tại
  - Nhưng thường không cần dùng nếu dùng databinding
- Điểm quan trọng: 
  - `id` dùng khi thao tác component trực tiếp
  - `binding`: dùng khi thao tác state


### Automatic UI Controlling

- Phương pháp chúng ta giới thiệu ở dayuad là điều khiển tương tác người dùng là để ZK điều khiển các UI component thay chúng ta. phương pháp này được phân loại theo pattern MVVM. 
- Mẫu này chia ứng dụng làm 3 phần
- `Model`: bao gồm dữ liệu ứng dụng và các quy tắc nghiệp vụ. `CarService` và các lớp khác được nó sử dụng địa diện cho phần này trong ứng dụng ví dụ của chúng ta
- `View`: nghĩa là giao diện người dùng. Trang zul chứa các component ZK đại diện cho phần này. Tương tác của người dùng với các Component sẽ kích hoạt các sự kiện được gửi tới Controller
- `ViewModel`: Chịu trách nhiệm cung cấp (expose) dữ liệu từ Model cho View và cung cấp các hành động cần thiết theo yêu cầu từ View. ViewModel là một dạng trừu tượng của View, chứa trạng thái (state) và hành vi (behavior) của View. Nhưng View Model không nên chứa bất kì tham chiếu nào tới các UI Component. ZK Framework xử lý việc giao tiếp và đồng bộ trạng thái giữa View và ViewModel
- Theo cách tiếp cận này, Chúng ta chỉ cần chuẩn bị một lớp ViewModel với các setter getter và các phương thức logic ứng dụng phù hợp. sau đó gán các biểu thức data-binding vào các thuộc tính của Component rtong ZUL. Trong ZK có một binder sẽ đồng bộ dữ liệu giữa ViewModel và Component đồng thời xử lý các sự kiện theo các biểu thức binding. Chúng ta không cần tự mình điều khiển component

- **`Giải thích hình ảnh MVVM trong tài liệu`**
- Hình minh họa thể hiển luồng xử lý gồm 4 bước chính giữa `View -> Binder -> ViewMOdel -> Model -> binder -> view`
- `1. SEnd an Email (View -> binder)`
- bên trái là View (UI)
  - Người dùng click nút search
  - Sự kiện (event) được gửi lên server
  - không gọi trực tiếp ViewMOdel
- `2. Invoke command method (binder -> ViewModel)`
- Binder đóng vai trò trung gian.
  - Binder chỉ đọc bindg expression trong ZUL
  - tìm `@Command`
  - Gọi method tương ứng trong ViewModel
```java
@Command
public void search()
```
- Binder giống như: 
  - DispatcherServelet trong Spring
  - Nhưng chuyển xử lý binding thay vì mapping URL

- `3. Access DatA (view Model -> model)`
- Trong hình: 
  - View Model (khôi giữa có bánh răng)
  - Model (database cylinder bên phải)
- Luồng: 

```java
carService.search(keyword);
```

- ViewMOdel không truy cập UI
- ViewModel chỉ thao tác dữ liệu
- Đây là điểm rất quan trọng

```
ViewMOdel không biết gì về ComponentUI
```

- `5. Reload State` (Binder -> View)
- sau khi ViewModel cập nhật state: 

```java
private List<Car> carList;
```

- binder sẽ: 
  - So sánh property thay đổi
  - Reload UI Component tương ứng
  - Update Listbox
- không cần: 
  - gêtLementById
  - setModel thủ công
  - AJAX manual call 
- Binder sex tuwj dodongf booj

- `Thay đổi state -> UI tự thay đổi`

- Ở đây chúng ta sử dụng chức năng Search để giải thích cách MVVM hoạt động trong ZK. Giả sử người dùng nhấn nút "search" sau đó listbox cập nhật nội dung của nó. Luồng xử lý như sau: 
  - 1. Người dùng bấm nút search và một event tương ứng được gửi đi
  - 2. Binder của ZK gọi phương thức command tương ứng trong ViewModel
  - 3. phương thức đó tr truy cập dữ liệu từ model và cập nhật một số thuộc tính của ViewMOdel
  - Bindiner của ZK tải lại (reload) các thuộc tính thay đổi từ ViewMOdel để cập nhật trạng thái của Component

### Atracting the View

- ViewModel là một sự trừu tượng hóa (abstraction) của View. Do đó khi chúng ta thiết kế một ViewModel, chúng ta nên phân tích các chức năng của UI để xác định nó chứa những state (state) nào và những behavior nào
- Trạng thái (State) :
  - Keword của người dùng
  - car list là kết quả tìm kiếm
  - selected car
- Hành vi: 
  - search
- Theo phân tích ở trên, ViewModel nên có 3 biến cho các trạng thái trên và một phương thức hành vi . Tỏng ZK, việc tạo một view Model giống như tọa một POJO, và nó expose các trạng thái của mình giống như các thuộc tính (property) của JavaBean thông qua các phương thức setter và getter. Phương thức search triển khai logic tìm kiếm bằng class service và cập nhật thuộc tính carList

- `SearhcViewModel.java`

```java
package tutorial;

import java.util.List;
import org.zkoss.bind.annotation.*;

public class SearchViewModel {

    private String keyword;
    private List<Car> carList;
    private Car selectedCar;
    
    //omit getter and setter

    public void search(){
        carList = carService.search(keyword);
    }
}
```

### Annotation: 

- Trong ZK MVVM, bất kì hành vi nào mà View có thẻ yêu cầu đều là một command trong ViewModel. Chúng ta có thể bind sự kiện của một Component tới Command này và ZK sẽ gọi phương thức khi khi sự kiện được bind được kích hoạt. Để ZK biết hành vi (method) nào được yêu cầu. Ta nên áp dụng `@Command` lên method. Chúng ta đánh dấu `search()` là một command. Với tên command mặc định là `search`. giống với tên phương thức. Tên command được sử dụng trong biểu thức data-binding mà chúng ta nói đến ở phần tiếp theo

- Trong `search()` chúng ta thay đổi một property của ViewModel `carList`. Do đó chúng ta nên thống báo cho ZK sự thay đổi này bằng `@NotifyChange` Để ZK có thể reload property đã thay đổi cho chúng ta sau khi nó gọi phương thức này.

```java
package tutorial;

import java.util.List;
import org.zkoss.bind.annotation.*;

public class SearchViewModel {

    //omit other codes

    @Command
    @NotifyChange("carList")
    public void search(){
        carList = carService.search(keyword);
    }
}
```

- **`Phân tích tư duy thiết kế ViewModel`**
- `1. Phân tích`
- B1: phân tích UI
- B2: xác đinh jState
- B3: xác định behavior
- UI có : 
  - input -> keyword
  - List hiển thị -> carList
  - selectedItem -> selectedCar
  - button -> search()
- Đây là nguyên lý: 
  - ViewModel = state + behiavor của VIew
- `3. @Command - tương đương gì trong Spring`
- Có thể hiểu như: 
  - @RequestMapping
  - @EventListener
- Nó Nó: 
  - Mapping qua binding expression trong ZUL
- Binder  sẽ: 
  - 1. nhận event từ UI
  - 2. Tìm command name
  - 3. invoke method tương ứng
- `4. @NotifyChange - cơ chế reactive`
-
```java
@NotifyChange("carList")
```

- Nó nói với binder: 
  - Sau khi method chạy xong, property "carList" đã thay đổi
- Binder sẽ: 
  - đánh dấu dirty property
  - Reload UI component bind với carlist
- Nếu không có `@NotifyChange`
  - UI sẽ không update
  - Vì binder không biết property đã thay đổi
- ZK cần explicit notify
- `5. Lifcycle khi click search`

```xml
onClick="@command('search')"
```

- Flow thực tế: 
  - 1. Browser gửi event
  - 2. ZK restore component tree
  - 3. Binder gọi search()
  - 4. search() gọi carService.search()
  - 5. carList thay đổi
  - 6. @NotifyChange đánh dấu carList
  - 7. binder reload listbox
  - 8. render diff gửi về client
- Khoogn có: 
  - HTTP request
  - JSON response
  - manual DOM update
- `6. Quan trọng`
  - ViewMOdel sống theo lifcyle của page
  - Mỗi tab có một state riêng
  - đây là stateful server model
- Điều này ảnh hưởng: 
  - memory usage
  - Session handling
  - Scalability

### Binding UI đến ViewModel

- THeo mô hình MVVM, chúng ta xây dựng UI giống như khi xây dựng cách tiếp cận MVC, sau đó chúng ta chỉ định mối quan hệ giữa một ZUL và một ViewMOdel bằng cách viết biểu thức data binding trong thuộc tính componetn và để ZK xử lý các Component thay cho chúng ta

### Bind a ViewModel

- Để bind một component với một ViewMOdel, chúng ta cần áp dụng một composer tên là `org.zkoss.bind.BindComposer`. Composer này xử lý các biểu thức data binding và khởi tạo lớp ViewModel. Sau đó chúng ta bind Component này với một ViewModel bằng cách thiết lập thuộc tính `viewMOdel` theo cú pháp sau: 
```
@id('ID') @init('FULL.QUALIFIED.CLASSNAME')
```

- `@id` dùng để đặt id cho ViewModel theo ý muốn, giống như một tên biến. Chúng ta sẽ dùng id này để tham chiếu đến thuộc tính của ViewModel `VD: vm.carList` trong biểu thức data binding
- Chúng ta cần cung cấp tên lớp đầy đủ (full qulified class name) cho `@Init()` để khởi tạo đối tượng viewModel
- `searchMvvm.zul`

```xml
    <window title="Search" width="600px" border="normal" 
            viewModel="@id('vm') @init('tutorial.SearchViewModel')">
    ...
    </window>
```

- Sau khi bind ViewModel với Component, tất cả các Component con của nó có thể truy cập cùng một VIewModel và các thuộc tính của nó.
- Chúng ta có thể bind View tới cả thuộc tính và hành vi của ViewModel thông qua biểu thức databINding. 

### Load Data from a VieWModel

- Vì chúng ta đã khai báo các biến trong các  class ViewModel cho trạng thái của Component ở phần trước. chusgn ta có thể bind các thuộc tính của Cmponent tới chúng. Sau khi bind thuộc tính của Component với ViewModel, Zk sẽ tự động đồng bộ dữ liệu giữa giá trị của thuộc tính và Property của ViewModel
- Chúng ta có thể chỉ dịnh thuộc tính nào được load từ property nào bằng cách viết biểu thức data binding làm giá trị thuộc tính component theo cú pháp

```
@load(vm.aProperty)
```

- Hãy nhớ rằng `vm` là id mà chúng ta đã đặt trong `@id()` trước đó và bây giờ chúng ta dùng nó để tham chiếu đến đối tượng ViewModel

### Save data vào một ViewModel

- Có 2 trạng thái liên quan đến chức năng search trong ViewModel theo phân tích trước đó.
- Thứ 1. chúng ta muốn lưu giá trị của textBox vào `keyword` trong ViewModel. Chúng ta có thể bind thuộc tính `value` của textbox tới `vm.keyword` bằng `@save(vm.keyword)`. như vậy ZK sẽ lưu dữ liêu jnguowif dùng nhập vào ViewMOdel tại thời điểm thích hợp
- Thứ 2. Chúng ta muốn gán dữ liệu cho ListBox bằng `carList` trong ViewMOdel, Do đó chúng ta nên bind thuộc tính `model` của nó tới `vm.carList`

- `searchMvvm.zul`
```xml
        <hbox>
            Keyword:
            <textbox value="@save(vm.keyword)" />
            <button label="Search" image="/img/search.png"/>
        </hbox>
        <listbox height="160px" model="@load(vm.carList)" emptyMessage="No car found in the result">
        <!-- omit other tags -->
```

### Gọi 1 method của VieWModel

- Chúng ta có thẻ bind thuộc tính sự kiện của Component (VD onClick) tới hành vi của ViewModel. Sau khi bind một sự kiện tới ViewModel, mỗi khi người dùng kích hoạt sự kiện đó. ZK sẽ tìm phương thức command đã bind và gọi nó. để xử lý việc nhấn nút "search", chúng ta cần bind thuộc tính `onClick` của button với một command method theo cú pháp
```
@command('COMMAND_NAME')
```

- Chúng ta nên tìm tên command đã được chỉ định trong phương thức của ViewModel
```xml
        <hbox>
            Keyword:
            <textbox value="@save(vm.keyword)" />
            <button label="Search" image="/img/search.png" onClick="@command('search')" />
        </hbox>
        <listbox height="160px" model="@load(vm.carList)" emptyMessage="No car found in the result">
        <!-- omit other tags -->
```

- Sua khi bind sự kiện "onClick" này, người ùng nhấn nút "search", ZJK sẽ gọi `search()` và reload thuộc tính "carList" đã được chỉ định trong `@notifyChange`
### Hiển thị Data Collection

- cách hiển thị một tập dữ liệu với data binding rất giống với cách trong mô hình MVC.
- Chúng ta sẽ sử dụng một thẻ đặc biệt `<template>` để kiểm soát render từng item
- Điểm khác biệt duy nhất là chúng ta nên sử dụng biểu thức datraabindg thay vì EL
- Các bước để sử dụng `<template>`
  - Sử dụng `<template>`
    - Đặt thuộc tính "name" của template thành model
    - Sử dụng biến ngầm định (implicit varaible) `each` để gán các thuộc tính của domain object vào thuộc tính của Component
- `searchMvvm.zul`

```xml
        <listbox height="160px" model="@load(vm.carList)" emptyMessage="No car found in the result">
            <listhead>
                <listheader label="Model" />
                <listheader label="Make" />
                <listheader label="Price" width="20%"/>
            </listhead>
            <template name="model">
                <listitem>
                    <listcell label="@init(each.model)"></listcell>
                    <listcell label="@init(each.make)"></listcell>
                    <listcell>$<label value="@init(each.price)" />
                    </listcell>
                </listitem>
            </template>
        </listbox>
```

### Triển khai Chức năng ViewDetails

- Các bước để triển khai chức năng xem chi tiết tương tự như các phần trước
- Chúng ta bind thuộc tính `selectedItem` của Listbox tới property `vm.selectedCar` để lưu domain object được chọn
- Vì chúng ta muốn hiển thị chi tiết của chiếc xe được chọn, chúng ta bind `value` của label và `src` của Image tới các thuộc tính của chiếc xe dược chọn, có thể truy cập bằng cách nối chuỗi theo cú phpas dấu chấm như `vm.selectedCar.price`
- Mỗi khi người dùng chọn một `listitem`, ZK sẽ lưu chiếc xe được chọn vào ViewModel. Sau đó ZK sẽ reload lại thuộc tính của `selectedCar` tới các thuộc tính đã được bind

```xml
        <listbox height="160px" model="@load(vm.carList)" emptyMessage="No car found in the result"
        selectedItem="@save(vm.selectedCar)">
        <!-- omit child components -->
        </listbox>
        <hbox style="margin-top:20px">
            <image width="250px" src="@load(vm.selectedCar.preview)" />
            <vbox>
                <label value="@load(vm.selectedCar.model)" />
                <label value="@load(vm.selectedCar.make)" />
                <label value="@load(vm.selectedCar.price)" />
                <label value="@load(vm.selectedCar.description)" />
            </vbox>
        </hbox>
```

- **`Giải thích thêm`**
- `<template name="model">` cơ chế render collection

```xml
<template name="model">
```

- Khi ta bind: 

```xml
model="@load(vm.carList)"
```

- ZK sẽ: 
  - 1. lặp qua từng phần tử trong carList
  - 2. Gán từng phần tử vào biến ngầm định `each`
  - 3. Render nôi jdung bên trong `<template>`
- `2. each- biến ngầm định`

```
<label value="@init(each.price)" />
```
- `each` đại diện cho: `Car currentItem`

- `3. @Init (each.model) ý nghĩa`

- Ở đây dùng
```xml
@init(each.model)
```

- `@Init` nghĩa là: 
  - Chỉ load giá trị khi Component được khởi tọa
  - không cần reactive update vì item không thay đổi sau khi tạo
- Khác với: 
```
@load(...)
```

- Vì @Load sẽ theo dõi property thay đổi


- `4. <Template> là gì? có phải child djdawc thù của listbox không`
- Đúng ?
- `<template>` là một child đặc biệt dùng bởi các Component hỗ trợ render collection 
  - `listBox`
  - `grid`
  - `tree`
  - `compobox`
- Nó không phải là Component hiển thị UI
- `5. model="@Load(vm.carList)" là gì`
- Ở đây: 
  - `model` là một `attribute đặc thù` của listbox
  - Nó KHÔNG phải là một biến tự do
  - nó là thuộc tính chính thức của Listbox Component
- Trong Java nội bộ của ZK, ListBox có: 

```java
setModel(ListModel model)
```

- Nghĩa là: 
  - `model` là nơi ta đưa Collection vào
- `3. 3️⃣ <template name="model"> có cố định không?`
- Có nó cố định trong trường hopwhj này
- Vì `model` là attribute của listbox
- Template cần có `name` trùng với attribute đó
- 