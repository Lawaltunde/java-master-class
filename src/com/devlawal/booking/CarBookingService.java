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
    private static CarBookingDao carBookingDao;
    private static final UserService userService;
    private static final CarService carService;

    static {
        carBookingDao = new CarBookingDao();
        userService = new UserService();
        carService = new CarService();
    }

    public CarBooking bookCar(UUID userId, UUID carId, LocalDate startDate, LocalDate endDate) {
        if (userId == null) {
            throw new IllegalArgumentException("userId can't be null");
        }
        User user = userService.getUserById(userId);
        if (carId == null) {
            throw new IllegalArgumentException("carId can't be null");
        }
        Car car = carService.getCarById(carId);
        if (startDate.isBefore(LocalDate.now())) {
            throw new IllegalArgumentException("startDate can't be before now");
        }
        if (endDate.isBefore(startDate)) {
            throw new IllegalArgumentException("endDate can't be before startDate");
        }
        for (CarBooking booking : carBookingDao.getAllBookings()) {
            if (booking.getStatus().equals(BookingStatus.ACTIVE) && booking.getCar().getId().equals(car.getId())) {
                throw new IllegalArgumentException("Car already booked!");
            }
        }
        long numberOfDays = ChronoUnit.DAYS.between(startDate, endDate);
        BigDecimal price = car.getRentalPrice().multiply(BigDecimal.valueOf(numberOfDays));
        CarBooking newBooking = new CarBooking(UUID.randomUUID(), user, car, startDate, endDate, price, BookingStatus.ACTIVE, LocalDateTime.now());
        carBookingDao.addBooking(newBooking);
        return newBooking;
    }

    public CarBooking[] getAllBookings() {
        int bookingCount = -1;
        for (CarBooking booking : carBookingDao.getAllBookings()) {
            if (booking.getStatus().equals(BookingStatus.ACTIVE)) {
                bookingCount++;
            }
        }
        int index = 0;
        if (bookingCount > -1) {
            CarBooking[] carBookings = new CarBooking[bookingCount + 1];
            for (CarBooking booking : carBookingDao.getAllBookings()) {
                if (booking.getStatus().equals(BookingStatus.ACTIVE)) {
                    carBookings[index++] = booking;
                }
            }
            return carBookings;
        }
        return new CarBooking[0];
    }

    public CarBooking getCarBookingById(UUID id) {
        if (id == null) {
            throw new IllegalArgumentException("Enter valid car ID!");
        }
        for (CarBooking booking : carBookingDao.getAllBookings()) {
            if (booking.getId().equals(id)) {
                return booking;
            }
        }
        throw new IllegalStateException("Car ID does not exist!");
    }

    public void deleteCarBooking(UUID id) {
        if (id == null) {
            throw new IllegalArgumentException("Enter valid car ID!");
        }
        CarBooking booking = getCarBookingById(id);
        if (booking.getStatus().equals(BookingStatus.ACTIVE)) {
            booking.setStatus(BookingStatus.CANCELLED);
            System.out.println("Booking has been cancelled!");
        } else {
            System.out.println("No worries the current status of the booking with id " + id + " is " + booking.getStatus());
        }
    }

    public Car[] getAllCarsBookedByUser(UUID userId) {
        if (userId == null) {
            throw new IllegalArgumentException("Enter valid user ID!");
        }
        int bookingCount = -1;
        for (CarBooking booking : carBookingDao.getAllBookings()) {
            if (booking.getUser().getId().equals(userId)) {
                bookingCount++;
            }
        }
        if (bookingCount < 0) {
            System.out.println("User has no bookings!");
            return new Car[0];
        }
        int index = 0;
        Car[] userBookedCars = new Car[bookingCount + 1];
        for (CarBooking booking : carBookingDao.getAllBookings()) {
            if (booking.getUser().getId().equals(userId)) {
                userBookedCars[index++] = booking.getCar();
            }
        }
        return userBookedCars;
    }
}
