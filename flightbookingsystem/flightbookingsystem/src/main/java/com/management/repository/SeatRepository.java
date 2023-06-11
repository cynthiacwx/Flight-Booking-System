package com.management.repository;

import com.management.model.Seat;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface SeatRepository extends JpaRepository<Seat, Long> {
    List<Seat> findByFlightId(Long flightId);

    Seat findByFlightIdAndSeatNumber(Long flightId, String seatNumber);

}
