package com.booking.platform.controller;

import com.booking.platform.dto.BookingRequest;
import com.booking.platform.model.Booking;
import com.booking.platform.service.BookingService;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/bookings")
@RequiredArgsConstructor
public class BookingController {

    private final BookingService bookingService;

    @PostMapping("/lock")
    public Booking lockSeats(@RequestBody BookingRequest request) {
        return bookingService.createBooking(request);
    }

    @PostMapping("/{bookingId}/confirm")
    public String confirm(@PathVariable Long bookingId) {
        return bookingService.confirmBooking(bookingId);
    }
}
