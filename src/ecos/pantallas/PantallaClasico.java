package ecos.pantallas;

import ecos.app.Pantalla;
import ecos.app.modos.JuegoC.BarraTiempo;
import ecos.app.modos.JuegoC.Paleta;
import ecos.app.modos.JuegoC.TableroJugador;
import ecos.app.modos.JuegoC.Utilidades;
import ecos.app.modos.JuegoC.VentanaVictoria;
import java.awt.BorderLayout;
import java.awt.Dimension;
import java.awt.FlowLayout;
import java.awt.Font;
import java.awt.GridBagLayout;
import java.awt.Window;
import javax.swing.BorderFactory;
import javax.swing.JButton;
import javax.swing.JLabel;
import javax.swing.JPanel;
import javax.swing.SwingUtilities;
import javax.swing.Timer;

/**
 * VISTA del modo clásico para dos jugadores (SOLO INTERFAZ).
 *
 * Ahora es una pantalla (extends Pantalla) que se muestra dentro de la Ventana
 * principal. La ventana, el fondo y los botones de arriba (Atrás, luna, sin
 * barra) los pone la Ventana / BarraSuperior, no esta clase.
 *
 * Esta clase NO guarda el estado del juego ni decide nada: solo muestra lo que
 * el controlador le pide y le avisa cuando el usuario toca algo.
 *
 * Métodos para el controlador:
 *   mostrarTurno(1 o 2)                    -> muestra el tablero de ese jugador, pasa el
 *                                             rectángulo del turno y arranca la barra
 *   mostrarCarta / ocultarCarta            -> voltea o tapa una carta (con sonido)
 *   marcarPareja / quitarMarcaPareja       -> ilumina el par encontrado (con sonido)
 *   mostrarVictoria(ganador, i1, i2)       -> abre la ventana de victoria
 *   getTablero1() / getTablero2()          -> acceso a cada tablero
 *   getBarraTiempo()                       -> acceso a la barra de tiempo
 */
public class PantallaClasico extends Pantalla {

    private static final int ENTRADA_MS = 600;  // cuánto tarda en llegar al centro
    private static final int PAUSA_MS = 1000;   // cuánto se queda quieto en el centro
    private static final int SALIDA_MS = 600;   // cuánto tarda en irse por la derecha
    private static final int DURACION_ANIMACION_MS = ENTRADA_MS + PAUSA_MS + SALIDA_MS;
    private static final int INTERVALO_MS = 15; // cada cuánto se mueve (~60 cuadros/seg)
    private static final int SEGUNDOS_POR_TURNO = 10;   // duración de la barra de tiempo
    private static final double SEGUNDOS_URGENTE = 3.0; // aquí cambia la música

    private final TableroJugador tablero1;
    private final TableroJugador tablero2;
    private final BarraTiempo barraTiempo;

    // Botones temporales de prueba (el controlador les pone la acción). BORRAR al final.
    private final JButton btnCambiarTurno = new JButton("Cambiar turno (prueba)");
    private final JButton btnVictoria = new JButton("Victoria (prueba)");
    private final JButton btnPar = new JButton("Par encontrado (prueba)");

    private final JPanel contenido;    // todo lo normal de la pantalla
    private final JPanel panelAviso;   // capa transparente encima, para el rectángulo de turno
    private final JLabel lblAviso;     // el rectángulo que se mueve
    private final Timer temporizadorAnimacion;
    private long inicioAnimacion;
    private boolean musicaUrgente = false;

