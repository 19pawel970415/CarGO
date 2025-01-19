package com.example.CarGo.services;


import com.example.CarGo.db.*;
import com.example.CarGo.domain.*;
import jakarta.mail.MessagingException;
import jakarta.mail.internet.MimeMessage;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.mail.javamail.JavaMailSender;
import org.springframework.mail.javamail.MimeMessageHelper;
import org.springframework.scheduling.annotation.Async;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.multipart.MultipartFile;
import org.springframework.web.util.UriComponentsBuilder;

import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.sql.SQLDataException;
import java.time.LocalDate;


import java.time.LocalDateTime;
import java.util.List;
import java.util.Objects;
import java.util.Optional;
import java.util.Random;
import java.util.stream.Collectors;

@Service
public class CarService {

    @Autowired
    private CarRepository carRepository;

    @Autowired
    private final ReservationRepository reservationRepository;

    @Autowired
    private final LocationRepository locationRepository;

    @Autowired
    private final CarMakeRepository carMakeRepository;

    @Autowired
    private final SeatCountRepository seatCountRepository;
    @Autowired
    private JavaMailSender javaMailSender;

    public CarService(ReservationRepository reservationRepository, LocationRepository locationRepository, CarMakeRepository carMakeRepository, SeatCountRepository seatCountRepository, JavaMailSender javaMailSender) {
        this.reservationRepository = reservationRepository;
        this.locationRepository = locationRepository;
        this.carMakeRepository = carMakeRepository;
        this.seatCountRepository = seatCountRepository;
        this.javaMailSender = javaMailSender;
    }

    public List<Car> findAllCars() {
        return carRepository.findAll();
    }

    public List<Car> findAllReadyForRentCars() {
        return carRepository.findAll().stream()
                .filter(car -> car.getStatus() == CarStatus.READY_FOR_RENT)
                .collect(Collectors.toList());
    }

    public List<Car> findCarsByLocation(Location location) {
        return carRepository.findByLocation(location);
    }

    public Optional<Car> findCarById(Long id) {
        return carRepository.findById(id);
    }

    public List<Car> findAvailableCars(LocalDate startDate, LocalDate endDate) {
        return carRepository.findAvailableCars(startDate.atStartOfDay(), endDate.atTime(23, 59, 59));
    }

    public List<Car> findAvailableCarsInLocation(Location location, LocalDate startDate, LocalDate endDate) {
        return carRepository.findAvailableCarsInLocation(location.getCity(), startDate.atStartOfDay(),
                endDate.atTime(23, 59, 59));
    }

    public List<Car> findCarsWithFilters(Location location,
                                         GearboxType gearboxType,
                                         ChassisType chassisType,
                                         Long seatCount,
                                         Integer yearMin,
                                         Integer yearMax,
                                         Double priceMin,
                                         Double priceMax,
                                         String make,
                                         FuelType fuelType,
                                         LocalDate startDate,
                                         LocalDate endDate) {
        return carRepository.findCarsWithFilters(
                        location.getCity(),
                        gearboxType,
                        chassisType,
                        seatCount,
                        yearMin,
                        yearMax,
                        priceMin,
                        priceMax,
                        make,
                        fuelType,
                        startDate.atStartOfDay(),
                        endDate.atTime(23, 59, 59))
                .stream()
                .filter(car -> car.getStatus() == CarStatus.READY_FOR_RENT)
                .collect(Collectors.toList());
    }

    public void setCarReadyForRent(Long carId) {
        Optional<Car> carOptional = carRepository.findById(carId);
        if (carOptional.isPresent()) {
            Car car = carOptional.get();
            car.setStatus(CarStatus.READY_FOR_RENT);
            carRepository.save(car);
        }
    }

    @Async
    public void changeStatusToInServiceAndWait(Long carId) {
        Optional<Car> carOptional = carRepository.findById(carId);
        if (carOptional.isPresent()) {
            Car car = carOptional.get();
            car.setStatus(CarStatus.IN_SERVICE);
            carRepository.save(car);

            try {
                // set to a minute just as an example
                Thread.sleep(60000);
            } catch (InterruptedException e) {
                e.printStackTrace();
            }

            car.setStatus(CarStatus.SERVICED);
            carRepository.save(car);
        }
    }

