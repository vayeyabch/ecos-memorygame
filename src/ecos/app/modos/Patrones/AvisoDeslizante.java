package ecos.app.modos.Patrones;

import ecos.componentes.Capas;
import ecos.recursos.Fuentes;
import java.awt.Color;
import java.awt.Font;
import java.awt.FontMetrics;
import java.awt.GradientPaint;
import java.awt.Graphics;
import java.awt.Graphics2D;
import java.awt.Image;
import javax.swing.JComponent;
import javax.swing.JLayeredPane;
import javax.swing.Timer;

// Mensaje que se desliza sobre la pantalla, por ejemplo "NIVEL 2", con un ícono, y desaparece solo.
public class AvisoDeslizante extends JComponent {

    public enum Estilo { JUGADOR1, JUGADOR2, NIVEL }

    // Texto de la franja, estilo, ícono opcional y hora de inicio.
    private final String texto;
    private final Estilo estilo;
    private final Image icono;
    private final long inicio = System.currentTimeMillis();
    // Repinta la franja cada 30 ms para animarla, y duración total del recorrido.
    private final Timer reloj;
    private static final int DURACION = 1300;

    public static void mostrar(java.awt.Component origen, String texto, Estilo estilo, Image icono) {
        // Se pone encima de todo, en la capa POPUP.
        AvisoDeslizante b = new AvisoDeslizante(texto, estilo, icono, origen);
        Capas.agregar(origen, b, JLayeredPane.POPUP_LAYER);
    }

    private AvisoDeslizante(String texto, Estilo estilo, Image icono, java.awt.Component origen) {
        this.texto = ecos.app.Idioma.t(texto);
        this.estilo = estilo;
        this.icono = icono;
        setOpaque(false);
        reloj = new Timer(30, e -> {
            // Al terminar la animación se deja de repintar y se quita.
            if (System.currentTimeMillis() - inicio > DURACION) {
                ((Timer) e.getSource()).stop();
                Capas.quitar(this);
            } else {
                repaint();
            }
        });
        reloj.start();
    }

    @Override
    public boolean contains(int x, int y) {
        // No tapa los clics.
        return false;
    }

    // Dos tonos de color según el estilo, para el degradado.
    private Color[] colores() {
        return switch (estilo) {
            case JUGADOR1 -> new Color[]{new Color(224, 100, 26), new Color(255, 122, 31)};
            case JUGADOR2 -> new Color[]{new Color(168, 47, 68), new Color(194, 63, 82)};
            default -> new Color[]{new Color(10, 58, 150), new Color(0, 71, 181)};
        };
    }

    @Override
    protected void paintComponent(Graphics g) {
        // t va de 0 a 1 durante la animación. La franja entra por la izquierda en el primer cuarto, se queda quieta en
        // el centro hasta el 75% y sale por la derecha.
        double t = Math.min(1, (System.currentTimeMillis() - inicio) / (double) DURACION);
        double paso = Math.floor(t * 14) / 14.0;
        double x;
        if (paso < 0.25) {
            x = -1.05 * (1 - paso / 0.25);
        } else if (paso < 0.75) {
            x = 0;
        } else {
            x = 1.05 * ((paso - 0.75) / 0.25);
        }
        Graphics2D g2 = (Graphics2D) g.create();
        int w = getWidth(); 
        // La franja mide 92 px de alto y va al 36% desde arriba.
        int h = 92;
        int y = (int) (getHeight() * 0.36);
        g2.translate((int) (x * w), 0);
        Color[] c = colores();
        g2.setColor(new Color(0, 0, 0, 64));
        g2.fillRect(0, y + 8, w, h);
        g2.setPaint(new GradientPaint(0, 0, c[0], w / 2f, 0, c[1]));
        g2.fillRect(0, y, w / 2, h);
        // Degradado de derecha a izquierda.
        g2.setPaint(new GradientPaint(w / 2f, 0, c[1], w, 0, c[0]));
        g2.fillRect(w / 2, y, w - w / 2, h);
        g2.setColor(ecos.app.Tema.borde());
        g2.fillRect(0, y, w, 6);
        g2.fillRect(0, y + h - 6, w, 6);
        Font f = Fuentes.titulo(Math.max(14f, Math.min(26f, w / 40f)));
        g2.setFont(f);
        FontMetrics fm = g2.getFontMetrics();
        int ic = icono != null ? 44 : 0;
        int total = fm.stringWidth(texto) + (ic > 0 ? ic + 16 : 0);  
        // Centra el conjunto ícono y texto.
        int tx = (w - total) / 2;
        if (icono != null) {
            g2.drawImage(icono, tx, y + (h - ic) / 2, ic, ic, null);
            tx += ic + 16; 
        }
        int ty = y + h / 2 + fm.getAscent() / 2 - 2; 
        g2.setColor(new Color(0, 0, 0, 100));
        g2.drawString(texto, tx + 3, ty + 3);
        // Texto blanco encima de la sombra.
        g2.setColor(Color.WHITE);
        g2.drawString(texto, tx, ty);
        g2.dispose();
    }
}
