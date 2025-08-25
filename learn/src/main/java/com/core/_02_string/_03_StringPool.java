package com.core._02_string;

public class _03_StringPool {
    public static void main(String[] args) {
        // whenCreateStringLiteralUseStringPool();
        // whenUseNewKeyWordNotTouchStringPool();
        // whenUseStringInternSaveToPool();
        // whenUseConcatLiteralMustTouchPool();
        whenUseVariableLiteralMustNotTouch();
    }

    /**
     * Chứng minh khi tạo 1 String với String literal thì nó cố gắng cache và sử
     * dụng các String sẵn có
     */
    public static void whenCreateStringLiteralUseStringPool() {
        String s1 = "Java";
        String s2 = "Java";
        System.out.println(s1 == s2); // true - Cả 2 cùng tham chiều về 1 địa chỉ ô nhớ
    }

    /**
     * Chứng minh khi tạo 1 String với từ khóa new. Nó ko tương tác với String Pool
     */
    public static void whenUseNewKeyWordNotTouchStringPool() {
        String s1 = "Java";
        String s2 = new String("Java");
        System.out.println(s1.equals(s2)); // true - 2 String có nội dung giống nhau
        System.out.println(s1 == s2); // false - Không tham chiếu đến cùng một ô nhớ
    }

    /**
     * Chúng ta có thể lưu một String nằm ngoài vùng StringPool vào trong Pool bằng
     * method `intern()`
     */
    public static void whenUseStringInternSaveToPool() {
        String s2 = new String("Java");
        String s3 = s2.intern(); // Kiểm tra xem có trong pool ko thì lấy. Không thì save xong lấy
        String s4 = "Java";

        System.out.println(s2 == s4); // false // s2 là new ko liên quan đến pool
        System.out.println(s3 == s4); // true - Vậy cả s3 đã lưu trong pool. Và s4 đã lấy ra từ pool
    }

    /**
     * Ta đã biết khi tạo một String literal theo dạng literal nối các chuỗi tường
     * mình. Thì trình biên dịch sẽ tối ưu và nối lại cho chúng ta luôn. khi chuyển
     * sang file jar
     */
    public static void whenUseConcatLiteralMustTouchPool() {
        String s1 = "HelloWorld";
        String s2 = "Hello" + "World"; // Trình biên dịch tối ưu và tạo ra một String s2 = "HelloWorld"; -> pool
        System.out.println(s1 == s2); // true
    }

    /**
     * Khi chúng ta khởi tạo một String mà nó được xác định trong RunTime thì nó sẽ
     * ko tương tác với Pool
     */
    public static void whenUseVariableLiteralMustNotTouch() {
        String s1 = "Hello";
        String part = "He";
        String s2 = part + "llo";
        System.out.println(s1 == s2); // false - Vậy s2 ko nằm trong pool
    }

}
