package ecos.componentes;

// Clases del propio proyecto que se usan aquí.
import ecos.recursos.Sprites;
// Librería gráfica AWT, Dimension para los tamaños y Graphics para dibujar.
import java.awt.Dimension;
import java.awt.Graphics;
// Librería Swing, JPanel para los paneles y Timer para los relojes.
import javax.swing.JPanel;
import javax.swing.Timer;

// Animación de mascotas que van pasando por la pantalla de acceso.
public class DesfileMascotas extends JPanel {

    // Cuántas mascotas hay, su tamaño en píxeles, el espacio entre ellas y cuánto sube cada una al saltar.
    private static final int MASCOTAS = 9;
    private static final int LADO = 80;
    private static final int SEPARACION = 8;
    private static final int SALTO = 16;
    // Altura del salto en 8 pasos (se ve como pixel art).
    private static final double[] ESCALONES = {0, 0.25, 0.5, 0.75, 1, 0.75, 0.5, 0.25};

    // Cuenta los pasos de la animación. Cada 100 ms avanza un paso y repinta.
    private int tic = 0;
    private final Timer reloj = new Timer(100, e -> { tic++; repaint(); });

    // Constructor sin parámetros.
    public DesfileMascotas() {
        // Es transparente, se ve el fondo de la pantalla. Ancho de todas las mascotas juntas y alto con sitio para saltar.
        setOpaque(false);
        setPreferredSize(new Dimension(MASCOTAS * LADO + (MASCOTAS - 1) * SEPARACION, LADO + SALTO + 8));
    }

    // Empieza a animar las mascotas.
    public void iniciar() {
        // Arranca el reloj.
        reloj.start();
    }

    // Deja de animar las mascotas.
    public void detener() {
        // Detiene el reloj.
        reloj.stop();
    }

    // Swing lo llama cada vez que hay que dibujar.
    @Override
    protected void paintComponent(Graphics g) {
        // Calcula el ancho de todas las mascotas juntas y la posición de la primera para centrarlas, y recorre cada una en
        // un punto distinto del salto.
        int total = MASCOTAS * LADO + (MASCOTAS - 1) * SEPARACION;
        int x0 = (getWidth() - total) / 2;
        for (int i = 0; i < MASCOTAS; i++) {
            int paso = (tic + i * 3) % ESCALONES.length;
            // Cuánto está subida en este paso. Dibuja la mascota.
            int salto = (int) Math.round(ESCALONES[paso] * SALTO);
            g.drawImage(Sprites.mascota(i + 1, LADO), x0 + i * (LADO + SEPARACION), getHeight() - LADO - 4 - salto, null);
        }
    }

    // Tamaño mínimo del componente.
    @Override
    public Dimension getMinimumSize() {
        // Nunca se achica más de lo que necesita.
        return getPreferredSize();
    }

    // Tamaño máximo del componente.
    @Override
    public Dimension getMaximumSize() {
        // Nunca se estira más de lo que necesita.
        return getPreferredSize();
    }
}
