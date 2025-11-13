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

    /**
     * Resuelve el problema de la mochila 0/1 usando Programación Dinámica.
     *
     * Cada estación se modela como un "item" con:
     *  - peso  = time  (tiempo asociado a la estación)
     *  - valor = interest (puntos de interés)
     *
     * Parámetros:
     *  - timeLimit: capacidad máxima de la mochila (tiempo total permitido).
     *
     * Notación:
     *   n = cantidad de estaciones (items)
     *   T = timeLimit
     *
     * COMPLEJIDAD:
     *   - Se construye una tabla dp de tamaño (n+1) × (T+1).
     *   - Dos bucles anidados:
     *       for i in [1..n]:
     *         for t in [0..T]:
     *           operaciones O(1)
     *   => O(n · T)
     *
     *   - Fase de reconstrucción del subconjunto óptimo:
     *       for i en [n..1]:
     *         operaciones O(1)
     *     => O(n)
     *
     *   - Costo total dominante: O(n · T)
     *
     *   - La matriz dp tiene tamaño (n+1) × (T+1):
     *       => O(n · T)
     *   - Estructuras auxiliares (listas, arrays weight y value) son O(n).
     *   - Espacio total: O(n · T).
     */
    public List<KnapsackItemDTO> maximizeInterest(int timeLimit) {

        // 1) Cargar items desde la base de datos.
        //    Este paso es O(n) respecto a la cantidad de estaciones retornadas.
        List<Map<String,Object>> rows = stationRepository.findKnapsackItems();
        List<KnapsackItemDTO> items = new ArrayList<>();

        for (Map<String,Object> r : rows) {
            items.add(new KnapsackItemDTO(
                    (String) r.get("id"),
                    (String) r.get("name"),
                    ((Number) r.get("interest")).intValue(),
                    ((Number) r.get("time")).intValue()
            ));
        }

        int n = items.size();

        // Arrays auxiliares para pesos (tiempo) y valores (interés).
        // Espacio: O(n)
        int[] weight = new int[n];
        int[] value  = new int[n];

        for (int i = 0; i < n; i++) {
            weight[i] = items.get(i).time();
            value[i]  = items.get(i).interest();
        }

        // 2) Tabla de Programación Dinámica.
        // dp[i][t] = máximo interés usando las primeras i estaciones
        //            con un tiempo máximo t.
        //
        // Tamaño de la matriz: (n+1) × (timeLimit+1) => O(n · T) espacio.
        int[][] dp = new int[n + 1][timeLimit + 1];

        // Construcción de la DP.
        // Bucle externo: recorre n items.
        // Bucle interno: recorre T+1 capacidades de tiempo.
        //
        // Complejidad temporal: O(n · T).
        for (int i = 1; i <= n; i++) {
            int w = weight[i - 1];  // peso del ítem i
            int v = value[i - 1];   // valor del ítem i

            for (int t = 0; t <= timeLimit; t++) {
                // Caso en el que NO tomamos el ítem i:
                dp[i][t] = dp[i - 1][t];

                // Caso en el que SÍ tomamos el ítem i (si entra en el tiempo t):
                if (w <= t) {
                    dp[i][t] = Math.max(
                            dp[i][t],                     // no tomarlo
                            dp[i - 1][t - w] + v          // tomarlo
                    );
                }
            }
        }

        // 3) Reconstrucción del subconjunto óptimo de estaciones.
        // Recorremos la matriz desde dp[n][timeLimit] hacia atrás,
        // verificando qué ítems fueron incluidos.
        // Complejidad de esta fase: O(n).
        List<KnapsackItemDTO> chosen = new ArrayList<>();
        int t = timeLimit;

        for (int i = n; i > 0; i--) {
            // Si dp[i][t] es distinto de dp[i-1][t], significa que el ítem i fue elegido.
            if (dp[i][t] != dp[i - 1][t]) {
                KnapsackItemDTO it = items.get(i - 1);
                chosen.add(it);
                t -= weight[i - 1]; // reducimos la capacidad restante
            }
        }

        // Invertimos la lista para mantener el orden original de selección.
        Collections.reverse(chosen);
        return chosen;
    }
}
