package com.uade.tp.services;

import com.uade.tp.dtos.NeighborDTO;
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

        Map<String, Integer> dist = new HashMap<>();
        Map<String, String> parent = new HashMap<>();
        dist.put(fromId, 0);

        PriorityQueue<String> pq =
                new PriorityQueue<>(Comparator.comparingInt(dist::get));
        pq.add(fromId);

        Set<String> visited = new HashSet<>();

        while (!pq.isEmpty()) {
            String current = pq.poll();
            if (visited.contains(current)) continue;
            visited.add(current);

            if (current.equals(toId)) break;

            List<NeighborDTO> neighbors = stationRepository.findNeighborsWithTime(current);

            for (NeighborDTO n : neighbors) {
                String neighborId = n.id();
                int time = n.time();

                int newDist = dist.get(current) + time;

                if (newDist < dist.getOrDefault(neighborId, Integer.MAX_VALUE)) {
                    dist.put(neighborId, newDist);
                    parent.put(neighborId, current);
                    pq.add(neighborId);
                }
            }
        }

        if (!dist.containsKey(toId)) {
            return Optional.empty();
        }

        LinkedList<StationDTO> path = new LinkedList<>();
        String current = toId;

        while (current != null) {
            stationRepository.findStationById(current).ifPresent(path::addFirst);
            current = parent.get(current);
        }

        return Optional.of(path);
    }

    public String stationIdByName(String name) {
        return stationRepository.findStationByName(name)
                .map(StationDTO::getId)
                .orElseThrow(() -> new RuntimeException("Station not found: " + name));
    }


    public Optional<List<StationDTO>> getAllStationsSortedByName() {
        Optional<List<StationDTO>> stationsOpt = stationRepository.findAllStationsAsDTO();

        if (stationsOpt.isEmpty()) {
            return Optional.empty();
        }
        List<StationDTO> stations = new ArrayList<>(stationsOpt.get());

        mergeSortStations(stations, 0, stations.size() - 1);

        return Optional.of(stations);
    }

    /**
     * Ordena la lista de estaciones por nombre usando MergeSort.
     * COMPLEJIDAD:
     *  - Tiempo:  O(n log n)
     *  - Espacio: O(n) por las listas auxiliares en el merge.
     */
    private void mergeSortStations(List<StationDTO> stations, int left, int right) {
        if (left >= right) {
            return; // caso base: 1 elemento
        }

        int mid = left + (right - left) / 2;

        // Divide
        mergeSortStations(stations, left, mid);
        mergeSortStations(stations, mid + 1, right);

        // Conquista + combinación
        merge(stations, left, mid, right);
    }

    /**
     * Combina dos sublistas ordenadas:
     *  - [left, mid]
     *  - [mid+1, right]
     * en una sola sublista ordenada por nombre.
     */
    private void merge(List<StationDTO> stations, int left, int mid, int right) {

        int n1 = mid - left + 1;
        int n2 = right - mid;

        List<StationDTO> leftList = new ArrayList<>(n1);
        List<StationDTO> rightList = new ArrayList<>(n2);

        for (int i = 0; i < n1; i++) {
            leftList.add(stations.get(left + i));
        }
        for (int j = 0; j < n2; j++) {
            rightList.add(stations.get(mid + 1 + j));
        }

        int i = 0;      // índice en leftList
        int j = 0;      // índice en rightList
        int k = left;   // índice en lista original

        // Merge clásico: elegimos el menor entre los dos punteros
        while (i < n1 && j < n2) {

            String nameLeft = leftList.get(i).getName();
            String nameRight = rightList.get(j).getName();

            // Orden alfabético (ascendente)
            if (nameLeft.compareToIgnoreCase(nameRight) <= 0) {
                stations.set(k, leftList.get(i));
                i++;
            } else {
                stations.set(k, rightList.get(j));
                j++;
            }
            k++;
        }

        // Copiamos cualquier resto de la izquierda
        while (i < n1) {
            stations.set(k, leftList.get(i));
            i++;
            k++;
        }

        // Copiamos cualquier resto de la derecha
        while (j < n2) {
            stations.set(k, rightList.get(j));
            j++;
            k++;
        }
    }


}