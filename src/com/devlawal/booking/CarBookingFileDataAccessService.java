package com.devlawal.booking;

import java.io.File;
import java.io.FileInputStream;
import java.io.ObjectInputStream;
import java.util.ArrayList;
import java.util.List;
import java.util.UUID;

public class CarBookingFileDataAccessService implements CarBookingDao {
    private final String filePath = "src/com/devlawal/booking/booking.dat";

    @Override
    public List<CarBooking> getAllBookings() {
        File file = new File(filePath);
        if (!file.exists()) {
            return new ArrayList<>();
        }
        try (ObjectInputStream ois = new ObjectInputStream(new FileInputStream(file))) {
            return (List<CarBooking>) ois.readObject();
        } catch (Exception e) {
            throw new RuntimeException(e);

        }
    }

    @Override
    public void addBooking(CarBooking carBooking) {
        List<CarBooking> temp = getAllBookings();
        temp.add(carBooking);
        try (java.io.ObjectOutputStream oos = new java.io.ObjectOutputStream(new java.io.FileOutputStream(filePath))) {
            oos.writeObject(temp);
        } catch (Exception e) {
            throw new RuntimeException(e);
        }
    }

    @Override
    public boolean deleteCarBooking(UUID id) {
        List<CarBooking> bookings = getAllBookings();
        if (!bookings.isEmpty()) {
            for (CarBooking booking : bookings) {
                if (booking.getId().equals(id)) {
                    booking.setStatus(BookingStatus.CANCELLED);
                    try (java.io.ObjectOutputStream oos = new java.io.ObjectOutputStream(new java.io.FileOutputStream(filePath))) {
                        oos.writeObject(bookings);
                    } catch (Exception e) {
                        throw new RuntimeException(e);
                    }
                    return true;
                }
            }
        }
        return false;
    }
}
