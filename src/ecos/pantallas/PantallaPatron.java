package ecos.pantallas;

import ecos.app.modos.JuegoC.BotonPixel;
import ecos.app.modos.JuegoC.Paleta;
import ecos.app.modos.JuegoC.Utilidades;
import ecos.app.modos.Patrones.BarraSegmentos;
import java.awt.BasicStroke;
import java.awt.BorderLayout;
import java.awt.Color;
import java.awt.Dimension;
import java.awt.FlowLayout;
import java.awt.Font;
import java.awt.FontFormatException;
import java.awt.FontMetrics;
import java.awt.Graphics;
import java.awt.Graphics2D;
import java.awt.GridBagConstraints;
import java.awt.GridBagLayout;
import java.awt.GridLayout;
import java.awt.Image;
import java.awt.Insets;
import java.io.File;
import java.io.IOException;
import java.util.function.IntConsumer;
import javax.swing.BorderFactory;
import javax.swing.BoxLayout;
import javax.swing.ImageIcon;
import javax.swing.JButton;
import javax.swing.JLabel;
import javax.swing.JPanel;
import javax.swing.Timer;

/**
 * VISTA del modo de patrones, para un jugador (SOLO INTERFAZ).
 *
 * Es una pantalla (extends Pantalla) que se muestra dentro de la Ventana
 * principal: la ventana y los botones de arriba los pone la Ventana /
 * BarraSuperior, no esta clase. El fondo (fondo_montana.gif) sí lo dibuja
 * esta pantalla, en paintComponent.
 *
 * Esta clase NO genera patrones ni decide si algo está bien o mal: solo muestra
 * lo que el controlador le pide y le avisa cuando el usuario toca algo.
 *
 * Métodos para el controlador:
 *   setNivel(nivel, casillas)            -> "Nivel 1 · 3 casillas"
 *   setMensaje(texto)                    -> el mensaje del centro
 *   iluminarCasilla(i)                   -> la prende (para mostrar el patrón)
 *   marcarCorrecta(i) / marcarIncorrecta(i) -> la pinta de verde / rojo
 *   limpiarCasilla(i) / limpiarCasillas()   -> la deja normal
 *   setCasillasActivas(true/false)       -> permite o bloquea los clics
 *   setAlClickCasilla(accion)            -> avisa qué casilla (0 a 15) se pulsó
 *   reiniciarTiempos(seg)                -> deja las dos barras llenas y quietas
 *   iniciarTiempoTotal(seg)              -> barra verde (tiempo total)
 *   iniciarTiempoEntreCasillas(seg)      -> barra roja (tiempo entre casillas)
 *   detenerTiempos()                     -> congela las dos barras
 *   setAlAgotarseTiempoTotal(r) / setAlAgotarseTiempoEntreCasillas(r)
 *   getBtnEmpezar(), setTextoEmpezar(t), setEmpezarHabilitado(b)
 */
public class PantallaPatron extends JPanel {

    public static final int FILAS = 4;
    public static final int COLUMNAS = 4;
    public static final int TOTAL_CASILLAS = FILAS * COLUMNAS;
    private static final int LADO_ICONO = 64;

    // Recursos
    private static final String RUTA_FONDO = "src/recursos/fondos/fondo_montana.gif";
    private static final String RUTA_ICONOS = "src/recursos/Iconos/icono"; // + número + ".png"
    private static final String CARPETA_FUENTES = "src/recursos/fuentes";

    // Tamaños de letra (la fuente PressStart2P es ancha, por eso son chicos)
    private static final float TAM_INFO = 12f;
    private static final float TAM_BOTON = 18f;
    private static final int ANCHO_MENSAJE = 400; // ancho fijo de la caja del centro

    // Animación del rectángulo de nivel (igual que la del turno en el modo clásico)
    private static final int ENTRADA_MS = 600;
    private static final int PAUSA_MS = 1000;
    private static final int SALIDA_MS = 600;
    private static final int INTERVALO_MS = 15;

    private static Font fuenteBase; // PressStart2P, se carga una sola vez

