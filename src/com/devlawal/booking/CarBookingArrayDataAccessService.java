package com.devlawal.booking;

import java.util.ArrayList;
import java.util.List;
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
      if (!carBookings.isEmpty()) {
          for (CarBooking booking : carBookings) {
              if (booking != null && booking.getId().equals(id)) {
                  booking.setStatus(BookingStatus.CANCELLED);
                  return true;
              }
          }
      }
        return false;
    }

}
