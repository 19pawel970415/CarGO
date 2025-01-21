package com.example.CarGo.services;


import com.example.CarGo.domain.*;
import com.example.CarGo.db.ReservationRepository;
import jakarta.mail.MessagingException;
import jakarta.mail.internet.MimeMessage;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.mail.javamail.JavaMailSender;
import org.springframework.mail.javamail.MimeMessageHelper;
import org.springframework.stereotype.Service;
import org.springframework.web.util.UriComponentsBuilder;

import java.time.LocalDateTime;
import java.util.List;
import java.util.Map;
import java.util.Optional;
import java.util.Random;
import java.util.stream.Collectors;
import java.text.Normalizer;
import java.util.regex.Pattern;


@Service
public class ReservationService {
    @Autowired
    private ReservationRepository reservationRepository;
    @Autowired
    private JavaMailSender javaMailSender;


    public void updateReservationStatuses() {
        LocalDateTime today = LocalDateTime.now().withHour(0).withMinute(0).withSecond(0).withNano(0);
        List<Reservation> reservations = reservationRepository.findAll();

        for (Reservation reservation : reservations) {
            if (reservation.getReservationEnd().isBefore(today) || reservation.getReservationEnd().isEqual(today)) {
                reservation.setStatus(ReservationStatus.COMPLETED);
            } else if (reservation.getReservationStart().isBefore(today) || reservation.getReservationStart().isEqual(today)) {
                if (!reservation.getIsPaid() && reservation.getReservationStart().isEqual(today)) {
                    reservation.setStatus(ReservationStatus.CANCELLED);
                } else {
                    reservation.setStatus(ReservationStatus.ACTIVE);
                }
            } else {
                reservation.setStatus(ReservationStatus.PENDING);
            }
        }

        reservationRepository.saveAll(reservations);
    }

    public void updateReservationIsPaid() {
        List<Reservation> reservations = reservationRepository.findAll();

        for (Reservation reservation : reservations) {
            if (reservation.getIsPaid() == null) {
                reservation.setIsPaid(false);
            }
        }
        reservationRepository.saveAll(reservations);
    }

    public Reservation saveReservation(Reservation reservation) {
        return reservationRepository.save(reservation);
    }

    public List<Reservation> findReservationsByUserId(Long userId) {
        return reservationRepository.findByUserId(userId).stream()
                .filter(reservation -> reservation.getStatus() != ReservationStatus.CANCELLED)
                .collect(Collectors.toList());
    }

    public void cancelReservation(Long reservationId, Long userId) {
        Reservation reservation = reservationRepository.findByIdAndUserId(reservationId, userId)
                .orElseThrow(() -> new IllegalArgumentException("Reservation not found or unauthorized"));
        reservation.setStatus(ReservationStatus.CANCELLED);
        reservationRepository.save(reservation);
    }

    public List<Reservation> findAllReservations() {
        return reservationRepository.findAll();
    }


    public void updateReservationStatus(Long reservationId, ReservationStatus newStatus) {
        Reservation reservation = reservationRepository.findById(reservationId)
                .orElseThrow(() -> new IllegalArgumentException("Reservation not found"));
        reservation.setStatus(newStatus);
        reservationRepository.save(reservation);
    }


    public void deleteReservation(Long reservationId) {
        if (!reservationRepository.existsById(reservationId)) {
            throw new IllegalArgumentException("Reservation not found");
        }
        reservationRepository.deleteById(reservationId);
    }


    public List<Map.Entry<Car, Long>> getMostRentedCars(LocalDateTime startDate, LocalDateTime endDate) {
        List<Reservation> reservations = reservationRepository.findByReservationEndBetween(startDate, endDate);

        return reservations.stream()
                .filter(reservation -> reservation.getStatus() == ReservationStatus.ACTIVE
                        || reservation.getStatus() == ReservationStatus.COMPLETED)
                .collect(Collectors.groupingBy(Reservation::getCar, Collectors.counting()))
                .entrySet()
                .stream()
                .sorted((e1, e2) -> Long.compare(e2.getValue(), e1.getValue()))
                .collect(Collectors.toList());
    }


    public List<Map.Entry<FuelType, Long>> getFuelTypeRanking(LocalDateTime startDate, LocalDateTime endDate) {
        List<Reservation> reservations = reservationRepository.findByReservationEndBetween(startDate, endDate);

        return reservations.stream()
                .filter(reservation -> reservation.getStatus() == ReservationStatus.ACTIVE
                        || reservation.getStatus() == ReservationStatus.COMPLETED)
                .map(reservation -> reservation.getCar().getFuelType())
                .collect(Collectors.groupingBy(fuelType -> fuelType, Collectors.counting()))
                .entrySet()
                .stream()
                .sorted((e1, e2) -> Long.compare(e2.getValue(), e1.getValue()))
                .collect(Collectors.toList());
    }

