package ecos.app.modos.JuegoC;

// Utilidades de Java, Random para los números al azar.
import java.util.Random;

// Las reglas del modo clásico. No usa pantallas ni dibuja nada, solo guarda el estado.
// Cada jugador tiene su propia matriz n x n, distinta de la del otro, y un intento es voltear dos cartas.
// Gana el primero que encuentra todas las parejas de su tablero (18 en el de 6x6).
public class PartidaClasica {

    public enum Jugada { IGNORADA, PRIMERA, SEGUNDA, ACIERTO, FALLO }

    // Tamaño de la matriz (6 para 6x6, 4 para 4x4). caras de cada jugador. Marca cuáles cartas ya se encontraron.
    // intentos del jugador.
    private final int n;
    private final int[][][] caras = new int[2][][];
    private final boolean[][] hecha = new boolean[2][];
    private final int[] intentos = new int[2];
    // Parejas encontradas del jugador, la comparación de dos cartas para saber si son pareja, el jugador que tiene el turno
    // (0 o 1) y la primera carta volteada (-1 si no hay ninguna).
    private final int[] parejas = new int[2];
    private final ComparadorParejasClasico comparador = new ComparadorParejasClasico();
    private int turno = 0;
    private int primera = -1;
    // Las dos cartas del último intento. Quién completó su tablero primero.
    private int cartaA = -1;
    private int cartaB = -1;
    private int terminoPrimero = -1;

    public PartidaClasica(int n) {
        // Tamaño de la matriz (6 para 6x6, 4 para 4x4).
        this.n = n;
        for (int p = 0; p < 2; p++) {
            // Un azar distinto por jugador. 36 valores false por defecto.
            caras[p] = MezclaCartasClasico.crearMatriz(n, new Random());
            hecha[p] = new boolean[n * n];
        }
    }

    // El indice va de 0 a n*n-1, fila = indice / n, columna = indice % n.
    private int cara(int jugador, int indice) {
        // Devuelve la cara de la carta en esa posición.
        return caras[jugador][indice / n][indice % n];
    }

    // El jugador toca una carta.
    public Jugada seleccionar(int jugador, int indice) {
        // No es su turno, o ya encontró esa carta, o tocó la misma dos veces.
        if (jugador != turno || hecha[jugador][indice] || indice == primera) {
            return Jugada.IGNORADA;
        }
        // No hay primera carta, esta será la primera.
        if (primera < 0) {
            primera = indice;
            return Jugada.PRIMERA;
        }
        cartaA = primera;
        // La segunda carta.
        cartaB = indice;
        primera = -1;
        // Incrementa el contador de intentos del jugador.
        intentos[jugador]++;
        return Jugada.SEGUNDA; 
    }

    // Se llama después de la SEGUNDA carta, compara y actualiza.
    public Jugada resolver() {
        // Compara las dos cartas del jugador que tiene el turno.
        boolean iguales = comparador.comparar(cara(turno, cartaA), cara(turno, cartaB));
        if (!iguales) {
            // Da fallo si no son iguales.
            return Jugada.FALLO;
        }
        hecha[turno][cartaA] = true;
        hecha[turno][cartaB] = true;
        parejas[turno]++;
        if (completo(turno) && terminoPrimero < 0) {
            terminoPrimero = turno;
        }
        // Da acierto si son iguales y marca las cartas como encontradas.
        return Jugada.ACIERTO;
    }

    // Pasa el turno al otro jugador, salvo que el otro ya terminó. Dice si cambió.
    public boolean pasarTurno() {
        int otro = 1 - turno;
        if (completo(otro)) {
            return false;
        }
        turno = otro;
        // Si es 0, da 1; si es 1, da 0.
        primera = -1;
        return true;
    }

    // Se acabó el tiempo, devuelve la carta que quedó volteada (o -1 si no había).
    public int cancelarSeleccion() {
        int carta = primera;
        primera = -1;
        return carta;
    }

    // Dice si hay una carta volteada.
    public boolean hayPrimera() {
        return primera >= 0;
    }

    // Dice si el jugador completó su tablero.
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

    // Devuelve la matriz de caras del jugador.
    public int[][] getCaras(int jugador) {
        return caras[jugador];
    }

    // Devuelve el jugador que tiene el turno (0 o 1).
    public int getTurno() {
        return turno;
    }

    // Devuelve la cantidad de intentos del jugador.
    public int getIntentos(int jugador) {
        return intentos[jugador];
    }

    // Devuelve la cantidad de parejas encontradas del jugador.
    public int getParejas(int jugador) {
        return parejas[jugador];
    }

    // Devuelve la primera carta.
    public int getCartaA() {
        return cartaA;
    }

    // Devuelve la segunda carta.
    public int getCartaB() {
        return cartaB;
    }
}
