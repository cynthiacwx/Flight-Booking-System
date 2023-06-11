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


//https://www.geeksforgeeks.org/spring-boot-thymeleaf-with-example/
//https://zetcode.com/springboot/model/
//https://www.baeldung.com/spring-boot-crud-thymeleaf
@Controller
public class LoginService {

    @Autowired
    private UserRepository userRepository;

    @GetMapping("/")
    public String viewHomePage() {
        return "index";
    }

    @GetMapping("/register/new_admin")
    public String registerAdmin(@ModelAttribute("user") User user, Model model) {
        model.addAttribute("userExist", false);
        model.addAttribute("user", user);
        return "register_admin";
    }

    @GetMapping("/register/new_customer")
    public String registerCustomer(@ModelAttribute("user") User user, Model model) {
        model.addAttribute("userExist", false);
        model.addAttribute("user", user);
        return "register_customer";
    }

    @PostMapping("/register/save_admin")
    public String saveAdminRegistration(@ModelAttribute("user") User user, Model model) {
        model.addAttribute("userExist", false);
        model.addAttribute("registerSuccess", false);
        String username = user.getUsername();
        if (!userRepository.existsByUsername(username)) {
            userRepository.save(User.builder().username(username).password(user.getPassword()).isAdmin(true).build());
        } else {
            model.addAttribute("userExist", true);
        }
        model.addAttribute("user", user);
        model.addAttribute("registerSuccess", true);
        return "register_admin";
    }

    @PostMapping("/register/save_customer")
    public String saveCustomerRegistration(@ModelAttribute("user") User user, Model model) {
        model.addAttribute("userExist", false);
        model.addAttribute("registerSuccess", false);
        String username = user.getUsername();
        if (!userRepository.existsByUsername(username)) {
            userRepository.save(User.builder().username(username).password(user.getPassword()).isAdmin(false).build());
        } else {
            model.addAttribute("userExist", true);
        }
        model.addAttribute("user", user);
        model.addAttribute("registerSuccess", false);
        return "register_customer";
    }

    @GetMapping("/login_page")
    public String loginPage(@ModelAttribute("user") User user, Model model) {
        model.addAttribute("LoginFailed", false);
        model.addAttribute("user", user);
        return "login_page";
    }

    @PostMapping("/login")
    public String login(@ModelAttribute("user") User user, Model model, HttpSession session) {
        model.addAttribute("LoginFailed", false);
        model.addAttribute("user", user);
        User savedUser = userRepository.findByUsername(user.getUsername());
        if (savedUser != null && savedUser.getPassword().equals(user.getPassword())) {
            session.setAttribute("user", savedUser);
            return savedUser.isAdmin() ? "redirect:/dashboard_admin" : "redirect:/dashboard_customer";
        } else {
            model.addAttribute("LoginFailed", true);
            return "login_page";
        }
    }

    @GetMapping("/dashboard_admin")
    public String dashboardAdmin() {
        return "dashboard_admin";
    }

    @GetMapping("/dashboard_customer")
    public String dashboardCustomer() {
        return "dashboard_customer";
    }

    @GetMapping("/logout")
    public String logout(HttpSession session) {
        session.removeAttribute("user");
        return "redirect:/";
    }

}
