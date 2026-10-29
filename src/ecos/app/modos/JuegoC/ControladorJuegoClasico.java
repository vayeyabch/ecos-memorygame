package ecos.app.modos.JuegoC;

import ecos.app.Sesion;
import ecos.pantallas.PantallaClasico;
import ecos.recursos.Sonido;
import java.util.function.Consumer;
import javax.swing.Timer;

// Solo interfaz: las cartas se voltean y la barra de tiempo corre.
// NO detecta parejas: eso lo hace el modelo del equipo de lógica.
// Cuando se acaba el tiempo solo alterna el turno en pantalla (temporal).
public class ControladorJuegoClasico {

    private static final double TIEMPO_MAX = 10.0; // segundos de la barra

    private final PantallaClasico vista;
    private final Timer reloj = new Timer(100, e -> tick());
    private double tiempo = TIEMPO_MAX;
    private boolean urgente = false;
    private int ultimoSegundo = 99;
    private int turno = 0; // solo para alternar la vista cuando se acaba el tiempo
    private final boolean[][] arriba = new boolean[2][36]; // solo para alternar la cara de cada carta

    public ControladorJuegoClasico(PantallaClasico vista, Consumer<String> irA) {
        this.vista = vista;
        vista.setAlClicCarta(this::clic);
        vista.setAlComprarOjo(p -> vista.ojoEspia(p));
        vista.setAlEntrar(this::iniciar);
        vista.setAlSalir(reloj::stop);
    }

    private void iniciar() {
        int n = Sesion.getTamano();
        int[][] caras = new int[2][n * n];
        for (int p = 0; p < 2; p++) {
            for (int i = 0; i < n * n; i++) {
                caras[p][i] = (i / 2) % 18; // caras de ejemplo en orden fijo
                arriba[p][i] = false;
            }
        }
        javax.swing.SwingUtilities.invokeLater(() -> {
            vista.configurar(n, Sesion.getJugador1(), Sesion.getJugador2(), 2, 7, 20, caras);
            vista.setTurno(0);
            vista.setMonedas(0, 30);
            vista.setMonedas(1, 30);
            tiempo = TIEMPO_MAX;
            turno = 0;
            vista.setTiempo(tiempo, TIEMPO_MAX, false);
            Sonido.efecto("cuenta_regresiva");
            reloj.restart();
        });
    }

    // Voltea la carta (si estaba arriba la tapa).
    private void clic(int p, int i) {
        arriba[p][i] = !arriba[p][i];
        Sonido.efecto("voltear_carta");
        vista.voltearCarta(p, i, arriba[p][i]);
    }

    // Solo la barra: baja de 10 a 0 y vuelve a empezar.
    private void tick() {
        tiempo = Math.max(0, tiempo - 0.1);
        vista.setTiempo(tiempo, TIEMPO_MAX, false);
        int s = (int) Math.ceil(tiempo);
        if (tiempo <= 3 && tiempo > 0 && s != ultimoSegundo) {
            ultimoSegundo = s;
            Sonido.efecto("tick");
            if (!urgente) {
                urgente = true;
                Sonido.musica("clasico_urgente");
            }
        }
        if (tiempo <= 0) {
            tiempo = TIEMPO_MAX;
            urgente = false;
            ultimoSegundo = 99;
            Sonido.musica("clasico");
            turno = 1 - turno; // por ahora, al acabarse el tiempo solo se alterna la vista
            Sonido.efecto("cambio_turno");
            vista.setTurno(turno);
            vista.mostrarBannerTurno(turno);
            vista.setTiempo(tiempo, TIEMPO_MAX, true);
        }
    }
}