package ecos.componentes;

import ecos.app.Tema;
import java.awt.AlphaComposite;
import java.awt.Component;
import java.awt.Graphics;
import java.awt.Graphics2D;
import java.awt.GridBagLayout;
import java.awt.event.MouseAdapter;
import javax.swing.JComponent;
import javax.swing.JLayeredPane;
import javax.swing.JPanel;
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
        add(contenido);
        addMouseListener(new MouseAdapter() { });
        // Cada 30 ms la ventana crece en escalones hasta que termina la animación.
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

    // Cierra todas las ventanas abiertas, se usa al salir de una pantalla.
    public static void cerrarTodas() {
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
        g2.translate(getWidth() / 2.0, getHeight() / 2.0);
        g2.scale(escala, escala);
        g2.translate(-getWidth() / 2.0, -getHeight() / 2.0);
        g2.setComposite(AlphaComposite.getInstance(AlphaComposite.SRC_OVER, (float) Math.min(1, (escala - 0.7) / 0.3 + 0.2)));
        paintChildren(g2);
        g2.dispose();
    }
}
