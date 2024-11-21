package com.example.CarGo.domain;

import jakarta.persistence.*;
import lombok.Getter;
import lombok.Setter;

import java.time.LocalDateTime;

@Entity
@Table(name = "reservations")
@Getter
@Setter
public class Reservation {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @ManyToOne
    @JoinColumn(name = "user_id", nullable = false)
    private User user;

    @ManyToOne
    @JoinColumn(name = "car_id", nullable = false)
    private Car car;

    @Column(nullable = false)
    private LocalDateTime reservationStart;

    @Column(nullable = false)
    private LocalDateTime reservationEnd;

    @ManyToOne(cascade = CascadeType.PERSIST)
    @JoinColumn(name = "pick_up_point_id", nullable = false)
    private Location pickUpPoint;

    @ManyToOne(cascade = CascadeType.PERSIST)
    @JoinColumn(name = "drop_off_point_id", nullable = false)
    private Location dropOfPoint;

    @Enumerated(EnumType.STRING)
    @Column(name = "status", length = 20)
    private ReservationStatus status;
}
