package com.example.CarGo.db;

import com.example.CarGo.domain.CarMake;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.Optional;

@Repository
public interface CarMakeRepository extends JpaRepository<CarMake, Long> {
    Optional<CarMake> findByName(String name);

    boolean existsByName(String name);
}



