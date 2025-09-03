package _01_creational._01_singleton.demo_realworld.cache;

public class SingletonCache extends DataCache {

    private static SingletonCache instance;

    public static SingletonCache getInstance() {
        if (instance == null) {
            synchronized (SingletonCache.class) {
                if (instance == null) {
                    instance = new SingletonCache();
                }
            }
        }

        return instance;
    }

}
