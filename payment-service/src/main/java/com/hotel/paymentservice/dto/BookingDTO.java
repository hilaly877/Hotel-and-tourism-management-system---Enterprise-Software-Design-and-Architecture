package com.hotel.paymentservice.dto;

import lombok.Data;
import lombok.NoArgsConstructor;

import java.math.BigDecimal;
import java.time.LocalDate;

// Mirrors the Booking object from Booking Service
@Data
@NoArgsConstructor
public class BookingDTO {
    private Long id;
    private Long guestId;
    private Long roomId;
    private LocalDate checkInDate;
    private LocalDate checkOutDate;
    private BigDecimal totalPrice;
    private String status;
}
