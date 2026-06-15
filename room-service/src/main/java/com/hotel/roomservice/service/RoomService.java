package com.hotel.roomservice.service;

import com.hotel.roomservice.exception.RoomNotFoundException;
import com.hotel.roomservice.model.Room;
import com.hotel.roomservice.model.RoomStatus;
import com.hotel.roomservice.model.RoomType;
import com.hotel.roomservice.repository.RoomRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.math.BigDecimal;
import java.util.List;

@Service
public class RoomService {

    @Autowired
    private RoomRepository roomRepository;

    // Get all rooms
    public List<Room> getAllRooms() {
        return roomRepository.findAll();
    }

    // Get room by ID
    public Room getRoomById(Long id) {
        return roomRepository.findById(id)
                .orElseThrow(() -> new RoomNotFoundException(id));
    }

    // Get room by room number
    public Room getRoomByNumber(String roomNumber) {
        return roomRepository.findByRoomNumber(roomNumber)
                .orElseThrow(() -> new RoomNotFoundException("Room not found with number: " + roomNumber));
    }

    // Add a new room
    public Room addRoom(Room room) {
        if (roomRepository.existsByRoomNumber(room.getRoomNumber())) {
            throw new IllegalArgumentException("Room number already exists: " + room.getRoomNumber());
        }
        return roomRepository.save(room);
    }

    // Update room details
    public Room updateRoom(Long id, Room updatedRoom) {
        Room existingRoom = roomRepository.findById(id)
                .orElseThrow(() -> new RoomNotFoundException(id));

        // Check if new room number conflicts with another room
        if (!existingRoom.getRoomNumber().equals(updatedRoom.getRoomNumber())
                && roomRepository.existsByRoomNumber(updatedRoom.getRoomNumber())) {
            throw new IllegalArgumentException("Room number already in use: " + updatedRoom.getRoomNumber());
        }

        existingRoom.setRoomNumber(updatedRoom.getRoomNumber());
        existingRoom.setType(updatedRoom.getType());
        existingRoom.setPricePerNight(updatedRoom.getPricePerNight());
        existingRoom.setStatus(updatedRoom.getStatus());
        existingRoom.setAmenities(updatedRoom.getAmenities());
        existingRoom.setMaxGuests(updatedRoom.getMaxGuests());
        existingRoom.setFloorNumber(updatedRoom.getFloorNumber());
        existingRoom.setDescription(updatedRoom.getDescription());

        return roomRepository.save(existingRoom);
    }

    // Delete a room
    public void deleteRoom(Long id) {
        if (!roomRepository.existsById(id)) {
            throw new RoomNotFoundException(id);
        }
        roomRepository.deleteById(id);
    }

    // Get all available rooms
    public List<Room> getAvailableRooms() {
        return roomRepository.findByStatus(RoomStatus.AVAILABLE);
    }

    // Get rooms by type
    public List<Room> getRoomsByType(RoomType type) {
        return roomRepository.findByType(type);
    }

    // Get available rooms by type
    public List<Room> getAvailableRoomsByType(RoomType type) {
        return roomRepository.findByStatusAndType(RoomStatus.AVAILABLE, type);
    }

    // Get rooms within a price range
    public List<Room> getRoomsByPriceRange(BigDecimal minPrice, BigDecimal maxPrice) {
        return roomRepository.findByPricePerNightBetween(minPrice, maxPrice);
    }

    // Get available rooms for a number of guests
    public List<Room> getAvailableRoomsForGuests(int guests) {
        return roomRepository.findAvailableRoomsForGuests(guests);
    }

    // Update room status only (used by Booking Service)
    public Room updateRoomStatus(Long id, RoomStatus status) {
        Room room = roomRepository.findById(id)
                .orElseThrow(() -> new RoomNotFoundException(id));
        room.setStatus(status);
        return roomRepository.save(room);
    }
}