    public List<Map.Entry<Car, Double>> getCarsEarnings(LocalDateTime startDate, LocalDateTime endDate) {
        List<Reservation> reservations = reservationRepository.findByReservationEndBetween(startDate, endDate);


        Map<Car, Double> carEarnings = reservations.stream()
                .filter(reservation -> reservation.getStatus() == ReservationStatus.ACTIVE
                        || reservation.getStatus() == ReservationStatus.COMPLETED)
                .collect(Collectors.groupingBy(
                        Reservation::getCar,
                        Collectors.summingDouble(this::calculateReservationIncome)
                ));


        return carEarnings.entrySet()
                .stream()
                .sorted((e1, e2) -> Double.compare(e2.getValue(), e1.getValue()))
                .collect(Collectors.toList());
    }


    public List<Map.Entry<String, Long>> getMostRentedLocations(LocalDateTime startDate, LocalDateTime endDate) {
        List<Reservation> reservations = reservationRepository.findByReservationEndBetween(startDate, endDate);

        return reservations.stream()
                .filter(reservation -> reservation.getStatus() == ReservationStatus.ACTIVE
                        || reservation.getStatus() == ReservationStatus.COMPLETED)
                .map(reservation -> normalize(reservation.getCar().getLocation().getCity()))
                .collect(Collectors.groupingBy(city -> city, Collectors.counting()))
                .entrySet()
                .stream()
                .sorted((e1, e2) -> Long.compare(e2.getValue(), e1.getValue()))
                .collect(Collectors.toList());
    }


    private String normalize(String input) {
        if (input == null) {
            return null;
        }
        String normalized = Normalizer.normalize(input, Normalizer.Form.NFD);
        Pattern pattern = Pattern.compile("\\p{InCombiningDiacriticalMarks}+");
        normalized = pattern.matcher(normalized).replaceAll("");
        return normalized.replace("ł", "l").replace("Ł", "L");
    }

    public List<Map.Entry<ChassisType, Long>> getMostRentedCarTypes(LocalDateTime startDate, LocalDateTime endDate) {
        List<Reservation> reservations = reservationRepository.findByReservationEndBetween(startDate, endDate);

        return reservations.stream()
                .filter(reservation -> reservation.getStatus() == ReservationStatus.ACTIVE
                        || reservation.getStatus() == ReservationStatus.COMPLETED)
                .map(reservation -> reservation.getCar().getChassisType())
                .collect(Collectors.groupingBy(carType -> carType, Collectors.counting()))
                .entrySet()
                .stream()
                .sorted((e1, e2) -> Long.compare(e2.getValue(), e1.getValue()))
                .collect(Collectors.toList());
    }


    private double calculateReservationIncome(Reservation reservation) {
        double pricePerDay = reservation.getCar().getPricePerDay();
        long days = Math.max(
                reservation.getReservationStart().until(reservation.getReservationEnd(), java.time.temporal.ChronoUnit.DAYS),
                1
        );
        return pricePerDay * days;
    }

    public Optional<Reservation> findById(Long reservationId) {
        return reservationRepository.findById(reservationId);
    }


