package ecos.app.modos.JuegoC;

// Clases del propio proyecto que se usan aquí.
import ecos.componentes.DialogoResultado;
// Librería gráfica AWT, Component para cualquier elemento de la pantalla y Image para las imágenes.
import java.awt.Component;
import java.awt.Image;
// Librería Swing, JComponent para la base de los componentes.
import javax.swing.JComponent;

// Ventana de victoria del modo clásico (usa el diálogo de resultado compartido).
public class VentanaVictoria {

    public static void mostrar(Component origen, String nombreGanador, Image m1, String n1, int a1, Image m2, String n2, int a2, boolean gana1, String premio, String extra, Runnable verPremios, Runnable cerrar) {
        // Arma una fila por jugador con la mascota, el nombre y los intentos. Si no hay cuenta no hay premio, y al final
        // muestra el diálogo con título, cuerpo y botones.
        JComponent cuerpo = DialogoResultado.filaJugadores(m1, n1, a1, gana1, m2, n2, a2);
        new DialogoResultado("¡Ganó " + nombreGanador + "!", extra, cuerpo, premio == null ? null : "Premio: " + premio,
                new String[]{"Ver premios", "Cerrar"}, new Runnable[]{verPremios, cerrar}).mostrar(origen);
    }
}
