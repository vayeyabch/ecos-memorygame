package ecos.app;

import java.awt.Color; // Clase de Java para representar un color (rojo, verde, azul).
import java.util.ArrayList; // Lista que puede crecer o achicarse.
import java.util.List; // Tipo general de lista.

public class Tema {

    private static boolean oscuro = false; // Guarda el modo actual: false = claro, true = oscuro.
    private static final List<Runnable> ESCUCHAS = new ArrayList<>(); // Lista de acciones que se ejecutan cuando cambia el tema.

    public static boolean esOscuro() { // Pregunta si el tema actual es oscuro.
        return oscuro; // Devuelve true si es oscuro, false si es claro.
    }

    public static void alternar() { // Cambia de modo claro a oscuro, o al revés.
        oscuro = !oscuro; // Invierte el modo actual.
        for (Runnable r : new ArrayList<>(ESCUCHAS)) { // Recorre una copia de la lista (para que no falle si alguien se agrega en medio).
            r.run(); // Ejecuta cada acción registrada para que repinte con los colores nuevos.
        }
    }

    // Registra algo que debe ejecutarse cuando cambie el tema.
    public static void alCambiar(Runnable accion) { // Recibe la acción que se quiere avisar.
        if (!ESCUCHAS.contains(accion)) { // Solo la agrega si no estaba ya (evita repetidos).
            ESCUCHAS.add(accion); // La guarda en la lista de escuchas.
        }
    }

    // Deja de avisarle a una acción (se usa cuando un componente ya no está en pantalla).
    public static void quitar(Runnable accion) { // Recibe la misma acción que se registró antes.
        ESCUCHAS.remove(accion); // La saca de la lista de escuchas.
    }

    public static Color panel()      { return oscuro ? new Color(43, 31, 56)    : new Color(255, 243, 230); } // Color de fondo de los paneles.
    public static Color panelAlto()  { return oscuro ? new Color(61, 45, 79)    : new Color(255, 255, 255); } // Línea clara que se ve dentro del borde de los paneles.
    public static Color caja()       { return oscuro ? new Color(28, 19, 39)    : new Color(244, 217, 196); } // Color de las cajas y zonas internas (fondo de barras y campos).
    public static Color texto()      { return oscuro ? new Color(255, 240, 228) : new Color(58, 27, 38); } // Color de las letras normales.
    public static Color textoSuave() { return oscuro ? new Color(207, 174, 192) : new Color(138, 85, 102); } // Color de las letras secundarias (más apagadas).
    public static Color acento()     { return oscuro ? new Color(255, 170, 90)  : new Color(194, 74, 0); } // Color de resalte para detalles importantes.
    public static Color borde()      { return oscuro ? new Color(11, 7, 16)     : new Color(91, 35, 51); } // Color del borde de paneles y botones.
    public static Color sombra()     { return oscuro ? new Color(7, 4, 12)      : new Color(91, 35, 51); } // Color de la sombra dura que se ve debajo de paneles y botones.
    public static Color bordeExterior() { return new Color(255, 122, 31); } // Naranja que se usa para marcar al jugador con el turno.
    public static Color dorado()     { return new Color(255, 204, 128); } // Durazno/dorado, igual en los dos modos.
    public static Color boton()      { return new Color(255, 204, 128); } // Color de fondo de los botones principales.
    public static Color textoBoton() { return new Color(194, 74, 0); } // Color de las letras de los botones principales.
    public static Color boton2()     { return oscuro ? new Color(61, 45, 79)    : new Color(247, 247, 247); } // Color de fondo de los botones secundarios.
    public static Color textoBoton2() { return oscuro ? new Color(252, 201, 205) : new Color(0, 71, 181); } // Color de las letras de los botones secundarios.
    public static Color rosa()       { return new Color(252, 165, 171); } // Rosa de la paleta del juego.
    public static Color carmesi()    { return new Color(194, 63, 82); } // Rojo carmesí de la paleta del juego.
    public static Color naranja()    { return new Color(255, 122, 31); } // Naranja fuerte de la paleta del juego.
    public static Color carta()      { return oscuro ? new Color(255, 241, 220) : new Color(255, 248, 234); } // Color de la cara de las cartas.
    public static Color acierto()    { return oscuro ? new Color(95, 217, 138)  : new Color(63, 191, 111); } // Verde para cuando se acierta.
    public static Color alerta()     { return oscuro ? new Color(255, 209, 92)  : new Color(255, 194, 51); } // Amarillo para avisos.
    public static Color peligro()    { return oscuro ? new Color(255, 91, 114)  : new Color(229, 52, 76); } // Rojo para errores o peligro.
    public static Color velo()       { return oscuro ? new Color(8, 5, 14, 184) : new Color(91, 35, 51, 148); } // Cortina semitransparente que oscurece la pantalla detrás de un diálogo.
}
