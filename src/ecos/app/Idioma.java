package ecos.app;

// ArrayList es la clase concreta de la lista. Sirve para crear la lista de escuchas y para sacar una copia de ella.
import java.util.ArrayList;
// List es el tipo de la lista de escuchas, o sea las acciones que se ejecutan cuando cambia el idioma.
import java.util.List;

// Guarda en qué idioma está el juego, español o inglés, y avisa cuando cambia para que todos los textos se pongan de nuevo.
// Funciona igual que Tema, con el mismo patrón de suscripción: una lista de acciones que se ejecutan al cambiar.
// Todo es static, así que se usa escribiendo Idioma.t("texto"), sin crear un objeto.
// Textos no se importa porque está en el mismo paquete, ecos.app.
public class Idioma {

    // Un enum es una lista de opciones fijas. Aquí son los dos idiomas posibles, y no se puede poner otro por error.
    public enum Lengua { ESPANOL, INGLES }

    // El idioma actual. Empieza en español.
    private static Lengua actual = Lengua.ESPANOL;
    // Lista de acciones guardadas, que se ejecutan cuando cambia el idioma.
    // Runnable es una acción lista para ejecutarse, sin datos de entrada ni de salida. Casi siempre es volver a traducirse.
    // Es final porque siempre es la misma lista, aunque se le agreguen o quiten acciones.
    private static final List<Runnable> ESCUCHAS = new ArrayList<>();

    // Dice cuál es el idioma actual. Lo usa VentanaIdiomas para saber qué botón pintar como activo.
    public static Lengua actual() {
        return actual;
    }

    // Cambia el idioma y avisa a todas las piezas suscritas. Lo llama VentanaIdiomas al tocar un idioma.
    public static void cambiar(Lengua nueva) {
        // Si se elige el mismo idioma que ya está, no hace nada. El return termina el método aquí.
        // Para comparar enums se usa ==, porque cada opción es un único objeto.
        if (nueva == actual) {
            return;
        }
        actual = nueva;
        // Ejecuta cada acción guardada con run. Cada pieza vuelve a traducir su texto.
        // Se recorre una copia de la lista por si alguien se agrega mientras se ejecutan las acciones, lo que daría error.
        for (Runnable r : new ArrayList<>(ESCUCHAS)) {
            r.run();
        }
    }

    // Sirve para suscribirse. Una pieza, como una Etiqueta, le dice a Idioma: avisame cuando cambie el idioma.
    // La acción se guarda aquí y no se ejecuta todavía.
    public static void alCambiar(Runnable accion) {
        // Solo la guarda si todavía no está en la lista, para que una misma acción no se ejecute dos veces.
        if (!ESCUCHAS.contains(accion)) {
            ESCUCHAS.add(accion);
        }
    }

    // Es lo contrario de alCambiar. Saca la acción de la lista.
    // Se usa cuando una pieza sale de la pantalla, para que Idioma no siga avisándole a algo que nadie ve.
    public static void quitar(Runnable accion) {
        ESCUCHAS.remove(accion);
    }

    // Traduce un texto en español al idioma actual. Es el método que más se usa de esta clase.
    // La t es de traducir, y es corta porque se escribe en muchísimos lugares.
    public static String t(String es) {
        // Si el texto es null, devuelve null para no fallar.
        if (es == null) {
            return null;
        }
        // El signo de pregunta es un if corto. Si el idioma es inglés, le pide la traducción a Textos.
        // Si es español, devuelve el mismo texto sin tocarlo.
        return actual == Lengua.INGLES ? Textos.ingles(es) : es;
    }
}