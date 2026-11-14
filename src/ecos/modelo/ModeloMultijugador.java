package ecos.modelo;

import java.util.ArrayList;
import java.util.List;
import java.util.Random;

import ecos.app.modos.JuegoC.MezclaCartasClasico;

// Reglas del multijugador en red: un solo tablero compartido y turnos alternados, primero el anfitrión y luego el
// invitado. Solo la computadora del anfitrión usa este modelo; él decide qué pasa y avisa al invitado con eventos.
// Cada evento es una línea de texto que las dos pantallas aplican igual. V es voltear una carta, P es una pareja
// encontrada (con el marcador nuevo), F es un fallo y las cartas se tapan, T es el cambio de turno y FIN es el final.
// El jugador 0 es el anfitrión y el 1 es el invitado. No sabe nada de pantallas ni de sockets.
public class ModeloMultijugador {

    // Caras de cada posición y cuáles ya son pareja.
    private final int[] caras;
    private final boolean[] hecha;
    private final int[] puntos = new int[2];
    private int turno = 0;
    // Primera carta volteada de la jugada (-1 si todavía no hay).
    private int primera = -1;
    // True entre un fallo y el cambio de turno (nadie puede voltear).
    private boolean esperando = false;
    private int parejas = 0;

    // Tablero nuevo de n x n con las cartas mezcladas.
    public ModeloMultijugador(int n) {
        this(n, MezclaCartasClasico.aplanar(MezclaCartasClasico.crearMatriz(n, new Random())));
    }

    // Tablero con las caras que se le den (sirve para probar).
    public ModeloMultijugador(int n, int[] caras) {
        this.caras = caras.clone();
        this.hecha = new boolean[caras.length];
    }

    public int[] getCaras() { return caras.clone(); }
    public int getTurno() { return turno; }

    // Quién ganó, 0 o 1. -1 si empataron.
    public int ganador() {
        return puntos[0] == puntos[1] ? -1 : (puntos[0] > puntos[1] ? 0 : 1);
    }

    // Un jugador voltea la carta i. Devuelve los eventos que pasaron (vacía si la jugada no vale, por ejemplo si no era
    // su turno).
    public List<String> voltear(int jugador, int i) {
        List<String> ev = new ArrayList<>();
        if (esperando || jugador != turno || i < 0 || i >= caras.length || hecha[i] || i == primera || parejas == caras.length / 2) {
            return ev;
        }
        ev.add("V " + i);
        if (primera < 0) {
            primera = i;
            return ev;
        }
        int a = primera;
        primera = -1;
        if (caras[a] == caras[i]) {
            hecha[a] = true;
            hecha[i] = true;
            puntos[turno]++;
            parejas++;
            ev.add("P " + a + " " + i + " " + turno + " " + puntos[0] + " " + puntos[1]);
            if (parejas == caras.length / 2) {
                ev.add("FIN");
            }
        } else {
            esperando = true;
            ev.add("F " + a + " " + i);
        }
        return ev;
    }

    // Después de un fallo, pasa el turno al otro. Devuelve el evento "T j", o null si no había nada que continuar.
    public String continuar() {
        if (!esperando) {
            return null;
        }
        esperando = false;
        turno = 1 - turno;
        return "T " + turno;
    }
}
