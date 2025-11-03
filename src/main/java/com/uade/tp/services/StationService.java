package com.uade.tp.services;

import com.uade.tp.dtos.StationDTO;
import com.uade.tp.repositories.StationRepository;
import org.springframework.stereotype.Service;
import java.util.List;
import java.util.Optional;

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

}