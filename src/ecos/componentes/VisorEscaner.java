package ecos.componentes;

import ecos.app.Tema;
import ecos.recursos.Fuentes;
import ecos.recursos.Sprites;
import java.awt.Color;
import java.awt.Dimension;
import java.awt.FontMetrics;
import java.awt.Graphics;
import java.awt.Graphics2D;
import javax.swing.JPanel;
import javax.swing.Timer;

// Visor del reconocimiento facial: muestra la mascota con una línea de escáner animada y tres estados (normal,
// escaneando y listo).
public class VisorEscaner extends JPanel {

    public enum Estado { NORMAL, ESCANEANDO, OK, ERROR }

    // Lado del visor, alto de la sombra, tamaño de la mascota y cuántas mascotas hay.
    private static final int LADO = 260;
    private static final int SOMBRA = 5;
    private static final int MASCOTA = 168;
    private static final int MASCOTAS = 9;
    // Cada cuántos pasos cambia la mascota, altura del salto y colores del visor (violeta oscuro y rosa).
    private static final int CAMBIO_MASCOTA_TICS = 13;
    private static final double[] ESCALONES = {0, 0.25, 0.5, 0.75, 1, 0.75, 0.5, 0.25};
    private static final Color FONDO = new Color(0x1C1327);
    private static final Color ROSA = new Color(0xFCA5AB);

    // Reloj de las animaciones, estado, mascota que se ve y cuenta de pasos.
    private final Timer reloj = new Timer(100, e -> alTic());
    private Estado estado = Estado.NORMAL;
    private int indice = 1;
    private int tic = 0;
    // Cuánto está corrida la mascota hacia los lados y cuánto debería estar.
    private double desplazamiento = 0;
    private int destino = 0;

    public VisorEscaner() {
        setOpaque(false);
        setPreferredSize(new Dimension(LADO, LADO + SOMBRA));
    }

    public void setEstado(Estado estado) {
        this.estado = estado;
        repaint();
    }

    // Corre la mascota hacia un lado, para simular que gira la cabeza.
    public void setDesplazamiento(int px) {
        this.destino = px * 2;
    }

    public void iniciar() {
        reloj.start();
    }

    public void detener() {
        reloj.stop();
    }

    private void alTic() {
        tic++;
        if (estado != Estado.ESCANEANDO && tic % CAMBIO_MASCOTA_TICS == 0) {
            indice = indice % MASCOTAS + 1;
        }
        desplazamiento += (destino - desplazamiento) / 2.5;
        repaint();
    }

    @Override
    protected void paintComponent(Graphics g) {
        // Marco verde si salió bien, rojo si falló.
        Graphics2D g2 = (Graphics2D) g.create();
        Color marco = Tema.borde();
        if (estado == Estado.OK) {
            marco = Tema.acierto();
        } else if (estado == Estado.ERROR) {
            marco = Tema.peligro();
        }
        g2.setColor(Tema.sombra());
        g2.fillRect(0, SOMBRA, LADO, LADO);
        g2.setColor(marco);
        g2.fillRect(0, 0, LADO, LADO);
        g2.setColor(FONDO);
        g2.fillRect(4, 4, LADO - 8, LADO - 8);

        // Lo que sigue se dibuja recortado dentro del visor.
        g2.setClip(4, 4, LADO - 8, LADO - 8);
        int salto = (int) Math.round(ESCALONES[tic % ESCALONES.length] * 16);
        int x = (LADO - MASCOTA) / 2 + (int) Math.round(desplazamiento);
        int y = (LADO - MASCOTA) / 2 - salto;
        g2.drawImage(Sprites.mascota(indice, MASCOTA), x, y, null);

        g2.setColor(ROSA);
        int m = 12;
        int l = 32;
        int t = 5;
        g2.fillRect(m, m, l, t); g2.fillRect(m, m, t, l);
        g2.fillRect(LADO - m - l, m, l, t); g2.fillRect(LADO - m - t, m, t, l);
        g2.fillRect(m, LADO - m - t, l, t); g2.fillRect(m, LADO - m - l, t, l);
        g2.fillRect(LADO - m - l, LADO - m - t, l, t); g2.fillRect(LADO - m - t, LADO - m - l, t, l);

        if (estado == Estado.ESCANEANDO) {
            int recorrido = LADO - 8 - 20;
            int paso = tic % 32;
            int arriba = paso < 16 ? paso : 32 - paso;
            // Calcula la posición del haz, sus colores y lo dibuja.
            int hy = 10 + arriba * recorrido / 16;
            g2.setColor(ROSA);
            g2.fillRect(12, hy, LADO - 24, 8);
            g2.setColor(new Color(0xFFF0E4));
            g2.drawRect(12 + 1, hy + 1, LADO - 24 - 3, 5);
        }

        if (estado == Estado.OK) {
            g2.setColor(new Color(11, 7, 16, 184));
            g2.fillRect(4, 4, LADO - 8, LADO - 8);
            g2.setFont(Fuentes.titulo(32f));
            // Escribe OK en el centro.
            FontMetrics fm = g2.getFontMetrics();
            g2.setColor(Tema.acierto());
            g2.drawString("OK", (LADO - fm.stringWidth("OK")) / 2, (LADO - fm.getHeight()) / 2 + fm.getAscent());
        }
        g2.dispose();
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
