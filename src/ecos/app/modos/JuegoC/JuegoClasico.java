package ecos.app.modos.JuegoC;

import java.awt.BorderLayout;
import java.awt.Dimension;
import java.awt.FlowLayout;
import java.awt.Font;
import java.awt.GridBagLayout;
import javax.swing.BorderFactory;
import javax.swing.JButton;
import javax.swing.JFrame;
import javax.swing.JLabel;
import javax.swing.JPanel;
import javax.swing.Timer;

/**
 * Ventana del modo clásico para dos jugadores (SOLO INTERFAZ).
 *
 * Para la lógica:
 *   setTurno(1) o setTurno(2) muestra solo el tablero de ese jugador, hace
 *   pasar el rectángulo del turno y, al terminar, arranca la barra de tiempo.
 *   getTablero1() / getTablero2() dan acceso a cada tablero.
 */
public class JuegoClasico extends JFrame {

    private static final int ENTRADA_MS = 600;  // cuánto tarda en llegar al centro
    private static final int PAUSA_MS = 1000;   // cuánto se queda quieto en el centro
    private static final int SALIDA_MS = 600;   // cuánto tarda en irse por la derecha
    private static final int DURACION_ANIMACION_MS = ENTRADA_MS + PAUSA_MS + SALIDA_MS;
    private static final int INTERVALO_MS = 15; // cada cuánto se mueve (~60 cuadros/seg)
    private static final int SEGUNDOS_POR_TURNO = 10; // duración de la barra de tiempo

    private final TableroJugador tablero1;
    private final TableroJugador tablero2;
    private final BarraTiempo barraTiempo;

    private final JPanel panelAviso;   // capa transparente que cubre la ventana
    private final JLabel lblAviso;     // el rectángulo que se mueve
    private final Timer temporizadorAnimacion;
    private long inicioAnimacion;

    private int turnoActual = 1;

    public JuegoClasico() {
        setTitle("Modo Clasico - 2 Jugadores");
        setSize(1840, 1000);
        setDefaultCloseOperation(JFrame.DISPOSE_ON_CLOSE);

        PanelFondo panelPrincipal = new PanelFondo("src/recursos/fondos/fondo_montana.gif");
        panelPrincipal.setLayout(new BorderLayout());

        tablero1 = new TableroJugador("Jugador 1");
        tablero2 = new TableroJugador("Visitante");

        // ----- Barra de turno y tiempo, de lado a lado, arriba -----
        barraTiempo = new BarraTiempo(SEGUNDOS_POR_TURNO);
        // barraTiempo.setAvatar("src/recursos/Iconos/avatar.png"); // opcional: imagen a la izquierda

        JPanel panelBarra = new JPanel(new BorderLayout());
        panelBarra.setOpaque(false);
        panelBarra.setBorder(BorderFactory.createEmptyBorder(15, 25, 0, 25));
        panelBarra.add(barraTiempo, BorderLayout.CENTER);
        panelPrincipal.add(panelBarra, BorderLayout.NORTH);

        // ----- Los dos tableros (solo se ve uno a la vez), centrados -----
        JPanel panelTableros = new JPanel(new FlowLayout(FlowLayout.CENTER, 60, 0));
        panelTableros.setOpaque(false);
        panelTableros.add(tablero1);
        panelTableros.add(tablero2);

        JPanel panelCentro = new JPanel(new GridBagLayout()); // centra los tableros
        panelCentro.setOpaque(false);
        panelCentro.add(panelTableros);
        panelPrincipal.add(panelCentro, BorderLayout.CENTER);

        // BOTÓN TEMPORAL para probar el cambio de turno. BORRAR cuando esté la lógica.
        JButton btnPrueba = new JButton("Cambiar turno (prueba)");
        btnPrueba.addActionListener(e -> setTurno(turnoActual == 1 ? 2 : 1));

        JPanel panelBoton = new JPanel(new FlowLayout(FlowLayout.CENTER, 20, 10));
        panelBoton.setOpaque(false);
        panelBoton.add(btnPrueba);
        panelPrincipal.add(panelBoton, BorderLayout.SOUTH);

        add(panelPrincipal);

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
        setGlassPane(panelAviso);

        temporizadorAnimacion = new Timer(INTERVALO_MS, e -> animarAviso());

        setLocationRelativeTo(null);
        setVisible(true);
        actualizarTurno(1); // empieza el jugador 1
    }

    /** Cambia el turno (1 o 2) y hace pasar el rectángulo por la pantalla. */
    public void setTurno(int jugador) {
        actualizarTurno(jugador);
    }

    private void actualizarTurno(int jugador) {
        turnoActual = jugador;
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

    public int getTurnoActual() {
        return turnoActual;
    }

    public TableroJugador getTablero1() {
        return tablero1;
    }

    public TableroJugador getTablero2() {
        return tablero2;
    }

    public BarraTiempo getBarraTiempo() {
        return barraTiempo;
    }
}