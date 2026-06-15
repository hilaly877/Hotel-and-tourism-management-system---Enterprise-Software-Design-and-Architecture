package com.hotel.guestservice.service;

import com.hotel.guestservice.exception.GuestNotFoundException;
import com.hotel.guestservice.model.Guest;
import com.hotel.guestservice.repository.GuestRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
public class GuestService {

    @Autowired
    private GuestRepository guestRepository;

    // Get all guests
    public List<Guest> getAllGuests() {
        return guestRepository.findAll();
    }

    // Get guest by ID
    public Guest getGuestById(Long id) {
        return guestRepository.findById(id)
                .orElseThrow(() -> new GuestNotFoundException(id));
    }

    // Register new guest
    public Guest registerGuest(Guest guest) {
        // Check if email already exists
        if (guestRepository.existsByEmail(guest.getEmail())) {
            throw new IllegalArgumentException("Email already registered: " + guest.getEmail());
        }
        // Check if phone already exists
        if (guestRepository.existsByPhone(guest.getPhone())) {
            throw new IllegalArgumentException("Phone number already registered: " + guest.getPhone());
        }
        return guestRepository.save(guest);
    }

    // Update guest details
    public Guest updateGuest(Long id, Guest updatedGuest) {
        Guest existingGuest = guestRepository.findById(id)
                .orElseThrow(() -> new GuestNotFoundException(id));

        // Check if the new email belongs to a different guest
        if (!existingGuest.getEmail().equals(updatedGuest.getEmail())
                && guestRepository.existsByEmail(updatedGuest.getEmail())) {
            throw new IllegalArgumentException("Email already in use: " + updatedGuest.getEmail());
        }

        existingGuest.setName(updatedGuest.getName());
        existingGuest.setEmail(updatedGuest.getEmail());
        existingGuest.setPhone(updatedGuest.getPhone());

        // Only update password if provided
        if (updatedGuest.getPassword() != null && !updatedGuest.getPassword().isEmpty()) {
            existingGuest.setPassword(updatedGuest.getPassword());
        }

        return guestRepository.save(existingGuest);
    }

    // Delete guest
    public void deleteGuest(Long id) {
        if (!guestRepository.existsById(id)) {
            throw new GuestNotFoundException(id);
        }
        guestRepository.deleteById(id);
    }

    // Get loyalty points
    public int getLoyaltyPoints(Long id) {
        Guest guest = guestRepository.findById(id)
                .orElseThrow(() -> new GuestNotFoundException(id));
        return guest.getLoyaltyPoints();
    }

    // Add loyalty points (called after a stay)
    public Guest addLoyaltyPoints(Long id, int points) {
        Guest guest = guestRepository.findById(id)
                .orElseThrow(() -> new GuestNotFoundException(id));
        int current = (guest.getLoyaltyPoints() == null) ? 0 : guest.getLoyaltyPoints();
        guest.setLoyaltyPoints(current + points);
        return guestRepository.save(guest);
    }
}
