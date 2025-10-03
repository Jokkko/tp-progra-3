package com.uade.tp.services;

import com.uade.tp.models.Persona;
import com.uade.tp.repositories.PersonaRepository;
import org.springframework.stereotype.Service;
import java.util.List;

@Service
public class PersonaService {

    private final PersonaRepository repo;

    public PersonaService(PersonaRepository repo) {
        this.repo = repo;
    }

    public List<Persona> findAll() {
        return repo.findAll();
    }

    public Persona save(Persona p) {
        return repo.save(p);
    }
}