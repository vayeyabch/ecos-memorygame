package ecos.app.modos.Patrones;

// Clases del propio proyecto que se usan aquí.
import ecos.app.Sesion;
import ecos.modelo.CalculoPremio;
import ecos.pantallas.PantallaPatron;
// Utilidades de Java, List para las listas.
import java.util.List;
// Acciones de Java, Consumer para una acción que recibe un dato.
import java.util.function.Consumer;
// Librería Swing, Timer para los relojes.
import javax.swing.Timer;

// Controlador del modo patrones con el juego completo.
// Primero se muestra un patrón (3 casillas en el nivel 1) y el jugador lo repite en el mismo orden, con 12 segundos en total
// y máximo 2 entre casillas. Si acierta, el siguiente patrón tiene una casilla más, y si falla, termina el juego.
public class ControladorPatron {

    // Segundos de la barra grande y de la delgada, cuánto se ve encendida cada casilla del patrón y la pausa entre una
    // casilla y otra.
    private static final double TIEMPO_TOTAL = 12.0;
    private static final double TIEMPO_ENTRE = 2.0;
    private static final int ENCENDIDA_MS = 600;
    private static final int PAUSA_MS = 250;
    // Espera mientras pasa el aviso de nivel.
    private static final int ANIMACION_NIVEL_MS = 1800;

    private enum Fase { ESPERANDO, MOSTRANDO, JUGANDO, TERMINADO }

    // La pantalla que muestra el juego, el modo de cambiar de pantalla, el reloj que llama a tick() cada 0.1 s y las reglas
    // con el patrón que hay que repetir.
    private final PantallaPatron vista;
    private final Consumer<String> irA;
    private final Timer reloj = new Timer(100, e -> tick());
    private SecuenciaPatron secuencia = new SecuenciaPatron();
    // La fase actual del juego y los segundos que quedan de los 12 y de los 2 entre casillas. La barra delgada corre desde
    // la primera casilla.
    private Fase fase = Fase.TERMINADO;
    private double total = TIEMPO_TOTAL;
    private double entre = TIEMPO_ENTRE;
    private boolean entreActivo = false;
    // Último segundo en que sonó el aviso. Sirve para ignorar temporizadores de una partida anterior.
    private int ultimoSegundo = 99;
    private int partida = 0;

    // Constructor que guarda la pantalla y le dice qué método llamar en cada evento.
    public ControladorPatron(PantallaPatron vista, Consumer<String> irA) {
        this.vista = vista;
        this.irA = irA;
        // Al tocar una casilla se llama clic(), al pulsar Empezar empezar(), al entrar a la pantalla iniciar() y al salir salir().
        vista.setAlClicCasilla(this::clic);
        vista.setAlEmpezar(this::empezar);
        vista.setAlEntrar(this::iniciar);
        vista.setAlSalir(this::salir);
    }

    // Partida nueva, nivel 1 y todo listo para pulsar "Empezar".
    private void iniciar() {
        partida++;
        reloj.stop();
        // Vuelve al nivel 1.
        secuencia = new SecuenciaPatron();
        fase = Fase.ESPERANDO;
        total = TIEMPO_TOTAL;
        entre = TIEMPO_ENTRE;
        // La barra delgada todavía no corre.
        entreActivo = false;
        ultimoSegundo = 99;
        vista.setNivel(secuencia.getNivel(), secuencia.getCasillas());
        vista.setInfo("Pulsá empezar");
        vista.setTiempoTotal(total, TIEMPO_TOTAL);
        vista.setTiempoEntre(entre, TIEMPO_ENTRE);
        // Casillas bloqueadas. aparece el botón "Empezar".
        vista.setJugable(false);
        vista.setBotonEmpezar(true);
    }

    // Al salir de la pantalla se cancela todo lo pendiente.
    private void salir() {
        // Invalida los temporizadores que quedaban.
        partida++;
        reloj.stop();
        fase = Fase.TERMINADO;
    }

