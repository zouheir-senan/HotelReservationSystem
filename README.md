# Hotel Booking System
A simple Java-based hotel room booking system that manages rooms, users, and bookings.
# Design Questions (Bonus)

## Question 1: Is putting all functions in one service recommended?

**Answer: NO**

### Problems with current single service approach:
- **Too many responsibilities**: One class handles rooms, users, and bookings
- **Hard to maintain**: Changes in one area can break other areas
- **Difficult to test**: Must test everything together
- **Not reusable**: Can't use room logic without booking logic

### Better approach:
```java
// Separate services for each responsibility
RoomService    - handles room operations
UserService    - handles user operations  
BookingService - handles booking operations
```

### Benefits:
- Easier to understand and maintain
- Can test each service separately
- Can reuse services independently
- Follows Single Responsibility Principle

---

## Question 2: setRoom() doesn't impact previous bookings - What's another way?

**Current approach**: Store snapshots of room data in each booking

### Alternative approache:

**Problem**: If room price changes, old bookings show new price (WRONG!)

#### Option 2: Room versioning with references (RECOMMENDED)
```java
// Each room change creates new version
record RoomVersion(int roomNumber, int version, RoomType type, int price, Date validFrom)

// Booking references specific room version
record Booking(int userId, int roomId, RoomVersion roomVersion, Date checkin, Date checkout)
```

### My recommendation: **Room Versioning**

**Why better than snapshots?**
- **Audit trail**: Can see all room changes over time
- **Business flexibility**: Easy to change prices for future bookings

**Example:**
- Room 101 v1: $100 (Dec 2024)
- 5 bookings reference Room 101 v1
- Create Room 101 v2: $120 (Jan 2025)
- Old bookings still point to v1 ($100)
- New bookings use v2 ($120)
**Result**: Previous bookings unaffected + business can update prices