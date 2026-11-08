package ecos.componentes;

// Clases del propio proyecto que se usan aquí.
import ecos.app.Tema;
// Librería gráfica AWT, AlphaComposite para la transparencia, Component para cualquier elemento de la pantalla, Graphics
// para dibujar, Graphics2D para dibujar con más opciones y GridBagLayout para acomodar en una cuadrícula flexible.
import java.awt.AlphaComposite;
import java.awt.Component;
import java.awt.Graphics;
import java.awt.Graphics2D;
import java.awt.GridBagLayout;
// Eventos del mouse y el teclado de AWT, MouseAdapter para reaccionar al mouse.
import java.awt.event.MouseAdapter;
// Librería Swing, JComponent para la base de los componentes, JLayeredPane para las capas, JPanel para los paneles y
// Timer para los relojes.
import javax.swing.JComponent;
import javax.swing.JLayeredPane;
import javax.swing.JPanel;
import javax.swing.Timer;

// Cortina oscura con un panel al centro que aparece "creciendo" (como el modal del diseño).
public class CapaModal extends JPanel {

    // Todas las ventanas abiertas, para poder cerrarlas juntas al salir de una pantalla.
    // Ventanas abiertas ahora.
    private static final java.util.List<CapaModal> ABIERTAS = new java.util.ArrayList<>();

    // Momento en que se abrió, el contenido y qué tan grande se ve (la animación va de 70 % a 100 %).
    private final long inicio = System.currentTimeMillis();
    private final JComponent contenido;
    // El panel crece de 0.7 a 1.
    private double escala = 0.7;
    private final Timer reloj;

    // Recibe la ventana que va al centro; la cortina se traga los clics para que no lleguen a lo que hay atrás.
    public CapaModal(JComponent contenido) {
        super(new GridBagLayout());
        this.contenido = contenido;
        setOpaque(false);
        add(contenido);
        // Se traga los clics para que no lleguen a lo de atrás.
        addMouseListener(new MouseAdapter() { });
        // Cada 30 ms la ventana crece en escalones, como se ve en un videojuego, hasta que termina la animación.
        reloj = new Timer(30, e -> {
            double t = Math.min(1, (System.currentTimeMillis() - inicio) / 350.0);
            escala = 0.7 + 0.3 * (Math.floor(t * 5) / 5.0);
            repaint();
            if (t >= 1) {
                ((Timer) e.getSource()).stop();
            }
        });
        reloj.start();
    }

    // Agrega la capa encima de la pantalla actual y le da el foco.
    public void mostrar(Component origen) {
        ABIERTAS.add(this);
        Capas.agregar(origen, this, JLayeredPane.MODAL_LAYER + 5);
        revalidate();
        contenido.requestFocusInWindow();
    }

    // Cierra todas las ventanas abiertas (se usa al salir de una pantalla).
    public static void cerrarTodas() {
        for (CapaModal c : new java.util.ArrayList<>(ABIERTAS)) {
            c.cerrar();
        }
    }

    // Cierra solo esta ventana, la saca de la lista y quita la capa.
    public void cerrar() {
        ABIERTAS.remove(this);
        reloj.stop();
        Capas.quitar(this);
    }

    // Pinta la cortina oscura que cubre toda la pantalla.
    @Override
    protected void paintComponent(Graphics g) {
        g.setColor(Tema.velo());
        g.fillRect(0, 0, getWidth(), getHeight());
    }

    // Primero se pinta la cortina sin escala y después el contenido, más pequeño y un poco transparente al inicio.
    @Override
    public void paint(Graphics g) {
        // La cortina primero, sin escala.
        paintComponent(g);
        Graphics2D g2 = (Graphics2D) g.create();
        g2.translate(getWidth() / 2.0, getHeight() / 2.0);
        g2.scale(escala, escala);
        g2.translate(-getWidth() / 2.0, -getHeight() / 2.0);
        g2.setComposite(AlphaComposite.getInstance(AlphaComposite.SRC_OVER, (float) Math.min(1, (escala - 0.7) / 0.3 + 0.2)));
        paintChildren(g2);
        g2.dispose();
    }
}
