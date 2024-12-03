package com.example.CarGo.domain;

import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
public class CarAddRequest {
    private CarMake make;
    private String model;
    private String registrationNumber;
    private String vin;
    private int yearOfProduction;
    private ChassisType chassisType;
    private GearboxType gearboxType;
    private FuelType fuelType;
    private SeatCount seatCount;
    private Double pricePerDay;
    private Location location;
}

