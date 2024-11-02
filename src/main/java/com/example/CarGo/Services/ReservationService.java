package com.example.CarGo.Services;


import com.example.CarGo.models.Reservation;
import com.example.CarGo.DB.ReservationRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;


@Service
public class ReservationService {


    @Autowired
    private ReservationRepository reservationRepository;


    public Reservation saveReservation(Reservation reservation) {
        return reservationRepository.save(reservation);
    }
}