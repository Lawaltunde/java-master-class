package com.devlawal.booking;

import com.devlawal.car.Brand;
import com.devlawal.car.Car;
import com.devlawal.user.User;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import java.math.BigDecimal;
import java.time.LocalDate;
import java.util.UUID;

import static org.assertj.core.api.Assertions.assertThat;

class CarBookingArrayDataAccessServiceTest {
    private CarBookingArrayDataAccessService underTest;
    private User user;
    private Car car;

    @BeforeEach
    void setUp() {
        underTest = new CarBookingArrayDataAccessService();
        user = new User(UUID.randomUUID(), "Alice");
        car = new Car(UUID.randomUUID(), "1234", new BigDecimal("100"), Brand.TESLA, true);
    }

    @Test
    void startsWithNoBookings() {
        assertThat(underTest.getAllBookings()).isEmpty();
    }

    @Test
    void canAddAndRetrieveBooking() {
        CarBooking booking = newActiveBooking(UUID.randomUUID());

        underTest.addBooking(booking);

        assertThat(underTest.getAllBookings()).containsExactly(booking);
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
    void deleteDoesNotRemoveBookingFromList() {
        UUID id = UUID.randomUUID();
        underTest.addBooking(newActiveBooking(id));

        underTest.deleteCarBooking(id);

        assertThat(underTest.getAllBookings()).hasSize(1);
    }

    @Test
    void returnsFalseWhenDeletingUnknownId() {
        boolean result = underTest.deleteCarBooking(UUID.randomUUID());
        assertThat(result).isFalse();
    }

    @Test
    void onlyTargetedBookingIsCancelled() {
        UUID cancelId = UUID.randomUUID();
        UUID keepId = UUID.randomUUID();
        underTest.addBooking(newActiveBooking(cancelId));
        underTest.addBooking(newActiveBooking(keepId));

        underTest.deleteCarBooking(cancelId);

        assertThat(underTest.getAllBookings())
                .filteredOn(b -> b.getId().equals(keepId))
                .extracting(CarBooking::getStatus)
                .containsExactly(BookingStatus.ACTIVE);
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
