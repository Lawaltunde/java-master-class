package com.devlawal;

import com.devlawal.booking.CarBooking;
import com.devlawal.booking.CarBookingService;
import com.devlawal.car.Car;
import com.devlawal.car.CarService;
import com.devlawal.user.User;
import com.devlawal.user.UserService;

import java.time.LocalDate;
import java.util.InputMismatchException;
import java.util.Scanner;
import java.util.UUID;

public class Main {
    public static void main(String[] args) {

        UserService userService = new UserService();
        CarService carService = new CarService();
        CarBookingService carBookingService = new CarBookingService();
        carBookingOperation(carBookingService, carService, userService);
    }

    public static void carBookingOperation(CarBookingService carBookingService,
                                           CarService carService,
                                           UserService userService) {
        boolean stopper = true;
        while (stopper) {
            int opt = printOptionsForCarBookingSystem();
            switch (opt) {
                case 1:
                    try {
                        for (User user : userService.getAllUsers()) {
                            System.out.println(user);
                        }
                        System.out.println("Enter User ID: ");
                        Scanner scannerUser = new Scanner(System.in);
                        UUID userId = UUID.fromString(scannerUser.nextLine());

                        for (Car car : carService.getAllCars()) { // 2026-03-16
                            System.out.println(car);
                        }
                        System.out.println("Enter Car ID: ");
                        Scanner scannerCar = new Scanner(System.in);
                        UUID carId = UUID.fromString(scannerCar.nextLine());

                        System.out.println("Enter Start Date: ");
                        Scanner scannerStartDate = new Scanner(System.in);
                        LocalDate startDate = LocalDate.parse(scannerStartDate.nextLine());

                        System.out.println("Enter End Date: ");
                        Scanner scannerEndDate = new Scanner(System.in);
                        LocalDate endDate = LocalDate.parse(scannerEndDate.nextLine());

                        CarBooking newBooking = carBookingService.bookCar(userId, carId, startDate, endDate);
                        System.out.println("Successfully Booked Car, ID: " + newBooking.getId() + "| Start Date: " + newBooking.getStartDate() + "| End Date: " + newBooking.getEndDate() + " | Price: "+newBooking.getPrice());
                    }catch (Exception e) {
                        System.out.println(e.getMessage());
                    }
                    break;
                case 2:
                    CarBooking[] activeBookings = carBookingService.getAllActiveBookings();
                    if (activeBookings == null || activeBookings.length == 0) {
                        System.out.println("There are no bookings to delete!");
                        break;
                    }
                    try {
                        for (CarBooking booking : activeBookings) {
                            System.out.println(booking);
                        }
                        System.out.println("Enter the booking ID you want to cancel: ");
                        Scanner scannerBookingId = new Scanner(System.in);
                        UUID bookingId = UUID.fromString(scannerBookingId.nextLine());
                        carBookingService.deleteCarBooking(bookingId);
                    } catch (Exception e) {
                        System.out.println(e.getMessage());
                    }
                    break;
                case 3:
                    for (User user : userService.getAllUsers()) {
                        System.out.println(user);
                    }
                    System.out.println("Enter User ID: ");
                    Scanner scannerUser = new Scanner(System.in);
                    UUID userId = UUID.fromString(scannerUser.nextLine());
                    for (Car booking : carBookingService.getAllCarsBookedByUser(userId)) {
                        System.out.println(booking);
                    }
                    break;
                case 4:
                    CarBooking[] allActiveBookings = carBookingService.getAllActiveBookings();
                    if (allActiveBookings == null || allActiveBookings.length == 0) {
                        System.out.println("There are no  bookings to display!");
                        break;
                    }
                    for (CarBooking booking : allActiveBookings) {
                        System.out.println(booking);
                    }
                    break;
                case 5:
                    for (Car car : carBookingService.getAllNotYetBookedCars(false)) {
                        System.out.println(car);
                    }
                    break;
                case 6:
                    System.out.println(carBookingService.getAllNotYetBookedCars(true).length);
                    for (Car car : carBookingService.getAllNotYetBookedCars(true)) {
                        System.out.println(car);
                    }
                    break;
                case 7:
                    for (User user : userService.getAllUsers()) {
                        System.out.println(user);
                    }
                    break;
                case 8:
                    System.out.println("Goodbye!");
                    stopper = false;
                    break;
            }
        }
    }

    public static int printOptionsForCarBookingSystem() {
        int opt;
        try {
            System.out.println(
                    """
                    1 - Book Car
                    2 - Delete Booking
                    3 - View All User Booked Cars
                    4 - View All Bookings
                    5 - View Available Cars
                    6 - View Available Electric Cars
                    7 - View All Users
                    8 - Exit
                    """
            );
            Scanner scanner = new Scanner(System.in);
            opt = scanner.nextInt();
            return  (opt < 1 || opt > 7) ? 8 : opt;
        }catch(Exception e){
            throw new InputMismatchException(e.getMessage());
        }
    }
}