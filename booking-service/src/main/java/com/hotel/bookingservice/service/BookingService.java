package com.hotel.bookingservice.service;

import com.hotel.bookingservice.dto.BookingResponse;
import com.hotel.bookingservice.dto.GuestDTO;
import com.hotel.bookingservice.dto.RoomDTO;
import com.hotel.bookingservice.exception.BookingNotFoundException;
import com.hotel.bookingservice.exception.RoomNotAvailableException;
import com.hotel.bookingservice.model.Booking;
import com.hotel.bookingservice.model.BookingStatus;
import com.hotel.bookingservice.repository.BookingRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;
import org.springframework.web.client.RestTemplate;
import org.springframework.web.client.ResourceAccessException;

import java.math.BigDecimal;
import java.time.temporal.ChronoUnit;
import java.util.List;

@Service
public class BookingService {

    @Autowired
    private BookingRepository bookingRepository;

    @Autowired
    private RestTemplate restTemplate;

    @Value("${services.guest-service.url}")
    private String guestServiceUrl;

    @Value("${services.room-service.url}")
    private String roomServiceUrl;

    // ─── GET ALL BOOKINGS ─────────────────────────────────────────────────────
    public List<Booking> getAllBookings() {
        return bookingRepository.findAll();
    }

    // ─── GET BOOKING BY ID ────────────────────────────────────────────────────
    public Booking getBookingById(Long id) {
        return bookingRepository.findById(id)
                .orElseThrow(() -> new BookingNotFoundException(id));
    }

    // ─── GET ENRICHED BOOKING ────────────────────────────────────────────────
    public BookingResponse getEnrichedBooking(Long id) {
        Booking booking = bookingRepository.findById(id)
                .orElseThrow(() -> new BookingNotFoundException(id));

        GuestDTO guest = null;
        RoomDTO room = null;

        // Try to call Guest Service — if down use mock
        try {
            guest = restTemplate.getForObject(
                    guestServiceUrl + "/api/guests/" + booking.getGuestId(),
                    GuestDTO.class);
        } catch (Exception e) {
            System.out.println("⚠️ Guest Service unavailable — using mock data");
            guest = createMockGuest(booking.getGuestId());
        }

        // Try to call Room Service — if down use mock
        try {
            room = restTemplate.getForObject(
                    roomServiceUrl + "/api/rooms/" + booking.getRoomId(),
                    RoomDTO.class);
        } catch (Exception e) {
            System.out.println("⚠️ Room Service unavailable — using mock data");
            room = createMockRoom(booking.getRoomId());
        }

        return buildBookingResponse(booking, guest, room);
    }

    // ─── CREATE BOOKING ───────────────────────────────────────────────────────
    public Booking createBooking(Booking booking) {

        // Validate dates
        if (!booking.getCheckInDate().isBefore(booking.getCheckOutDate())) {
            throw new IllegalArgumentException(
                "Check-in date must be before check-out date");
        }

        // Try to verify guest — if Guest Service down, skip verification
        try {
            restTemplate.getForObject(
                    guestServiceUrl + "/api/guests/" + booking.getGuestId(),
                    GuestDTO.class);
        } catch (ResourceAccessException e) {
            System.out.println(
                "⚠️ Guest Service unavailable — skipping guest verification");
        }

        // Try to get room details — if Room Service down, use default price
        RoomDTO room = null;
        try {
            room = restTemplate.getForObject(
                    roomServiceUrl + "/api/rooms/" + booking.getRoomId(),
                    RoomDTO.class);

            // Check room is AVAILABLE
            if (room != null && !"AVAILABLE".equals(room.getStatus())) {
                throw new RoomNotAvailableException(
                    "Room " + room.getRoomNumber() +
                    " is currently " + room.getStatus());
            }
        } catch (ResourceAccessException e) {
            System.out.println(
                "⚠️ Room Service unavailable — skipping room verification");
        }

        // Check date conflicts
        List<Booking> overlapping = bookingRepository.findOverlappingBookings(
                booking.getRoomId(),
                booking.getCheckInDate(),
                booking.getCheckOutDate());

        if (!overlapping.isEmpty()) {
            throw new RoomNotAvailableException(
                    booking.getRoomId(),
                    booking.getCheckInDate().toString(),
                    booking.getCheckOutDate().toString());
        }

        // Calculate total price
        if (room != null && room.getPricePerNight() != null) {
            long nights = ChronoUnit.DAYS.between(
                booking.getCheckInDate(), booking.getCheckOutDate());
            booking.setTotalPrice(
                room.getPricePerNight().multiply(BigDecimal.valueOf(nights)));
        } else if (booking.getTotalPrice() == null) {
            // If room service down and no price — set default
            booking.setTotalPrice(BigDecimal.ZERO);
        }

        booking.setStatus(BookingStatus.CONFIRMED);
        return bookingRepository.save(booking);
    }

