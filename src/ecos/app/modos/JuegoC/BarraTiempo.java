package ecos.app.modos.JuegoC;

import java.awt.Color;
import java.awt.Dimension;
import java.awt.Font;
import java.awt.FontMetrics;
import java.awt.Graphics;
import java.awt.Graphics2D;
import java.awt.Image;
import java.awt.RenderingHints;
import java.io.File;
import java.util.Locale;
import javax.swing.ImageIcon;
import javax.swing.JPanel;
import javax.swing.Timer;

/**
 * Barra de turno y tiempo (SOLO VISUAL), de lado a lado:
 *
 *   [avatar]  Turno de Jugador 1  [======== barra verde ========]  10.0s
 *
 * Empieza llena y se va vaciando durante los segundos indicados. No hace nada
 * al terminar, a menos que se le asigne algo con setAlTerminar(...).
 *
 *   iniciar()            -> la llena y empieza a vaciarla
 *   detener()            -> la congela donde esté
 *   reiniciar()          -> la deja llena y quieta
 *   setNombreJugador(s)  -> cambia el "Turno de ..."
 *   setAvatar(ruta)      -> (opcional) imagen a la izquierda
 *   setAlTerminar(r)     -> (opcional) qué hacer cuando llegue a cero
 */
public class BarraTiempo extends JPanel {

    private static final int INTERVALO_MS = 30; // cada cuánto se redibuja
    private static final int SEGMENTOS = 50;    // cuántos "cuadritos" tiene la barra

    // Colores de la barra
    private static final Color VERDE = new Color(122, 189, 122);
    private static final Color NARANJA = new Color(255, 165, 0);
    private static final Color ROJO = Paleta.ROJO_CARTA;

    // Cuándo cambia de color la barra (en segundos restantes)
    private static final double SEGUNDOS_NARANJA = 5.0;
    private static final double SEGUNDOS_ROJO = 2.0;

    private final int duracionMs;
    private final Timer temporizador;
    private final Font fuenteNombre;
    private final Font fuenteTiempo;

    private long inicio;
    private double progreso = 1.0; // 1 = llena, 0 = vacía
    private String nombreJugador = "";
    private Image avatar = null;
    private Runnable alTerminar;

    public BarraTiempo(int duracionSegundos) {
        this.duracionMs = duracionSegundos * 1000;
        this.fuenteNombre = Utilidades.cargarFuentePixel(22f).deriveFont(Font.BOLD);
        this.fuenteTiempo = Utilidades.cargarFuentePixel(36f).deriveFont(Font.BOLD);

        setOpaque(false);
        setPreferredSize(new Dimension(800, 90));

        temporizador = new Timer(INTERVALO_MS, e -> actualizar());
    }

    // ---------- Control de la barra ----------

    /** La llena y empieza a vaciarla. */
    public void iniciar() {
        progreso = 1.0;
        inicio = System.currentTimeMillis();
        temporizador.start();
        repaint();
    }

    /** La congela donde esté. */
    public void detener() {
        temporizador.stop();
    }

    /** La deja llena y quieta. */
    public void reiniciar() {
        temporizador.stop();
        progreso = 1.0;
        repaint();
    }

    public void setNombreJugador(String nombre) {
        this.nombreJugador = nombre;
        repaint();
    }

    /** Opcional: imagen que aparece a la izquierda (si el archivo no existe, no se muestra). */
    public void setAvatar(String ruta) {
        if (new File(ruta).exists()) {
            this.avatar = new ImageIcon(ruta).getImage();
        } else {
            this.avatar = null;
        }
        repaint();
    }

    /** Opcional: algo que se ejecuta cuando la barra llega a cero. */
    public void setAlTerminar(Runnable accion) {
        this.alTerminar = accion;
    }

    private void actualizar() {
        long transcurrido = System.currentTimeMillis() - inicio;
        progreso = Math.max(0.0, 1.0 - transcurrido / (double) duracionMs);
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
        super.paintComponent(g);
        Graphics2D g2 = (Graphics2D) g.create();

        int w = getWidth();
        int h = getHeight();

        int borde = 6;   // marco guinda
        int linea = 3;   // línea blanca interior
        int sombra = 6;  // grosor extra del borde de abajo

        // Marco guinda (todo el fondo)
        g2.setColor(Paleta.GUINDA);
        g2.fillRect(0, 0, w, h);

        // Línea blanca
        g2.setColor(Color.WHITE);
        g2.fillRect(borde, borde, w - borde * 2, h - borde * 2 - sombra);

        // Fondo crema
        int pad = borde + linea;
        g2.setColor(Paleta.CREMA);
        g2.fillRect(pad, pad, w - pad * 2, h - pad * 2 - sombra);

        // Zona de contenido
        int margenLateral = 16;
        int x0 = pad + margenLateral;
        int x1 = w - pad - margenLateral;
        int yArriba = pad;
        int yAbajo = h - sombra - pad;
        int alto = yAbajo - yArriba;
        int cy = (yArriba + yAbajo) / 2;

        // Avatar (opcional)
        if (avatar != null) {
            int lado = alto - 16;
            g2.setRenderingHint(RenderingHints.KEY_INTERPOLATION,
                    RenderingHints.VALUE_INTERPOLATION_NEAREST_NEIGHBOR);
            g2.drawImage(avatar, x0, cy - lado / 2, lado, lado, this);
            x0 += lado + 16;
        }

        // Texto "Turno de ..."
        String textoNombre = nombreJugador;
        g2.setFont(fuenteNombre);
        g2.setColor(Paleta.GUINDA);
        FontMetrics fmNombre = g2.getFontMetrics();
        g2.drawString(textoNombre, x0, cy - fmNombre.getHeight() / 2 + fmNombre.getAscent());
        x0 += fmNombre.stringWidth(textoNombre) + 24;

        // Tiempo restante, a la derecha (ej. "10.0s")
        double segundosRestantes = progreso * duracionMs / 1000.0;
        String textoTiempo = String.format(Locale.US, "%.1fs", segundosRestantes);
        g2.setFont(fuenteTiempo);
        FontMetrics fmTiempo = g2.getFontMetrics();
        int anchoReservado = fmTiempo.stringWidth(String.format(Locale.US, "%.1fs", duracionMs / 1000.0));
        g2.drawString(textoTiempo,
                x1 - fmTiempo.stringWidth(textoTiempo),
                cy - fmTiempo.getHeight() / 2 + fmTiempo.getAscent());
        int finBarra = x1 - anchoReservado - 24;

        // Barra por segmentos
        int barraAncho = finBarra - x0;
        if (barraAncho > 20) {
            int barraAlto = Math.min(alto - 24, 52);
            int barraY = cy - barraAlto / 2;

            // Marco guinda de la barra
            g2.setColor(Paleta.GUINDA);
            g2.fillRect(x0, barraY, barraAncho, barraAlto);

            // Interior (parte ya gastada)
            int ix = x0 + 4;
            int iy = barraY + 4;
            int iw = barraAncho - 8;
            int ih = barraAlto - 8;
            g2.setColor(Paleta.BEIGE);
            g2.fillRect(ix, iy, iw, ih);

            // Color: verde; naranja a los 5 s; rojo a los 2 s
            Color base;
            if (segundosRestantes <= SEGUNDOS_ROJO) {
                base = ROJO;
            } else if (segundosRestantes <= SEGUNDOS_NARANJA) {
                base = NARANJA;
            } else {
                base = VERDE;
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