package ecos.app.modos.JuegoC;

import ecos.componentes.DialogoResultado;
import java.awt.Component;
import java.awt.Image;
import javax.swing.JComponent;

// Ventana de victoria del modo clásico (usa el diálogo de resultado compartido).
public class VentanaVictoria {

    public static void mostrar(Component origen, String nombreGanador, Image m1, String n1, int a1, Image m2, String n2, int a2, boolean gana1, String premio, Runnable verPremios, Runnable cerrar) {
        JComponent cuerpo = DialogoResultado.filaJugadores(m1, n1, a1, gana1, m2, n2, a2);
        new DialogoResultado("¡Ganó " + nombreGanador + "!", null, cuerpo, "Premio: " + premio,
                new String[]{"Ver premios", "Cerrar"}, new Runnable[]{verPremios, cerrar}).mostrar(origen);
    }
}
