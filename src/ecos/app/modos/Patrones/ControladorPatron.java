package ecos.app.modos.Patrones;

import ecos.pantallas.PantallaPatron;
import ecos.recursos.Sonido;
import java.util.function.Consumer;
import javax.swing.Timer;

// Solo interfaz: las casillas se encienden al tocarlas y las barras de tiempo corren.
// NO genera patrones ni revisa si está bien: eso lo hace el modelo del equipo de lógica.
// Cuando se acaba el tiempo solo muestra la animación de subir de nivel (temporal).
public class ControladorPatron {

    private static final double TIEMPO_TOTAL = 12.0; // segundos de la barra grande
    private static final double TIEMPO_ENTRE = 2.0; // segundos de la barra delgada

    private final PantallaPatron vista;
    private final Timer reloj = new Timer(100, e -> tick());
    private double total = TIEMPO_TOTAL;
    private double entre = TIEMPO_ENTRE;
    private int nivel = 1; // solo para mostrar el número en pantalla

    public ControladorPatron(PantallaPatron vista, Consumer<String> irA) {
        this.vista = vista;
        vista.setAlClicCasilla(this::clic);
        vista.setAlEmpezar(this::empezar);
        vista.setAlEntrar(() -> { nivel = 1; reiniciar(); });
        vista.setAlSalir(reloj::stop);
    }

    private void reiniciar() {
        reloj.stop();
        total = TIEMPO_TOTAL;
        entre = TIEMPO_ENTRE;
        vista.setNivel(nivel, nivel + 2);
        vista.setInfo("Pulsá empezar");
        vista.setTiempoTotal(total, TIEMPO_TOTAL);
        vista.setTiempoEntre(entre, TIEMPO_ENTRE);
        vista.setJugable(false);
        vista.setBotonEmpezar(true);
    }

    // Arranca las barras para que se vean funcionando.
    private void empezar() {
        total = TIEMPO_TOTAL;
        entre = TIEMPO_ENTRE;
        vista.setInfo("¡Tu turno!");
        vista.setJugable(true);
        vista.setBotonEmpezar(false);
        Sonido.efecto("empezar_patron");
        reloj.restart();
    }

    // Al tocar una casilla se enciende un momento y suena su nota; la barra delgada se recarga.
    private void clic(int casilla) {
        Sonido.efecto(String.format("patron_casilla_%02d", casilla + 1));
        vista.encender(casilla, true);
        Timer apagar = new Timer(240, e -> vista.encender(casilla, false));
        apagar.setRepeats(false);
        apagar.start();
        entre = TIEMPO_ENTRE;
    }

    private void tick() {
        total = Math.max(0, total - 0.1);
        entre = Math.max(0, entre - 0.1);
        vista.setTiempoTotal(total, TIEMPO_TOTAL);
        vista.setTiempoEntre(entre, TIEMPO_ENTRE);
        if (total <= 0) {
            nivel++; // por ahora, al acabarse el tiempo solo se muestra la animación de nivel
            Sonido.efecto("nivel_superado");
            vista.mostrarNivelSuperado(nivel, nivel + 2);
            reiniciar();
        }
    }
}