package com.devlawal.car;

import java.io.Serializable;
import java.math.BigDecimal;
import java.util.Objects;
import java.util.UUID;

public class Car implements Serializable {
    private UUID id;
    private String regNumber;
    private BigDecimal rentalPrice;
    private Brand brand;
    private boolean isElectric;

    public Car(UUID id, String regNumber, BigDecimal rentalPrice, Brand brand, boolean isElectric) {
        this.id = id;
        this.regNumber = regNumber;
        this.rentalPrice = rentalPrice;
        this.brand = brand;
        this.isElectric = isElectric;
    }

    public UUID getId() {
        return id;
    }

    public void setId(UUID id) {
        this.id = id;
    }

    public String getRegNumber() {
        return regNumber;
    }

    public void setRegNumber(String regNumber) {
        this.regNumber = regNumber;
    }

    public BigDecimal getRentalPrice() {
        return rentalPrice;
    }

    public void setRentalPrice(BigDecimal rentalPrice) {
        this.rentalPrice = rentalPrice;
    }

    public Brand getBrand() {
        return brand;
    }

    public void setBrand(Brand brand) {
        this.brand = brand;
    }

    public boolean isElectric() {
        return isElectric;
    }

    public void setElectric(boolean electric) {
        isElectric = electric;
    }

    @Override
    public String toString() {
        return "Car{" +
                "id=" + id +
                ", regNumber='" + regNumber + '\'' +
                ", rentalPrice=" + rentalPrice +
                ", brand=" + brand +
                ", isElectric=" + isElectric +
                '}';
    }

    @Override
    public boolean equals(Object o) {
        if (o == null || getClass() != o.getClass()) return false;
        Car car = (Car) o;
        return isElectric == car.isElectric && Objects.equals(id, car.id) && Objects.equals(regNumber, car.regNumber) && Objects.equals(rentalPrice, car.rentalPrice) && brand == car.brand;
    }

    @Override
    public int hashCode() {
        return Objects.hash(id, regNumber, rentalPrice, brand, isElectric);
    }
}
