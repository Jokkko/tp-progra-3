package com.uade.tp.models;

import com.uade.tp.models.valueObjets.EstacionId;

import java.util.Collection;
import java.util.HashMap;
import java.util.Map;

public class RedSubte {

    private final Map<EstacionId, Estacion> estaciones = new HashMap<>();

    public void agregarEstacion(Estacion estacion) {
        if (estacion == null) {
            throw new IllegalArgumentException("La estación no puede ser nula");
        }
        estaciones.put(estacion.getEstacionId(), estacion);
    }

    public Estacion getEstacionPorId(EstacionId id) {
        return estaciones.get(id);
    }

    public Collection<Estacion> getTodasLasEstaciones() {
        return estaciones.values();
    }

    public void conectar(Tramo tramo) {
        if (tramo == null) {
            throw new IllegalArgumentException("El tramo no puede ser nulo");
        }

        tramo.getOrigen().agregarTramo(tramo);

        Tramo inverso = new Tramo(
                tramo.getDestino(),
                tramo.getOrigen(),
                tramo.getLinea(),
                tramo.getTiempoEnMinutos()
        );
        tramo.getDestino().agregarTramo(inverso);
    }

    @Override
    public String toString() {
        return "RedSubte con " + estaciones.size() + " estaciones";
    }
}