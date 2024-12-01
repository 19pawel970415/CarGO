package com.example.CarGo.services;


import com.example.CarGo.db.ReservationRepository;
import com.example.CarGo.domain.*;
import com.example.CarGo.db.CarRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.scheduling.annotation.Async;
import org.springframework.stereotype.Service;
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

    public CarService(ReservationRepository reservationRepository) {
        this.reservationRepository = reservationRepository;
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

    public void updateCar(CarUpdateRequest request) {
        Car car = carRepository.findById(request.getId())
                .orElseThrow(() -> new IllegalArgumentException("Car not found"));

//        car.setLocation();
        car.setRegistrationNumber(request.getRegistrationNumber());
        car.setPricePerDay(request.getPricePerDay());
        carRepository.save(car);
    }

}