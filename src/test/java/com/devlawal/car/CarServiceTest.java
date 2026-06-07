package com.devlawal.car;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.math.BigDecimal;
import java.util.List;
import java.util.UUID;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class CarServiceTest {
    @Mock
    private CarDao carDao;
    private CarService underTest;

    @BeforeEach
    void setUp() {
        underTest = new CarService(carDao);
    }

    @Test
    void canGetAllCars() {
        List<Car> cars = List.of(
                new Car(UUID.randomUUID(), "1234", new BigDecimal("100"), Brand.TESLA, true),
                new Car(UUID.randomUUID(), "5678", new BigDecimal("90"), Brand.TOYOTA, false)
        );
        when(carDao.getAllCars()).thenReturn(cars);

        List<Car> actual = underTest.getAllCars();

        assertThat(actual).isEqualTo(cars);
        verify(carDao).getAllCars();
    }

    @Test
    void returnsEmptyListWhenDaoReturnsNull() {
        when(carDao.getAllCars()).thenReturn(null);

        assertThat(underTest.getAllCars()).isEmpty();
    }

    @Test
    void canGetAllElectricCars() {
        Car electric = new Car(UUID.randomUUID(), "1234", new BigDecimal("100"), Brand.TESLA, true);
        Car nonElectric = new Car(UUID.randomUUID(), "5678", new BigDecimal("90"), Brand.TOYOTA, false);
        when(carDao.getAllCars()).thenReturn(List.of(electric, nonElectric));

        List<Car> actual = underTest.getAllElectricCars();

        assertThat(actual).containsExactly(electric);
    }

    @Test
    void returnsEmptyListWhenNoElectricCars() {
        Car nonElectric = new Car(UUID.randomUUID(), "5678", new BigDecimal("90"), Brand.TOYOTA, false);
        when(carDao.getAllCars()).thenReturn(List.of(nonElectric));

        assertThat(underTest.getAllElectricCars()).isEmpty();
    }

    @Test
    void canGetCarById() {
        UUID id = UUID.randomUUID();
        Car car = new Car(id, "1234", new BigDecimal("100"), Brand.TESLA, true);
        when(carDao.getCarById(id)).thenReturn(car);

        Car actual = underTest.getCarById(id);

        assertThat(actual).isEqualTo(car);
        verify(carDao).getCarById(id);
    }

    @Test
    void throwsWhenGetCarByIdWithNullId() {
        assertThatThrownBy(() -> underTest.getCarById(null))
                .isInstanceOf(IllegalArgumentException.class)
                .hasMessageContaining("id cannot be null");
        verifyNoInteractions(carDao);
    }

    @Test
    void returnsNullWhenCarNotFound() {
        UUID id = UUID.randomUUID();
        when(carDao.getCarById(id)).thenReturn(null);

        assertThat(underTest.getCarById(id)).isNull();
    }

    @Test
    void canGetCarByRegNumber() {
        Car car = new Car(UUID.randomUUID(), "1234", new BigDecimal("100"), Brand.TESLA, true);
        when(carDao.getAllCars()).thenReturn(List.of(car));

        Car actual = underTest.getCarByRegNumber("1234");

        assertThat(actual).isEqualTo(car);
    }

    @Test
    void returnsNullWhenRegNumberNotFound() {
        when(carDao.getAllCars()).thenReturn(List.of());

        assertThat(underTest.getCarByRegNumber("UNKNOWN")).isNull();
    }

    @Test
    void throwsWhenGetCarByRegNumberWithNull() {
        assertThatThrownBy(() -> underTest.getCarByRegNumber(null))
                .isInstanceOf(IllegalArgumentException.class)
                .hasMessageContaining("Registration Number cannot be null");
    }
}