    public void updateCarStatuses() {
        LocalDateTime today = LocalDateTime.now()
                .withHour(0)
                .withMinute(0)
                .withSecond(0)
                .withNano(0);

        List<Reservation> reservationsEndingToday = reservationRepository.findByReservationEnd(today);

        List<Car> carsToUpdate = reservationsEndingToday.stream()
                .map(Reservation::getCar)
                .filter(Objects::nonNull)
                .collect(Collectors.toList());

        carsToUpdate.forEach(car -> car.setStatus(CarStatus.BEFORE_SERVICE));

        carRepository.saveAll(carsToUpdate);
    }

    public CarRequest getCarById(Long id) {
        Car car = carRepository.findById(id)
                .orElseThrow(() -> new IllegalArgumentException("Car not found"));

        return new CarRequest(
                car.getId(),
                car.getMake(),
                car.getModel(),
                car.getRegistrationNumber(),
                car.getVin(),
                car.getYearOfProduction(),
                car.getChassisType(),
                car.getGearboxType(),
                car.getFuelType(),
                car.getSeatCount(),
                car.getPricePerDay(),
                car.getLocation(),
                car.getStatus()
        );
    }

    @Transactional
    public void updateCar(CarUpdateRequest request) {
        Car car = carRepository.findById(request.getId())
                .orElseThrow(() -> new IllegalArgumentException("Car not found"));

        if (request.getLocation() != null) {
            Location location = locationRepository.findByCity(request.getLocation().getCity())
                    .orElseGet(() -> locationRepository.save(new Location(request.getLocation().getCity())));

            car.setLocation(location);
        }

        car.setRegistrationNumber(request.getRegistrationNumber());
        car.setPricePerDay(request.getPricePerDay());

        carRepository.save(car);
    }

    @Transactional
    public void addCar(CarAddRequest request, MultipartFile image) {
        // Walidacja unikalności VIN i numeru rejestracyjnego
        try {
            validateCarUniqueness(request);
        } catch (SQLDataException e) {
            throw new RuntimeException(e);
        }

        // Znalezienie lub utworzenie lokalizacji
        Location location = findOrCreateLocation(request);

        // Sprawdzamy istnienie marki samochodu w bazie
        CarMake carMake = carMakeRepository.findByName(request.getMake().getName())
                .orElseGet(() -> createNewCarMake(request.getMake().getName()));

        if (request.getYearOfProduction() < 1900 || request.getYearOfProduction() > LocalDate.now().getYear()) {
            throw new IllegalArgumentException("Year of production must be between 1900 and the current year.");
        }

        // Znalezienie SeatCount na podstawie liczby foteli
        SeatCount seatCount = seatCountRepository.findByCount(request.getSeatCount().getId())
                .orElseThrow(() -> new IllegalArgumentException("Seat count not found"));

        // Zmiana dostępności na true
        seatCount.setAvailable(true);
        seatCountRepository.save(seatCount);  // Zapisanie zmiany dostępności foteli

        // Tworzenie samochodu
        Car car = new Car();
        car.setMake(carMake);  // Używamy znalezionej lub nowo utworzonej marki
        car.setModel(request.getModel());
        car.setRegistrationNumber(request.getRegistrationNumber());
        car.setVin(request.getVin());
        car.setYearOfProduction(request.getYearOfProduction());
        car.setChassisType(request.getChassisType());
        car.setGearboxType(request.getGearboxType());
        car.setFuelType(request.getFuelType());
        car.setSeatCount(seatCount);  // Przypisanie zaktualizowanego SeatCount
        car.setPricePerDay(request.getPricePerDay());
        car.setLocation(location);
        car.setStatus(CarStatus.READY_FOR_RENT);

        carRepository.save(car);

        saveCarImage(image, car.getId());
    }

    // Metoda pomocnicza do tworzenia nowej marki samochodu, jeśli nie istnieje
    private CarMake createNewCarMake(String makeName) {
        CarMake newCarMake = new CarMake();
        newCarMake.setName(makeName);
        return carMakeRepository.save(newCarMake);
    }

