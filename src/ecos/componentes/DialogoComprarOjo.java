package ecos.componentes;

import java.awt.Window;
import javax.swing.JDialog;

/**
 * Dialogo para comprar el "ojo espia" con monedas.
 * Sonidos: "comprar" si alcanza, "sin_monedas" si no, "ojo_espia" al activarlo.
 * td: mostrar costo y monedas del jugador, botones "Comprar" / "Cancelar".
 */
public class DialogoComprarOjo extends JDialog {

    public DialogoComprarOjo(Window padre) {
        super(padre, ModalityType.APPLICATION_MODAL);
        setUndecorated(true);
        // Td: construir el contenido, pack() y setLocationRelativeTo(padre)
    }
}
