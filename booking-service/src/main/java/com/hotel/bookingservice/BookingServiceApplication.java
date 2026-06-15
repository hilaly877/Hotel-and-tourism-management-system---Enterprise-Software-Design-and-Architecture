package com.hotel.bookingservice;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.context.annotation.Bean;
import org.springframework.web.client.RestTemplate;

@SpringBootApplication
public class BookingServiceApplication {

    public static void main(String[] args) {
        SpringApplication.run(BookingServiceApplication.class, args);
    }

    // RestTemplate is used to call other microservices (Guest Service, Room Service)
    @Bean
    public RestTemplate restTemplate() {
        return new RestTemplate();
    }
}
