package ecos.modelo;

// ArrayList: lista donde se guardan los eventos de cada jugada.
import java.util.ArrayList;
// List: tipo de la lista de eventos.
import java.util.List;
// Random: sirve para mezclar las cartas al crear el tablero.
import java.util.Random;

// MezclaCartasClasico: fabrica la matriz de cartas (cada imagen dos veces, mezcladas) y la aplana a una lista.
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
    // Parejas de cada jugador: puntos[0] anfitrión, puntos[1] invitado.
    private final int[] puntos = new int[2];
    private int turno = 0;
    // Primera carta volteada de la jugada (-1 si todavía no hay).
    private int primera = -1;
    // True entre un fallo y el cambio de turno (nadie puede voltear).
    private boolean esperando = false;
    // Cuántas parejas se han encontrado en total.
    private int parejas = 0;

    // Tablero nuevo de n x n con las cartas mezcladas.
    public ModeloMultijugador(int n) {
        // this(...) llama al otro constructor pasándole las caras ya mezcladas y aplanadas a una lista.
        this(n, MezclaCartasClasico.aplanar(MezclaCartasClasico.crearMatriz(n, new Random())));
    }

    // Tablero con las caras que se le den (sirve para probar).
    public ModeloMultijugador(int n, int[] caras) {
        // clone(): copia, para que nadie de afuera cambie el tablero.
        this.caras = caras.clone();
        this.hecha = new boolean[caras.length];
    }

    // Devuelve una copia de las caras; el anfitrión se las manda al invitado.
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
        // Jugada inválida: se está esperando, no es su turno, índice fuera del tablero, carta ya hecha pareja,
        // misma carta de la primera, o el juego ya terminó.
        if (esperando || jugador != turno || i < 0 || i >= caras.length || hecha[i] || i == primera || parejas == caras.length / 2) {
            return ev;
        }
        // Evento V: se volteó la carta i.
        ev.add("V " + i);
        // Si es la primera carta de la jugada, solo se guarda y se espera la segunda.
        if (primera < 0) {
            primera = i;
            return ev;
        }
        // Es la segunda carta: a es la primera; se reinicia primera para la próxima jugada.
        int a = primera;
        primera = -1;
        if (caras[a] == caras[i]) {
            // Pareja: se marcan las dos, el jugador suma un punto y sigue con el turno.
            hecha[a] = true;
            hecha[i] = true;
            puntos[turno]++;
            parejas++;
            // Evento P: las dos cartas, quién la hizo y el marcador de los dos.
            ev.add("P " + a + " " + i + " " + turno + " " + puntos[0] + " " + puntos[1]);
            // Si ya están todas las parejas, el juego termina.
            if (parejas == caras.length / 2) {
                ev.add("FIN");
            }
        } else {
            // Fallo: nadie puede voltear hasta que se llame a continuar().
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
        // 1 - turno alterna entre 0 y 1.
        turno = 1 - turno;
        return "T " + turno;
    }
}