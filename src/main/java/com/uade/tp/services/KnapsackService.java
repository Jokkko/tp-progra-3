package com.uade.tp.services;

import com.uade.tp.dtos.KnapsackItemDTO;
import com.uade.tp.repositories.StationRepository;
import org.springframework.stereotype.Service;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.Map;

@Service
public class KnapsackService {

    private final StationRepository stationRepository;

    public KnapsackService(StationRepository stationRepository) {
        this.stationRepository = stationRepository;
    }

    public List<KnapsackItemDTO> maximizeInterest(int timeLimit) {

        // Neo4j ya devuelve directamente la lista de DTO
        List<KnapsackItemDTO> items = stationRepository.findKnapsackItems();
        int n = items.size();

        // Arrays auxiliares para DP
        int[] weight = new int[n];
        int[] value  = new int[n];

        for (int i = 0; i < n; i++) {
            weight[i] = items.get(i).time();
            value[i]  = items.get(i).interest();
        }

        // DP table
        int[][] dp = new int[n + 1][timeLimit + 1];

        for (int i = 1; i <= n; i++) {
            int w = weight[i - 1];
            int v = value[i - 1];

            for (int t = 0; t <= timeLimit; t++) {
                dp[i][t] = dp[i - 1][t]; // no tomar

                if (w <= t) {
                    dp[i][t] = Math.max(
                            dp[i][t],
                            dp[i - 1][t - w] + v
                    );
                }
            }
        }

        // Reconstrucción de solución óptima
        List<KnapsackItemDTO> chosen = new ArrayList<>();
        int t = timeLimit;

        for (int i = n; i > 0; i--) {
            if (dp[i][t] != dp[i - 1][t]) {
                KnapsackItemDTO it = items.get(i - 1);
                chosen.add(it);
                t -= weight[i - 1];
            }
        }

        Collections.reverse(chosen);
        return chosen;
    }
}
