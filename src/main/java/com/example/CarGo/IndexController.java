package com.example.CarGo;

import com.example.CarGo.DB.UserRepository;
import com.example.CarGo.Services.CarService;
import com.example.CarGo.Services.ReservationService;
import com.example.CarGo.Services.UserService;
import com.example.CarGo.models.Car;
import com.example.CarGo.models.Reservation;
import com.example.CarGo.models.ReservationStatus;
import com.example.CarGo.models.User;
import jakarta.servlet.http.HttpSession;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestParam;

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.LocalTime;
import java.util.List;
import java.util.Optional;

@Controller
public class IndexController {

    @Autowired
    private ReservationService reservationService;
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
    public String showGallery(
            @RequestParam(value = "location", required = false) String location,
            Model model) {
        List<Car> cars;
        if (location != null && !location.isEmpty()) {
            // Znajdź samochody według lokalizacji
            cars = carService.findCarsByLocation(location);
        } else {
            // Znajdź wszystkie samochody
            cars = carService.findAllCars();
        }
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
            HttpSession session,
            Model model) {
        // Wyszukiwanie użytkownika na podstawie loginu
        Optional<User> userOptional = userRepository.findByLogin(login);
        // Sprawdzenie, czy użytkownik istnieje i czy hasło jest poprawne
        if (userOptional.isPresent()) {
            User user = userOptional.get();
            if (user.getPassword().equals(password)) {
                // Zapisanie użytkownika w sesji
                session.setAttribute("loggedInUser", user);
                return "redirect:/index";
            } else {
                model.addAttribute("error", "Invalid password");
            }
        } else {
            model.addAttribute("error", "User not found");
        }
        return "login";
    }
    @GetMapping("/logout")
    public String logoutUser(HttpSession session) {
        session.invalidate();  // Usunięcie wszystkich danych z sesji
        return "redirect:/index";  // Przekierowanie na stronę główną
    }
    @GetMapping("/indexSignedIn")
    public String showIndexSignedIn() {
        return "indexSignedIn";
    }
    @GetMapping("/book/{carId}")
    public String showBookingForm(@PathVariable("carId") Long carId, Model model) {
        Optional<Car> car = carService.findCarById(carId);
        if (car.isPresent()) {
            model.addAttribute("car", car.get());
            return "book";
        } else {
            return "redirect:/gallery";
        }
    }

    @PostMapping("/reserve")
    public String reserveCar(
            @RequestParam("startDate") String startDate,
            @RequestParam("endDate") String endDate,
            @RequestParam("returnLocation") String returnLocation,
            @RequestParam("carId") Long carId,
            HttpSession session,
            Model model) {

        // Check for the logged-in user in the session
        User loggedInUser = (User) session.getAttribute("loggedInUser");
        if (loggedInUser == null) {
            return "redirect:/login";  // Redirect to login if not authenticated
        }

        // Find the car by ID
        Optional<Car> carOptional = carService.findCarById(carId);
        if (!carOptional.isPresent()) {
            model.addAttribute("error", "Car not found");
            return "book";  // Redirect back to booking page if car is not found
        }

        // Create and set up reservation data
        Car car = carOptional.get();
        Reservation reservation = new Reservation();

        // Set reservation fields
        reservation.setId(null); // Ensure ID is generated automatically if using auto-generated IDs
        reservation.setReservationStart(LocalDateTime.of(LocalDate.parse(startDate), LocalTime.MIN));
        reservation.setReservationEnd(LocalDateTime.of(LocalDate.parse(endDate), LocalTime.MAX));
        reservation.setStatus(ReservationStatus.PENDING); // Set initial status as per enum or String
        reservation.setCar(car);
        reservation.setUser(loggedInUser);
        reservation.setPickUpPoint(car.getLocation());
        reservation.setDropOfPoint(returnLocation);

        // Save reservation
        reservationService.saveReservation(reservation);

        // Redirect to a confirmation page or any other relevant page
        return "redirect:/confirmation";
    }

    @GetMapping("/confirmation")
    public String showConfirmation() {
        return "confirmation";
    }
}