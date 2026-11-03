package ecos.app.modos.Patrones;

import ecos.app.Sesion;
import ecos.pantallas.PantallaPatron;
import java.util.List;
import java.util.function.Consumer;
import javax.swing.Timer;

// Controlador del modo patrones con el juego completo.
//  1. Se muestra un patrón (3 casillas en el nivel 1).
//  2. El jugador lo repite en el mismo orden: 12 s en total y máximo 2 s entre casillas.
//  3. Si acierta, el siguiente patrón tiene 1 casilla más. Si falla, termina el juego.
public class ControladorPatron {

    private static final double TIEMPO_TOTAL = 12.0; // segundos de la barra grande
    private static final double TIEMPO_ENTRE = 2.0; // segundos de la barra delgada
    private static final int ENCENDIDA_MS = 600; // cuánto se ve encendida cada casilla del patrón
    private static final int PAUSA_MS = 250; // pausa entre una casilla y otra
    private static final int ANIMACION_NIVEL_MS = 1800; // espera mientras pasa el aviso de nivel
    private static final int MONEDAS_POR_NIVEL = 5; // premio: monedas por cada nivel superado

    private enum Fase { ESPERANDO, MOSTRANDO, JUGANDO, TERMINADO }

    private final PantallaPatron vista;
    private final Consumer<String> irA;
    private final Timer reloj = new Timer(100, e -> tick());
    private SecuenciaPatron secuencia = new SecuenciaPatron();
    private Fase fase = Fase.TERMINADO;
    private double total = TIEMPO_TOTAL;
    private double entre = TIEMPO_ENTRE;
    private boolean entreActivo = false; // la barra delgada corre desde la primera casilla
    private int ultimoSegundo = 99;
    private int partida = 0; // sirve para ignorar temporizadores de una partida anterior

    public ControladorPatron(PantallaPatron vista, Consumer<String> irA) {
        this.vista = vista;
        this.irA = irA;
        vista.setAlClicCasilla(this::clic);
        vista.setAlEmpezar(this::empezar);
        vista.setAlEntrar(this::iniciar);
        vista.setAlSalir(this::salir);
    }

    // Partida nueva: nivel 1 y todo listo para pulsar "Empezar".
    private void iniciar() {
        partida++;
        reloj.stop();
        secuencia = new SecuenciaPatron();
        fase = Fase.ESPERANDO;
        total = TIEMPO_TOTAL;
        entre = TIEMPO_ENTRE;
        entreActivo = false;
        ultimoSegundo = 99;
        vista.setNivel(secuencia.getNivel(), secuencia.getCasillas());
        vista.setInfo("Pulsá empezar");
        vista.setTiempoTotal(total, TIEMPO_TOTAL);
        vista.setTiempoEntre(entre, TIEMPO_ENTRE);
        vista.setJugable(false);
        vista.setBotonEmpezar(true);
    }

    private void salir() {
        partida++;
        reloj.stop();
        fase = Fase.TERMINADO;
    }

    // Crea el patrón del nivel y lo muestra casilla por casilla.
    private void empezar() {
        if (fase != Fase.ESPERANDO) {
            return;
        }
        secuencia.nuevaRonda();
        fase = Fase.MOSTRANDO;
        vista.setBotonEmpezar(false);
        vista.setJugable(false);
        vista.setInfo("Memoriza el patrón");
        vista.setTiempoTotal(TIEMPO_TOTAL, TIEMPO_TOTAL);
        vista.setTiempoEntre(TIEMPO_ENTRE, TIEMPO_ENTRE);
        SonidosPartidaPatron.empezar();
        despues(500, () -> mostrarPaso(0));
    }

    private void mostrarPaso(int k) {
        List<Integer> patron = secuencia.getPatron();
        if (k >= patron.size()) {
            comenzarTurno();
            return;
        }
        int casilla = patron.get(k);
        vista.encender(casilla, true);
        SonidosPartidaPatron.casilla(casilla);
        despues(ENCENDIDA_MS, () -> {
            vista.encender(casilla, false);
            despues(PAUSA_MS, () -> mostrarPaso(k + 1));
        });
    }

