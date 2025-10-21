package com.uade.tp.services;

import com.uade.tp.dtos.StationDTO;
import com.uade.tp.repositories.StationRepository;
import org.springframework.stereotype.Service;
import java.util.List;

@Service
public class StationService {

    private final StationRepository stationRepository;

    public StationService(StationRepository stationRepository) {
        this.stationRepository = stationRepository;
    }

    public List<StationDTO> getAllStations() {
        return stationRepository.findAllStationsAsDTO();
    }

}