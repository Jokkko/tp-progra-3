package com.uade.tp.services;

import com.uade.tp.dtos.NeighborDTO;
import com.uade.tp.dtos.StationDTO;
import com.uade.tp.repositories.StationRepository;
import com.uade.tp.services.util.DijkstraUtils;
import com.uade.tp.services.util.GraphAlgorithms;
import com.uade.tp.services.util.TspBranchAndBound;
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

    // ---------- BFS (mínima cantidad de paradas) ----------

    public Optional<List<StationDTO>> minimumRouteBFS(String fromId, String toId){
        return GraphAlgorithms.bfsShortestHops(
                fromId,
                toId,
                stationRepository::getNeighbors,        // String -> Optional<List<StationDTO>>
                stationRepository::findStationById      // String -> Optional<StationDTO>
        );
    }



    // ---------- MergeSort de estaciones por nombre ----------

    public Optional<List<StationDTO>> getAllStationsSortedByName() {
        Optional<List<StationDTO>> stationsOpt = stationRepository.findAllStationsAsDTO();

        if (stationsOpt.isEmpty()) {
            return Optional.empty();
        }
        List<StationDTO> stations = new ArrayList<>(stationsOpt.get());

        mergeSortStations(stations, 0, stations.size() - 1);

        return Optional.of(stations);
    }

    private void mergeSortStations(List<StationDTO> stations, int left, int right) {
        if (left >= right) {
            return;
        }

        int mid = left + (right - left) / 2;
        mergeSortStations(stations, left, mid);
        mergeSortStations(stations, mid + 1, right);
        merge(stations, left, mid, right);
    }

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

        int i = 0;
        int j = 0;
        int k = left;

        while (i < n1 && j < n2) {
            String nameLeft = leftList.get(i).name();
            String nameRight = rightList.get(j).name();

            if (nameLeft.compareToIgnoreCase(nameRight) <= 0) {
                stations.set(k, leftList.get(i));
                i++;
            } else {
                stations.set(k, rightList.get(j));
                j++;
            }
            k++;
        }

        while (i < n1) {
            stations.set(k, leftList.get(i));
            i++;
            k++;
        }

        while (j < n2) {
            stations.set(k, rightList.get(j));
            j++;
            k++;
        }
    }

    // ---------- TSP Branch & Bound sobre un subconjunto de estaciones ----------

    public Optional<List<StationDTO>> tspBranchAndBound(List<String> stationIds) {
        System.out.println("TSP stationIds = " + stationIds);

        if (stationIds == null || stationIds.size() < 2) {
            System.out.println("Menos de 2 ids, no se puede TSP");
            return Optional.empty();
        }

        int n = stationIds.size();
        int[][] cost = new int[n][n];

        for (int i = 0; i < n; i++) {
            String fromId = stationIds.get(i);
            System.out.println("Corriendo Dijkstra desde: " + fromId);

            Map<String, Integer> distFromI = DijkstraUtils.dijkstraTimesFrom(
                    fromId,
                    stationRepository::findNeighborsWithTime
            );
            System.out.println("Distancias desde " + fromId + ": " + distFromI);

            for (int j = 0; j < n; j++) {
                if (i == j) {
                    cost[i][j] = 0;
                } else {
                    String toId = stationIds.get(j);
                    Integer d = distFromI.get(toId);
                    if (d == null) {
                        System.out.println("NO HAY CAMINO de " + fromId + " a " + toId);
                        return Optional.empty();
                    }
                    cost[i][j] = d;
                }
            }
        }

        System.out.println("Matriz de costos construida ok");

        Optional<List<Integer>> idxPathOpt = TspBranchAndBound.solve(cost);
        if (idxPathOpt.isEmpty()) {
            System.out.println("El solver TSP no encontró ruta");
            return Optional.empty();
        }

        List<Integer> idxPath = idxPathOpt.get();
        System.out.println("Índices ruta TSP: " + idxPath);

        List<StationDTO> route = new ArrayList<>();
        for (int idx : idxPath) {
            String stationId = stationIds.get(idx);
            var optStation = stationRepository.findStationById(stationId);
            if (optStation.isEmpty()) {
                System.out.println("NO ENCONTRADA en Neo4j la estación con id = " + stationId);
            } else {
                route.add(optStation.get());
            }
        }

        System.out.println("Ruta final: " + route);

        return route.isEmpty() ? Optional.empty() : Optional.of(route);
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
    public List<StationDTO> minimumTimeDijkstra(String fromId, String toId) {

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
            return List.of(); // lista vacía → no hay camino
        }

        LinkedList<StationDTO> path = new LinkedList<>();
        String current = toId;

        while (current != null) {
            List<StationDTO> stations = stationRepository.findStationByIdd(current);

            if (!stations.isEmpty()) {
                path.addFirst(stations.get(0));
            }

            current = parent.get(current);
        }


        return path;
    }

    public String stationIdByName(String name) {
        List<StationDTO> results = stationRepository.findStationByName(name);

        if (results.isEmpty()) {
            throw new RuntimeException("Station not found: " + name);
        }

        return results.getFirst().id();
    }
}