package ecos.app.modos.Patrones;

// Capas: pone un componente encima de toda la ventana y lo quita después.
import ecos.componentes.Capas;
// Fuentes: da la letra de los títulos para el mensaje.
import ecos.recursos.Fuentes;
// Color: guarda colores.
import java.awt.Color;
// Font: tipo de letra del mensaje.
import java.awt.Font;
// FontMetrics: mide el texto para centrarlo.
import java.awt.FontMetrics;
// GradientPaint: pinta con un degradado de dos colores.
import java.awt.GradientPaint;
// Graphics: el "pincel" para dibujar.
import java.awt.Graphics;
// Graphics2D: versión del pincel con más opciones (degradados, mover el origen).
import java.awt.Graphics2D;
// Image: ícono opcional que acompaña al texto.
import java.awt.Image;
// JComponent: clase base de la que hereda AvisoDeslizante para poder dibujarse sola.
import javax.swing.JComponent;
// JLayeredPane: da la capa POPUP_LAYER, una capa alta para que el aviso quede encima de todo.
import javax.swing.JLayeredPane;
// Timer (de Swing): reloj que repinta la franja cada 30 ms.
import javax.swing.Timer;

// Mensaje que se desliza sobre la pantalla, por ejemplo "NIVEL 2", con un ícono, y desaparece solo.
public class AvisoDeslizante extends JComponent {

    // Los tres estilos de color de la franja.
    public enum Estilo { JUGADOR1, JUGADOR2, NIVEL }

    // Texto de la franja, estilo, ícono opcional y hora de inicio.
    private final String texto;
    private final Estilo estilo;
    private final Image icono;
    private final long inicio = System.currentTimeMillis();
    // Repinta la franja cada 30 ms para animarla, y duración total del recorrido.
    private final Timer reloj;
    private static final int DURACION = 1300;

    // Lo único que usan las otras clases: crea el aviso y lo pone encima de la ventana.
    public static void mostrar(java.awt.Component origen, String texto, Estilo estilo, Image icono) {
        // Se pone encima de todo, en la capa POPUP.
        AvisoDeslizante b = new AvisoDeslizante(texto, estilo, icono, origen);
        Capas.agregar(origen, b, JLayeredPane.POPUP_LAYER);
    }

    // Constructor privado: solo se crea desde mostrar().
    private AvisoDeslizante(String texto, Estilo estilo, Image icono, java.awt.Component origen) {
        // El texto se traduce al idioma actual.
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

    // Le dice a Swing que este componente nunca recibe el mouse, para que no tape los clics de abajo.
    @Override
    public boolean contains(int x, int y) {
        // No tapa los clics.
        return false;
    }

    // Dos tonos de color según el estilo, para el degradado.
    private Color[] colores() {
        // switch con flecha: devuelve un arreglo de dos colores según el estilo.
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
        // paso: t redondeado hacia abajo a 14 escalones, para que el movimiento sea "a saltos" como pixel art.
        double paso = Math.floor(t * 14) / 14.0;
        // x: posición horizontal de la franja, medida en anchos de pantalla (-1 afuera a la izquierda, 0 centro, 1 afuera a la derecha).
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
        // Mueve todo lo que se dibuje a partir de aquí hacia los lados según x.
        g2.translate((int) (x * w), 0);
        Color[] c = colores();
        // Sombra de la franja, un poco más abajo.
        g2.setColor(new Color(0, 0, 0, 64));
        g2.fillRect(0, y + 8, w, h);
        // Degradado de izquierda a centro (de c[0] a c[1])...
        g2.setPaint(new GradientPaint(0, 0, c[0], w / 2f, 0, c[1]));
        g2.fillRect(0, y, w / 2, h);
        // Degradado de derecha a izquierda.
        g2.setPaint(new GradientPaint(w / 2f, 0, c[1], w, 0, c[0]));
        g2.fillRect(w / 2, y, w - w / 2, h);
        // Bordes oscuros arriba y abajo de la franja.
        g2.setColor(ecos.app.Tema.borde());
        g2.fillRect(0, y, w, 6);
        g2.fillRect(0, y + h - 6, w, 6);
        // El tamaño de la letra depende del ancho de la ventana, entre 14 y 26.
        Font f = Fuentes.titulo(Math.max(14f, Math.min(26f, w / 40f)));
        g2.setFont(f);
        FontMetrics fm = g2.getFontMetrics();
        // Si hay ícono, ocupa 44 px más 16 de separación.
        int ic = icono != null ? 44 : 0;
        int total = fm.stringWidth(texto) + (ic > 0 ? ic + 16 : 0);  
        // Centra el conjunto ícono y texto.
        int tx = (w - total) / 2;
        if (icono != null) {
            g2.drawImage(icono, tx, y + (h - ic) / 2, ic, ic, null);
            tx += ic + 16; 
        }
        int ty = y + h / 2 + fm.getAscent() / 2 - 2; 
        // Sombra del texto, corrida 3 px.
        g2.setColor(new Color(0, 0, 0, 100));
        g2.drawString(texto, tx + 3, ty + 3);
        // Texto blanco encima de la sombra.
        g2.setColor(Color.WHITE);
        g2.drawString(texto, tx, ty);
        g2.dispose();
    }
}