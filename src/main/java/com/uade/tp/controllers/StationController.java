package com.uade.tp.controllers;

import com.uade.tp.dtos.StationDTO;
import com.uade.tp.services.StationService;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.List;

@RestController
@RequestMapping("/api/stations")
public class StationController {

    private final StationService stationService;

    public StationController(StationService stationService) {
        this.stationService = stationService;
    }

    @GetMapping("/all")
    public List<StationDTO> getAllStations() {
        return stationService.getAllStations();
    }
}