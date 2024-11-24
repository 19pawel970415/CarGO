package com.example.CarGo.db;

import com.example.CarGo.domain.SeatCount;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface SeatCountRepository extends JpaRepository<SeatCount, Long> {

    List<SeatCount> findAll();

    @Query("SELECT COUNT(c) > 0 FROM Car c WHERE c.seatCount.id = :seatCountId")
    boolean isAssignedToCar(@Param("seatCountId") Long seatCountId);
}

