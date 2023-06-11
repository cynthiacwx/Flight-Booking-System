package com.management.service;

import com.management.model.Flight;
import com.management.model.Reservation;
import com.management.model.Seat;
import com.management.model.User;
import com.management.repository.FlightRepository;
import com.management.repository.ReservationRepository;
import com.management.repository.SeatRepository;
import jakarta.servlet.http.HttpSession;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.*;

import java.util.Date;
import java.util.List;
import java.util.stream.Collectors;

@Controller
public class ReservationService {

    @Autowired
    private FlightRepository flightRepository;

    @Autowired
    private ReservationRepository reservationRepository;

    @Autowired
    private SeatRepository seatRepository;


    @GetMapping("/reservation/reservations")
    public String viewReservations(HttpSession session, Model model) {
        User user = (User) session.getAttribute("user");
        Date today = new Date();
        List<Reservation> allReservations = reservationRepository.findByUserId(user.getUserId());
        List<Reservation> upcomingReservations = allReservations.stream().filter(reservation -> !reservation.getDate().before(today)).collect(Collectors.toList());
        model.addAttribute("reservations", upcomingReservations);
        return "reservations";
    }

    @GetMapping("/reservation/past_flights")
    public String viewPastFlights(HttpSession session, Model model) {
        User user = (User) session.getAttribute("user");
        Date today = new Date();
        List<Reservation> allReservations = reservationRepository.findByUserId(user.getUserId());
        List<Reservation> pastReservations = allReservations.stream().filter(reservation -> reservation.getDate().before(today)).collect(Collectors.toList());
        model.addAttribute("pastFlights", pastReservations);
        return "past_flights";
    }

    @GetMapping ("/reservation/add_reservation")
    public String createReservation(Model model) {
        model.addAttribute("reservationExist", false);
        model.addAttribute("flightExist", true);
        model.addAttribute("createSuccess", false);
        model.addAttribute("flight",new Flight());
        return "add_reservation";
    }

    @PostMapping ("/reservation/save_created_reservation")
    public String saveCreatedReservation(@ModelAttribute("flight") Flight flight, Model model, HttpSession session) {
        User user = (User) session.getAttribute("user");
        model.addAttribute("reservationExist", false);
        model.addAttribute("flightExist", true);
        model.addAttribute("createSuccess", false);
        Flight savedFlight = flightRepository.findByFromAndToAndDate(flight.getFrom(), flight.getTo(), flight.getDate());
        if (savedFlight != null) {
            if (!reservationRepository.existsByUserIdAndFlightId(user.getUserId(), savedFlight.getFlightId())) {
                reservationRepository.save(Reservation.builder()
                        .userId(user.getUserId())
                        .flightId(savedFlight.getFlightId())
                        .from(savedFlight.getFrom())
                        .to(savedFlight.getTo())
                        .date(savedFlight.getDate()).build());
                model.addAttribute("createSuccess", true);
            } else {
                model.addAttribute("reservationExist", true);
            }
        } else {
            model.addAttribute("flightExist", false);
        }
        return "add_reservation";
    }

    @PostMapping ("/reservation/modify_reservation")
    public String modifyReservation(@RequestParam("reservationId") Long reservationId, Model model) {
        Reservation reservation = reservationRepository.findById(reservationId).get();
        model.addAttribute("selectedReservation", reservation);
        model.addAttribute("modifyReservationSuccess", false);
        model.addAttribute("flightExist", true);
        model.addAttribute("seatOccupied", false);
        return "modify_reservation";
    }

    @PostMapping ("/reservation/save_modified_reservation")
    public String saveModifiedReservation(@ModelAttribute("selectedReservation") Reservation reservation, Model model, HttpSession session) {
        User user = (User) session.getAttribute("user");
        model.addAttribute("modifyReservationSuccess", false);
        model.addAttribute("flightExist", true);
        model.addAttribute("seatOccupied", false);
        Flight flight = flightRepository.findByFromAndToAndDate(reservation.getFrom(), reservation.getTo(), reservation.getDate());
        Reservation savedReservation = reservationRepository.findById(reservation.getReservationId()).get();
        if (flight != null) {
            if (reservation.getSeatNumber() != null) {
                Seat seat = seatRepository.findByFlightIdAndSeatNumber(flight.getFlightId(), reservation.getSeatNumber());
                if (seat!= null && seat.isOccupied()) {
                    model.addAttribute("seatOccupied", true);
                } else {
                    reservationRepository.save(Reservation.builder()
                            .reservationId(savedReservation.getReservationId())
                            .userId(user.getUserId())
                            .flightId(flight.getFlightId())
                            .from(flight.getFrom())
                            .to(flight.getTo())
                            .date(flight.getDate())
                            .seatNumber(reservation.getSeatNumber()).build());
                    if (seat == null) {
                        seatRepository.save(Seat.builder().flightId(savedReservation.getFlightId()).seatNumber(reservation.getSeatNumber()).isOccupied(true).build());
                    } else {
                        seatRepository.save(Seat.builder().seatId(seat.getSeatId()).flightId(savedReservation.getFlightId()).seatNumber(reservation.getSeatNumber()).isOccupied(true).build());
                    }
                    model.addAttribute("modifyReservationSuccess", true);
                }
            } else {
                reservationRepository.save(Reservation.builder()
                        .reservationId(savedReservation.getReservationId())
                        .userId(user.getUserId())
                        .flightId(flight.getFlightId())
                        .from(flight.getFrom())
                        .to(flight.getTo())
                        .date(flight.getDate()).build());
                model.addAttribute("modifyReservationSuccess", true);
            }
        } else {
            model.addAttribute("flightExist", false);
        }
        model.addAttribute("selectedReservation", reservation);
        return "modify_reservation";
    }

    @PostMapping ("/reservation/delete_reservation")
    public String deleteReservation(@RequestParam("reservationId") Long reservationId) {
        Reservation reservation = reservationRepository.findById(reservationId).get();
        reservationRepository.deleteById(reservation.getReservationId());
        return "redirect:/reservation/reservations";
    }

}
