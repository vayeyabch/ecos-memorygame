package ecos.componentes;

// Librería gráfica AWT, Color para los colores, Dimension para los tamaños y Graphics para dibujar.
import java.awt.Color;
import java.awt.Dimension;
import java.awt.Graphics;
// Librería Swing, JPanel para los paneles.
import javax.swing.JPanel;

// Barra de carga de la intro, hecha de cuadritos que se van encendiendo.
public class BarraCarga extends JPanel {

    // Cuántos cuadritos tiene la barra.
    public static final int SEGMENTOS = 24;

    // Grosor del borde oscuro y del contorno rosado de afuera, y espacio entre el borde y los cuadritos y entre un cuadrito
    // y el siguiente.
    private static final int BORDE = 4;
    private static final int CONTORNO = 3;
    private static final int MARGEN = 4;
    private static final int SEPARACION = 3;
    // Alto de la franja clara de cada cuadrito encendido y colores del contorno rosado, del borde casi negro y del fondo
    // violeta oscuro.
    private static final int BRILLO = 4;
    private static final Color CONTORNO_COLOR = new Color(0xFCC9CD);
    private static final Color BORDE_COLOR = new Color(0x0B0710);
    private static final Color FONDO = new Color(0x1C1327);
    // Colores de los cuadritos, violeta si están apagados, naranja si están encendidos y rosa en uno de cada tres
    // encendidos.
    private static final Color APAGADO = new Color(0x3D2D4F);
    private static final Color NARANJA = new Color(0xFF7A1F);
    private static final Color ROSA = new Color(0xFCA5AB);

    // Cuántos cuadritos están encendidos ahora.
    private int encendidos = 0;

    // Constructor sin parámetros.
    public BarraCarga() {
        // Es transparente, se ve el fondo de la pantalla. Tamaño de la barra (incluye el contorno rosado).
        setOpaque(false);
        setPreferredSize(new Dimension(520, 40));
    }

    // Cambia cuántos cuadritos están encendidos.
    public void setEncendidos(int n) {
        // Lo deja entre 0 y el total.
        encendidos = Math.max(0, Math.min(SEGMENTOS, n));
        repaint();
    }

    // Swing lo llama cada vez que hay que dibujar.
    @Override
    protected void paintComponent(Graphics g) {
        // Calcula el ancho y el alto sin el contorno y dibuja el contorno rosado.
        int w = getWidth() - CONTORNO * 2;
        int h = getHeight() - CONTORNO * 2;
        g.setColor(CONTORNO_COLOR);
        g.fillRect(0, 0, getWidth(), getHeight());
        // Color del borde oscuro. Dibuja el borde oscuro dejando 1 píxel de separación con el contorno. Color del borde.
        // Dibuja el cuerpo de la barra.
        g.setColor(BORDE_COLOR);
        g.fillRect(CONTORNO - 1, CONTORNO - 1, w + 2, h + 2);
        g.setColor(BORDE_COLOR);
        g.fillRect(CONTORNO, CONTORNO, w, h);
        // Color del fondo. Dibuja el fondo dejando visible el borde.
        g.setColor(FONDO);
        g.fillRect(CONTORNO + BORDE, CONTORNO + BORDE, w - BORDE * 2, h - BORDE * 2);

        // Calcula dónde empiezan los cuadritos, el ancho y el alto disponibles y el ancho de cada cuadrito.
        int hueco = CONTORNO + BORDE + MARGEN;
        int anchoUtil = getWidth() - 2 * hueco;
        int alto = getHeight() - 2 * hueco;
        int ancho = (anchoUtil - (SEGMENTOS - 1) * SEPARACION) / SEGMENTOS;
        // Ancho que ocupan todos juntos. Posición del primero (para centrarlos).
        int usado = SEGMENTOS * ancho + (SEGMENTOS - 1) * SEPARACION;
        int x0 = hueco + (anchoUtil - usado) / 2;

        // Recorre los cuadritos. Posición horizontal de este cuadrito. Si está encendido. Uno de cada tres es rosa; los demás naranja.
        for (int i = 0; i < SEGMENTOS; i++) {
            int x = x0 + i * (ancho + SEPARACION);
            if (i < encendidos) {
                g.setColor((i + 1) % 3 == 0 ? ROSA : NARANJA);
            // Si está apagado. Violeta apagado.
            } else {
                g.setColor(APAGADO);
            }
            // Dibuja el cuadrito. Si está encendido. Blanco semitransparente. Dibuja la franja clara de arriba.
            g.fillRect(x, hueco, ancho, alto);
            if (i < encendidos) {
                g.setColor(new Color(255, 255, 255, 90));
                g.fillRect(x, hueco, ancho, BRILLO);
            }
        }
    }

    // Tamaño mínimo del componente.
    @Override
    public Dimension getMinimumSize() {
        // Nunca se achica más de lo que necesita.
        return getPreferredSize();
    }

    // Tamaño máximo del componente.
    @Override
    public Dimension getMaximumSize() {
        // Nunca se estira más de lo que necesita.
        return getPreferredSize();
    }
}
