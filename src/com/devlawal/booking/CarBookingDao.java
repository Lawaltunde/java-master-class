package com.devlawal.booking;

public class CarBookingDao {
    private static CarBooking[] carBookings;

    static {
        carBookings = new CarBooking[0];
    }

    public CarBooking[] getAllBookings() {
        return carBookings;
    }

    public void addBooking(CarBooking carBooking) {
        int nextFreeSlot = -1;
        for (int i = 0; i < carBookings.length; i++) {
            if (carBookings[i] == null) {
                nextFreeSlot = i;
            }
        }
        if (nextFreeSlot > -1) {
            carBookings[nextFreeSlot] = carBooking;
            return;
        }

        CarBooking[] temp = new CarBooking[carBookings.length + 5];
        for (int i = 0; i < carBookings.length; i++) {
            temp[i] = carBookings[i];
        }
        nextFreeSlot = carBookings.length;
        temp[nextFreeSlot] = carBooking;
        carBookings = temp;
    }

}