    // ─── UPDATE BOOKING ───────────────────────────────────────────────────────
    public Booking updateBooking(Long id, Booking updatedBooking) {
        Booking existing = bookingRepository.findById(id)
                .orElseThrow(() -> new BookingNotFoundException(id));

        if (existing.getStatus() == BookingStatus.CANCELLED) {
            throw new IllegalArgumentException(
                "Cannot modify a cancelled booking");
        }

        if (!updatedBooking.getCheckInDate()
                .isBefore(updatedBooking.getCheckOutDate())) {
            throw new IllegalArgumentException(
                "Check-in date must be before check-out date");
        }

        existing.setCheckInDate(updatedBooking.getCheckInDate());
        existing.setCheckOutDate(updatedBooking.getCheckOutDate());
        existing.setNumGuests(updatedBooking.getNumGuests());
        existing.setSpecialRequests(updatedBooking.getSpecialRequests());

        // Recalculate price if Room Service available
        try {
            RoomDTO room = restTemplate.getForObject(
                    roomServiceUrl + "/api/rooms/" + existing.getRoomId(),
                    RoomDTO.class);
            if (room != null && room.getPricePerNight() != null) {
                long nights = ChronoUnit.DAYS.between(
                    updatedBooking.getCheckInDate(),
                    updatedBooking.getCheckOutDate());
                existing.setTotalPrice(
                    room.getPricePerNight().multiply(BigDecimal.valueOf(nights)));
            }
        } catch (Exception e) {
            System.out.println(
                "⚠️ Room Service unavailable — keeping existing price");
        }

        return bookingRepository.save(existing);
    }

    // ─── CANCEL BOOKING ───────────────────────────────────────────────────────
    public Booking cancelBooking(Long id) {
        Booking booking = bookingRepository.findById(id)
                .orElseThrow(() -> new BookingNotFoundException(id));

        if (booking.getStatus() == BookingStatus.CANCELLED) {
            throw new IllegalArgumentException("Booking is already cancelled");
        }
        if (booking.getStatus() == BookingStatus.COMPLETED ||
            booking.getStatus() == BookingStatus.CHECKED_OUT) {
            throw new IllegalArgumentException(
                "Cannot cancel a completed booking");
        }

        booking.setStatus(BookingStatus.CANCELLED);
        return bookingRepository.save(booking);
    }

    // ─── GET BY GUEST ─────────────────────────────────────────────────────────
    public List<Booking> getBookingsByGuest(Long guestId) {
        return bookingRepository.findByGuestIdOrderByCreatedAtDesc(guestId);
    }

    // ─── GET BY STATUS ────────────────────────────────────────────────────────
    public List<Booking> getBookingsByStatus(BookingStatus status) {
        return bookingRepository.findByStatus(status);
    }

    // ─── UPDATE STATUS ────────────────────────────────────────────────────────
    public Booking updateBookingStatus(Long id, BookingStatus status) {
        Booking booking = bookingRepository.findById(id)
                .orElseThrow(() -> new BookingNotFoundException(id));
        booking.setStatus(status);
        return bookingRepository.save(booking);
    }

    // ─── MOCK DATA (used when other services are down) ────────────────────────
    private GuestDTO createMockGuest(Long guestId) {
        GuestDTO guest = new GuestDTO();
        guest.setId(guestId);
        guest.setName("Guest " + guestId);
        guest.setEmail("guest" + guestId + "@hotel.com");
        guest.setPhone("N/A");
        return guest;
    }

    private RoomDTO createMockRoom(Long roomId) {
        RoomDTO room = new RoomDTO();
        room.setId(roomId);
        room.setRoomNumber("Room-" + roomId);
        room.setType("STANDARD");
        room.setPricePerNight(BigDecimal.ZERO);
        room.setStatus("AVAILABLE");
        return room;
    }

    // ─── HELPER ───────────────────────────────────────────────────────────────
    private BookingResponse buildBookingResponse(
            Booking booking, GuestDTO guest, RoomDTO room) {
        BookingResponse response = new BookingResponse();
        response.setBookingId(booking.getId());
        response.setStatus(booking.getStatus());
        response.setCheckInDate(booking.getCheckInDate());
        response.setCheckOutDate(booking.getCheckOutDate());
        response.setNumGuests(booking.getNumGuests());
        response.setTotalPrice(booking.getTotalPrice());
        response.setSpecialRequests(booking.getSpecialRequests());
        response.setCreatedAt(booking.getCreatedAt());

        if (guest != null) {
            response.setGuestId(guest.getId());
            response.setGuestName(guest.getName());
            response.setGuestEmail(guest.getEmail());
            response.setGuestPhone(guest.getPhone());
        }

        if (room != null) {
            response.setRoomId(room.getId());
            response.setRoomNumber(room.getRoomNumber());
            response.setRoomType(room.getType());
            response.setPricePerNight(room.getPricePerNight());
            response.setAmenities(room.getAmenities());
        }

        return response;
    }
}