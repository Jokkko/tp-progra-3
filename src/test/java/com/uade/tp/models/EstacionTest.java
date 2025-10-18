package com.uade.tp.models;

import com.uade.tp.models.valueObjets.EstacionId;
import com.uade.tp.models.valueObjets.LineaId;
import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.*;

class EstacionTest {

    @Test
    void deberiaCrearEstacionValida() {
        Estacion estacion = new Estacion(
                new EstacionId("A01"),
                "Plaza de Mayo",
                true
        );

        assertEquals("Plaza de Mayo", estacion.getNombre());
        assertTrue(estacion.isAccesible());
        assertEquals("A01", estacion.getEstacionId().value());
        assertTrue(estacion.getTramos().isEmpty());
    }

    @Test
    void deberiaFallarSiIdEsNull() {
        assertThrows(IllegalArgumentException.class, () ->
                new Estacion(null, "Once", true)
        );
    }

    @Test
    void deberiaFallarSiNombreEsNull() {
        assertThrows(IllegalArgumentException.class, () ->
                new Estacion(new EstacionId("B01"), null, false)
        );
    }

    @Test
    void deberiaFallarSiNombreEsVacio() {
        assertThrows(IllegalArgumentException.class, () ->
                new Estacion(new EstacionId("B02"), "   ", false)
        );
    }

    @Test
    void tramosDeberiaEstarVacioAlCrear() {
        Estacion estacion = new Estacion(
                new EstacionId("A01"),
                "Plaza de Mayo",
                true
        );
        assertNotNull(estacion.getTramos());
        assertTrue(estacion.getTramos().isEmpty());
    }

    @Test
    void deberiaAgregarTramoCorrectamente() {
        Estacion origen = new Estacion(new EstacionId("A01"), "Plaza de Mayo", true);
        Estacion destino = new Estacion(new EstacionId("A02"), "Lima", true);

        Tramo tramo = new Tramo(origen, destino, new LineaId("A"), 3.5);
        origen.agregarTramo(tramo);

        assertEquals(1, origen.getTramos().size());
        assertEquals(destino, origen.getTramos().getFirst().getDestino());
    }

    @Test
    void deberiaFallarSiSeAgregaTramoNull() {
        Estacion estacion = new Estacion(
                new EstacionId("A01"),
                "Plaza de Mayo",
                true
        );

        assertThrows(IllegalArgumentException.class, () ->
                estacion.agregarTramo(null)
        );
    }
}
