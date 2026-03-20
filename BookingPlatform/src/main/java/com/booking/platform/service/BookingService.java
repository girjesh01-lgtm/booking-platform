package com.booking.platform.service;

import com.booking.platform.dto.BookingRequest;
import com.booking.platform.model.Booking;
import com.booking.platform.model.BookingStatus;
import com.booking.platform.model.Seat;
import com.booking.platform.model.SeatStatus;
import com.booking.platform.repository.BookingRepository;
import com.booking.platform.repository.SeatRepository;
import jakarta.transaction.Transactional;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.time.LocalDateTime;
import java.util.List;

@Service
@RequiredArgsConstructor
public class BookingService {

    private final SeatRepository seatRepository;
    private final BookingRepository bookingRepository;

    @Transactional
    public Booking createBooking(BookingRequest request) {

        // 1. Lock seats
        List<Seat> seats = seatRepository.findSeatsForUpdate(
                request.getShowId(),
                request.getSeats()
        );

        for (Seat seat : seats) {
            if (seat.getStatus() != SeatStatus.AVAILABLE) {
                throw new RuntimeException("Seat not available: " + seat.getSeatNumber());
            }
        }

        for (Seat seat : seats) {
            seat.setStatus(SeatStatus.LOCKED);
        }
        seatRepository.saveAll(seats);

        // 2. Create booking (LOCKED state)
        Booking booking = new Booking();
        booking.setUserId(request.getUserId());
        booking.setShowId(request.getShowId());
        booking.setStatus(BookingStatus.LOCKED);
        booking.setCreatedAt(LocalDateTime.now());

        booking = bookingRepository.save(booking);

        return booking;
    }

    @Transactional
    public String confirmBooking(Long bookingId) {

        Booking booking = bookingRepository.findById(bookingId)
                .orElseThrow(() -> new RuntimeException("Booking not found"));

        // simulate payment
        boolean paymentSuccess = true;

        if (paymentSuccess) {
            booking.setStatus(BookingStatus.CONFIRMED);
            bookingRepository.save(booking);

            // mark seats as BOOKED
            // (you can improve by linking booking-seat mapping)
            return "Booking Confirmed!";
        } else {
            booking.setStatus(BookingStatus.FAILED);
            bookingRepository.save(booking);

            // release seats
            // set seats back to AVAILABLE

            return "Booking Failed!";
        }
    }
}