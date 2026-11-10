package ecos.app;

// Librería gráfica AWT, Color para los colores.
import java.awt.Color;
// Utilidades de Java, ArrayList para listas que crecen y List para las listas.
import java.util.ArrayList;
import java.util.List;

// Colores del juego en modo claro y oscuro; al cambiar de tema avisa a las piezas que estén escuchando para que se repinten.
public class Tema {

    // Guarda el modo actual, false = claro, true = oscuro. Lista de acciones que se ejecutan cuando cambia el tema.
    private static boolean oscuro = false;
    private static final List<Runnable> ESCUCHAS = new ArrayList<>();

    // Pregunta si el tema actual es oscuro.
    public static boolean esOscuro() {
        // Devuelve true si es oscuro, false si es claro.
        return oscuro;
    }

    // Cambia de modo claro a oscuro, o al revés.
    public static void alternar() {
        // Invierte el modo actual. Recorre una copia de la lista (para que no falle si alguien se agrega en medio). Ejecuta
        // cada acción registrada para que repinte con los colores nuevos.
        oscuro = !oscuro;
        for (Runnable r : new ArrayList<>(ESCUCHAS)) {
            r.run();
        }
    }

    // Registra algo que debe ejecutarse cuando cambie el tema.
    // Recibe la acción que se quiere avisar.
    public static void alCambiar(Runnable accion) {
        // Solo la agrega si no estaba ya (evita repetidos). La guarda en la lista de escuchas.
        if (!ESCUCHAS.contains(accion)) {
            ESCUCHAS.add(accion);
        }
    }

    // Deja de avisarle a una acción (se usa cuando un componente ya no está en pantalla).
    // Recibe la misma acción que se registró antes.
    public static void quitar(Runnable accion) {
        // La saca de la lista de escuchas.
        ESCUCHAS.remove(accion);
    }

    // Cada método devuelve un color distinto según el modo actual, claro u oscuro. Algunos son iguales en los dos modos.

    // Fondo de los paneles.
    public static Color panel()      { return oscuro ? new Color(43, 31, 56)    : new Color(255, 243, 230); }
    // Línea clara que se ve dentro del borde de los paneles.
    public static Color panelAlto()  { return oscuro ? new Color(61, 45, 79)    : new Color(255, 255, 255); }
    // Cajas y zonas internas, como el fondo de las barras y los campos.
    public static Color caja()       { return oscuro ? new Color(28, 19, 39)    : new Color(244, 217, 196); }
    // Letras normales y letras secundarias, más apagadas.
    public static Color texto()      { return oscuro ? new Color(255, 240, 228) : new Color(58, 27, 38); }
    public static Color textoSuave() { return oscuro ? new Color(207, 174, 192) : new Color(138, 85, 102); }
    // Color de resalte para detalles importantes.
    public static Color acento()     { return oscuro ? new Color(255, 170, 90)  : new Color(194, 74, 0); }
    // Borde de paneles y botones, y la sombra dura que se ve debajo de ellos.
    public static Color borde()      { return oscuro ? new Color(11, 7, 16)     : new Color(91, 35, 51); }
    public static Color sombra()     { return oscuro ? new Color(7, 4, 12)      : new Color(91, 35, 51); }
    // Naranja que se usa para marcar al jugador con el turno.
    public static Color bordeExterior() { return new Color(255, 122, 31); }
    // Durazno dorado, igual en los dos modos.
    public static Color dorado()     { return new Color(255, 204, 128); }
    // Botones principales, el fondo y el color de sus letras.
    public static Color boton()      { return new Color(255, 204, 128); }
    public static Color textoBoton() { return new Color(194, 74, 0); }
    // Botones secundarios, el fondo y el color de sus letras.
    public static Color boton2()     { return oscuro ? new Color(61, 45, 79)    : new Color(247, 247, 247); }
    public static Color textoBoton2() { return oscuro ? new Color(252, 201, 205) : new Color(0, 71, 181); }
    // Rosa, rojo carmesí y naranja de la paleta del juego, iguales en los dos modos.
    public static Color rosa()       { return new Color(252, 165, 171); }
    public static Color carmesi()    { return new Color(194, 63, 82); }
    public static Color naranja()    { return new Color(255, 122, 31); }
    // Cara de las cartas.
    public static Color carta()      { return oscuro ? new Color(255, 241, 220) : new Color(255, 248, 234); }
    // Verde para cuando se acierta, amarillo para avisos y rojo para errores o peligro.
    public static Color acierto()    { return oscuro ? new Color(95, 217, 138)  : new Color(63, 191, 111); }
    public static Color alerta()     { return oscuro ? new Color(255, 209, 92)  : new Color(255, 194, 51); }
    public static Color peligro()    { return oscuro ? new Color(255, 91, 114)  : new Color(229, 52, 76); }
    // Cortina semitransparente que oscurece la pantalla detrás de un diálogo.
    public static Color velo()       { return oscuro ? new Color(8, 5, 14, 184) : new Color(91, 35, 51, 148); }
}
