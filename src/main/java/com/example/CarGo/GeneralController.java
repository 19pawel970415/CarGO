package com.example.CarGo;

import com.example.CarGo.db.PersonRepository;
import com.example.CarGo.db.UserRepository;
import com.example.CarGo.services.CarService;
import com.example.CarGo.services.ReservationService;
import com.example.CarGo.services.UserService;
import com.example.CarGo.domain.*;
import jakarta.mail.MessagingException;
import jakarta.servlet.http.HttpSession;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.core.io.FileSystemResource;
import org.springframework.format.annotation.DateTimeFormat;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.*;

import java.nio.file.Paths;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.LocalTime;
import java.util.List;
import java.util.Optional;

@Controller
public class GeneralController {

    @Autowired
    private ReservationService reservationService;
    @Autowired
    private CarService carService;
    @Autowired
    private UserService userService;
    @Autowired
    private UserRepository userRepository;
    @Autowired
    private PersonRepository personRepository;


    @GetMapping(value = {"/", "/index"})
    public String showIndex() {
        return "index";
    }
    @GetMapping("/about")
    public String showAbout() {
        return "about";
    }

    @GetMapping("/downloadFile")
    public ResponseEntity<FileSystemResource> downloadFile() {
        // Define the path to the file in your main project folder
        String filePath = "We are CarGo!.docx";

        FileSystemResource file = new FileSystemResource(Paths.get(filePath));

        if (!file.exists()) {
            return ResponseEntity.notFound().build();
        }

        // Setting headers to trigger the file download
        HttpHeaders headers = new HttpHeaders();
        headers.add(HttpHeaders.CONTENT_DISPOSITION, "attachment; filename=" + file.getFilename());

        return ResponseEntity.ok()
                .headers(headers)
                .body(file);
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
            @RequestParam(value = "yearMin", required = false) Integer yearMin,
            @RequestParam(value = "yearMax", required = false) Integer yearMax,
            @RequestParam(value = "carType", required = false) ChassisType carType,
            @RequestParam(value = "make", required = false) String make,
            @RequestParam(value = "priceMin", required = false) Double priceMin,
            @RequestParam(value = "priceMax", required = false) Double priceMax,
            @RequestParam(value = "gearbox", required = false) GearboxType gearbox,
            @RequestParam(value = "seatCount", required = false) Integer seatCount,
            @RequestParam(value = "location", required = false) String location,
            @RequestParam(value = "fuelType", required = false) FuelType fuelType,
            @RequestParam(value = "startDate", required = false) @DateTimeFormat(iso = DateTimeFormat.ISO.DATE) LocalDate startDate,
            @RequestParam(value = "endDate", required = false) @DateTimeFormat(iso = DateTimeFormat.ISO.DATE) LocalDate endDate,
            Model model) {

        List<Car> cars;

        if (startDate != null && endDate != null) {
            cars = carService.findCarsWithFilters(location, gearbox, carType, seatCount, yearMin, yearMax, priceMin, priceMax, make, fuelType, startDate, endDate);
        } else {
            cars = carService.findAllCars();
        }

        model.addAttribute("cars", cars);
        model.addAttribute("location", location);
        model.addAttribute("startDate", startDate);
        model.addAttribute("endDate", endDate);
        model.addAttribute("yearMin", yearMin);
        model.addAttribute("yearMax", yearMax);
        model.addAttribute("carType", carType);
        model.addAttribute("make", make);
        model.addAttribute("priceMin", priceMin);
        model.addAttribute("priceMax", priceMax);
        model.addAttribute("gearbox", gearbox);
        model.addAttribute("seatCount", seatCount);
        model.addAttribute("fuelType", fuelType);

        return "gallery";
    }

    @GetMapping("/book/{carId}")
    public String showBookingForm(
            @PathVariable Long carId,
            @RequestParam @DateTimeFormat(iso = DateTimeFormat.ISO.DATE) LocalDate startDate,
            @RequestParam @DateTimeFormat(iso = DateTimeFormat.ISO.DATE) LocalDate endDate,
            Model model) {
        Optional<Car> car = carService.findCarById(carId);
        if (car.isPresent()) {
            model.addAttribute("car", car.get());
            model.addAttribute("location", car.get().getLocation());
            model.addAttribute("startDate", startDate);
            model.addAttribute("endDate", endDate);
            return "book";
        } else {
            return "redirect:/gallery";
        }
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
            @RequestParam(value = "subscribe", required = false) boolean subscribeCheckbox,
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
            if (subscribeCheckbox) {
                try {
                    userService.sendSubscriptionConfirmation(email);
                } catch (MessagingException e) {
                    throw new RuntimeException(e);
                }
            }
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

        // Wyszukiwanie użytkownika na podstawie loginu w całym systemie
        Optional<Person> personOptional = personRepository.findByLogin(login);

        if (personOptional.isPresent()) {
            Person person = personOptional.get();

            // Sprawdzamy, czy hasło jest zgodne z danym użytkownikiem
            String storedPassword = null;
            if (person instanceof User) {
                storedPassword = ((User) person).getPassword();
            } else if (person instanceof Manager) {
                storedPassword = ((Manager) person).getPassword();
            } else if (person instanceof Admin) {
                storedPassword = ((Admin) person).getPassword();
            }

            if (storedPassword != null && storedPassword.equals(password)) {
                // Zapisanie użytkownika w sesji
                session.setAttribute("loggedInUser", person);
                return "redirect:/index";
            } else {
                model.addAttribute("error", "Invalid password");
            }
        } else {
            model.addAttribute("error", "User not found");
        }
        return "login";
    }

