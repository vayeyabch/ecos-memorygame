package ecos.app.modos.Patrones;

import ecos.componentes.DialogoResultado;
import java.awt.Component;
import javax.swing.JComponent;

// Ventana de fin del modo patrones (la de un solo jugador, parecida a VentanaVictoria).
public class VentanaFinPatron {

    public static void mostrar(Component origen, String nombre, int nivelAlcanzado, int casillas, String motivo,
            String premio, Runnable verPremios, Runnable jugarOtra) {
        JComponent cuerpo = DialogoResultado.filaDatos("Niveles superados", "" + (nivelAlcanzado - 1),
                "Casillas del último patrón", "" + casillas);
        new DialogoResultado("¡" + nombre + ", llegaste al nivel " + nivelAlcanzado + "!", motivo, cuerpo,
                "Premio: " + premio, new String[]{"Ver premios", "Jugar otra vez"},
                new Runnable[]{verPremios, jugarOtra}).mostrar(origen);
    }
}