    public void sendCancellationInformation(String email, Location pickUpLocation, LocalDateTime reservationFrom, LocalDateTime reservationTo, GearboxType gearboxType, SeatCount seatCount, ChassisType chassisType, FuelType fuelType, CarMake carMake, String carModel) throws MessagingException {
        MimeMessage message = javaMailSender.createMimeMessage();
        MimeMessageHelper helper = new MimeMessageHelper(message, true);

        String reservationLink = UriComponentsBuilder.fromHttpUrl("http://localhost:8080/gallery")
                .queryParam("location", pickUpLocation.getCity())
                .queryParam("startDate", reservationFrom.toLocalDate())
                .queryParam("endDate", reservationTo.toLocalDate())
                .queryParam("gearbox", gearboxType.name())
                .queryParam("seatCountId", seatCount.getId())
                .queryParam("carType", chassisType.name())
                .queryParam("fuelType", "")
                .queryParam("make", "")
                .queryParam("carModel", "")
                .build()
                .toUriString();

        helper.setFrom("cargomailboxpl@gmail.com");
        helper.setTo(email);
        helper.setSubject("Reservation Cancellation: " + reservationFrom.toString() + " - " + reservationTo.toString() + " on " + carMake.getName() + " " + carModel);

        String content = "<html><body>"
                + "<h3><strong>We had to cancel your reservation!</strong></h3>"
                + "<p>We’re sorry to inform you that we had to cancel your reservation on <strong>" + " " + carMake.getName() + " " + carModel + "</strong>. :(</p>"
                + "<p>Follow these steps to reserve a similar car again: </p>"
                + "<p>1. Log in to your account on out website.</p>"
                + "<p>2. Click the button below to reserve a different car:</p>"
                + "<p><br></p>"
                + "<a href=\"" + reservationLink + "\" style=\""
                + "background-color: #4CAF50; "
                + "color: white; "
                + "padding: 15px 32px; "
                + "text-align: center; "
                + "text-decoration: none; "
                + "display: inline-block; "
                + "font-size: 16px; "
                + "border-radius: 5px; "
                + "border: none; "
                + "cursor: pointer;\">Reserve again</a>"
                + "<p><br></p>"
                + "<p>Thank you for your understanding,</p>"
                + "<p>The CarGo Team</p>"
                + "</body></html>";

        helper.setText(content, true);

        javaMailSender.send(message);
    }



    public void sendPaymentEmail(Reservation reservation) throws MessagingException {

        User user = reservation.getUser();
        String userEmail = user.getEmail();
        Long reservationId = reservation.getId();
        Double pricePerDay = reservation.getCar().getPricePerDay();
        long days = Math.max(
                reservation.getReservationStart().until(reservation.getReservationEnd(), java.time.temporal.ChronoUnit.DAYS),
                1
        );
        Double totalPrice = pricePerDay * days;


        String paymentCode = generateRandomPaymentCode();


        MimeMessage message = javaMailSender.createMimeMessage();
        MimeMessageHelper helper = new MimeMessageHelper(message, true);


        helper.setFrom("cargomailboxpl@gmail.com");
        helper.setTo(userEmail);
        helper.setSubject("Payment Details for Your Reservation " + reservationId);


        String content = "<h3><strong>Hi " + user.getFirstName() + ",</strong></h3>"
                + "<p><br></p>"
                + "<p>We're happy that you want to pay for your reservation " + reservationId + ".</p>"
                + "<p><br></p>"
                + "<p>Below you can find your payment code:</p>"
                + "<p><br></p>"
                + "<p><br></p>"
                + "<p><strong>" + paymentCode + "</strong></p>"
                + "<p><br></p>"
                + "<p><br></p>"
                + "<p>Paste it into your transfer title and enter " + totalPrice + " as the amount.</p>"
                + "<p><br></p>"
                + "<p><br></p>"
                + "<p>If you want to pay in cash, visit <a href=\"https://www.google.com/maps/place/Wydzia%C5%82+Ekonomiczno-Socjologiczny+Uniwersytetu+%C5%81%C3%B3dzkiego/@51.7752141,19.4611604,17z/data=!3m2!4b1!5s0x471bcb27fb23fb65:0xc900bfbd73fd4323!4m6!3m5!1s0x471bcb27d95ca219:0x7a09bc332d4a8d73!8m2!3d51.7752141!4d19.4637353!16s%2Fg%2F120l7rrv?entry=ttu&g_ep=EgoyMDI0MTAyOS4wIKXMDSoASAFQAw%3D%3D\" style=\"color: #007bff; text-decoration: underline;\">our office</a>.</p>"
                + "<p><br></p>"
                + "<p>Best regards,</p>"
                + "<p><br></p>"
                + "<p>The CarGo Team</p>";

        helper.setText(content, true);


        javaMailSender.send(message);
    }


    private String generateRandomPaymentCode() {
        Random random = new Random();
        StringBuilder code = new StringBuilder();
        for (int i = 0; i < 10; i++) {
            code.append(random.nextInt(10));
        }
        return code.toString();
    }

    public void sendEmailsRemindingAboutPayment() {
        LocalDateTime today = LocalDateTime.now().withHour(0).withMinute(0).withSecond(0).withNano(0);
        List<Reservation> reservations = reservationRepository.findAll();
        List<Reservation> tomorrowsReservations = reservations.stream()
                .filter(reservation ->
                    reservation.getReservationStart().toLocalDate().equals(today.toLocalDate().plusDays(1))
                )
                .filter(reservation -> !reservation.getIsPaid())
                .toList();


        for (Reservation reservation : tomorrowsReservations) {
            try {
                sendPaymentReminderEmail(reservation);
            } catch (MessagingException e) {
                throw new RuntimeException(e);
            }
        }
    }

