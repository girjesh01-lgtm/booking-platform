package com.booking.platform.model;

import jakarta.persistence.*;
import lombok.Data;

import java.time.LocalDateTime;

@Entity
@Data
public class Booking {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    private Long userId;

    private Long showId;

    @Enumerated(EnumType.STRING)
    private BookingStatus status; // LOCKED, CONFIRMED, FAILED

    private LocalDateTime createdAt;
}