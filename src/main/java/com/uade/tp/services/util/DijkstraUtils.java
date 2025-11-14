package com.uade.tp.services.util;

import com.uade.tp.dtos.NeighborDTO;

import java.util.*;
import java.util.function.Function;

public final class DijkstraUtils {

    private DijkstraUtils() {
    }

    /**
     * Dijkstra: devuelve distancias mínimas desde sourceId a todos los nodos alcanzables.
     */
    public static Map<String, Integer> dijkstraTimesFrom(
            String sourceId,
            Function<String, List<NeighborDTO>> neighborProvider
    ) {
        Map<String, Integer> dist = new HashMap<>();
        dist.put(sourceId, 0);

        PriorityQueue<String> pq =
                new PriorityQueue<>(Comparator.comparingInt(dist::get));
        pq.add(sourceId);

        Set<String> visited = new HashSet<>();

        while (!pq.isEmpty()) {
            String current = pq.poll();
            if (visited.contains(current)) continue;
            visited.add(current);

            List<NeighborDTO> neighbors = neighborProvider.apply(current);
            if (neighbors == null) continue;

            for (NeighborDTO n : neighbors) {
                String neighborId = n.id();
                int time = n.time();

                int newDist = dist.get(current) + time;

                if (newDist < dist.getOrDefault(neighborId, Integer.MAX_VALUE)) {
                    dist.put(neighborId, newDist);
                    pq.add(neighborId);
                }
            }
        }

        return dist;
    }
}
