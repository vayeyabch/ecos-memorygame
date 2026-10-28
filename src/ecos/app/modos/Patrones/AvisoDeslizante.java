package ecos.app.modos.Patrones;

import java.awt.Color;
import java.awt.Dimension;
import java.awt.Font;
import javax.swing.BorderFactory;
import javax.swing.JLabel;
import javax.swing.JPanel;
import javax.swing.Timer;

/**
 * Capa transparente con un rectángulo que cruza la pantalla de izquierda a derecha
 * (entra, se queda 1 s en el centro y sale). SOLO VISUAL: mismo estilo del
 * "Turno de: ..." del modo clásico.
 *
 * Uso: se agrega a la pantalla, se le ponen los límites de toda la pantalla
 * (en doLayout) y se llama mostrar("Nivel 2").
 */
public class AvisoDeslizante extends JPanel {

    // Colores del juego (copiados de Paleta, así este archivo no depende de ella)
    private static final Color GUINDA = new Color(91, 37, 56);
    private static final Color DORADO = new Color(240, 204, 136);

    private static final int ENTRADA_MS = 600;  // llega al centro (frena al final)
    private static final int PAUSA_MS = 1000;   // se queda quieto en el centro
    private static final int SALIDA_MS = 600;   // se va por la derecha (acelera)
    private static final int INTERVALO_MS = 15; // ~60 cuadros por segundo

    private final JLabel lblAviso = new JLabel("", JLabel.CENTER);
    private final Timer temporizador = new Timer(INTERVALO_MS, e -> animar());
    private long inicio;

    public AvisoDeslizante() {
        super(null); // sin layout: el rectángulo se mueve a mano
        setOpaque(false);
        setVisible(false);

        lblAviso.setFont(new Font("Pixelify Sans", Font.BOLD, 48));
        lblAviso.setForeground(GUINDA);
        lblAviso.setOpaque(true);
        lblAviso.setBackground(DORADO);
        lblAviso.setBorder(BorderFactory.createCompoundBorder(
                BorderFactory.createLineBorder(GUINDA, 6),
                BorderFactory.createEmptyBorder(25, 70, 25, 70)));
        add(lblAviso);
    }

    /** Hace pasar el rectángulo con este texto. */
    public void mostrar(String texto) {
        lblAviso.setText(texto);
        Dimension tamano = lblAviso.getPreferredSize();
        lblAviso.setSize(tamano);
        lblAviso.setLocation(-tamano.width, (getHeight() - tamano.height) / 2);

        temporizador.stop(); // por si todavía se estaba moviendo el anterior
        inicio = System.currentTimeMillis();
        setVisible(true);
        temporizador.start();
    }

    /** Un cuadro de la animación: entra, pausa en el centro y sale. */
    private void animar() {
        long t = System.currentTimeMillis() - inicio;
        int anchoAviso = lblAviso.getWidth();
        int centroX = (getWidth() - anchoAviso) / 2;

        int x;
        if (t < ENTRADA_MS) {
            double p = t / (double) ENTRADA_MS;
            x = (int) (-anchoAviso + (centroX + anchoAviso) * (1 - Math.pow(1 - p, 3)));
        } else if (t < ENTRADA_MS + PAUSA_MS) {
            x = centroX;
        } else {
            double p = Math.min(1.0, (t - ENTRADA_MS - PAUSA_MS) / (double) SALIDA_MS);
            x = (int) (centroX + (getWidth() - centroX) * Math.pow(p, 3));
        }
        lblAviso.setLocation(x, (getHeight() - lblAviso.getHeight()) / 2);

        if (t >= ENTRADA_MS + PAUSA_MS + SALIDA_MS) {
            temporizador.stop();
            setVisible(false);
        }
    }
}