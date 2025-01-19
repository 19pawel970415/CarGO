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

    @Column(nullable = false)
    private Boolean isPaid = false;

    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public User getUser() {
        return user;
    }

    public void setUser(User user) {
        this.user = user;
    }

    public Car getCar() {
        return car;
    }

    public void setCar(Car car) {
        this.car = car;
    }

    public LocalDateTime getReservationStart() {
        return reservationStart;
    }

    public void setReservationStart(LocalDateTime reservationStart) {
        this.reservationStart = reservationStart;
    }

    public LocalDateTime getReservationEnd() {
        return reservationEnd;
    }

    public void setReservationEnd(LocalDateTime reservationEnd) {
        this.reservationEnd = reservationEnd;
    }

    public Location getPickUpPoint() {
        return pickUpPoint;
    }

    public void setPickUpPoint(Location pickUpPoint) {
        this.pickUpPoint = pickUpPoint;
    }

    public Location getDropOfPoint() {
        return dropOfPoint;
    }

    public void setDropOfPoint(Location dropOfPoint) {
        this.dropOfPoint = dropOfPoint;
    }

    public ReservationStatus getStatus() {
        return status;
    }

    public void setStatus(ReservationStatus status) {
        this.status = status;
    }

    public Boolean getIsPaid() {
        return isPaid;
    }

    public void setIsPaid(Boolean isPaid) {
        this.isPaid = isPaid;
    }
}
