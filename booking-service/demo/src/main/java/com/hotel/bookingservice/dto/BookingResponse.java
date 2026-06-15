package com.hotel.bookingservice.dto;

import com.hotel.bookingservice.model.BookingStatus;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.math.BigDecimal;
import java.time.LocalDate;
import java.time.LocalDateTime;

// This is the enriched response returned to the client
// It combines Booking data with Guest and Room details
@Data
@NoArgsConstructor
public class BookingResponse {

    private Long bookingId;
    private BookingStatus status;
    private LocalDate checkInDate;
    private LocalDate checkOutDate;
    private int numGuests;
    private BigDecimal totalPrice;
    private String specialRequests;
    private LocalDateTime createdAt;

    // Guest details (from Guest Service)
    private Long guestId;
    private String guestName;
    private String guestEmail;
    private String guestPhone;

    // Room details (from Room Service)
    private Long roomId;
    private String roomNumber;
    private String roomType;
    private BigDecimal pricePerNight;
    private String amenities;
}
