package com.core._01_data_type;

/**
 * Demo các kiểu dữ liệu nguyên thủy trong Java
 */
public class _02_InitialPrimitive {

    public static void main(String[] args) {
        happyCase();
        overflowCase();
        defaultValue();
    }

    private static void happyCase() {
        // Number
        byte b = 1; // 8 Bit //BETWEEN -2^7 AND (2^7)-1;
        short s = 16; // 16 Bit BETWEEN (-2^15) AND (2^15 -1)
        int i = 32; // 32 Bit BETWEEN (-2^32) AND (2^32 -1)
        long l = 64L;// 64 Bit BETWEEN (-2^64) AND (2^64 -1)

        // NUmber nhưng có đấu phảy động
        float f = 32.32f;
        double d = 64.64d;

        // Boolean
        boolean bool = false; // true OR false
        char c = 'a';

        System.out.println(b); // 1
        System.out.println(s); // 16
        System.out.println(i); // 32
        System.out.println(l); // 64
        System.out.println(f); // 32.32
        System.out.println(bool); // false
        System.out.println(d);
        System.out.println(c); // a
    }

    /**
     * Kiểm tra các trường hợp khi mà số bị tràn quá khoảng kiểu dữ liệu có thể chấp
     * nhận
     * 
     * - NX: Nó sẽ bị sai các phép toán đi. -> Tùy vòa từng thì ta có thể
     * 
     */
    private static void overflowCase() {
        // BYTE (-128 → 127)
        byte byteMax = 127;
        byte byteMin = -128;
        System.out.println("byte overflow: " + (byte) (byteMax + 100)); // -29
        System.out.println("byte underflow: " + (byte) (byteMin - 100)); // 28

        // SHORT (-32768 → 32767)
        short shortMax = 32767;
        short shortMin = -32768;
        System.out.println("short overflow: " + (short) (shortMax + 1)); // -32768
        System.out.println("short underflow: " + (short) (shortMin - 1)); // 32767

        // INT (-2^31 → 2^31-1)
        int intMax = Integer.MAX_VALUE;
        int intMin = Integer.MIN_VALUE;
        System.out.println("int overflow: " + (intMax + 1)); // -2147483648
        System.out.println("int underflow: " + (intMin - 1)); // 2147483647

        // LONG (-2^63 → 2^63-1)
        long longMax = Long.MAX_VALUE;
        long longMin = Long.MIN_VALUE;
        System.out.println("long overflow: " + (longMax + 1)); // -9223372036854775808
        System.out.println("long underflow: " + (longMin - 1)); // 9223372036854775807

        // FLOAT (±3.4028235e38)
        float floatMax = Float.MAX_VALUE;
        float floatMin = -Float.MAX_VALUE;
        System.out.println("float overflow: " + (floatMax * 2)); // Infinity
        System.out.println("float underflow: " + (floatMin * 2)); // -Infinity

        // DOUBLE (±1.7976931348623157e308)
        double doubleMax = Double.MAX_VALUE;
        double doubleMin = -Double.MAX_VALUE;
        System.out.println("double overflow: " + (doubleMax * 2)); // Infinity
        System.out.println("double underflow: " + (doubleMin * 2)); // -Infinity
    }

    public static class DefaultValue {
        byte b;
        short s;
        int i;
        long l;

        // NUmber nhưng có đấu phảy động
        float f;
        double d;

        // Boolean
        boolean bool;
        char c;

    }

    /**
     * Các giá trị khi khai báo ở block của class sẽ tự động được nhận các giá trị
     * khởi tạo
     */
    public static void defaultValue() {
        DefaultValue defaultValue = new DefaultValue();
        System.out.println(defaultValue.b);// 0
        System.out.println(defaultValue.s);// 0
        System.out.println(defaultValue.i);// 0
        System.out.println(defaultValue.l);// 0
        System.out.println(defaultValue.f);// 0.0
        System.out.println(defaultValue.d);// 0.0
        System.out.println(defaultValue.bool);// false

        System.out.println(defaultValue.c); // \u0000 (Kí tự Unicode 0 - ko hiển thị)
    }

}
