package ecos.componentes;

import ecos.recursos.Sprites;
import java.awt.Dimension;
import java.awt.Graphics;
import javax.swing.JPanel;
import javax.swing.Timer;

// Mascotas que van pasando por la pantalla de acceso.
public class DesfileMascotas extends JPanel {

    // Cuántas mascotas hay, su tamaño, el espacio entre ellas y cuánto sube cada una al saltar.
    private static final int MASCOTAS = 9;
    private static final int LADO = 80;
    private static final int SEPARACION = 8;
    private static final int SALTO = 16;
    // Altura del salto en 8 pasos, como pixel art.
    private static final double[] ESCALONES = {0, 0.25, 0.5, 0.75, 1, 0.75, 0.5, 0.25};

    // Cuenta los pasos; cada 100 ms avanza uno y repinta.
    private int tic = 0;
    private final Timer reloj = new Timer(100, e -> { tic++; repaint(); });

    public DesfileMascotas() {
        setOpaque(false);
        setPreferredSize(new Dimension(MASCOTAS * LADO + (MASCOTAS - 1) * SEPARACION, LADO + SALTO + 8));
    }

    public void iniciar() {
        reloj.start();
    }

    public void detener() {
        reloj.stop();
    }

    @Override
    protected void paintComponent(Graphics g) {
        // Centra todas las mascotas y pone cada una en un punto distinto del salto.
        int total = MASCOTAS * LADO + (MASCOTAS - 1) * SEPARACION;
        int x0 = (getWidth() - total) / 2;
        for (int i = 0; i < MASCOTAS; i++) {
            int paso = (tic + i * 3) % ESCALONES.length;
            int salto = (int) Math.round(ESCALONES[paso] * SALTO);
            g.drawImage(Sprites.mascota(i + 1, LADO), x0 + i * (LADO + SEPARACION), getHeight() - LADO - 4 - salto, null);
        }
    }

    @Override
    public Dimension getMinimumSize() {
        return getPreferredSize();
    }

    @Override
    public Dimension getMaximumSize() {
        return getPreferredSize();
    }
}
