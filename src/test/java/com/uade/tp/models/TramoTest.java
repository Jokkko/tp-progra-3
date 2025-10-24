package com.uade.tp.models;

import com.uade.tp.models.valueObjets.EstacionId;
import com.uade.tp.models.valueObjets.LineaId;
import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.*;

class TramoTest {

    @Test
    void deberiaCrearTramoValido() {
        Estacion origen = new Estacion(new EstacionId("A01"), "Plaza de Mayo", true);
        Estacion destino = new Estacion(new EstacionId("A02"), "Lima", true);
        LineaId linea = new LineaId("A");

        Tramo tramo = new Tramo(origen, destino, linea, 3.5);

        assertEquals(origen, tramo.getOrigen());
        assertEquals(destino, tramo.getDestino());
        assertEquals(linea, tramo.getLinea());
        assertEquals(3.5, tramo.getTiempoEnMinutos());
    }

    @Test
    void deberiaFallarSiOrigenEsNull() {
        Estacion destino = new Estacion(new EstacionId("A02"), "Lima", true);
        LineaId linea = new LineaId("A");

        assertThrows(IllegalArgumentException.class, () ->
                new Tramo(null, destino, linea, 3.5)
        );
    }

    @Test
    void deberiaFallarSiDestinoEsNull() {
        Estacion origen = new Estacion(new EstacionId("A01"), "Plaza de Mayo", true);
        LineaId linea = new LineaId("A");

        assertThrows(IllegalArgumentException.class, () ->
                new Tramo(origen, null, linea, 3.5)
        );
    }

    @Test
    void deberiaFallarSiLineaEsNull() {
        Estacion origen = new Estacion(new EstacionId("A01"), "Plaza de Mayo", true);
        Estacion destino = new Estacion(new EstacionId("A02"), "Lima", true);

        assertThrows(IllegalArgumentException.class, () ->
                new Tramo(origen, destino, null, 3.5)
        );
    }

    @Test
    void deberiaFallarSiTiempoEsNegativo() {
        Estacion origen = new Estacion(new EstacionId("A01"), "Plaza de Mayo", true);
        Estacion destino = new Estacion(new EstacionId("A02"), "Lima", true);
        LineaId linea = new LineaId("A");

        assertThrows(IllegalArgumentException.class, () ->
                new Tramo(origen, destino, linea, -1)
        );
    }

    @Test
    void deberiaFallarSiTiempoEsCero() {
        Estacion origen = new Estacion(new EstacionId("A01"), "Plaza de Mayo", true);
        Estacion destino = new Estacion(new EstacionId("A02"), "Lima", true);
        LineaId linea = new LineaId("A");

        assertThrows(IllegalArgumentException.class, () ->
                new Tramo(origen, destino, linea, 0)
        );
    }
}
