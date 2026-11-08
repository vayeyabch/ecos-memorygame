package ecos.componentes;

// Clases del propio proyecto que se usan aquí.
import ecos.app.Tema;
// Librería gráfica AWT, Color para los colores, FlowLayout para acomodar en fila, Graphics para dibujar, Graphics2D para
// dibujar con más opciones y LayoutManager para el tipo de acomodo.
import java.awt.Color;
import java.awt.FlowLayout;
import java.awt.Graphics;
import java.awt.Graphics2D;
import java.awt.LayoutManager;
// Librería Swing, BorderFactory para los bordes y JPanel para los paneles.
import javax.swing.BorderFactory;
import javax.swing.JPanel;

// Caja con borde duro y sombra; es el "papel" de casi todas las ventanas y tarjetas.
public class PanelPixel extends JPanel {

    // Grosor del borde en píxeles, grosor de la línea clara que va dentro del borde y tamaño de la sombra dura de abajo.
    private static final int BORDE = 4;
    private static final int LINEA = 3;
    private static final int SOMBRA = 6;

    // Color de fondo y de borde propios, que si son null usan el color del tema, y acción que repinta el panel cuando cambia
    // el tema.
    private Color colorFondo = null;
    private Color colorBorde = null;
    private final Runnable alCambiarTema = this::repaint;

    // Constructor sin parámetros.
    public PanelPixel() {
        // Llama al otro constructor con un FlowLayout por defecto.
        this(new FlowLayout());
    }

    // Constructor que permite elegir el layout.
    public PanelPixel(LayoutManager layout) {
        // Le pasa el layout al JPanel. Hace el panel transparente, porque el fondo se dibuja a mano. Pone un espacio interno
        // de 14 píxeles.
        super(layout);
        setOpaque(false);
        setPadding(14);
    }

    // Swing lo llama cuando el panel entra a una ventana.
    @Override
    public void addNotify() {
        // Deja que Swing haga lo suyo primero. Empieza a escuchar los cambios de tema.
        super.addNotify();
        Tema.alCambiar(alCambiarTema);
    }

    // Swing lo llama cuando el panel sale de la ventana.
    @Override
    public void removeNotify() {
        // Deja de escuchar los cambios de tema (así no queda guardado de más). Deja que Swing haga lo suyo.
        Tema.quitar(alCambiarTema);
        super.removeNotify();
    }

    // Espacio entre el borde y lo que va adentro.
    // Recibe el espacio deseado en píxeles.
    public void setPadding(int px) {
        // Suma el borde y la línea clara para que el contenido no los toque. Margen vacío; abajo suma la sombra.
        int p = px + BORDE + LINEA;
        setBorder(BorderFactory.createEmptyBorder(p, p, p + SOMBRA, p));
    }

    // Cambia el color de fondo del panel. Con null vuelve al color del tema.
    // Recibe el nuevo color.
    public void setColorFondo(Color color) {
        // Repinta para que se vea el cambio.
        this.colorFondo = color;
        repaint();
    }

    // Cambia el color del borde del panel. Con null vuelve al color del tema.
    // Recibe el nuevo color.
    public void setColorBorde(Color color) {
        // Repinta para que se vea el cambio.
        this.colorBorde = color;
        repaint();
    }

    // Swing lo llama cada vez que hay que dibujar el panel.
    @Override
    protected void paintComponent(Graphics g) {
        // Crea una copia del pincel, toma el ancho total y el alto sin sombra y elige el color de la sombra dura.
        Graphics2D g2 = (Graphics2D) g.create();
        int w = getWidth();
        int h = getHeight() - SOMBRA;
        g2.setColor(Tema.sombra());
        // Dibuja la sombra, el mismo rectángulo, corrido hacia abajo. Elige el color del borde (propio o del tema). Dibuja el
        // rectángulo del borde. Elige el color de la línea clara interior.
        g2.fillRect(0, SOMBRA, w, h);
        g2.setColor(colorBorde != null ? colorBorde : Tema.borde());
        g2.fillRect(0, 0, w, h);
        g2.setColor(Tema.panelAlto());
        // Dibuja la línea clara. Elige el color de relleno (propio o del tema). Dibuja el relleno dejando visible la línea
        // clara. Libera el pincel.
        g2.fillRect(BORDE, BORDE, w - BORDE * 2, h - BORDE * 2);
        g2.setColor(colorFondo != null ? colorFondo : Tema.panel());
        g2.fillRect(BORDE + LINEA, BORDE + LINEA, w - (BORDE + LINEA) * 2, h - (BORDE + LINEA) * 2);
        g2.dispose();
        // Deja que JPanel termine su dibujo normal.
        super.paintComponent(g);
    }
}
