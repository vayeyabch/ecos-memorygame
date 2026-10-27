package ecos.app.modos.JuegoC;

import javax.swing.Timer;

/**
 * CONTROLADOR (temporal, solo para probar la vista).
 *
 * Aquí vive lo que NO es interfaz: de quién es el turno y qué pasa cuando el
 * usuario toca un botón. Cuando tengan el modelo real, esta clase es la que
 * hay que reemplazar: le pregunta cosas al modelo y le ordena cosas a la vista.
 *
 * Para arrancar el juego:
 *   new ControladorJuegoClasico(new JuegoClasico()).iniciar();
 */
public class ControladorJuegoClasico {

    private final JuegoClasico vista;
    private int turnoActual = 1;

    public ControladorJuegoClasico(JuegoClasico vista) {
        this.vista = vista;
        conectarVista();
    }

    /** Muestra la ventana y empieza el jugador 1. */
    public void iniciar() {
        vista.setVisible(true);
        vista.mostrarTurno(turnoActual);
    }

    private void conectarVista() {
        // Click en una carta de cualquiera de los dos tableros
        vista.getTablero1().setAlClickCarta(i -> alPulsarCarta(vista.getTablero1(), i));
        vista.getTablero2().setAlClickCarta(i -> alPulsarCarta(vista.getTablero2(), i));

        // Botones de prueba (BORRAR cuando esté el modelo)
        vista.getBtnCambiarTurno().addActionListener(e -> cambiarTurno());
        vista.getBtnVictoria().addActionListener(e -> vista.mostrarVictoria(turnoActual, 21, 27));
        vista.getBtnPar().addActionListener(e -> probarParEncontrado());
    }

    private void cambiarTurno() {
        turnoActual = (turnoActual == 1) ? 2 : 1;
        vista.mostrarTurno(turnoActual);
    }

    /** TODO (modelo): aquí va la lógica real. Por ahora solo voltea la carta de prueba. */
    private void alPulsarCarta(TableroJugador tablero, int indice) {
        tablero.mostrarCarta(indice, indice % TableroJugador.TOTAL_PAREJAS);
    }

    /** Prueba: voltea las cartas 0 y 1 del tablero actual y las ilumina 1.5 s. */
    private void probarParEncontrado() {
        TableroJugador t = (turnoActual == 1) ? vista.getTablero1() : vista.getTablero2();
        t.mostrarCarta(0, 0);
        t.mostrarCarta(1, 0);
        t.marcarPareja(0, 1);
        Timer quitar = new Timer(1500, ev -> t.quitarMarcaPareja(0, 1));
        quitar.setRepeats(false);
        quitar.start();
    }

    public int getTurnoActual() {
        return turnoActual;
    }
}