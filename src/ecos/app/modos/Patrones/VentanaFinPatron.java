package ecos.app.modos.Patrones;

import ecos.componentes.DialogoResultado;
import java.awt.Component;
import javax.swing.JComponent;

// Ventana de fin del modo patrones, de un solo jugador. Parecida a VentanaVictoria pero sin premio.
public class VentanaFinPatron {

    public static void mostrar(Component origen, String nombre, int nivelAlcanzado, int casillas, String motivo,
            Runnable irMenu, Runnable jugarOtra) {
        // Dos datos: niveles superados (al último no llegó a pasarlo, por eso se resta uno) y casillas.
        JComponent cuerpo = DialogoResultado.filaDatos("Niveles superados", "" + (nivelAlcanzado - 1),
                "Casillas del último patrón", "" + casillas);
        // Título, motivo, cuerpo y botones.
        new DialogoResultado("¡" + nombre + ", llegaste al nivel " + nivelAlcanzado + "!", motivo, cuerpo,
                null, new String[]{"Volver al menú", "Jugar otra vez"},
                new Runnable[]{irMenu, jugarOtra}).mostrar(origen);
    }
}
