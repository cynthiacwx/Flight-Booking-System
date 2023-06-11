package com.management.repository;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;
import com.management.model.Reservation;

import java.util.List;

@Repository
public interface ReservationRepository extends JpaRepository<Reservation, Long> {

    List<Reservation> findByUserId(Long userId);

    Reservation findByUserIdAndFlightId(Long userId, Long flightId);

    boolean existsByUserIdAndFlightId(Long userId, Long flightId);
}
