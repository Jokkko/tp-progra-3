package com.uade.tp.models;

import com.uade.tp.models.valueObjets.EstacionId;

import java.util.ArrayList;
import java.util.List;

public class Estacion {

    private final EstacionId estacionId;
    private final String nombre;
    private final boolean accesible;
    private final List<Tramo> tramos = new ArrayList<>();

    public Estacion(EstacionId estacionId, String nombre, boolean accesible) {
        if (estacionId == null) {
            throw new IllegalArgumentException("El id de la estación no puede ser nulo");
        }
        if (nombre == null || nombre.isBlank()) {
            throw new IllegalArgumentException("El nombre de la estación no puede estar vacío");
        }
        this.estacionId = estacionId;
        this.nombre = nombre;
        this.accesible = accesible;
    }

    public EstacionId getEstacionId() {
        return estacionId;
    }

    public String getNombre() {
        return nombre;
    }

    public boolean isAccesible() {
        return accesible;
    }

    public List<Tramo> getTramos() {
        return tramos;
    }

    public void agregarTramo(Tramo tramo) {
        if (tramo == null) {
            throw new IllegalArgumentException("El tramo no puede ser nulo");
        }
        tramos.add(tramo);
    }

    @Override
    public String toString() {
        return nombre + " (" + estacionId + ")";
    }
}
