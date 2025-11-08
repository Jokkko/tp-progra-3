package com.uade.tp.services;

import com.uade.tp.dtos.StationDTO;
import com.uade.tp.repositories.StationRepository;
import org.springframework.stereotype.Service;

import java.util.*;

@Service
public class StationService {

    private final StationRepository stationRepository;

    public StationService(StationRepository stationRepository) {
        this.stationRepository = stationRepository;
    }

    public Optional<List<StationDTO>> getAllStations() {
        return stationRepository.findAllStationsAsDTO();
    }

    public Optional<StationDTO> getById(String id) {
        return stationRepository.findStationById(id);
    }

    public Optional<List<StationDTO>> getByLine(String lineName){
        return stationRepository.findStationByLine(lineName);
    }

    public Optional<List<StationDTO>> minimumRouteBFS(String fromId, String toId){
        Queue<String> queue = new LinkedList<>();
        Set<String> visited = new HashSet<>();
        Map<String, String> parentMap = new HashMap<>();
        Map<String, StationDTO> stationCache = new HashMap<>();

        Optional<StationDTO> startStationOpt = stationRepository.findStationById(fromId);
        if (startStationOpt.isEmpty()) {
            return Optional.empty();
        }
        StationDTO startStation = startStationOpt.get();
        stationCache.put(fromId, startStation);

        queue.offer(fromId);
        visited.add(fromId);
        parentMap.put(fromId, null);

        boolean found = false;

        while (!queue.isEmpty()) {
            String currentId = queue.poll();

            if (currentId.equals(toId)) {
                found = true;
                break;
            }

            Optional<List<StationDTO>> neighborsOpt = stationRepository.getNeighbors(currentId);

            if (neighborsOpt.isPresent()) {
                for (StationDTO neighbor : neighborsOpt.get()) {
                    String neighborId = neighbor.getId();
                    if (!visited.contains(neighborId)) {
                        visited.add(neighborId);
                        parentMap.put(neighborId, currentId);
                        queue.offer(neighborId);
                        stationCache.put(neighborId, neighbor);
                    }
                }
            }
        }

        if (!found) {
            return Optional.empty();
        }

        LinkedList<StationDTO> path = new LinkedList<>();
        String currentId = toId;

        while (currentId != null) {
            path.addFirst(stationCache.get(currentId));
            currentId = parentMap.get(currentId);
        }

        return Optional.of(path);
    }

}