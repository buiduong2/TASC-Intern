## CascadeType 

- `CascadeType`: xác định các hành động trên một thực thể sẽ được áp dụng (thác đổ) cho các thực thể liên quan
    - `CascadeType.PERSIST`: khi lưu (persist) một thực thể, các thực thể liên quan cũng sẽ được lưu 
    - `CascadeType.MERGE`: Khi sát nhập (MErge) một thực thể, các thực thể liên quan cũng sẽ bị sát nhập
    - `CascadeType.REMOVE`: khi xóa mộ thực thể, các thực thể liên quan cũng sẽ bị xóa,. Đây là một hành động manh jvaf cần cẩn trọng
    - `CascadeType.DETACH`: Khi tách một thực thể khỏi persistence COntext (từ trạng thái managed sang detached) các thực thể liên quan cũng bị tách ra 
    - `CascadeType.REFRESH`: khi làm mới refresh. một thực thể từ CSDL, cá thực theerl iên quan cũng sẽ được lầm mới
    - `CascadeType.ALL`: áp dung jtaast cả các kiểu Cascade type bên trên


## Fetch Type

-  `FetchType` xác định cách dữ liệu của các thực thể liên quan được nạp từ CSDL
    - `FecthType.EAGER`: dữ liệu của thực thể lien quan sẽ được nạp ngay lập tức cùng với thực thể chính 
        - `Mặc định cho` `@ManyToOne` và `@OneToOne`
        - Lợi ích: Dữ liệu sẵn sangf để sử dụng ngay lập tức, khoogn cần truy vấn bổ xung sau này
        - Hạn chế; có thể dẫn dtoiws vấn đề hiệu năng nếu có nhiều mối quan hệ EAGER gây ra N+ 1 quẻy problem

    - `FetchType.LAZY`: dữ liệu thực thể liên quan chỉ được nạp khi bạn thực sự truy cập vào chugns'
        - Mặc định cho: `@OneToMany` và `@ManyToMany`
        - Lợi ích: tối ưu hiệu năng, giảm số lượng truy vấn ban đầu
        - Hạn chế : CÓ thể gây ra `LazyInitlizationException`nếu chúng ta cố gắng truy cập dữ liệu sau khi session đã đóng 