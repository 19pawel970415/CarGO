package com.example.CarGo;

import com.example.CarGo.services.ReservationService;
import org.springframework.boot.context.event.ApplicationReadyEvent;
import org.springframework.context.event.EventListener;
import org.springframework.stereotype.Component;

@Component
public class StartupTask {

    private final ReservationService reservationService;

    public StartupTask(ReservationService reservationService) {
        this.reservationService = reservationService;
    }

    @EventListener(ApplicationReadyEvent.class)
    public void onApplicationReady() {
        reservationService.updateReservationStatuses();
        System.out.println("Reservation statuses have been updated.");
    }
}

