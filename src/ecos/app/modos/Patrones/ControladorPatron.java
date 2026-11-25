package ecos.app.modos.Patrones;

// Sesion: da el nombre de quien juega para la ventana final.
import ecos.app.Sesion;
// PantallaPatron: la vista que este controlador maneja.
import ecos.pantallas.PantallaPatron;
// List: tipo de la lista de casillas del patrón.
import java.util.List;
// Consumer: función que recibe un dato; aquí recibe el id de la pantalla a la que ir.
import java.util.function.Consumer;
// Timer (de Swing): reloj que ejecuta algo cada cierto tiempo sin congelar la ventana.
import javax.swing.Timer;

// Controlador del modo patrones. Se muestra un patrón (3 casillas en el nivel 1) y el jugador lo repite en el mismo
// orden, con 12 segundos en total y máximo 2 entre casillas. Si acierta, el siguiente patrón tiene una casilla más; si
// falla, termina el juego.
public class ControladorPatron {

    // Segundos de la barra grande y de la delgada, tiempo que se ve encendida cada casilla y pausa entre casillas.
    private static final double TIEMPO_TOTAL = 12.0;
    private static final double TIEMPO_ENTRE = 2.0;
    private static final int ENCENDIDA_MS = 600;
    private static final int PAUSA_MS = 250;
    // Espera mientras pasa el aviso de nivel.
    private static final int ANIMACION_NIVEL_MS = 1800;

    // Las fases del juego: esperando que se pulse Empezar, mostrando el patrón, jugando el usuario, o terminado.
    private enum Fase { ESPERANDO, MOSTRANDO, JUGANDO, TERMINADO }

    // Pantalla, cómo cambiar de pantalla, reloj que llama a tick() cada 0.1 s y reglas con el patrón a repetir.
    private final PantallaPatron vista;
    private final Consumer<String> irA;
    private final Timer reloj = new Timer(100, e -> tick());
    private SecuenciaPatron secuencia = new SecuenciaPatron();
    // Fase actual y segundos que quedan de los 12 y de los 2 entre casillas. La barra delgada corre desde la primera
    // casilla.
    private Fase fase = Fase.TERMINADO;
    private double total = TIEMPO_TOTAL;
    private double entre = TIEMPO_ENTRE;
    private boolean entreActivo = false;
    // Último segundo en que sonó el aviso y número de partida, para ignorar temporizadores viejos.
    private int ultimoSegundo = 99;
    private int partida = 0;

    // Conecta la vista con el controlador por ganchos (referencias a métodos con ::).
    public ControladorPatron(PantallaPatron vista, Consumer<String> irA) {
        this.vista = vista;
        this.irA = irA;
        vista.setAlClicCasilla(this::clic);
        vista.setAlEmpezar(this::empezar);
        vista.setAlEntrar(this::iniciar);
        vista.setAlSalir(this::salir);
    }

