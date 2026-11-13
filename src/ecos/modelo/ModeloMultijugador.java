package ecos.modelo;

// Utilidades de Java, ArrayList y List para las listas de eventos y Random para mezclar las cartas.
import java.util.ArrayList;
import java.util.List;
import java.util.Random;

import ecos.app.modos.JuegoC.MezclaCartasClasico;

// Reglas del multijugador en red, UN solo tablero compartido y turnos alternados (primero el anfitrión, luego el
// invitado). Solo la computadora del anfitrión usa este modelo, él decide qué pasa y avisa al invitado con "eventos".
// Cada evento es una línea de texto, las dos pantallas los aplican igual:
//   V i            la carta i se voltea
//   P a b j p0 p1  las cartas a y b son pareja del jugador j (marcador nuevo p0 y p1)
//   F a b          a y b no son pareja, se vuelven a tapar
//   T j            ahora es el turno del jugador j
//   FIN            ya no quedan cartas
// Jugador 0 = anfitrión, jugador 1 = invitado. No sabe nada de pantallas ni de sockets.
// A cargo de Hellen.
public class ModeloMultijugador {

    // Lado del tablero (4 o 6), las caras de cada posición y cuáles ya son pareja.
    private final int n;
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
        // Se guarda una copia para que nadie de afuera cambie las caras.
        this.n = n;
        this.caras = caras.clone();
        this.hecha = new boolean[caras.length];
    }

    public int getN() { return n; }
    public int[] getCaras() { return caras.clone(); }
    public int getTurno() { return turno; }
    public int getPuntos(int jugador) { return puntos[jugador]; }

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
        // Primera carta de la jugada.
        if (primera < 0) {
            primera = i;
            return ev;
        }
        // Segunda carta, se compara con la primera.
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
            // Si acertó sigue jugando él.
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
