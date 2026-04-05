package com.devlawal.booking;

import java.io.File;
import java.io.ObjectInputStream;
import java.util.UUID;

public class CarBookingFileDataAccessService implements CarBookingDao {
    private final String filePath = "src/com/devlawal/booking/booking.dat";

    @Override
    public CarBooking[] getAllBookings() {
        File file = new File(filePath);
        if (!file.exists()) {
            return new CarBooking[0];
        }
        try (ObjectInputStream ois = new ObjectInputStream(new java.io.FileInputStream(filePath))) {
            return (CarBooking[]) ois.readObject();
        } catch (Exception e) {
            throw new RuntimeException(e);

        }
    }

    @Override
    public void addBooking(CarBooking carBooking) {
        CarBooking[] existingBookings = getAllBookings();
        CarBooking[] temp = new CarBooking[existingBookings.length + 1];
        for (int i = 0; i < existingBookings.length; i++) {
            temp[i] = existingBookings[i];
        }
        temp[existingBookings.length] = carBooking;
        try (java.io.ObjectOutputStream oos = new java.io.ObjectOutputStream(new java.io.FileOutputStream(filePath))) {
            oos.writeObject(temp);
        } catch (Exception e) {
            throw new RuntimeException(e);
        }
    }

    @Override
    public boolean deleteCarBooking(UUID id) {
        CarBooking[] bookings = getAllBookings();
        int index = 0;
        boolean found = false;
        for (CarBooking booking : bookings) {
            if (booking.getId().equals(id)) {
                found = true;
                booking.setStatus(BookingStatus.CANCELLED);
            }
        }
        if (found) {
            try (java.io.ObjectOutputStream oos = new java.io.ObjectOutputStream(new java.io.FileOutputStream(filePath))) {
                oos.writeObject(bookings);
            } catch (Exception e) {
                throw new RuntimeException(e);
            }
            return true;
        }
        return false;
    }
}
