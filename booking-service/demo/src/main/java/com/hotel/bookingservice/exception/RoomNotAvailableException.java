package com.hotel.bookingservice.exception;

import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.ResponseStatus;

@ResponseStatus(HttpStatus.CONFLICT)
public class RoomNotAvailableException extends RuntimeException {

    public RoomNotAvailableException(String message) {
        super(message);
    }

    public RoomNotAvailableException(Long roomId, String checkIn, String checkOut) {
        super("Room " + roomId + " is not available from " + checkIn + " to " + checkOut);
    }
}
