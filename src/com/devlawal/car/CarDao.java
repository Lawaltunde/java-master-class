package com.devlawal.car;

import java.util.UUID;

public interface CarDao {

    Car[] getAllCars();

    Car getCarById(UUID id);
}