    // Partida nueva: nivel 1 y todo listo para pulsar Empezar.
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
        // Casillas bloqueadas y botón Empezar visible.
        vista.setJugable(false);
        vista.setBotonEmpezar(true);
    }

    // Al salir: invalida temporizadores pendientes, detiene el reloj y marca el juego como terminado.
    private void salir() {
        partida++;
        reloj.stop();
        fase = Fase.TERMINADO;
    }

    // Crea el patrón del nivel y lo muestra casilla por casilla.
    private void empezar() {
        // Solo se puede empezar si se está esperando.
        if (fase != Fase.ESPERANDO) {
            return;
        }
        // Se sortea el patrón, se muestra y mientras tanto no se puede tocar nada.
        secuencia.nuevaRonda();
        fase = Fase.MOSTRANDO;
        vista.setBotonEmpezar(false);
        vista.setJugable(false);
        vista.setInfo("Memoriza el patrón");
        // Las dos barras se llenan.
        vista.setTiempoTotal(TIEMPO_TOTAL, TIEMPO_TOTAL);
        vista.setTiempoEntre(TIEMPO_ENTRE, TIEMPO_ENTRE);
        SonidosPartidaPatron.empezar();
        // Medio segundo después, empieza a mostrar la casilla 0 del patrón.
        despues(500, () -> mostrarPaso(0));
    }

    // Enciende la casilla k del patrón y luego pasa a la siguiente, con pausas.
    private void mostrarPaso(int k) {
        List<Integer> patron = secuencia.getPatron();
        // Si ya se mostraron todas, le toca al jugador.
        if (k >= patron.size()) {
            comenzarTurno();
            return;
        }
        // Enciende la casilla que toca y suena su nota.
        int casilla = patron.get(k);
        vista.encender(casilla, true);
        SonidosPartidaPatron.casilla(casilla);
        // Después de 600 ms la apaga, y 250 ms después muestra la siguiente (recursión por tiempo).
        despues(ENCENDIDA_MS, () -> {
            vista.encender(casilla, false);
            despues(PAUSA_MS, () -> mostrarPaso(k + 1));
        });
    }

    // Ya se mostró el patrón, ahora le toca al jugador.
    private void comenzarTurno() {
        // Las dos barras se recargan y el jugador ya puede tocar.
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

    // El jugador tocó una casilla.
    private void clic(int casilla) {
        // Solo cuenta mientras juega el usuario.
        if (fase != Fase.JUGANDO) {
            return;
        }
        // El modelo compara con la casilla que toca y devuelve el resultado.
        SecuenciaPatron.Resultado resultado = secuencia.pulsar(casilla);
        if (resultado == SecuenciaPatron.Resultado.INCORRECTA) {
            SonidosPartidaPatron.fallo();
            terminar("Esa casilla no era la del patrón");
            return;
        }
        // Acertó: suena su nota y se enciende un momento.
        SonidosPartidaPatron.casilla(casilla);
        vista.encender(casilla, true);
        despues(240, () -> vista.encender(casilla, false));
        entre = TIEMPO_ENTRE;
        // Desde el primer acierto corre la barra delgada, que se recarga con cada acierto.
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
        // Muestra el nivel y cuántas casillas tiene.
        vista.mostrarNivelSuperado(secuencia.getNivel(), secuencia.getCasillas());
        vista.setInfo("¡Muy bien!");
        // Cuando termina el aviso, empieza la ronda siguiente.
        despues(ANIMACION_NIVEL_MS, this::empezar);
    }

    // Corre cada 0.1 s, baja las dos barras y avisa los últimos 3 segundos.
    private void tick() {
        // El tiempo solo corre mientras juega el usuario.
        if (fase != Fase.JUGANDO) {
            return;
        }
        total = Math.max(0, total - 0.1);
        vista.setTiempoTotal(total, TIEMPO_TOTAL);
        // La barra delgada solo baja después del primer acierto.
        if (entreActivo) {
            entre = Math.max(0, entre - 0.1);
            vista.setTiempoEntre(entre, TIEMPO_ENTRE);
        }
        int s = (int) Math.ceil(total);
         // En los últimos 3 segundos suena un aviso por segundo; en el 3 suena la cuenta regresiva completa.
        if (total <= 3 && total > 0 && s != ultimoSegundo) {
            ultimoSegundo = s;
            if (s == 3) {
                SonidosPartidaPatron.cuentaRegresiva();
            } else {
                SonidosPartidaPatron.tick();
            }
        }
        // Se acabó el tiempo.
        if (total <= 0) {
            terminar("Se acabaron los 12 segundos");
        // Pasaron 2 s sin tocar casilla.
        } else if (entreActivo && entre <= 0) {
            terminar("Pasaron más de 2 segundos entre casillas");
        }
    }

    // El jugador perdió: suena la derrota y sale la ventana final con el motivo. Este modo no da premio ni monedas.
    private void terminar(String motivo) {
        reloj.stop();
        fase = Fase.TERMINADO;
        vista.setJugable(false);
        vista.setInfo("Fin del juego");
        SonidosPartidaPatron.derrota();
        final int actual = partida;
        int nivel = secuencia.getNivel();
        int casillas = secuencia.getCasillas();
        // Espera 0.7 s para que se oiga la derrota y abre la ventana final.
        despues(700, () -> {
            if (actual == partida) {
                // Dos botones: uno va al menú y el otro vuelve a jugar (iniciar).
                VentanaFinPatron.mostrar(vista, Sesion.getNombre(), nivel, casillas, motivo,
                        () -> irA.accept("menu"), this::iniciar);
            }
        });
    }

    // Hace algo una sola vez después de unos milisegundos, si la partida sigue siendo la misma.
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