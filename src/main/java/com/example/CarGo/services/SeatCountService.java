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
}
