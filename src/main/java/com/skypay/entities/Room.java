package com.skypay.entities;

import com.skypay.enums.RoomType;

public record Room (int roomNumber, RoomType roomType, int pricePerNight){
//    @Override
//    public String toString() {
//
//    }
}