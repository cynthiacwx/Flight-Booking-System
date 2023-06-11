package com.management.repository;

import com.management.model.Flight;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.Date;

@Repository
public interface FlightRepository extends JpaRepository<Flight, Long> {

    Flight findByFromAndToAndDate(String from, String to, Date date);

    boolean existsByFromAndToAndDate(String from, String to, Date date);

}
