package com.example.CarGo.models;

import jakarta.persistence.*;
import java.util.List;

@Entity
@Table(name = "users")
public class User extends Person {

    @Column(unique = true, nullable = false)
    private String login;

    @Column(unique = true, nullable = false)
    private String password;

    @OneToMany(mappedBy = "user", cascade = CascadeType.ALL, fetch = FetchType.LAZY)
    private List<Reservation> reservations;

}
