package ecos.app.modos.Patrones;

import ecos.app.Sesion;
import ecos.pantallas.PantallaPatron;
import ecos.recursos.Sonido;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.function.Consumer;
import javax.swing.Timer;

// Controlador temporal del modo patrones: lo justo para que la vista se vea y se sienta como juego.
// La lógica de verdad (modelo) la reemplaza el equipo de lógica.
public class ControladorPatron {

    private static final double TIEMPO_TOTAL = 12.0; // segundos para repetir el patrón
    private static final double TIEMPO_ENTRE = 2.0; // segundos máximos entre casilla y casilla

    private final PantallaPatron vista;
    private final Consumer<String> irA;
    private final Timer reloj = new Timer(100, e -> tick());
    private List<Integer> patron = new ArrayList<>();
    private int nivel = 1;
    private int avance = 0;
    private double total = TIEMPO_TOTAL;
    private double entre = TIEMPO_ENTRE;
    private boolean jugando = false; // true = es el turno de repetir
    private boolean mostrando = false; // true = se está mostrando el patrón

    public ControladorPatron(PantallaPatron vista, Consumer<String> irA) {
        this.vista = vista;
        this.irA = irA;
        vista.setAlClicCasilla(this::clic);
        vista.setAlEmpezar(this::empezarNivel);
        vista.setAlEntrar(this::reiniciar);
        vista.setAlSalir(this::detener);
    }

    private void reiniciar() {
        detener();
        nivel = 1;
        vista.setNivel(1, 3);
        vista.setInfo("Pulsá empezar");
        vista.setTiempoTotal(TIEMPO_TOTAL, TIEMPO_TOTAL);
        vista.setTiempoEntre(TIEMPO_ENTRE, TIEMPO_ENTRE);
        vista.setJugable(false);
        vista.setBotonEmpezar(true);
    }

    public void detener() {
        reloj.stop();
        jugando = false;
        mostrando = false;
    }

    private void empezarNivel() {
        if (mostrando || jugando) {
            return;
        }
        int casillas = nivel + 2;
        List<Integer> todas = new ArrayList<>();
        for (int i = 0; i < 16; i++) {
            todas.add(i);
        }
        Collections.shuffle(todas);
        patron = new ArrayList<>(todas.subList(0, casillas));
        mostrando = true;
        vista.setBotonEmpezar(false);
        vista.setJugable(false);
        vista.setNivel(nivel, casillas);
        vista.setInfo("Mirá el patrón…");
        Sonido.efecto("empezar_patron");
        mostrar(0);
    }

    // Enciende la casilla k del patrón, la apaga y sigue con la siguiente.
    private void mostrar(int k) {
        if (!mostrando) {
            return;
        }
        if (k >= patron.size()) {
            mostrando = false;
            turnoDelJugador();
            return;
        }
        int casilla = patron.get(k);
        Timer encender = new Timer(k == 0 ? 700 : 220, e -> {
            sonarCasilla(casilla);
            vista.encender(casilla, true);
            Timer apagar = new Timer(520, x -> {
                vista.encender(casilla, false);
                mostrar(k + 1);
            });
            apagar.setRepeats(false);
            apagar.start();
        });
        encender.setRepeats(false);
        encender.start();
    }

    private void turnoDelJugador() {
        avance = 0;
        total = TIEMPO_TOTAL;
        entre = TIEMPO_ENTRE;
        jugando = true;
        vista.setInfo("¡Tu turno!");
        vista.setJugable(true);
        vista.setTiempoTotal(total, TIEMPO_TOTAL);
        vista.setTiempoEntre(entre, TIEMPO_ENTRE);
        reloj.start();
    }

    private void tick() {
        if (!jugando) {
            return;
        }
        total = Math.max(0, total - 0.1);
        entre = Math.max(0, entre - 0.1);
        vista.setTiempoTotal(total, TIEMPO_TOTAL);
        vista.setTiempoEntre(entre, TIEMPO_ENTRE);
        if (total <= 3 && total > 0 && Math.abs(total - Math.round(total)) < 0.05) {
            Sonido.efecto("tick");
        }
        if (total <= 0) {
            terminar("Se acabaron los 12 segundos.");
        } else if (entre <= 0) {
            terminar("Pasaron más de 2 segundos entre casillas.");
        }
    }

    private void clic(int casilla) {
        if (!jugando) {
            return;
        }
        sonarCasilla(casilla);
        vista.encender(casilla, true);
        Timer apagar = new Timer(240, e -> vista.encender(casilla, false));
        apagar.setRepeats(false);
        apagar.start();
        if (casilla != patron.get(avance)) {
            terminar("Esa no era la casilla.");
            return;
        }
        avance++;
        entre = TIEMPO_ENTRE;
        if (avance == patron.size()) {
            nivelSuperado();
        }
    }

    private void nivelSuperado() {
        jugando = false;
        reloj.stop();
        vista.setJugable(false);
        Sesion.setMejorNivel(nivel);
        nivel++;
        Sonido.efecto("nivel_superado");
        vista.mostrarNivelSuperado(nivel, nivel + 2);
        vista.setInfo("¡Bien! Siguiente nivel");
        vista.setBotonEmpezar(true);
    }

    private void terminar(String motivo) {
        jugando = false;
        reloj.stop();
        vista.setJugable(false);
        Sonido.efecto("derrota");
        int superados = nivel - 1;
        vista.mostrarFin(motivo, superados, Sesion.getMejorNivel(), this::reiniciar, () -> irA.accept("menu"));
    }

    private void sonarCasilla(int casilla) {
        Sonido.efecto(String.format("patron_casilla_%02d", casilla + 1));
    }
}
