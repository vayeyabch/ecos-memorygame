package ecos.pantallas;

import ecos.app.Pantalla;
import ecos.app.modos.JuegoC.BotonPixel;
import ecos.app.modos.JuegoC.Paleta;
import ecos.app.modos.JuegoC.Utilidades;
import ecos.app.modos.Patrones.BarraSegmentos;
import ecos.app.modos.Patrones.TableroPatron;
import ecos.recursos.Sonido;
import java.awt.BorderLayout;
import java.awt.Color;
import java.awt.Dimension;
import java.awt.FlowLayout;
import java.awt.Font;
import java.awt.FontFormatException;
import java.awt.Graphics;
import java.awt.GridBagConstraints;
import java.awt.GridBagLayout;
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
public class PantallaPatron extends Pantalla {

    public static final int TOTAL_CASILLAS = TableroPatron.TOTAL_CASILLAS;

    // Recursos
    private static final String RUTA_FONDO = "src/recursos/fondos/fondo_montana.gif";
    private static final String CARPETA_FUENTES = "src/recursos/fuentes";

    // Tamaños de letra (la fuente PressStart2P es ancha, por eso son chicos)
    private static final float TAM_INFO = 12f;
    private static final float TAM_BOTON = 18f;
    private static final int ANCHO_MENSAJE = 400; // ancho fijo de la caja del centro

    private static Font fuenteBase; // PressStart2P, se carga una sola vez

    private final JLabel lblNivel;
    private final JLabel lblMensaje;
    private final JLabel lblTiempo;

    private final BarraSegmentos barraTotal;
    private final BarraSegmentos barraEntre;

    private final TableroPatron tablero = new TableroPatron(); // matriz 4 x 4 de casillas
    private final BotonPixel btnEmpezar;
    private final Image fondo = new ImageIcon(RUTA_FONDO).getImage();

    private final BotonPixel btnAtras;
    private final JButton btnLuna;
    private final JButton btnSinBarra;

    private final JPanel contenido;   // todo lo normal de la pantalla
    private final AvisoDeslizante panelAviso = new AvisoDeslizante(); // rectángulo "Nivel N"

    public PantallaPatron() {
        super("patrones", "patrones");

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

        JPanel panelSuperior = TableroPatron.crearPanelMarco(new BorderLayout(0, 14));
        panelSuperior.add(filaInfo, BorderLayout.NORTH);
        panelSuperior.add(panelBarras, BorderLayout.CENTER);
        Dimension d = panelSuperior.getPreferredSize();
        panelSuperior.setPreferredSize(new Dimension(Math.max(d.width, 760), d.height));

        // ----- Cuadrícula de 4 x 4 casillas: la dibuja TableroPatron -----
        JPanel panelCuadricula = tablero;

        // ----- Botón Empezar -----
        btnEmpezar = new BotonPixel(Paleta.DORADO, "Empezar");
        btnEmpezar.setFont(fuenteTexto(TAM_BOTON));
        btnEmpezar.addActionListener(e -> efecto("click"));

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
        btnAtras = new BotonPixel(Paleta.DORADO, "< Atrás");
        btnAtras.setPreferredSize(new Dimension(120, 50));
        btnAtras.setBorder(BorderFactory.createEmptyBorder(4, 14, 8, 14));
        btnLuna = BotonPixel.crearLuna();
        btnSinBarra = BotonPixel.crearSinBarra();
        btnAtras.addActionListener(e -> efecto("click"));
        btnLuna.addActionListener(e -> efecto("cambio_tema"));

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

        // La capa del aviso va primero (queda por encima del contenido)
        add(panelAviso);
        add(contenido);

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
        efecto(nivel > 1 ? "nivel_superado" : "empezar_patron");
        panelAviso.mostrar("Nivel " + nivel);
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

    // ---------- Sonido (archivos .wav de src/recursos/audio, sin la extensión) ----------

    private static void efecto(String nombre) {
        Sonido.efecto(nombre);
    }

    /** Aviso temporal de nivel, definido aquí para no depender de una clase con paquete incorrecto. */
    private static class AvisoDeslizante extends JPanel {
        private final JLabel etiqueta = new JLabel("", JLabel.CENTER);
        private final javax.swing.Timer temporizador;

        AvisoDeslizante() {
            setOpaque(false);
            setLayout(new GridBagLayout());
            etiqueta.setForeground(Paleta.BEIGE);
            etiqueta.setBackground(Paleta.GUINDA);
            etiqueta.setOpaque(true);
            etiqueta.setBorder(BorderFactory.createCompoundBorder(
                    BorderFactory.createLineBorder(Paleta.DORADO, 4),
                    BorderFactory.createEmptyBorder(16, 28, 16, 28)));
            add(etiqueta);
            setVisible(false);
            temporizador = new javax.swing.Timer(1600, e -> setVisible(false));
            temporizador.setRepeats(false);
        }

        void mostrar(String texto) {
            etiqueta.setText(texto);
            setVisible(true);
            temporizador.restart();
            repaint();
        }
    }

    /** Cada casilla tiene su sonido: casilla 0 -> patron_casilla_01 ... casilla 15 -> patron_casilla_16. */
    private static void efectoCasilla(int indice) {
        efecto(String.format("patron_casilla_%02d", indice + 1));
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

    // ---------- Casillas (las maneja TableroPatron) ----------

    /** Prende una casilla (para mostrar el patrón). */
    public void iluminarCasilla(int indice) {
        efectoCasilla(indice);
        tablero.iluminar(indice);
    }

    /** La pinta de verde: el usuario acertó. */
    public void marcarCorrecta(int indice) {
        efectoCasilla(indice);
        tablero.marcarCorrecta(indice);
    }

    /** La pinta de rojo: el usuario falló. */
    public void marcarIncorrecta(int indice) {
        efecto("fallo");
        tablero.marcarIncorrecta(indice);
    }

    public void limpiarCasilla(int indice) {
        tablero.limpiar(indice);
    }

    public void limpiarCasillas() {
        tablero.limpiarTodas();
    }

    /** true: el usuario puede pulsar las casillas. false: se bloquean. */
    public void setCasillasActivas(boolean activas) {
        tablero.setActivas(activas);
    }

    /** El controlador se registra aquí para enterarse de qué casilla (0 a 15) pulsó el usuario. */
    public void setAlClickCasilla(IntConsumer accion) {
        tablero.setAlClick(accion);
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
}