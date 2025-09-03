package _01_creational._01_singleton.demo_realworld;

import java.util.List;

import _01_creational._01_singleton.demo_realworld.cache.DataCache;
import _01_creational._01_singleton.demo_realworld.cache.NonSingletonCache;
import _01_creational._01_singleton.demo_realworld.cache.SingletonCache;
import _01_creational._01_singleton.demo_realworld.model.Data;
import _01_creational._01_singleton.demo_realworld.service.DataRepository;
import _01_creational._01_singleton.demo_realworld.service.DataService;

/**
 * Giả sử chúng ta ko dùng Singleton DP
 * 
 * - Khi đó đôi khi lỗi ở người dùng trong đa luồng có thể hó ẽ tạo ra nhiều
 * instance của Cache và gán vào Service khiến cho dữ liệu bị sai
 * (Hoặc là dùng Cache ở nhiều Service khác nhau)
 */
public class Main {
    public static void main(String[] args) {
        // nonSingletonCacheDemo();
        singletonCache();
    }

    /*
     * Singleton Cache : Dữ liệu được đồng bộ
     */
    public static void singletonCache() {
        DataCache cache = SingletonCache.getInstance();
        DataCache duplicateCache = SingletonCache.getInstance();

        getNewDatas2(cache, duplicateCache);

        // 1. Tiến hành đọc và ghi vào Cache
        // Thêm d? li?u m?i vào Cache
        // Tr? v? d? li?u Cache -> Dữ liệu ko bị ghi 2 lần

        // 2. Xóa một Data
        // Data [k=3, v=3] -> Data bị xóa
        // Làm m?i d? li?u Cache -> Cache đã được làm mới
        // Thêm d? li?u m?i vào Cache -> Lần gọi tiếp theo sẽ thêm mới
        // Tr? v? d? li?u Cache -> lần gọi ở Service khác cũng sẽ lấy dữ liệu được Cache
        
        // Service1 Datas: [Data [k=1, v=1], Data [k=2, v=2]]
        // Service2 datas: [Data [k=1, v=1], Data [k=2, v=2]]
    }

    /**
     * Khi thực hiện nonsingleton đôi khi client có thể nhầm lẫn và tạo hơn nhiều
     * Instance của Cache khiến cho dữ liệu không được đồng bộ
     */
    public static void nonSingletonCacheDemo() {
        DataCache cache = new NonSingletonCache();
        DataCache duplicateCache = new NonSingletonCache();

        getNewDatas2(cache, duplicateCache);

        // 1. Tiến hành đọc và ghi vào Cache
        // Khi đọc ban đàu sẽ không có lỗi gì cả
        // Thêm d? li?u m?i vào Cache
        // Thêm d? li?u m?i vào Cache -> Lưu Cache 2 lần

        // 2. Xóa một dữ liệu
        // Data [k=3, v=3] -> Dữ liệu bị xóa
        // Thêm d? li?u m?i vào Cache
        // Tr? v? d? li?u Cache - Cache bị lấy dữ liệu cũ
        // Service1 Datas: [Data [Data [k=1, v=1], Data [k=2, v=2]]
        // -> dữ liệu được làm mới -> Và xóa thật sự
        // Service2 datas:
        // [Data [k=1, v=1], Data [k=2, v=2], Data [k=3, v=3]] -> Dữ liệu cũ
    }

    private static void getNewDatas2(DataCache cache, DataCache duplicateCache) {
        DataRepository repository = new DataRepository();
        DataService service = new DataService(cache, repository);
        DataService service2 = new DataService(duplicateCache, repository);

        service.findAll();

        service2.findAll();

        Long queryKey = 3L;

        service.deleteData(queryKey);

        List<Data> newDatas = service.findAll();

        List<Data> newDatas2 = service2.findAll();

        System.out.println("Service1 Datas: " + newDatas);

        System.out.println("Service2 datas: " + newDatas2);

    }

}
