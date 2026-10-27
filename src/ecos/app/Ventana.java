package ecos.app;

import java.awt.BorderLayout;
import java.awt.Dimension;
import javax.swing.JComponent;
import javax.swing.JFrame;

/**
 * Ventana principal del juego. Tiene la barra superior (opcional) arriba
 * y las pantallas en el centro, que cambia el Navegador.
 */
public class Ventana extends JFrame {

    private final Navegador navegador = new Navegador();

    public Ventana() {
        super("ECOS Memory Game");
        setDefaultCloseOperation(EXIT_ON_CLOSE);
        setMinimumSize(new Dimension(960, 600));
        setSize(1280, 760);
        setLocationRelativeTo(null);
        setLayout(new BorderLayout());
        add(navegador.getContenedor(), BorderLayout.CENTER);
    }

    public Navegador getNavegador() {
        return navegador;
    }

    /** Pone la barra superior (Atras, tema, musica) arriba de todas las pantallas. */
    public void setBarraSuperior(JComponent barra) {
        add(barra, BorderLayout.NORTH);
    }

    /** Muestra la pantalla indicada y abre la ventana. */
    public void iniciar(String idPrimeraPantalla) {
        setVisible(true);
        navegador.ir(idPrimeraPantalla);
    }
}
