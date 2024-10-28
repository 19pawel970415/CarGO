package com.example.CarGo.models;

import jakarta.persistence.*;


@Entity
@Table(name = "managers")
public class Manager extends Person {

    @Column(unique = true, nullable = false)
    private String login;

    @Column(unique = true, nullable = false)
    private String password;
}
