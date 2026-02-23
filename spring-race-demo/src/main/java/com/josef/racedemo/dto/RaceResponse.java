package com.josef.racedemo.dto;

import com.josef.racedemo.model.RaceStatus;

import java.time.Instant;
import java.util.List;

public class RaceResponse {

    private final String id;
    private final RaceStatus status;
    private final int finishLine;
    private final Integer winnerNumber;
    private final Instant updatedAt;
    private final List<RacerResponse> racers;

    public RaceResponse(String id,
                        RaceStatus status,
                        int finishLine,
                        Integer winnerNumber,
                        Instant updatedAt,
                        List<RacerResponse> racers) {
        this.id = id;
        this.status = status;
        this.finishLine = finishLine;
        this.winnerNumber = winnerNumber;
        this.updatedAt = updatedAt;
        this.racers = racers;
    }

    public String getId() {
        return id;
    }

    public RaceStatus getStatus() {
        return status;
    }

    public int getFinishLine() {
        return finishLine;
    }

    public Integer getWinnerNumber() {
        return winnerNumber;
    }

    public Instant getUpdatedAt() {
        return updatedAt;
    }

    public List<RacerResponse> getRacers() {
        return racers;
    }
}
