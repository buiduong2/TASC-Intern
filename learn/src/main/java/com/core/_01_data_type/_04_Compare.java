package com.core._01_data_type;

public class _04_Compare {
    public static void main(String[] args) {

        // comparePrimitive();
        compareObject();

    }

    /**
     * Demo so sánh giữa các primitive với nhau
     * 
     * - Nó sẽ so sánh các giá trị.
     * - Có thể sử dụng tất cả các toán tử toán học thông thường
     *
     */
    public static void comparePrimitive() {
        int a = 5;
        int b = 5;
        int c = 100;

        System.out.println(a > b); // false
        System.out.println(a == b); // true
        System.out.println(a > c); // false
    }

    /**
     * Demo so sánh 2 giá trị Wrapper (Object) với nhau
     * 
     * - Vậy Chúng giống như một Object thông thường.
     * - Khi so sánh == sẽ so sánh địa chỉ ô nhớ
     * - Khi sử dụng equals() sẽ so sánh nội dung
     */
    public static void compareWrapperAndWrapper() {
        Integer x = 1000;
        Integer y = 1000;

        System.out.println(x == y); // false, khác object
        System.out.println(x.equals(y)); // true . So sánh cùng giá trị
    }

    /**
     * So sánh giữa Primmitive và Object (wrapper)
     */
    public static void compareWrapperAndPrimitive() {
        int a = 50;
        Integer b = 50;
        System.out.println(a == b);
        System.out.println(b.equals(a));
    }

    /**
     * Demo so sánh giữa 2 Object với nhau sử dụng == hoặc `equals()`
     */
    public static void compareObject() {
        StudentWithOutEqual s1 = new StudentWithOutEqual("Duong");
        StudentWithOutEqual s2 = new StudentWithOutEqual("Duong");
        StudentWithOutEqual s3 = s1;

        System.out.println(s1 == s2); // false // Mặc dù đều chứa Field có giá tị giống nhau
        System.out.println(s1 == s3); // true
        System.out.println(s1.equals(s3)); // Mặc định chưa override equals() sẽ ra kết quả giống ==

        // Overrided equals() // Ta giả sử 2 Student là một nếu chúng cùng tên. Tuổi ko
        // xét
        StudentWithEqual se1 = new StudentWithEqual("Duong", 10);
        StudentWithEqual se2 = new StudentWithEqual("Duong", 100);
        StudentWithEqual se3 = se1;
        System.out.println(se1 == se3); // true - cùng địa chỉ ô nhớ
        System.out.println(se1 == se2); // false - khác địa chỉ ô nhớ

        System.out.println(se1.equals(se2)); // true Vì logic đa chạy vào trong method equals() của chúng ta đã override

        // So sánh 2 Object khác nhau
        // System.out.println(se1 == s1);
        // Eror incompatible Operand - Lỗi ngay từ thời điểm biên dịch
        // - Vì chúng khác class nên ko bao giờ có thể == được

    }

    public static class StudentWithOutEqual {
        private String name;

        public StudentWithOutEqual(String name) {
            this.name = name;
        }

        public String getName() {
            return name;
        }

        public void setName(String name) {
            this.name = name;
        }

    }

    public static class StudentWithEqual {
        private String name;
        private int age;

        public StudentWithEqual(String name, int age) {
            this.name = name;
            this.age = age;
        }

        @Override
        public int hashCode() {
            final int prime = 31;
            int result = 1;
            result = prime * result + ((name == null) ? 0 : name.hashCode());
            result = prime * result + age;
            return result;
        }

        @Override
        public boolean equals(Object obj) {
            if (this == obj)
                return true;
            if (obj == null)
                return false;
            if (getClass() != obj.getClass())
                return false;
            StudentWithEqual other = (StudentWithEqual) obj;
            if (name == null) {
                if (other.name != null)
                    return false;
            } else if (!name.equals(other.name))
                return false;
            // ta nói khác tuổi vẫn bằng nhau
            // if (age != other.age)
            // return false;
            return true;
        }

        public String getName() {
            return name;
        }

        public void setName(String name) {
            this.name = name;
        }

        public int getAge() {
            return age;
        }

        public void setAge(int age) {
            this.age = age;
        }

    }
}
