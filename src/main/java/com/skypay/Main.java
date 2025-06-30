package com.skypay;

import com.skypay.enums.RoomType;
import com.skypay.services.Service;

import java.util.Calendar;
import java.util.Date;

public class Main {
    public static void main(String[] args) {


        Service service = new Service();

        try {
            // Create 3 rooms
            service.setRoom(1, RoomType.STANDARD_SUITE, 1000);
            service.setRoom(2, RoomType.JUNIOR_SUITE, 2000);
            service.setRoom(3, RoomType.MASTER_SUITE, 3000);

            // Create 2 users
            service.setUser(1, 5000);
            service.setUser(2, 10000);

            // Create dates
            Calendar cal = Calendar.getInstance();

            // User 1 tries booking Room 2 from 30/06/2026 to 07/07/2026 (7 nights)
            cal.set(2026, Calendar.JUNE, 30);
            Date checkin1 = cal.getTime();
            cal.set(2026, Calendar.JULY, 7);
            Date checkout1 = cal.getTime();

            try {
                service.bookRoom(1, 2, checkin1, checkout1);
                System.out.println("✓ User 1 successfully booked Room 2 from 30/06/2026 to 07/07/2026");
            } catch (Exception e) {
                System.out.println("✗ User 1 booking Room 2 failed: " + e.getMessage());
            }

            // User 1 tries booking Room 2 from 07/07/2026 to 30/06/2026 (invalid dates)
            cal.set(2026, Calendar.JULY, 7);
            Date checkin2 = cal.getTime();
            cal.set(2026, Calendar.JUNE, 30);
            Date checkout2 = cal.getTime();

            try {
                service.bookRoom(1, 2, checkin2, checkout2);
                System.out.println("✓ User 1 successfully booked Room 2 from 07/07/2026 to 30/06/2026");
            } catch (Exception e) {
                System.out.println("✗ User 1 booking Room 2 (invalid dates) failed: " + e.getMessage());
            }

            // User 1 tries booking Room 1 from 07/07/2026 to 08/07/2026 (1 night)
            cal.set(2026, Calendar.JULY, 7);
            Date checkin3 = cal.getTime();
            cal.set(2026, Calendar.JULY, 8);
            Date checkout3 = cal.getTime();

            try {
                service.bookRoom(1, 1, checkin3, checkout3);
                System.out.println("✓ User 1 successfully booked Room 1 from 07/07/2026 to 08/07/2026");
            } catch (Exception e) {
                System.out.println("✗ User 1 booking Room 1 failed: " + e.getMessage());
            }

            // User 2 tries booking Room 1 from 07/07/2026 to 09/07/2026 (2 nights)
            cal.set(2026, Calendar.JULY, 7);
            Date checkin4 = cal.getTime();
            cal.set(2026, Calendar.JULY, 9);
            Date checkout4 = cal.getTime();

            try {
                service.bookRoom(2, 1, checkin4, checkout4);
                System.out.println("✓ User 2 successfully booked Room 1 from 07/07/2026 to 09/07/2026");
            } catch (Exception e) {
                System.out.println("✗ User 2 booking Room 1 failed: " + e.getMessage());
            }

            // User 2 tries booking Room 3 from 07/07/2026 to 08/07/2026 (1 night)
            cal.set(2026, Calendar.JULY, 7);
            Date checkin5 = cal.getTime();
            cal.set(2026, Calendar.JULY, 8);
            Date checkout5 = cal.getTime();

            try {
                service.bookRoom(2, 3, checkin5, checkout5);
                System.out.println("✓ User 2 successfully booked Room 3 from 07/07/2026 to 08/07/2026");
            } catch (Exception e) {
                System.out.println("✗ User 2 booking Room 3 failed: " + e.getMessage());
            }

            // setRoom(1, suite, 10000) - Try to update existing room (should fail)
            try {
                service.setRoom(1, RoomType.JUNIOR_SUITE, 10000);
                System.out.println("✓ Room 1 updated to Suite with price 10000");
            } catch (Exception e) {
                System.out.println("✗ setRoom(1, suite, 10000) failed: " + e.getMessage());
            }

            System.out.println("\n" + "=".repeat(50));
            System.out.println("FINAL RESULTS:");
            System.out.println("=".repeat(50));

            service.printAll();
            System.out.println();
            service.printAllUsers();

        } catch (Exception e) {
            System.out.println("Error: " + e.getMessage());
            e.printStackTrace();
        }
    }

}
