package com.example.CarGo;

import com.example.CarGo.DB.UserRepository;
import com.example.CarGo.Services.CarService;
import com.example.CarGo.Services.UserService;
import com.example.CarGo.models.Car;
import com.example.CarGo.models.User;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestParam;

import java.util.List;
import java.util.Optional;

@Controller
public class IndexController {

    @Autowired
    private CarService carService;

    @Autowired
    private UserService userService;

    @Autowired
    private UserRepository userRepository;

    @GetMapping(value = {"/", "/index"})
    public String showIndex() {
        return "index";
    }

    @GetMapping("/about")
    public String showAbout() {
        return "about";
    }

    @GetMapping("/client")
    public String showClient() {
        return "client";
    }

    @GetMapping("/contact")
    public String showContact() {
        return "contact";
    }

    @GetMapping("/gallery")
    public String showGallery(Model model) {
        List<Car> cars = carService.findAllCars();
        model.addAttribute("cars", cars);
        return "gallery";
    }

    @GetMapping("/services")
    public String showServices() {
        return "services";
    }

    @GetMapping("/register")
    public String showRegister() {
        return "register";
    }

    @PostMapping("/register")
    public String registerUser(
            @RequestParam("firstName") String firstName,
            @RequestParam("lastName") String lastName,
            @RequestParam("email") String email,
            @RequestParam("phoneNumber") String phoneNumber,
            @RequestParam("login") String login,
            @RequestParam("password") String password,
            @RequestParam("confirmPassword") String confirmPassword,
            Model model) {

        // Walidacja hasła
        if (!password.equals(confirmPassword)) {
            model.addAttribute("error", "Passwords do not match");
            return "register";
        }

        User user = new User();
        user.setFirstName(firstName);
        user.setLastName(lastName);
        user.setEmail(email);
        user.setPhoneNumber(phoneNumber);
        user.setLogin(login);
        user.setPassword(password);

        // Rejestracja użytkownika
        String result = userService.registerUser(user);
        if (result.equals("User registered successfully")) {
            return "redirect:/login";
        } else {
            model.addAttribute("error", result);
            return "register";
        }
    }

    @GetMapping("/login")
    public String showLoginForm() {
        return "login";
    }

    @PostMapping("/login")
    public String loginUser(
            @RequestParam("login") String login,
            @RequestParam("password") String password,
            Model model) {

        // Wyszukiwanie użytkownika na podstawie loginu
        Optional<User> userOptional = userRepository.findByLogin(login);

        // Sprawdzenie, czy użytkownik istnieje i czy hasło jest poprawne
        if (userOptional.isPresent()) {
            User user = userOptional.get();
            if (user.getPassword().equals(password)) {
                // Przekierowanie do strony głównej po pomyślnym logowaniu
                return "redirect:/indexSignedIn";
            } else {
                model.addAttribute("error", "Invalid password");
            }
        } else {
            model.addAttribute("error", "User not found");
        }
        return "login";
    }

    @GetMapping("/indexSignedIn")
    public String showIndexSignedIn() {
        return "indexSignedIn";
    }
}
