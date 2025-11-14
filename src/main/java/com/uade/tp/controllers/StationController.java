package com.uade.tp.controllers;

import com.uade.tp.dtos.StationDTO;
import com.uade.tp.services.StationService;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

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

    @GetMapping("/minimumRoute")
    public ResponseEntity<List<StationDTO>> getMinimumRoute(@RequestParam("from") String fromId, @RequestParam("to") String toId) {
        Optional<List<StationDTO>> result = stationService.minimumRouteBFS(fromId,toId);

        return result.map(ResponseEntity::ok).orElseGet(() -> ResponseEntity.notFound().build());

    }

    @GetMapping("/fastest/{from}/{to}")
    public ResponseEntity<?> getFastestRoute(
            @PathVariable String from,
            @PathVariable String to
    ) {
        String fromId = stationService.stationIdByName(from);
        String toId   = stationService.stationIdByName(to);

        List<StationDTO> result = stationService.minimumTimeDijkstra(fromId, toId);

        if (result.isEmpty()) {
            return ResponseEntity.notFound().build();
        }

        return ResponseEntity.ok(result);
    }

}