package ecos.app.modos.JuegoC;

import java.util.Random;

// Las reglas del modo clásico. No usa pantallas ni dibuja nada: solo guarda el estado.
//  - Cada jugador tiene su matriz n x n (distinta una de otra).
//  - Un intento = voltear dos cartas.
//  - Gana el primero que encuentra todas las parejas de su tablero (18 en el de 6x6).
public class PartidaClasica {

    public enum Jugada { IGNORADA, PRIMERA, SEGUNDA, ACIERTO, FALLO }

    private final int n;
    private final int[][][] caras = new int[2][][]; // caras[jugador][fila][columna]
    private final boolean[][] hecha = new boolean[2][]; // hecha[jugador][indice]
    private final int[] intentos = new int[2];
    private final int[] parejas = new int[2];
    private final ComparadorParejasClasico comparador = new ComparadorParejasClasico();
    private int turno = 0;
    private int primera = -1; // primera carta volteada (-1 = ninguna)
    private int cartaA = -1; // las dos cartas del último intento
    private int cartaB = -1;
    private int terminoPrimero = -1; // quién completó su tablero primero

    public PartidaClasica(int n) {
        this.n = n;
        for (int p = 0; p < 2; p++) {
            caras[p] = MezclaCartasClasico.crearMatriz(n, new Random()); // un azar distinto por jugador
            hecha[p] = new boolean[n * n];
        }
    }

    // El indice va de 0 a n*n-1: fila = indice / n, columna = indice % n.
    private int cara(int jugador, int indice) {
        return caras[jugador][indice / n][indice % n];
    }

    // El jugador toca una carta.
    public Jugada seleccionar(int jugador, int indice) {
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

    // Se llama después de la SEGUNDA carta: compara y actualiza.
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

    // Se acabó el tiempo: devuelve la carta que quedó volteada (o -1 si no había).
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

    // La partida se acaba cuando alguien completa todas sus parejas.
    public boolean terminado() {
        return completo(0) || completo(1);
    }

    // El ganador es quien completó primero su tablero (-1 si todavía nadie).
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
