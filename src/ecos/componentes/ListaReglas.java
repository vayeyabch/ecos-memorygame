package ecos.componentes;

// Clases del propio proyecto que se usan aquí.
import ecos.app.Tema;
// Librería gráfica AWT, BorderLayout para acomodar por zonas, Component para cualquier elemento de la pantalla,
// Dimension para los tamaños y Graphics para dibujar.
import java.awt.BorderLayout;
import java.awt.Component;
import java.awt.Dimension;
import java.awt.Graphics;
// Librería Swing, Box para los espacios entre componentes, BoxLayout para apilar en fila o columna, JComponent para la
// base de los componentes y JPanel para los paneles.
import javax.swing.Box;
import javax.swing.BoxLayout;
import javax.swing.JComponent;
import javax.swing.JPanel;

// Lista de reglas con una viñeta cuadrada por cada regla; el texto se parte en varias líneas si no cabe.
public class ListaReglas extends JPanel {

    // Los textos de las reglas. Ancho del texto, en píxeles (se usa para que parta las líneas).
    private final Etiqueta[] etiquetas;
    private final int ancho;

    // Recibe el ancho del texto y las reglas.
    public ListaReglas(int ancho, String... reglas) {
        // Guarda el ancho. Es transparente. Apila las reglas en una columna. Crea el espacio para los textos.
        this.ancho = ancho;
        setOpaque(false);
        setLayout(new BoxLayout(this, BoxLayout.Y_AXIS));
        etiquetas = new Etiqueta[reglas.length];
        // Recorre las reglas. Crea la etiqueta del texto. Le pone el texto. Fila, cuadradito + texto.
        for (int i = 0; i < reglas.length; i++) {
            etiquetas[i] = Etiqueta.texto("", 20f, Etiqueta.Rol.TEXTO);
            setRegla(i, reglas[i]);
            JPanel fila = new JPanel(new BorderLayout(12, 0));
            // La fila es transparente. Alineada a la izquierda. El cuadradito rosa de la izquierda.
            fila.setOpaque(false);
            fila.setAlignmentX(Component.LEFT_ALIGNMENT);
            JComponent cuadro = new JComponent() {
                // Dibuja el cuadradito.
                @Override
                protected void paintComponent(Graphics g) {
                    // Color de la sombra del cuadradito. Dibuja la sombra corrida. Dibuja el cuadradito.
                    g.setColor(Tema.borde());
                    g.fillRect(2, 9, 12, 12);
                    g.setColor(Tema.rosa());
                    g.fillRect(0, 7, 12, 12);
                }

                // Tamaño del cuadradito.
                @Override
                public Dimension getPreferredSize() {
                    // Un poco más ancho para dejar espacio al texto.
                    return new Dimension(16, 26);
                }
            };
            // Pone el cuadradito a la izquierda. Pone el texto en el centro. Agrega la fila a la lista. Deja un espacio entre reglas.
            fila.add(cuadro, BorderLayout.WEST);
            fila.add(etiquetas[i], BorderLayout.CENTER);
            add(fila);
            add(Box.createVerticalStrut(8));
        }
    }

    // Cambia el texto de una regla (por ejemplo, para mostrar el tamaño del tablero elegido).
    // Recibe cuál regla y el texto nuevo.
    public void setRegla(int indice, String texto) {
        // Usa HTML con ancho fijo para que el texto se parta en varias líneas.
        etiquetas[indice].setText("<html><div style='width:" + ancho + "px'>" + ecos.recursos.Fuentes.conNumeros(texto) + "</div></html>");
    }
}