package com.uade.tp.repositories;


import com.uade.tp.dtos.StationDTO;
import com.uade.tp.models.StationNode;
import org.springframework.data.neo4j.repository.Neo4jRepository;
import org.springframework.data.neo4j.repository.query.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.util.List;
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
}