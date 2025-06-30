package com.skypay.services;

import com.skypay.entities.Booking;
import com.skypay.entities.Room;
import com.skypay.entities.User;
import com.skypay.enums.RoomType;

import java.util.ArrayList;
import java.util.Calendar;
import java.util.Date;
import java.util.Optional;

public class Service {
    private ArrayList<Room> rooms = new ArrayList<>();
    private ArrayList<User> users = new ArrayList<>();
    private ArrayList<Booking> bookings = new ArrayList<>();


    public void setRoom(int roomNumber, RoomType roomType, int pricePerNight) {

        if (pricePerNight < 0) {
            throw new IllegalArgumentException("Price per night must be greater than 0");
        }
        if (roomType == null) {
            throw new IllegalArgumentException("Room type must be not null ");
        }
        if (rooms.stream().anyMatch(room -> room.roomNumber() == roomNumber)) {
            throw new IllegalArgumentException("Room already exists");
        }
        rooms.add(new Room(roomNumber, roomType, pricePerNight));
    }

    public void setUser(int userId, int balance) {
        if (balance < 0) {
            throw new IllegalArgumentException("Balance must be greater than 0");
        }
        if (users.stream().anyMatch(user -> user.id() == userId)){
            throw new IllegalArgumentException("User already exists");
        }
        users.add(new User(userId, balance));
    }

    public void bookRoom(int userId, int roomNumber, Date checkin, Date checkOut) {
        // Validate dates
        if (checkin.after(checkOut)) {
            throw new IllegalArgumentException("Checkin time must be after checkOut time");
        }

        // 1. Find the user and room.
        Optional<User> userOpt = users.stream().filter(user -> user.id() == userId).findFirst();
        Optional<Room> roomOpt = rooms.stream().filter(room -> room.roomNumber() == roomNumber).findFirst();

        if (!userOpt.isPresent()) {
            throw new IllegalArgumentException("User not found");
        }
        if (!roomOpt.isPresent()) {
            throw new IllegalArgumentException("Room not found");
        }
        User user = userOpt.get();
        Room room = roomOpt.get();

        // 2. Check if the room is available for the given period.
            boolean available = bookings.stream()
                    .filter(b -> b.roomId() == room.roomNumber())
                    .noneMatch(b-> datesOverlap(checkin, checkOut, b.checkinDate(), b.checkoutDate()));
            if (!available) {
                throw new IllegalArgumentException("Room not available for the specified period");
            }

        // 3. Check if the user has enough balance.
        int numberOfNights =  calculateNights(checkin, checkOut);
        int totalprice = numberOfNights * room.pricePerNight();
        if (totalprice > user.balance())
        {
            throw new IllegalArgumentException("Insufficient balance");
        }
                // 4. Create booking with snapshots and update user balance User userSnapshot = new User(user.id(), user.balance());
        Room roomSnapshot = new Room(room.roomNumber(), room.roomType(), room.pricePerNight());
        User userSnapshot = new User(user.id(), user.balance());
        bookings.add(new Booking(userId, roomNumber, checkin, checkOut, userSnapshot, roomSnapshot));
        user.setBalance(user.balance() - totalprice);
    }

    public void printAll() {
        System.out.println("Rooms : ");
        for (int i = rooms.size()-1; i >=0; --i) {
            var room = rooms.get(i);
             System.out.printf("Room %d - Type: %s, Price/night: %d%n",
                    room.roomNumber(), room.roomType(), room.pricePerNight());
        }
        System.out.println("Bookings : ");
        for (int i = bookings.size()-1; i >=0; --i) {
            var booking = bookings.get(i);
            System.out.printf("Booking: User %d, Room %d, Check-in: %s, Check-out: %s%n",
                    booking.userId(), booking.roomId(),
                    formatDate(booking.checkinDate()), formatDate(booking.checkoutDate()));
            System.out.printf("  User at booking: ID=%d, Balance=%d%n",
                    booking.userSnapshot().id(), booking.userSnapshot().balance());
            System.out.printf("  Room at booking: ID=%d, Type=%s, Price/night=%d%n",
                    booking.roomSnapshot().roomNumber(), booking.roomSnapshot().roomType(),
                    booking.roomSnapshot().pricePerNight());
            System.out.println();
        }
    }

    public void printAllUsers() {
        System.out.println("Users : ");
        for (int i = users.size()-1; i >=0; --i) {
            var user = users.get(i);
            System.out.println("User ID: " + user.id() + ", Balance: " + user.balance());
        }
    }

    public User getUser(int i) {
        return users.stream().filter(user -> user.id() == i).findFirst().orElse(null);
    }


    public Room getRoom(int i) {
        return rooms.stream().filter(room -> room.roomNumber() == i).findFirst().orElse(null);
    }


    private boolean datesOverlap(Date start1, Date end1, Date start2, Date end2) {
        return start1.before(end2) && end1.after(start2);
    }
    private int calculateNights(Date checkin, Date checkout) {
        long diffInMillies = checkout.getTime() - checkin.getTime();
        return (int) (diffInMillies / (1000 * 60 * 60 * 24));
    }
    private String formatDate(Date date) {
        Calendar cal = Calendar.getInstance();
        cal.setTime(date);
        return String.format("%02d/%02d/%04d",
                cal.get(Calendar.DAY_OF_MONTH),
                cal.get(Calendar.MONTH) + 1,
                cal.get(Calendar.YEAR));
    }
}
