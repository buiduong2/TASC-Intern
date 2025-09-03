package _01_creational._01_singleton.demo_realworld.cache;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

public abstract class BaseCache<T> {

    private Map<String, List<T>> map;

    protected BaseCache() {
        this.map = new HashMap<>();
    }

    synchronized protected void put(String key, List<T> value) {
        this.map.put(key, value);
    }

    synchronized protected void evict(String key) {
        this.map.remove(key);
    }

    synchronized protected List<T> get(String key) {
        if (this.map.containsKey(key)) {
            return this.map.get(key);
        }
        return null;
    }

    synchronized protected boolean contains(String key) {
        return this.map.containsKey(key);
    }
}
