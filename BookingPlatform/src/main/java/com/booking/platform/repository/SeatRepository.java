package com.booking.platform.repository;

import com.booking.platform.model.Seat;
import jakarta.persistence.LockModeType;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Lock;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface SeatRepository extends JpaRepository<Seat, Long> {

    @Lock(LockModeType.PESSIMISTIC_WRITE)
    @Query("SELECT s FROM Seat s WHERE s.showId = :showId AND s.seatNumber IN :seatNumbers")
    List<Seat> findSeatsForUpdate(@Param("showId") Long showId, @Param("seatNumbers") List<String> seatNumbers);

    List<Seat> findByShowIdAndStatus(Long showId, com.booking.platform.model.SeatStatus status);
}
