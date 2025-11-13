package com.uade.tp.services;

import com.uade.tp.dtos.ConnectionDTO;
import com.uade.tp.dtos.NeighborDTO;
import com.uade.tp.dtos.StationDTO;
import com.uade.tp.repositories.StationRepository;
import org.springframework.stereotype.Service;

import java.util.*;

@Service
public class PathService {

    private final StationRepository stationRepository;

    public PathService(StationRepository stationRepository) {
        this.stationRepository = stationRepository;
    }

    /**
     * Verifica si existe un camino entre dos estaciones utilizando DFS (Depth-First Search).
     *
     * Complejidad temporal total: O(V + E)
     * - V: cantidad de estaciones (nodos)
     * - E: cantidad de conexiones (aristas)
     *
     * Complejidad espacial total: O(V + E)
     * - Se almacenan las listas de adyacencia y los nodos visitados.
     */
    public boolean existsPathDFS(String startName, String endName) {
        // Obtiene todas las conexiones (aristas) desde la base de datos Neo4j.
        // Supone que cada ConnectionDTO representa una relación entre dos estaciones.
        List<ConnectionDTO> connections = stationRepository.findAllConnections();

        // Construye el grafo en memoria como una lista de adyacencia.
        // Cada clave representa una estación, y su lista asociada contiene las estaciones vecinas.
        Map<String, List<String>> graph = new HashMap<>();

        // Este bucle recorre todas las conexiones: O(E)
        // Cada operación de inserción en el HashMap es O(1) promedio.
        for (ConnectionDTO conn : connections) {
            String from = conn.from();
            String to = conn.to();

            // Añade aristas en ambas direcciones (grafo no dirigido).
            graph.computeIfAbsent(from, k -> new ArrayList<>()).add(to);
            graph.computeIfAbsent(to, k -> new ArrayList<>()).add(from);
        }

        // Si alguna de las estaciones no existe en el grafo, no hay camino.
        if (!graph.containsKey(startName) || !graph.containsKey(endName)) {
            return false;
        }

        // Conjunto para marcar los nodos ya visitados durante la búsqueda: O(V)
        Set<String> visited = new HashSet<>();

        // Inicia la búsqueda DFS desde startName hasta endName.
        // Complejidad de la búsqueda: O(V + E)
        return dfs(graph, startName, endName, visited);
    }

    /**
     * Implementa la búsqueda en profundidad (Depth-First Search).
     *
     * @param graph   Grafo representado como mapa de adyacencia
     * @param current Nodo actual
     * @param target  Nodo objetivo (destino)
     * @param visited Conjunto de nodos ya visitados
     *
     * Complejidad temporal: O(V + E)
     *  - Cada nodo y arista se visitan a lo sumo una vez.
     * Complejidad espacial: O(V)
     *  - Por el conjunto visited y la pila de recursión.
     */
    private boolean dfs(Map<String, List<String>> graph, String current, String target, Set<String> visited) {
        // Caso base: si llegamos al destino, hay un camino.
        if (current.equals(target)) return true;

        // Marca el nodo actual como visitado: O(1)
        visited.add(current);

        // Explora todos los vecinos del nodo actual.
        // En total, esta iteración recorre todas las aristas una sola vez: O(E)
        for (String neighbor : graph.getOrDefault(current, List.of())) {
            // Si el vecino no fue visitado, continúa la búsqueda recursiva.
            if (!visited.contains(neighbor) && dfs(graph, neighbor, target, visited)) {
                return true; // Camino encontrado
            }
        }

        // Si ningún camino lleva al destino, retorna falso.
        return false;
    }

    /**
     * (BACKTRACKING)
     * Encuentra TODAS las rutas simples entre dos estaciones (sin ciclos).
     * COMPLEJIDAD:   O(V! · V)
     * - El algoritmo debe explorar TODAS las rutas simples posibles.
     * - En un grafo denso, el número de rutas simples entre dos nodos es O(V!).
     * - Cada vez que se encuentra un camino, se copia la lista → O(V).
     * Por lo tanto:
     *      Tiempo total = cantidad de rutas  ×  costo de copiarlas
     *                    = O(V!) × O(V)
     *                    = O(V! · V)
     *
     * COMPLEJIDAD ESPACIAL:   O(V)  (sin contar las soluciones)
     * - visited: hasta V nodos
     * - currentPath: hasta V nodos
     * - stack recursiva: profundidad ≤ V
     *
     * SI CONTAMOS el almacenamiento de todas las rutas:
     *
     *      O(V! · V)
     *
     * porque se guardan O(V!) caminos de largo O(V).
     *
     * Este problema es EXPONENCIAL e inherentemente no optimizable,
     * ya que se pide explícitamente devolver TODAS las rutas.
     */
    public List<List<String>> findAllSimplePaths(String start, String end) {

        // 1. Construir grafo desde Neo4j (O(E))
        List<ConnectionDTO> connections = stationRepository.findAllConnections();
        Map<String, List<String>> graph = new HashMap<>();

        for (ConnectionDTO c : connections) {
            graph.computeIfAbsent(c.from(), k -> new ArrayList<>()).add(c.to());
            graph.computeIfAbsent(c.to(), k -> new ArrayList<>()).add(c.from());
        }

        // Lista donde se guardan TODAS las rutas encontradas → puede crecer a O(V! · V)
        List<List<String>> results = new ArrayList<>();

        // Estructuras auxiliares para el backtracking (O(V) espacio)
        Set<String> visited = new HashSet<>();
        List<String> currentPath = new ArrayList<>();

        // Llamada inicial
        backtrack(start, end, graph, visited, currentPath, results);

        return results;
    }


    /**
     * Backtracking puro para generar rutas simples.
     * COMPLEJIDAD DE ESTE MÉTODO: O(V! · V) explicada arriba.
     */
    private void backtrack(
            String current,
            String target,
            Map<String, List<String>> graph,
            Set<String> visited,
            List<String> currentPath,
            List<List<String>> results
    ) {
        // Agregar nodo actual al camino (O(1))
        visited.add(current);
        currentPath.add(current);

        // Caso base: si llegamos al destino → copiar camino O(V)
        if (current.equals(target)) {
            results.add(new ArrayList<>(currentPath)); // copia de tamaño ≤ V → O(V)
        } else {
            // Recorrer vecinos (en total V nodos, E aristas)
            for (String neighbor : graph.getOrDefault(current, List.of())) {
                // Evitar ciclos
                if (!visited.contains(neighbor)) {
                    backtrack(neighbor, target, graph, visited, currentPath, results);
                }
            }
        }

        // BACKTRACK: remover nodo actual antes de volver (O(1))
        visited.remove(current);
        currentPath.remove(currentPath.size() - 1);
    }
}