    // Ya se mostró el patrón: ahora le toca al jugador.
    private void comenzarTurno() {
        fase = Fase.JUGANDO;
        total = TIEMPO_TOTAL;
        entre = TIEMPO_ENTRE;
        entreActivo = false;
        ultimoSegundo = 99;
        vista.setInfo("¡Tu turno!");
        vista.setTiempoTotal(total, TIEMPO_TOTAL);
        vista.setTiempoEntre(entre, TIEMPO_ENTRE);
        vista.setJugable(true);
        reloj.restart();
    }

    // El jugador toca una casilla.
    private void clic(int casilla) {
        if (fase != Fase.JUGANDO) {
            return;
        }
        SecuenciaPatron.Resultado resultado = secuencia.pulsar(casilla);
        if (resultado == SecuenciaPatron.Resultado.INCORRECTA) {
            SonidosPartidaPatron.fallo();
            terminar("Esa casilla no era la del patrón");
            return;
        }
        SonidosPartidaPatron.casilla(casilla);
        vista.encender(casilla, true);
        despues(240, () -> vista.encender(casilla, false));
        entre = TIEMPO_ENTRE; // la barra delgada se recarga con cada acierto
        entreActivo = true;
        vista.setTiempoEntre(entre, TIEMPO_ENTRE);
        if (resultado == SecuenciaPatron.Resultado.NIVEL_COMPLETO) {
            nivelSuperado();
        }
    }

    // Repitió todo el patrón: sube el nivel y empieza la ronda siguiente.
    private void nivelSuperado() {
        reloj.stop();
        fase = Fase.ESPERANDO;
        vista.setJugable(false);
        secuencia.subirNivel();
        SonidosPartidaPatron.nivelSuperado();
        vista.mostrarNivelSuperado(secuencia.getNivel(), secuencia.getCasillas());
        vista.setInfo("¡Muy bien!");
        despues(ANIMACION_NIVEL_MS, this::empezar);
    }

    // Corre cada 0.1 s: baja las dos barras y avisa los últimos 3 segundos.
    private void tick() {
        if (fase != Fase.JUGANDO) {
            return;
        }
        total = Math.max(0, total - 0.1);
        vista.setTiempoTotal(total, TIEMPO_TOTAL);
        if (entreActivo) {
            entre = Math.max(0, entre - 0.1);
            vista.setTiempoEntre(entre, TIEMPO_ENTRE);
        }
        int s = (int) Math.ceil(total);
        if (total <= 3 && total > 0 && s != ultimoSegundo) {
            ultimoSegundo = s;
            if (s == 3) {
                SonidosPartidaPatron.cuentaRegresiva();
            } else {
                SonidosPartidaPatron.tick();
            }
        }
        if (total <= 0) {
            terminar("Se acabaron los 12 segundos");
        } else if (entreActivo && entre <= 0) {
            terminar("Pasaron más de 2 segundos entre casillas");
        }
    }

    // El jugador perdió: suena la derrota y sale la ventana final.
    private void terminar(String motivo) {
        reloj.stop();
        fase = Fase.TERMINADO;
        vista.setJugable(false);
        vista.setInfo("Fin del juego");
        SonidosPartidaPatron.derrota();
        int nivel = secuencia.getNivel();
        int casillas = secuencia.getCasillas();
        String premio = premioPorNivel(nivel - 1);
        despues(700, () -> VentanaFinPatron.mostrar(vista, Sesion.getJugador1(), nivel, casillas, motivo, premio,
                () -> irA.accept("premios"), this::iniciar));
    }

    private static String premioPorNivel(int nivelesSuperados) {
        if (nivelesSuperados <= 0) {
            return "ninguno (¡inténtalo otra vez!)";
        }
        return nivelesSuperados * MONEDAS_POR_NIVEL + " monedas";
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
