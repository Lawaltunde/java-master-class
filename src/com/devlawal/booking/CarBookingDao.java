package com.devlawal.booking;

import java.util.UUID;

public interface CarBookingDao {

    CarBooking[] getAllBookings();

    void addBooking(CarBooking carBooking);

    boolean deleteCarBooking(UUID id);

}
