package com.management.service;

import com.management.model.User;
import com.management.repository.UserRepository;
import jakarta.servlet.http.HttpSession;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.ModelAttribute;
import org.springframework.web.bind.annotation.PostMapping;

@Controller
public class UserProfileService {
    @Autowired
    private UserRepository userRepository;


    @GetMapping("/customer/profile")
    public String viewProfile(HttpSession session, Model model) {
        User user = (User) session.getAttribute("user");
        User savedUser = userRepository.findByUsername(user.getUsername());
        model.addAttribute("user", savedUser);
        model.addAttribute("updateSuccess", false);
        return "user_profile";
    }

    @PostMapping("/customer/save_profile")
    public String saveProfile(@ModelAttribute("user") User user, Model model) {
        User savedUser = userRepository.findByUsername(user.getUsername());
        userRepository.save(User.builder()
                .userId(savedUser.getUserId())
                .username(savedUser.getUsername())
                .password(user.getPassword())
                .firstName(user.getFirstName())
                .lastName(user.getLastName())
                .email(user.getEmail())
                .dob(user.getDob())
                .phone(user.getPhone())
                .address(user.getAddress())
                .isAdmin(false).build());
        model.addAttribute("user", user);
        model.addAttribute("updateSuccess", true);
        return "user_profile";
    }

}
