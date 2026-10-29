package ecos.componentes;

import ecos.app.Tema; // Para usar los colores del tema actual.
import ecos.recursos.Fuentes; // Para usar la fuente pixel del juego.
import java.awt.BorderLayout; // Layout que divide en norte, sur, este, oeste y centro.
import java.awt.Component; // Tipo general de cualquier componente.
import java.awt.Dimension; // Representa un tamaño (ancho y alto).
import java.awt.Graphics; // Herramienta básica para dibujar.
import javax.swing.Box; // Caja con espacios invisibles.
import javax.swing.BoxLayout; // Layout que apila componentes en una columna.
import javax.swing.JComponent; // Componente básico de Swing.
import javax.swing.JPanel; // Panel de Swing: la clase de la que hereda esta.

public class ListaReglas extends JPanel {

    private final Etiqueta[] etiquetas; // Los textos de las reglas.
    private final int ancho; // Ancho del texto, en píxeles (se usa para que parta las líneas).

    public ListaReglas(int ancho, String... reglas) { // Recibe el ancho del texto y las reglas.
        this.ancho = ancho; // Guarda el ancho.
        setOpaque(false); // Es transparente.
        setLayout(new BoxLayout(this, BoxLayout.Y_AXIS)); // Apila las reglas en una columna.
        etiquetas = new Etiqueta[reglas.length]; // Crea el espacio para los textos.
        for (int i = 0; i < reglas.length; i++) { // Recorre las reglas.
            etiquetas[i] = Etiqueta.texto("", 20f, Etiqueta.Rol.TEXTO); // Crea la etiqueta del texto.
            setRegla(i, reglas[i]); // Le pone el texto.
            JPanel fila = new JPanel(new BorderLayout(12, 0)); // Fila: cuadradito + texto.
            fila.setOpaque(false); // La fila es transparente.
            fila.setAlignmentX(Component.LEFT_ALIGNMENT); // Alineada a la izquierda.
            JComponent cuadro = new JComponent() { // El cuadradito rosa de la izquierda.
                @Override // Indica que se reemplaza el método de dibujo de JComponent.
                protected void paintComponent(Graphics g) { // Dibuja el cuadradito.
                    g.setColor(Tema.borde()); // Color de la sombra del cuadradito.
                    g.fillRect(2, 9, 12, 12); // Dibuja la sombra corrida.
                    g.setColor(Tema.rosa()); // Color rosa.
                    g.fillRect(0, 7, 12, 12); // Dibuja el cuadradito.
                }

                @Override // Indica que se reemplaza un método de JComponent.
                public Dimension getPreferredSize() { // Tamaño del cuadradito.
                    return new Dimension(16, 26); // Un poco más ancho para dejar espacio al texto.
                }
            };
            fila.add(cuadro, BorderLayout.WEST); // Pone el cuadradito a la izquierda.
            fila.add(etiquetas[i], BorderLayout.CENTER); // Pone el texto en el centro.
            add(fila); // Agrega la fila a la lista.
            add(Box.createVerticalStrut(8)); // Deja un espacio entre reglas.
        }
    }

    // Cambia el texto de una regla (por ejemplo, para mostrar el tamaño del tablero elegido).
    public void setRegla(int indice, String texto) { // Recibe cuál regla y el texto nuevo.
        etiquetas[indice].setText("<html><div style='width:" + ancho + "px'>" + texto + "</div></html>"); // Usa HTML con ancho fijo para que el texto se parta en varias líneas.
    }
}
