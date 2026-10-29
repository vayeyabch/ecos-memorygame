package ecos.componentes;

import ecos.recursos.Sprites; // Para obtener las mascotas.
import java.awt.Dimension; // Representa un tamaño (ancho y alto).
import java.awt.Graphics; // Herramienta básica para dibujar.
import javax.swing.JPanel; // Panel de Swing: la clase de la que hereda esta.
import javax.swing.Timer; // Reloj que ejecuta una acción cada cierto tiempo.

public class DesfileMascotas extends JPanel {

    private static final int MASCOTAS = 9; // Cuántas mascotas hay.
    private static final int LADO = 80; // Tamaño de cada mascota, en píxeles.
    private static final int SEPARACION = 8; // Espacio entre mascotas.
    private static final int SALTO = 16; // Cuánto sube cada mascota al saltar.
    private static final double[] ESCALONES = {0, 0.25, 0.5, 0.75, 1, 0.75, 0.5, 0.25}; // Altura del salto en 8 pasos (se ve como pixel art).

    private int tic = 0; // Cuenta los pasos de la animación.
    private final Timer reloj = new Timer(100, e -> { tic++; repaint(); }); // Cada 100 ms avanza un paso y repinta.

    public DesfileMascotas() { // Constructor: no recibe nada.
        setOpaque(false); // Es transparente: se ve el fondo de la pantalla.
        setPreferredSize(new Dimension(MASCOTAS * LADO + (MASCOTAS - 1) * SEPARACION, LADO + SALTO + 8)); // Ancho de todas las mascotas juntas y alto con sitio para saltar.
    }

    public void iniciar() { // Empieza a animar las mascotas.
        reloj.start(); // Arranca el reloj.
    }

    public void detener() { // Deja de animar las mascotas.
        reloj.stop(); // Detiene el reloj.
    }

    @Override // Indica que se reemplaza el método de dibujo de JPanel.
    protected void paintComponent(Graphics g) { // Swing lo llama cada vez que hay que dibujar.
        int total = MASCOTAS * LADO + (MASCOTAS - 1) * SEPARACION; // Ancho de todas las mascotas juntas.
        int x0 = (getWidth() - total) / 2; // Posición de la primera (para centrarlas).
        for (int i = 0; i < MASCOTAS; i++) { // Recorre las mascotas.
            int paso = (tic + i * 3) % ESCALONES.length; // Cada mascota va en un punto distinto del salto.
            int salto = (int) Math.round(ESCALONES[paso] * SALTO); // Cuánto está subida en este paso.
            g.drawImage(Sprites.mascota(i + 1, LADO), x0 + i * (LADO + SEPARACION), getHeight() - LADO - 4 - salto, null); // Dibuja la mascota.
        }
    }

    @Override // Indica que se reemplaza un método de JComponent.
    public Dimension getMinimumSize() { // Tamaño mínimo del componente.
        return getPreferredSize(); // Nunca se achica más de lo que necesita.
    }

    @Override // Indica que se reemplaza un método de JComponent.
    public Dimension getMaximumSize() { // Tamaño máximo del componente.
        return getPreferredSize(); // Nunca se estira más de lo que necesita.
    }
}
