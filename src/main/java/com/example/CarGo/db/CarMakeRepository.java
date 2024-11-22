package com.example.CarGo.db;

import com.example.CarGo.domain.CarMake;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.util.Optional;

@Repository
public interface CarMakeRepository extends JpaRepository<CarMake, Long> {
    Optional<CarMake> findByName(String name);

    boolean existsByName(String name);

    // Custom query to check if a Car Make is used by any Car
    @Query("SELECT COUNT(c) > 0 FROM Car c WHERE c.make.name = :carMakeName")
    boolean isCarMakeUsedInCars(@Param("carMakeName") String carMakeName);
}



