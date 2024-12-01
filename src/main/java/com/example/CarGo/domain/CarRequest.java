package com.example.CarGo.domain;

import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
public class CarRequest {

    private Long id;
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
    private CarStatus status;

    public CarRequest(Long id, CarMake make, String model, String registrationNumber, String vin,
                      int yearOfProduction, ChassisType chassisType, GearboxType gearboxType,
                      FuelType fuelType, SeatCount seatCount, Double pricePerDay,
                      Location location, CarStatus status) {
        this.id = id;
        this.make = make;
        this.model = model;
        this.registrationNumber = registrationNumber;
        this.vin = vin;
        this.yearOfProduction = yearOfProduction;
        this.chassisType = chassisType;
        this.gearboxType = gearboxType;
        this.fuelType = fuelType;
        this.seatCount = seatCount;
        this.pricePerDay = pricePerDay;
        this.location = location;
        this.status = status;
    }
}
