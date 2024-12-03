package com.example.CarGo.services;


import com.example.CarGo.domain.Reservation;
import com.example.CarGo.db.ReservationRepository;
import com.example.CarGo.domain.ReservationStatus;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.time.LocalDateTime;
import java.util.List;


@Service
public class ReservationService {
    @Autowired
    private ReservationRepository reservationRepository;


    public void updateReservationStatuses() {
        LocalDateTime today = LocalDateTime.now();
        List<Reservation> reservations = reservationRepository.findAll();

        for (Reservation reservation : reservations) {
            if (reservation.getReservationEnd().isBefore(today) || reservation.getReservationEnd().isEqual(today)) {
                reservation.setStatus(ReservationStatus.COMPLETED);
            } else if (reservation.getReservationStart().isBefore(today) || reservation.getReservationStart().isEqual(today)) {
                reservation.setStatus(ReservationStatus.ACTIVE);
            } else {
                reservation.setStatus(ReservationStatus.PENDING);
            }
        }

        reservationRepository.saveAll(reservations);
    }

    public Reservation saveReservation(Reservation reservation) {
        return reservationRepository.save(reservation);
    }

    public List<Reservation> findReservationsByUserId(Long userId) {
        return reservationRepository.findByUserId(userId);
    }

    public void cancelReservation(Long reservationId, Long userId) {
        Reservation reservation = reservationRepository.findByIdAndUserId(reservationId, userId)
                .orElseThrow(() -> new IllegalArgumentException("Reservation not found or unauthorized"));
        reservation.setStatus(ReservationStatus.CANCELLED);
        reservationRepository.save(reservation);
    }

    public List<Reservation> findAllReservations() {
        return reservationRepository.findAll();
    }

    // Aktualizacja statusu rezerwacji
    public void updateReservationStatus(Long reservationId, ReservationStatus newStatus) {
        Reservation reservation = reservationRepository.findById(reservationId)
                .orElseThrow(() -> new IllegalArgumentException("Reservation not found"));
        reservation.setStatus(newStatus);
        reservationRepository.save(reservation);
    }

    // Usunięcie rezerwacji
    public void deleteReservation(Long reservationId) {
        if (!reservationRepository.existsById(reservationId)) {
            throw new IllegalArgumentException("Reservation not found");
        }
        reservationRepository.deleteById(reservationId);
    }
}