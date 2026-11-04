package ecos.app.modos.Patrones;

import ecos.componentes.DialogoResultado;
import java.awt.Component;
import javax.swing.JComponent;

// Ventana de fin del modo patrones (la de un solo jugador, parecida a VentanaVictoria).
public class VentanaFinPatron {

// Abre la ventana final. origen = pantalla sobre la que aparece; verPremios / jugarOtra = lo que hacen los botones
    public static void mostrar(Component origen, String nombre, int nivelAlcanzado, int casillas, String motivo,
            String premio, Runnable verPremios, Runnable jugarOtra) {   // Cuerpo: dos datos (niveles superados = nivel alcanzado - 1, porque el último nivel no lo pasó)
        JComponent cuerpo = DialogoResultado.filaDatos("Niveles superados", "" + (nivelAlcanzado - 1),
                "Casillas del último patrón", "" + casillas);  // Título, motivo (subtítulo), cuerpo, premio abajo y los dos botones
        new DialogoResultado("¡" + nombre + ", llegaste al nivel " + nivelAlcanzado + "!", motivo, cuerpo,
                "Premio: " + premio, new String[]{"Ver premios", "Jugar otra vez"},
                new Runnable[]{verPremios, jugarOtra}).mostrar(origen);
    }
}
