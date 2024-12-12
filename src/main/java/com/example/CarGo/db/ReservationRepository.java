package com.example.CarGo.db;

import com.example.CarGo.domain.Reservation;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.time.LocalDateTime;
import java.util.List;
import java.util.Optional;

@Repository
public interface ReservationRepository extends JpaRepository<Reservation, Long> {
    List<Reservation> findByUserId(Long userId);
    Optional<Reservation> findByIdAndUserId(Long reservationId, Long userId);

    List<Reservation> findByReservationEnd(LocalDateTime reservationEnd);

    List<Reservation> findByReservationEndBetween(LocalDateTime start, LocalDateTime end);
}
