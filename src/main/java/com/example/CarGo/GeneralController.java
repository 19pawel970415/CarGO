package com.example.CarGo;

import com.example.CarGo.db.*;
import com.example.CarGo.services.*;
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
import org.springframework.web.multipart.MultipartFile;
import org.springframework.web.servlet.mvc.support.RedirectAttributes;

import java.nio.file.Paths;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.LocalTime;
import java.time.format.DateTimeFormatter;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.Optional;
import java.util.stream.Collectors;

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
    @Autowired
    private LocationRepository locationRepository;
    @Autowired
    private LocationService locationService;
    @Autowired
    private CarMakeService carMakeService;
    @Autowired
    private SeatCountService seatCountService;
    @Autowired
    private PersonService personService;
    @Autowired
    private AdminService adminService;
    @Autowired
    private ManagerService managerService;
    @Autowired
    private ManagerRepository managerRepository;
    @Autowired
    private AdminRepository adminRepository;
    @Autowired
    private CarReportService carReportService;


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
            @RequestParam(value = "seatCountId", required = false) Long seatCountId,
            @RequestParam(value = "location", required = false) String location,
            @RequestParam(value = "fuelType", required = false) FuelType fuelType,
            @RequestParam(value = "startDate", required = false) @DateTimeFormat(iso = DateTimeFormat.ISO.DATE) LocalDate startDate,
            @RequestParam(value = "endDate", required = false) @DateTimeFormat(iso = DateTimeFormat.ISO.DATE) LocalDate endDate,
            Model model) {

        List<Location> locations = locationRepository.findAll();
        List<SeatCount> seatCounts = seatCountService.findAllSeatCounts();

        // Validate price range
        if (priceMin != null && priceMax != null && priceMin > priceMax) {
            model.addAttribute("priceError", "Min price cannot be greater than max price.");
            return "gallery"; // Return the same page with an error message
        }

        // Validate year range
        if (yearMin != null && yearMax != null && yearMin > yearMax) {
            model.addAttribute("yearError", "Min year cannot be greater than max year.");
            return "gallery"; // Return the same page with an error message
        }

        // Validate startDate and endDate on the server side
        if (startDate != null && startDate.isBefore(LocalDate.now())) {
            model.addAttribute("dateError", "Start date cannot be in the past.");
            return "gallery";  // Return the same page with an error message
        }

        if (startDate != null && endDate != null && startDate.isAfter(endDate)) {
            model.addAttribute("dateError", "Start date cannot be after the end date.");
            return "gallery";  // Return the same page with an error message
        }

        List<Car> cars;
        if (startDate != null && endDate != null) {
            cars = carService.findCarsWithFilters(new Location(location), gearbox, carType, seatCountId, yearMin, yearMax, priceMin, priceMax, make, fuelType, startDate, endDate);
        } else {
            cars = carService.findAllCars();
        }

        List<String> carMakes = carMakeService.findAllCarMakes().stream()
                .map(cm -> cm.getName())
                .collect(Collectors.toList());

        model.addAttribute("cars", cars);
        model.addAttribute("locations", locations);
        model.addAttribute("carMakes", carMakes);
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
        model.addAttribute("seatCounts", seatCounts);
        model.addAttribute("seatCountId", seatCountId);
        model.addAttribute("fuelType", fuelType);

        return "gallery";
    }

    @PostMapping("/car-make/add")
    @ResponseBody
    public ResponseEntity<String> addCarMake(@RequestBody Map<String, String> request) {
        String make = request.get("make");

        if (make == null || make.trim().isEmpty()) {
            return ResponseEntity.badRequest().body("Car Make cannot be empty");
        }

        carMakeService.addCarMake(make.trim());
        return ResponseEntity.ok("Car Make added successfully");
    }

    @PostMapping("/delete-car-make")
    @ResponseBody
    public ResponseEntity<String> deleteCarMake(@RequestBody CarMakeRequest carMakeRequest) {
        String carMakeName = carMakeRequest.getMake(); // Assuming the request contains a carMake property

        // Call the service method to delete the car make
        boolean deleted = carMakeService.deleteCarMake(carMakeName);

        if (deleted) {
            return ResponseEntity.ok("Car Make deleted successfully.");
        } else {
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR)
                    .body("Error deleting Car Make. At least one of the cars or reservations is assigned to this Car Make. Delete the car(s) and/or the reservation(s) first.");
        }
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
            model.addAttribute("location", car.get().getLocation().getCity());
            model.addAttribute("startDate", startDate);
            model.addAttribute("endDate", endDate);
            return "book";
        } else {
            return "redirect:/gallery";
        }
    }

    @PostMapping("/add-location")
    @ResponseBody
    public ResponseEntity<String> addLocation(@RequestBody LocationRequest locationRequest) {
        String locationName = locationRequest.getLocation();

        // Wywołaj metodę serwisową, aby dodać lokalizację
        boolean added = locationService.addLocation(locationName);

        if (added) {
            return ResponseEntity.ok("Location added successfully.");
        } else {
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body("Error adding location.");
        }
    }

    @PostMapping("/delete-location")
    @ResponseBody
    public ResponseEntity<String> deleteLocation(@RequestBody LocationRequest locationRequest) {
        String locationName = locationRequest.getLocation();

        // Wywołaj metodę serwisową, aby usunąć lokalizację
        boolean deleted = locationService.deleteLocation(locationName);

        if (deleted) {
            return ResponseEntity.ok("Location deleted successfully.");
        } else {
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body("Error deleting location.");
        }
    }

    @PostMapping("/add-seat-count")
    @ResponseBody
    public ResponseEntity<String> addSeatCount(@RequestBody SeatCountRequest seatCountRequest) {
        Long seatCountId = seatCountRequest.getSeatCountId();

        // Wywołaj metodę serwisową, aby dodać liczbę miejsc
        boolean added = seatCountService.addSeatCount(seatCountId);

        if (added) {
            return ResponseEntity.ok("Seat count added successfully.");
        } else {
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body("Error adding seat count.");
        }
    }

    @PostMapping("/delete-seat-count")
    @ResponseBody
    public ResponseEntity<String> deleteSeatCount(@RequestBody SeatCountRequest seatCountRequest) {
        Long seatCountId = seatCountRequest.getSeatCountId(); // Assuming the request contains a seatCountId property

        // Wywołaj metodę serwisową, aby usunąć Seat Count
        boolean deleted = seatCountService.deactivateSeatCount(seatCountId);

        if (deleted) {
            System.out.println("Skasjfnnkasjbfkaskjasbnvfkja");
            return ResponseEntity.ok("Seat Count deleted successfully.");
        } else {
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR)
                    .body("Error deleting Seat Count. It might be assigned to an active reservation.");
        }
    }



    @GetMapping("/services")
    public String getCarsBeforeService(Model model) {
        List<Car> allCars = carService.findAllCars();
        List<Car> carsBeforeInAfterService = allCars.stream()
                .filter(c -> (c.getStatus() == CarStatus.BEFORE_SERVICE || c.getStatus() == CarStatus.IN_SERVICE || c.getStatus() == CarStatus.SERVICED))
                .collect(Collectors.toList());
        model.addAttribute("carsBeforeService", carsBeforeInAfterService);
        return "services";
    }

    @PostMapping("/service/ready/{id}")
    public String setCarReadyForRent(@PathVariable Long id) {
        carService.setCarReadyForRent(id);
        return "redirect:/services";
    }

    @PostMapping("/service/start/{id}")
    public String startService(@PathVariable Long id) {
        carService.changeStatusToInServiceAndWait(id);
        try {
            Thread.sleep(3000);
        } catch (InterruptedException e) {
            e.printStackTrace();
        }
        return "redirect:/services";
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
        reservation.setDropOfPoint(new Location(returnLocation));

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
            Model model) {

        // Pobranie aktualnie zalogowanego użytkownika z sesji
        User currentUser = (User) session.getAttribute("loggedInUser");

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

    @GetMapping("/personnel_management")
    public String showPersonnelManagement(Model model) {
        // Pobierz listy użytkowników, menedżerów i administratorów
        List<Person> users = personService.getAllUsers();
        List<Person> managers = personService.getAllManagers();
        List<Person> admins = personService.getAllAdmins();

        // Przekaż dane do widoku
        model.addAttribute("users", users);
        model.addAttribute("managers", managers);
        model.addAttribute("admins", admins);

        return "personnel_management";
    }

    @GetMapping("/addAdmin")
    public String showAddAdmin() {
        return "addAdmin";
    }

    @PostMapping("/registerAdmin")
    public String registerAdmin(
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
            return "addAdmin";
        }
        Admin admin = new Admin();
        admin.setFirstName(firstName);
        admin.setLastName(lastName);
        admin.setEmail(email);
        admin.setPhoneNumber(phoneNumber);
        admin.setLogin(login);
        admin.setPassword(password);
        // Rejestracja użytkownika
        String result = adminService.registerAdmin(admin);

        if (result.equals("Admin registered successfully")) {
            return "redirect:/personnel_management";
        } else {
            model.addAttribute("error", result);
            return "addAdmin";
        }
    }

    @GetMapping("/addManager")
    public String showAddManager() {
        return "addManager";
    }

    @PostMapping("/registerManager")
    public String registerManager(
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
            return "addManager";
        }
        Manager manager = new Manager();
        manager.setFirstName(firstName);
        manager.setLastName(lastName);
        manager.setEmail(email);
        manager.setPhoneNumber(phoneNumber);
        manager.setLogin(login);
        manager.setPassword(password);
        // Rejestracja użytkownika
        String result = managerService.registerManager(manager);

        if (result.equals("Manager registered successfully")) {
            return "redirect:/personnel_management";
        } else {
            model.addAttribute("error", result);
            return "addManager";
        }
    }

    @GetMapping("/addUser")
    public String showAddUser() {
        return "addUser";
    }

    @PostMapping("/registerUser")
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
            return "addUser";
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
            return "redirect:/personnel_management";
        } else {
            model.addAttribute("error", result);
            return "addUser";
        }
    }
    @PostMapping("/deleteUser")
    public String deleteUser(@RequestParam("userId") Long userId, RedirectAttributes redirectAttributes) {
        boolean result = userService.deleteUserById(userId);

        if (result) {
            redirectAttributes.addFlashAttribute("success", "User deleted successfully");
        } else {
            redirectAttributes.addFlashAttribute("error", "Failed to delete user");
        }
        return "redirect:/personnel_management";
    }
    @PostMapping("/deleteManager")
    public String deleteManager(@RequestParam("managerId") Long managerId, RedirectAttributes redirectAttributes) {
        boolean result = managerService.deleteManagerById(managerId);

        if (result) {
            redirectAttributes.addFlashAttribute("success", "Manager deleted successfully");
        } else {
            redirectAttributes.addFlashAttribute("error", "Failed to delete user");
        }
        return "redirect:/personnel_management";
    }
    @PostMapping("/deleteAdmin")
    public String deleteAdmin(@RequestParam("adminId") Long adminId, RedirectAttributes redirectAttributes) {
        boolean result = adminService.deleteAdminById(adminId);

        if (result) {
            redirectAttributes.addFlashAttribute("success", "Admin deleted successfully");
        } else {
            redirectAttributes.addFlashAttribute("error", "Failed to delete user");
        }
        return "redirect:/personnel_management";
    }
    @GetMapping("/car/{id}")
    @ResponseBody  // This annotation will return the response as JSON
    public CarRequest getCar(@PathVariable Long id) {
        // Get car by ID
        CarRequest carRequest = carService.getCarById(id);

        // Return the car details as JSON
        return carRequest;
    }

    @PutMapping("/car/update")
    @ResponseStatus(HttpStatus.OK)
    public void updateCar(@RequestBody CarUpdateRequest request) {
        carService.updateCar(request);
    }

    @PostMapping("/car/add")
    @ResponseStatus(HttpStatus.CREATED)
    public void addCar(@RequestPart("carData") CarAddRequest request,
                       @RequestPart("image") MultipartFile image) {
        carService.addCar(request, image);
    }

    @RestController
    @RequestMapping("/api")
    public class SuggestionController {

        private final CarMakeRepository carMakeRepository;
        private final LocationRepository locationRepository;

        public SuggestionController(CarMakeRepository carMakeRepository, LocationRepository locationRepository) {
            this.carMakeRepository = carMakeRepository;
            this.locationRepository = locationRepository;
        }

        @GetMapping("/car-makes")
        public List<String> getCarMakes(@RequestParam("query") String query) {
            return carMakeRepository.findByNameContainingIgnoreCase(query)
                    .stream()
                    .map(CarMake::getName)
                    .toList();
        }

        @GetMapping("/locations")
        public List<String> getLocations(@RequestParam("query") String query) {
            return locationRepository.findByCityContainingIgnoreCase(query)
                    .stream()
                    .map(Location::getCity)
                    .toList();
        }
    }


    @DeleteMapping("/car/deleteCar/{id}")
    @ResponseBody
    public ResponseEntity<String> deleteCar(@PathVariable Long id) {
        try {
            if (id == null) {
                return ResponseEntity.status(HttpStatus.BAD_REQUEST).body("Invalid car ID");
            }
            carService.deleteCar(id);
            return ResponseEntity.ok("Car deleted successfully!");
        } catch (IllegalStateException e) {
            return ResponseEntity.status(HttpStatus.CONFLICT).body(e.getMessage());
        } catch (Exception e) {
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR)
                    .body("Failed to delete car: " + e.getMessage());
        }
    }

    @PostMapping("/updateManager")
    public String updateManager(
            @RequestParam("id") Long id,
            @RequestParam("firstName") String firstName,
            @RequestParam("lastName") String lastName,
            @RequestParam("email") String email,
            @RequestParam("phoneNumber") String phoneNumber,
            @RequestParam("login") String login,
            @RequestParam("password") String password,
            @RequestParam("confirmPassword") String confirmPassword,
            Model model) {

        if (!password.equals(confirmPassword)) {
            model.addAttribute("error", "Passwords do not match");
            return "editManager";
        }

        // Znajdź menedżera i zaktualizuj dane
        Manager manager = managerRepository.findById(id)
                .orElseThrow(() -> new IllegalArgumentException("Manager not found"));

        manager.setFirstName(firstName);
        manager.setLastName(lastName);
        manager.setEmail(email);
        manager.setPhoneNumber(phoneNumber);
        manager.setLogin(login);
        manager.setPassword(password);

        // Zapisz zmiany
        String result = managerService.updateManager(manager);

        if (result.equals("Manager updated successfully")) {
            return "redirect:/personnel_management?success";
        } else {
            model.addAttribute("error", result);
            return "editManager";
        }
    }

    @GetMapping("/editManager/{id}")
    public String editManager(@PathVariable Long id, Model model) {
        // Pobierz menedżera z bazy danych
        Manager manager = managerRepository.findById(id)
                .orElseThrow(() -> new IllegalArgumentException("Manager not found"));

        // Dodaj menedżera do modelu
        model.addAttribute("manager", manager);

        // Wyświetl stronę edycji
        return "editManager";
    }

    @PostMapping("/updateAdmin")
    public String updateAdmin(
            @RequestParam("id") Long id,
            @RequestParam("firstName") String firstName,
            @RequestParam("lastName") String lastName,
            @RequestParam("email") String email,
            @RequestParam("phoneNumber") String phoneNumber,
            @RequestParam("login") String login,
            @RequestParam("password") String password,
            @RequestParam("confirmPassword") String confirmPassword,
            Model model) {

        if (!password.equals(confirmPassword)) {
            model.addAttribute("error", "Passwords do not match");
            return "editAdmin";
        }

        Admin admin = adminRepository.findById(id)
                .orElseThrow(() -> new IllegalArgumentException("Admin not found"));

        admin.setFirstName(firstName);
        admin.setLastName(lastName);
        admin.setEmail(email);
        admin.setPhoneNumber(phoneNumber);
        admin.setLogin(login);
        admin.setPassword(password);

        String result = adminService.updateAdmin(admin);

        if (result.equals("Admin updated successfully")) {
            return "redirect:/personnel_management?success";
        } else {
            model.addAttribute("error", result);
            return "editAdmin";
        }
    }

    @GetMapping("/editAdmin/{id}")
    public String editAdmin(@PathVariable Long id, Model model) {
        Admin admin = adminRepository.findById(id)
                .orElseThrow(() -> new IllegalArgumentException("Manager not found"));

        model.addAttribute("admin", admin);

        return "editAdmin";
    }

    @PostMapping("/updateUser")
    public String updateUser(
            @RequestParam("id") Long id,
            @RequestParam("firstName") String firstName,
            @RequestParam("lastName") String lastName,
            @RequestParam("email") String email,
            @RequestParam("phoneNumber") String phoneNumber,
            @RequestParam("login") String login,
            @RequestParam("password") String password,
            @RequestParam("confirmPassword") String confirmPassword,
            Model model) {

        if (!password.equals(confirmPassword)) {
            model.addAttribute("error", "Passwords do not match");
            return "editUser";
        }

        User user = userRepository.findById(id)
                .orElseThrow(() -> new IllegalArgumentException("User not found"));

        user.setFirstName(firstName);
        user.setLastName(lastName);
        user.setEmail(email);
        user.setPhoneNumber(phoneNumber);
        user.setLogin(login);
        user.setPassword(password);

        String result = userService.updateUser(user);

        if (result.equals("User updated successfully")) {
            return "redirect:/personnel_management?success";
        } else {
            model.addAttribute("error", result);
            return "editUser";
        }
    }

    @GetMapping("/editUser/{id}")
    public String editUser(@PathVariable Long id, Model model) {
        User user = userRepository.findById(id)
                .orElseThrow(() -> new IllegalArgumentException("User not found"));

        model.addAttribute("user", user);

        return "editUser";
    }

    @GetMapping("/reports")
    public String getCarReport(Model model) {
        Map<String, Long> carCountByLocation = carReportService.getCarsCountByLocation();
        model.addAttribute("carCountByLocation", carCountByLocation);
        return "reports";
    }

    @GetMapping("/manageReservations")
    public String showManageReservations(Model model, HttpSession session) {
        Manager loggedInManager = (Manager) session.getAttribute("loggedInUser");
        if (loggedInManager != null) {
            List<Reservation> allReservations = reservationService.findAllReservations();
            model.addAttribute("reservations", allReservations);
        }
        return "manageReservations";
    }

    @PostMapping("/delete_reservation")
    public String deleteReservation(@RequestParam Long reservationId) {
        reservationService.deleteReservation(reservationId);
        return "redirect:/manageReservations";
    }

    @PostMapping("/update_reservation_status")
    public String updateReservationStatus(@RequestParam Long reservationId, @RequestParam String status) {
        reservationService.updateReservationStatus(reservationId, ReservationStatus.valueOf(status));
        return "redirect:/manageReservations";
    }

    @GetMapping("/stats")
    public String getStats(@RequestParam(value = "startDate", required = false) String startDate,
                           @RequestParam(value = "endDate", required = false) String endDate,
                           Model model,
                           RedirectAttributes redirectAttributes) {


        DateTimeFormatter formatter = DateTimeFormatter.ofPattern("yyyy-MM-dd");

        // Validation: Missing or empty dates
        if (startDate == null || endDate == null || startDate.isEmpty() || endDate.isEmpty()) {
            model.addAttribute("errorMessage", "Proszę uzupełnić oba pola daty.");
            return "stats"; // Return the stats page without redirecting
        }

        LocalDateTime start = LocalDate.parse(startDate, formatter).atStartOfDay();
        LocalDateTime end = LocalDate.parse(endDate, formatter).atStartOfDay();

        // Pobierz dane z serwisu
        List<Map.Entry<Car, Long>> mostRentedCars = reservationService.getMostRentedCars(start, end);
        List<Map.Entry<FuelType, Long>> fuelTypeRanking = reservationService.getFuelTypeRanking(start, end);
        List<Map.Entry<Reservation, Double>> reservationsWithEarnings = reservationService.getReservationsWithEarnings(start, end);

        // Przygotowanie danych do wykresu
        List<String> fuelTypes = new ArrayList<>();
        List<Long> fuelCounts = new ArrayList<>();
        for (Map.Entry<FuelType, Long> entry : fuelTypeRanking) {
            fuelTypes.add(entry.getKey().toString()); // np. Diesel, Benzyna
            fuelCounts.add(entry.getValue()); // Liczba wynajęć
        }

        // Dodaj dane do modelu
        model.addAttribute("mostRentedCars", mostRentedCars);
        model.addAttribute("fuelTypeRanking", fuelTypeRanking);
        model.addAttribute("reservationsWithEarnings", reservationsWithEarnings);
        model.addAttribute("fuelTypes", fuelTypes);
        model.addAttribute("fuelCounts", fuelCounts);

        return "stats"; // Nazwa widoku HTML (statystyki.html)
    }
}