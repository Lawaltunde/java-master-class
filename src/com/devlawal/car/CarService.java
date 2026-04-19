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
        if (allCars.isEmpty()) {
            return new ArrayList<>();
        }


        List<Car> electricCars = new ArrayList<>();
        for (Car car : allCars) {
            if (car != null && car.isElectric()) {
                electricCars.add(car);
            }
        }
        return electricCars;
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
        for (Car car : getAllCars()) {
            if (car != null && car.getRegNumber() != null && car.getRegNumber().equals(regNumber)) {
                return car;
            }
        }
        return null;
    }
}
