## Controller là gì

-   Một một COmponent hoặc class mà nó xử lý các request của cient, xử lý chúng (thường với sự giúp đỡ của các models (entity)) và xác định response để trả về, như là dữ liệu hoặc view. Nó hoạt động giữa model (data) và view (presentation layer - lớp trình diện)

## RestController

-   là một annotation đặc biệt trong Spring Framaework được sử dụng cho lâp jtrinhf Restful web sericces. Nó bao gồm các annotaion đánh dấu trên nó là của Controller và ResponseBody . Để dơn giản hóa việc tạo các COntroller ms nó trả dữ liệu trực tiếp về cho người dùng

## Sự khác nhau giữa RestController và Controller

|                                                     Controller                                                     |                                           RestController                                            |
| :----------------------------------------------------------------------------------------------------------------: | :-------------------------------------------------------------------------------------------------: |
|            MỘt Annotation ngữ nghĩa được sử dụng trong SPringMVC để định nghĩa một Controller Component            |       Một Annotation thuận tiện và bản thân nó được đánh dấu với @Controller và @ResponseBody       |
|                           Mục đích sử dụng chính cho các ứng dụng web và trả về một view                           |            được sử dụng để xây dựng một RestFul web service trả về dữ liệu JSON hoặc XML            |
|         yêu cầu @ResponseBody trên các method để tuần tự hóa object để trả về trong body của HTTP Response         | Tư động tuần tự hóa các object return thành đến Body của HTTP response bởi vì bao gồm @ResponseBody |
|          Hỗ trợ giải quyết view, cho phép return một tên veiew mà nó sẽ được xử lý bởi một view Resolver           |        không hỗ trợ xử lý view. Được thiết kế để return dữ liệu trực tiếp về cho người dùng         |
|                           phù hợp cho ứng dụng mà nó return một HTML hoặc một JSP pages                            |             phù hợp cho việc lập trình API nơi mà responsebody là dữ liệu (JSON , XML)              |
|   có thể sản xuất ra bất kì nội dung, bao gồm HTML, nhưng yêu cầu đánh dấu để trả về dữ liệu dạng JSON hoặc XML    |                                mục đích chính là trả vềJSON hoặc XML                                |
| Chúng ta phải thêm thủ công @ResponseBody cho nó để thêm các header liên quan chỉ thị rằng kiểu của dữ liệu trả về |               Tự động xử lý bao gồm các headers chỉ thị kiểu dữ liệu của kiểu trả về                |