    // Metoda do znalezienia lub utworzenia lokalizacji
    private Location findOrCreateLocation(CarAddRequest request) {
        return locationRepository.findByCity(request.getLocation().getCity())
                .orElseGet(() -> locationRepository.save(new Location(request.getLocation().getCity())));
    }


    private void validateCarUniqueness(CarAddRequest request) throws SQLDataException {
        if (carRepository.existsByVin(request.getVin())) {
            throw new SQLDataException("Car with the same VIN already exists.");
        }
        if (carRepository.existsByRegistrationNumber(request.getRegistrationNumber())) {
            throw new SQLDataException("Car with the same Registration Number already exists.");
        }
    }

    public void deleteCar(Long carId) {
        if (hasActiveOrPendingReservations(carId)) {
            throw new IllegalStateException("You cannot delete this car as it is either rented now or booked for the future!");
        }

        List<Car> allCars = carRepository.findAll();

        // Pobranie marki samochodu
        String markName = carRepository.findById(carId).get().getMake().getName();

        // Liczba samochodów danej marki
        long carsWithTheSameMake = allCars.stream()
                .filter(c -> c.getMake().getName().equals(markName))
                .count();

        // Pobranie lokalizacji powiązanej z danym samochodem
        String cityName = carRepository.findById(carId).get().getLocation().getCity();

        // Liczba samochodów w tej samej lokalizacji
        long carsInTheSameLocation = allCars.stream()
                .filter(c -> c.getLocation().getCity().equals(cityName))
                .count();

        // Pobranie SeatCount powiązanego z samochodem
        Long seatCountId = carRepository.findById(carId).get().getSeatCount().getId();

        // Liczba samochodów z tym samym SeatCount
        long carsWithTheSameSeatCount = allCars.stream()
                .filter(c -> c.getSeatCount().getId() == seatCountId)
                .count();

        // Usunięcie samochodu
        carRepository.deleteById(carId);

        // Usunięcie marki, jeśli nie ma innych samochodów tej marki
        if (carsWithTheSameMake <= 1) {
            carMakeRepository.deleteById(carMakeRepository.findByName(markName).get().getId());
        }

        // Usunięcie lokalizacji, jeśli nie ma innych samochodów w tej lokalizacji
        if (carsInTheSameLocation <= 1) {
            locationRepository.deleteById(locationRepository.findByCity(cityName).get().getId());
        }


        // Usunięcie SeatCount, jeśli nie ma innych samochodów z tym samym SeatCount
        if (carsWithTheSameSeatCount <= 1) {
            Optional<SeatCount> seatCountbyId = seatCountRepository.findById(seatCountId);
            seatCountbyId.get().setAvailable(false);
            seatCountRepository.save(seatCountbyId.get());
        }

        // Usunięcie obrazu samochodu
        deleteCarImage(carId);
    }

    private void saveCarImage(MultipartFile image, long carNumber) {
        if (image.isEmpty()) {
            throw new IllegalArgumentException("Uploaded image cannot be empty");
        }

        String uploadDir = "C:/Users/48721/IdeaProjects/CarGo/src/main/resources/static/images/";
        String fileName = carNumber + ".jpg"; // Możesz dostosować rozszerzenie

        Path filePath = Paths.get(uploadDir + fileName);
        try {
            Files.createDirectories(filePath.getParent()); // Upewnij się, że katalog istnieje
            image.transferTo(filePath.toFile());
        } catch (IOException e) {
            throw new RuntimeException("Failed to save image: " + e.getMessage(), e);
        }

        try {
            Runtime.getRuntime().exec(new String[]{"git", "add", filePath.toString()});
        } catch (IOException e) {
            throw new RuntimeException(e);
        }
    }

    private void deleteCarImage(Long carId) {
        // Ścieżka do obrazu samochodu
        String uploadDir = "C:/Users/48721/IdeaProjects/CarGo/src/main/resources/static/images/";
        String fileName = carId + ".jpg"; // Przy założeniu, że obrazy mają rozszerzenie .jpg
        Path filePath = Paths.get(uploadDir + fileName);

        // Usuwanie pliku, jeśli istnieje
        try {
            Files.deleteIfExists(filePath);
        } catch (IOException e) {
            throw new RuntimeException("Failed to delete image: " + e.getMessage(), e);
        }
    }

