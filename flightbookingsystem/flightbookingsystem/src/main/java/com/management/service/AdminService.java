package com.management.service;

import com.management.model.Flight;
import com.management.repository.FlightRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@Controller
public class AdminService {

    @Autowired
    private FlightRepository flightRepository;

    @GetMapping("/admin/flight_schedules")
    public String viewFlightSchedules(Model model) {
        List<Flight> flightSchedules = flightRepository.findAll();
        model.addAttribute("flightSchedules", flightSchedules);
        return "flight_schedules_admin";

    }

    @GetMapping("/admin/add_flight")
    public String addFlightSchedule(@ModelAttribute("flight") Flight flight, Model model) {
        // check whether flight already exist
        model.addAttribute("flightExist", false);
        model.addAttribute("addFlightSuccess", false);
        model.addAttribute("flight", flight);
        return "add_flight_admin";
    }

    @PostMapping ("/admin/save_added_flight")
    public String saveAddedFlightSchedule(@ModelAttribute("flight") Flight flight, Model model) {
        // check whether flight already exist
        model.addAttribute("flightExist", false);
        model.addAttribute("addFlightSuccess", false);
        if (!flightRepository.existsByFromAndToAndDate(flight.getFrom(), flight.getTo(), flight.getDate())) {
            flightRepository.save(flight);
            model.addAttribute("addFlightSuccess", true);
        } else {
            model.addAttribute("flightExist", true);
        }
        model.addAttribute("flight", flight);
        return "add_flight_admin";
    }

    @GetMapping("/admin/delete_flight")
    public String deleteFlight(@ModelAttribute("flight") Flight flight, Model model) {
        model.addAttribute("flightExist", true);
        model.addAttribute("deleteFlightSuccess", false);
        model.addAttribute("flight", flight);
        return "delete_flight_admin";
    }

    @PostMapping("/admin/save_delete_flight")
    public String saveDeletedFlight(@ModelAttribute("flight") Flight flight, Model model) {
        // check whether flight already exist
        model.addAttribute("flightExist", true);
        model.addAttribute("deleteFlightSuccess", false);
        Flight savedFlight = flightRepository.findByFromAndToAndDate(flight.getFrom(), flight.getTo(), flight.getDate());
        if (savedFlight != null) {
            flightRepository.delete(savedFlight);
            model.addAttribute("deleteFlightSuccess", true);
        } else {
            model.addAttribute("flightExist", false);
        }
        model.addAttribute("flight", flight);
        return "delete_flight_admin";
    }

}
