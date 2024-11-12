package com.example.CarGo.DB;

import com.example.CarGo.models.Car;
import com.example.CarGo.models.ChassisType;
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
}


