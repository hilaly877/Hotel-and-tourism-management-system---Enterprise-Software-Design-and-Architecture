package com.hotel.bookingservice.repository;

import com.hotel.bookingservice.model.Booking;
import com.hotel.bookingservice.model.BookingStatus;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.time.LocalDate;
import java.util.List;

@Repository
public interface BookingRepository extends JpaRepository<Booking, Long> {

    // Get all bookings for a specific guest
    List<Booking> findByGuestId(Long guestId);

    // Get all bookings for a specific room
    List<Booking> findByRoomId(Long roomId);

    // Get bookings by status
    List<Booking> findByStatus(BookingStatus status);

    // Get active bookings for a guest (not cancelled/completed)
    List<Booking> findByGuestIdAndStatus(Long guestId, BookingStatus status);

    // Check if a room is already booked for a given date range (overlap check)
    @Query("SELECT b FROM Booking b WHERE b.roomId = :roomId " +
           "AND b.status NOT IN ('CANCELLED', 'COMPLETED', 'CHECKED_OUT') " +
           "AND b.checkInDate < :checkOutDate " +
           "AND b.checkOutDate > :checkInDate")
    List<Booking> findOverlappingBookings(
            @Param("roomId") Long roomId,
            @Param("checkInDate") LocalDate checkInDate,
            @Param("checkOutDate") LocalDate checkOutDate);

    // Get bookings by guest ordered by most recent first
    List<Booking> findByGuestIdOrderByCreatedAtDesc(Long guestId);
}
