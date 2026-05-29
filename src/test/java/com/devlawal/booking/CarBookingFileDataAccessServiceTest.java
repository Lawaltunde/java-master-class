package com.devlawal.booking;

import com.devlawal.car.Brand;
import com.devlawal.car.Car;
import com.devlawal.user.User;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.io.TempDir;

import java.math.BigDecimal;
import java.nio.file.Path;
import java.time.LocalDate;
import java.util.List;
import java.util.UUID;

import static org.assertj.core.api.Assertions.assertThat;

class CarBookingFileDataAccessServiceTest {
    @TempDir
    Path tempDir;

    private CarBookingFileDataAccessService underTest;
    private User user;
    private Car car;

    @BeforeEach
    void setUp() {
        String path = tempDir.resolve("booking.dat").toString();
        underTest = new CarBookingFileDataAccessService(path);
        user = new User(UUID.randomUUID(), "Alice");
        car = new Car(UUID.randomUUID(), "1234", new BigDecimal("100"), Brand.TESLA, true);
    }

    @Test
    void returnsEmptyListWhenFileDoesNotExist() {
        assertThat(underTest.getAllBookings()).isEmpty();
    }

    @Test
    void canAddAndRetrieveBooking() {
        UUID id = UUID.randomUUID();
        underTest.addBooking(newActiveBooking(id));

        List<CarBooking> actual = underTest.getAllBookings();

        assertThat(actual).hasSize(1);
        assertThat(actual.get(0).getId()).isEqualTo(id);
    }

    @Test
    void canAddMultipleBookings() {
        underTest.addBooking(newActiveBooking(UUID.randomUUID()));
        underTest.addBooking(newActiveBooking(UUID.randomUUID()));

        assertThat(underTest.getAllBookings()).hasSize(2);
    }

    @Test
    void deleteSetsCancelledStatus() {
        UUID id = UUID.randomUUID();
        underTest.addBooking(newActiveBooking(id));

        boolean result = underTest.deleteCarBooking(id);

        assertThat(result).isTrue();
        assertThat(underTest.getAllBookings().get(0).getStatus()).isEqualTo(BookingStatus.CANCELLED);
    }

    @Test
    void deletedStatusIsPersisted() {
        UUID id = UUID.randomUUID();
        underTest.addBooking(newActiveBooking(id));
        underTest.deleteCarBooking(id);

        // re-read from disk to confirm the cancelled status was written
        assertThat(underTest.getAllBookings().get(0).getStatus()).isEqualTo(BookingStatus.CANCELLED);
    }

    @Test
    void returnsFalseWhenDeletingUnknownId() {
        boolean result = underTest.deleteCarBooking(UUID.randomUUID());
        assertThat(result).isFalse();
    }

    @Test
    void bookingsArePersistentAcrossInstances() {
        UUID id = UUID.randomUUID();
        underTest.addBooking(newActiveBooking(id));

        String samePath = tempDir.resolve("booking.dat").toString();
        CarBookingFileDataAccessService secondInstance = new CarBookingFileDataAccessService(samePath);

        assertThat(secondInstance.getAllBookings()).hasSize(1);
        assertThat(secondInstance.getAllBookings().get(0).getId()).isEqualTo(id);
    }

    private CarBooking newActiveBooking(UUID id) {
        return new CarBooking(id, user, car,
                LocalDate.now().plusDays(1),
                LocalDate.now().plusDays(7),
                new BigDecimal("600"),
                BookingStatus.ACTIVE,
                LocalDate.now().atStartOfDay()
        );
    }
}
