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
    // Los cuadraditos de cada regla y cuáles están marcados con un check.
    private final JComponent[] cuadros;
    private final boolean[] marcadas;

    // Recibe el ancho del texto y las reglas.
    public ListaReglas(int ancho, String... reglas) {
        // Guarda el ancho. Es transparente. Apila las reglas en una columna. Crea el espacio para los textos.
        this.ancho = ancho;
        setOpaque(false);
        setLayout(new BoxLayout(this, BoxLayout.Y_AXIS));
        etiquetas = new Etiqueta[reglas.length];
        cuadros = new JComponent[reglas.length];
        marcadas = new boolean[reglas.length];
        // Recorre las reglas. Crea la etiqueta del texto. Le pone el texto. Fila, cuadradito + texto.
        for (int i = 0; i < reglas.length; i++) {
            etiquetas[i] = Etiqueta.texto("", 20f, Etiqueta.Rol.TEXTO);
            setRegla(i, reglas[i]);
            JPanel fila = new JPanel(new BorderLayout(12, 0));
            // La fila es transparente. Alineada a la izquierda. El cuadradito rosa de la izquierda.
            fila.setOpaque(false);
            fila.setAlignmentX(Component.LEFT_ALIGNMENT);
            final int posicion = i;
            JComponent cuadro = new JComponent() {
                // Dibuja el cuadradito.
                @Override
                protected void paintComponent(Graphics g) {
                    // Color de la sombra del cuadradito. Dibuja la sombra corrida. Dibuja el cuadradito (verde si está marcado).
                    g.setColor(Tema.borde());
                    g.fillRect(2, 9, 12, 12);
                    g.setColor(marcadas[posicion] ? Tema.acierto() : Tema.rosa());
                    g.fillRect(0, 7, 12, 12);
                    // Si está marcado, dibuja el check (una palomita hecha de cuadritos).
                    if (marcadas[posicion]) {
                        g.setColor(Tema.borde());
                        g.fillRect(2, 12, 2, 2);
                        g.fillRect(4, 14, 2, 2);
                        g.fillRect(6, 12, 2, 2);
                        g.fillRect(8, 10, 2, 2);
                    }
                }

                // Tamaño del cuadradito.
                @Override
                public Dimension getPreferredSize() {
                    // Un poco más ancho para dejar espacio al texto.
                    return new Dimension(16, 26);
                }
            };
            // Pone el cuadradito a la izquierda. Pone el texto en el centro. Agrega la fila a la lista. Deja un espacio entre reglas.
            cuadros[i] = cuadro;
            fila.add(cuadro, BorderLayout.WEST);
            fila.add(etiquetas[i], BorderLayout.CENTER);
            add(fila);
            add(Box.createVerticalStrut(8));
        }
    }

    // Pone o quita el check de una regla (por ejemplo, cuando ya se cumplió).
    // Recibe cuál regla y si queda marcada.
    public void setMarcada(int indice, boolean marcada) {
        marcadas[indice] = marcada;
        cuadros[indice].repaint();
    }

    // Cambia el texto de una regla (por ejemplo, para mostrar el tamaño del tablero elegido).
    // Recibe cuál regla y el texto nuevo.
    public void setRegla(int indice, String texto) {
        // Usa HTML con ancho fijo para que el texto se parta en varias líneas.
        etiquetas[indice].setText("<html><div style='width:" + ancho + "px'>" + ecos.recursos.Fuentes.conNumeros(texto) + "</div></html>");
    }
}