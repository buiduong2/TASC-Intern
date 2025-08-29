package _01_creational._01_singleton.demo;

import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.ObjectInput;
import java.io.ObjectInputStream;
import java.io.ObjectOutput;
import java.io.ObjectOutputStream;

import _01_creational._01_singleton.EnumSingleton;
import _01_creational._01_singleton.SerializedSingleton;

public class _03_SerializationSingleton {

    public static void main(String[] args) {
        serializedSingleton();

    }

    /**
     * SO sánh việc khi làm việc với Deserialization và Serialization với Singleton
     */
    public static void serializedSingleton() {
        SerializedSingleton serializedSingleton = SerializedSingleton.getInstance();
        EnumSingleton enumSingleton = EnumSingleton.INSTANCE;
        SerializedSingleton serializedSingleton2 = null;
        EnumSingleton enumSingleton2 = null;

        try (ObjectOutput out = new ObjectOutputStream(new FileOutputStream("fileOutPutStream.txt"))) {
            // Mỗi Obj ghi một dòng
            // B1: tiến hành ghi cả 2 singleton Instance vào trong một file
            out.writeObject(serializedSingleton);
            out.writeObject(enumSingleton);
        } catch (IOException e) {

            e.printStackTrace();
        }

        try (// B2: tiến hành giải mã chuyển đổi nó thành Obj
                ObjectInput in = new ObjectInputStream(new FileInputStream("fileOutPutStream.txt"))) {

            serializedSingleton2 = (SerializedSingleton) in.readObject();
            enumSingleton2 = (EnumSingleton) in.readObject();
        } catch (IOException | ClassNotFoundException e) {
            e.printStackTrace();
        }

        System.out.println("Singleton Object bình thường có bị phá vỡ khởi tạo mới không: "
                + (serializedSingleton == serializedSingleton2)); // false - Object - Đã bị khởi tạo 1 đối tượng mới
        System.out.println("Singleton Enum có bị phá vỡ khởi tạo mới không: " + (enumSingleton == enumSingleton2));
        // true - Interface vẫn là một instance cũ
    }
}