    private final JLabel lblNivel;
    private final JLabel lblMensaje;
    private final JLabel lblTiempo;

    private final BarraSegmentos barraTotal;
    private final BarraSegmentos barraEntre;

    // El tablero es una matriz de 4 x 4: casillas[fila][columna]
    private final CasillaPatron[][] casillas = new CasillaPatron[FILAS][COLUMNAS];
    private final BotonPixel btnEmpezar;
    private final Image fondo = new ImageIcon(RUTA_FONDO).getImage();
    private IntConsumer alClickCasilla;

    private final BotonPixel btnAtras;
    private final JButton btnLuna;
    private final JButton btnSinBarra;

    private final JPanel contenido;   // todo lo normal de la pantalla
    private final JPanel panelAviso;  // capa transparente encima, para el rectángulo de nivel
    private final JLabel lblAviso;    // el rectángulo que se mueve
    private final Timer temporizadorAnimacion;
    private long inicioAnimacion;

    public PantallaPatron() {
        super();

        // Sin layout: acomodamos las dos capas a mano en doLayout()
        setLayout(null);

        Font fuente = fuenteTexto(TAM_INFO);

        // ----- Panel de arriba: nivel, mensaje, tiempo y las dos barras -----
        lblNivel = crearCajaInfo("Nivel 1 · 3 casillas", fuente);
        lblMensaje = crearCajaInfo("Pulsa empezar", fuente);
        lblTiempo = crearCajaInfo("12 s", fuente);

        // La caja del mensaje tiene ancho fijo para que los mensajes largos no se corten
        Dimension tamanoMensaje = lblMensaje.getPreferredSize();
        lblMensaje.setPreferredSize(new Dimension(
                Math.max(tamanoMensaje.width, ANCHO_MENSAJE), tamanoMensaje.height));

        JPanel centroMensaje = new JPanel(new FlowLayout(FlowLayout.CENTER, 0, 0));
        centroMensaje.setOpaque(false);
        centroMensaje.add(lblMensaje);

        JPanel filaInfo = new JPanel(new BorderLayout(12, 0));
        filaInfo.setOpaque(false);
        filaInfo.add(lblNivel, BorderLayout.WEST);
        filaInfo.add(centroMensaje, BorderLayout.CENTER);
        filaInfo.add(lblTiempo, BorderLayout.EAST);

        barraTotal = new BarraSegmentos(new Color(122, 189, 122), 34, true); // verde, 12 s
        barraEntre = new BarraSegmentos(Paleta.ROJO_CARTA, 18, false);       // roja, 2 s
        barraTotal.setAlActualizar(s -> lblTiempo.setText((int) Math.ceil(s) + " s"));
        // Estado inicial (el controlador lo vuelve a poner con reiniciarTiempos): barras llenas
        barraTotal.reiniciar(12);
        barraEntre.reiniciar(2);

        JPanel panelBarras = new JPanel();
        panelBarras.setOpaque(false);
        panelBarras.setLayout(new BoxLayout(panelBarras, BoxLayout.Y_AXIS));
        panelBarras.add(barraTotal);
        panelBarras.add(javax.swing.Box.createVerticalStrut(10));
        panelBarras.add(barraEntre);

        JPanel panelSuperior = crearPanelMarco(new BorderLayout(0, 14));
        panelSuperior.add(filaInfo, BorderLayout.NORTH);
        panelSuperior.add(panelBarras, BorderLayout.CENTER);
        Dimension d = panelSuperior.getPreferredSize();
        panelSuperior.setPreferredSize(new Dimension(Math.max(d.width, 760), d.height));

        // ----- Cuadrícula de 4 x 4 casillas -----
        JPanel panelCuadricula = crearPanelMarco(new GridLayout(FILAS, COLUMNAS, 12, 12));
        for (int i = 0; i < TOTAL_CASILLAS; i++) {
            // Con el for se cargan icono1.png, icono2.png ... icono16.png
            // (los mismos del otro modo), uno por casilla.
            ImageIcon icono = Utilidades.cargarIconoRedimensionado(
                    RUTA_ICONOS + (i + 1) + ".png", LADO_ICONO, LADO_ICONO);
            panelCuadricula.add(crearCasilla(i, icono));
        }

        // ----- Botón Empezar -----
        btnEmpezar = new BotonPixel("Empezar", Paleta.DORADO, Paleta.TEXTO_NARANJA, TAM_BOTON);
        btnEmpezar.setFont(fuenteTexto(TAM_BOTON));

        // ----- Todo junto, centrado -----
        JPanel centro = new JPanel(new GridBagLayout());
        centro.setOpaque(false);
        GridBagConstraints gbc = new GridBagConstraints();
        gbc.gridx = 0;
        gbc.insets = new Insets(0, 0, 16, 0);
        gbc.gridy = 0;
        centro.add(panelSuperior, gbc);
        gbc.gridy = 1;
        centro.add(panelCuadricula, gbc);
        gbc.gridy = 2;
        gbc.insets = new Insets(0, 0, 0, 0);
        centro.add(btnEmpezar, gbc);

        // ----- Botones de arriba (decorativos): Atrás a la izquierda, luna y sin barra a la derecha -----
        btnAtras = new BotonPixel("< Atrás", Paleta.DORADO, Paleta.TEXTO_NARANJA, 14f);
        btnAtras.setPreferredSize(new Dimension(120, 50));
        btnAtras.setBorder(BorderFactory.createEmptyBorder(4, 14, 8, 14));
        btnLuna = BotonPixel.crearLuna();
        btnSinBarra = BotonPixel.crearSinBarra();

        JPanel panelIzq = new JPanel(new FlowLayout(FlowLayout.LEFT, 0, 0));
        panelIzq.setOpaque(false);
        panelIzq.add(btnAtras);
        JPanel panelDer = new JPanel(new FlowLayout(FlowLayout.RIGHT, 10, 0));
        panelDer.setOpaque(false);
        panelDer.add(btnLuna);
        panelDer.add(btnSinBarra);

        JPanel panelBotonesArriba = new JPanel(new BorderLayout());
        panelBotonesArriba.setOpaque(false);
        panelBotonesArriba.setBorder(BorderFactory.createEmptyBorder(15, 25, 0, 25));
        panelBotonesArriba.add(panelIzq, BorderLayout.WEST);
        panelBotonesArriba.add(panelDer, BorderLayout.EAST);

        contenido = new JPanel(new BorderLayout());
        contenido.setOpaque(false);
        contenido.add(panelBotonesArriba, BorderLayout.NORTH);
        contenido.add(centro, BorderLayout.CENTER);

        // ----- Rectángulo de nivel (cruza la pantalla, igual que el de turno del modo clásico) -----
        lblAviso = new JLabel("", JLabel.CENTER);
        lblAviso.setFont(Utilidades.cargarFuentePixel(48f).deriveFont(Font.BOLD));
        lblAviso.setForeground(Paleta.GUINDA);
        lblAviso.setOpaque(true);
        lblAviso.setBackground(Paleta.DORADO);
        lblAviso.setBorder(BorderFactory.createCompoundBorder(
                BorderFactory.createLineBorder(Paleta.GUINDA, 6),
                BorderFactory.createEmptyBorder(25, 70, 25, 70)));

        panelAviso = new JPanel(null);
        panelAviso.setOpaque(false);
        panelAviso.add(lblAviso);
        panelAviso.setVisible(false);

        // La capa del aviso va primero (queda por encima del contenido)
        add(panelAviso);
        add(contenido);

        temporizadorAnimacion = new Timer(INTERVALO_MS, e -> animarAviso());
    }

