package com.example.CarGo.domain;

import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.Setter;

@Entity
@Table(name = "seat_counts")
@Getter
@Setter
public class SeatCount {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(nullable = false)
    private int count;

    @Column(nullable = false)
    private boolean available;

    public SeatCount() {}

    public SeatCount(int count, boolean available) {
        this.count = count;
        this.available = available;
    }
}

