package com.example.CarGo;

import com.example.CarGo.services.CarService;
import com.example.CarGo.services.ReservationService;
import org.springframework.boot.context.event.ApplicationReadyEvent;
import org.springframework.context.event.EventListener;
import org.springframework.stereotype.Component;

@Component
public class StartupTask {

    private final ReservationService reservationService;
    private final CarService carService;

    public StartupTask(ReservationService reservationService, CarService carService) {
        this.reservationService = reservationService;
        this.carService = carService;
    }

    @EventListener(ApplicationReadyEvent.class)
    public void onApplicationReady() {
        reservationService.updateReservationStatuses();
        carService.updateCarStatuses();

        System.out.println("Reservation statuses have been updated.");
        System.out.println("Car statuses have been updated.");
    }

    //TODO na birząco aktaulizacja raportów

    //TODO po edycji rezerwacji mail do klienta
}


