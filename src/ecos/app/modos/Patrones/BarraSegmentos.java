package ecos.app.modos.Patrones;

import ecos.app.modos.JuegoC.Paleta;
import java.awt.Color;
import java.awt.Dimension;
import java.awt.Graphics;
import java.awt.Graphics2D;
import java.util.function.DoubleConsumer;
import javax.swing.JPanel;
import javax.swing.Timer;

/**
 * Barra de tiempo por segmentos (SOLO VISUAL), mismo estilo que la del modo clásico.
 * Empieza llena y se va vaciando durante los segundos que se le pidan.
 *
 *   iniciar(seg)       -> la llena y empieza a vaciarla
 *   detener()          -> la congela donde esté
 *   reiniciar(seg)     -> la deja llena y quieta
 *   setAlTerminar(r)   -> qué hacer cuando llegue a cero
 *   setAlActualizar(c) -> recibe los segundos que quedan (para mostrar "12 s")
 *
 * Si cambiaColor es true, la barra pasa de su color a naranja cuando quedan
 * 5 s y a rojo cuando quedan 2 s.
 */
public class BarraSegmentos extends JPanel {

    private static final int INTERVALO_MS = 30; // cada cuánto se redibuja
    private static final int SEGMENTOS = 50;    // cuántos "cuadritos" tiene la barra

    private static final Color NARANJA = new Color(255, 165, 0);
    private static final Color ROJO = Paleta.ROJO_CARTA;
    private static final double SEGUNDOS_NARANJA = 5.0;
    private static final double SEGUNDOS_ROJO = 2.0;

    private final Color colorBase;
    private final boolean cambiaColor;
    private final Timer temporizador;

    private int duracionMs = 1000;
    private long inicio;
    private double progreso = 1.0; // 1 = llena, 0 = vacía
    private Runnable alTerminar;
    private DoubleConsumer alActualizar;

    public BarraSegmentos(Color colorBase, int alto, boolean cambiaColor) {
        this.colorBase = colorBase;
        this.cambiaColor = cambiaColor;

        setOpaque(false);
        setPreferredSize(new Dimension(400, alto));
        setMinimumSize(new Dimension(50, alto));
        setMaximumSize(new Dimension(Short.MAX_VALUE, alto));

        temporizador = new Timer(INTERVALO_MS, e -> actualizar());
    }

    // ---------- Control de la barra ----------

    /** La llena y empieza a vaciarla durante estos segundos. */
    public void iniciar(int segundos) {
        duracionMs = segundos * 1000;
        progreso = 1.0;
        inicio = System.currentTimeMillis();
        temporizador.start();
        avisarSegundos();
        repaint();
    }

    /** La congela donde esté. */
    public void detener() {
        temporizador.stop();
    }

    /** La deja llena y quieta (lista para durar estos segundos). */
    public void reiniciar(int segundos) {
        temporizador.stop();
        duracionMs = segundos * 1000;
        progreso = 1.0;
        avisarSegundos();
        repaint();
    }

    public void setAlTerminar(Runnable accion) {
        this.alTerminar = accion;
    }

    public void setAlActualizar(DoubleConsumer accion) {
        this.alActualizar = accion;
    }

    private void avisarSegundos() {
        if (alActualizar != null) {
            alActualizar.accept(progreso * duracionMs / 1000.0);
        }
    }

    private void actualizar() {
        long transcurrido = System.currentTimeMillis() - inicio;
        progreso = Math.max(0.0, 1.0 - transcurrido / (double) duracionMs);
        avisarSegundos();
        repaint();

        if (progreso <= 0.0) {
            temporizador.stop();
            if (alTerminar != null) {
                alTerminar.run();
            }
        }
    }

    // ---------- Dibujo (estilo pixel, mismos colores del juego) ----------
    @Override
    protected void paintComponent(Graphics g) {
        Graphics2D g2 = (Graphics2D) g.create();
        int w = getWidth();
        int h = getHeight();
        int borde = (h >= 24) ? 4 : 3;

        // Marco guinda
        g2.setColor(Paleta.GUINDA);
        g2.fillRect(0, 0, w, h);

        // Interior (parte ya gastada)
        int ix = borde;
        int iy = borde;
        int iw = w - borde * 2;
        int ih = h - borde * 2;
        g2.setColor(Paleta.BEIGE);
        g2.fillRect(ix, iy, iw, ih);

        // Color de la barra
        double restantes = progreso * duracionMs / 1000.0;
        Color base = colorBase;
        if (cambiaColor) {
            if (restantes <= SEGUNDOS_ROJO) {
                base = ROJO;
            } else if (restantes <= SEGUNDOS_NARANJA) {
                base = NARANJA;
            }
        }
        Color claro = mezclar(base, Color.WHITE, 0.25);
        Color oscuro = mezclar(base, Paleta.GUINDA, 0.35);

        int llenos = (int) Math.ceil(progreso * SEGMENTOS);
        for (int i = 0; i < llenos; i++) {
            int sx = ix + i * iw / SEGMENTOS;
            int ex = ix + (i + 1) * iw / SEGMENTOS;
            int ancho = ex - sx;

            g2.setColor(base);
            g2.fillRect(sx, iy, ancho - 1, ih);

            g2.setColor(claro);
            g2.fillRect(sx, iy, ancho - 1, ih / 3);

            g2.setColor(oscuro);
            g2.fillRect(ex - 1, iy, 1, ih);
        }

        g2.dispose();
    }

    /** Mezcla dos colores (t = 0 da el primero, t = 1 da el segundo). */
    private static Color mezclar(Color a, Color b, double t) {
        int r = (int) (a.getRed() + (b.getRed() - a.getRed()) * t);
        int g = (int) (a.getGreen() + (b.getGreen() - a.getGreen()) * t);
        int bl = (int) (a.getBlue() + (b.getBlue() - a.getBlue()) * t);
        return new Color(r, g, bl);
    }
}