package com.example.CarGo.db;

import com.example.CarGo.domain.*;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import java.time.LocalDateTime;
import java.util.List;

import java.util.Optional;

@Repository
public interface CarRepository extends JpaRepository<Car, Long> {

    List<Car> findByLocation(Location location);

    Optional<Car> findById(Long id);

    @Query("SELECT c FROM Car c WHERE c.location.city = :location AND " +
            "NOT EXISTS (SELECT r FROM Reservation r WHERE r.car = c " +
            "AND (r.reservationStart <= :endDate AND r.reservationEnd >= :startDate))")
    List<Car> findAvailableCarsInLocation(@Param("location") String location,
                                          @Param("startDate") LocalDateTime startDate,
                                          @Param("endDate") LocalDateTime endDate);



    @Query("SELECT c FROM Car c WHERE " +
            "NOT EXISTS (SELECT r FROM Reservation r WHERE r.car = c " +
            "AND (r.reservationStart <= :endDate AND r.reservationEnd >= :startDate))")
    List<Car> findAvailableCars(@Param("startDate") LocalDateTime startDate,
                                @Param("endDate") LocalDateTime endDate);

    @Query("SELECT c FROM Car c " +
            "WHERE (:location IS NULL OR :location = '' OR c.location.city = :location) AND " +
            "(:gearboxType IS NULL OR c.gearboxType = :gearboxType) AND " +
            "(:chassisType IS NULL OR c.chassisType = :chassisType) AND " +
            "(:seatCount IS NULL OR c.seatCount = :seatCount) AND " +
            "(:yearMin IS NULL OR c.yearOfProduction >= :yearMin) AND " +
            "(:yearMax IS NULL OR c.yearOfProduction <= :yearMax) AND " +
            "(:priceMin IS NULL OR c.pricePerDay >= :priceMin) AND " +
            "(:priceMax IS NULL OR c.pricePerDay <= :priceMax) AND " +
            "(:make IS NULL OR :make = '' OR c.make.name = :make) AND " +
            "(:fuelType IS NULL OR c.fuelType = :fuelType) AND " +
            "(NOT EXISTS (SELECT r FROM Reservation r WHERE r.car = c AND " +
            "(r.reservationStart <= :endDate AND r.reservationEnd >= :startDate)))")
    List<Car> findCarsWithFilters(
            @Param("location") String location,
            @Param("gearboxType") GearboxType gearboxType,
            @Param("chassisType") ChassisType chassisType,
            @Param("seatCount") Integer seatCount,
            @Param("yearMin") Integer yearMin,
            @Param("yearMax") Integer yearMax,
            @Param("priceMin") Double priceMin,
            @Param("priceMax") Double priceMax,
            @Param("make") String make,
            @Param("fuelType") FuelType fuelType,
            @Param("startDate") LocalDateTime startDate,
            @Param("endDate") LocalDateTime endDate);
}

