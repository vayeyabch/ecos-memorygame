package ecos.componentes;

// Librería gráfica AWT, Color para los colores y Graphics para dibujar.
import java.awt.Color;
import java.awt.Graphics;
// Utilidades de Java, ArrayList para listas que crecen, List para las listas y Random para los números al azar.
import java.util.ArrayList;
import java.util.List;
import java.util.Random;
// Librería Swing, JComponent para la base de los componentes, JLayeredPane para las capas y Timer para los relojes.
import javax.swing.JComponent;
import javax.swing.JLayeredPane;
import javax.swing.Timer;

// Animación de confeti para cuando alguien gana, pedacitos de colores que salen de los lados y caen.
public class Confeti extends JComponent {

    // Colores del confeti, los mismos de la paleta del juego.
    private static final Color[] COLORES = {new Color(0xfca5ab), new Color(0xc23f52), new Color(0xffcc80), new Color(0xff7a1f), new Color(0x0047b5), new Color(0xf7f7f7)};
    // Cada "pixel" del confeti mide 4 px reales.
    private static final int ESCALA = 4;

    // Cada pedacito, posición, velocidad, tamaño, color y una fase para que se muevan distinto.
    private static class Particula {
        double x, y, vx, vy, fase;
        int tam;
        Color color;
    }

    // Lista de todos los pedacitos y el momento en que arrancó la animación.
    private final List<Particula> particulas = new ArrayList<>();
    private final long inicio = System.currentTimeMillis();

    // Lanza confeti, n pedacitos durante duracion ms. Sale de los lados y cae desde arriba.
    public static void lanzar(java.awt.Component origen, int cantidad, int duracionMs) {
        Confeti c = new Confeti(cantidad, duracionMs, origen);
        Capas.agregar(origen, c, JLayeredPane.DRAG_LAYER);
    }

    // Crea los pedacitos, unos salen por la izquierda, otros por la derecha y otros caen desde arriba.
    private Confeti(int cantidad, int duracion, java.awt.Component origen) {
        setOpaque(false);
        // Se mide el tamaño de la ventana para saber dónde nacen.
        java.awt.Container raiz = javax.swing.SwingUtilities.getRootPane(origen);
        int w = Math.max(1, raiz.getWidth() / ESCALA);
        int h = Math.max(1, raiz.getHeight() / ESCALA);
        Random r = new Random();
        // A cada pedacito se le da posición, velocidad, tamaño y color al azar; un tercio sale por cada lado y el resto cae desde arriba.
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
        // Cada 16 ms se mueven los pedacitos con una caída suave; cuando se acaba el tiempo se quita el confeti.
        // Cada 16 ms los pedacitos caen y se mueven con un vaivén; al terminar el tiempo se quita el confeti.
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

    // Devuelve false para que el confeti no tape los clics.
    @Override
    public boolean contains(int x, int y) {
        // No tapa los clics.
        return false;
    }

    // Dibuja cada pedacito como un cuadrado grande para que parezca pixel art.
    @Override
    protected void paintComponent(Graphics g) {
        for (Particula p : particulas) {
            g.setColor(p.color);
            g.fillRect((int) Math.round(p.x) * ESCALA, (int) Math.round(p.y) * ESCALA, p.tam * ESCALA, p.tam * ESCALA);
        }
    }
}