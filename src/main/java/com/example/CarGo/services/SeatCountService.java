package com.example.CarGo.services;

import com.example.CarGo.db.SeatCountRepository;
import com.example.CarGo.domain.SeatCount;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.Optional;

@Service
public class SeatCountService {

    @Autowired
    private SeatCountRepository seatCountRepository;

    public List<SeatCount> findAllSeatCounts() {
        return seatCountRepository.findAll();
    }

    public Optional<SeatCount> findSeatCountById(Long id) {
        return seatCountRepository.findById(id);
    }

    public boolean addSeatCount(Long seatCountId) {
        Optional<SeatCount> seatCountOptional = seatCountRepository.findById(seatCountId);

        if (seatCountOptional.isPresent()) {
            SeatCount seatCount = seatCountOptional.get();
            if (!seatCount.isAvailable()) {
                seatCount.setAvailable(true);  // Ustaw dostępność
                seatCountRepository.save(seatCount);  // Zapisz zaktualizowaną liczbę miejsc
                return true;
            }
        }
        return false;
    }

    public boolean deactivateSeatCount(Long seatCountId) {
        Optional<SeatCount> seatCountOptional = seatCountRepository.findById(seatCountId);

        if (seatCountOptional.isPresent()) {
            SeatCount seatCount = seatCountOptional.get();

            // Walidacja: sprawdzenie powiązania z samochodem
            if (seatCountRepository.isAssignedToCar(seatCount.getId())) { // Zakładamy, że istnieje metoda isAssignedToCar()
                return false; // Nie można dezaktywować, jeśli jest powiązany z samochodem
            }

            seatCount.setAvailable(false); // Ustawienie dostępności na false
            seatCountRepository.save(seatCount); // Zapis zaktualizowanego obiektu
            return true;
        }

        return false; // Jeśli Seat Count nie istnieje
    }
}
