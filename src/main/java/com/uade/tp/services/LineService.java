package com.uade.tp.services;

import com.uade.tp.repositories.StationRepository;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.Optional;

@Service
public class LineService {
    private final StationRepository stationRepository;

    public LineService(StationRepository stationRepository) {
        this.stationRepository = stationRepository;
    }

    public Optional<List<String>> GetAllLines(){
        return stationRepository.getAllLines();
    }

}
