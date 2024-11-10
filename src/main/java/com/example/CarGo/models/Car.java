package com.example.CarGo.models;

import jakarta.persistence.*;
import lombok.Getter;
import lombok.Setter;

import java.util.List;

@Entity
@Table(name = "cars")
@Getter
@Setter
public class Car {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(nullable = false)
    private String make;

    @Column(nullable = false)
    private String model;

    @Column(nullable = false, unique = true)
    private String registrationNumber;

    @Column(nullable = false, unique = true)
    private String vin;

    @Column(nullable = false)
    private int yearOfProduction;

    @Column
    @Enumerated(EnumType.STRING)
    private ChassisType chassisType;

    @Column
    @Enumerated(EnumType.STRING)
    private GearboxType gearboxType;

    @Column
    private int seatCount;

    @Column
    private Double pricePerDay;

    @Column(nullable = false)
    private String location;

    @Enumerated(EnumType.STRING)
    private CarStatus status;

    @OneToMany(mappedBy = "car", cascade = CascadeType.ALL, fetch = FetchType.LAZY)
    private List<Reservation> reservations;

    public String getLocation() {
        return location;
    }

    public void setLocation(String location) {
        this.location = location;
    }
}
