package ecos.app;

import java.awt.Color;
import java.util.ArrayList;
import java.util.List;

/**
 * Colores del juego en modo claro (por defecto) y oscuro.
 * Los colores claros son los mismos de la paleta de Isa.
 * Tema.alternar() cambia de modo y avisa a quien se haya registrado con alCambiar().
 */
public class Tema {

    private static boolean oscuro = false;
    private static final List<Runnable> ESCUCHAS = new ArrayList<>();

    public static boolean esOscuro() {
        return oscuro;
    }

    public static void alternar() {
        oscuro = !oscuro;
        for (Runnable r : new ArrayList<>(ESCUCHAS)) {
            r.run();
        }
    }

    /** Registra algo que debe ejecutarse cuando cambie el tema (por ejemplo repaint()). */
    public static void alCambiar(Runnable accion) {
        ESCUCHAS.add(accion);
    }

    public static Color panel()      { return oscuro ? new Color(40, 31, 58)    : new Color(252, 243, 231); }
    public static Color texto()      { return oscuro ? new Color(246, 238, 255) : new Color(91, 37, 56); }
    public static Color acento()     { return oscuro ? new Color(255, 170, 90)  : new Color(200, 96, 30); }
    public static Color borde()      { return oscuro ? new Color(18, 12, 30)    : new Color(91, 37, 56); }
    public static Color bordeExterior() { return oscuro ? new Color(150, 110, 220) : new Color(229, 129, 47); }
    public static Color caja()       { return oscuro ? new Color(66, 52, 92)    : new Color(238, 217, 196); }
    public static Color dorado()     { return new Color(240, 204, 136); }
    public static Color boton()      { return oscuro ? new Color(150, 110, 220) : new Color(229, 129, 47); }
    public static Color textoBoton() { return Color.WHITE; }
    public static Color acierto()    { return new Color(88, 190, 110); }
    public static Color alerta()     { return new Color(240, 190, 60); }
    public static Color peligro()    { return new Color(214, 70, 80); }
}
