package com.example.CarGo.services;

import com.example.CarGo.db.CarMakeRepository;
import com.example.CarGo.domain.CarMake;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.Optional;

@Service
public class CarMakeService {

    @Autowired
    private CarMakeRepository carMakeRepository;

    public List<CarMake> findAllCarMakes() {
        return carMakeRepository.findAll();
    }

    public Optional<CarMake> findCarMakeByName(String name) {
        return carMakeRepository.findByName(name);
    }

    public CarMake saveCarMake(String name) {
        return carMakeRepository.save(new CarMake(null, name));
    }

    @Transactional
    public boolean deleteCarMake(String carMakeName) {
        try {
            // Sprawdź, czy marka samochodu jest przypisana do jakiegoś auta
            boolean isUsedInCars = carMakeRepository.isCarMakeUsedInCars(carMakeName);

            // Jeśli marka jest przypisana do auta, nie można jej usunąć
            if (isUsedInCars) {
                return false;
            }

            // Jeśli marka nie jest przypisana do żadnego auta, usuń ją
            Optional<CarMake> carMake = carMakeRepository.findByName(carMakeName);
            if (carMake.isPresent()) {
                carMakeRepository.delete(carMake.get());
                return true;
            }

            return false; // Jeśli marka samochodu nie istnieje
        } catch (Exception e) {
            return false;
        }
    }

    public void addCarMake(String make) {
        if (carMakeRepository.existsByName(make)) {
            throw new IllegalArgumentException("Car Make already exists");
        }

        CarMake carMake = new CarMake();
        carMake.setName(make);
        carMakeRepository.save(carMake);
    }
}

