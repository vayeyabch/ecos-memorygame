package ecos.componentes;

import ecos.app.Tema;
import javax.swing.JPanel;
import java.awt.Color;
import java.awt.Dimension;
import java.awt.Graphics;

public class BarraCarga extends JPanel {

    public static final int SEGMENTOS = 24;

    private static final int BORDE = 4;
    private static final int MARGEN = 4;
    private static final int SEPARACION = 2;
    private static final int BRILLO = 4;

    private int encendidos = 0;

    public BarraCarga() {
        setPreferredSize(new Dimension(440, 34));
    }

    public void setEncendidos(int n) {
        encendidos = Math.max(0, Math.min(SEGMENTOS, n));
        repaint();
    }

    @Override
    protected void paintComponent(Graphics g) {
        super.paintComponent(g);

        g.setColor(Tema.caja());
        g.fillRect(0, 0, getWidth(), getHeight());
        g.setColor(Tema.borde());
        for (int i = 0; i < BORDE; i++) {
            g.drawRect(i, i, getWidth() - 1 - 2 * i, getHeight() - 1 - 2 * i);
        }

        int hueco = BORDE + MARGEN;
        int anchoUtil = getWidth() - 2 * hueco;
        int alto = getHeight() - 2 * hueco;
        int ancho = (anchoUtil - (SEGMENTOS - 1) * SEPARACION) / SEGMENTOS;
        int usado = SEGMENTOS * ancho + (SEGMENTOS - 1) * SEPARACION;
        int x0 = hueco + (anchoUtil - usado) / 2;

        for (int i = 0; i < SEGMENTOS; i++) {
            int x = x0 + i * (ancho + SEPARACION);
            if (i >= encendidos) {
                g.setColor(Tema.panel());
            } else {
                g.setColor((i + 1) % 3 == 0 ? Tema.dorado() : Tema.acento());
            }
            g.fillRect(x, hueco, ancho, alto);
            if (i < encendidos) {
                g.setColor(new Color(255, 255, 255, 70));
                g.fillRect(x, hueco, ancho, BRILLO);
            }
        }
    }
}