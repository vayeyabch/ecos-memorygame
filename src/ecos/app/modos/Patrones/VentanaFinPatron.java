package ecos.app.modos.Patrones;

// Clases del propio proyecto que se usan aquí.
import ecos.componentes.DialogoResultado;
// Librería gráfica AWT, Component para cualquier elemento de la pantalla.
import java.awt.Component;
// Librería Swing, JComponent para la base de los componentes.
import javax.swing.JComponent;

// Ventana de fin del modo patrones (la de un solo jugador, parecida a VentanaVictoria).
public class VentanaFinPatron {

// Abre la ventana final. Origen = pantalla sobre la que aparece; verPremios / jugarOtra = lo que hacen los botones
    public static void mostrar(Component origen, String nombre, int nivelAlcanzado, int casillas, String motivo,
            // Cuerpo, dos datos (niveles superados = nivel alcanzado - 1, porque el último nivel no lo pasó).
            String premio, Runnable verPremios, Runnable jugarOtra) {
        JComponent cuerpo = DialogoResultado.filaDatos("Niveles superados", "" + (nivelAlcanzado - 1),
                // Título, motivo (subtítulo), cuerpo, premio abajo y los dos botones.
                "Casillas del último patrón", "" + casillas);
        new DialogoResultado("¡" + nombre + ", llegaste al nivel " + nivelAlcanzado + "!", motivo, cuerpo,
                "Premio: " + premio, new String[]{"Ver premios", "Jugar otra vez"},
                new Runnable[]{verPremios, jugarOtra}).mostrar(origen);
    }
}
