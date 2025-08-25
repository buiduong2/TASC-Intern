package com.core._01_data_type;

import java.util.Arrays;

public class _02_Object {
    public static void main(String[] args) {
        // initialAndAsign();
        // equalInObject();
        // defaultValueOfObjectType();
        // referenceChange();
        objectMethod();
    }

    public static void initialAndAsign() {
        String s = "hello";
        String s2 = new String("world");
        System.out.println(s); // hello
        System.out.println(s2); // world

        Person p = new Person("Duong", 10);
        System.out.println(p); // com.learn.core._01_object_data_type.Person@2f92e0f4 // TeenClass@hashcode

        Integer i1 = 1;
        Double d = 2d;
        System.out.println(i1); // 1
        System.out.println(d);// 2.0

        int[] nums = new int[] { 1, 2, 3, 4 };
        System.out.println(nums); // [I@28a418fc

    }

    /**
     * Khi sử dụng toán tử == nó sẽ so sánh các địa chỉ
     * // Sử dụng equal() thì sẽ so sánh các giá trị tùy thuộc vào cách override
     * method equal()
     */
    public static void equalInObject() {
        String s = "hello";
        String s3 = "hello";
        String s2 = new String("hello");
        System.out.println(s == s2); // false .
        System.out.println(s.equals(s2)); // true
        System.out.println(s == s3); // true
        // String khi khai báo dạng literal sẽ sử dụng SringPool
        // Khai báo bằng từ khóa new sẽ tạo ra một đối tượng mới

        // Custom Class
        Person p = new Person("Duong", 10);
        Person p2 = new Person("Duong", 10);

        System.out.println(p == p2); // false
        System.out.println(p.equals(p2)); // true (Trong ngầm sử dụng method equal để so sánh)

        // Number
        Integer i = 1;
        Integer i2 = Integer.valueOf(1);
        int i3 = 1;

        System.out.println(i == i3); // true

        System.out.println(i == i2); // true
        // Các lớp wrapper sẽ đặc biệt có cơ chế autoboxing để biến đổi về cùng kiểu dữ
        // liệu nên ko lo lắng đến việc so sánh tham chiều

    }

    public static class DefaultValue {
        int[] nums;
        String s;
        Integer i;
    }

    /**
     * Khi khai báo biến mà không gán dữ liệu thì các kiểu dữ liệu Object sẽ có giá
     * trị mặ định là Null
     */
    public static void defaultValueOfObjectType() {
        DefaultValue defaultValue = new DefaultValue();
        System.out.println(defaultValue.nums); // null
        System.out.println(defaultValue.s); // null
        System.out.println(defaultValue.i);// null
    }

    /**
     * Demo: Tính tham chiếu của các Object
     * 
     * - Khi chúng ta tạo ra một Object và gán nó vào một biến. Thì biến đó sẽ lưu
     * địa chỉ ô nhớ trong bộ nhớ Heap
     * 
     */
    public static void referenceChange() {
        Person person = new Person("Duong", 120);
        System.out.println(person.getName());// Duong
        System.out.println(person.getAge()); // 120

        Person clone = person;
        System.out.println(clone == person); // true ; Cả clone và person đều cùng tham chiếu về 1 địa chỉ

        clone.setAge(100);
        clone.setName("Duc");
        System.out.println(person.getName());// Duc
        System.out.println(person.getAge()); // 100
        // Khi thay đổi giá trị của CLone thì giá trị của Person cũng thay đổi theo
        // Vậy à biến chỉ lưu giá trị ô địa chỉ bộ nhớ

        // Demo asgin
        String s = "hello";
        String s2 = s;
        s2 = "hello " + "World";

        System.out.println(s); // hello
        System.out.println(s2); // "hello " + "World"
        // Khi ta gán lại các giá trị thì nó không phải là thay đổi bản chất của Object
        // mà chỉ là thay đổi địa chỉ ô nhớ
    }

    /**
     * Các object mặc định cung cấp các method tiện ích để chúng ta sử dụng
     */
    public static void objectMethod() {
        String s = "Hello world";
        System.out.println(s.length());// 11 - Kiểm tra độ dài
        System.out.println(s.toUpperCase()); // HELLO WORLD - Chuyển đổi thành chữ hoa

        Integer i = 1;
        Integer i2 = 100;
        System.out.println(i.compareTo(i2)); // -1 ( nhỏ hơn) - So sánh 2 comparable

        int[] nums = new int[] { 1, 2, 3, 4, 5 };
        int[] nums2 = nums.clone(); // Tạo ra 1 Clone một Object (kế thừa từ Object)

        System.out.println(nums.length); // 5 - Lấy ra độ dài
        System.out.println(nums == nums2); // false - 2 object độc lập
        System.out.println(Arrays.toString(nums2)); // [1, 2, 3, 4, 5]

    }

}
