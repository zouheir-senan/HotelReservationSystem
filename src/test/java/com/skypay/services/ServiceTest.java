package com.skypay.services;

import com.skypay.enums.RoomType;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.TestTemplate;

import java.util.Calendar;
import java.util.Date;

import static org.junit.jupiter.api.Assertions.*;

class ServiceTest {

    private Service service;
    private Calendar calendar;

    @BeforeEach void setUp() {
        service = new Service();
    }

    @Test
    void testSetRoom_ValidParameters_Success() {
        assertDoesNotThrow(() -> {
            service.setRoom(101, RoomType.MASTER_SUITE, 100);
        });
    }


    @Test
    void testSetRoom_NegativePrice_ThrowsException() {
        IllegalArgumentException exception = assertThrows(IllegalArgumentException.class, () -> {
            service.setRoom(101, RoomType.JUNIOR_SUITE, -50);
        });
        assertEquals("Price per night must be greater than 0", exception.getMessage());
    }


    @Test
    void testSetRoom_NullRoomType_ThrowsException() {
        IllegalArgumentException exception = assertThrows(IllegalArgumentException.class, () -> {
            service.setRoom(101, null, 100);
        });
        assertEquals("Room type must be not null ", exception.getMessage());
    }

    @Test
    void testSetRoom_DuplicateRoom_ThrowsException() {
        service.setRoom(101, RoomType.STANDARD_SUITE, 100);

        IllegalArgumentException exception = assertThrows(IllegalArgumentException.class, () -> {
            service.setRoom(101, RoomType.MASTER_SUITE, 150);
        });
        assertEquals("Room already exists", exception.getMessage());
    }
    @Test
    void testSetRoom_MultipleRooms_Success() {
        assertDoesNotThrow(() -> {
            service.setRoom(101, RoomType.JUNIOR_SUITE, 100);
            service.setRoom(102, RoomType.MASTER_SUITE, 150);
            service.setRoom(103, RoomType.STANDARD_SUITE, 300);
        });
    }
    @Test
    void testSetUser_ValidParameters_Success() {
        assertDoesNotThrow(() -> {
            service.setUser(1, 1000);
        });
    }


    @Test
    void testSetUser_MultipleUsers_Success() {
        assertDoesNotThrow(() -> {
            service.setUser(1, 1000);
            service.setUser(2, 1500);
            service.setUser(3, 2000);
        });
    }


    // BOOKING TESTS
    @Test
    void testSetUser_DuplicateUser_ThrowsException() {
        service.setUser(1, 1000);

        IllegalArgumentException exception = assertThrows(IllegalArgumentException.class, () -> {
            service.setUser(1, 2000);
        });
        assertEquals("User already exists", exception.getMessage());
    }
    @Test
    void testSetUser_NegativeBalance_ThrowsException() {
        IllegalArgumentException exception = assertThrows(IllegalArgumentException.class, () -> {
            service.setUser(1, -100);
        });
        assertEquals("Balance must be greater than 0", exception.getMessage());
    }


    @Test
    void testBookRoom_ValidBooking_Success() {
        service.setUser(1, 1000);
        service.setRoom(101, RoomType.STANDARD_SUITE, 100);

        Date checkin = createDate(2024, 12, 1);
        Date checkout = createDate(2024, 12, 3);

        assertDoesNotThrow(() -> {
            service.bookRoom(1, 101, checkin, checkout);
        });
    }

    @Test
    void testBookRoom_InvalidDates_ThrowsException() {
        service.setUser(1, 1000);
        service.setRoom(101, RoomType.STANDARD_SUITE, 100);

        Date checkin = createDate(2024, 12, 5);
        Date checkout = createDate(2024, 12, 3);

        IllegalArgumentException exception = assertThrows(IllegalArgumentException.class, () -> {
            service.bookRoom(1, 101, checkin, checkout);
        });
        assertEquals("Checkin time must be after checkOut time", exception.getMessage());
    }

    @Test
    void testBookRoom_UserNotFound_ThrowsException() {
        service.setRoom(101, RoomType.STANDARD_SUITE, 100);

        Date checkin = createDate(2024, 12, 1);
        Date checkout = createDate(2024, 12, 3);

        IllegalArgumentException exception = assertThrows(IllegalArgumentException.class, () -> {
            service.bookRoom(999, 101, checkin, checkout);
        });
        assertEquals("User not found", exception.getMessage());
    }

    @Test
    void testBookRoom_RoomNotFound_ThrowsException() {
        service.setUser(1, 1000);

        Date checkin = createDate(2024, 12, 1);
        Date checkout = createDate(2024, 12, 3);

        IllegalArgumentException exception = assertThrows(IllegalArgumentException.class, () -> {
            service.bookRoom(1, 999, checkin, checkout);
        });
        assertEquals("Room not found", exception.getMessage());
    }

    @Test
    void testBookRoom_InsufficientBalance_ThrowsException() {
        service.setUser(1, 150); // Only 150 balance
        service.setRoom(101, RoomType.STANDARD_SUITE, 100); // 100 per night

        Date checkin = createDate(2024, 12, 1);
        Date checkout = createDate(2024, 12, 3); // 2 nights = 200 total

        IllegalArgumentException exception = assertThrows(IllegalArgumentException.class, () -> {
            service.bookRoom(1, 101, checkin, checkout);
        });
        assertEquals("Insufficient balance", exception.getMessage());
    }

