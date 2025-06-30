package com.skypay.services;

import com.skypay.enums.RoomType;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.*;

import java.io.ByteArrayOutputStream;
import java.io.PrintStream;
import java.text.SimpleDateFormat;

public class HotelServiceTest {
    private Service hotelService;
    private final SimpleDateFormat sdf = new SimpleDateFormat("dd/MM/yyyy");
    private final PrintStream originalOut = System.out;
    private ByteArrayOutputStream outContent;


    @BeforeEach
    void setUp() {
        hotelService = new Service();
        outContent = new ByteArrayOutputStream();
        System.setOut(new PrintStream(outContent));
    }
    @Test
    void testFullTestCase() throws Exception {

        hotelService.setRoom(1, RoomType.STANDARD_SUITE, 1000);
        hotelService.setRoom(2, RoomType.JUNIOR_SUITE, 2000);
        hotelService.setRoom(3, RoomType.MASTER_SUITE, 3000);


        hotelService.setUser(1, 5000);
        hotelService.setUser(2, 10000);



        assertThrows(IllegalArgumentException.class, () -> {
            hotelService.bookRoom(1, 2, sdf.parse("30/06/2026"), sdf.parse("07/07/2026"));
        }, "Insufficient balance");
        assertEquals(5000, hotelService.getUser(1).balance(), "User 1 balance should not change after failed booking.");


        assertThrows(IllegalArgumentException.class, () -> {
            hotelService.bookRoom(1, 2, sdf.parse("07/07/2026"), sdf.parse("30/06/2026"));
        }, "Should throw exception for Insufficient balance.");

        hotelService.bookRoom(1, 1, sdf.parse("07/07/2026"), sdf.parse("08/07/2026"));
        assertEquals(4000, hotelService.getUser(1).balance(), "User 1 balance should be updated after successful booking.");


        assertThrows(IllegalArgumentException.class, () -> {
            hotelService.bookRoom(2, 1, sdf.parse("07/07/2026"), sdf.parse("09/07/2026"));
        }, "Should throw exception for Room not available for the specified period.");
        assertEquals(10000, hotelService.getUser(2).balance(), "User 2 balance should not change after failed booking.");


        hotelService.bookRoom(2, 3, sdf.parse("07/07/2026"), sdf.parse("08/07/2026"));
        assertEquals(7000, hotelService.getUser(2).balance(), "User 2 balance should be updated after successful booking.");

        assertThrows(IllegalArgumentException.class, () -> { hotelService.setRoom(1, RoomType.MASTER_SUITE, 10000);}," Room already exists");


        assertEquals(1000, hotelService.getRoom(1).pricePerNight(), "Room 1 price should  should not change after failed.");
        assertEquals(RoomType.STANDARD_SUITE, hotelService.getRoom(1).roomType(), "Room 1 type should should  should not change after failed.");


        System.setOut(originalOut);

        System.out.println("====== VERIFYING FINAL OUTPUT ======");


        outContent.reset();
        System.setOut(new PrintStream(outContent));
        hotelService.printAllUsers();
        System.setOut(originalOut);

        String usersOutput = outContent.toString();
        System.out.println("\n--- Captured printAllUsers() Output ---");
        System.out.println(usersOutput);

        assertTrue(usersOutput.contains("User{id=2, balance=7000}"), "Final output for User 2 is incorrect.");
        assertTrue(usersOutput.contains("User{id=1, balance=4000}"), "Final output for User 1 is incorrect.");


        outContent.reset();
        System.setOut(new PrintStream(outContent));
        hotelService.printAll();
        System.setOut(originalOut);

        String allOutput = outContent.toString();
        System.out.println("\n--- Captured printAll() Output ---");
        System.out.println(allOutput);


        assertTrue(allOutput.contains("Room 3 - Type: MASTER_SUITE, Price/night: 3000"));
        assertTrue(allOutput.contains("Room 2 - Type: JUNIOR_SUITE, Price/night: 2000"));
        assertTrue(allOutput.contains("Room 1 - Type: STANDARD_SUITE, Price/night: 1000"));

    }
}
