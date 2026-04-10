package com.booking.platform.service;

import com.booking.platform.dto.BookingRequest;
import com.booking.platform.model.Booking;
import com.booking.platform.model.BookingStatus;
import com.booking.platform.model.Seat;
import com.booking.platform.model.SeatStatus;
import com.booking.platform.repository.BookingRepository;
import com.booking.platform.repository.SeatRepository;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.util.ArrayList;
import java.util.List;
import java.util.Optional;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

@ExtendWith(MockitoExtension.class)
class BookingServiceTest {

    @Mock
    private SeatRepository seatRepository;

    @Mock
    private BookingRepository bookingRepository;

    @InjectMocks
    private BookingService bookingService;

    @Test
    void createBookingLocksSeatsAndLinksThemToBooking() {
        BookingRequest request = bookingRequest(List.of("A1", "A2"));
        Seat seatA1 = seat("A1", SeatStatus.AVAILABLE);
        Seat seatA2 = seat("A2", SeatStatus.AVAILABLE);

        when(seatRepository.findSeatsForUpdate(101L, List.of("A1", "A2")))
                .thenReturn(List.of(seatA1, seatA2));
        when(bookingRepository.save(any(Booking.class)))
                .thenAnswer(invocation -> invocation.getArgument(0));

        Booking booking = bookingService.createBooking(request);

        assertEquals(BookingStatus.LOCKED, booking.getStatus());
        assertEquals(List.of(seatA1, seatA2), booking.getSeats());
        assertEquals(SeatStatus.LOCKED, seatA1.getStatus());
        assertEquals(SeatStatus.LOCKED, seatA2.getStatus());
        verify(seatRepository).saveAll(List.of(seatA1, seatA2));
    }

    @Test
    void confirmBookingMarksLockedSeatsAsBooked() {
        Seat seatA1 = seat("A1", SeatStatus.LOCKED);
        Seat seatA2 = seat("A2", SeatStatus.LOCKED);
        Booking booking = lockedBooking(seatA1, seatA2);

        when(bookingRepository.findById(1L)).thenReturn(Optional.of(booking));

        String response = bookingService.confirmBooking(1L);

        assertEquals("Booking Confirmed!", response);
        assertEquals(BookingStatus.CONFIRMED, booking.getStatus());
        assertEquals(SeatStatus.BOOKED, seatA1.getStatus());
        assertEquals(SeatStatus.BOOKED, seatA2.getStatus());
        verify(seatRepository).saveAll(booking.getSeats());
        verify(bookingRepository).save(booking);
    }

    @Test
    void failBookingReleasesLockedSeats() {
        Seat seatA1 = seat("A1", SeatStatus.LOCKED);
        Seat seatA2 = seat("A2", SeatStatus.LOCKED);
        Booking booking = lockedBooking(seatA1, seatA2);

        when(bookingRepository.findById(1L)).thenReturn(Optional.of(booking));

        String response = bookingService.failBooking(1L);

        assertEquals("Booking Failed!", response);
        assertEquals(BookingStatus.FAILED, booking.getStatus());
        assertEquals(SeatStatus.AVAILABLE, seatA1.getStatus());
        assertEquals(SeatStatus.AVAILABLE, seatA2.getStatus());
        verify(seatRepository).saveAll(booking.getSeats());
        verify(bookingRepository).save(booking);
    }

    @Test
    void createBookingRejectsDuplicateSeatNumbers() {
        BookingRequest request = bookingRequest(List.of("A1", "A1"));

        assertThrows(RuntimeException.class, () -> bookingService.createBooking(request));
    }

    private BookingRequest bookingRequest(List<String> seatNumbers) {
        BookingRequest request = new BookingRequest();
        request.setUserId(7L);
        request.setShowId(101L);
        request.setSeats(seatNumbers);
        return request;
    }

    private Booking lockedBooking(Seat... seats) {
        Booking booking = new Booking();
        booking.setId(1L);
        booking.setUserId(7L);
        booking.setShowId(101L);
        booking.setStatus(BookingStatus.LOCKED);
        booking.setSeats(new ArrayList<>(List.of(seats)));
        return booking;
    }

    private Seat seat(String seatNumber, SeatStatus status) {
        Seat seat = new Seat();
        seat.setSeatNumber(seatNumber);
        seat.setShowId(101L);
        seat.setStatus(status);
        return seat;
    }
}
