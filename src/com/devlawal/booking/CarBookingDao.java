package com.devlawal.booking;

public class CarBookingDao {
    private static CarBooking[] carBookings;

    static {
        carBookings = new CarBooking[5];
    }

    public CarBooking[] getAllBookings() {
        return carBookings;
    }

    public boolean addBooking(CarBooking carBooking) {
        int availableSlot = -1, index = -1;
        for (CarBooking booking : getAllBookings()) {
            index++;
            if (booking == null) {
                availableSlot = index;
                break;
            }
        }
        if (availableSlot >= 0) {
            carBookings[availableSlot] = carBooking;
            return true;
        }
        if (index >= 0) {
            CarBooking[] newCarBookings = new CarBooking[carBookings.length + 5];
            System.arraycopy(carBookings, 0, newCarBookings, 0, index);
            newCarBookings[++index] = carBooking;
            carBookings = newCarBookings;
            return true;
        }
        return false;
    }

}
