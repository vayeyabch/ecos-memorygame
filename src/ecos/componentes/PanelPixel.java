package ecos.componentes;

import ecos.app.Tema; // Para usar los colores del tema actual.
import java.awt.Color; // Clase de Java para representar un color.
import java.awt.FlowLayout; // Layout que acomoda los componentes uno al lado del otro.
import java.awt.Graphics; // Herramienta básica para dibujar.
import java.awt.Graphics2D; // Versión más completa de Graphics para dibujar figuras.
import java.awt.LayoutManager; // Tipo general de cualquier layout.
import javax.swing.BorderFactory; // Fábrica para crear bordes de Swing.
import javax.swing.JPanel; // Panel de Swing: el contenedor del que hereda esta clase.

public class PanelPixel extends JPanel {

    private static final int BORDE = 4; // Grosor del borde, en píxeles.
    private static final int SOMBRA = 6; // Tamaño de la sombra, en píxeles.

    private Color colorFondo = null; // Color de fondo propio; null significa que usa el color del tema.

    public PanelPixel() { // Constructor sin parámetros.
        this(new FlowLayout()); // Llama al otro constructor con un FlowLayout por defecto.
    }

    public PanelPixel(LayoutManager layout) { // Constructor que permite elegir el layout.
        super(layout); // Le pasa el layout al JPanel.
        setOpaque(false); // Hace el panel transparente, porque el fondo se dibuja a mano.
        setPadding(14); // Pone un espacio interno de 14 píxeles.
        Tema.alCambiar(this::repaint); // Repinta el panel cuando cambie el tema.
    }

    // Espacio entre el borde y lo que va adentro.
    public void setPadding(int px) { // Recibe el espacio deseado en píxeles.
        int p = px + BORDE; // Suma el grosor del borde para que el contenido no lo toque.
        setBorder(BorderFactory.createEmptyBorder(p, p, p + SOMBRA, p + SOMBRA)); // Margen vacío; abajo y a la derecha suma la sombra.
    }

    // Cambia el color de fondo del panel. Con null vuelve al color del tema.
    public void setColorFondo(Color color) { // Recibe el nuevo color.
        this.colorFondo = color; // Lo guarda.
        repaint(); // Repinta para que se vea el cambio.
    }

    @Override // Indica que se reemplaza el método de dibujo de JPanel.
    protected void paintComponent(Graphics g) { // Swing lo llama cada vez que hay que dibujar el panel.
        Graphics2D g2 = (Graphics2D) g.create(); // Crea una copia del pincel para dibujar sin afectar al original.
        int w = getWidth() - SOMBRA; // Ancho del panel sin contar la sombra.
        int h = getHeight() - SOMBRA; // Alto del panel sin contar la sombra.
        g2.setColor(new Color(0, 0, 0, 70)); // Elige negro semitransparente para la sombra.
        g2.fillRect(SOMBRA, SOMBRA, w, h); // Dibuja la sombra corrida abajo y a la derecha.
        g2.setColor(Tema.borde()); // Elige el color del borde según el tema.
        g2.fillRect(0, 0, w, h); // Dibuja el rectángulo del borde.
        g2.setColor(colorFondo != null ? colorFondo : Tema.panel()); // Elige el color propio si existe; si no, el del tema.
        g2.fillRect(BORDE, BORDE, w - BORDE * 2, h - BORDE * 2); // Dibuja el relleno dejando visible el borde.
        g2.dispose(); // Libera el pincel.
        super.paintComponent(g); // Deja que JPanel termine su dibujo normal.
    }
}