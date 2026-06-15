package com.hotel.guestservice.repository;

import com.hotel.guestservice.model.Guest;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.Optional;

@Repository
public interface GuestRepository extends JpaRepository<Guest, Long> {

    // Find guest by email
    Optional<Guest> findByEmail(String email);

    // Check if email already exists
    boolean existsByEmail(String email);

    // Check if phone already exists
    boolean existsByPhone(String phone);
}
