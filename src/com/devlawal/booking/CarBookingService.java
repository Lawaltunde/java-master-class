package com.devlawal.booking;

import com.devlawal.car.Car;
import com.devlawal.car.CarService;
import com.devlawal.user.User;
import com.devlawal.user.UserService;

import java.math.BigDecimal;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.temporal.ChronoUnit;
import java.util.UUID;

public class CarBookingService {
    private final UserService userService;
    private final CarService carService;
    private final CarBookingDao carBookingDao;

    public CarBookingService(UserService userService, CarService carService, CarBookingDao carBookingDao) {
        this.userService = userService;
        this.carService = carService;
        this.carBookingDao = carBookingDao;
    }

    public CarBooking[] getAllBookings() {
        return carBookingDao.getAllBookings();
    }

    public CarBooking bookCar(UUID userId, UUID carId, LocalDate startDate, LocalDate endDate) {
        if (userId == null) {
            throw new IllegalArgumentException("userId can't be null");
        }
        User user = userService.getUserById(userId);
        if (user == null) {
            throw new IllegalArgumentException("User with Id " + userId + " can't be found!");
        }
        if (carId == null) {
            throw new IllegalArgumentException("carId can't be null");
        }
        Car car = carService.getCarById(carId);
        if (car == null) {
            throw new IllegalStateException("Car with Id " + carId + " can't be found!");
        }
        if (startDate.isBefore(LocalDate.now())) {
            throw new IllegalArgumentException("startDate can't be before now!");
        }
        if (endDate.isBefore(startDate)) {
            throw new IllegalArgumentException("endDate can't be before startDate!");
        }
        CarBooking[] allBookings = getAllBookings();
        for (CarBooking booking : allBookings) {
            if (booking != null && booking.getStatus().equals(BookingStatus.ACTIVE) && booking.getCar().getId().equals(car.getId())) {
                throw new IllegalArgumentException("Car already booked!");
            }
        }

        long numberOfDays = ChronoUnit.DAYS.between(startDate, endDate);
        BigDecimal price = car.getRentalPrice().multiply(BigDecimal.valueOf(numberOfDays));
        CarBooking newBooking = new CarBooking(UUID.randomUUID(), user, car, startDate, endDate, price, BookingStatus.ACTIVE, LocalDateTime.now());
        carBookingDao.addBooking(newBooking);
        return newBooking;
    }

    public CarBooking[] getAllActiveBookings() {
        int bookingCount = 0;
        CarBooking[] allBookings = getAllBookings();
        for (CarBooking booking : allBookings) {
            if (booking != null && booking.getStatus().equals(BookingStatus.ACTIVE)) {
                bookingCount++;
            }
        }
        if (bookingCount == 0) {
            return new CarBooking[0];
        }
        int index = 0;
        CarBooking[] carBookings = new CarBooking[bookingCount];
        for (CarBooking booking : allBookings) {
            if (booking != null && booking.getStatus().equals(BookingStatus.ACTIVE)) {
                carBookings[index++] = booking;
            }
        }
        return carBookings;
    }

    public CarBooking getCarBookingById(UUID id) {
        if (id == null) {
            throw new IllegalArgumentException("Enter valid car ID!");
        }
        CarBooking[] allBookings = getAllBookings();
        for (CarBooking booking : allBookings) {
            if (booking != null && booking.getId().equals(id)) {
                return booking;
            }
        }
        throw new IllegalStateException("Car ID does not exist!");
    }

    public boolean deleteCarBooking(UUID id) {
        if (id == null) {
            throw new IllegalArgumentException("Enter valid Booking ID!");
        }
        return carBookingDao.deleteCarBooking(id);
    }

    public Car[] getCarsThatAreNotYetBooked() {
        Car[] cars = carService.getAllCars();
        if (cars.length == 0) {
            return new Car[0];
        }

        CarBooking[] allBookings = getAllBookings();
        if (allBookings == null || allBookings.length == 0) {
            return cars;
        }

        int availableCount = 0;
        for (Car car : cars) {
            if (car == null) {
                continue;
            }
            boolean isBooked = false;
            for (CarBooking booking : allBookings) {
                if (booking == null || booking.getCar() == null || booking.getCar().getId() == null) {
                    continue;
                }
                if (booking.getCar().getId().equals(car.getId()) && booking.getStatus() == BookingStatus.ACTIVE) {
                    isBooked = true;
                    break;
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
            if (car == null) {
                continue;
            }
            boolean isBooked = false;
            for (CarBooking booking : allBookings) {
                if (booking == null || booking.getCar() == null || booking.getCar().getId() == null) {
                    continue;
                }
                if (booking.getCar().getId().equals(car.getId()) && booking.getStatus() == BookingStatus.ACTIVE) {
                    isBooked = true;
                    break;
                }
            }
            if (!isBooked) {
                availableCars[idx++] = car;
            }
        }
        return availableCars;
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

    public Car[] getAllCarsBookedByUser(UUID userId) {
        if (userId == null) {
            throw new IllegalArgumentException("Enter valid user ID!");
        }
        int bookingCount = -1;
        CarBooking[] allBookings = getAllBookings();
        for (CarBooking booking : allBookings) {
            if (booking != null && booking.getUser().getId().equals(userId)) {
                bookingCount++;
            }
        }
        if (bookingCount < 0) {
            System.out.println("User has no bookings!");
            return new Car[0];
        }
        int index = 0;
        Car[] userBookedCars = new Car[bookingCount + 1];
        for (CarBooking booking : allBookings) {
            if (booking != null && booking.getUser().getId().equals(userId)) {
                userBookedCars[index++] = booking.getCar();
            }
        }
        return userBookedCars;
    }
}
