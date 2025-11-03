package com.uade.tp.controllers;

import com.uade.tp.dtos.StationDTO;
import com.uade.tp.services.StationService;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.List;
import java.util.Optional;

@RestController
@RequestMapping("/api/stations")
public class StationController {

    private final StationService stationService;

    public StationController(StationService stationService) {
        this.stationService = stationService;
    }

    @GetMapping("/all")
    public ResponseEntity<List<StationDTO>> getAllStations() {
        return stationService.getAllStations()
                .map(ResponseEntity::ok)
                .orElse(ResponseEntity.internalServerError().build());
    }

    @GetMapping("/{id}")
    public ResponseEntity<StationDTO> getById(@PathVariable("id") String id){
        return stationService.getById(id)
                .map(ResponseEntity::ok)
                .orElse(ResponseEntity.internalServerError().build());
    }

    @GetMapping("/line/{lineName}")
    public ResponseEntity<List<StationDTO>> getByLine(@PathVariable("lineName") String lineName){
        return stationService.getByLine(lineName)
                .map(ResponseEntity::ok)
                .orElse(ResponseEntity.notFound().build());
    }
}