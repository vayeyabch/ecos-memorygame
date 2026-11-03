package ecos.app.modos.Patrones;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.Random;

// Las reglas del modo patrones (no dibuja nada, solo lleva la cuenta).
//  - El nivel 1 tiene 3 casillas; cada nivel que se supera suma 1 casilla.
//  - El jugador tiene que tocar las mismas casillas, en el mismo orden.
public class SecuenciaPatron {

    public enum Resultado { CORRECTA, NIVEL_COMPLETO, INCORRECTA }

    public static final int CASILLAS_TABLERO = 16; // el tablero es de 4x4
    public static final int CASILLAS_INICIALES = 3;

    private final Random azar = new Random();
    private final List<Integer> patron = new ArrayList<>();
    private int nivel = 1;
    private int posicion = 0; // cuántas casillas del patrón ya acertó en esta ronda

    public int getNivel() {
        return nivel;
    }

    // Casillas que tiene el patrón del nivel actual: 3, 4, 5...
    public int getCasillas() {
        return CASILLAS_INICIALES + nivel - 1;
    }

    // Crea un patrón nuevo para el nivel actual (nunca repite la misma casilla seguida).
    public void nuevaRonda() {
        patron.clear();
        posicion = 0;
        int anterior = -1;
        for (int i = 0; i < getCasillas(); i++) {
            int casilla;
            do {
                casilla = azar.nextInt(CASILLAS_TABLERO);
            } while (casilla == anterior);
            patron.add(casilla);
            anterior = casilla;
        }
    }

    public List<Integer> getPatron() {
        return Collections.unmodifiableList(patron);
    }

    // El jugador toca una casilla: ¿es la que sigue en el patrón?
    public Resultado pulsar(int casilla) {
        if (posicion >= patron.size() || casilla != patron.get(posicion)) {
            return Resultado.INCORRECTA;
        }
        posicion++;
        return posicion == patron.size() ? Resultado.NIVEL_COMPLETO : Resultado.CORRECTA;
    }

    public void subirNivel() {
        nivel++;
    }
}
