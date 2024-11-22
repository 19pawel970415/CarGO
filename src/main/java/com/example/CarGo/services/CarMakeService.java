package com.example.CarGo.services;

import com.example.CarGo.db.CarMakeRepository;
import com.example.CarGo.domain.CarMake;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

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

    public boolean deleteCarMake(Long id) {
        if (carMakeRepository.existsById(id)) {
            carMakeRepository.deleteById(id);
            return true;
        }
        return false;
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

