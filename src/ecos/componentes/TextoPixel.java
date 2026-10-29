package ecos.componentes;

import java.awt.Color; // Clase de Java para representar un color.
import java.awt.Dimension; // Representa un tamaño (ancho y alto).
import java.awt.Font; // Tipo de letra.
import java.awt.FontMetrics; // Medidas de una fuente (ancho de un texto, alto de una línea).
import java.awt.Graphics; // Herramienta básica para dibujar.
import javax.swing.JComponent; // Componente básico de Swing: la clase de la que hereda esta.

public class TextoPixel extends JComponent {

    private String texto; // Texto que se dibuja.
    private Color colorTexto; // Color de las letras.
    private Color colorSombra; // Color de la sombra dura de las letras.
    private int sombra; // Cuántos píxeles se corre la sombra (hacia abajo y a la derecha).
    private float opacidad = 1f; // 1 = se ve completo; 0 = invisible.

    public TextoPixel(String texto, Font fuente, Color colorTexto, Color colorSombra, int sombra) { // Recibe todo lo necesario para dibujar.
        this.texto = texto; // Guarda el texto.
        this.colorTexto = colorTexto; // Guarda el color de las letras.
        this.colorSombra = colorSombra; // Guarda el color de la sombra.
        this.sombra = sombra; // Guarda el tamaño de la sombra.
        setFont(fuente); // Pone la fuente.
        setOpaque(false); // Es transparente: solo se ven las letras.
    }

    public void setTexto(String texto) { // Cambia el texto.
        this.texto = texto; // Lo guarda.
        revalidate(); // Avisa que el tamaño pudo cambiar.
        repaint(); // Repinta.
    }

    public void setColores(Color colorTexto, Color colorSombra) { // Cambia los colores.
        this.colorTexto = colorTexto; // Guarda el color de las letras.
        this.colorSombra = colorSombra; // Guarda el color de la sombra.
        repaint(); // Repinta.
    }

    public void setOpacidad(float opacidad) { // Cambia qué tan visible es el texto (para el parpadeo).
        this.opacidad = opacidad; // La guarda.
        repaint(); // Repinta.
    }

    @Override // Indica que se reemplaza un método de JComponent.
    public Dimension getPreferredSize() { // Tamaño que necesita el componente.
        FontMetrics fm = getFontMetrics(getFont()); // Medidas de la fuente.
        return new Dimension(fm.stringWidth(texto) + sombra, fm.getHeight() + sombra); // Ancho del texto y alto de una línea, más la sombra.
    }

    @Override // Indica que se reemplaza el método de dibujo de JComponent.
    protected void paintComponent(Graphics g) { // Swing lo llama cada vez que hay que dibujar.
        java.awt.Graphics2D g2 = (java.awt.Graphics2D) g.create(); // Copia del pincel para no afectar al original.
        g2.setComposite(java.awt.AlphaComposite.getInstance(java.awt.AlphaComposite.SRC_OVER, opacidad)); // Aplica la opacidad.
        g2.setFont(getFont()); // Usa la fuente del componente.
        FontMetrics fm = g2.getFontMetrics(); // Medidas de la fuente.
        int x = (getWidth() - sombra - fm.stringWidth(texto)) / 2; // Centra el texto horizontalmente.
        int y = (getHeight() - sombra - fm.getHeight()) / 2 + fm.getAscent(); // Centra el texto verticalmente.
        g2.setColor(colorSombra); // Elige el color de la sombra.
        g2.drawString(texto, x + sombra, y + sombra); // Dibuja la sombra corrida.
        g2.setColor(colorTexto); // Elige el color de las letras.
        g2.drawString(texto, x, y); // Dibuja las letras encima.
        g2.dispose(); // Libera el pincel.
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
