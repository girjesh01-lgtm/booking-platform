package com.booking.platform.model;


import jakarta.persistence.*;
import lombok.Data;

@Entity
@Data
public class Seat {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    private String seatNumber;

    private Long showId;

    @Enumerated(EnumType.STRING)
    private SeatStatus status; // AVAILABLE, LOCKED, BOOKED
}
