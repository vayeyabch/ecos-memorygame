package ecos.app.modos.JuegoC;

import ecos.app.Sesion;
import ecos.pantallas.PantallaClasico;
import ecos.recursos.Sonido;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.function.Consumer;
import javax.swing.Timer;

// Controlador temporal del modo clásico: lo justo para que la vista se vea y se sienta como juego.
// La lógica de verdad (modelo) la reemplaza el equipo de lógica.
public class ControladorJuegoClasico {

    private static final double TIEMPO_MAX = 10.0; // segundos por jugada
    private static final double BONUS = 7.0; // segundos que se ganan al acertar
    private static final int PRECIO_OJO = 20;

    private final PantallaClasico vista;
    private final Consumer<String> irA;
    private final Timer reloj = new Timer(100, e -> tick());
    private int n;
    private int[][] caras;
    private int turno;
    private double tiempo;
    private int[] monedas = new int[2];
    private int[] intentos = new int[2];
    private int[] parejas = new int[2];
    private int primera = -1;
    private boolean bloqueado = true;
    private boolean urgente = false;
    private int ultimoSegundo = 99;

    public ControladorJuegoClasico(PantallaClasico vista, Consumer<String> irA) {
        this.vista = vista;
        this.irA = irA;
        vista.setAlClicCarta(this::clic);
        vista.setAlComprarOjo(this::comprarOjo);
        vista.setAlEntrar(this::iniciar);
        vista.setAlSalir(this::detener);
    }

    public void iniciar() {
        n = Sesion.getTamano();
        caras = new int[2][n * n];
        for (int p = 0; p < 2; p++) {
            List<Integer> lista = new ArrayList<>();
            for (int i = 0; i < n * n / 2; i++) {
                lista.add(i % 18);
                lista.add(i % 18);
            }
            Collections.shuffle(lista);
            for (int i = 0; i < lista.size(); i++) {
                caras[p][i] = lista.get(i);
            }
        }
        turno = 0;
        monedas = new int[]{30, 30};
        intentos = new int[2];
        parejas = new int[2];
        primera = -1;
        urgente = false;
        bloqueado = true;
        javax.swing.SwingUtilities.invokeLater(() -> {
            vista.configurar(n, Sesion.getJugador1(), Sesion.getJugador2(), 2, 7, PRECIO_OJO, caras);
            vista.setTurno(0);
            tiempo = TIEMPO_MAX;
            vista.setTiempo(tiempo, TIEMPO_MAX, false);
            for (int p = 0; p < 2; p++) {
                vista.setMonedas(p, monedas[p]);
            }
            Sonido.efecto("cuenta_regresiva");
            Timer espera = new Timer(1700, e -> { bloqueado = false; reloj.start(); });
            espera.setRepeats(false);
            espera.start();
        });
    }

    public void detener() {
        reloj.stop();
    }

    private void tick() {
        if (bloqueado) {
            return;
        }
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
            pasarTurno();
        }
    }

    private void clic(int p, int i) {
        if (bloqueado || p != turno || vista == null) {
            return;
        }
        if (primera == i) {
            return;
        }
        Sonido.efecto("voltear_carta");
        vista.voltearCarta(p, i, true);
        if (primera == -1) {
            primera = i;
            return;
        }
        int a = primera;
        primera = -1;
        intentos[p]++;
        vista.setIntentos(p, intentos[p]);
        if (caras[p][a] == caras[p][i]) {
            acierto(p, a, i);
        } else {
            fallo(p, a, i);
        }
    }

    private void acierto(int p, int a, int b) {
        bloqueado = true;
        Timer t = new Timer(260, e -> {
            Sonido.efecto("par_encontrado");
            vista.marcarPareja(p, a, b);
            parejas[p]++;
            monedas[p] += 5;
            vista.setParejas(p, parejas[p]);
            vista.setMonedas(p, monedas[p]);
            tiempo = Math.min(TIEMPO_MAX, tiempo + BONUS);
            vista.setTiempo(tiempo, TIEMPO_MAX, true);
            vista.mostrarBonus("+7s");
            if (tiempo > 3 && urgente) {
                urgente = false;
                Sonido.musica("clasico");
            }
            if (parejas[p] == n * n / 2) {
                terminar();
            } else {
                bloqueado = false;
            }
        });
        t.setRepeats(false);
        t.start();
    }

    private void fallo(int p, int a, int b) {
        bloqueado = true;
        Timer t = new Timer(750, e -> {
            Sonido.efecto("fallo");
            vista.voltearCarta(p, a, false);
            vista.voltearCarta(p, b, false);
            Timer luego = new Timer(300, x -> pasarTurno());
            luego.setRepeats(false);
            luego.start();
        });
        t.setRepeats(false);
        t.start();
    }

    private void pasarTurno() {
        bloqueado = true;
        int anterior = turno;
        if (primera != -1) {
            vista.voltearCarta(anterior, primera, false);
            primera = -1;
        }
        turno = 1 - turno;
        tiempo = TIEMPO_MAX;
        urgente = false;
        ultimoSegundo = 99;
        Sonido.musica("clasico");
        Sonido.efecto("cambio_turno");
        vista.setTurno(turno);
        vista.setTiempo(tiempo, TIEMPO_MAX, true);
        vista.mostrarBannerTurno(turno);
        Timer t = new Timer(1300, e -> bloqueado = false);
        t.setRepeats(false);
        t.start();
    }

    private void comprarOjo(int p) {
        if (bloqueado || p != turno || primera != -1 || monedas[p] < PRECIO_OJO) {
            Sonido.efecto("sin_monedas");
            return;
        }
        monedas[p] -= PRECIO_OJO;
        vista.setMonedas(p, monedas[p]);
        Sonido.efecto("ojo_espia");
        bloqueado = true;
        vista.ojoEspia(p);
        Timer t = new Timer(2300, e -> bloqueado = false);
        t.setRepeats(false);
        t.start();
    }

    private void terminar() {
        reloj.stop();
        Sonido.musica(null);
        Sonido.efecto("victoria");
        int ganador = intentos[0] < intentos[1] ? 0 : intentos[1] < intentos[0] ? 1 : turno;
        String premio = Sesion.textoPremio(Math.max(1, intentos[ganador]));
        vista.mostrarVictoria(ganador, intentos[0], intentos[1], premio, () -> irA.accept("premios"), () -> irA.accept("menu"));
    }
}
