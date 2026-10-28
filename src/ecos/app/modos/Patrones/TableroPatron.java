package ecos.app.modos.Patrones;

import java.awt.BasicStroke;
import java.awt.Color;
import java.awt.Dimension;
import java.awt.Font;
import java.awt.FontMetrics;
import java.awt.Graphics;
import java.awt.Graphics2D;
import java.awt.GridLayout;
import java.awt.LayoutManager;
import java.util.function.IntConsumer;
import javax.swing.BorderFactory;
import javax.swing.ImageIcon;
import javax.swing.JButton;
import javax.swing.JPanel;

/**
 * Tablero del modo patrones (SOLO INTERFAZ, sin lógica de juego).
 * Es una matriz de 4 x 4 casillas: casillas[fila][columna].
 * Las casillas se piden por número (0 a 15): fila = i / 4, columna = i % 4.
 *
 *   iluminar(i) / marcarCorrecta(i) / marcarIncorrecta(i) / limpiar(i)
 *   limpiarTodas()          -> todas en estado normal
 *   setActivas(boolean)     -> permite o bloquea los clics
 *   setAlClick(accion)      -> avisa qué casilla (0 a 15) se pulsó
 */
public class TableroPatron extends JPanel {

    // Definiciones locales para que este tablero no dependa del paquete JuegoC.
    private static final class Paleta {
        private static final Color CREMA = new Color(253, 248, 236);
        private static final Color GUINDA = new Color(112, 31, 57);
        private static final Color ROJO_CARTA = new Color(190, 45, 55);
    }

    private static final class Utilidades {
        private static ImageIcon cargarIconoRedimensionado(String ruta, int ancho, int alto) {
            ImageIcon original = new ImageIcon(ruta);
            if (original.getIconWidth() <= 0 || original.getIconHeight() <= 0) {
                return null;
            }
            java.awt.Image imagen = original.getImage().getScaledInstance(
                    ancho, alto, java.awt.Image.SCALE_SMOOTH);
            return new ImageIcon(imagen);
        }

        private static Font cargarFuentePixel(float tamano) {
            return new Font(Font.MONOSPACED, Font.PLAIN, Math.round(tamano));
        }
    }

    public static final int FILAS = 4;
    public static final int COLUMNAS = 4;
    public static final int TOTAL_CASILLAS = FILAS * COLUMNAS;

    private static final int LADO_ICONO = 64;
    private static final String RUTA_ICONOS = "src/recursos/Iconos/icono"; // + número + ".png"

    private final CasillaPatron[][] casillas = new CasillaPatron[FILAS][COLUMNAS];
    private IntConsumer alClick;

    public TableroPatron() {
        super(new GridLayout(FILAS, COLUMNAS, 12, 12));
        setBackground(Paleta.CREMA);
        setBorder(crearBordeMarco());

        for (int fila = 0; fila < FILAS; fila++) {
            for (int columna = 0; columna < COLUMNAS; columna++) {
                // Con los dos for se cargan icono1.png ... icono16.png
                // (los mismos del otro modo), uno por casilla.
                final int indice = fila * COLUMNAS + columna;
                ImageIcon icono = Utilidades.cargarIconoRedimensionado(
                        RUTA_ICONOS + (indice + 1) + ".png", LADO_ICONO, LADO_ICONO);

                CasillaPatron casilla = new CasillaPatron(indice + 1, icono);
                casilla.setEnabled(false); // hasta que el controlador las active
                casilla.addActionListener(e -> {
                    if (alClick != null) {
                        alClick.accept(indice);
                    }
                });
                casillas[fila][columna] = casilla;
                add(casilla);
            }
        }
    }

    // ---------- Marco crema con borde guinda (también lo usa el panel de arriba de la pantalla) ----------

    public static javax.swing.border.Border crearBordeMarco() {
        return BorderFactory.createCompoundBorder(
                BorderFactory.createCompoundBorder(
                        BorderFactory.createMatteBorder(6, 6, 12, 6, Paleta.GUINDA),
                        BorderFactory.createLineBorder(Color.WHITE, 3)),
                BorderFactory.createEmptyBorder(18, 22, 18, 22));
    }

    public static JPanel crearPanelMarco(LayoutManager layout) {
        JPanel panel = new JPanel(layout);
        panel.setBackground(Paleta.CREMA);
        panel.setBorder(crearBordeMarco());
        return panel;
    }

    // ---------- Lo que la pantalla le pide al tablero ----------

