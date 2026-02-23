package com.josef.racedemo.model;

public class Racer {

    private final int number;
    private int position; // initalzing counter

    public Racer(int number) {
        this.number = number;
        this.position = 0;
    }

    public int getNumber() {
        return number;
    }

    public int getPosition() {
        return position;
    }

    public void advance(int amount) {
        this.position += amount; // incarmenting counter
    }

    public void reset() {
        this.position = 0;
    }
}
