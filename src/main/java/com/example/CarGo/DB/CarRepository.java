package com.example.CarGo.DB;

import com.example.CarGo.models.Car;
import com.example.CarGo.models.ChassisType;
import com.example.CarGo.models.FuelType;
import com.example.CarGo.models.GearboxType;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import java.time.LocalDateTime;
import java.util.List;

import java.util.List;
import java.util.Optional;

@Repository
public interface CarRepository extends JpaRepository<Car, Long> {

    List<Car> findByLocation(String location);

    Optional<Car> findById(Long id);

    @Query("SELECT c FROM Car c WHERE c.location = :location AND " +
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

    @Query("SELECT c FROM Car c WHERE " +
            "(:location IS NULL OR :location = '' OR c.location = :location) AND " +
            "(:gearboxType IS NULL OR c.gearboxType = :gearboxType) AND " +
            "(:chassisType IS NULL OR c.chassisType = :chassisType) AND " +
            "(:seatCount IS NULL OR c.seatCount = :seatCount) AND " +
            "(:yearMin IS NULL OR c.yearOfProduction >= :yearMin) AND " +  // Filtrujemy po minimalnym roku produkcji
            "(:yearMax IS NULL OR c.yearOfProduction <= :yearMax) AND " +  // Filtrujemy po maksymalnym roku produkcji
            "(:priceMin IS NULL OR c.pricePerDay >= :priceMin) AND " +  // Filtrujemy po minimalnej cenie
            "(:priceMax IS NULL OR c.pricePerDay <= :priceMax) AND " +  // Filtrujemy po maksymalnej cenie
            "(:make IS NULL OR :make = '' OR c.make = :make) AND " +
            "(:fuelType IS NULL OR c.fuelType = :fuelType) AND " + // Dodano filtr fuelType
            "NOT EXISTS (SELECT r FROM Reservation r WHERE r.car = c " +
            "AND (r.reservationStart <= :endDate AND r.reservationEnd >= :startDate))")
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
            @Param("fuelType") FuelType fuelType, // Dodano parametr fuelType
            @Param("startDate") LocalDateTime startDate,
            @Param("endDate") LocalDateTime endDate);
}