    @PostMapping("/forgot-password")
    public String processForgotPassword(
            @RequestParam("email") String email,
            Model model) {

        boolean userExists = userRepository.existsByEmail(email);

        if (userExists) {
            try {
                userService.sendPasswordResetLink(email);
            } catch (MessagingException e) {
                throw new RuntimeException(e);
            }
            model.addAttribute("message", "A password reset link has been sent to your email.");
        } else {
            model.addAttribute("error", "Email address not found. Enter a correct email address.");
        }

        return "login";
    }

    @GetMapping("/reset-password")
    public String showResetPasswordPage(@RequestParam("email") String email, HttpSession session, Model model) {
        session.setAttribute("email", email);
        model.addAttribute("email", email);
        return "password_reset";
    }


    @PostMapping("/reset-password")
    public String resetPassword(
            @RequestParam("password") String password,
            @RequestParam("confirmPassword") String confirmPassword,
            HttpSession session,
            Model model) {

        String email = (String) session.getAttribute("email");  // Odczytanie emaila z sesji
        if (email == null) {
            model.addAttribute("error", "Email not found");
            return "password_reset";  // Zwrócenie na stronę resetowania, jeśli email nie jest w sesji
        }

        if (!password.equals(confirmPassword)) {
            model.addAttribute("error", "Passwords do not match");
            return "password_reset";  // Zwracamy do strony resetowania hasła
        }

        Optional<User> userOptional = userRepository.findByEmail(email);
        if (userOptional.isPresent()) {
            User user = userOptional.get();
            user.setPassword(password);  // Ustawienie nowego hasła
            userService.updatePassword(email, password);  // Zaktualizowanie hasła w bazie
            model.addAttribute("message", "Password successfully reset");
            return "login";  // Po pomyślnym zresetowaniu hasła przekierowanie na stronę logowania
        } else {
            model.addAttribute("error", "Email not found");
            return "password_reset";  // Jeśli email nie istnieje, wracamy do formularza
        }
    }


    @GetMapping("/signout")
    public String signoutUser(HttpSession session) {
        session.invalidate();  // Usunięcie wszystkich danych z sesji
        return "redirect:/index";  // Przekierowanie na stronę główną
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

    @PostMapping("/subscribe")
    @ResponseBody
    public ResponseEntity<String> subscribe(
            @RequestParam("email") String email) {

        try {
            userService.sendSubscriptionConfirmation(email);
            return ResponseEntity.ok("Thank you for subscribing! A confirmation email has been sent to your address.");
        } catch (MessagingException e) {
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body("Failed to send subscription confirmation email. Please try again later.");
        }
    }

    @PostMapping("/sendMessage")
    public String sendMessage(
            @RequestParam("name") String name,
            @RequestParam("email") String email,
            @RequestParam("phone") String phone,
            @RequestParam("message") String message,
            Model model) {

        String subject = "Message from " + email;
        String content = "Message: " + message + "\n\nThis message was sent by " + name + " from " + email;

        try {
            userService.sendContactFormMessage("cargomailboxpl@gmail.com", subject, content);
            model.addAttribute("message", "Your message has been sent successfully!");
        } catch (MessagingException e) {
            model.addAttribute("error", "Failed to send your message. Please try again later.");
        }

        return "contact";
    }

    @GetMapping("/account_details")
    public String showAccountDetails(@RequestParam(required = false) String success, Model model) {
        if (success != null) {
            model.addAttribute("success", "Your account details have been updated successfully.");
        }
        return "account_details";
    }

    @PostMapping("/updateDetails")
    public String updateUserDetails(
            @RequestParam("firstName") String firstName,
            @RequestParam("lastName") String lastName,
            @RequestParam("email") String email,
            @RequestParam("phoneNumber") String phoneNumber,
            @RequestParam("login") String login,
            @RequestParam("password") String password,
            @RequestParam("confirmPassword") String confirmPassword,
            HttpSession session,
            Model model){

        // Pobranie aktualnie zalogowanego użytkownika z sesji
        User currentUser =  (User) session.getAttribute("loggedInUser");

        // Walidacja hasła
        if (!password.equals(confirmPassword)) {
            model.addAttribute("error", "Passwords do not match");
            return "account_details";
        }

        // Aktualizacja danych użytkownika
        currentUser.setFirstName(firstName);
        currentUser.setLastName(lastName);
        currentUser.setEmail(email);
        currentUser.setPhoneNumber(phoneNumber);
        currentUser.setLogin(login);
        currentUser.setPassword(password);

        // Zapis zmian w bazie danych
        String result = userService.updateUser(currentUser);

        if (result.equals("User updated successfully")) {
            return "redirect:/account_details?success";
        } else {
            model.addAttribute("error", result);
            return "account_details";
        }
    }

    @GetMapping("/view_reservations")
    public String showViewReservations(Model model, HttpSession session) {
        User loggedInUser = (User) session.getAttribute("loggedInUser");
        if (loggedInUser != null) {
            List<Reservation> reservations = reservationService.findReservationsByUserId(loggedInUser.getId());
            model.addAttribute("reservations", reservations);
        }
        return "view_reservations";
    }

    @PostMapping("/cancel_reservation")
    public String cancelReservation(@RequestParam("reservationId") Long reservationId, HttpSession session) {
        User loggedInUser = (User) session.getAttribute("loggedInUser");
        if (loggedInUser != null) {
            reservationService.cancelReservation(reservationId, loggedInUser.getId());
        }
        return "redirect:/view_reservations";
    }
}