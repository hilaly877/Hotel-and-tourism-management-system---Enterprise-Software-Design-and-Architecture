package com.hotel.bookingservice.dto;

import lombok.Data;
import lombok.NoArgsConstructor;

import java.math.BigDecimal;

// This DTO mirrors the Room object from Room Service
// Used when calling Room Service via RestTemplate
@Data
@NoArgsConstructor
public class RoomDTO {
    private Long id;
    private String roomNumber;
    private String type;
    private BigDecimal pricePerNight;
    private String status;
    private String amenities;
    private int maxGuests;
    private int floorNumber;
    private String description;
}
