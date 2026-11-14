package ecos.componentes;

import ecos.app.Idioma;
import ecos.app.Tema;
import ecos.recursos.Fuentes;
import ecos.recursos.Sprites;
import java.awt.AlphaComposite;
import java.awt.Cursor;
import java.awt.Dimension;
import java.awt.FontMetrics;
import java.awt.Graphics;
import java.awt.Graphics2D;
import java.awt.event.MouseAdapter;
import java.awt.event.MouseEvent;
import javax.swing.JButton;

// Botón de un comodín, como el Ojo espía, con su precio en monedas. Se ve apagado si no alcanzan, pero se puede tocar
// para ver el aviso.
public class BotonPoder extends JButton {

    // Nombre del comodín, precio, si no alcanzan las monedas y si el mouse está encima.
    private final String texto;
    private final int precio;
    private boolean sinFondos = false;
    private boolean encima = false;
    private final Runnable alCambiarTema = this::repaint;
    private final Runnable alCambiarIdioma = this::repaint;

    public BotonPoder(String texto, int precio) {
        this.texto = texto;
        this.precio = precio;
        setContentAreaFilled(false);
        setBorderPainted(false);
        setFocusPainted(false);
        setOpaque(false);
        setCursor(Cursor.getPredefinedCursor(Cursor.HAND_CURSOR));
        addMouseListener(new MouseAdapter() {
            @Override
            public void mouseEntered(MouseEvent e) {
                encima = true;
                repaint();
            }

            @Override
            public void mouseExited(MouseEvent e) {
                encima = false;
                repaint();
            }
        });
    }

    public void setSinFondos(boolean sinFondos) {
        this.sinFondos = sinFondos;
        repaint();
    }

    @Override
    public void addNotify() {
        super.addNotify();
        Tema.alCambiar(alCambiarTema);
        Idioma.alCambiar(alCambiarIdioma);
    }

    @Override
    public void removeNotify() {
        Tema.quitar(alCambiarTema);
        Idioma.quitar(alCambiarIdioma);
        super.removeNotify();
    }

    @Override
    public Dimension getPreferredSize() {
        return new Dimension(250, 52);
    }

    @Override
    public Dimension getMinimumSize() {
        return getPreferredSize();
    }

    @Override
    public Dimension getMaximumSize() {
        return getPreferredSize();
    }

    @Override
    protected void paintComponent(Graphics g) {
        // Si el mouse está encima y el botón está activo, sube 2 píxeles.
        Graphics2D g2 = (Graphics2D) g.create();
        int w = getWidth();
        int h = getHeight() - 8;
        int dy = encima && isEnabled() && !sinFondos ? -2 : 0;
        // Sin monedas suficientes se dibuja medio transparente.
        if (!isEnabled() || sinFondos) {
            g2.setComposite(AlphaComposite.getInstance(AlphaComposite.SRC_OVER, 0.45f));
        }
        g2.translate(0, dy + 2);
        g2.setColor(Tema.sombra());
        g2.fillRect(0, 4, w, h);
        g2.setColor(Tema.borde());
        g2.fillRect(0, 0, w, h);
        g2.setColor(Tema.boton());
        g2.fillRect(3, 3, w - 6, h - 6);
        g2.drawImage(Sprites.ui("eye", 26), 9, (h - 26) / 2, null);
        g2.setFont(Fuentes.titulo(9f));
        FontMetrics fm = g2.getFontMetrics();
        g2.setColor(Tema.textoBoton());
        g2.drawString(Idioma.t(texto), 44, h / 2 + fm.getAscent() / 2 - 1);
        // Cajita del precio pegada a la derecha.
        int pw = 66;
        int px = w - pw - 8;
        g2.setColor(Tema.borde());
        g2.fillRect(px, 8, pw, h - 16);
        g2.setColor(Tema.panel());
        g2.fillRect(px + 2, 10, pw - 4, h - 20);
        g2.drawImage(Sprites.ui("coin", 14), px + 8, (h - 14) / 2, null);
        g2.setColor(Tema.texto());
        // Dibuja el precio al lado de la moneda.
        g2.drawString("" + precio, px + 28, h / 2 + fm.getAscent() / 2 - 1);
        g2.dispose();
    }
}
