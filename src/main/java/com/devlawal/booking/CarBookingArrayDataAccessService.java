package com.devlawal.booking;

import java.util.ArrayList;
import java.util.List;
import java.util.Objects;
import java.util.UUID;

public class CarBookingArrayDataAccessService implements CarBookingDao {
    private final List<CarBooking> carBookings = new ArrayList<>();

    @Override
    public List<CarBooking> getAllBookings() {
        return carBookings;
    }

    @Override
    public void addBooking(CarBooking carBooking) {
        carBookings.add(carBooking);
    }

    @Override
    public boolean deleteCarBooking(UUID id) {
        return carBookings.stream()
                .filter(carBooking -> carBooking != null && Objects.equals(carBooking.getId(), id))
                .findFirst()
                .map(carBooking -> {
                    carBooking.setStatus(BookingStatus.CANCELLED);
                    return true;
                })
                .orElse(false);
    }

}
