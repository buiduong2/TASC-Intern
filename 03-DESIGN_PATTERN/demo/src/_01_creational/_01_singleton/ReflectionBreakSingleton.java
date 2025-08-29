package _01_creational._01_singleton;

import java.lang.reflect.Constructor;
import java.lang.reflect.InvocationTargetException;

/**
 * Vậy là chúng ta có thể sử dụng Reflection để lấy ra các đối tương Metadata thay đổi acess modifer và có thể gọi method một cách trực tiếp
 */
public class ReflectionBreakSingleton {
    public static void main(String[] args)
            throws InstantiationException, IllegalAccessException, IllegalArgumentException, InvocationTargetException,
            NoSuchMethodException, SecurityException {
        EagerInitializedSingleton instanceOne = EagerInitializedSingleton.getInstance();
        EagerInitializedSingleton instanceTwo = null;

        // Lấy ra tất cả các constrcutor của một class bao gồm cả private
        Constructor<EagerInitializedSingleton> constructor = EagerInitializedSingleton.class.getDeclaredConstructor();
        constructor.setAccessible(true);
        instanceTwo = (EagerInitializedSingleton) constructor.newInstance(new Object[] {});

        System.out.println("2 instance này có phải là 1 không" + (instanceOne == instanceTwo));
        System.out.println(instanceOne);
        System.out.println(instanceTwo);

        // 2 instance này có ph?i là 1 khôngfalse
        // _01_creational._01_singleton.EagerInitializedSingleton@66048bfd
        // _01_creational._01_singleton.EagerInitializedSingleton@61443d8f

    }
}
