package _01_creational._01_singleton.demo_realworld.service;

import java.util.List;

import _01_creational._01_singleton.demo_realworld.cache.DataCache;
import _01_creational._01_singleton.demo_realworld.model.Data;

public class DataService {

    private volatile DataCache cache;

    private DataRepository repo;

    public DataService(DataCache cache, DataRepository repo) {
        this.cache = cache;
        this.repo = repo;

    }

    synchronized public Data deleteData(Long key) {
        int index = this.repo.getDatas().indexOf(new Data(key, null));
        if (index == -1) {
            return null;
        }

        Data rebundantData = this.repo.getDatas().remove(index);
        System.out.println(rebundantData);

        this.cache.evictFindAll();
        System.out.println("Làm mới dữ liệu Cache");
        return rebundantData;
    }

    synchronized public List<Data> findAll() {
        if (cache.containsFindAll()) {
            System.out.println("Trả về dữ liệu Cache");
            return cache.getFindAll();
        }

        List<Data> results = this.repo.getDatas().stream().toList();
        cache.putFindAll(results);

        System.out.println("Thêm dữ liệu mới vào Cache");
        return results;
    }

}
