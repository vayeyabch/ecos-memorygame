package ecos.componentes;

import ecos.app.Tema;
import java.awt.Color;
import java.awt.FlowLayout;
import java.awt.Graphics;
import java.awt.Graphics2D;
import java.awt.LayoutManager;
import javax.swing.BorderFactory;
import javax.swing.JPanel;

// Caja con borde duro y sombra; es el papel de casi todas las ventanas y tarjetas.
public class PanelPixel extends JPanel {

    // Grosor del borde, de la línea clara de adentro y de la sombra dura de abajo.
    private static final int BORDE = 4;
    private static final int LINEA = 3;
    private static final int SOMBRA = 6;

    // Color de borde propio (null usa el del tema) y acción que repinta al cambiar el tema.
    private Color colorBorde = null;
    private final Runnable alCambiarTema = this::repaint;

    public PanelPixel() {
        this(new FlowLayout());
    }

    public PanelPixel(LayoutManager layout) {
        super(layout);
        setOpaque(false);
        setPadding(14);
    }

    @Override
    public void addNotify() {
        super.addNotify();
        Tema.alCambiar(alCambiarTema);
    }

    @Override
    public void removeNotify() {
        Tema.quitar(alCambiarTema);
        super.removeNotify();
    }

    // Espacio entre el borde y lo de adentro: suma el borde y la línea clara, y abajo la sombra.
    public void setPadding(int px) {
        int p = px + BORDE + LINEA;
        setBorder(BorderFactory.createEmptyBorder(p, p, p + SOMBRA, p));
    }

    // Cambia el color del borde; con null vuelve al del tema.
    public void setColorBorde(Color color) {
        this.colorBorde = color;
        repaint();
    }

    @Override
    protected void paintComponent(Graphics g) {
        Graphics2D g2 = (Graphics2D) g.create();
        int w = getWidth();
        int h = getHeight() - SOMBRA;
        g2.setColor(Tema.sombra());
        g2.fillRect(0, SOMBRA, w, h);
        g2.setColor(colorBorde != null ? colorBorde : Tema.borde());
        g2.fillRect(0, 0, w, h);
        g2.setColor(Tema.panelAlto());
        g2.fillRect(BORDE, BORDE, w - BORDE * 2, h - BORDE * 2);
        g2.setColor(Tema.panel());
        g2.fillRect(BORDE + LINEA, BORDE + LINEA, w - (BORDE + LINEA) * 2, h - (BORDE + LINEA) * 2);
        g2.dispose();
        super.paintComponent(g);
    }
}
