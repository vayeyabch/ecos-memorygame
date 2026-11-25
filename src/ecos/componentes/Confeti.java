package ecos.componentes;

// Color: guarda los colores del confeti.
import java.awt.Color;
// Graphics: el "pincel" con el que se dibuja cada pedacito en paintComponent.
import java.awt.Graphics;
// ArrayList: la lista donde se guardan todos los pedacitos.
import java.util.ArrayList;
// List: el tipo de la lista (se declara con la interfaz y se crea con ArrayList).
import java.util.List;
// Random: genera números al azar para posición, velocidad, tamaño y fase.
import java.util.Random;
// JComponent: clase base de la que hereda Confeti para poder dibujar y ponerse encima de la pantalla.
import javax.swing.JComponent;
// JLayeredPane: da la capa DRAG_LAYER, una capa alta para que el confeti quede encima de todo.
import javax.swing.JLayeredPane;
// Timer (de Swing): el reloj que mueve los pedacitos cada 16 ms sin congelar la ventana.
import javax.swing.Timer;

// Animación de confeti para cuando alguien gana: pedacitos de colores que salen de los lados y caen.
public class Confeti extends JComponent {

    // Colores del confeti, los de la paleta del juego.
    private static final Color[] COLORES = {new Color(0xfca5ab), new Color(0xc23f52), new Color(0xffcc80), new Color(0xff7a1f), new Color(0x0047b5), new Color(0xf7f7f7)};
    // Cada pixel del confeti mide 4 px reales. Las posiciones se calculan en "pixeles chicos" y al dibujar se multiplican por 4.
    private static final int ESCALA = 4;

    // Cada pedacito: posición (x, y), velocidad (vx, vy), tamaño, color y una fase para que se muevan distinto.
    private static class Particula {
        double x, y, vx, vy, fase;
        int tam;
        Color color;
    }

    // Lista de pedacitos y momento en que arrancó la animación (para saber cuánto tiempo ha pasado).
    private final List<Particula> particulas = new ArrayList<>();
    private final long inicio = System.currentTimeMillis();

    // Lanza "cantidad" pedacitos durante "duracionMs" milisegundos. Es lo único que usan las otras clases.
    // Crea el confeti y lo agrega encima de la ventana con Capas (clase del mismo paquete, por eso no se importa).
    public static void lanzar(java.awt.Component origen, int cantidad, int duracionMs) {
        Confeti c = new Confeti(cantidad, duracionMs, origen);
        Capas.agregar(origen, c, JLayeredPane.DRAG_LAYER);
    }

    // Constructor privado: solo se crea desde lanzar().
    private Confeti(int cantidad, int duracion, java.awt.Component origen) {
        // Transparente, para que se vea la pantalla de abajo.
        setOpaque(false);
        // Se mide la ventana para saber dónde nacen. Se divide entre ESCALA porque trabajamos en pixeles chicos.
        // Math.max(1, ...) evita que el ancho o alto sea 0.
        java.awt.Container raiz = javax.swing.SwingUtilities.getRootPane(origen);
        int w = Math.max(1, raiz.getWidth() / ESCALA);
        int h = Math.max(1, raiz.getHeight() / ESCALA);
        Random r = new Random();
        // Cada pedacito recibe posición, velocidad, tamaño y color al azar. Un tercio sale por cada lado y el resto cae
        // desde arriba.
        for (int i = 0; i < cantidad; i++) {
            Particula p = new Particula();
            // i % 3: resto de dividir entre 3. Si es 0 sale por la izquierda, si es 1 por la derecha, si es 2 cae desde arriba.
            boolean izq = i % 3 == 0;
            boolean der = i % 3 == 1;
            // Posición inicial: borde izquierdo (0), borde derecho (w) o un punto al azar arriba.
            p.x = izq ? 0 : der ? w : r.nextDouble() * w;
            // Los de los lados nacen al 70% de la altura; los de arriba nacen fuera de la pantalla (y negativa).
            p.y = izq || der ? h * 0.7 : -r.nextDouble() * h * 0.5;
            // Velocidad horizontal: los de la izquierda van a la derecha (+), los de la derecha a la izquierda (-),
            // los de arriba casi no se mueven de lado.
            p.vx = izq ? 20 + r.nextDouble() * 50 : der ? -20 - r.nextDouble() * 50 : (r.nextDouble() - 0.5) * 14;
            // Velocidad vertical: los de los lados salen disparados hacia arriba (negativo); los de arriba caen suave.
            p.vy = izq || der ? -(60 + r.nextDouble() * 70) : 10 + r.nextDouble() * 25;
            // Tamaño de 1 a 3 pixeles chicos.
            p.tam = 1 + r.nextInt(3);
            // Colores en orden, repitiendo la lista con el resto.
            p.color = COLORES[i % COLORES.length];
            // Fase al azar para que cada pedacito se mueva de lado en un momento distinto.
            p.fase = r.nextDouble() * 6;
            particulas.add(p);
        }
        // Cada 16 ms (unos 60 cuadros por segundo) los pedacitos caen con un vaivén; al terminar el tiempo se quita el confeti.
        Timer reloj = new Timer(16, e -> {
            // Segundos que han pasado desde que arrancó.
            double t = (System.currentTimeMillis() - inicio) / 1000.0;
            // Si ya pasó la duración: se detiene el reloj, se quita el confeti de la pantalla y se sale.
            if (t * 1000 > duracion) {
                ((Timer) e.getSource()).stop();
                Capas.quitar(this);
                return;
            }
            for (Particula p : particulas) {
                // Gravedad: la velocidad hacia abajo aumenta un poco en cada cuadro.
                p.vy += 70 * 0.016;
                // Se mueve de lado con su velocidad más un balanceo con seno, que da el vaivén.
                p.x += (p.vx + Math.sin(t * 3 + p.fase) * 8) * 0.016;
                // Se mueve hacia abajo según su velocidad vertical.
                p.y += p.vy * 0.016;
                // Roce con el aire: pierde un poquito de velocidad de lado.
                p.vx *= 0.995;
            }
            // Pide volver a dibujar con las posiciones nuevas.
            repaint();
        });
        reloj.start();
    }

    // Le dice a Swing que este componente nunca recibe el mouse, para que no tape los botones de abajo.
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
            // Posición y tamaño se multiplican por ESCALA (4) para pasar de pixeles chicos a pixeles reales.
            g.fillRect((int) Math.round(p.x) * ESCALA, (int) Math.round(p.y) * ESCALA, p.tam * ESCALA, p.tam * ESCALA);
        }
    }
}