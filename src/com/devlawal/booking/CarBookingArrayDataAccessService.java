package com.devlawal.booking;

import java.util.ArrayList;
import java.util.List;
import java.util.Optional;
import java.util.UUID;

public class CarBookingArrayDataAccessService implements CarBookingDao {
    private final List<CarBooking> carBookings = new ArrayList<CarBooking>();

    public List<CarBooking> getAllBookings() {
        return carBookings;
    }

    public void addBooking(CarBooking carBooking) {
        carBookings.add(carBooking);
    }

    @Override
    public boolean deleteCarBooking(UUID id) {

        Optional<CarBooking> booking = carBookings.stream().filter(carBooking -> carBooking.getId().equals(id)).findFirst();
        if (booking.isPresent()) {
            booking.ifPresent(carBooking -> carBooking.setStatus(BookingStatus.CANCELLED));
            return true;
        }
        return false;
    }

}