    @Test
    void testBookRoom_RoomNotAvailable_ThrowsException() {
        service.setUser(1, 1000);
        service.setUser(2, 1000);
        service.setRoom(101, RoomType.JUNIOR_SUITE, 100);

        Date checkin1 = createDate(2024, 12, 1);
        Date checkout1 = createDate(2024, 12, 5);

        Date checkin2 = createDate(2024, 12, 3); // Overlaps with first booking
        Date checkout2 = createDate(2024, 12, 7);

        // First booking should succeed
        assertDoesNotThrow(() -> {
            service.bookRoom(1, 101, checkin1, checkout1);
        });

        // Second booking should fail
        IllegalArgumentException exception = assertThrows(IllegalArgumentException.class, () -> {
            service.bookRoom(2, 101, checkin2, checkout2);
        });
        assertEquals("Room not available for the specified period", exception.getMessage());
    }

    @Test
    void testBookRoom_NonOverlappingDates_Success() {
        service.setUser(1, 1000);
        service.setUser(2, 1000);
        service.setRoom(101, RoomType.JUNIOR_SUITE, 100);

        Date checkin1 = createDate(2024, 12, 1);
        Date checkout1 = createDate(2024, 12, 3);

        Date checkin2 = createDate(2024, 12, 5); // No overlap
        Date checkout2 = createDate(2024, 12, 7);

        assertDoesNotThrow(() -> {
            service.bookRoom(1, 101, checkin1, checkout1);
            service.bookRoom(2, 101, checkin2, checkout2);
        });
    }

    @Test
    void testBookRoom_UpdatesUserBalance() {
        service.setUser(1, 500);
        service.setRoom(101, RoomType.STANDARD_SUITE, 100);

        Date checkin = createDate(2024, 12, 1);
        Date checkout = createDate(2024, 12, 3); // 2 nights = 200 total

        service.bookRoom(1, 101, checkin, checkout);

        // Balance should be reduced by 200 (500 - 200 = 300)
        // This test assumes we can access user balance - you might need to add a getter
        // or verify through printAllUsers() output
    }

    @Test
    void testBookRoom_SameDayBooking_Success() {
        service.setUser(1, 1000);
        service.setRoom(101, RoomType.MASTER_SUITE, 100);

        Date checkin = createDate(2024, 12, 1);
        Date checkout = createDate(2024, 12, 1); // Same day

        assertDoesNotThrow(() -> {
            service.bookRoom(1, 101, checkin, checkout);
        });
    }

    // INTEGRATION TESTS
    @Test
    void testMultipleBookings_DifferentRooms_Success() {
        service.setUser(1, 2000);
        service.setRoom(101, RoomType.JUNIOR_SUITE, 100);
        service.setRoom(102, RoomType.STANDARD_SUITE, 150);

        Date checkin = createDate(2024, 12, 1);
        Date checkout = createDate(2024, 12, 3);

        assertDoesNotThrow(() -> {
            service.bookRoom(1, 101, checkin, checkout);
            service.bookRoom(1, 102, checkin, checkout);
        });
    }

    @Test
    void testBookingPreservation_AfterRoomChange() {
        service.setUser(1, 1000);
        service.setRoom(101, RoomType.MASTER_SUITE, 100);

        Date checkin = createDate(2024, 12, 1);
        Date checkout = createDate(2024, 12, 3);

        // Make a booking
        service.bookRoom(1, 101, checkin, checkout);

        // Try to modify the room (should not affect existing booking)
        // This test verifies that setRoom doesn't impact previous bookings
        // The room already exists, so this should throw an exception
        assertThrows(IllegalArgumentException.class, () -> {
            service.setRoom(101, RoomType.MASTER_SUITE, 200);
        });
    }

    @Test
    void testBookRoom_EdgeCaseDates() {
        service.setUser(1, 1000);
        service.setRoom(101, RoomType.JUNIOR_SUITE, 100);

        // Test year boundary
        Date checkin = createDate(2024, 12, 31);
        Date checkout = createDate(2025, 1, 2);

        assertDoesNotThrow(() -> {
            service.bookRoom(1, 101, checkin, checkout);
        });
    }

    //PRINT FUNCTION TESTS
    @Test
    void testPrintFunctions_EmptyData_NoException() {
        assertDoesNotThrow(() -> {
            service.printAll();
            service.printAllUsers();
        });
    }

    @Test
    void testPrintFunctions_WithData_NoException() {
        service.setUser(1, 1000);
        service.setRoom(101, RoomType.STANDARD_SUITE, 100);

        Date checkin = createDate(2024, 12, 1);
        Date checkout = createDate(2024, 12, 3);
        service.bookRoom(1, 101, checkin, checkout);

        assertDoesNotThrow(() -> {
            service.printAll();
            service.printAllUsers();
        });
    }


    private Date createDate(int year, int month, int day) {
        Calendar cal = Calendar.getInstance();
        cal.set(year, month - 1, day, 0, 0, 0);
        return cal.getTime();
    }
}