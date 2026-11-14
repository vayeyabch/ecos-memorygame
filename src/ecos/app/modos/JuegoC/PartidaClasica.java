package ecos.app.modos.JuegoC;

import java.util.Random;

// Reglas del modo clásico. No usa pantallas, solo guarda el estado. Cada jugador tiene su propia matriz n x n, distinta
// de la del otro, y un intento es voltear dos cartas. Gana quien encuentra primero todas las parejas de su tablero.
public class PartidaClasica {

    public enum Jugada { IGNORADA, PRIMERA, SEGUNDA, ACIERTO, FALLO }

    // Tamaño de la matriz (6 o 4), caras de cada jugador, cartas ya encontradas e intentos.
    private final int n;
    private final int[][][] caras = new int[2][][];
    private final boolean[][] hecha = new boolean[2][];
    private final int[] intentos = new int[2];
    // Parejas encontradas, comparador de cartas, jugador con el turno (0 o 1) y primera carta volteada (-1 si no hay).
    private final int[] parejas = new int[2];
    private final ComparadorParejasClasico comparador = new ComparadorParejasClasico();
    private int turno = 0;
    private int primera = -1;
    // Las dos cartas del último intento y quién completó su tablero primero.
    private int cartaA = -1;
    private int cartaB = -1;
    private int terminoPrimero = -1;

    public PartidaClasica(int n) {
        this.n = n;
        for (int p = 0; p < 2; p++) {
            // Cada jugador tiene su propio azar.
            caras[p] = MezclaCartasClasico.crearMatriz(n, new Random());
            hecha[p] = new boolean[n * n];
        }
    }

    // El índice va de 0 a n*n-1: fila = índice / n, columna = índice % n.
    private int cara(int jugador, int indice) {
        return caras[jugador][indice / n][indice % n];
    }

    public Jugada seleccionar(int jugador, int indice) {
        // No cuenta si no es su turno, si ya encontró esa carta o si toca la misma dos veces.
        if (jugador != turno || hecha[jugador][indice] || indice == primera) {
            return Jugada.IGNORADA;
        }
        if (primera < 0) {
            primera = indice;
            return Jugada.PRIMERA;
        }
        cartaA = primera;
        cartaB = indice;
        primera = -1;
        intentos[jugador]++;
        return Jugada.SEGUNDA; 
    }

    // Se llama después de la segunda carta: compara y actualiza.
    public Jugada resolver() {
        boolean iguales = comparador.comparar(cara(turno, cartaA), cara(turno, cartaB));
        if (!iguales) {
            return Jugada.FALLO;
        }
        hecha[turno][cartaA] = true;
        hecha[turno][cartaB] = true;
        parejas[turno]++;
        if (completo(turno) && terminoPrimero < 0) {
            terminoPrimero = turno;
        }
        return Jugada.ACIERTO;
    }

    // Pasa el turno al otro jugador, salvo que el otro ya terminó. Dice si cambió.
    public boolean pasarTurno() {
        int otro = 1 - turno;
        if (completo(otro)) {
            return false;
        }
        turno = otro;
        primera = -1;
        return true;
    }

    // Se acabó el tiempo: devuelve la carta que quedó volteada, o -1 si no había.
    public int cancelarSeleccion() {
        int carta = primera;
        primera = -1;
        return carta;
    }

    public boolean hayPrimera() {
        return primera >= 0;
    }

    public boolean completo(int jugador) {
        return parejas[jugador] == n * n / 2;
    }

    public boolean terminado() {
        return completo(0) || completo(1);
    }

    // El ganador es quien completó primero su tablero, -1 si todavía nadie.
    public int ganador() {
        return terminoPrimero;
    }

    public int[][] getCaras(int jugador) {
        return caras[jugador];
    }

    public int getTurno() {
        return turno;
    }

    public int getIntentos(int jugador) {
        return intentos[jugador];
    }

    public int getParejas(int jugador) {
        return parejas[jugador];
    }

    public int getCartaA() {
        return cartaA;
    }

    public int getCartaB() {
        return cartaB;
    }
}
