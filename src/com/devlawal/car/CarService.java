package com.devlawal.car;

import java.util.UUID;

public class CarService {
    private static final CarDao carDao;

    static {
        carDao = new CarDao();
    }

    public Car[] getAllCars() {
        return carDao.getAllCars();
    }

    public Car[] getAllElectricCars() {
        int electricCarCounter = -1;

        for (Car car : getAllCars()) {
            if (car.isElectric()) {
                electricCarCounter++;
            }
        }

        if (electricCarCounter < 0) {
            System.out.println("No Electric cars found!");
            return null;
        }
        int index = 0;
        Car[] electricCars = new Car[electricCarCounter + 1];
        for (Car car : getAllCars()) {
            if (car.isElectric()) {
                electricCars[index++] = car;
            }
        }
        return electricCars;
    }

    public Car getCarById(UUID id) {
        if (id == null) {
            throw new IllegalArgumentException("id cannot be null");
        }
        for (Car car : getAllCars()) {
            if (car.getId().equals(id)) {
                return car;
            }
        }
        return null;
    }

    public Car getCarByRegNumber(String regNumber) {
        if (regNumber == null) {
            throw new IllegalArgumentException("Registration Number cannot be null!");
        }
        for (Car car : getAllCars()) {
            if (car.getRegNumber().equals(regNumber)) {
                return car;
            }
        }
        return null;
    }
}
