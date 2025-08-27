package com.core._06_excepcion;

public class _01_ThrowAndThrows {
    public static void main(String[] args) {

        try {
            // Ta Nơi gọi method có throws thì ta phải có nghĩa vụ try-catch nó. Hoặc throws
            // nó lên một method tiếp theo
            useThrowsCheckedException();
        } catch (MyCheckedException e) {
            System.out.println(e.getMessage());
        }

    }

    public static void useThrowException() {
        // Nếu throw một UncheckedExceptioin thì ta ko cần phải khai báo try-catch nó
        // một cách tường mình
        throw new MyUncheckedException();
    }

    public static void useThrowsCheckedException() throws MyCheckedException {
        // Khi khai báo throws ở header method . Ta có thể throw ra CheckedException đó.
        // Và ủy quyền cho nơi gọi method này try-catch
        throw new MyCheckedException();
    }

    public static void useNotUseThrowsCheckedException() {
        // Khi không khai báo throws ở phần header của method. Ta bắt buộc phải
        // try-catch exception
        try {
            throw new MyCheckedException();
        } catch (MyCheckedException e) {
            System.out.println(e.getMessage());
        }
    }
}
