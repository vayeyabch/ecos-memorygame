package ecos.app.modos.Patrones;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.Random;

// Las reglas del modo patrones (no dibuja nada, solo lleva la cuenta).
//  - El nivel 1 tiene 3 casillas; cada nivel que se supera suma 1 casilla.
//  - El jugador tiene que tocar las mismas casillas, en el mismo orden.
public class SecuenciaPatron {

    public enum Resultado { CORRECTA, NIVEL_COMPLETO, INCORRECTA }  // Qué pasó al tocar una casilla: acertó, completó el patrón o se equivocó.

    public static final int CASILLAS_TABLERO = 16; // el tablero es de 4x4
    public static final int CASILLAS_INICIALES = 3; // casillas del nivel 1

    private final Random azar = new Random(); // sortea casillas al azar
    private final List<Integer> patron = new ArrayList<>();  // el patrón actual, en orden (números 0 a 15)
    private int nivel = 1;  // nivel actual
    private int posicion = 0; // cuántas casillas del patrón ya acertó en esta ronda

    public int getNivel() {  // Devuelve el nivel actual.
        return nivel;
    }

    // Casillas que tiene el patrón del nivel actual: 3, 4, 5...
    public int getCasillas() {
        return CASILLAS_INICIALES + nivel - 1;
    }

    // Crea un patrón nuevo para el nivel actual (nunca repite la misma casilla seguida).
    public void nuevaRonda() {
        patron.clear(); // borra el patrón anterior
        posicion = 0; // vuelve a empezar desde la primera casilla
        int anterior = -1; // última casilla sorteada (-1 = todavía ninguna)
        for (int i = 0; i < getCasillas(); i++) { // una vuelta por cada casilla del patrón
            int casilla;
            do { // sortea hasta que sea distinta de la anterior
                casilla = azar.nextInt(CASILLAS_TABLERO);
            } while (casilla == anterior);
            patron.add(casilla); // la agrega al patrón
            anterior = casilla;
        }
    }

    public List<Integer> getPatron() {  // Devuelve el patrón (solo para leerlo, no se puede modificar desde afuera).
        return Collections.unmodifiableList(patron);
    }

    // El jugador toca una casilla: ¿es la que sigue en el patrón?
    public Resultado pulsar(int casilla) {
        if (posicion >= patron.size() || casilla != patron.get(posicion)) {  // Se equivocó si ya no quedan casillas o si no es la que toca
            return Resultado.INCORRECTA;
        }
        posicion++; // acertó: ahora espera la siguiente
        return posicion == patron.size() ? Resultado.NIVEL_COMPLETO : Resultado.CORRECTA;  // Si ya tocó todas, el nivel está completo
    }

    public void subirNivel() {  // Pasa al siguiente nivel (el patrón tendrá una casilla más).
        nivel++;
    }
}
