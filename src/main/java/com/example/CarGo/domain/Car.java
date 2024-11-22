package com.example.CarGo.domain;

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

    @ManyToOne
    @JoinColumn(name = "make_id", nullable = false)
    private CarMake make;

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
    @Enumerated(EnumType.STRING)
    private FuelType fuelType;

    @Column
    private int seatCount;

    @Column
    private Double pricePerDay;

    @ManyToOne
    @JoinColumn(name = "location_id", nullable = false)
    private Location location;

    @Enumerated(EnumType.STRING)
    private CarStatus status;

    @OneToMany(mappedBy = "car", cascade = CascadeType.ALL, fetch = FetchType.LAZY)
    private List<Reservation> reservations;
}
