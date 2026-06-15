package com.hotel.roomservice.controller;

import com.hotel.roomservice.model.Room;
import com.hotel.roomservice.model.RoomStatus;
import com.hotel.roomservice.model.RoomType;
import com.hotel.roomservice.service.RoomService;
import jakarta.validation.Valid;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.math.BigDecimal;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

@RestController
@RequestMapping("/api/rooms")
@CrossOrigin(origins = "*")
public class RoomController {

    @Autowired
    private RoomService roomService;

    // ✅ GET /api/rooms — Retrieve all rooms
    @GetMapping
    public ResponseEntity<List<Room>> getAllRooms() {
        return ResponseEntity.ok(roomService.getAllRooms());
    }

    // ✅ GET /api/rooms/{id} — Retrieve room by ID
    @GetMapping("/{id}")
    public ResponseEntity<Room> getRoomById(@PathVariable Long id) {
        return ResponseEntity.ok(roomService.getRoomById(id));
    }

    // ✅ GET /api/rooms/number/{roomNumber} — Retrieve room by room number
    @GetMapping("/number/{roomNumber}")
    public ResponseEntity<Room> getRoomByNumber(@PathVariable String roomNumber) {
        return ResponseEntity.ok(roomService.getRoomByNumber(roomNumber));
    }

    // ✅ POST /api/rooms — Add a new room
    @PostMapping
    public ResponseEntity<Room> addRoom(@Valid @RequestBody Room room) {
        Room savedRoom = roomService.addRoom(room);
        return new ResponseEntity<>(savedRoom, HttpStatus.CREATED);
    }

    // ✅ PUT /api/rooms/{id} — Update room details
    @PutMapping("/{id}")
    public ResponseEntity<Room> updateRoom(@PathVariable Long id,
                                            @Valid @RequestBody Room room) {
        return ResponseEntity.ok(roomService.updateRoom(id, room));
    }

    // ✅ DELETE /api/rooms/{id} — Remove a room
    @DeleteMapping("/{id}")
    public ResponseEntity<Map<String, String>> deleteRoom(@PathVariable Long id) {
        roomService.deleteRoom(id);
        Map<String, String> response = new HashMap<>();
        response.put("message", "Room with ID " + id + " deleted successfully");
        return ResponseEntity.ok(response);
    }

    // ✅ GET /api/rooms/available — Get all available rooms
    @GetMapping("/available")
    public ResponseEntity<List<Room>> getAvailableRooms() {
        return ResponseEntity.ok(roomService.getAvailableRooms());
    }

    // ✅ GET /api/rooms/available?guests=2 — Get available rooms for a number of guests
    @GetMapping("/available/guests")
    public ResponseEntity<List<Room>> getAvailableRoomsForGuests(@RequestParam int guests) {
        return ResponseEntity.ok(roomService.getAvailableRoomsForGuests(guests));
    }

    // ✅ GET /api/rooms/type/{type} — Get rooms by type
    @GetMapping("/type/{type}")
    public ResponseEntity<List<Room>> getRoomsByType(@PathVariable RoomType type) {
        return ResponseEntity.ok(roomService.getRoomsByType(type));
    }

    // ✅ GET /api/rooms/available/type/{type} — Get available rooms by type
    @GetMapping("/available/type/{type}")
    public ResponseEntity<List<Room>> getAvailableRoomsByType(@PathVariable RoomType type) {
        return ResponseEntity.ok(roomService.getAvailableRoomsByType(type));
    }

    // ✅ GET /api/rooms/price?min=100&max=500 — Get rooms in price range
    @GetMapping("/price")
    public ResponseEntity<List<Room>> getRoomsByPriceRange(
            @RequestParam BigDecimal min,
            @RequestParam BigDecimal max) {
        return ResponseEntity.ok(roomService.getRoomsByPriceRange(min, max));
    }

    // ✅ PATCH /api/rooms/{id}/status — Update room status only
    @PatchMapping("/{id}/status")
    public ResponseEntity<Room> updateRoomStatus(@PathVariable Long id,
                                                  @RequestParam RoomStatus status) {
        return ResponseEntity.ok(roomService.updateRoomStatus(id, status));
    }
}
