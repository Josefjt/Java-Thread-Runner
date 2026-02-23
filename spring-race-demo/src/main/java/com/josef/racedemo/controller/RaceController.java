package com.josef.racedemo.controller;

import com.josef.racedemo.dto.RaceResponse;
import com.josef.racedemo.service.RaceService;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/api/races")
public class RaceController {

    private final RaceService raceService;

    public RaceController(RaceService raceService) {
        this.raceService = raceService;
    }

    @PostMapping
    public RaceResponse createRace(@RequestParam(defaultValue = "5") int racers) {
        return raceService.createRace(racers);
    }

    @GetMapping("/{id}")
    public RaceResponse getRace(@PathVariable String id) {
        return raceService.getRace(id);
    }

    @PostMapping("/{id}/start")
    public RaceResponse startRace(@PathVariable String id) {
        return raceService.startRace(id);
    }

    @PostMapping("/{id}/restart")
    public RaceResponse restartRace(@PathVariable String id) {
        return raceService.restartRace(id);
    }
}
