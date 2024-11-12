package com.example.CarGo.Services;


import com.example.CarGo.models.Car;
import com.example.CarGo.DB.CarRepository;
import com.example.CarGo.models.ChassisType;
import com.example.CarGo.models.GearboxType;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Service;
import java.time.LocalDate;


import java.time.LocalDateTime;
import java.util.List;
import java.util.Optional;

@Service
public class CarService {

    @Autowired
    private CarRepository carRepository;

    public List<Car> findAllCars() {
        return carRepository.findAll();
    }

    public List<Car> findCarsByLocation(String location) {
        return carRepository.findByLocation(location);
    }

    public Optional<Car> findCarById(Long id) {
        return carRepository.findById(id);
    }

    public List<Car> findAvailableCars(LocalDate startDate, LocalDate endDate) {
        return carRepository.findAvailableCars( startDate.atStartOfDay(), endDate.atTime(23, 59, 59));
    }
    public List<Car> findAvailableCarsInLocation(String location, LocalDate startDate, LocalDate endDate) {
        return carRepository.findAvailableCarsInLocation(location, startDate.atStartOfDay(), endDate.atTime(23, 59, 59));
    }
    public List<Car> findCarsWithFilters(String location,
                                         GearboxType gearboxType,
                                         ChassisType chassisType,
                                         Integer seatCount,
                                         Integer year,
                                         Double pricePerDay,
                                         String make,
                                         LocalDate startDate,
                                         LocalDate endDate) {

        List<Car> cars = findAvailableCars(startDate, endDate);

        if (location != null && !location.isEmpty()) {
            cars = cars.stream()
                    .filter(car -> location.equals(car.getLocation()))
                    .toList();
        }

        if (gearboxType != null) {
            cars = cars.stream()
                    .filter(car -> gearboxType.equals(car.getGearboxType()))
                    .toList();
        }

        if (chassisType != null) {
            cars = cars.stream()
                    .filter(car -> chassisType.equals(car.getChassisType()))
                    .toList();
        }

        if (seatCount != null && seatCount > 0) {
            cars = cars.stream()
                    .filter(car -> seatCount.equals(car.getSeatCount()))
                    .toList();
        }

        if (year != null && year >= 1886 && year <= LocalDate.now().getYear()) {
            cars = cars.stream()
                    .filter(car -> year.equals(car.getYearOfProduction()))
                    .toList();
        }

        if (pricePerDay != null && pricePerDay > 0) {
            cars = cars.stream()
                    .filter(car -> car.getPricePerDay() <= pricePerDay)
                    .toList();
        }

        if (make != null && !make.isEmpty()) {
            cars = cars.stream()
                    .filter(car -> make.equals(car.getMake()))
                    .toList();
        }

        return cars;
    }
}