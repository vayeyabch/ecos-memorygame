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
        ESCUCHAS.add(accion); // La guarda en la lista de escuchas.
    }

    public static Color panel()      { return oscuro ? new Color(40, 31, 58)    : new Color(252, 243, 231); } // Color de fondo de los paneles.
    public static Color texto()      { return oscuro ? new Color(246, 238, 255) : new Color(91, 37, 56); } // Color de las letras.
    public static Color acento()     { return oscuro ? new Color(255, 170, 90)  : new Color(200, 96, 30); } // Color de resalte para detalles importantes.
    public static Color borde()      { return oscuro ? new Color(18, 12, 30)    : new Color(91, 37, 56); } // Color del borde de paneles y botones.
    public static Color bordeExterior() { return oscuro ? new Color(150, 110, 220) : new Color(229, 129, 47); } // Color del borde de afuera de los paneles.
    public static Color caja()       { return oscuro ? new Color(66, 52, 92)    : new Color(238, 217, 196); } // Color de las cajas y zonas internas.
    public static Color dorado()     { return new Color(240, 204, 136); } // Color dorado, igual en los dos modos.
    public static Color boton()      { return oscuro ? new Color(150, 110, 220) : new Color(229, 129, 47); } // Color de fondo de los botones.
    public static Color textoBoton() { return Color.WHITE; } // Color de las letras de los botones: siempre blanco.
    public static Color acierto()    { return new Color(88, 190, 110); } // Verde para cuando se acierta.
    public static Color alerta()     { return new Color(240, 190, 60); } // Amarillo para avisos.
    public static Color peligro()    { return new Color(214, 70, 80); } // Rojo para errores o peligro.
}