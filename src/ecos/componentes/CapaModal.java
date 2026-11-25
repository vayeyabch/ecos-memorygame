package ecos.componentes;

// Tema: da el color de la cortina oscura (velo) según claro u oscuro.
import ecos.app.Tema;
// AlphaComposite: permite dibujar con transparencia (el contenido aparece poco a poco).
import java.awt.AlphaComposite;
// Component: tipo base de cualquier elemento visual; aquí es el "origen" de la ventana.
import java.awt.Component;
// Graphics: el "pincel" para dibujar.
import java.awt.Graphics;
// Graphics2D: versión del pincel con más opciones (escalar, mover, transparencia).
import java.awt.Graphics2D;
// GridBagLayout: layout que centra el contenido en el medio de la capa.
import java.awt.GridBagLayout;
// MouseAdapter: clase lista para escuchar el mouse; aquí se usa vacía solo para "tragarse" los clics.
import java.awt.event.MouseAdapter;
// JComponent: tipo del contenido (la ventana del centro).
import javax.swing.JComponent;
// JLayeredPane: da el nivel MODAL_LAYER, una capa alta para que la cortina quede encima de la pantalla.
import javax.swing.JLayeredPane;
// JPanel: clase base de la que hereda CapaModal.
import javax.swing.JPanel;
// Timer (de Swing): reloj que anima el crecimiento del contenido.
import javax.swing.Timer;

// Cortina oscura con un panel al centro que aparece creciendo.
public class CapaModal extends JPanel {

    // Ventanas abiertas, para poder cerrarlas juntas al salir de una pantalla.
    private static final java.util.List<CapaModal> ABIERTAS = new java.util.ArrayList<>();

    // Momento en que se abrió, contenido y escala de la animación (de 70 % a 100 %).
    private final long inicio = System.currentTimeMillis();
    private final JComponent contenido;
    private double escala = 0.7;
    private final Timer reloj;

    // Recibe la ventana del centro; la cortina se traga los clics para que no lleguen a lo de atrás.
    public CapaModal(JComponent contenido) {
        super(new GridBagLayout());
        this.contenido = contenido;
        setOpaque(false);
        // Con GridBagLayout y sin reglas, el contenido queda centrado.
        add(contenido);
        // Un MouseAdapter vacío basta para que esta capa reciba los clics y no los pase a lo de atrás.
        addMouseListener(new MouseAdapter() { });
        // Cada 30 ms la ventana crece en escalones hasta que termina la animación.
        reloj = new Timer(30, e -> {
            // t va de 0 a 1 en 350 ms.
            double t = Math.min(1, (System.currentTimeMillis() - inicio) / 350.0);
            // La escala sube de 0.7 a 1 en 5 escalones (pixel art).
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
        // MODAL_LAYER + 5: una capa por encima de los diálogos normales.
        Capas.agregar(origen, this, JLayeredPane.MODAL_LAYER + 5);
        revalidate();
        contenido.requestFocusInWindow();
    }

    // Cierra todas las ventanas abiertas, se usa al salir de una pantalla.
    public static void cerrarTodas() {
        // Se recorre una copia de la lista, porque cerrar() quita elementos de la original.
        for (CapaModal c : new java.util.ArrayList<>(ABIERTAS)) {
            c.cerrar();
        }
    }

    // Cierra solo esta ventana.
    public void cerrar() {
        ABIERTAS.remove(this);
        reloj.stop();
        Capas.quitar(this);
    }

    // Pinta solo la cortina oscura (el velo) sobre toda la capa.
    @Override
    protected void paintComponent(Graphics g) {
        g.setColor(Tema.velo());
        g.fillRect(0, 0, getWidth(), getHeight());
    }

    // Primero se pinta la cortina sin escala y después el contenido, más pequeño y transparente al inicio.
    @Override
    public void paint(Graphics g) {
        paintComponent(g);
        Graphics2D g2 = (Graphics2D) g.create();
        // Escala desde el centro: mueve el origen al centro, escala y lo devuelve.
        g2.translate(getWidth() / 2.0, getHeight() / 2.0);
        g2.scale(escala, escala);
        g2.translate(-getWidth() / 2.0, -getHeight() / 2.0);
        // La opacidad sube junto con la escala, de unos 0.2 hasta 1.
        g2.setComposite(AlphaComposite.getInstance(AlphaComposite.SRC_OVER, (float) Math.min(1, (escala - 0.7) / 0.3 + 0.2)));
        // Dibuja los hijos (el contenido) con esa escala y transparencia.
        paintChildren(g2);
        g2.dispose();
    }
}