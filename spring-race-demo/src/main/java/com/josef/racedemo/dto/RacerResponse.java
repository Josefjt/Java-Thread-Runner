package com.josef.racedemo.dto;

public class RacerResponse {

    private final int number;
    private final int position;

    public RacerResponse(int number, int position) {
        this.number = number;
        this.position = position;
    }

    public int getNumber() {
        return number;
    }

    public int getPosition() {
        return position;
    }
}
