package com.example.CarGo.Services;


import com.example.CarGo.models.Car;
import com.example.CarGo.DB.CarRepository;
import com.example.CarGo.models.ChassisType;
import com.example.CarGo.models.FuelType;
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
        return carRepository.findAvailableCars(startDate.atStartOfDay(), endDate.atTime(23, 59, 59));
    }

    public List<Car> findAvailableCarsInLocation(String location, LocalDate startDate, LocalDate endDate) {
        return carRepository.findAvailableCarsInLocation(location, startDate.atStartOfDay(),
                endDate.atTime(23, 59, 59));
    }

    public List<Car> findCarsWithFilters(String location,
                                         GearboxType gearboxType,
                                         ChassisType chassisType,
                                         Integer seatCount,
                                         Integer yearMin,
                                         Integer yearMax,
                                         Double priceMin,
                                         Double priceMax,
                                         String make,
                                         FuelType fuelType,
                                         LocalDate startDate,
                                         LocalDate endDate) {
        return carRepository.findCarsWithFilters(location,
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
}