package ecos.componentes;

import java.awt.Color;
import java.awt.Graphics;
import java.util.ArrayList;
import java.util.List;
import java.util.Random;
import javax.swing.JComponent;
import javax.swing.JLayeredPane;
import javax.swing.Timer;

public class Confeti extends JComponent {

    private static final Color[] COLORES = {new Color(0xfca5ab), new Color(0xc23f52), new Color(0xffcc80), new Color(0xff7a1f), new Color(0x0047b5), new Color(0xf7f7f7)};
    private static final int ESCALA = 4; // cada "pixel" del confeti mide 4 px reales

    private static class Particula {
        double x, y, vx, vy, fase;
        int tam;
        Color color;
    }

    private final List<Particula> particulas = new ArrayList<>();
    private final long inicio = System.currentTimeMillis();

    // Lanza confeti: n pedacitos durante duracion ms. Sale de los lados y cae desde arriba.
    public static void lanzar(java.awt.Component origen, int cantidad, int duracionMs) {
        Confeti c = new Confeti(cantidad, duracionMs, origen);
        Capas.agregar(origen, c, JLayeredPane.DRAG_LAYER);
    }

    private Confeti(int cantidad, int duracion, java.awt.Component origen) {
        setOpaque(false);
        java.awt.Container raiz = javax.swing.SwingUtilities.getRootPane(origen);
        int w = Math.max(1, raiz.getWidth() / ESCALA);
        int h = Math.max(1, raiz.getHeight() / ESCALA);
        Random r = new Random();
        for (int i = 0; i < cantidad; i++) {
            Particula p = new Particula();
            boolean izq = i % 3 == 0;
            boolean der = i % 3 == 1;
            p.x = izq ? 0 : der ? w : r.nextDouble() * w;
            p.y = izq || der ? h * 0.7 : -r.nextDouble() * h * 0.5;
            p.vx = izq ? 20 + r.nextDouble() * 50 : der ? -20 - r.nextDouble() * 50 : (r.nextDouble() - 0.5) * 14;
            p.vy = izq || der ? -(60 + r.nextDouble() * 70) : 10 + r.nextDouble() * 25;
            p.tam = 1 + r.nextInt(3);
            p.color = COLORES[i % COLORES.length];
            p.fase = r.nextDouble() * 6;
            particulas.add(p);
        }
        Timer reloj = new Timer(16, e -> {
            double t = (System.currentTimeMillis() - inicio) / 1000.0;
            if (t * 1000 > duracion) {
                ((Timer) e.getSource()).stop();
                Capas.quitar(this);
                return;
            }
            for (Particula p : particulas) {
                p.vy += 70 * 0.016;
                p.x += (p.vx + Math.sin(t * 3 + p.fase) * 8) * 0.016;
                p.y += p.vy * 0.016;
                p.vx *= 0.995;
            }
            repaint();
        });
        reloj.start();
    }

    @Override
    public boolean contains(int x, int y) {
        return false; // no tapa los clics
    }

    @Override
    protected void paintComponent(Graphics g) {
        for (Particula p : particulas) {
            g.setColor(p.color);
            g.fillRect((int) Math.round(p.x) * ESCALA, (int) Math.round(p.y) * ESCALA, p.tam * ESCALA, p.tam * ESCALA);
        }
    }
}