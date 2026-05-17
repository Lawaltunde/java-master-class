package com.devlawal.booking;

import com.devlawal.car.Car;
import com.devlawal.car.CarService;
import com.devlawal.user.User;
import com.devlawal.user.UserService;

import java.math.BigDecimal;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.temporal.ChronoUnit;
import java.util.ArrayList;
import java.util.List;
import java.util.Objects;
import java.util.UUID;
import java.util.stream.Collectors;

public class CarBookingService {
    private final UserService userService;
    private final CarService carService;
    private final CarBookingDao carBookingDao;

    public CarBookingService(UserService userService, CarService carService, CarBookingDao carBookingDao) {
        this.userService = userService;
        this.carService = carService;
        this.carBookingDao = carBookingDao;
    }

    public List<CarBooking> getAllBookings() {
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
        List<CarBooking> allBookings = getAllBookings();
        boolean carAlreadyBooked = allBookings.stream()
                .anyMatch(booking -> booking != null
                        && booking.getStatus() == BookingStatus.ACTIVE
                        && booking.getCar() != null
                        && Objects.equals(booking.getCar().getId(), car.getId()));
        if (carAlreadyBooked) {
            throw new IllegalArgumentException("Car already booked!");
        }

        long numberOfDays = ChronoUnit.DAYS.between(startDate, endDate);
        BigDecimal price = car.getRentalPrice().multiply(BigDecimal.valueOf(numberOfDays));
        CarBooking newBooking = new CarBooking(UUID.randomUUID(), user, car, startDate, endDate, price, BookingStatus.ACTIVE, LocalDateTime.now());
        carBookingDao.addBooking(newBooking);
        return newBooking;
    }

    public List<CarBooking> getAllActiveBookings() {
        List<CarBooking> allBookings = getAllBookings();
        return allBookings.stream()
                .filter(booking -> booking != null && booking.getStatus() == BookingStatus.ACTIVE)
                .collect(Collectors.toList());
    }

    public CarBooking getCarBookingById(UUID id) {
        if (id == null) {
            throw new IllegalArgumentException("Enter valid car ID!");
        }
        List<CarBooking> allBookings = getAllBookings();
        return allBookings.stream().filter(booking -> booking != null && booking.getId().equals(id)).
                findFirst().orElseThrow(() -> new IllegalStateException("Car ID does not exist!"));
    }

    public boolean deleteCarBooking(UUID id) {
        if (id == null) {
            throw new IllegalArgumentException("Enter valid Booking ID!");
        }
        return carBookingDao.deleteCarBooking(id);
    }

    public List<Car> getCarsThatAreNotYetBooked() {
        List<Car> cars = carService.getAllCars();
        if (cars.isEmpty()) {
            return new ArrayList<>();
        }

        List<CarBooking> allBookings = getAllBookings();
        if (allBookings == null || allBookings.isEmpty()) {
            return cars;
        }

        return cars.stream()
                .filter(car -> car != null && car.getId() != null)
                .filter(car -> allBookings.stream()
                        .filter(booking -> booking != null && booking.getCar() != null && booking.getCar().getId() != null)
                        .noneMatch(booking -> booking.getStatus() == BookingStatus.ACTIVE
                                && Objects.equals(booking.getCar().getId(), car.getId())))
                .collect(Collectors.toList());
    }

    public List<Car> getNotYetBookedElectricCars() {
        List<Car> notYetBooked = getCarsThatAreNotYetBooked();
        if (notYetBooked == null || notYetBooked.isEmpty()) {
            return new ArrayList<>();
        }
        return notYetBooked.stream().filter(car -> car != null && car.isElectric()).collect(Collectors.toList());
    }

    public List<Car> getAllCarsBookedByUser(UUID userId) {
        if (userId == null) {
            throw new IllegalArgumentException("Enter valid user ID!");
        }

        List<CarBooking> allBookings = getAllBookings();
        if (allBookings == null || allBookings.isEmpty()) {
            return new ArrayList<>();
        }

        return allBookings.stream()
                .filter(booking -> booking != null
                        && booking.getUser() != null
                        && Objects.equals(booking.getUser().getId(), userId))
                .map(CarBooking::getCar)
                .filter(Objects::nonNull)
                .collect(Collectors.toList());

    }
}
