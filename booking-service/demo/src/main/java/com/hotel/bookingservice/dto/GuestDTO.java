package com.hotel.bookingservice.dto;

import lombok.Data;
import lombok.NoArgsConstructor;

// This DTO mirrors the Guest object from Guest Service
// Used when calling Guest Service via RestTemplate
@Data
@NoArgsConstructor
public class GuestDTO {
    private Long id;
    private String name;
    private String email;
    private String phone;
    private int loyaltyPoints;
}
