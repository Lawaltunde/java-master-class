package com.devlawal.car;

import java.util.List;
import java.util.UUID;

public interface CarDao {

    List<Car> getAllCars();

    Car getCarById(UUID id);
}