    /** Las dos capas ocupan toda la pantalla. */
    @Override
    public void doLayout() {
        if (contenido == null || panelAviso == null) {
            return;
        }
        contenido.setBounds(0, 0, getWidth(), getHeight());
        panelAviso.setBounds(0, 0, getWidth(), getHeight());
    }

    // ---------- Rectángulo de nivel ----------

    /** Hace pasar el rectángulo "Nivel N" por la pantalla (entra, se detiene y sale). */
    public void mostrarNivel(int nivel) {
        lblAviso.setText("Nivel " + nivel);

        Dimension tamano = lblAviso.getPreferredSize();
        lblAviso.setSize(tamano);
        lblAviso.setLocation(-tamano.width, (panelAviso.getHeight() - tamano.height) / 2);

        temporizadorAnimacion.stop();
        inicioAnimacion = System.currentTimeMillis();
        panelAviso.setVisible(true);
        temporizadorAnimacion.start();
    }

    /** Un cuadro de la animación: entra con ease-out, pausa en el centro y sale acelerando. */
    private void animarAviso() {
        long transcurrido = System.currentTimeMillis() - inicioAnimacion;
        int anchoPantalla = panelAviso.getWidth();
        int anchoAviso = lblAviso.getWidth();
        int centroX = (anchoPantalla - anchoAviso) / 2;

        int x;
        if (transcurrido < ENTRADA_MS) {
            double p = transcurrido / (double) ENTRADA_MS;
            double suave = 1 - Math.pow(1 - p, 3);
            x = (int) (-anchoAviso + (centroX + anchoAviso) * suave);
        } else if (transcurrido < ENTRADA_MS + PAUSA_MS) {
            x = centroX;
        } else {
            double p = Math.min(1.0, (transcurrido - ENTRADA_MS - PAUSA_MS) / (double) SALIDA_MS);
            x = (int) (centroX + (anchoPantalla - centroX) * Math.pow(p, 3));
        }
        lblAviso.setLocation(x, (panelAviso.getHeight() - lblAviso.getHeight()) / 2);

        if (transcurrido >= ENTRADA_MS + PAUSA_MS + SALIDA_MS) {
            temporizadorAnimacion.stop();
            panelAviso.setVisible(false);
        }
    }

