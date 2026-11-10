package ecos.app.modos.Patrones;

// Clases del propio proyecto que se usan aquí.
import ecos.componentes.DialogoResultado;
// Librería gráfica AWT, Component para cualquier elemento de la pantalla.
import java.awt.Component;
// Librería Swing, JComponent para la base de los componentes.
import javax.swing.JComponent;

// Ventana de fin del modo patrones, la de un solo jugador, parecida a VentanaVictoria pero sin premio.
public class VentanaFinPatron {

    // Abre la ventana final sobre la pantalla de origen. Recibe el nombre, el nivel alcanzado, las casillas del último patrón,
    // el motivo por el que terminó y lo que hacen los botones de menú y jugar otra vez.
    public static void mostrar(Component origen, String nombre, int nivelAlcanzado, int casillas, String motivo,
            Runnable irMenu, Runnable jugarOtra) {
        // El cuerpo muestra dos datos, los niveles superados (el último nivel no lo pasó, por eso se resta uno) y las casillas.
        JComponent cuerpo = DialogoResultado.filaDatos("Niveles superados", "" + (nivelAlcanzado - 1),
                "Casillas del último patrón", "" + casillas);
        // Título, motivo, cuerpo y los dos botones, sin premio.
        new DialogoResultado("¡" + nombre + ", llegaste al nivel " + nivelAlcanzado + "!", motivo, cuerpo,
                null, new String[]{"Volver al menú", "Jugar otra vez"},
                new Runnable[]{irMenu, jugarOtra}).mostrar(origen);
    }
}
