package com.answer._01_core.hiding;

public class _01_InstanceFieldHiding {
    static class Parent {
        public String name = "Parent";

        public String getParentName() {
            return name;
        }

    }

    static class Child extends Parent {
        public String name = "Child";

        // Không phải ghi đè
        public String getSuperName() {
            return super.name;
        }

        public String getThisName() {
            return this.name;
        }

        public String getChildName() {
            return name;
        }

    }

    /**
     * Khi các field không thể bị ghi đè thì chúng tồn tại song song
     * - Cách truy cập phụ thuộc vào kiểu tham chiếu
     * 
     */
    public static void main(String[] args) {
        accessWithVariableType();
        // accessWithCasting();
        // accessWithSuperAndThis();
    }

    /**
     * truy cập bằng kiểu tham chiếu của biến
     * 
     * NX:
     * 
     * - Field: thì kể cả kiểu Object có là Child thì nếu var type
     * là Parent chúng sẽ lấy type của Parent
     * 
     * - Method: Nếu không ghi đè method thì nó sẽ chỉ lấy ra field ở trong block
     * method mà nó được khai báo (nhìn là biết)
     * 
     */
    public static void accessWithVariableType() {
        Parent parent = new Parent();
        Child child = new Child();
        Parent childWithParentVarType = new Child();

        System.out.println(parent.name); // Parent
        System.out.println(child.name); // Child
        System.out.println(childWithParentVarType.name); // Parent

        System.out.println(child.getParentName()); // Parent

    }

    /**
     * Logic cũng sẽ như việc chúng ta đặt tên biến
     */
    public static void accessWithCasting() {
        Child child = new Child();

        System.out.println(child.name); // Child
        System.out.println(((Parent) child).name); // Parent

    }

    /**
     * Khi chúng ta sử dụng khai báo thì nó sẽ hoạt động bất kể kiểu tham chiếu tại
     * nơi sử dụng new là gì . nếu đặt kiểu tham chiếu Parent thì ko còn gì để nói
     * vì ko truy cập được
     * 
     * -
     * - Bởi vì bên trong khai báo class nó sử dụng instance khác để gọi đến các
     * field
     * 
     * 
     */
    public static void accessWithSuperAndThis() {
        Child child = new Child();
        System.out.println(child.getSuperName()); // Parent
        System.out.println(child.getThisName()); // Child
    }

    /**
     * * - Còn field ở trong block class không sử dụng this hay super thì nó sẽ theo
     * logic gần nó nhất
     * 
     * 
     */
    public void accessWithNoThis() {
        Child child = new Child();
        System.out.println(child.getChildName()); // Child -Lấy name trong ngữ cảnh local nhất

    }

}