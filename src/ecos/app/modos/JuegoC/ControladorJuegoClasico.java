package ecos.app.modos.JuegoC;

import ecos.pantallas.PantallaClasico;
import javax.swing.Timer;

/**
 * CONTROLADOR (temporal, solo para probar la vista).
 *
 * Aquí vive lo que NO es interfaz: de quién es el turno y qué pasa cuando el
 * usuario toca un botón. Cuando tengan el modelo real, esta clase es la que
 * hay que reemplazar: le pregunta cosas al modelo y le ordena cosas a la vista.
 *
 * La pantalla ya no es una ventana, así que el controlador NO la muestra:
 * eso lo hace el Navegador. Solo se crea con la pantalla y se llama a iniciar()
 * cuando el juego deba empezar:
 *   ControladorJuegoClasico controlador = new ControladorJuegoClasico(pantalla);
 *   controlador.iniciar();
 */
public class ControladorJuegoClasico {

    private final PantallaClasico vista;
    private int turnoActual = 1;

    public ControladorJuegoClasico(PantallaClasico vista) {
        this.vista = vista;
        conectarVista();
    }

    /** Empieza el jugador 1 (rectángulo de turno + barra de tiempo). */
    public void iniciar() {
        turnoActual = 1;
        vista.mostrarTurno(turnoActual);
    }

    private void conectarVista() {
        // Click en una carta de cualquiera de los dos tableros
        vista.getTablero1().setAlClickCarta(i -> alPulsarCarta(1, i));
        vista.getTablero2().setAlClickCarta(i -> alPulsarCarta(2, i));

        // Botones de prueba (BORRAR cuando esté el modelo)
        vista.getBtnCambiarTurno().addActionListener(e -> cambiarTurno());
        vista.getBtnVictoria().addActionListener(e -> vista.mostrarVictoria(turnoActual, 21, 27));
        vista.getBtnPar().addActionListener(e -> probarParEncontrado());
    }

    private void cambiarTurno() {
        turnoActual = (turnoActual == 1) ? 2 : 1;
        vista.mostrarTurno(turnoActual);
    }

    // TODO (modelo): aquí va la lógica real. Por ahora solo voltea la carta de prueba. 
    private void alPulsarCarta(int jugador, int indice) {
        vista.mostrarCarta(jugador, indice, indice % TableroJugador.TOTAL_PAREJAS);
    }

    /** Prueba: voltea las cartas 0 y 1 del tablero actual y las ilumina 1.5 s. */
    private void probarParEncontrado() {
        int jugador = turnoActual;
        vista.mostrarCarta(jugador, 0, 0);
        vista.mostrarCarta(jugador, 1, 0);
        vista.marcarPareja(jugador, 0, 1);
        Timer quitar = new Timer(1500, ev -> vista.quitarMarcaPareja(jugador, 0, 1));
        quitar.setRepeats(false);
        quitar.start();
    }

    public int getTurnoActual() {
        return turnoActual;
    }
}