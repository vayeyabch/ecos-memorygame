package ecos.app.modos.JuegoC;

import ecos.app.Sesion;
import ecos.pantallas.PantallaClasico;
import java.util.function.Consumer;
import javax.swing.Timer;

// Controlador del modo clásico: une el de interfaz (cartas, barra, sonidos) con las reglas del juego.
//  - 10 segundos por jugada; si acierta suma 7 segundos y sigue; si falla pierde el turno.
//  - Gana el primero que encuentra todas las parejas; ahí se abre la ventana de victoria.
public class ControladorJuegoClasico {

    private static final double TIEMPO_MAX = 10.0;
    private static final double BONUS = 7.0;
    private static final int MONEDAS_INICIO = 30;
    private static final int PRECIO_OJO = 20;
    private static final int ESPERA_PAR_MS = 700; // para ver las dos cartas antes de resolver
    private static final String PREMIO = "Premio del modo clásico";

    private final PantallaClasico vista;
    private final Consumer<String> irA;
    private final Timer reloj = new Timer(100, e -> tick());
    private final int[] monedas = new int[2];
    private PartidaClasica modelo;
    private double tiempo = TIEMPO_MAX;
    private boolean esperando = false; // congela clics y tiempo mientras se resuelve un intento
    private boolean urgente = false;
    private int ultimoSegundo = 99;
    private int partida = 0; // sirve para ignorar temporizadores de una partida anterior

    public ControladorJuegoClasico(PantallaClasico vista, Consumer<String> irA) {
        this.vista = vista;
        this.irA = irA;
        vista.setAlClicCarta(this::clic);
        vista.setAlComprarOjo(this::comprarOjo);
        vista.setAlEntrar(this::iniciar);
        vista.setAlSalir(this::salir);
    }

    private void iniciar() {
        partida++;
        int n = Sesion.getTamano();
        modelo = new PartidaClasica(n);
        int[][] caras = new int[2][];
        for (int p = 0; p < 2; p++) {
            caras[p] = MezclaCartasClasico.aplanar(modelo.getCaras(p));
            monedas[p] = MONEDAS_INICIO;
        }
        esperando = false;
        urgente = false;
        ultimoSegundo = 99;
        tiempo = TIEMPO_MAX;
        javax.swing.SwingUtilities.invokeLater(() -> {
            vista.configurar(n, Sesion.getJugador1(), Sesion.getJugador2(), 2, 7, PRECIO_OJO, caras);
            vista.setTurno(0);
            for (int p = 0; p < 2; p++) {
                vista.setMonedas(p, monedas[p]);
                vista.setIntentos(p, 0);
                vista.setParejas(p, 0);
            }
            vista.setTiempo(tiempo, TIEMPO_MAX, false);
            SonidosPartidaClasica.cuentaRegresiva();
            reloj.restart();
        });
    }

    private void salir() {
        partida++;
        reloj.stop();
    }

    // El jugador toca una carta.
    private void clic(int p, int indice) {
        if (modelo == null || esperando || modelo.terminado()) {
            return;
        }
        PartidaClasica.Jugada jugada = modelo.seleccionar(p, indice);
        if (jugada == PartidaClasica.Jugada.IGNORADA) {
            return;
        }
        SonidosPartidaClasica.voltear();
        vista.voltearCarta(p, indice, true);
        if (jugada == PartidaClasica.Jugada.SEGUNDA) {
            esperando = true;
            vista.bloquear(true);
            despues(ESPERA_PAR_MS, () -> resolver(p));
        }
    }

    // Ya están las dos cartas arriba: ¿iguales o diferentes?
    private void resolver(int p) {
        int a = modelo.getCartaA();
        int b = modelo.getCartaB();
        PartidaClasica.Jugada resultado = modelo.resolver();
        vista.setIntentos(p, modelo.getIntentos(p));

        if (resultado == PartidaClasica.Jugada.ACIERTO) {
            vista.marcarPareja(p, a, b);
            vista.setParejas(p, modelo.getParejas(p));
            if (modelo.terminado()) {
                terminar();
                return;
            }
            tiempo += BONUS; // acierto: +7 segundos y sigue jugando
            quitarUrgencia();
            vista.setTiempo(tiempo, TIEMPO_MAX, false);
            vista.mostrarBonus("+" + (int) BONUS + "s");
            esperando = false;
            vista.bloquear(false);
        } else {
            vista.voltearCarta(p, a, false); // fallo: se tapan y pierde el turno
            vista.voltearCarta(p, b, false);
            esperando = false;
            cambiarTurno();
        }
    }

    private void cambiarTurno() {
        boolean cambio = modelo.pasarTurno();
        tiempo = TIEMPO_MAX;
        quitarUrgencia();
        if (cambio) {
            SonidosPartidaClasica.cambioTurno();
            vista.setTurno(modelo.getTurno());
            vista.mostrarBannerTurno(modelo.getTurno());
        } else {
            vista.bloquear(false); // el otro ya terminó: sigue el mismo jugador
        }
        vista.setTiempo(tiempo, TIEMPO_MAX, true);
    }

    // Corre cada 0.1 s: baja la barra y avisa los últimos 3 segundos.
    private void tick() {
        if (modelo == null || esperando) {
            return;
        }
        tiempo = Math.max(0, tiempo - 0.1);
        vista.setTiempo(tiempo, TIEMPO_MAX, false);
        int s = (int) Math.ceil(tiempo);
        if (tiempo <= 3 && tiempo > 0 && s != ultimoSegundo) {
            ultimoSegundo = s;
            SonidosPartidaClasica.tick();
            if (!urgente) {
                urgente = true;
                SonidosPartidaClasica.musicaUrgente();
            }
        }
        if (tiempo <= 0) {
            int carta = modelo.cancelarSeleccion(); // si había una carta volteada, se tapa
            if (carta >= 0) {
                vista.voltearCarta(modelo.getTurno(), carta, false);
            }
            cambiarTurno();
        }
    }

    private void quitarUrgencia() {
        if (urgente) {
            SonidosPartidaClasica.musicaNormal();
        }
        urgente = false;
        ultimoSegundo = 99;
    }

    private void terminar() {
        reloj.stop();
        quitarUrgencia();
        esperando = true;
        despues(900, () -> vista.mostrarVictoria(modelo.ganador(), modelo.getIntentos(0), modelo.getIntentos(1),
                PREMIO, () -> irA.accept("premios"), this::iniciar));
    }

    // Ojo espía: solo se puede comprar si no hay una carta volteada.
    private void comprarOjo(int p) {
        if (modelo == null || esperando || p != modelo.getTurno() || modelo.hayPrimera() || monedas[p] < PRECIO_OJO) {
            return;
        }
        monedas[p] -= PRECIO_OJO;
        vista.setMonedas(p, monedas[p]);
        vista.ojoEspia(p);
        esperando = true; // el tiempo se congela mientras se ven las cartas
        despues(2100, () -> {
            esperando = false;
            vista.bloquear(false);
        });
    }

    // Hace algo una sola vez después de unos milisegundos (si la partida sigue siendo la misma).
    private void despues(int ms, Runnable accion) {
        final int actual = partida;
        Timer t = new Timer(ms, e -> {
            if (actual == partida) {
                accion.run();
            }
        });
        t.setRepeats(false);
        t.start();
    }
}
