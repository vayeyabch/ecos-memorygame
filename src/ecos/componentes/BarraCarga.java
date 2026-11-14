package ecos.componentes;

import java.awt.Color;
import java.awt.Dimension;
import java.awt.Graphics;
import javax.swing.JPanel;

// Barra de carga de la intro, hecha de cuadritos que se van encendiendo.
public class BarraCarga extends JPanel {

    public static final int SEGMENTOS = 24;

    // Grosor del borde oscuro y del contorno rosado, y espacios entre el borde y los cuadritos.
    private static final int BORDE = 4;
    private static final int CONTORNO = 3;
    private static final int MARGEN = 4;
    private static final int SEPARACION = 3;
    // Alto de la franja clara de cada cuadrito y colores del contorno, del borde y del fondo.
    private static final int BRILLO = 4;
    private static final Color CONTORNO_COLOR = new Color(0xFCC9CD);
    private static final Color BORDE_COLOR = new Color(0x0B0710);
    private static final Color FONDO = new Color(0x1C1327);
    // Colores de los cuadritos: violeta apagado, naranja encendido y rosa en uno de cada tres.
    private static final Color APAGADO = new Color(0x3D2D4F);
    private static final Color NARANJA = new Color(0xFF7A1F);
    private static final Color ROSA = new Color(0xFCA5AB);

    private int encendidos = 0;

    public BarraCarga() {
        setOpaque(false);
        setPreferredSize(new Dimension(520, 40));
    }

    public void setEncendidos(int n) {
        encendidos = Math.max(0, Math.min(SEGMENTOS, n));
        repaint();
    }

    @Override
    protected void paintComponent(Graphics g) {
        int w = getWidth() - CONTORNO * 2;
        int h = getHeight() - CONTORNO * 2;
        g.setColor(CONTORNO_COLOR);
        g.fillRect(0, 0, getWidth(), getHeight());
        g.setColor(BORDE_COLOR);
        g.fillRect(CONTORNO - 1, CONTORNO - 1, w + 2, h + 2);
        g.setColor(BORDE_COLOR);
        g.fillRect(CONTORNO, CONTORNO, w, h);
        g.setColor(FONDO);
        g.fillRect(CONTORNO + BORDE, CONTORNO + BORDE, w - BORDE * 2, h - BORDE * 2);

        int hueco = CONTORNO + BORDE + MARGEN;
        int anchoUtil = getWidth() - 2 * hueco;
        int alto = getHeight() - 2 * hueco;
        int ancho = (anchoUtil - (SEGMENTOS - 1) * SEPARACION) / SEGMENTOS;
        int usado = SEGMENTOS * ancho + (SEGMENTOS - 1) * SEPARACION;
        int x0 = hueco + (anchoUtil - usado) / 2;

        for (int i = 0; i < SEGMENTOS; i++) {
            int x = x0 + i * (ancho + SEPARACION);
            if (i < encendidos) {
                g.setColor((i + 1) % 3 == 0 ? ROSA : NARANJA);
            } else {
                g.setColor(APAGADO);
            }
            g.fillRect(x, hueco, ancho, alto);
            if (i < encendidos) {
                g.setColor(new Color(255, 255, 255, 90));
                g.fillRect(x, hueco, ancho, BRILLO);
            }
        }
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
