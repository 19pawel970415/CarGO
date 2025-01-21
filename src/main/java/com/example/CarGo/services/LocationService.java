package com.example.CarGo.services;

import com.example.CarGo.db.LocationRepository;
import com.example.CarGo.domain.Location;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
public class LocationService {

    @Autowired
    private LocationRepository locationRepository;

    @Transactional
    public boolean addLocation(String locationName) {
        try {

            boolean locationExists = locationRepository.existsByCity(locationName);
            if (locationExists) {
                return false;
            }


            Location newLocation = new Location();
            newLocation.setCity(locationName);
            locationRepository.save(newLocation);

            return true;
        } catch (Exception e) {

            return false;
        }
    }



    @Transactional
    public boolean deleteLocation(String locationName) {
        try {

            boolean isUsedInReservations = locationRepository.isLocationUsedInReservations(locationName);
            boolean isUsedInCars = locationRepository.isLocationUsedInCars(locationName);
            if (isUsedInReservations || isUsedInCars) {
                return false;
            }


            locationRepository.deleteByCity(locationName);
            return true;
        } catch (Exception e) {

            return false;
        }
    }
}
