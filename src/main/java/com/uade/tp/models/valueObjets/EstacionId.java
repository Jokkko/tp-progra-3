package com.uade.tp.models.valueObjets;

public record EstacionId(String value) {

    public EstacionId {
        if (value == null || value.isBlank()) {
            throw new IllegalArgumentException("El id de estación no puede ser nulo o vacío");
        }
    }

}
