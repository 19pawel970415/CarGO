package com.example.CarGo.services;


import com.example.CarGo.domain.*;
import com.example.CarGo.db.ReservationRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.time.LocalDateTime;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;


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

    // Najczęściej wynajmowane auto
    public List<Map.Entry<Car, Long>> getMostRentedCars(LocalDateTime startDate, LocalDateTime endDate) {
        List<Reservation> reservations = reservationRepository.findByReservationEndBetween(startDate, endDate);

        return reservations.stream()
                .filter(reservation -> reservation.getStatus() == ReservationStatus.ACTIVE
                        || reservation.getStatus() == ReservationStatus.COMPLETED)
                .collect(Collectors.groupingBy(Reservation::getCar, Collectors.counting()))
                .entrySet()
                .stream()
                .sorted((e1, e2) -> Long.compare(e2.getValue(), e1.getValue())) // Sortuj malejąco po liczbie wynajmów
                .collect(Collectors.toList());
    }

    // Najczęściej używane paliwo
    public List<Map.Entry<FuelType, Long>> getFuelTypeRanking(LocalDateTime startDate, LocalDateTime endDate) {
        List<Reservation> reservations = reservationRepository.findByReservationEndBetween(startDate, endDate);

        return reservations.stream()
                .filter(reservation -> reservation.getStatus() == ReservationStatus.ACTIVE
                        || reservation.getStatus() == ReservationStatus.COMPLETED)
                .map(reservation -> reservation.getCar().getFuelType())
                .collect(Collectors.groupingBy(fuelType -> fuelType, Collectors.counting()))
                .entrySet()
                .stream()
                .sorted((e1, e2) -> Long.compare(e2.getValue(), e1.getValue())) // Sortuj malejąco po popularności
                .collect(Collectors.toList());
    }

    // Lokalizacja, która zarobiła najwięcej
    public List<Map.Entry<Reservation, Double>> getReservationsWithEarnings(LocalDateTime startDate, LocalDateTime endDate) {
        List<Reservation> reservations = reservationRepository.findByReservationEndBetween(startDate, endDate);

        return reservations.stream()
                .filter(reservation -> reservation.getStatus() == ReservationStatus.ACTIVE
                        || reservation.getStatus() == ReservationStatus.COMPLETED)
                .collect(Collectors.toMap(
                        reservation -> reservation,
                        reservation -> calculateReservationIncome(reservation)
                ))
                .entrySet()
                .stream()
                .sorted((e1, e2) -> Double.compare(e2.getValue(), e1.getValue())) // Sortuj malejąco po zarobkach
                .collect(Collectors.toList());
    }

    public List<Map.Entry<Location, Long>> getMostRentedLocations(LocalDateTime startDate, LocalDateTime endDate) {
        List<Reservation> reservations = reservationRepository.findByReservationEndBetween(startDate, endDate);

        return reservations.stream()
                .filter(reservation -> reservation.getStatus() == ReservationStatus.ACTIVE
                        || reservation.getStatus() == ReservationStatus.COMPLETED)
                .map(reservation -> reservation.getCar().getLocation()) // Assuming Car has a `location` field
                .collect(Collectors.groupingBy(location -> location, Collectors.counting()))
                .entrySet()
                .stream()
                .sorted((e1, e2) -> Long.compare(e2.getValue(), e1.getValue())) // Sort descending by count
                .collect(Collectors.toList());
    }

    public List<Map.Entry<ChassisType, Long>> getMostRentedCarTypes(LocalDateTime startDate, LocalDateTime endDate) {
        List<Reservation> reservations = reservationRepository.findByReservationEndBetween(startDate, endDate);

        return reservations.stream()
                .filter(reservation -> reservation.getStatus() == ReservationStatus.ACTIVE
                        || reservation.getStatus() == ReservationStatus.COMPLETED)
                .map(reservation -> reservation.getCar().getChassisType()) // Assuming Car has a `carType` field
                .collect(Collectors.groupingBy(carType -> carType, Collectors.counting()))
                .entrySet()
                .stream()
                .sorted((e1, e2) -> Long.compare(e2.getValue(), e1.getValue())) // Sort descending by count
                .collect(Collectors.toList());
    }

    // Metoda pomocnicza do obliczania zarobków
    private double calculateReservationIncome(Reservation reservation) {
        double pricePerDay = reservation.getCar().getPricePerDay();
        long days = Math.max(
                reservation.getReservationStart().until(reservation.getReservationEnd(), java.time.temporal.ChronoUnit.DAYS),
                1 // Minimalna długość rezerwacji to 1 dzień
        );
        return pricePerDay * days;
    }
}