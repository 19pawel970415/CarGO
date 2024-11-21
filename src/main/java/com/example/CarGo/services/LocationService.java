package com.example.CarGo.services;

import com.example.CarGo.db.LocationRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
public class LocationService {

    @Autowired
    private LocationRepository locationRepository;

    // Metoda do usunięcia lokalizacji z bazy danych
    @Transactional
    public boolean deleteLocation(String locationName) {
        try {
            // Sprawdź, czy lokalizacja jest używana w tabeli reservations i cars
            boolean isUsedInReservations = locationRepository.isLocationUsedInReservations(locationName);
            boolean isUsedInCars = locationRepository.isLocationUsedInCars(locationName);
            if (isUsedInReservations || isUsedInCars) {
                return false; // Zwróć false, jeśli lokalizacja jest używana w innych tabelach
            }

            // Jeśli lokalizacja nie jest używana, usuń ją
            locationRepository.deleteByCity(locationName);
            return true;
        } catch (Exception e) {
            // Zwróć fałsz w przypadku błędu
            return false;
        }
    }
}
