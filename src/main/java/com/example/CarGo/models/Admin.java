package com.example.CarGo.models;

import jakarta.persistence.*;

@Entity
@Table(name = "admins")
public class Admin extends Person {

    @Column(unique = true, nullable = false)
    private String login;

    @Column(unique = true, nullable = false)
    private String password;

}
