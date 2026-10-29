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
    private static final int LINEA = 3; // Grosor de la línea clara que va dentro del borde.
    private static final int SOMBRA = 6; // Tamaño de la sombra dura de abajo, en píxeles.

    private Color colorFondo = null; // Color de fondo propio; null significa que usa el color del tema.
    private Color colorBorde = null; // Color de borde propio; null significa que usa el color del tema.
    private final Runnable alCambiarTema = this::repaint; // Acción que repinta el panel cuando cambia el tema.

    public PanelPixel() { // Constructor sin parámetros.
        this(new FlowLayout()); // Llama al otro constructor con un FlowLayout por defecto.
    }

    public PanelPixel(LayoutManager layout) { // Constructor que permite elegir el layout.
        super(layout); // Le pasa el layout al JPanel.
        setOpaque(false); // Hace el panel transparente, porque el fondo se dibuja a mano.
        setPadding(14); // Pone un espacio interno de 14 píxeles.
    }

    @Override // Indica que se reemplaza un método de JPanel.
    public void addNotify() { // Swing lo llama cuando el panel entra a una ventana.
        super.addNotify(); // Deja que Swing haga lo suyo primero.
        Tema.alCambiar(alCambiarTema); // Empieza a escuchar los cambios de tema.
    }

    @Override // Indica que se reemplaza un método de JPanel.
    public void removeNotify() { // Swing lo llama cuando el panel sale de la ventana.
        Tema.quitar(alCambiarTema); // Deja de escuchar los cambios de tema (así no queda guardado de más).
        super.removeNotify(); // Deja que Swing haga lo suyo.
    }

    // Espacio entre el borde y lo que va adentro.
    public void setPadding(int px) { // Recibe el espacio deseado en píxeles.
        int p = px + BORDE + LINEA; // Suma el borde y la línea clara para que el contenido no los toque.
        setBorder(BorderFactory.createEmptyBorder(p, p, p + SOMBRA, p)); // Margen vacío; abajo suma la sombra.
    }

    // Cambia el color de fondo del panel. Con null vuelve al color del tema.
    public void setColorFondo(Color color) { // Recibe el nuevo color.
        this.colorFondo = color; // Lo guarda.
        repaint(); // Repinta para que se vea el cambio.
    }

    // Cambia el color del borde del panel. Con null vuelve al color del tema.
    public void setColorBorde(Color color) { // Recibe el nuevo color.
        this.colorBorde = color; // Lo guarda.
        repaint(); // Repinta para que se vea el cambio.
    }

    @Override // Indica que se reemplaza el método de dibujo de JPanel.
    protected void paintComponent(Graphics g) { // Swing lo llama cada vez que hay que dibujar el panel.
        Graphics2D g2 = (Graphics2D) g.create(); // Crea una copia del pincel para dibujar sin afectar al original.
        int w = getWidth(); // Ancho total del panel.
        int h = getHeight() - SOMBRA; // Alto del panel sin contar la sombra.
        g2.setColor(Tema.sombra()); // Elige el color de la sombra dura.
        g2.fillRect(0, SOMBRA, w, h); // Dibuja la sombra: el mismo rectángulo, corrido hacia abajo.
        g2.setColor(colorBorde != null ? colorBorde : Tema.borde()); // Elige el color del borde (propio o del tema).
        g2.fillRect(0, 0, w, h); // Dibuja el rectángulo del borde.
        g2.setColor(Tema.panelAlto()); // Elige el color de la línea clara interior.
        g2.fillRect(BORDE, BORDE, w - BORDE * 2, h - BORDE * 2); // Dibuja la línea clara.
        g2.setColor(colorFondo != null ? colorFondo : Tema.panel()); // Elige el color de relleno (propio o del tema).
        g2.fillRect(BORDE + LINEA, BORDE + LINEA, w - (BORDE + LINEA) * 2, h - (BORDE + LINEA) * 2); // Dibuja el relleno dejando visible la línea clara.
        g2.dispose(); // Libera el pincel.
        super.paintComponent(g); // Deja que JPanel termine su dibujo normal.
    }
}
