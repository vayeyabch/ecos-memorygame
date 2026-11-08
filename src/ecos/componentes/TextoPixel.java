package ecos.componentes;

// Librería gráfica AWT, Color para los colores, Dimension para los tamaños, Font para las fuentes, FontMetrics para
// Librería gráfica AWT, Color para los colores, Dimension para los tamaños, Font para las fuentes, FontMetrics para
// medir el texto y Graphics para dibujar.
import java.awt.Color;
import java.awt.Dimension;
import java.awt.Font;
import java.awt.FontMetrics;
import java.awt.Graphics;
// Librería Swing, JComponent para la base de los componentes.
import javax.swing.JComponent;

// Texto con sombra y opacidad que se puede cambiar (se usa para que START parpadee).
public class TextoPixel extends JComponent {

    // Texto que se dibuja, color de las letras, color de su sombra dura y cuántos píxeles se corre la sombra (hacia abajo y
    // a la derecha).
    private String texto;
    private Color colorTexto;
    private Color colorSombra;
    private int sombra;
    // 1 = se ve completo; 0 = invisible.
    private float opacidad = 1f;

    // Recibe todo lo necesario para dibujar.
    public TextoPixel(String texto, Font fuente, Color colorTexto, Color colorSombra, int sombra) {
        // Guarda el texto. Guarda el color de las letras. Guarda el color de la sombra. Guarda el tamaño de la sombra.
        this.texto = texto;
        this.colorTexto = colorTexto;
        this.colorSombra = colorSombra;
        this.sombra = sombra;
        // Pone la fuente. Es transparente, solo se ven las letras.
        setFont(fuente);
        setOpaque(false);
    }

    // Cambia el texto.
    public void setTexto(String texto) {
        // Avisa que el tamaño pudo cambiar.
        this.texto = texto;
        revalidate();
        repaint();
    }

    // Cambia los colores.
    public void setColores(Color colorTexto, Color colorSombra) {
        // Guarda el color de las letras. Guarda el color de la sombra.
        this.colorTexto = colorTexto;
        this.colorSombra = colorSombra;
        repaint();
    }

    // Cambia qué tan visible es el texto (para el parpadeo).
    public void setOpacidad(float opacidad) {
        this.opacidad = opacidad;
        repaint();
    }

    // Tamaño que necesita el componente.
    @Override
    public Dimension getPreferredSize() {
        // Medidas de la fuente. Ancho del texto y alto de una línea, más la sombra.
        FontMetrics fm = getFontMetrics(getFont());
        return new Dimension(fm.stringWidth(texto) + sombra, fm.getHeight() + sombra);
    }

    // Swing lo llama cada vez que hay que dibujar.
    @Override
    protected void paintComponent(Graphics g) {
        // Copia del pincel para no afectar al original. Aplica la opacidad. Usa la fuente del componente. Medidas de la fuente.
        java.awt.Graphics2D g2 = (java.awt.Graphics2D) g.create();
        g2.setComposite(java.awt.AlphaComposite.getInstance(java.awt.AlphaComposite.SRC_OVER, opacidad));
        g2.setFont(getFont());
        FontMetrics fm = g2.getFontMetrics();
        // Centra el texto en horizontal y vertical, elige el color de la sombra y dibuja la sombra corrida.
        int x = (getWidth() - sombra - fm.stringWidth(texto)) / 2;
        int y = (getHeight() - sombra - fm.getHeight()) / 2 + fm.getAscent();
        g2.setColor(colorSombra);
        g2.drawString(texto, x + sombra, y + sombra);
        // Elige el color de las letras. Dibuja las letras encima. Libera el pincel.
        g2.setColor(colorTexto);
        g2.drawString(texto, x, y);
        g2.dispose();
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
