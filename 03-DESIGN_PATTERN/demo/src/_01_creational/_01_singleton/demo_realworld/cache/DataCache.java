package _01_creational._01_singleton.demo_realworld.cache;

import java.util.List;

import _01_creational._01_singleton.demo_realworld.model.Data;

public abstract class DataCache extends BaseCache<Data> {

    private final String FILTER_GREATER_KEY_PREFIX = "F_G_K-";

    public void putFindAll(List<Data> listToCache) {
        put(FILTER_GREATER_KEY_PREFIX, listToCache);
    }

    public void evictFindAll() {
        evict(FILTER_GREATER_KEY_PREFIX);

        // Evict All relative to List<Data>...
        // Evict FindAll ..
        // Evict ByKey ...
    }

    public List<Data> getFindAll() {
        return get(FILTER_GREATER_KEY_PREFIX);
    }

    public boolean containsFindAll() {
        return contains(FILTER_GREATER_KEY_PREFIX);
    }
}
