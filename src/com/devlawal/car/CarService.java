package com.devlawal.car;

import java.util.ArrayList;
import java.util.List;
import java.util.UUID;

public class CarService {
    private final CarDao carDao;

    public CarService(CarDao carDao) {
        this.carDao = carDao;
    }

    public List<Car> getAllCars() {
        List<Car> cars = carDao.getAllCars();
        return cars == null ? new ArrayList<>() : cars;
    }

    public List<Car> getAllElectricCars() {
        List<Car> allCars = getAllCars();
        return allCars.stream().filter(Car::isElectric).toList();
    }

    public Car getCarById(UUID id) {
        if (id == null) {
            throw new IllegalArgumentException("id cannot be null");
        }
        return carDao.getCarById(id);
    }

    public Car getCarByRegNumber(String regNumber) {
        if (regNumber == null) {
            throw new IllegalArgumentException("Registration Number cannot be null!");
        }
        List<Car> allCars = getAllCars();
        return allCars.stream().filter(car -> car.getRegNumber().equals(regNumber)).findFirst().orElse(null);

    }
}
