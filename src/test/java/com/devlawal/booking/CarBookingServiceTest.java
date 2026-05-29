package com.devlawal.booking;

import com.devlawal.car.Brand;
import com.devlawal.car.Car;
import com.devlawal.car.CarService;
import com.devlawal.user.User;
import com.devlawal.user.UserService;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.math.BigDecimal;
import java.time.Clock;
import java.time.LocalDate;
import java.time.ZoneId;
import java.util.List;
import java.util.UUID;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class CarBookingServiceTest {
    @Mock private UserService userService;
    @Mock private CarService carService;
    @Mock private CarBookingDao carBookingDao;

    private Clock clock;
    private CarBookingService underTest;

    private static final LocalDate TODAY = LocalDate.of(2026, 5, 29);
    private static final LocalDate TOMORROW = TODAY.plusDays(1);
    private static final LocalDate NEXT_WEEK = TODAY.plusDays(7);

    private User testUser;
    private Car testCar;

    @BeforeEach
    void setUp() {
        clock = Clock.fixed(TODAY.atStartOfDay(ZoneId.systemDefault()).toInstant(), ZoneId.systemDefault());
        underTest = new CarBookingService(userService, carService, carBookingDao, clock);
        testUser = new User(UUID.randomUUID(), "Alice");
        testCar = new Car(UUID.randomUUID(), "1234", new BigDecimal("100"), Brand.TESLA, true);
    }

    // --- getAllBookings ---

    @Test
    void canGetAllBookings() {
        List<CarBooking> bookings = List.of(activeBooking(UUID.randomUUID()));
        when(carBookingDao.getAllBookings()).thenReturn(bookings);

        assertThat(underTest.getAllBookings()).isEqualTo(bookings);
        verify(carBookingDao).getAllBookings();
    }

    // --- bookCar: happy path ---

    @Test
    void canBookCar() {
        when(userService.getUserById(testUser.getId())).thenReturn(testUser);
        when(carService.getCarById(testCar.getId())).thenReturn(testCar);
        when(carBookingDao.getAllBookings()).thenReturn(List.of());

        CarBooking result = underTest.bookCar(testUser.getId(), testCar.getId(), TOMORROW, NEXT_WEEK);

        assertThat(result.getUser()).isEqualTo(testUser);
        assertThat(result.getCar()).isEqualTo(testCar);
        assertThat(result.getStartDate()).isEqualTo(TOMORROW);
        assertThat(result.getEndDate()).isEqualTo(NEXT_WEEK);
        assertThat(result.getStatus()).isEqualTo(BookingStatus.ACTIVE);
        verify(carBookingDao).addBooking(result);
    }

    @Test
    void bookCarCalculatesPriceCorrectly() {
        when(userService.getUserById(testUser.getId())).thenReturn(testUser);
        when(carService.getCarById(testCar.getId())).thenReturn(testCar);
        when(carBookingDao.getAllBookings()).thenReturn(List.of());

        CarBooking result = underTest.bookCar(testUser.getId(), testCar.getId(), TOMORROW, NEXT_WEEK);

        // 6 days × £100/day = £600
        assertThat(result.getPrice()).isEqualByComparingTo(new BigDecimal("600"));
    }

    @Test
    void bookCarSetsBookedAtFromClock() {
        when(userService.getUserById(testUser.getId())).thenReturn(testUser);
        when(carService.getCarById(testCar.getId())).thenReturn(testCar);
        when(carBookingDao.getAllBookings()).thenReturn(List.of());

        CarBooking result = underTest.bookCar(testUser.getId(), testCar.getId(), TOMORROW, NEXT_WEEK);

        assertThat(result.getBookedAt()).isEqualTo(TODAY.atStartOfDay());
    }

    // --- bookCar: null / not-found guards ---

    @Test
    void throwsWhenUserIdIsNull() {
        assertThatThrownBy(() -> underTest.bookCar(null, testCar.getId(), TOMORROW, NEXT_WEEK))
                .isInstanceOf(IllegalArgumentException.class)
                .hasMessageContaining("userId can't be null");
        verifyNoInteractions(userService, carService, carBookingDao);
    }

    @Test
    void throwsWhenUserNotFound() {
        when(userService.getUserById(testUser.getId())).thenReturn(null);

        assertThatThrownBy(() -> underTest.bookCar(testUser.getId(), testCar.getId(), TOMORROW, NEXT_WEEK))
                .isInstanceOf(IllegalArgumentException.class)
                .hasMessageContaining("can't be found");
    }

    @Test
    void throwsWhenCarIdIsNull() {
        when(userService.getUserById(testUser.getId())).thenReturn(testUser);

        assertThatThrownBy(() -> underTest.bookCar(testUser.getId(), null, TOMORROW, NEXT_WEEK))
                .isInstanceOf(IllegalArgumentException.class)
                .hasMessageContaining("carId can't be null");
    }

    @Test
    void throwsWhenCarNotFound() {
        when(userService.getUserById(testUser.getId())).thenReturn(testUser);
        when(carService.getCarById(testCar.getId())).thenReturn(null);

        assertThatThrownBy(() -> underTest.bookCar(testUser.getId(), testCar.getId(), TOMORROW, NEXT_WEEK))
                .isInstanceOf(IllegalStateException.class)
                .hasMessageContaining("can't be found");
    }

    // --- bookCar: date guards ---

    @Test
    void throwsWhenStartDateIsBeforeToday() {
        LocalDate yesterday = TODAY.minusDays(1);
        when(userService.getUserById(testUser.getId())).thenReturn(testUser);
        when(carService.getCarById(testCar.getId())).thenReturn(testCar);

        assertThatThrownBy(() -> underTest.bookCar(testUser.getId(), testCar.getId(), yesterday, NEXT_WEEK))
                .isInstanceOf(IllegalArgumentException.class)
                .hasMessageContaining("startDate can't be before now");
    }

    @Test
    void throwsWhenEndDateIsBeforeStartDate() {
        when(userService.getUserById(testUser.getId())).thenReturn(testUser);
        when(carService.getCarById(testCar.getId())).thenReturn(testCar);

        assertThatThrownBy(() -> underTest.bookCar(testUser.getId(), testCar.getId(), NEXT_WEEK, TOMORROW))
                .isInstanceOf(IllegalArgumentException.class)
                .hasMessageContaining("endDate can't be before startDate");
    }

    // --- bookCar: duplicate booking guard ---

    @Test
    void throwsWhenCarAlreadyBooked() {
        when(userService.getUserById(testUser.getId())).thenReturn(testUser);
        when(carService.getCarById(testCar.getId())).thenReturn(testCar);
        when(carBookingDao.getAllBookings()).thenReturn(List.of(activeBooking(UUID.randomUUID())));

        assertThatThrownBy(() -> underTest.bookCar(testUser.getId(), testCar.getId(), TOMORROW, NEXT_WEEK))
                .isInstanceOf(IllegalArgumentException.class)
                .hasMessageContaining("Car already booked");
    }

    @Test
    void canRebookCancelledCar() {
        CarBooking cancelled = new CarBooking(UUID.randomUUID(), testUser, testCar,
                TOMORROW, NEXT_WEEK, new BigDecimal("600"), BookingStatus.CANCELLED, TODAY.atStartOfDay());
        when(userService.getUserById(testUser.getId())).thenReturn(testUser);
        when(carService.getCarById(testCar.getId())).thenReturn(testCar);
        when(carBookingDao.getAllBookings()).thenReturn(List.of(cancelled));

        CarBooking result = underTest.bookCar(testUser.getId(), testCar.getId(), TOMORROW, NEXT_WEEK);

        assertThat(result.getStatus()).isEqualTo(BookingStatus.ACTIVE);
    }

    // --- getAllActiveBookings ---

    @Test
    void canGetAllActiveBookings() {
        CarBooking active = activeBooking(UUID.randomUUID());
        CarBooking cancelled = new CarBooking(UUID.randomUUID(), testUser, testCar,
                TOMORROW, NEXT_WEEK, new BigDecimal("600"), BookingStatus.CANCELLED, TODAY.atStartOfDay());
        when(carBookingDao.getAllBookings()).thenReturn(List.of(active, cancelled));

        List<CarBooking> actual = underTest.getAllActiveBookings();

        assertThat(actual).containsExactly(active);
    }

    @Test
    void returnsEmptyListWhenNoActiveBookings() {
        when(carBookingDao.getAllBookings()).thenReturn(List.of());

        assertThat(underTest.getAllActiveBookings()).isEmpty();
    }

    // --- getCarBookingById ---

    @Test
    void canGetBookingById() {
        UUID id = UUID.randomUUID();
        CarBooking booking = activeBooking(id);
        when(carBookingDao.getAllBookings()).thenReturn(List.of(booking));

        assertThat(underTest.getCarBookingById(id)).isEqualTo(booking);
    }

    @Test
    void throwsWhenGetBookingByIdWithNull() {
        assertThatThrownBy(() -> underTest.getCarBookingById(null))
                .isInstanceOf(IllegalArgumentException.class)
                .hasMessageContaining("Enter valid car ID");
    }

    @Test
    void throwsWhenBookingNotFound() {
        when(carBookingDao.getAllBookings()).thenReturn(List.of());

        assertThatThrownBy(() -> underTest.getCarBookingById(UUID.randomUUID()))
                .isInstanceOf(IllegalStateException.class)
                .hasMessageContaining("Car ID does not exist");
    }

    // --- deleteCarBooking ---

    @Test
    void canDeleteBooking() {
        UUID id = UUID.randomUUID();
        when(carBookingDao.deleteCarBooking(id)).thenReturn(true);

        assertThat(underTest.deleteCarBooking(id)).isTrue();
        verify(carBookingDao).deleteCarBooking(id);
    }

    @Test
    void throwsWhenDeleteWithNullId() {
        assertThatThrownBy(() -> underTest.deleteCarBooking(null))
                .isInstanceOf(IllegalArgumentException.class)
                .hasMessageContaining("Enter valid Booking ID");
    }

    // --- getCarsThatAreNotYetBooked ---

    @Test
    void canGetAvailableCars() {
        Car bookedCar = new Car(UUID.randomUUID(), "5678", new BigDecimal("90"), Brand.TOYOTA, false);
        Car availableCar = new Car(UUID.randomUUID(), "9012", new BigDecimal("80"), Brand.AUDI, false);
        CarBooking booking = new CarBooking(UUID.randomUUID(), testUser, bookedCar,
                TOMORROW, NEXT_WEEK, new BigDecimal("540"), BookingStatus.ACTIVE, TODAY.atStartOfDay());
        when(carService.getAllCars()).thenReturn(List.of(bookedCar, availableCar));
        when(carBookingDao.getAllBookings()).thenReturn(List.of(booking));

        assertThat(underTest.getCarsThatAreNotYetBooked()).containsExactly(availableCar);
    }

    @Test
    void cancelledBookingDoesNotBlockCar() {
        Car car = new Car(UUID.randomUUID(), "5678", new BigDecimal("90"), Brand.TOYOTA, false);
        CarBooking cancelled = new CarBooking(UUID.randomUUID(), testUser, car,
                TOMORROW, NEXT_WEEK, new BigDecimal("540"), BookingStatus.CANCELLED, TODAY.atStartOfDay());
        when(carService.getAllCars()).thenReturn(List.of(car));
        when(carBookingDao.getAllBookings()).thenReturn(List.of(cancelled));

        assertThat(underTest.getCarsThatAreNotYetBooked()).containsExactly(car);
    }

    @Test
    void returnsAllCarsWhenNoBookingsExist() {
        when(carService.getAllCars()).thenReturn(List.of(testCar));
        when(carBookingDao.getAllBookings()).thenReturn(List.of());

        assertThat(underTest.getCarsThatAreNotYetBooked()).containsExactly(testCar);
    }

    // --- getNotYetBookedElectricCars ---

    @Test
    void canGetAvailableElectricCars() {
        Car electric = new Car(UUID.randomUUID(), "1234", new BigDecimal("100"), Brand.TESLA, true);
        Car nonElectric = new Car(UUID.randomUUID(), "5678", new BigDecimal("90"), Brand.TOYOTA, false);
        when(carService.getAllCars()).thenReturn(List.of(electric, nonElectric));
        when(carBookingDao.getAllBookings()).thenReturn(List.of());

        assertThat(underTest.getNotYetBookedElectricCars()).containsExactly(electric);
    }

    @Test
    void returnsEmptyWhenAllElectricCarsAreBooked() {
        Car electric = new Car(UUID.randomUUID(), "1234", new BigDecimal("100"), Brand.TESLA, true);
        CarBooking booking = new CarBooking(UUID.randomUUID(), testUser, electric,
                TOMORROW, NEXT_WEEK, new BigDecimal("600"), BookingStatus.ACTIVE, TODAY.atStartOfDay());
        when(carService.getAllCars()).thenReturn(List.of(electric));
        when(carBookingDao.getAllBookings()).thenReturn(List.of(booking));

        assertThat(underTest.getNotYetBookedElectricCars()).isEmpty();
    }

    // --- getAllCarsBookedByUser ---

    @Test
    void canGetAllCarsBookedByUser() {
        UUID userId = UUID.randomUUID();
        User user = new User(userId, "Bob");
        Car car1 = new Car(UUID.randomUUID(), "1111", new BigDecimal("100"), Brand.TESLA, true);
        CarBooking userBooking = new CarBooking(UUID.randomUUID(), user, car1,
                TOMORROW, NEXT_WEEK, new BigDecimal("600"), BookingStatus.ACTIVE, TODAY.atStartOfDay());
        CarBooking otherBooking = new CarBooking(UUID.randomUUID(), testUser, testCar,
                TOMORROW, NEXT_WEEK, new BigDecimal("600"), BookingStatus.ACTIVE, TODAY.atStartOfDay());
        when(carBookingDao.getAllBookings()).thenReturn(List.of(userBooking, otherBooking));

        List<Car> actual = underTest.getAllCarsBookedByUser(userId);

        assertThat(actual).containsExactly(car1);
    }

    @Test
    void throwsWhenGetCarsBookedByUserWithNullId() {
        assertThatThrownBy(() -> underTest.getAllCarsBookedByUser(null))
                .isInstanceOf(IllegalArgumentException.class)
                .hasMessageContaining("Enter valid user ID");
    }

    @Test
    void returnsEmptyListWhenUserHasNoBookings() {
        when(carBookingDao.getAllBookings()).thenReturn(List.of());

        assertThat(underTest.getAllCarsBookedByUser(UUID.randomUUID())).isEmpty();
    }

    private CarBooking activeBooking(UUID id) {
        return new CarBooking(id, testUser, testCar, TOMORROW, NEXT_WEEK,
                new BigDecimal("600"), BookingStatus.ACTIVE, TODAY.atStartOfDay());
    }
}
