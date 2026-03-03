package com.example;

import java.util.ArrayList;
import java.util.List;

import org.zkoss.bind.annotation.Command;
import org.zkoss.bind.annotation.Init;
import org.zkoss.bind.annotation.NotifyChange;

public class SearchViewModel {

    private String keyword;
    private List<Car> carList;

    private Car selectedCar;

    private CarService carService = new CarServiceImpl();

    @Init
    public void init() {
        this.carList = carService.findAll();
    }

    @Command
    @NotifyChange("carList")
    public void search() {
        carList = new ArrayList<>();
        if (keyword == null || keyword.isBlank()) {
            carList.addAll(carService.search(null));
        } else {
            carList.addAll(carService.search(keyword));
        }
    }

    public String getKeyword() {
        return keyword;
    }

    public void setKeyword(String keyword) {
        this.keyword = keyword;
    }

    public List<Car> getCarList() {
        return carList;
    }

    public void setCarList(List<Car> carList) {
        this.carList = carList;
    }

    public Car getSelectedCar() {
        return selectedCar;
    }

    public void setSelectedCar(Car selectedCar) {
        this.selectedCar = selectedCar;
    }

    public CarService getCarService() {
        return carService;
    }

    public void setCarService(CarService carService) {
        this.carService = carService;
    }

}
