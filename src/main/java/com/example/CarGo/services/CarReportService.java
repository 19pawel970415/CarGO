package com.example.CarGo.services;

import com.example.CarGo.db.CarRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

@Service
public class CarReportService {

    @Autowired
    private CarRepository carRepository;

    public Map<String, Long> getCarsCountByLocation() {
        List<Object[]> results = carRepository.countCarsByLocation();
        Map<String, Long> carCountByLocation = new HashMap<>();

        for (Object[] result : results) {
            String city = (String) result[0];
            Long count = (Long) result[1];
            carCountByLocation.put(city, count);
        }

        return carCountByLocation;
    }
}
