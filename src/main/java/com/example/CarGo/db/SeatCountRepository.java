package com.example.CarGo.db;

import com.example.CarGo.domain.SeatCount;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface SeatCountRepository extends JpaRepository<SeatCount, Long> {

    List<SeatCount> findByAvailableTrue();
}

