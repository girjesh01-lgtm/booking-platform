package com.booking.platform.controller;

import com.booking.platform.dto.BookingRequest;
import com.booking.platform.model.Booking;
import com.booking.platform.model.Seat;
import com.booking.platform.service.BookingService;
import lombok.RequiredArgsConstructor;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/bookings")
@RequiredArgsConstructor
public class BookingController {

    private final BookingService bookingService;

    @GetMapping("/shows/{showId}/seats/available")
    public List<Seat> getAvailableSeats(@PathVariable Long showId) {
        return bookingService.getAvailableSeats(showId);
    }

    @PostMapping("/lock")
    public Booking lockSeats(@RequestBody BookingRequest request) {
        return bookingService.createBooking(request);
    }

    @PostMapping("/{bookingId}/confirm")
    public String confirm(@PathVariable Long bookingId) {
        return bookingService.confirmBooking(bookingId);
    }

    @PostMapping("/{bookingId}/fail")
    public String fail(@PathVariable Long bookingId) {
        return bookingService.failBooking(bookingId);
    }
}