    // Crea el patrón del nivel y lo muestra casilla por casilla.
    private void empezar() {
        // Solo se puede empezar si estamos esperando.
        if (fase != Fase.ESPERANDO) {
            return;
        }
        // Sortea el patrón del nivel. Ahora se enseña el patrón. Desaparece el botón "Empezar". casillas bloqueadas.
        secuencia.nuevaRonda();
        fase = Fase.MOSTRANDO;
        vista.setBotonEmpezar(false);
        vista.setJugable(false);
        vista.setInfo("Memoriza el patrón");
        // La barra grande se llena. La barra delgada se llena.
        vista.setTiempoTotal(TIEMPO_TOTAL, TIEMPO_TOTAL);
        vista.setTiempoEntre(TIEMPO_ENTRE, TIEMPO_ENTRE);
        SonidosPartidaPatron.empezar();
        despues(500, () -> mostrarPaso(0));
    }

    // Enseña la casilla número k del patrón y luego pasa a la siguiente (recursivo con pausas).
    private void mostrarPaso(int k) {
        List<Integer> patron = secuencia.getPatron();
        // Ya se mostraron todas.
        if (k >= patron.size()) {
            comenzarTurno();
            return;
        }
        // La casilla que toca mostrar. Suena su nota.
        int casilla = patron.get(k);
        vista.encender(casilla, true);
        SonidosPartidaPatron.casilla(casilla);
        despues(ENCENDIDA_MS, () -> {
            // Y se pasa a la siguiente.
            vista.encender(casilla, false);
            despues(PAUSA_MS, () -> mostrarPaso(k + 1));
        });
    }

    // Ya se mostró el patrón, ahora le toca al jugador.
    private void comenzarTurno() {
        // El jugador puede tocar casillas. la barra grande se recarga. La barra delgada se recarga. La barra delgada todavía no corre.
        fase = Fase.JUGANDO;
        total = TIEMPO_TOTAL;
        entre = TIEMPO_ENTRE;
        entreActivo = false;
        // Para que suene el aviso de los últimos 3 segundos. mensaje en la pantalla. La barra grande se recarga. La barra
        // delgada se recarga.
        ultimoSegundo = 99;
        vista.setInfo("¡Tu turno!");
        vista.setTiempoTotal(total, TIEMPO_TOTAL);
        vista.setTiempoEntre(entre, TIEMPO_ENTRE);
        // Se pueden tocar las casillas. empiezan a correr los 12 segundos.
        vista.setJugable(true);
        reloj.restart();
    }

    // El jugador toca una casilla.
    private void clic(int casilla) {
        // Fuera de turno se ignora el clic.
        if (fase != Fase.JUGANDO) {
            return;
        }
        // CORRECTA, NIVEL_COMPLETO o INCORRECTA. Se equivocó, pierde.
        SecuenciaPatron.Resultado resultado = secuencia.pulsar(casilla);
        if (resultado == SecuenciaPatron.Resultado.INCORRECTA) {
            SonidosPartidaPatron.fallo();
            terminar("Esa casilla no era la del patrón");
            return;
        }
        // Acertó, suena su nota y se enciende un momento. La barra delgada se recarga con cada acierto.
        SonidosPartidaPatron.casilla(casilla);
        vista.encender(casilla, true);
        despues(240, () -> vista.encender(casilla, false));
        entre = TIEMPO_ENTRE;
        // Desde el primer acierto corre la barra delgada. La barra delgada se recarga.
        entreActivo = true;
        vista.setTiempoEntre(entre, TIEMPO_ENTRE);
        if (resultado == SecuenciaPatron.Resultado.NIVEL_COMPLETO) {
            // Sube el nivel y empieza la ronda siguiente.
            nivelSuperado();
        }
    }

