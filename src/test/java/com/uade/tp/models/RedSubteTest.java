package com.uade.tp.models;

import com.uade.tp.models.valueObjets.EstacionId;
import com.uade.tp.models.valueObjets.LineaId;
import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.*;

class RedSubteTest {

    @Test
    void deberiaAgregarEstacionesCorrectamente() {
        RedSubte red = new RedSubte();
        Estacion estacion = new Estacion(new EstacionId("A01"), "Plaza de Mayo", true);

        red.agregarEstacion(estacion);

        assertEquals(1, red.getTodasLasEstaciones().size());
        assertEquals(estacion, red.getEstacionPorId(new EstacionId("A01")));
    }

    @Test
    void deberiaFallarSiSeAgregaEstacionNull() {
        RedSubte red = new RedSubte();

        assertThrows(IllegalArgumentException.class, () ->
                red.agregarEstacion(null)
        );
    }

    @Test
    void deberiaConectarTramosEnAmbosSentidos() {
        RedSubte red = new RedSubte();

        Estacion origen = new Estacion(new EstacionId("A01"), "Plaza de Mayo", true);
        Estacion destino = new Estacion(new EstacionId("A02"), "Lima", true);
        LineaId linea = new LineaId("A");
        Tramo tramo = new Tramo(origen, destino, linea, 3.0);

        red.agregarEstacion(origen);
        red.agregarEstacion(destino);
        red.conectar(tramo);

        // Verificamos conexión A -> B
        assertEquals(1, origen.getTramos().size());
        assertEquals(destino, origen.getTramos().getFirst().getDestino());

        // Verificamos conexión B -> A
        assertEquals(1, destino.getTramos().size());
        assertEquals(origen, destino.getTramos().getFirst().getDestino());
    }

    @Test
    void deberiaFallarSiSeConectaTramoNull() {
        RedSubte red = new RedSubte();
        assertThrows(IllegalArgumentException.class, () ->
                red.conectar(null)
        );
    }

    @Test
    void toStringDeberiaMostrarCantidadDeEstaciones() {
        RedSubte red = new RedSubte();
        red.agregarEstacion(new Estacion(new EstacionId("A01"), "Plaza de Mayo", true));
        red.agregarEstacion(new Estacion(new EstacionId("A02"), "Lima", true));

        String resultado = red.toString();
        assertTrue(resultado.contains("2 estaciones"));
    }
}
