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

public class AvisoDeslizante extends JComponent {

    public enum Estilo { JUGADOR1, JUGADOR2, NIVEL } // naranja, carmesí, azul

    private final String texto; // lo que dice la franja
    private final Estilo estilo; // de qué color es
    private final Image icono; // dibujito opcional al lado del texto
    private final long inicio = System.currentTimeMillis(); // hora a la que empezó
    private final Timer reloj; // repinta la franja cada 30 ms para animarla
    private static final int DURACION = 1300; // ms que dura todo el recorrido

    // Muestra la franja deslizando por la pantalla y se quita sola.
    public static void mostrar(java.awt.Component origen, String texto, Estilo estilo, Image icono) {
        AvisoDeslizante b = new AvisoDeslizante(texto, estilo, icono, origen); // Se crea la franja y se pone encima de todo (capa POPUP)
        Capas.agregar(origen, b, JLayeredPane.POPUP_LAYER);
    }

    private AvisoDeslizante(String texto, Estilo estilo, Image icono, java.awt.Component origen) {
        this.texto = texto;
        this.estilo = estilo;
        this.icono = icono;
        setOpaque(false); // transparente: solo se ve la franja
        reloj = new Timer(30, e -> {
            if (System.currentTimeMillis() - inicio > DURACION) { // terminó la animación
                ((Timer) e.getSource()).stop(); // deja de repintar
                Capas.quitar(this); // se elimina de la pantalla
            } else {
                repaint(); // dibuja el siguiente cuadro de la animación
            }
        });
        reloj.start();
    }

    @Override
    public boolean contains(int x, int y) {
        return false; // no tapa los clics
    }

    private Color[] colores() {  // Dos tonos de color según el estilo (para el degradado).
        return switch (estilo) {
            case JUGADOR1 -> new Color[]{new Color(224, 100, 26), new Color(255, 122, 31)};
            case JUGADOR2 -> new Color[]{new Color(168, 47, 68), new Color(194, 63, 82)};
            default -> new Color[]{new Color(10, 58, 150), new Color(0, 71, 181)};
        };
    }

    @Override
    protected void paintComponent(Graphics g) {
        double t = Math.min(1, (System.currentTimeMillis() - inicio) / (double) DURACION);  // t va de 0 a 1 durante la animación
        double paso = Math.floor(t * 14) / 14.0; // se mueve por pasos
        double x; // posición, en anchos de pantalla
        if (paso < 0.25) { // primer cuarto: entra desde la izquierda
            x = -1.05 * (1 - paso / 0.25);
        } else if (paso < 0.75) {  // mitad del tiempo: se queda quieta en el centro
            x = 0;
        } else {
            x = 1.05 * ((paso - 0.75) / 0.25);  // último cuarto: sale hacia la derecha
        }
        Graphics2D g2 = (Graphics2D) g.create(); // copia de g para no dañar el original
        int w = getWidth(); 
        int h = 92; // altura de la franja
        int y = (int) (getHeight() * 0.36); // posición vertical (36% desde arriba)
        g2.translate((int) (x * w), 0); // mueve la franja según la animación
        Color[] c = colores(); // colores según el estilo
        g2.setColor(new Color(0, 0, 0, 64)); // sombra 
        g2.fillRect(0, y + 8, w, h); // sombra debajo de la franja
        g2.setPaint(new GradientPaint(0, 0, c[0], w / 2f, 0, c[1])); // degradado de izquierda a derecha
        g2.fillRect(0, y, w / 2, h); // mitad izquierda con degradado
        g2.setPaint(new GradientPaint(w / 2f, 0, c[1], w, 0, c[0])); // degradado de derecha a izquierda
        g2.fillRect(w / 2, y, w - w / 2, h); // mitad derecha con degradado
        g2.setColor(ecos.app.Tema.borde()); // borde
        g2.fillRect(0, y, w, 6); // borde arriba
        g2.fillRect(0, y + h - 6, w, 6); // borde abajo
        Font f = Fuentes.titulo(Math.max(14f, Math.min(26f, w / 40f))); // tamaño de fuente según el ancho de la pantalla
        g2.setFont(f); // dibuja el texto centrado, con sombra y opcionalmente un icono a la izquierda
        FontMetrics fm = g2.getFontMetrics(); // para medir el ancho del texto y centrarlo
        int ic = icono != null ? 44 : 0; // ancho del icono (si hay)
        int total = fm.stringWidth(texto) + (ic > 0 ? ic + 16 : 0);  
        int tx = (w - total) / 2; // para centrar el conjunto icono + texto
        if (icono != null) {
            g2.drawImage(icono, tx, y + (h - ic) / 2, ic, ic, null); // el icono ya llega de 44 px
            tx += ic + 16; 
        }
        int ty = y + h / 2 + fm.getAscent() / 2 - 2; 
        g2.setColor(new Color(0, 0, 0, 100)); // sombra del texto
        g2.drawString(texto, tx + 3, ty + 3);
        g2.setColor(Color.WHITE); // texto blanco encima
        g2.drawString(texto, tx, ty); // libera la copia de g
        g2.dispose();
    }
}
