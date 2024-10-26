package com.example.CarGo.models;

import jakarta.persistence.*;
import java.time.LocalDateTime;

@Entity
@Table(name = "reports")
public class Raport {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(nullable = false)
    private LocalDateTime generatedAt;

    @Column
    private String reportType;

    @Lob
    private String reportData;

    // Getters and Setters
}
