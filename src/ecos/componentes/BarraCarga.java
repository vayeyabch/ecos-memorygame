package ecos.componentes;

import java.awt.Color; // Clase de Java para representar un color.
import java.awt.Dimension; // Representa un tamaño (ancho y alto).
import java.awt.Graphics; // Herramienta básica para dibujar.
import javax.swing.JPanel; // Panel de Swing: la clase de la que hereda esta.

public class BarraCarga extends JPanel {

    public static final int SEGMENTOS = 24; // Cuántos cuadritos tiene la barra.

    private static final int BORDE = 4; // Grosor del borde oscuro.
    private static final int CONTORNO = 3; // Grosor del contorno rosado de afuera.
    private static final int MARGEN = 4; // Espacio entre el borde y los cuadritos.
    private static final int SEPARACION = 3; // Espacio entre un cuadrito y el siguiente.
    private static final int BRILLO = 4; // Alto de la franja clara de cada cuadrito encendido.
    private static final Color CONTORNO_COLOR = new Color(0xFCC9CD); // Rosado del contorno.
    private static final Color BORDE_COLOR = new Color(0x0B0710); // Casi negro del borde.
    private static final Color FONDO = new Color(0x1C1327); // Violeta oscuro del fondo.
    private static final Color APAGADO = new Color(0x3D2D4F); // Violeta de los cuadritos apagados.
    private static final Color NARANJA = new Color(0xFF7A1F); // Naranja de los cuadritos encendidos.
    private static final Color ROSA = new Color(0xFCA5AB); // Rosa de uno de cada tres cuadritos encendidos.

    private int encendidos = 0; // Cuántos cuadritos están encendidos ahora.

    public BarraCarga() { // Constructor: no recibe nada.
        setOpaque(false); // Es transparente: se ve el fondo de la pantalla.
        setPreferredSize(new Dimension(520, 40)); // Tamaño de la barra (incluye el contorno rosado).
    }

    public void setEncendidos(int n) { // Cambia cuántos cuadritos están encendidos.
        encendidos = Math.max(0, Math.min(SEGMENTOS, n)); // Lo deja entre 0 y el total.
        repaint(); // Repinta.
    }

    @Override // Indica que se reemplaza el método de dibujo de JPanel.
    protected void paintComponent(Graphics g) { // Swing lo llama cada vez que hay que dibujar.
        int w = getWidth() - CONTORNO * 2; // Ancho sin el contorno.
        int h = getHeight() - CONTORNO * 2; // Alto sin el contorno.
        g.setColor(CONTORNO_COLOR); // Color del contorno rosado.
        g.fillRect(0, 0, getWidth(), getHeight()); // Dibuja el contorno rosado.
        g.setColor(BORDE_COLOR); // Color del borde oscuro.
        g.fillRect(CONTORNO - 1, CONTORNO - 1, w + 2, h + 2); // Dibuja el borde oscuro dejando 1 píxel de separación con el contorno.
        g.setColor(BORDE_COLOR); // Color del borde.
        g.fillRect(CONTORNO, CONTORNO, w, h); // Dibuja el cuerpo de la barra.
        g.setColor(FONDO); // Color del fondo.
        g.fillRect(CONTORNO + BORDE, CONTORNO + BORDE, w - BORDE * 2, h - BORDE * 2); // Dibuja el fondo dejando visible el borde.

        int hueco = CONTORNO + BORDE + MARGEN; // Dónde empiezan los cuadritos.
        int anchoUtil = getWidth() - 2 * hueco; // Ancho disponible para los cuadritos.
        int alto = getHeight() - 2 * hueco; // Alto de los cuadritos.
        int ancho = (anchoUtil - (SEGMENTOS - 1) * SEPARACION) / SEGMENTOS; // Ancho de cada cuadrito.
        int usado = SEGMENTOS * ancho + (SEGMENTOS - 1) * SEPARACION; // Ancho que ocupan todos juntos.
        int x0 = hueco + (anchoUtil - usado) / 2; // Posición del primero (para centrarlos).

        for (int i = 0; i < SEGMENTOS; i++) { // Recorre los cuadritos.
            int x = x0 + i * (ancho + SEPARACION); // Posición horizontal de este cuadrito.
            if (i < encendidos) { // Si está encendido.
                g.setColor((i + 1) % 3 == 0 ? ROSA : NARANJA); // Uno de cada tres es rosa; los demás naranja.
            } else { // Si está apagado.
                g.setColor(APAGADO); // Violeta apagado.
            }
            g.fillRect(x, hueco, ancho, alto); // Dibuja el cuadrito.
            if (i < encendidos) { // Si está encendido.
                g.setColor(new Color(255, 255, 255, 90)); // Blanco semitransparente.
                g.fillRect(x, hueco, ancho, BRILLO); // Dibuja la franja clara de arriba.
            }
        }
    }

    @Override // Indica que se reemplaza un método de JComponent.
    public Dimension getMinimumSize() { // Tamaño mínimo del componente.
        return getPreferredSize(); // Nunca se achica más de lo que necesita.
    }

    @Override // Indica que se reemplaza un método de JComponent.
    public Dimension getMaximumSize() { // Tamaño máximo del componente.
        return getPreferredSize(); // Nunca se estira más de lo que necesita.
    }
}