    public PantallaClasico() {
        super("clasico", "clasico");

        // Sin layout: acomodamos las dos capas a mano en doLayout()
        setLayout(null);

        tablero1 = new TableroJugador("Jugador 1");
        tablero2 = new TableroJugador("Visitante");

        // ----- Barra de turno y tiempo, de lado a lado, arriba -----
        barraTiempo = new BarraTiempo(SEGUNDOS_POR_TURNO);
        // Cuando quedan 3 s, la música cambia a la versión urgente
        barraTiempo.setAlQuedarSegundos(SEGUNDOS_URGENTE, () -> {
            musicaUrgente = true;
            musica("clasico_urgente");
        });

        JPanel panelBarra = new JPanel(new BorderLayout());
        panelBarra.setOpaque(false);
        panelBarra.setBorder(BorderFactory.createEmptyBorder(15, 25, 0, 25));
        panelBarra.add(barraTiempo, BorderLayout.CENTER);

        // ----- Los dos tableros (solo se ve uno a la vez), centrados -----
        JPanel panelTableros = new JPanel(new FlowLayout(FlowLayout.CENTER, 60, 0));
        panelTableros.setOpaque(false);
        panelTableros.add(tablero1);
        panelTableros.add(tablero2);

        JPanel panelCentro = new JPanel(new GridBagLayout()); // centra los tableros
        panelCentro.setOpaque(false);
        panelCentro.add(panelTableros);

        // ----- Botones temporales de prueba, abajo -----
        JPanel panelBoton = new JPanel(new FlowLayout(FlowLayout.CENTER, 20, 10));
        panelBoton.setOpaque(false);
        panelBoton.add(btnCambiarTurno);
        panelBoton.add(btnVictoria);
        panelBoton.add(btnPar);

        contenido = new JPanel(new BorderLayout());
        contenido.setOpaque(false);
        contenido.add(panelBarra, BorderLayout.NORTH);
        contenido.add(panelCentro, BorderLayout.CENTER);
        contenido.add(panelBoton, BorderLayout.SOUTH);

        // ----- Rectángulo de turno (cruza la pantalla) -----
        Font fuente = Utilidades.cargarFuentePixel(48f).deriveFont(Font.BOLD);

        lblAviso = new JLabel("", JLabel.CENTER);
        lblAviso.setFont(fuente);
        lblAviso.setForeground(Paleta.GUINDA);
        lblAviso.setOpaque(true);
        lblAviso.setBackground(Paleta.DORADO);
        lblAviso.setBorder(BorderFactory.createCompoundBorder(
                BorderFactory.createLineBorder(Paleta.GUINDA, 6),
                BorderFactory.createEmptyBorder(25, 70, 25, 70)));

        // Capa transparente sin layout: así podemos mover el rectángulo a mano
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

    // ---------- Sonido (ajustar aquí si la clase Sonido se llama distinto) ----------

    private static void musica(String nombre) {
        // El módulo de sonido no está disponible en este proyecto.
    }

    private static void efecto(String nombre) {
        // El módulo de sonido no está disponible en este proyecto.
    }

    // ---------- Lo que el controlador le pide a la vista ----------

    /** Muestra el tablero del jugador (1 o 2) y hace pasar el rectángulo de turno. */
    public void mostrarTurno(int jugador) {
        // Si venía sonando la música urgente, regresa a la normal
        if (musicaUrgente) {
            musicaUrgente = false;
            musica("clasico");
        }

        tablero1.setTurnoActivo(jugador == 1);
        tablero2.setTurnoActivo(jugador == 2);

        // Solo se ve el tablero del jugador que tiene el turno
        tablero1.setVisible(jugador == 1);
        tablero2.setVisible(jugador == 2);

        String nombre = (jugador == 1) ? tablero1.getNombre() : tablero2.getNombre();

        // La barra queda llena y quieta mientras pasa el rectángulo
        barraTiempo.reiniciar();

        lblAviso.setText("Turno de: " + nombre);

        // Ajusta el tamaño del rectángulo al texto y lo deja fuera, a la izquierda
        Dimension tamano = lblAviso.getPreferredSize();
        lblAviso.setSize(tamano);
        lblAviso.setLocation(-tamano.width, (panelAviso.getHeight() - tamano.height) / 2);

        temporizadorAnimacion.stop(); // por si todavía se estaba moviendo el anterior
        inicioAnimacion = System.currentTimeMillis();
        panelAviso.setVisible(true);
        temporizadorAnimacion.start();
    }

    /** Voltea una carta del tablero del jugador (con sonido). */
    public void mostrarCarta(int jugador, int indice, int idImagen) {
        efecto("voltear_carta");
        tablero(jugador).mostrarCarta(indice, idImagen);
    }

    /** Vuelve a tapar una carta del tablero del jugador (con sonido). */
    public void ocultarCarta(int jugador, int indice) {
        efecto("voltear_carta");
        tablero(jugador).ocultarCarta(indice);
    }

    /** Par encontrado: ilumina las dos cartas con borde amarillo (con sonido). */
    public void marcarPareja(int jugador, int indiceA, int indiceB) {
        efecto("par_encontrado");
        tablero(jugador).marcarPareja(indiceA, indiceB);
    }

    public void quitarMarcaPareja(int jugador, int indiceA, int indiceB) {
        tablero(jugador).quitarMarcaPareja(indiceA, indiceB);
    }

    /** Abre la ventana de victoria (ganador = 1 o 2). */
    public void mostrarVictoria(int ganador, int intentos1, int intentos2) {
        Window ventana = SwingUtilities.getWindowAncestor(this);
        new VentanaVictoria(ventana, ganador,
                tablero1.getNombre(), intentos1,
                tablero2.getNombre(), intentos2).setVisible(true);
    }

    private TableroJugador tablero(int jugador) {
        return (jugador == 1) ? tablero1 : tablero2;
    }

    /** Se ejecuta en cada "cuadro" de la animación: entra, pausa en el centro y sale. */
    private void animarAviso() {
        long transcurrido = System.currentTimeMillis() - inicioAnimacion;

        int anchoPantalla = panelAviso.getWidth();
        int anchoAviso = lblAviso.getWidth();
        int altoAviso = lblAviso.getHeight();
        int centroX = (anchoPantalla - anchoAviso) / 2;

        int x;
        if (transcurrido < ENTRADA_MS) {
            // Fase 1: entra desde la izquierda y va frenando al llegar al centro
            double p = transcurrido / (double) ENTRADA_MS;
            double suave = 1 - Math.pow(1 - p, 3);
            x = (int) (-anchoAviso + (centroX + anchoAviso) * suave);
        } else if (transcurrido < ENTRADA_MS + PAUSA_MS) {
            // Fase 2: se queda quieto en el centro
            x = centroX;
        } else {
            // Fase 3: arranca despacio y sale acelerando hacia la derecha
            double p = Math.min(1.0, (transcurrido - ENTRADA_MS - PAUSA_MS) / (double) SALIDA_MS);
            double suave = Math.pow(p, 3);
            x = (int) (centroX + (anchoPantalla - centroX) * suave);
        }

        int y = (panelAviso.getHeight() - altoAviso) / 2;
        lblAviso.setLocation(x, y);

        if (transcurrido >= DURACION_ANIMACION_MS) {
            temporizadorAnimacion.stop();
            panelAviso.setVisible(false);
            barraTiempo.iniciar(); // ahora sí empieza a gastarse el tiempo
        }
    }

    // ---------- Getters (para que el controlador conecte acciones) ----------

    public TableroJugador getTablero1() {
        return tablero1;
    }

    public TableroJugador getTablero2() {
        return tablero2;
    }

    public BarraTiempo getBarraTiempo() {
        return barraTiempo;
    }

    public JButton getBtnCambiarTurno() {
        return btnCambiarTurno;
    }

    public JButton getBtnVictoria() {
        return btnVictoria;
    }

    public JButton getBtnPar() {
        return btnPar;
    }
}