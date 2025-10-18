package com.uade.tp.models.valueObjets;

public record LineaId(String value) {
    public LineaId {
        if (value == null || value.isBlank()) {
            throw new IllegalArgumentException("El id de la línea no puede ser nulo o vacío");
        }
    }
}
