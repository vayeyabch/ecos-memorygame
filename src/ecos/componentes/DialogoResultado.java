package ecos.componentes;

import java.awt.Window;
import javax.swing.JDialog;

/**
 * Dialogo de fin de partida (victoria o derrota). Reemplaza a VentanaVictoria.
 * Debe usar PanelPixel y BotonPixel para respetar el tema claro/oscuro.
 * Td: mostrar ganador, intentos de cada jugador, premio y botones "Ver premios" / "Cerrar".
 */
public class DialogoResultado extends JDialog {

    public DialogoResultado(Window padre) {
        super(padre, ModalityType.APPLICATION_MODAL);
        setUndecorated(true);
        // Td: construir el contenido, pack() y setLocationRelativeTo(padre)
    }
}
