package com.hotel.roomservice.repository;

import com.hotel.roomservice.model.Room;
import com.hotel.roomservice.model.RoomStatus;
import com.hotel.roomservice.model.RoomType;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.math.BigDecimal;
import java.util.List;
import java.util.Optional;

@Repository
public interface RoomRepository extends JpaRepository<Room, Long> {

    // Find room by room number
    Optional<Room> findByRoomNumber(String roomNumber);

    // Check if room number already exists
    boolean existsByRoomNumber(String roomNumber);

    // Find rooms by status (e.g., AVAILABLE)
    List<Room> findByStatus(RoomStatus status);

    // Find rooms by type
    List<Room> findByType(RoomType type);

    // Find available rooms by type
    List<Room> findByStatusAndType(RoomStatus status, RoomType type);

    // Find rooms within a price range
    List<Room> findByPricePerNightBetween(BigDecimal minPrice, BigDecimal maxPrice);

    // Find available rooms that fit a certain number of guests
    @Query("SELECT r FROM Room r WHERE r.status = 'AVAILABLE' AND r.maxGuests >= :guests")
    List<Room> findAvailableRoomsForGuests(@Param("guests") int guests);

    // Find rooms by floor
    List<Room> findByFloorNumber(int floorNumber);
}