    public boolean hasActiveOrPendingReservations(Long carId) {
        // Pobierz wszystkie rezerwacje dla danego samochodu
        long numberOfReservationOnTheCar = reservationRepository.findAll().stream()
                .filter(r -> r.getCar().getId() == carId)
                .filter(r -> r.getStatus() == ReservationStatus.ACTIVE || r.getStatus() == ReservationStatus.PENDING)
                .count();

        if (numberOfReservationOnTheCar > 0) {
            return true;
        } else {
            return false;
        }
    }

    public void sendEmailRequestingExtraCharge(Long carId, Double extraCharge) throws MessagingException {
        LocalDateTime today = LocalDateTime.now().withHour(0).withMinute(0).withSecond(0).withNano(0);
        MimeMessage message = javaMailSender.createMimeMessage();
        MimeMessageHelper helper = new MimeMessageHelper(message, true);

        // Pobranie rezerwacji i użytkownika
        Optional<Reservation> reservation = reservationRepository.findAll().stream()
                .filter(r -> r.getCar().getId().equals(carId))
                .filter(r -> r.getReservationEnd().isEqual(today))
                .findFirst();

        if (reservation.isEmpty()) {
            throw new IllegalArgumentException("Cannot find reservation for car ID: " + carId);
        }

        Person user = reservation.get().getUser();
        Optional<Car> car = carRepository.findById(carId);
        Car theCar = car.get();

        if (car.isEmpty()) {
            throw new IllegalArgumentException("Cannot find car details for car ID: " + carId);
        }

        // Generowanie kodu płatności
        String paymentCode = generateRandomPaymentCode();

        // Treść wiadomości e-mail
        String content = "<h3><strong>Hi " + user.getFirstName() + ",</strong></h3>"
                + "<p><br></p>"
                + "<p>We would like to inform you about an additional charge for your car rental that finishes today.</p>"
                + "<p><br></p>"
                + "<p>The returned <strong>" + theCar.getMake().getName() + " " + theCar.getModel() + "</strong> is in a worse condition than it was on the first day of your rental. Therefore, we are forced to extra charge you under paragraph 4 and 6 of our agreement.</p>"
                + "<p><br></p>"
                + "<p>Below you can find your payment code:</p>"
                + "<p><br></p>"
                + "<p><strong>" + paymentCode + "</strong></p>"
                + "<p><br></p>"
                + "<p>Paste this code into your transfer title and enter " + extraCharge + " as the amount.</p>"
                + "<p><br></p>"
                + "<p>If you prefer to pay in cash, you can visit <a href=\"https://www.google.com/maps/place/Wydzia%C5%82+Ekonomiczno-Socjologiczny+Uniwersytetu+%C5%81%C3%B3dzkiego/@51.7752141,19.4611604,17z/data=!3m2!4b1!5s0x471bcb27fb23fb65:0xc900bfbd73fd4323!4m6!3m5!1s0x471bcb27d95ca219:0x7a09bc332d4a8d73!8m2!3d51.7752141!4d19.4637353!16s%2Fg%2F120l7rrv?entry=ttu&g_ep=EgoyMDI0MTAyOS4wIKXMDSoASAFQAw%3D%3D\" style=\"color: #007bff; text-decoration: underline;\">our office</a>.</p>"
                + "<p><br></p>"
                + "<p>Thank you for your cooperation!</p>"
                + "<p><br></p>"
                + "<p>Best regards,</p>"
                + "<p><br></p>"
                + "<p>The CarGo Team</p>";

        helper.setFrom("cargomailboxpl@gmail.com");
        helper.setTo(user.getEmail());
        helper.setSubject("Request for extra charge for car rental: " + car.get().getMake() + " " + car.get().getModel());
        helper.setText(content, true);

        javaMailSender.send(message);
    }

    private String generateRandomPaymentCode() {
        Random random = new Random();
        StringBuilder code = new StringBuilder();
        for (int i = 0; i < 10; i++) {
            code.append(random.nextInt(10)); // Dodajemy losową cyfrę
        }
        return code.toString();
    }
}