    // Repitió todo el patrón, sube el nivel y empieza la ronda siguiente.
    private void nivelSuperado() {
        // El tiempo se detiene.
        reloj.stop();
        fase = Fase.ESPERANDO;
        vista.setJugable(false);
        // Un nivel más (patrón con una casilla extra).
        secuencia.subirNivel();
        SonidosPartidaPatron.nivelSuperado();
        // Muestra el nivel y cuántas casillas tiene.
        vista.mostrarNivelSuperado(secuencia.getNivel(), secuencia.getCasillas());
        vista.setInfo("¡Muy bien!");
        // Cuando termina el aviso, empieza la siguiente ronda.
        despues(ANIMACION_NIVEL_MS, this::empezar);
    }

    // Corre cada 0.1 s, baja las dos barras y avisa los últimos 3 segundos.
    private void tick() {
        // El tiempo solo corre mientras juega el usuario.
        if (fase != Fase.JUGANDO) {
            return;
        }
        // Resta 0.1 s (sin bajar de 0).
        total = Math.max(0, total - 0.1);
        vista.setTiempoTotal(total, TIEMPO_TOTAL);
        // La barra delgada solo baja después del primer acierto.
        if (entreActivo) {
            entre = Math.max(0, entre - 0.1);
            vista.setTiempoEntre(entre, TIEMPO_ENTRE);
        }
        // Segundo actual redondeado hacia arriba.
        int s = (int) Math.ceil(total);
         // En los últimos 3 segundos suena un aviso por segundo
        if (total <= 3 && total > 0 && s != ultimoSegundo) {
            ultimoSegundo = s;
            // En el segundo 3 suena la cuenta regresiva completa.
            if (s == 3) {
                SonidosPartidaPatron.cuentaRegresiva();
            } else {
                SonidosPartidaPatron.tick();
            }
        }
        // Se acabó el tiempo, pierde.
        if (total <= 0) {
            terminar("Se acabaron los 12 segundos");
        // Pasaron 2 s sin tocar casilla.
        } else if (entreActivo && entre <= 0) {
            terminar("Pasaron más de 2 segundos entre casillas");
        }
    }

    // El jugador perdió, suena la derrota y sale la ventana final.
   // motivo = por qué terminó (se muestra en la ventana final

    private void terminar(String motivo) {
        reloj.stop();
        fase = Fase.TERMINADO;
        vista.setJugable(false);
        vista.setInfo("Fin del juego");
        SonidosPartidaPatron.derrota();
        // Nivel en el que perdió, casillas de ese patrón y texto del premio.
        int nivel = secuencia.getNivel();
        int casillas = secuencia.getCasillas();
        String premio = premioFinal(nivel);
        // Espera 0.7 s (para que se oiga la derrota) y abre la ventana final
        despues(700, () -> VentanaFinPatron.mostrar(vista, Sesion.getJugador1(), nivel, casillas, motivo, premio,
                // Botón "Ver premios" y jugar otra vez.
                () -> irA.accept("premios"), this::iniciar));
    }

    // Premio con la fórmula del equipo, 1 / intentos x 100 x dólar del día (CalculoPremio).
    // Aquí cada nivel que el jugador intentó cuenta como un intento. Si no superó ni el nivel 1, no hay premio.
    private static String premioFinal(int nivelAlcanzado) {
        // No superó ni el primer nivel.
        if (nivelAlcanzado <= 1) {
            return "ninguno (¡inténtalo otra vez!)";
        }
        return CalculoPremio.textoPremio(nivelAlcanzado);
    }

    // Hace algo una sola vez después de unos milisegundos (si la partida sigue siendo la misma).
    private void despues(int ms, Runnable accion) {
        // Guarda en qué partida estamos.
        final int actual = partida;
        Timer t = new Timer(ms, e -> {
            // Solo si sigue siendo la misma partida.
            if (actual == partida) {
                accion.run();
            }
        });
        t.setRepeats(false);
        t.start();
    }
}
