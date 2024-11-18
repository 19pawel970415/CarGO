package com.example.CarGo.services;


import com.example.CarGo.domain.*;
import com.example.CarGo.db.CarRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.scheduling.annotation.Async;
import org.springframework.stereotype.Service;
import java.time.LocalDate;


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
}