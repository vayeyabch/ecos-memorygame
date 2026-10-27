package ecos.app.modos.JuegoC;

import java.awt.BorderLayout;
import java.awt.Color;
import java.awt.Dimension;
import java.awt.FlowLayout;
import java.awt.Font;
import java.awt.Graphics;
import java.awt.Graphics2D;
import java.awt.GridLayout;
import javax.swing.BorderFactory;
import javax.swing.ImageIcon;
import javax.swing.JButton;
import javax.swing.JLabel;
import javax.swing.JPanel;
import javax.swing.Timer;

/**
 * Tablero de un jugador (SOLO INTERFAZ, sin lógica de juego).
 *
 * Métodos públicos para que la lógica controle la interfaz:
 *   mostrarCarta(indice, idImagen)  -> voltea una carta
 *   ocultarCarta(indice)            -> la vuelve a tapar
 *   setParejas(n) / setIntentos(n)  -> actualiza los contadores
 *   setTurnoActivo(true/false)      -> muestra "TU TURNO" si le toca a este jugador
 */
public class TableroJugador extends JPanel {

    public static final int ANCHO_CARTA = 70;
    public static final int ALTO_CARTA = 70;
    public static final int TOTAL_PAREJAS = 18;
    public static final int TOTAL_CARTAS = 36;

    private final String nombre;

    private final JLabel lblParejas;
    private final JLabel lblIntentos;
    private final JLabel lblTurno;

    private static final Color AMARILLO_PAREJA = new Color(255, 214, 51);
    private static final javax.swing.border.Border BORDE_PAREJA =
            BorderFactory.createLineBorder(AMARILLO_PAREJA, 4);
    private static final javax.swing.border.Border BORDE_NORMAL =
            BorderFactory.createLineBorder(Paleta.GUINDA, 2);

    private static final int VOLTEO_MS = 300;   // duración de la animación de voltear
    private static final int VOLTEO_INTERVALO_MS = 15;

    private final CartaBoton[] botonesCartas = new CartaBoton[TOTAL_CARTAS];
    private final ImageIcon dorsoIcono;

    public TableroJugador(String nombre) {
        this.nombre = nombre;

        setLayout(new BorderLayout());
        setBackground(Paleta.CREMA);
        setBorder(BorderFactory.createLineBorder(Paleta.NARANJA, 6));

        dorsoIcono = Utilidades.cargarIconoRedimensionado(
                "src/recursos/Iconos/iconoTapa.jpeg", ANCHO_CARTA, ALTO_CARTA);

        // ----- Información del jugador -----
        Font fuente = Utilidades.cargarFuentePixel(16f).deriveFont(Font.BOLD);

        JPanel panelInfo = new JPanel(new FlowLayout(FlowLayout.CENTER, 12, 12));
        panelInfo.setBackground(Paleta.CREMA);

        JLabel lblJugador = new JLabel(nombre);
        lblJugador.setFont(fuente);
        lblJugador.setForeground(Paleta.GUINDA);

        lblIntentos = crearCajaInfo("Intentos 0", fuente);
        lblParejas = crearCajaInfo("Parejas 0/" + TOTAL_PAREJAS, fuente);

        panelInfo.add(lblJugador);
        panelInfo.add(lblIntentos);
        panelInfo.add(lblParejas);

        // ----- Indicador de turno (reserva su espacio aunque esté vacío) -----
        lblTurno = new JLabel(" ", JLabel.CENTER);
        lblTurno.setFont(fuente);
        lblTurno.setForeground(Paleta.TEXTO_NARANJA);

        JPanel panelNorte = new JPanel(new BorderLayout());
        panelNorte.setBackground(Paleta.CREMA);
        panelNorte.add(panelInfo, BorderLayout.NORTH);
        panelNorte.add(lblTurno, BorderLayout.SOUTH);
        add(panelNorte, BorderLayout.NORTH);

        // ----- Cuadrícula de cartas, con marco guinda -----
        JPanel panelCuadricula = new JPanel(new GridLayout(6, 6, 6, 6));
        panelCuadricula.setBackground(Paleta.BEIGE);
        panelCuadricula.setBorder(BorderFactory.createCompoundBorder(
                BorderFactory.createCompoundBorder(
                        BorderFactory.createLineBorder(Paleta.GUINDA, 5),
                        BorderFactory.createLineBorder(Color.WHITE, 3)),
                BorderFactory.createEmptyBorder(8, 8, 8, 8)));

        for (int i = 0; i < TOTAL_CARTAS; i++) {
            panelCuadricula.add(crearBoton(i));
        }

        JPanel panelContenedor = new JPanel(new BorderLayout());
        panelContenedor.setBackground(Paleta.CREMA);
        panelContenedor.setBorder(BorderFactory.createEmptyBorder(8, 16, 16, 16));
        panelContenedor.add(panelCuadricula, BorderLayout.CENTER);

        add(panelContenedor, BorderLayout.CENTER);
    }

    // ---------- Caja de información (beige con borde guinda) ----------
    private JLabel crearCajaInfo(String texto, Font fuente) {
        JLabel etiqueta = new JLabel(texto, JLabel.CENTER);
        etiqueta.setFont(fuente);
        etiqueta.setForeground(Paleta.GUINDA);
        etiqueta.setOpaque(true);
        etiqueta.setBackground(Paleta.BEIGE);
        etiqueta.setBorder(BorderFactory.createCompoundBorder(
                BorderFactory.createLineBorder(Paleta.GUINDA, 4),
                BorderFactory.createEmptyBorder(10, 18, 10, 18)));
        return etiqueta;
    }

