## Tìm hiểu các loại quan hệ trong JPA : Many To One, One To Many, Many To Many

## ManyToOne 

- QUan hệ ManyToONe mô tả việc nhiều thực thể của một loại liên kết với thực thể duy nhất của loại khác
- VD nhiều nhận viên `Employee` làm việc trong một phòng ban `Department`
- `Cách thể hiện`: quan hệ này được ánh xạ bằng cách sử dụng annotation. `@ManyToOne` trên trường đại diện cho thực thể one trong thực thể many 
- `Cơ chế lưu trữ`. JPA sẽ tạo ra một `khóa ngoaij (foreign Key)` trong bảng của thực thể many để tham chiếu đến khóa chính (primary key) của thực thể one

- 

```java
@Entity
public class Employee {
    // ... các trường khác
    @ManyToOne
    @JoinColumn(name = "department_id") // Tùy chọn, tạo tên cột khóa ngoại
    private Department department;
}

@Entity
public class Department {
    // ... các trường khác
}
```

### One To Many

- Quan hệ ONeToMany mô việc thực thể của một loại liên kết với thực thể của loại khác.  Đây thường là `mặt đối lập` của quan hệ ManyToOne
- Cách thể hiện: Quan hệ này được ánh xạ bằng annotation `@OneToMany`. THường được đặt trên một `Collection` VD `List` hoặc `Set` của các thực thể many. Để tránh tạo bảng liên kết khoogn cần thiết `@OneToMany` thường được khai báo với thuộc tính `mappedBy`
- Cơ chế lưu trữ: Không tạo thêm cột mới trong thực thể của one. Thay vào đó nó dưa jvafo khóa ngoại đã có sẵn trong thực thể many

```java
@Entity
public class Department {
    // ... các trường khác
    @OneToMany(mappedBy = "department", cascade = CascadeType.ALL) // "department" là tên trường trong lớp Employee
    private List<Employee> employees;
}

@Entity
public class Employee {
    // ...
    @ManyToOne
    private Department department;
}
```

- Thuộc tính `mappedBy` chỉ ra rằng quan hệ này được ánh xạb ởi trường `departmetn` trong class `Employee`

## ManyToMany

- Quan hệ ManyToMany mô tả việc thực thể của một loại có thể liên kết với nhiều thực thể của loại khác
    -  VD nhiều sinh viên `STudent`c ó thể đặt kí nhiều khóa học `Course`
    - Cách thể hiện: Quan hệ này được ánh xạ bằng annotation `@ManyToMany` ở cả 2 phía của quan hệ
    - Cơ chế lưu trữ;Để giải quyết mối quan hệ ManyToMany JPA tự động tao ra một `bảng liên kết trung gian join table` bảng này chỉ chứa 2 cột. mỗi cột là một khóa ngoại trỏ tới khóa chính của 2 bảng ban đầu

```java
@Entity
public class Student {
    // ... các trường khác
    @ManyToMany
    @JoinTable(
        name = "student_course", // Tên bảng liên kết
        joinColumns = @JoinColumn(name = "student_id"), // Khóa ngoại trỏ đến Student
        inverseJoinColumns = @JoinColumn(name = "course_id") // Khóa ngoại trỏ đến Course
    )
    private Set<Course> courses = new HashSet<>();
}

@Entity
public class Course {
    // ... các trường khác
    @ManyToMany(mappedBy = "courses") // "courses" là tên trường trong lớp Student
    private Set<Student> students = new HashSet<>();
}
```
