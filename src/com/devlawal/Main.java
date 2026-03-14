package com.devlawal;

public class Main {
    public static void main(String[] args) {

        printOptions();
    }

    public static void printOptions() {
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
    }
}