    // ---------- Botones de arriba (no hacen nada, la Ventana/controlador les pone la acción) ----------

    public JButton getBtnAtras() {
        return btnAtras;
    }

    public JButton getBtnLuna() {
        return btnLuna;
    }

    public JButton getBtnSinBarra() {
        return btnSinBarra;
    }

    // ---------- Fondo ----------

    /** Dibuja fondo_montana.gif detrás de todo (también se mueve si el gif es animado). */
    // No @Override here because paint(Graphics) does not override a superclass method
    @Override
    public void paint(Graphics g) {
        if (fondo != null) {
            g.drawImage(fondo, 0, 0, getWidth(), getHeight(), this);
        }
        super.paint(g);
    }

    // ---------- Fuente ----------

    /**
     * Fuente PressStart2P (la que se usa en el diseño). Si no se encuentra el
     * archivo en src/recursos/fuentes, usa la fuente pixel de siempre.
     */
    private static Font fuenteTexto(float tamano) {
        if (fuenteBase == null) {
            File[] archivos = new File(CARPETA_FUENTES).listFiles(
                    (carpeta, nombre) -> nombre.startsWith("PressStart2P") && nombre.endsWith(".ttf"));
            if (archivos != null && archivos.length > 0) {
                try {
                    fuenteBase = Font.createFont(Font.TRUETYPE_FONT, archivos[0]);
                } catch (FontFormatException | IOException e) {
                    fuenteBase = null;
                }
            }
        }
        if (fuenteBase != null) {
            return fuenteBase.deriveFont(tamano);
        }
        return Utilidades.cargarFuentePixel(tamano);
    }

    // ---------- Piezas de la interfaz ----------

    /** Panel crema con marco guinda (más grueso abajo), como el resto del juego. */
    private JPanel crearPanelMarco(java.awt.LayoutManager layout) {
        JPanel panel = new JPanel(layout);
        panel.setBackground(Paleta.CREMA);
        panel.setBorder(BorderFactory.createCompoundBorder(
                BorderFactory.createCompoundBorder(
                        BorderFactory.createMatteBorder(6, 6, 12, 6, Paleta.GUINDA),
                        BorderFactory.createLineBorder(Color.WHITE, 3)),
                BorderFactory.createEmptyBorder(18, 22, 18, 22)));
        return panel;
    }

