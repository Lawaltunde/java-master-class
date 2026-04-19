package com.devlawal.booking;

import java.util.List;
import java.util.UUID;

public interface CarBookingDao {

    List<CarBooking> getAllBookings();

    void addBooking(CarBooking carBooking);

    boolean deleteCarBooking(UUID id);

}
