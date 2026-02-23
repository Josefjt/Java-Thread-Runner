package com.josef.racedemo.service;

import com.josef.racedemo.dto.RaceResponse;
import com.josef.racedemo.dto.RacerResponse;
import com.josef.racedemo.model.Race;
import com.josef.racedemo.model.RaceStatus;
import com.josef.racedemo.model.Racer;
import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Service;
import org.springframework.web.server.ResponseStatusException;

import javax.annotation.PreDestroy;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.UUID;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.Executors;
import java.util.concurrent.ScheduledExecutorService;
import java.util.concurrent.ScheduledFuture;
import java.util.concurrent.ThreadLocalRandom;
import java.util.concurrent.TimeUnit;

@Service
public class RaceService {

    private static final int DEFAULT_FINISH_LINE = 900;
    private static final int MIN_RACERS = 2;
    private static final int MAX_RACERS = 12;
    private static final long TICK_MILLIS = 80L;

    private final Map<String, Race> races = new ConcurrentHashMap<>();
    private final Map<String, ScheduledFuture<?>> tasks = new ConcurrentHashMap<>();
    private final ScheduledExecutorService scheduler = Executors.newScheduledThreadPool(4);

    public RaceResponse createRace(int racersCount) {
        if (racersCount < MIN_RACERS || racersCount > MAX_RACERS) {
            throw new ResponseStatusException(
                    HttpStatus.BAD_REQUEST,
                    "racers must be between " + MIN_RACERS + " and " + MAX_RACERS
            );
        }

        String id = UUID.randomUUID().toString().substring(0, 8);
        Race race = new Race(id, racersCount, DEFAULT_FINISH_LINE);
        races.put(id, race);
        cancelTask(id);
        return toResponse(race);
    }

    public RaceResponse getRace(String id) {
        return toResponse(findRace(id));
    }

    public RaceResponse restartRace(String id) {
        Race race = findRace(id);
        cancelTask(id);
        synchronized (race) {
            race.reset();
            return toResponse(race);
        }
    }

    public RaceResponse startRace(String id) {
        Race race = findRace(id);
        synchronized (race) {
            if (race.getStatus() == RaceStatus.RUNNING) {
                return toResponse(race);
            }
            if (race.getStatus() == RaceStatus.FINISHED) {
                throw new ResponseStatusException(HttpStatus.CONFLICT, "Race is finished. Restart it first.");
            }
            race.setStatus(RaceStatus.RUNNING);
        }

        ScheduledFuture<?> future = scheduler.scheduleAtFixedRate(
                () -> tickRace(id),
                0,
                TICK_MILLIS,
                TimeUnit.MILLISECONDS
        );
        ScheduledFuture<?> previous = tasks.put(id, future);
        if (previous != null) {
            previous.cancel(true);
        }

        return getRace(id);
    }

    private void tickRace(String id) {
        Race race = races.get(id);
        if (race == null) {
            cancelTask(id);
            return;
        }

        boolean finished = false;
        synchronized (race) {
            if (race.getStatus() != RaceStatus.RUNNING) {
                return;
            }

            for (Racer racer : race.getRacers()) {
                int step = ThreadLocalRandom.current().nextInt(1, 16); // creating a random number generator incliuding trype casting
                racer.advance(step);
                if (racer.getPosition() >= race.getFinishLine() && race.getWinnerNumber() == null) {
                    race.setWinnerNumber(racer.getNumber());
                    race.setStatus(RaceStatus.FINISHED);
                    finished = true;
                    break;
                }
            }

            if (!finished) {
                race.touch();
            }
        }

        if (finished) {
            cancelTask(id);
        }
    }

    private Race findRace(String id) {
        Race race = races.get(id);
        if (race == null) {
            throw new ResponseStatusException(HttpStatus.NOT_FOUND, "Race not found: " + id);
        }
        return race;
    }

    private RaceResponse toResponse(Race race) {
        synchronized (race) {
            List<RacerResponse> racers = new ArrayList<>();
            for (Racer racer : race.getRacers()) {
                racers.add(new RacerResponse(racer.getNumber(), racer.getPosition()));
            }

            return new RaceResponse(
                    race.getId(),
                    race.getStatus(),
                    race.getFinishLine(),
                    race.getWinnerNumber(),
                    race.getUpdatedAt(),
                    racers
            );
        }
    }

    private void cancelTask(String id) {
        ScheduledFuture<?> existing = tasks.remove(id);
        if (existing != null) {
            existing.cancel(true);
        }
    }

    @PreDestroy
    public void shutdown() {
        scheduler.shutdownNow();
    }
}
