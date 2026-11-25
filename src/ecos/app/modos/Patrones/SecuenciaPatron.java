package ecos.app.modos.Patrones;

// ArrayList: lista donde se guarda el patrón de casillas.
import java.util.ArrayList;
// Collections: ayudante; aquí devuelve la lista como "solo lectura".
import java.util.Collections;
// List: tipo de la lista del patrón.
import java.util.List;
// Random: sortea las casillas al azar.
import java.util.Random;

// Reglas del modo patrones. No dibuja nada, solo lleva la cuenta. El nivel 1 tiene 3 casillas y cada nivel superado suma
// una más. El jugador toca las mismas casillas en el mismo orden.
public class SecuenciaPatron {

    // Qué pasó al tocar una casilla: acertó, completó el patrón o se equivocó.
    public enum Resultado { CORRECTA, NIVEL_COMPLETO, INCORRECTA }

    // Tablero de 4x4.
    public static final int CASILLAS_TABLERO = 16;
    public static final int CASILLAS_INICIALES = 3;

    // Azar para sortear casillas, patrón actual (0 a 15) y cuántas casillas ya acertó en la ronda.
    private final Random azar = new Random();
    private final List<Integer> patron = new ArrayList<>();
    private int nivel = 1;
    // posicion: cuál casilla del patrón toca ahora (empieza en 0).
    private int posicion = 0;

    public int getNivel() {
        return nivel;
    }

    // Casillas del patrón del nivel actual: 3, 4, 5...
    public int getCasillas() {
        return CASILLAS_INICIALES + nivel - 1;
    }

    // Crea un patrón nuevo para el nivel, sin repetir la misma casilla seguida.
    public void nuevaRonda() {
        patron.clear();
        posicion = 0;
        // anterior: la última casilla sorteada (-1 al inicio, que no es ninguna casilla).
        int anterior = -1;
        for (int i = 0; i < getCasillas(); i++) {
            int casilla;
            // do-while: sortea y repite mientras salga la misma que la anterior.
            do {
                casilla = azar.nextInt(CASILLAS_TABLERO);
            } while (casilla == anterior);
            patron.add(casilla);
            anterior = casilla;
        }
    }

    // Devuelve el patrón como lista de solo lectura, para que nadie de afuera lo cambie.
    public List<Integer> getPatron() {
        return Collections.unmodifiableList(patron);
    }

    // El jugador toca una casilla: ¿es la que sigue en el patrón?
    public Resultado pulsar(int casilla) {
        // Si no hay patrón o la casilla no es la que toca, se equivocó.
        if (posicion >= patron.size() || casilla != patron.get(posicion)) {
            return Resultado.INCORRECTA;
        }
        // Acertó; si ya tocó todas, el nivel está completo.
        posicion++;
        return posicion == patron.size() ? Resultado.NIVEL_COMPLETO : Resultado.CORRECTA;
    }

    // Pasa al siguiente nivel, con una casilla más.
    public void subirNivel() {
        nivel++;
    }
}