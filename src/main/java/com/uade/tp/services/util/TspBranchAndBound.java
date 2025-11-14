package com.uade.tp.services.util;

import java.util.*;

public final class TspBranchAndBound {

    private TspBranchAndBound() {
    }

    public static class TspResult {
        public int bestCost = Integer.MAX_VALUE;
        public List<Integer> bestPath = new ArrayList<>(); // índices 0..n, incluye vuelta al inicio
    }

    /**
     * Resuelve TSP sobre una matriz de costos completa.
     * Se asume que el recorrido:
     *  - Empieza en el nodo 0
     *  - Visita todos los nodos exactamente una vez
     *  - Vuelve a 0 al final
     *
     * @param cost matriz de costos cost[i][j]
     * @return Optional con lista de índices (ej: [0,2,1,3,0])
     */
    public static Optional<List<Integer>> solve(int[][] cost) {
        int n = cost.length;
        if (n == 0) return Optional.empty();

        int[] minOutgoing = new int[n];
        for (int i = 0; i < n; i++) {
            int min = Integer.MAX_VALUE;
            for (int j = 0; j < n; j++) {
                if (i != j && cost[i][j] < min) {
                    min = cost[i][j];
                }
            }
            minOutgoing[i] = min;
        }

        boolean[] visited = new boolean[n];
        visited[0] = true;

        List<Integer> currentPath = new ArrayList<>();
        currentPath.add(0);

        TspResult result = new TspResult();

        branchAndBoundTsp(
                0,
                0,
                visited,
                currentPath,
                cost,
                minOutgoing,
                result
        );

        if (result.bestCost == Integer.MAX_VALUE || result.bestPath.isEmpty()) {
            return Optional.empty();
        }

        return Optional.of(result.bestPath);
    }

    private static void branchAndBoundTsp(
            int lastIndex,
            int currentCost,
            boolean[] visited,
            List<Integer> currentPath,
            int[][] cost,
            int[] minOutgoing,
            TspResult result
    ) {
        int n = cost.length;

        if (currentPath.size() == n) {
            int totalCost = currentCost + cost[lastIndex][0];  // vuelta al inicio
            if (totalCost < result.bestCost) {
                result.bestCost = totalCost;
                result.bestPath = new ArrayList<>(currentPath);
                result.bestPath.add(0);
            }
            return;
        }

        int bound = currentCost;
        bound += minOutgoing[lastIndex];
        for (int i = 0; i < n; i++) {
            if (!visited[i]) {
                bound += minOutgoing[i];
            }
        }

        if (bound >= result.bestCost) {
            return;
        }

        for (int next = 0; next < n; next++) {
            if (!visited[next]) {

                int newCost = currentCost + cost[lastIndex][next];
                if (newCost >= result.bestCost) {
                    continue;
                }

                visited[next] = true;
                currentPath.add(next);

                branchAndBoundTsp(
                        next,
                        newCost,
                        visited,
                        currentPath,
                        cost,
                        minOutgoing,
                        result
                );

                visited[next] = false;
                currentPath.removeLast();
            }
        }
    }
}
