package com.hotel.guestservice.controller;

import com.hotel.guestservice.model.Guest;
import com.hotel.guestservice.service.GuestService;
import jakarta.validation.Valid;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

@RestController
@RequestMapping("/api/guests")
@CrossOrigin(origins = "*") // Allow all origins for development
public class GuestController {

    @Autowired
    private GuestService guestService;

    // ✅ GET /api/guests — Retrieve all guests
    @GetMapping
    public ResponseEntity<List<Guest>> getAllGuests() {
        List<Guest> guests = guestService.getAllGuests();
        return ResponseEntity.ok(guests);
    }

    // ✅ GET /api/guests/{id} — Retrieve guest by ID
    @GetMapping("/{id}")
    public ResponseEntity<Guest> getGuestById(@PathVariable Long id) {
        Guest guest = guestService.getGuestById(id);
        return ResponseEntity.ok(guest);
    }

    // ✅ POST /api/guests — Register a new guest
    @PostMapping
    public ResponseEntity<Guest> registerGuest(@Valid @RequestBody Guest guest) {
        Guest savedGuest = guestService.registerGuest(guest);
        return new ResponseEntity<>(savedGuest, HttpStatus.CREATED);
    }

    // ✅ PUT /api/guests/{id} — Update guest details
    @PutMapping("/{id}")
    public ResponseEntity<Guest> updateGuest(@PathVariable Long id,
                                              @Valid @RequestBody Guest guest) {
        Guest updatedGuest = guestService.updateGuest(id, guest);
        return ResponseEntity.ok(updatedGuest);
    }

    // ✅ DELETE /api/guests/{id} — Delete guest account
    @DeleteMapping("/{id}")
    public ResponseEntity<Map<String, String>> deleteGuest(@PathVariable Long id) {
        guestService.deleteGuest(id);
        Map<String, String> response = new HashMap<>();
        response.put("message", "Guest with ID " + id + " deleted successfully");
        return ResponseEntity.ok(response);
    }

    // ✅ GET /api/guests/{id}/loyalty-points — Get guest loyalty points
    @GetMapping("/{id}/loyalty-points")
    public ResponseEntity<Map<String, Object>> getLoyaltyPoints(@PathVariable Long id) {
        int points = guestService.getLoyaltyPoints(id);
        Map<String, Object> response = new HashMap<>();
        response.put("guestId", id);
        response.put("loyaltyPoints", points);
        return ResponseEntity.ok(response);
    }

    // ✅ POST /api/guests/{id}/loyalty-points — Add loyalty points
    @PostMapping("/{id}/loyalty-points")
    public ResponseEntity<?> addLoyaltyPoints(@PathVariable Long id,
                                              @RequestBody Map<String, Integer> payload) {
        Integer points = null;
        if (payload != null) {
            if (payload.containsKey("points")) {
                points = payload.get("points");
            } else if (payload.containsKey("loyaltyPoints")) {
                points = payload.get("loyaltyPoints");
            }
        }
        if (points == null) {
            Map<String, String> err = new HashMap<>();
            err.put("error", "Missing 'points' in request body (expected 'points' or 'loyaltyPoints')");
            return ResponseEntity.badRequest().body(err);
        }

        Guest updatedGuest = guestService.addLoyaltyPoints(id, points);
        return ResponseEntity.ok(updatedGuest);
    }
}
