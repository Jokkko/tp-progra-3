package com.uade.tp.repositories;


import com.uade.tp.dtos.StationDTO;
import com.uade.tp.models.StationNode;
import org.springframework.data.neo4j.repository.Neo4jRepository;
import org.springframework.data.neo4j.repository.query.Query;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface StationRepository extends Neo4jRepository<StationNode, Long> {

    @Query("MATCH (n:Station) " +
            "WITH n, [l IN labels(n) WHERE l <> 'Station'][0] AS lineLabel " +
            "RETURN " +
            "  n.name AS name, " +
            "  lineLabel AS line " )
    List<StationDTO> findAllStationsAsDTO();

}