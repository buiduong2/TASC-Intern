package _01_creational._01_singleton;

import java.io.Serializable;

public class SerializedSingleton implements Serializable {
    // Tem nhãn phiên bản class -> khi deserilization lại thành obj sẽ kiểm tra
    // phiên bản trước và sau
    private static final long serialVersionUID = 1L;

    // private
    private SerializedSingleton() {

    }

    private static class SingletonHepler {
        private static final SerializedSingleton instance = new SerializedSingleton();
    }

    public static SerializedSingleton getInstance() {
        return SingletonHepler.instance;
    }
}
