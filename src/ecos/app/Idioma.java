package ecos.app;

// Utilidades de Java, ArrayList para listas que crecen y List para las listas.
import java.util.ArrayList;
import java.util.List;

// Idioma actual del juego, español o inglés; al cambiar avisa a las piezas que estén escuchando para que vuelvan a poner sus
// textos, igual que Tema con los colores.
public class Idioma {

    // Los dos idiomas del juego.
    public enum Lengua { ESPANOL, INGLES }

    // Guarda el idioma actual. Lista de acciones que se ejecutan cuando cambia el idioma.
    private static Lengua actual = Lengua.ESPANOL;
    private static final List<Runnable> ESCUCHAS = new ArrayList<>();

    // Entrega el idioma actual.
    public static Lengua actual() {
        // Devuelve español o inglés.
        return actual;
    }

    // Cambia el idioma y avisa a todas las piezas que estén escuchando.
    // Recibe el idioma nuevo.
    public static void cambiar(Lengua nueva) {
        // Si ya estaba en ese idioma no hace nada. Guarda el idioma nuevo. Recorre una copia de la lista (para que no falle si
        // alguien se agrega en medio). Ejecuta cada acción registrada para que ponga sus textos nuevos.
        if (nueva == actual) {
            return;
        }
        actual = nueva;
        for (Runnable r : new ArrayList<>(ESCUCHAS)) {
            r.run();
        }
    }

    // Registra algo que debe ejecutarse cuando cambie el idioma.
    // Recibe la acción que se quiere avisar.
    public static void alCambiar(Runnable accion) {
        // Solo la agrega si no estaba ya (evita repetidos). La guarda en la lista de escuchas.
        if (!ESCUCHAS.contains(accion)) {
            ESCUCHAS.add(accion);
        }
    }

    // Deja de avisarle a una acción (se usa cuando una pieza ya no está en pantalla).
    // Recibe la misma acción que se registró antes.
    public static void quitar(Runnable accion) {
        // La saca de la lista de escuchas.
        ESCUCHAS.remove(accion);
    }

    // Entrega un texto en el idioma actual. El texto que se pasa siempre es el de español, que sirve de clave.
    // Recibe el texto en español.
    public static String t(String es) {
        // Si no hay texto, no hay nada que traducir. En español deja el texto igual, en inglés busca su traducción.
        if (es == null) {
            return null;
        }
        return actual == Lengua.INGLES ? Textos.ingles(es) : es;
    }
}
