package com.skypay.entities;

public class User {
    private final int id;
    private int balance;

    public User(int id, int balance) {
        this.id = id;
        this.balance = balance;
    }

    public int id() { return id; }
    public int balance() { return balance; }
    public void setBalance(int balance) { this.balance = balance; }

    @Override
    public String toString() {
        return String.format("User{id=%d, balance=%d}", id, balance);
    }
}
