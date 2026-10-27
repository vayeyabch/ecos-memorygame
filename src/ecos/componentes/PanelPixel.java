package ecos.componentes;

import ecos.app.Tema;
import java.awt.Color;
import java.awt.FlowLayout;
import java.awt.Graphics;
import java.awt.Graphics2D;
import java.awt.LayoutManager;
import javax.swing.BorderFactory;
import javax.swing.JPanel;

/**
 * Panel con borde duro y sombra solida (estilo pixel art).
 * Es un JPanel normal: se le ponen layout y componentes como siempre.
 */
public class PanelPixel extends JPanel {

    private static final int BORDE = 4;
    private static final int SOMBRA = 6;

    private Color colorFondo = null; // null = usar el color del tema

    public PanelPixel() {
        this(new FlowLayout());
    }

    public PanelPixel(LayoutManager layout) {
        super(layout);
        setOpaque(false);
        setPadding(14);
        Tema.alCambiar(this::repaint);
    }

    /** Espacio entre el borde y lo que va adentro. */
    public void setPadding(int px) {
        int p = px + BORDE;
        setBorder(BorderFactory.createEmptyBorder(p, p, p + SOMBRA, p + SOMBRA));
    }

    /** Cambia el color de fondo del panel. Con null vuelve al color del tema. */
    public void setColorFondo(Color color) {
        this.colorFondo = color;
        repaint();
    }

    @Override
    protected void paintComponent(Graphics g) {
        Graphics2D g2 = (Graphics2D) g.create();
        int w = getWidth() - SOMBRA;
        int h = getHeight() - SOMBRA;
        g2.setColor(new Color(0, 0, 0, 70));          // sombra
        g2.fillRect(SOMBRA, SOMBRA, w, h);
        g2.setColor(Tema.borde());                    // borde
        g2.fillRect(0, 0, w, h);
        g2.setColor(colorFondo != null ? colorFondo : Tema.panel()); // relleno
        g2.fillRect(BORDE, BORDE, w - BORDE * 2, h - BORDE * 2);
        g2.dispose();
        super.paintComponent(g);
    }
}
