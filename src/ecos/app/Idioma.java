package ecos.app;

import java.util.ArrayList;
import java.util.List;

// Idioma actual del juego, español o inglés. Al cambiar avisa a quienes escuchan para que pongan sus textos de nuevo.
public class Idioma {

    public enum Lengua { ESPANOL, INGLES }

    // Idioma actual y acciones que se ejecutan cuando cambia.
    private static Lengua actual = Lengua.ESPANOL;
    private static final List<Runnable> ESCUCHAS = new ArrayList<>();

    public static Lengua actual() {
        return actual;
    }

    // Cambia el idioma y avisa a los que escuchan. Recorre una copia de la lista por si alguien se agrega en medio.
    public static void cambiar(Lengua nueva) {
        if (nueva == actual) {
            return;
        }
        actual = nueva;
        for (Runnable r : new ArrayList<>(ESCUCHAS)) {
            r.run();
        }
    }

    // Registra algo que debe ejecutarse cuando cambie el idioma.
    public static void alCambiar(Runnable accion) {
        if (!ESCUCHAS.contains(accion)) {
            ESCUCHAS.add(accion);
        }
    }

    // Deja de avisarle a una acción, se usa cuando una pieza ya no está en pantalla.
    public static void quitar(Runnable accion) {
        ESCUCHAS.remove(accion);
    }

    public static String t(String es) {
        if (es == null) {
            return null;
        }
        return actual == Lengua.INGLES ? Textos.ingles(es) : es;
    }
}
