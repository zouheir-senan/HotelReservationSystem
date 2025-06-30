package com.skypay.entities;

import java.util.Date;

public record Booking(int userId, int roomId, Date checkinDate, Date checkoutDate, long creationTime, User userSnapshot, Room roomSnapshot) {
    public Booking(int userId, int roomId, Date checkinDate, Date checkoutDate, User userSnapshot, Room roomSnapshot) {
        this(userId, roomId, checkinDate, checkoutDate, System.currentTimeMillis(), userSnapshot, roomSnapshot);
    }
}