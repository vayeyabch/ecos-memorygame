package ecos.componentes;

import ecos.app.Tema;
import ecos.recursos.Fuentes;
import ecos.recursos.Sonido;
import java.awt.Color;
import java.awt.Graphics;
import java.awt.Graphics2D;
import java.awt.event.KeyAdapter;
import java.awt.event.KeyEvent;
import javax.swing.BorderFactory;
import javax.swing.JTextField;

/**
 * Cuadro de texto estilo pixel art con texto de ayuda (placeholder)
 * y sonido al teclear.
 */
public class CampoTextoPixel extends JTextField {

    private static final int BORDE = 4;
    private final String ayuda;

    public CampoTextoPixel(String ayuda, int columnas) {
        super(columnas);
        this.ayuda = ayuda;
        setOpaque(false);
        setFont(Fuentes.texto(20f));
        setBorder(BorderFactory.createEmptyBorder(10, 14, 10, 14));
        addKeyListener(new KeyAdapter() {
            @Override
            public void keyTyped(KeyEvent e) {
                Sonido.efecto("teclear");
            }
        });
        Tema.alCambiar(this::actualizarColores);
        actualizarColores();
    }

    private void actualizarColores() {
        setForeground(Tema.texto());
        setCaretColor(Tema.texto());
        repaint();
    }

    @Override
    protected void paintComponent(Graphics g) {
        Graphics2D g2 = (Graphics2D) g.create();
        g2.setColor(hasFocus() ? Tema.boton() : Tema.borde());
        g2.fillRect(0, 0, getWidth(), getHeight());
        g2.setColor(Tema.esOscuro() ? Tema.caja() : Color.WHITE);
        g2.fillRect(BORDE, BORDE, getWidth() - BORDE * 2, getHeight() - BORDE * 2);
        if (getText().isEmpty() && ayuda != null) {
            g2.setFont(getFont());
            Color c = Tema.texto();
            g2.setColor(new Color(c.getRed(), c.getGreen(), c.getBlue(), 120));
            g2.drawString(ayuda, getInsets().left, (getHeight() + g2.getFontMetrics().getAscent()) / 2 - 2);
        }
        g2.dispose();
        super.paintComponent(g);
    }
}
