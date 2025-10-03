package com.uade.tp.repositories;

import com.uade.tp.models.Persona;
import org.springframework.data.neo4j.repository.Neo4jRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface PersonaRepository extends Neo4jRepository<Persona, String> {
}