    // ---------- Creación de un botón ----------
    private CartaBoton crearBoton(final int index) {
        CartaBoton boton = new CartaBoton();
        boton.setPreferredSize(new Dimension(ANCHO_CARTA, ALTO_CARTA));

        if (dorsoIcono != null) {
            boton.setIcon(dorsoIcono);
        }

        boton.setFocusPainted(false);

        // Fondo guinda, sin el azul al pasar el mouse o hacer clic
        boton.setBackground(Paleta.GUINDA);
        boton.setForeground(Color.WHITE);
        boton.setOpaque(true);
        boton.setContentAreaFilled(false);
        boton.setRolloverEnabled(false);

        boton.setBorder(BORDE_NORMAL);

        boton.addActionListener(e -> alHacerClickEnCarta(index));

        botonesCartas[index] = boton;
        return boton;
    }

    /**
     * Se llama cuando el jugador pulsa una carta.
     * TODO (lógica): aquí va la lógica del juego. Por ahora solo voltea la
     * carta con una imagen de prueba para poder ver la interfaz.
     */
    private void alHacerClickEnCarta(int indice) {
        mostrarCarta(indice, indice % TOTAL_PAREJAS);
    }

    // ---------- Métodos para que la lógica controle la interfaz ----------

    /** Voltea una carta mostrando la imagen número idImagen (0 a 17). */
    public void mostrarCarta(int indice, int idImagen) {
        ImageIcon imagenFrente = Utilidades.cargarIconoRedimensionado(
                "src/recursos/Iconos/icono" + (idImagen + 1) + ".png", ANCHO_CARTA, ALTO_CARTA);

        animarVolteo(indice, () -> {
            if (imagenFrente != null) {
                botonesCartas[indice].setText("");
                botonesCartas[indice].setIcon(imagenFrente);
            } else {
                botonesCartas[indice].setIcon(null);
                botonesCartas[indice].setText(String.valueOf(idImagen + 1));
            }
        });
    }

    /** Vuelve a tapar una carta (también con animación). */
    public void ocultarCarta(int indice) {
        animarVolteo(indice, () -> {
            botonesCartas[indice].setText("");
            botonesCartas[indice].setIcon(dorsoIcono);
        });
    }

    /**
     * Animación de voltear: la carta se encoge a lo ancho hasta quedar de canto,
     * en ese momento cambia la imagen (cambioDeCara) y se vuelve a abrir.
     */
    private void animarVolteo(int indice, Runnable cambioDeCara) {
        CartaBoton boton = botonesCartas[indice];
        long inicio = System.currentTimeMillis();
        boolean[] yaCambio = {false};

        Timer temporizador = new Timer(VOLTEO_INTERVALO_MS, null);
        temporizador.addActionListener(e -> {
            double p = Math.min(1.0, (System.currentTimeMillis() - inicio) / (double) VOLTEO_MS);

            if (p < 0.5) {
                boton.escalaX = 1.0 - p * 2;      // 1 -> 0 (se cierra)
            } else {
                if (!yaCambio[0]) {               // a la mitad cambia la cara
                    cambioDeCara.run();
                    yaCambio[0] = true;
                }
                boton.escalaX = (p - 0.5) * 2;    // 0 -> 1 (se abre)
            }

            if (p >= 1.0) {
                boton.escalaX = 1.0;
                temporizador.stop();
            }
            boton.repaint();
        });
        temporizador.start();
    }

    /** Botón que se dibuja encogido a lo ancho para simular el giro de la carta. */
    private static class CartaBoton extends JButton {
        double escalaX = 1.0;

        @Override
        public void paint(Graphics g) {
            Graphics2D g2 = (Graphics2D) g.create();
            double centro = getWidth() / 2.0;
            g2.translate(centro, 0);
            g2.scale(Math.max(0.02, escalaX), 1.0);
            g2.translate(-centro, 0);
            super.paint(g2);
            g2.dispose();
        }
    }

    /**
     * Par encontrado: ilumina las dos cartas con borde amarillo.
     * Llama a quitarMarcaPareja(a, b) para volver al borde normal.
     */
    public void marcarPareja(int indiceA, int indiceB) {
        botonesCartas[indiceA].setBorder(BORDE_PAREJA);
        botonesCartas[indiceB].setBorder(BORDE_PAREJA);
    }

    public void quitarMarcaPareja(int indiceA, int indiceB) {
        botonesCartas[indiceA].setBorder(BORDE_NORMAL);
        botonesCartas[indiceB].setBorder(BORDE_NORMAL);
    }

    public void setParejas(int parejas) {
        lblParejas.setText("Parejas " + parejas + "/" + TOTAL_PAREJAS);
    }

    public void setIntentos(int intentos) {
        lblIntentos.setText("Intentos " + intentos);
    }

    /** Muestra "TU TURNO" cuando le toca a este jugador. */
    public void setTurnoActivo(boolean activo) {
        lblTurno.setText(activo ? "▶ TU TURNO ◀" : " ");
    }

    public String getNombre() {
        return nombre;
    }
}