package ecos.app.modos.Patrones;

// Clases del propio proyecto que se usan aquí.
import ecos.componentes.Capas;
import ecos.recursos.Fuentes;
// Librería gráfica AWT, Color para los colores, Font para las fuentes, FontMetrics para medir el texto, GradientPaint
// para los degradados lineales, Graphics para dibujar, Graphics2D para dibujar con más opciones y Image para las
// imágenes.
import java.awt.Color;
import java.awt.Font;
import java.awt.FontMetrics;
import java.awt.GradientPaint;
import java.awt.Graphics;
import java.awt.Graphics2D;
import java.awt.Image;
// Librería Swing, JComponent para la base de los componentes, JLayeredPane para las capas y Timer para los relojes.
import javax.swing.JComponent;
import javax.swing.JLayeredPane;
import javax.swing.Timer;

// Mensaje que se desliza sobre la pantalla (por ejemplo "NIVEL 2") con un ícono y desaparece solo.
public class AvisoDeslizante extends JComponent {

    // Naranja, carmesí, azul.
    public enum Estilo { JUGADOR1, JUGADOR2, NIVEL }

    // Lo que dice la franja, su color, un dibujito opcional al lado del texto y la hora a la que empezó.
    private final String texto;
    private final Estilo estilo;
    private final Image icono;
    private final long inicio = System.currentTimeMillis();
    // Repinta la franja cada 30 ms para animarla. Ms que dura todo el recorrido.
    private final Timer reloj;
    private static final int DURACION = 1300;

    // Muestra la franja deslizando por la pantalla y se quita sola.
    public static void mostrar(java.awt.Component origen, String texto, Estilo estilo, Image icono) {
        // Se crea la franja y se pone encima de todo (capa POPUP).
        AvisoDeslizante b = new AvisoDeslizante(texto, estilo, icono, origen);
        Capas.agregar(origen, b, JLayeredPane.POPUP_LAYER);
    }

    private AvisoDeslizante(String texto, Estilo estilo, Image icono, java.awt.Component origen) {
        this.texto = texto;
        this.estilo = estilo;
        this.icono = icono;
        // Transparente, solo se ve la franja.
        setOpaque(false);
        reloj = new Timer(30, e -> {
            // Terminó la animación. Deja de repintar. Se elimina de la pantalla.
            if (System.currentTimeMillis() - inicio > DURACION) {
                ((Timer) e.getSource()).stop();
                Capas.quitar(this);
            } else {
                // Dibuja el siguiente cuadro de la animación.
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

    // Dos tonos de color según el estilo (para el degradado).
    private Color[] colores() {
        return switch (estilo) {
            case JUGADOR1 -> new Color[]{new Color(224, 100, 26), new Color(255, 122, 31)};
            case JUGADOR2 -> new Color[]{new Color(168, 47, 68), new Color(194, 63, 82)};
            default -> new Color[]{new Color(10, 58, 150), new Color(0, 71, 181)};
        };
    }

    @Override
    protected void paintComponent(Graphics g) {
        // T va de 0 a 1 durante la animación, que se mueve por pasos. La posición se mide en anchos de pantalla y en el
        // primer cuarto entra desde la izquierda.
        double t = Math.min(1, (System.currentTimeMillis() - inicio) / (double) DURACION);
        double paso = Math.floor(t * 14) / 14.0;
        double x;
        if (paso < 0.25) {
            x = -1.05 * (1 - paso / 0.25);
        // Mitad del tiempo, se queda quieta en el centro.
        } else if (paso < 0.75) {
            x = 0;
        } else {
            // Último cuarto, sale hacia la derecha.
            x = 1.05 * ((paso - 0.75) / 0.25);
        }
        // Copia de g para no dañar el original.
        Graphics2D g2 = (Graphics2D) g.create();
        int w = getWidth(); 
        // Define la altura de la franja y su posición vertical (36% desde arriba), la mueve según la animación y elige los
        // colores según el estilo.
        int h = 92;
        int y = (int) (getHeight() * 0.36);
        g2.translate((int) (x * w), 0);
        Color[] c = colores();
        // Sombra debajo de la franja. Degradado de izquierda a derecha. Mitad izquierda con degradado.
        g2.setColor(new Color(0, 0, 0, 64));
        g2.fillRect(0, y + 8, w, h);
        g2.setPaint(new GradientPaint(0, 0, c[0], w / 2f, 0, c[1]));
        g2.fillRect(0, y, w / 2, h);
        // Degradado de derecha a izquierda. Mitad derecha con degradado.
        g2.setPaint(new GradientPaint(w / 2f, 0, c[1], w, 0, c[0]));
        g2.fillRect(w / 2, y, w - w / 2, h);
        g2.setColor(ecos.app.Tema.borde());
        g2.fillRect(0, y, w, 6);
        // Tamaño de fuente según el ancho de la pantalla. Dibuja el texto centrado, con sombra y opcionalmente un icono a la
        // izquierda. Para medir el ancho del texto y centrarlo.
        g2.fillRect(0, y + h - 6, w, 6);
        Font f = Fuentes.titulo(Math.max(14f, Math.min(26f, w / 40f)));
        g2.setFont(f);
        FontMetrics fm = g2.getFontMetrics();
        // Ancho del icono (si hay).
        int ic = icono != null ? 44 : 0;
        int total = fm.stringWidth(texto) + (ic > 0 ? ic + 16 : 0);  
        // Para centrar el conjunto icono + texto.
        int tx = (w - total) / 2;
        if (icono != null) {
            // El icono ya llega de 44 px.
            g2.drawImage(icono, tx, y + (h - ic) / 2, ic, ic, null);
            tx += ic + 16; 
        }
        int ty = y + h / 2 + fm.getAscent() / 2 - 2; 
        // Sombra del texto.
        g2.setColor(new Color(0, 0, 0, 100));
        g2.drawString(texto, tx + 3, ty + 3);
        // Texto blanco encima. Libera la copia de g.
        g2.setColor(Color.WHITE);
        g2.drawString(texto, tx, ty);
        g2.dispose();
    }
}
