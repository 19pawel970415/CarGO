package com.example.CarGo;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.scheduling.annotation.EnableAsync;

@SpringBootApplication
@EnableAsync
public class CarGoApplication {

	public static void main(String[] args) {
		SpringApplication.run(CarGoApplication.class, args);
	}

}
