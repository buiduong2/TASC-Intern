package com.answer._01_core.hiding;

public class _03_InnerClassHiding {
    static class OuterClazz {
        public String name = "Outer";

        public String getName() {
            return name;
        }

        /**
         * Khbi khai báo tự do sử dụng những gì có ở block bên ngoài
         */
        class NoNStaticInnerClazz {
            String name = "Inner";

            public String getouterNameWithThis() {
                return OuterClazz.this.name;
            }

            public String getOuterNameWithBlock() {
                return getName();
            }

            public String getInnerName() {
                return name;
            }
        }
    }

    public static void main(String[] args) {
        accessWithOuterThis();
    }

    /**
     * Chỉ có thể truy cập thông qua Outer.this không thể ép kiểu như con nhà người
     * ta
     * 
     * - Nói chung vì 2 class không liên quan đến nhau. Nên ta chỉ có thể nó phụ
     * thuộc vào kiểu dữ liệu ta khai báo khiu chúng ta khai báo class
     * 
     * - Tuân theo -> varaible Type -> block
     */
    public static void accessWithOuterThis() {
        OuterClazz outer = new OuterClazz();
        OuterClazz.NoNStaticInnerClazz inner = outer.new NoNStaticInnerClazz();

        System.out.println(outer.name); // outer
        System.out.println(inner.name); // inner

        System.out.println(inner.getInnerName()); // inner

        // Kiểu tham chiếu ko hề có getName()
        // System.out.println(inner.getName());
        System.out.println(inner.getouterNameWithThis()); // outer
        System.out.println(inner.getOuterNameWithBlock()); // outer
        System.out.println(inner.getouterNameWithThis()); // outer
    }
}
