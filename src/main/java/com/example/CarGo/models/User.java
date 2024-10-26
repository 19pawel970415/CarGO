package com.example.CarGo.models;

import jakarta.persistence.*;
import java.util.List;

@Entity
@Table(name = "users")
public class User extends Person {

    @Column(nullable = false)
    private String city;

    @Column(nullable = false)
    private String postalCode;

    @Column
    private String street;

    @Column
    private String apartmentNumber;

    @Column(nullable = false, unique = true)
    private String pesel;

    @Column(nullable = false)
    private String birthDate;

    @Column(nullable = false)
    private String gender;

    @OneToMany(mappedBy = "user", cascade = CascadeType.ALL, fetch = FetchType.LAZY)
    private List<Reservation> reservations;

}
