package com.uade.tp.controllers;

import com.uade.tp.models.Persona;
import com.uade.tp.services.PersonaService;
import org.springframework.web.bind.annotation.*;
import java.util.List;

@RestController
@RequestMapping("/personas")
public class PersonaController {

    private final PersonaService service;

    public PersonaController(PersonaService service) {
        this.service = service;
    }

    @GetMapping
    public List<Persona> getAll() {
        return service.findAll();
    }

    @PostMapping
    public Persona create(@RequestBody Persona p) {
        return service.save(p);
    }
}