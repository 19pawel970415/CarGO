package com.example.CarGo.services;


import com.example.CarGo.db.*;
import com.example.CarGo.domain.*;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.scheduling.annotation.Async;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.sql.SQLDataException;
import java.time.LocalDate;


import java.time.LocalDateTime;
import java.util.List;
import java.util.Objects;
import java.util.Optional;
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

    public CarService(ReservationRepository reservationRepository, LocationRepository locationRepository, CarMakeRepository carMakeRepository, SeatCountRepository seatCountRepository) {
        this.reservationRepository = reservationRepository;
        this.locationRepository = locationRepository;
        this.carMakeRepository = carMakeRepository;
        this.seatCountRepository = seatCountRepository;
    }

    public List<Car> findAllCars() {
        return carRepository.findAll();
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
        return carRepository.findCarsWithFilters(location.getCity(),
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
                endDate.atTime(23, 59, 59));
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
    public void addCar(CarAddRequest request) {
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
        carRepository.deleteById(carId);
    }
}