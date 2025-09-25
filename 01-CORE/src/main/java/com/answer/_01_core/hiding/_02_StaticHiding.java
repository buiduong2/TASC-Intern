package com.answer._01_core.hiding;

/**
 * Một số field static và Method static Hiding
 * 
 * - Java không có cách nào gọi được super cho `class intance` giống của
 * Javascript
 * 
 * - Super không tồn tại trong static context
 */
public class _02_StaticHiding {
    static class Parent {
        static String name = "Parent";

        // Static clas vẫn kế thừa như thường
        static String nameInherit = "Parent Inherit";

        static String getName() {
            return name;
        }
    }

    static class Child extends Parent {
        static String name = "Child";

        static String getName() {
            return name;
        }
    }

    public static void main(String[] args) {
        // accessWithClassName();
        accessWithInstance();
    }

    /**
     * Khi sử dụng ClassName.fieldName hoặc ClassName.methodName
     * 
     * - không có gì đặc biệt ở đây. Gọi từ class nào thì ta sử dụng class đo
     * - Không thẻ sử dụng super hoặc this giống Javascript
     */
    public static void accessWithClassName() {
        System.out.println(Child.name); // Child
        System.out.println(Parent.name); // Parent
        System.out.println(Child.nameInherit); // Parent Inherit
    }

    /**
     * Khi sử dụng instance để truy cập các static field hoặc method
     * 
     * - Áp dụng đúng định luật kiểu tham chiếu
     */
    @SuppressWarnings("static-access")
    public static void accessWithInstance() {
        Parent childWithParentType = new Child();
        Child child = new Child();

        System.out.println(childWithParentType.name); // Parent
        System.out.println(child.name); // Child
        System.out.println(((Parent) child).name);// parent
    }

}
