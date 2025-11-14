package com.uade.tp.repositories;


import com.uade.tp.dtos.ConnectionDTO;
import com.uade.tp.dtos.KnapsackItemDTO;
import com.uade.tp.dtos.NeighborDTO;
import com.uade.tp.dtos.StationDTO;
import com.uade.tp.models.StationNode;
import org.springframework.data.neo4j.repository.Neo4jRepository;
import org.springframework.data.neo4j.repository.query.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Map;
import java.util.Optional;

@Repository
public interface StationRepository extends Neo4jRepository<StationNode, Long> {

    @Query("MATCH (n:Station) " +
            "WITH n, [l IN labels(n) WHERE l <> 'Station'][0] AS lineLabel " +
            "RETURN " +
            "  n.name AS name, " +
            "  lineLabel AS line, " +
            "  n.id AS id "
    )
    Optional<List<StationDTO>> findAllStationsAsDTO();


    @Query("MATCH (n:Station) " +
            "WHERE n.id = $id " +
            "WITH n, [l IN labels(n) WHERE l <> 'Station'][0] AS lineLabel " +
            "RETURN " +
            "  n.name AS name, " +
            "  lineLabel AS line, " +
            " n.id AS id"
    )
    Optional<StationDTO> findStationById(@Param("id") String id);

    @Query("MATCH (n:Station) " +
    "WITH n, [l IN labels(n) WHERE l <> 'Station'][0] AS lineLabel " +
    "WHERE lineLabel = $line " +
    " RETURN " +
    " n.name AS name, " +
    " lineLabel AS line, " +
    " n.id AS id"
    )
    Optional<List<StationDTO>> findStationByLine(@Param("line") String line);

    @Query("MATCH (n)" +
    "RETURN DISTINCT labels(n)[1]"
    )
    Optional<List<String>>  getAllLines();

    @Query("""
MATCH (a:Station)-[r:NEXT_STATION|TRANSFER]-(b:Station)
RETURN a.name AS from, b.name AS to
""")
    List<ConnectionDTO> findAllConnections();

    @Query("MATCH (n:Station)-[]-(m:Station) " +
            "WITH m, [l IN labels(m) WHERE l <> 'Station'][0] AS lineLabel " +
                    "WHERE n.id = $id " +
                    "RETURN DISTINCT " +
                    " m.name AS name, " +
                    " lineLabel AS line, " +
                    " m.id AS id"
    )
    Optional<List<StationDTO>> getNeighbors(@Param("id") String id);

    @Query("""
MATCH (a:Station {id: $id})-[r:NEXT_STATION]-(b:Station)
WITH b, r, [l IN labels(b) WHERE l <> 'Station'][0] AS lineLabel
RETURN 
    b.id AS id,
    b.name AS name,
    lineLabel AS line,
    coalesce(r.time, 1) AS time
""")
    List<NeighborDTO> findNeighborsWithTime(@Param("id") String id);

    @Query("MATCH (s:Station)\n" +
            "RETURN \n" +
            "    elementId(s) AS neoId,\n" +
            "    s.name AS name,\n" +
            "    s.interest AS interest,\n" +
            "    s.time AS time")
    List<KnapsackItemDTO> findKnapsackItems();


    @Query("""
MATCH (s:Station {name: $name})
WITH DISTINCT s, [l IN labels(s) WHERE l <> 'Station'][0] AS lineLabel
RETURN s.name AS name, lineLabel AS line, s.id AS id
""")
    List<StationDTO> findStationByName(String name);


    @Query("""
MATCH (n:Station)
WHERE n.id = $id
WITH DISTINCT n, [l IN labels(n) WHERE l <> 'Station'][0] AS lineLabel
RETURN 
  n.name AS name,
  lineLabel AS line,
  n.id AS id
""")
    List<StationDTO> findStationByIdd(@Param("id") String id);


}