    /** Caja de información (beige con borde guinda). */
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

    private CasillaPatron crearCasilla(final int indice, ImageIcon icono) {
        CasillaPatron casilla = new CasillaPatron(indice + 1, icono);
        casilla.setEnabled(false); // hasta que el controlador las active
        casilla.addActionListener(e -> {
            if (alClickCasilla != null) {
                alClickCasilla.accept(indice);
            }
        });
        casillas[indice / COLUMNAS][indice % COLUMNAS] = casilla;
        return casilla;
    }

    // ---------- Textos ----------

    public void setNivel(int nivel, int cantidadCasillas) {
        lblNivel.setText("Nivel " + nivel + " · " + cantidadCasillas + " casillas");
    }

    public void setMensaje(String texto) {
        lblMensaje.setText(texto);
    }

    public String getMensaje() {
        return lblMensaje.getText();
    }

    // ---------- Casillas ----------

    /** Casilla de la matriz a partir de su número (0 a 15): fila = i / 4, columna = i % 4. */
    private CasillaPatron casilla(int indice) {
        return casillas[indice / COLUMNAS][indice % COLUMNAS];
    }

    /** Prende una casilla (para mostrar el patrón). */
    public void iluminarCasilla(int indice) {
        casilla(indice).setEstado(Estado.ILUMINADA);
    }

    /** La pinta de verde: el usuario acertó. */
    public void marcarCorrecta(int indice) {
        casilla(indice).setEstado(Estado.CORRECTA);
    }

    /** La pinta de rojo: el usuario falló. */
    public void marcarIncorrecta(int indice) {
        casilla(indice).setEstado(Estado.INCORRECTA);
    }

    public void limpiarCasilla(int indice) {
        casilla(indice).setEstado(Estado.NORMAL);
    }

    public void limpiarCasillas() {
        for (CasillaPatron[] fila : casillas) {
            for (CasillaPatron casilla : fila) {
                casilla.setEstado(Estado.NORMAL);
            }
        }
    }

    /** true: el usuario puede pulsar las casillas. false: se bloquean. */
    public void setCasillasActivas(boolean activas) {
        for (CasillaPatron[] fila : casillas) {
            for (CasillaPatron casilla : fila) {
                casilla.setEnabled(activas);
            }
        }
    }

    /** El controlador se registra aquí para enterarse de qué casilla (0 a 15) pulsó el usuario. */
    public void setAlClickCasilla(IntConsumer accion) {
        this.alClickCasilla = accion;
    }

    // ---------- Tiempos ----------

    /** Deja las dos barras llenas y quietas, y muestra el tiempo total. */
    public void reiniciarTiempos(int segundosTotal) {
        barraTotal.reiniciar(segundosTotal);
        barraEntre.reiniciar(1);
    }

    /** Barra verde: tiempo total para repetir el patrón. */
    public void iniciarTiempoTotal(int segundos) {
        barraTotal.iniciar(segundos);
    }

    /** Barra roja: tiempo máximo entre una casilla y la siguiente. */
    public void iniciarTiempoEntreCasillas(int segundos) {
        barraEntre.iniciar(segundos);
    }

    public void detenerTiempos() {
        barraTotal.detener();
        barraEntre.detener();
    }

    public void setAlAgotarseTiempoTotal(Runnable accion) {
        barraTotal.setAlTerminar(accion);
    }

    public void setAlAgotarseTiempoEntreCasillas(Runnable accion) {
        barraEntre.setAlTerminar(accion);
    }

    // ---------- Botón Empezar ----------

    public JButton getBtnEmpezar() {
        return btnEmpezar;
    }

    public void setTextoEmpezar(String texto) {
        btnEmpezar.setText(texto);
    }

    public void setEmpezarHabilitado(boolean habilitado) {
        btnEmpezar.setEnabled(habilitado);
    }

    // ---------- Casilla (botón con el icono, dibujado estilo pixel) ----------

    private enum Estado { NORMAL, ILUMINADA, CORRECTA, INCORRECTA }

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