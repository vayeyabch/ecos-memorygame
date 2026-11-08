package ecos.app.modos.Patrones;

// Utilidades de Java, ArrayList para listas que crecen, Collections para mezclar listas, List para las listas y Random
// Utilidades de Java, ArrayList para listas que crecen, Collections para mezclar listas, List para las listas y Random
// para los números al azar.
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.Random;

// Las reglas del modo patrones (no dibuja nada, solo lleva la cuenta).
// El nivel 1 tiene 3 casillas y cada nivel que se supera suma una más.
// El jugador tiene que tocar las mismas casillas, en el mismo orden.
public class SecuenciaPatron {

    // Qué pasó al tocar una casilla, acertó, completó el patrón o se equivocó.
    public enum Resultado { CORRECTA, NIVEL_COMPLETO, INCORRECTA }

    // El tablero es de 4x4. Casillas del nivel 1.
    public static final int CASILLAS_TABLERO = 16;
    public static final int CASILLAS_INICIALES = 3;

    // Sortea casillas al azar. El patrón actual, en orden (números 0 a 15). cuántas casillas del patrón ya acertó en esta ronda.
    private final Random azar = new Random();
    private final List<Integer> patron = new ArrayList<>();
    private int nivel = 1;
    private int posicion = 0;

    // Devuelve el nivel actual.
    public int getNivel() {
        return nivel;
    }

    // Casillas que tiene el patrón del nivel actual, 3, 4, 5...
    public int getCasillas() {
        return CASILLAS_INICIALES + nivel - 1;
    }

    // Crea un patrón nuevo para el nivel actual (nunca repite la misma casilla seguida).
    public void nuevaRonda() {
        // Borra el patrón anterior. Vuelve a empezar desde la primera casilla. Última casilla sorteada (-1 = todavía
        // ninguna). una vuelta por cada casilla del patrón.
        patron.clear();
        posicion = 0;
        int anterior = -1;
        for (int i = 0; i < getCasillas(); i++) {
            int casilla;
            // Sortea hasta que sea distinta de la anterior.
            do {
                casilla = azar.nextInt(CASILLAS_TABLERO);
            } while (casilla == anterior);
            // La agrega al patrón.
            patron.add(casilla);
            anterior = casilla;
        }
    }

    // Devuelve el patrón (solo para leerlo, no se puede modificar desde afuera).
    public List<Integer> getPatron() {
        return Collections.unmodifiableList(patron);
    }

    // El jugador toca una casilla. ¿Es la que sigue en el patrón?
    public Resultado pulsar(int casilla) {
        // Se equivocó si ya no quedan casillas o si no es la que toca.
        if (posicion >= patron.size() || casilla != patron.get(posicion)) {
            return Resultado.INCORRECTA;
        }
        // Acertó, ahora espera la siguiente. Si ya tocó todas, el nivel está completo.
        posicion++;
        return posicion == patron.size() ? Resultado.NIVEL_COMPLETO : Resultado.CORRECTA;
    }

    // Pasa al siguiente nivel (el patrón tendrá una casilla más).
    public void subirNivel() {
        nivel++;
    }
}
