package ecos.app;

import java.awt.Dimension;
import java.awt.event.ComponentAdapter;
import java.awt.event.ComponentEvent;
import javax.swing.JComponent;
import javax.swing.JFrame;
import javax.swing.JLayeredPane;

// Ventana principal: tiene la barra de arriba, el navegador con las pantallas y el botón de la esquina.
public class Ventana extends JFrame {

    private final Navegador navegador = new Navegador();

    public Ventana() {
        // Al cerrar la ventana se cierra el programa. Se centra, se abre maximizada y tiene un tamaño mínimo.
        super("ECOS Memory Game");
        setDefaultCloseOperation(EXIT_ON_CLOSE);
        setMinimumSize(new Dimension(1000, 640));
        setSize(1280, 760);
        setLocationRelativeTo(null);
        setExtendedState(MAXIMIZED_BOTH);
        setContentPane(navegador.getContenedor());
    }

    public Navegador getNavegador() {
        return navegador;
    }

    // Pone la barra superior flotando encima de todas las pantallas y la acomoda cuando cambia el tamaño de la ventana.
    public void setBarraSuperior(JComponent barra) {
        JLayeredPane capas = getLayeredPane();
        capas.add(barra, JLayeredPane.PALETTE_LAYER);
        acomodar(barra);
        capas.addComponentListener(new ComponentAdapter() {
            @Override
            public void componentResized(ComponentEvent e) {
                acomodar(barra);
            }
        });
    }

    private void acomodar(JComponent barra) {
        barra.setBounds(0, 0, getLayeredPane().getWidth(), barra.getPreferredSize().height);
    }

    // Pone un componente, como el botón "i", pegado a la esquina inferior derecha.
    public void setEsquinaInferior(JComponent boton) {
        JLayeredPane capas = getLayeredPane();
        capas.add(boton, JLayeredPane.PALETTE_LAYER);
        ubicarEsquina(boton);
        capas.addComponentListener(new ComponentAdapter() {
            @Override
            public void componentResized(ComponentEvent e) {
                ubicarEsquina(boton);
            }
        });
    }

    // Lo pega a la esquina inferior derecha con 16 px de margen.
    private void ubicarEsquina(JComponent boton) {
        Dimension d = boton.getPreferredSize();
        boton.setBounds(getLayeredPane().getWidth() - d.width - 16, getLayeredPane().getHeight() - d.height - 16, d.width, d.height);
    }

    // Muestra la pantalla indicada y abre la ventana.
    public void iniciar(String idPrimeraPantalla) {
        setVisible(true);
        navegador.ir(idPrimeraPantalla);
    }
}
