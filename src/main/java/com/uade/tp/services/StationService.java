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

    /**
     * Encuentra la ruta más rápida (menor tiempo total) entre dos estaciones usando
     * el algoritmo de Dijkstra.
     *
     * COMPLEJIDAD:
     *      V = cantidad de estaciones (nodos)
     *      E = cantidad de conexiones (aristas NEXT_STATION)
     *
     * - Extraer el mínimo de la PriorityQueue cuesta O(log V)
     * - Relajar cada arista cuesta O(1)
     * - Iteramos cada arista como mucho una vez → O(E)
     *
     * COMPLEJIDAD TOTAL:
     *   O( (V + E) · log V )
     */
    public Optional<List<StationDTO>> minimumTimeDijkstra(String fromId, String toId) {

        // Distancia mínima conocida a cada nodo (inicialmente infinito excepto origen)
        Map<String, Integer> dist = new HashMap<>();

        // Mapa para reconstruir el camino más corto
        Map<String, String> parent = new HashMap<>();

        // Inicializar distancia del nodo inicial
        dist.put(fromId, 0);

        // PriorityQueue ordenada por distancia acumulada
        // COMPLEJIDAD O(log V) por inserción/extracción
        PriorityQueue<String> pq = new PriorityQueue<>(Comparator.comparingInt(dist::get));
        pq.add(fromId);

        // Seguimiento de nodos ya procesados
        Set<String> visited = new HashSet<>();

        while (!pq.isEmpty()) {
            String current = pq.poll(); // O(log V)

            // Evitar reprocesar nodos
            if (visited.contains(current)) continue;
            visited.add(current);

            // Si llegamos al destino, terminamos
            if (current.equals(toId)) break;

            // Obtener vecinos con sus tiempos
            // Cada estación tiene un set pequeño de vecinos → O(1) amortizado
            List<Map<String, Object>> neighbors = stationRepository.findNeighborsWithTime(current);

            for (Map<String, Object> n : neighbors) {
                String neighborId = (String) n.get("id");
                int time = ((Number) n.get("time")).intValue();

                // RELAJACIÓN
                int newDist = dist.get(current) + time;

                // Si mejoramos distancia, actualizamos
                if (newDist < dist.getOrDefault(neighborId, Integer.MAX_VALUE)) {
                    dist.put(neighborId, newDist);     // O(1)
                    parent.put(neighborId, current);   // O(1)
                    pq.add(neighborId);                // O(log V)
                }
            }
        }

        // Si nunca alcanzamos destino → no existe camino
        if (!dist.containsKey(toId)) {
            return Optional.empty();
        }

        // Reconstrucción del camino desde el destino hacia atrás
        LinkedList<StationDTO> path = new LinkedList<>();
        String current = toId;

        while (current != null) {
            stationRepository.findStationById(current).ifPresent(path::addFirst);
            current = parent.get(current);
        }
        return Optional.of(path);
    }
}