    private CasillaPatron casilla(int indice) {
        return casillas[indice / COLUMNAS][indice % COLUMNAS];
    }

    public void iluminar(int indice) {
        casilla(indice).setEstado(Estado.ILUMINADA);
    }

    public void marcarCorrecta(int indice) {
        casilla(indice).setEstado(Estado.CORRECTA);
    }

    public void marcarIncorrecta(int indice) {
        casilla(indice).setEstado(Estado.INCORRECTA);
    }

    public void limpiar(int indice) {
        casilla(indice).setEstado(Estado.NORMAL);
    }

    public void limpiarTodas() {
        for (CasillaPatron[] fila : casillas) {
            for (CasillaPatron casilla : fila) {
                casilla.setEstado(Estado.NORMAL);
            }
        }
    }

    public void setActivas(boolean activas) {
        for (CasillaPatron[] fila : casillas) {
            for (CasillaPatron casilla : fila) {
                casilla.setEnabled(activas);
            }
        }
    }

    public void setAlClick(IntConsumer accion) {
        this.alClick = accion;
    }

    // ---------- Casilla (botón con el icono, dibujado estilo pixel) ----------

    public enum Estado { NORMAL, ILUMINADA, CORRECTA, INCORRECTA }

    private static class CasillaPatron extends JButton {

        private final int numero;
        private final ImageIcon icono;
        private Estado estado = Estado.NORMAL;

        CasillaPatron(int numero, ImageIcon icono) {
            this.numero = numero;
            this.icono = icono;
            setPreferredSize(new Dimension(100, 108));
            setOpaque(false);
            setContentAreaFilled(false);
            setBorderPainted(false);
            setFocusPainted(false);
            setRolloverEnabled(false);
        }

        void setEstado(Estado nuevo) {
            this.estado = nuevo;
            repaint();
        }

        @Override
        protected void paintComponent(Graphics g) {
            Graphics2D g2 = (Graphics2D) g.create();
            int w = getWidth();
            int h = getHeight();
            int borde = 4;
            int sombra = 8; // el borde de abajo es más grueso

            // Marco guinda
            g2.setColor(Paleta.GUINDA);
            g2.fillRect(0, 0, w, h);

            // Cara de la casilla
            int fx = borde;
            int fy = borde;
            int fw = w - borde * 2;
            int fh = h - borde * 2 - sombra;
            g2.setColor(colorCara());
            g2.fillRect(fx, fy, fw, fh);

            // Franja de abajo
            g2.setColor(colorFranja());
            g2.fillRect(fx, fy + fh - 4, fw, 4);

            // Contorno de color cuando está iluminada, acertada o fallada
            if (estado != Estado.NORMAL) {
                g2.setColor(colorContorno());
                g2.setStroke(new BasicStroke(4f));
                g2.drawRect(fx + 2, fy + 2, fw - 4, fh - 4);
            }

            // Icono (si no existe la imagen, se dibuja el número)
            if (icono != null) {
                int x = fx + (fw - icono.getIconWidth()) / 2;
                int y = fy + (fh - icono.getIconHeight()) / 2 - 2;
                icono.paintIcon(this, g2, x, y);
            } else {
                g2.setFont(Utilidades.cargarFuentePixel(28f).deriveFont(Font.BOLD));
                g2.setColor(Paleta.GUINDA);
                FontMetrics fm = g2.getFontMetrics();
                String texto = String.valueOf(numero);
                g2.drawString(texto, fx + (fw - fm.stringWidth(texto)) / 2,
                        fy + (fh - fm.getHeight()) / 2 + fm.getAscent());
            }
            g2.dispose();
        }

        private Color colorCara() {
            return switch (estado) {
                case ILUMINADA -> new Color(255, 236, 150);
                case CORRECTA -> new Color(200, 235, 200);
                case INCORRECTA -> new Color(240, 190, 195);
                default -> new Color(253, 248, 236);
            };
        }

        private Color colorFranja() {
            return switch (estado) {
                case ILUMINADA -> new Color(230, 205, 110);
                case CORRECTA -> new Color(150, 200, 150);
                case INCORRECTA -> new Color(210, 140, 150);
                default -> new Color(219, 210, 190);
            };
        }

        private Color colorContorno() {
            return switch (estado) {
                case ILUMINADA -> new Color(235, 160, 20);
                case CORRECTA -> new Color(90, 170, 90);
                default -> Paleta.ROJO_CARTA;
            };
        }
    }
}