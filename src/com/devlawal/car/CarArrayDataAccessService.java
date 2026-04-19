package com.devlawal.car;

import java.math.BigDecimal;
import java.util.ArrayList;
import java.util.List;
import java.util.UUID;

public class CarArrayDataAccessService implements CarDao {
    private static final List<Car> cars;

    static {
        cars = new ArrayList<>();
        cars.add(new Car(UUID.fromString("2ea85178-fada-4279-9d5e-eea627049fa2"), "1234", new BigDecimal("100"), Brand.MERCEDES, true));
        cars.add(new Car(UUID.fromString("576590ff-57a1-4df3-8430-79980eb42343"), "5678", new BigDecimal("150"), Brand.TESLA, true));
        cars.add(new Car(UUID.fromString("9d818235-ce3b-40e8-b74a-3674985c6bcd"), "9012", new BigDecimal("90"), Brand.TOYOTA, false));
        cars.add(new Car(UUID.fromString("87cb62d9-d262-4174-b1b2-957f9e2a1f40"), "3456", new BigDecimal("60"), Brand.AUDI, false));
    }

    public  List<Car> getAllCars() {
        return cars;
    }

    public Car getCarById(UUID id) {
        for (Car car : cars) {
            if (car.getId().equals(id)) {
                return car;
            }
        }
        return null;
    }
}
