package com.uade.tp.models;

import com.uade.tp.models.valueObjets.LineaId;

public class Tramo {

    private final Estacion origen;
    private final Estacion destino;
    private final LineaId linea;
    private final double tiempoEnMinutos;

    public Tramo(Estacion origen, Estacion destino, LineaId linea, double tiempoEnMinutos) {
        if (origen == null || destino == null) {
            throw new IllegalArgumentException("El origen y el destino no pueden ser nulos");
        }
        if (linea == null) {
            throw new IllegalArgumentException("La línea no puede ser nula");
        }
        if (tiempoEnMinutos <= 0) {
            throw new IllegalArgumentException("El tiempo debe ser mayor a 0");
        }
        this.origen = origen;
        this.destino = destino;
        this.linea = linea;
        this.tiempoEnMinutos = tiempoEnMinutos;
    }

    public Estacion getOrigen() {
        return origen;
    }

    public Estacion getDestino() {
        return destino;
    }

    public LineaId getLinea() {
        return linea;
    }

    public double getTiempoEnMinutos() {
        return tiempoEnMinutos;
    }

    @Override
    public String toString() {
        return getOrigen().getNombre() + " -> " + getDestino().getNombre()
                + " (" + linea.value() + ") " + getTiempoEnMinutos() + " min";
    }
}
