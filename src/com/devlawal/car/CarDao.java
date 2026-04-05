package com.devlawal.car;

import java.math.BigDecimal;
import java.util.UUID;

public interface CarDao {

    Car[] getAllCars();

    Car getCarById(UUID id);
}
