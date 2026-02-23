package com.josef.racedemo.model;

import java.time.Instant;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

public class Race {

    private final String id;
    private final int finishLine;
    private final List<Racer> racers;
    private RaceStatus status;
    private Integer winnerNumber;
    private Instant updatedAt;

    public Race(String id, int racersCount, int finishLine) {
        this.id = id;
        this.finishLine = finishLine;
        this.racers = new ArrayList<>();
        for (int i = 1; i <= racersCount; i++) {
            this.racers.add(new Racer(i));
        }
        this.status = RaceStatus.READY;
        this.updatedAt = Instant.now();
    }

    public String getId() {
        return id;
    }

    public int getFinishLine() {
        return finishLine;
    }

    public List<Racer> getRacers() {
        return Collections.unmodifiableList(racers);
    }

    public RaceStatus getStatus() {
        return status;
    }

    public void setStatus(RaceStatus status) {
        this.status = status;
        this.updatedAt = Instant.now();
    }

    public Integer getWinnerNumber() {
        return winnerNumber;
    }

    public void setWinnerNumber(Integer winnerNumber) {
        this.winnerNumber = winnerNumber;
        this.updatedAt = Instant.now();
    }

    public Instant getUpdatedAt() {
        return updatedAt;
    }

    public void touch() {
        this.updatedAt = Instant.now();
    }

    public void reset() {
        for (Racer racer : racers) {
            racer.reset();
        }
        winnerNumber = null;
        status = RaceStatus.READY;
        updatedAt = Instant.now();
    }
}
