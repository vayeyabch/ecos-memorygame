package ecos.app.modos.Patrones;

import ecos.pantallas.PantallaPatron;
import java.util.ArrayList;
import java.util.List;
import java.util.Random;
import javax.swing.Timer;

/**
 * CONTROLADOR (temporal, NO funcional: solo para probar la vista).
 *
 * No revisa si el usuario acierta ni se equivoca, no hay mensajes de error ni
 * límites de tiempo. Solo muestra lo visual:
 *   - al iniciar / pulsar Empezar pasa el rectángulo "Nivel N"
 *   - luego enseña un patrón de casillas
 *   - las casillas que toca el usuario se iluminan un momento
 * Cuando tengan el modelo real, esta clase es la que hay que reemplazar.
 *
 *   ControladorPatron controlador = new ControladorPatron(pantalla);
 *   controlador.iniciar();
 */
public class ControladorPatron {

    private static final int CASILLAS_INICIALES = 3;
    private static final int SEGUNDOS_TOTAL = 12;
    private static final int ENCENDIDA_MS = 600;
    private static final int PAUSA_MS = 250;
    private static final int ANIMACION_NIVEL_MS = 2300; // lo que tarda en pasar el rectángulo

    private final PantallaPatron vista;
    private final Random azar = new Random();
    private final List<Integer> patron = new ArrayList<>();
    private int nivel = 1;

    public ControladorPatron(PantallaPatron vista) {
        this.vista = vista;
        vista.getBtnEmpezar().addActionListener(e -> mostrarRonda());
        vista.setAlClickCasilla(this::iluminarUnMomento);
    }

    /** Deja la pantalla lista y hace pasar el rectángulo del nivel 1. */
    public void iniciar() {
        nivel = 1;
        vista.setNivel(nivel, CASILLAS_INICIALES);
        vista.setMensaje("Pulsa empezar");
        vista.limpiarCasillas();
        vista.setCasillasActivas(false);
        vista.reiniciarTiempos(SEGUNDOS_TOTAL);
        vista.mostrarNivel(nivel);
    }

    /** Pasa el rectángulo del nivel, enseña el patrón y deja las casillas libres. */
    private void mostrarRonda() {
        int cantidad = CASILLAS_INICIALES + nivel - 1;
        vista.setNivel(nivel, cantidad);
        vista.setMensaje("Memoriza el patrón");
        vista.setEmpezarHabilitado(false);
        vista.setCasillasActivas(false);
        vista.limpiarCasillas();
        vista.mostrarNivel(nivel);

        patron.clear();
        for (int i = 0; i < cantidad; i++) {
            patron.add(azar.nextInt(PantallaPatron.TOTAL_CASILLAS));
        }
        despues(ANIMACION_NIVEL_MS, () -> mostrarPaso(0));
    }

    private void mostrarPaso(int k) {
        if (k >= patron.size()) {
            vista.setMensaje("¡Tu turno!");
            vista.setCasillasActivas(true);
            vista.setEmpezarHabilitado(true);
            nivel++; // el siguiente "Empezar" muestra el nivel siguiente
            return;
        }
        int casilla = patron.get(k);
        vista.iluminarCasilla(casilla);
        despues(ENCENDIDA_MS, () -> {
            vista.limpiarCasilla(casilla);
            despues(PAUSA_MS, () -> mostrarPaso(k + 1));
        });
    }

    private void iluminarUnMomento(int casilla) {
        vista.iluminarCasilla(casilla);
        despues(300, () -> vista.limpiarCasilla(casilla));
    }

    private void despues(int milisegundos, Runnable accion) {
        Timer temporizador = new Timer(milisegundos, e -> accion.run());
        temporizador.setRepeats(false);
        temporizador.start();
    }

    public static void main(String[] args) {
        javax.swing.SwingUtilities.invokeLater(() -> {
            PantallaPatron pantalla = new PantallaPatron();
            ControladorPatron controlador = new ControladorPatron(pantalla);
 
            javax.swing.JFrame ventana = new javax.swing.JFrame("Patrones");
            ventana.setDefaultCloseOperation(javax.swing.JFrame.EXIT_ON_CLOSE);
            ventana.setSize(1100, 1000);
            ventana.setContentPane(pantalla);
            ventana.setLocationRelativeTo(null);
            ventana.setVisible(true);
 
            controlador.iniciar();
        });
    }
}