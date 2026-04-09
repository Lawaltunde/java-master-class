package com.devlawal.car;

import java.util.UUID;

public class CarService {
    private final CarDao carDao;

    public CarService(CarDao carDao) {
        this.carDao = carDao;
    }

    public Car[] getAllCars() {
        Car[] cars = carDao.getAllCars();
        return cars == null ? new Car[0] : cars;
    }

    public Car[] getAllElectricCars() {
        Car[] allCars = getAllCars();
        if (allCars.length == 0) {
            return new Car[0];
        }
        int electricCarCounter = 0;

        for (Car car : allCars) {
            if (car != null && car.isElectric()) {
                electricCarCounter++;
            }
        }

        if (electricCarCounter == 0) {
            return new Car[0];
        }
        int index = 0;
        Car[] electricCars = new Car[electricCarCounter];
        for (Car car : allCars) {
            if (car != null && car.isElectric()) {
                electricCars[index++] = car;
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
