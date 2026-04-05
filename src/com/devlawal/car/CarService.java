package com.devlawal.car;

import com.devlawal.booking.BookingStatus;
import com.devlawal.booking.CarBooking;
import com.devlawal.booking.CarBookingService;

import java.util.UUID;

public class CarService {
    private final CarDao carDao = new CarFileDataAccessService();
    private CarBookingService carBookingService;

    private CarBookingService getCarBookingService() {
        if (carBookingService == null) {
            carBookingService = new CarBookingService();
        }
        return carBookingService;
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

    public Car[] getNotYetBookedElectricCars() {
        Car[] notYetBooked = getCarsThatAreNotYetBooked();
        if (notYetBooked == null || notYetBooked.length == 0) {
            return new Car[0];
        }

        int count = 0;
        for (Car car : notYetBooked) {
            if (car != null && car.isElectric()) {
                count++;
            }
        }
        if (count == 0) {
            return new Car[0];
        }
        int index = 0;
        Car[] result = new Car[count];
        for (Car car : notYetBooked) {
            if (car != null && car.isElectric()) {
                result[index++] = car;
            }
        }
        return result;
    }

    public Car[] getCarsThatAreNotYetBooked() {
        Car[] cars = getAllCars();
        if (cars.length == 0) {
            return new Car[0];
        }

        CarBooking[] activeBookings = getCarBookingService().getAllActiveBookings();
        if (activeBookings == null || activeBookings.length == 0) {
            return cars;
        }

        CarBooking[] allBookings = getCarBookingService().getAllBookings();
        int availableCount = 0;
        for (Car car : cars) {
            if (car == null) {
                continue;
            }
            boolean isBooked = false;
            if (allBookings != null) {
                for (CarBooking booking : allBookings) {
                    if (booking == null || booking.getCar() == null || booking.getCar().getId() == null) continue;
                    if (booking.getCar().getId().equals(car.getId()) && booking.getStatus() == BookingStatus.ACTIVE) {
                        isBooked = true;
                        break;
                    }
                }
            }
            if (!isBooked) {
                availableCount++;
            }
        }

        if (availableCount == 0) {
            return new Car[0];
        }

        Car[] availableCars = new Car[availableCount];
        int idx = 0;
        for (Car car : cars) {
            if (car == null) continue;
            boolean isBooked = false;
            if (allBookings != null) {
                for (CarBooking booking : allBookings) {
                    if (booking == null || booking.getCar() == null || booking.getCar().getId() == null) continue;
                    if (booking.getCar().getId().equals(car.getId()) && booking.getStatus() == BookingStatus.ACTIVE) {
                        isBooked = true;
                        break;
                    }
                }
            }
            if (!isBooked) {
                availableCars[idx++] = car;
            }
        }
        return availableCars;
    }
}
