package com.devlawal.car;

import org.junit.jupiter.api.Test;

import java.util.List;
import java.util.UUID;

import static org.assertj.core.api.Assertions.assertThat;

class CarArrayDataAccessServiceTest {
    private final CarArrayDataAccessService underTest = new CarArrayDataAccessService();

    @Test
    void canGetAllCars() {
        assertThat(underTest.getAllCars()).hasSize(4);
    }

    @Test
    void carsDoNotContainNull() {
        assertThat(underTest.getAllCars()).doesNotContainNull();
    }

    @Test
    void carsHaveUniqueIds() {
        List<UUID> ids = underTest.getAllCars().stream().map(Car::getId).toList();
        assertThat(ids).doesNotHaveDuplicates();
    }

    @Test
    void canGetCarById() {
        UUID id = UUID.fromString("2ea85178-fada-4279-9d5e-eea627049fa2");

        Car actual = underTest.getCarById(id);

        assertThat(actual).isNotNull();
        assertThat(actual.getId()).isEqualTo(id);
        assertThat(actual.getRegNumber()).isEqualTo("1234");
        assertThat(actual.getBrand()).isEqualTo(Brand.MERCEDES);
        assertThat(actual.isElectric()).isTrue();
    }

    @Test
    void returnsNullForUnknownCarId() {
        assertThat(underTest.getCarById(UUID.randomUUID())).isNull();
    }

    @Test
    void containsExpectedElectricCarCount() {
        long electricCount = underTest.getAllCars().stream().filter(Car::isElectric).count();
        assertThat(electricCount).isEqualTo(2);
    }

    @Test
    void getAllCarsReturnsCopy() {
        List<Car> first = underTest.getAllCars();
        List<Car> second = underTest.getAllCars();
        assertThat(first).isNotSameAs(second);
    }
}
