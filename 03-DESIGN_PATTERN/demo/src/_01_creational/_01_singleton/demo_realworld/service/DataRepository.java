package _01_creational._01_singleton.demo_realworld.service;

import java.util.ArrayList;
import java.util.List;
import java.util.stream.LongStream;

import _01_creational._01_singleton.demo_realworld.model.Data;

public class DataRepository {
    private volatile List<Data> datas;

    public DataRepository() {
        this.datas = new ArrayList<>();
        LongStream.range(1, 4).forEach(num -> {
            datas.add(new Data(num, num));
        });
    }

    public List<Data> getDatas() {
        return datas;
    }

    public void setDatas(List<Data> datas) {
        this.datas = datas;
    }

}
