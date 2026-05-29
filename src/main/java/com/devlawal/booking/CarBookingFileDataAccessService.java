package com.devlawal.booking;

import java.io.*;
import java.util.ArrayList;
import java.util.List;
import java.util.UUID;

public class CarBookingFileDataAccessService implements CarBookingDao, Serializable {
    private final String PATH;
    private final File file;

    public CarBookingFileDataAccessService() {
        this.PATH = "src/main/resources/booking.dat";
        this.file = new File(this.PATH);
    }

    public CarBookingFileDataAccessService(String path) {
        this.PATH = path;
        this.file = new File(path);
    }

    @Override
    public List<CarBooking> getAllBookings() {
        if (!file.exists()) {
            return new ArrayList<>();
        }
        try (ObjectInputStream oos = new ObjectInputStream(new FileInputStream(file))) {
            return (List<CarBooking>) oos.readObject();
        } catch (Exception e) {
            throw new RuntimeException(e);
        }
    }

    @Override
    public void addBooking(CarBooking carBooking) {
        List<CarBooking> temp = getAllBookings();
        temp.add(carBooking);
        try (java.io.ObjectOutputStream oos = new ObjectOutputStream(new FileOutputStream(file))) {
            oos.writeObject(temp);
        } catch (Exception e) {
            throw new RuntimeException(e);
        }
    }

    @Override
    public boolean deleteCarBooking(UUID id) {
        List<CarBooking> temp = getAllBookings();
        return temp.stream()
                .filter(booking -> booking != null && booking.getId() != null && booking.getId().equals(id))
                .findFirst()
                .map(booking -> {
                    booking.setStatus(BookingStatus.CANCELLED);
                    try (java.io.ObjectOutputStream oos = new ObjectOutputStream(new FileOutputStream(file))) {
                        oos.writeObject(temp);
                    } catch (Exception e) {
                        throw new RuntimeException(e);
                    }
                    return true;
                })
                .orElse(false);
    }
}
