package com.uade.tp.services.util;

import com.uade.tp.dtos.StationDTO;

import java.util.*;
import java.util.function.BiFunction;
import java.util.function.Function;

public final class GraphAlgorithms {

    private GraphAlgorithms() {
    }

    /**
     * BFS: camino con menor cantidad de paradas (aristas) entre fromId y toId.
     */
    public static Optional<List<StationDTO>> bfsShortestHops(
            String fromId,
            String toId,
            Function<String, Optional<List<StationDTO>>> neighborsProvider,
            Function<String, Optional<StationDTO>> stationLookup
    ) {
        Queue<String> queue = new LinkedList<>();
        Set<String> visited = new HashSet<>();
        Map<String, String> parentMap = new HashMap<>();
        Map<String, StationDTO> stationCache = new HashMap<>();

        Optional<StationDTO> startStationOpt = stationLookup.apply(fromId);
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

            Optional<List<StationDTO>> neighborsOpt = neighborsProvider.apply(currentId);

            if (neighborsOpt.isPresent()) {
                for (StationDTO neighbor : neighborsOpt.get()) {
                    String neighborId = neighbor.id();
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
     * Greedy: siempre elige el vecino cuya distancia al destino sea menor.
     * NO garantiza óptimo, solo es una heurística.
     */
    public static Optional<List<StationDTO>> greedyRoute(
            String fromId,
            String toId,
            Function<String, Optional<List<StationDTO>>> neighborsProvider,
            Function<String, Optional<StationDTO>> stationLookup,
            BiFunction<StationDTO, StationDTO, Double> distanceFn
    ) {
        Optional<StationDTO> fromOpt = stationLookup.apply(fromId);
        Optional<StationDTO> toOpt   = stationLookup.apply(toId);

        if (fromOpt.isEmpty() || toOpt.isEmpty()) {
            return Optional.empty();
        }

        StationDTO start  = fromOpt.get();
        StationDTO target = toOpt.get();

        Set<String> visited = new HashSet<>();
        List<StationDTO> path = new ArrayList<>();

        StationDTO current   = start;
        String currentId     = current.id();

        visited.add(currentId);
        path.add(current);

        int safetyLimit = 1000;
        int steps = 0;

        while (!currentId.equals(toId) && steps < safetyLimit) {
            steps++;

            Optional<List<StationDTO>> neighborsOpt = neighborsProvider.apply(currentId);

            if (neighborsOpt.isEmpty()) {
                return Optional.empty();
            }

            List<StationDTO> neighbors = neighborsOpt.get();

            List<StationDTO> candidates = neighbors.stream()
                    .filter(n -> !visited.contains(n.id()))
                    .toList();

            if (candidates.isEmpty()) {
                return Optional.empty();
            }

            StationDTO bestNeighbor = null;
            double bestDistance = Double.MAX_VALUE;

            for (StationDTO neighbor : candidates) {
                double d = distanceFn.apply(neighbor, target);
                if (d < bestDistance) {
                    bestDistance = d;
                    bestNeighbor = neighbor;
                }
            }

            current = bestNeighbor;
            assert current != null;
            currentId = current.id();
            visited.add(currentId);
            path.add(current);
        }

        if (!currentId.equals(toId)) {
            return Optional.empty();
        }

        return Optional.of(path);
    }
}