    public void sendPaymentReminderEmail(Reservation reservation) throws MessagingException {

        User user = reservation.getUser();
        String userEmail = user.getEmail();
        Long reservationId = reservation.getId();
        Double pricePerDay = reservation.getCar().getPricePerDay();
        long days = Math.max(
                reservation.getReservationStart().until(reservation.getReservationEnd(), java.time.temporal.ChronoUnit.DAYS),
                1
        );
        Double totalPrice = pricePerDay * days;


        MimeMessage message = javaMailSender.createMimeMessage();
        MimeMessageHelper helper = new MimeMessageHelper(message, true);


        helper.setFrom("cargomailboxpl@gmail.com");
        helper.setTo(userEmail);
        helper.setSubject("Reminder: Payment Pending for Your Reservation " + reservationId);


        String content = "<h3><strong>Hi " + user.getFirstName() + ",</strong></h3>"
                + "<p><br></p>"
                + "<p>This is a reminder that your reservation with ID " + reservationId + " is still pending payment.</p>"
                + "<p><br></p>"
                + "<p>Below you can find your payment code:</p>"
                + "<p><br></p>"
                + "<p><strong>" + generateRandomPaymentCode() + "</strong></p>"
                + "<p><br></p>"
                + "<p><br></p>"
                + "<p>Paste it into your transfer title and enter " + totalPrice + " as the amount.</p>"
                + "<p><br></p>"
                + "<p><br></p>"
                + "<p>If you want to pay in cash, visit <a href=\"https://www.google.com/maps/place/Wydzia%C5%82+Ekonomiczno-Socjologiczny+Uniwersytetu+%C5%81%C3%B3dzkiego/@51.7752141,19.4611604,17z/data=!3m2!4b1!5s0x471bcb27fb23fb65:0xc900bfbd73fd4323!4m6!3m5!1s0x471bcb27d95ca219:0x7a09bc332d4a8d73!8m2!3d51.7752141!4d19.4637353!16s%2Fg%2F120l7rrv?entry=ttu&g_ep=EgoyMDI0MTAyOS4wIKXMDSoASAFQAw%3D%3D\" style=\"color: #007bff; text-decoration: underline;\">our office</a>.</p>"
                + "<p><br></p>"
                + "<p>Best regards,</p>"
                + "<p><br></p>"
                + "<p>The CarGo Team</p>";

        helper.setText(content, true);


        javaMailSender.send(message);
    }

    public void sendEmailsInformingThatReservationWasCancelledDueToLackOfPayment() {
        LocalDateTime today = LocalDateTime.now().withHour(0).withMinute(0).withSecond(0).withNano(0);
        List<Reservation> reservations = reservationRepository.findAll();


        List<Reservation> cancelledReservations = reservations.stream()
                .filter(reservation ->
                        reservation.getReservationStart().toLocalDate().equals(today.toLocalDate()) &&
                                !reservation.getIsPaid()
                )
                .toList();


        for (Reservation reservation : cancelledReservations) {
            try {
                sendCancellationEmailDueToLackOfPayment(reservation);
            } catch (MessagingException e) {
                throw new RuntimeException(e);
            }
        }
    }

    public void sendCancellationEmailDueToLackOfPayment(Reservation reservation) throws MessagingException {

        User user = reservation.getUser();
        String userEmail = user.getEmail();
        Long reservationId = reservation.getId();


        MimeMessage message = javaMailSender.createMimeMessage();
        MimeMessageHelper helper = new MimeMessageHelper(message, true);


        helper.setFrom("cargomailboxpl@gmail.com");
        helper.setTo(userEmail);
        helper.setSubject("Your Reservation " + reservationId + " Was Cancelled Due to Lack of Payment");


        String content = "<h3><strong>Hi " + user.getFirstName() + ",</strong></h3>"
                + "<p><br></p>"
                + "<p>We regret to inform you that your reservation with ID " + reservationId + " has been cancelled due to lack of payment. Since the reservation was supposed to start today and no payment was made, we were forced to cancel it.</p>"
                + "<p><br></p>"
                + "<p>If you believe this is a mistake or if you would like to make the payment and restore your reservation, please contact us as soon as possible.</p>"
                + "<p><br></p>"
                + "<p>Best regards,</p>"
                + "<p><br></p>"
                + "<p>The CarGo Team</p>";

        helper.setText(content, true);


        javaMailSender.send(message);
    }

}