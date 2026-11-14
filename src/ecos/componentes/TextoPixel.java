package ecos.componentes;

import java.awt.Color;
import java.awt.Dimension;
import java.awt.Font;
import java.awt.FontMetrics;
import java.awt.Graphics;
import javax.swing.JComponent;

// Texto con sombra y opacidad que se puede cambiar; se usa para que START parpadee.
public class TextoPixel extends JComponent {

    // Texto, color de las letras, color de la sombra y cuántos píxeles se corre la sombra.
    private String texto;
    private final Color colorTexto;
    private final Color colorSombra;
    private int sombra;
    // Opacidad: 1 se ve completo, 0 invisible.
    private float opacidad = 1f;

    public TextoPixel(String texto, Font fuente, Color colorTexto, Color colorSombra, int sombra) {
        this.texto = texto;
        this.colorTexto = colorTexto;
        this.colorSombra = colorSombra;
        this.sombra = sombra;
        setFont(fuente);
        setOpaque(false);
    }

    public void setTexto(String texto) {
        this.texto = texto;
        revalidate();
        repaint();
    }

    // Cambia qué tan visible es el texto, para el parpadeo.
    public void setOpacidad(float opacidad) {
        this.opacidad = opacidad;
        repaint();
    }

    @Override
    public Dimension getPreferredSize() {
        FontMetrics fm = getFontMetrics(getFont());
        return new Dimension(fm.stringWidth(texto) + sombra, fm.getHeight() + sombra);
    }

    @Override
    protected void paintComponent(Graphics g) {
        java.awt.Graphics2D g2 = (java.awt.Graphics2D) g.create();
        g2.setComposite(java.awt.AlphaComposite.getInstance(java.awt.AlphaComposite.SRC_OVER, opacidad));
        g2.setFont(getFont());
        FontMetrics fm = g2.getFontMetrics();
        // Centra el texto y dibuja primero la sombra corrida.
        int x = (getWidth() - sombra - fm.stringWidth(texto)) / 2;
        int y = (getHeight() - sombra - fm.getHeight()) / 2 + fm.getAscent();
        g2.setColor(colorSombra);
        g2.drawString(texto, x + sombra, y + sombra);
        g2.setColor(colorTexto);
        g2.drawString(texto, x, y);
        g2.dispose();
    }

    @Override
    public Dimension getMinimumSize() {
        return getPreferredSize();
    }

    @Override
    public Dimension getMaximumSize() {
        return getPreferredSize();
    }
}
