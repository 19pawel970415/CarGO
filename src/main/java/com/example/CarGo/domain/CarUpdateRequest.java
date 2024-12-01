package com.example.CarGo.domain;

import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
public class CarUpdateRequest {
    private Long id;
//    private String location;
    private String registrationNumber;
    private Double pricePerDay;
}

