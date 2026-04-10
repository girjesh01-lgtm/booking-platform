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
import java.util.LinkedHashSet;
import java.util.List;
import java.util.Set;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
public class BookingService {

    private final SeatRepository seatRepository;
    private final BookingRepository bookingRepository;

    @Transactional
    public Booking createBooking(BookingRequest request) {
        List<String> requestedSeats = validateAndNormalize(request);

        // 1. Lock seats
        List<Seat> seats = seatRepository.findSeatsForUpdate(
                request.getShowId(),
                requestedSeats
        );

        if (seats.size() != requestedSeats.size()) {
            Set<String> foundSeatNumbers = seats.stream()
                    .map(Seat::getSeatNumber)
                    .collect(Collectors.toSet());

            List<String> missingSeatNumbers = requestedSeats.stream()
                    .filter(seatNumber -> !foundSeatNumbers.contains(seatNumber))
                    .toList();

            throw new RuntimeException("Seats not found: " + missingSeatNumbers);
        }

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
        booking.setSeats(seats);

        booking = bookingRepository.save(booking);

        return booking;
    }

    public List<Seat> getAvailableSeats(Long showId) {
        return seatRepository.findByShowIdAndStatus(showId, SeatStatus.AVAILABLE);
    }

    @Transactional
    public String confirmBooking(Long bookingId) {
        Booking booking = getLockedBooking(bookingId);

        for (Seat seat : booking.getSeats()) {
            seat.setStatus(SeatStatus.BOOKED);
        }

        booking.setStatus(BookingStatus.CONFIRMED);
        bookingRepository.save(booking);
        seatRepository.saveAll(booking.getSeats());

        return "Booking Confirmed!";
    }

    @Transactional
    public String failBooking(Long bookingId) {
        Booking booking = getLockedBooking(bookingId);

        for (Seat seat : booking.getSeats()) {
            seat.setStatus(SeatStatus.AVAILABLE);
        }

        booking.setStatus(BookingStatus.FAILED);
        bookingRepository.save(booking);
        seatRepository.saveAll(booking.getSeats());

        return "Booking Failed!";
    }

    private Booking getLockedBooking(Long bookingId) {
        Booking booking = bookingRepository.findById(bookingId)
                .orElseThrow(() -> new RuntimeException("Booking not found"));

        if (booking.getStatus() != BookingStatus.LOCKED) {
            throw new RuntimeException("Booking is not in LOCKED state: " + bookingId);
        }

        return booking;
    }

    private List<String> validateAndNormalize(BookingRequest request) {
        if (request.getUserId() == null) {
            throw new RuntimeException("User id is required");
        }
        if (request.getShowId() == null) {
            throw new RuntimeException("Show id is required");
        }
        if (request.getSeats() == null || request.getSeats().isEmpty()) {
            throw new RuntimeException("At least one seat is required");
        }

        Set<String> uniqueSeatNumbers = new LinkedHashSet<>(request.getSeats());
        if (uniqueSeatNumbers.size() != request.getSeats().size()) {
            throw new RuntimeException("Duplicate seats are not allowed");
        }

        return List.copyOf(uniqueSeatNumbers);
    }
}
