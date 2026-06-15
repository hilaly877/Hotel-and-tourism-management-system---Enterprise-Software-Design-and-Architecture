package com.hotel.paymentservice.config;

import com.stripe.Stripe;
import jakarta.annotation.PostConstruct;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Configuration;

@Configuration
public class StripeConfig {

    @Value("${stripe.secret.key}")
    private String secretKey;

    // This runs automatically when the app starts
    // It initializes Stripe with your secret key
    @PostConstruct
    public void init() {
        Stripe.apiKey = secretKey;
        System.out.println("✅ Stripe initialized successfully");
    }
}