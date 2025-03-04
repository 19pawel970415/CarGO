package com.example.CarGo.db;

import com.example.CarGo.domain.Location;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;

@Repository
public interface LocationRepository extends JpaRepository<Location, Long> {

    void deleteByCity(String city);
    boolean existsByCity(String city);
    Optional<Location> findByCity(String city);

    
    @Query("SELECT COUNT(r) > 0 FROM Reservation r WHERE r.pickUpPoint.city = :locationName OR r.dropOfPoint.city = :locationName")
    boolean isLocationUsedInReservations(@Param("locationName") String locationName);

    @Query("SELECT COUNT(c) > 0 FROM Car c WHERE c.location.city = :locationName")
    boolean isLocationUsedInCars(@Param("locationName") String locationName);

    List<Location> findByCityContainingIgnoreCase(String city);
}

