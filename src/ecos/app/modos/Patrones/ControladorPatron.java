package ecos.app.modos.Patrones;

import ecos.app.Sesion;
import ecos.modelo.CalculoPremio;
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

    private enum Fase { ESPERANDO, MOSTRANDO, JUGANDO, TERMINADO }

    private final PantallaPatron vista; //pantalla que muestra el juego
    private final Consumer<String> irA; //para cambiar de pantalla 
    private final Timer reloj = new Timer(100, e -> tick()); //cada 0.1 s llama a tick() 
    private SecuenciaPatron secuencia = new SecuenciaPatron(); // las reglas del juego y el patrón que hay que repetir
    private Fase fase = Fase.TERMINADO; // la fase actual del juego
    private double total = TIEMPO_TOTAL; // segundos que quedan de los 12
    private double entre = TIEMPO_ENTRE; //  segundos que quedan de los 2 entre casillas
    private boolean entreActivo = false; // la barra delgada corre desde la primera casilla
    private int ultimoSegundo = 99; // último segundo en que sonó el aviso 
    private int partida = 0; // sirve para ignorar temporizadores de una partida anterior

    public ControladorPatron(PantallaPatron vista, Consumer<String> irA) {    // Constructor: guarda la pantalla y le dice qué método llamar en cada evento.
        this.vista = vista;
        this.irA = irA;
        vista.setAlClicCasilla(this::clic);  // al tocar una casilla -> clic()
        vista.setAlEmpezar(this::empezar);  // al pulsar "Empezar" -> empezar()
        vista.setAlEntrar(this::iniciar);  // al entrar a la pantalla -> iniciar()
        vista.setAlSalir(this::salir);   // al salir de la pantalla -> salir()
    }

    // Partida nueva: nivel 1 y todo listo para pulsar "Empezar".
    private void iniciar() {
        partida++;
        reloj.stop();
        secuencia = new SecuenciaPatron();  // vuelve al nivel 1
        fase = Fase.ESPERANDO;
        total = TIEMPO_TOTAL;
        entre = TIEMPO_ENTRE;
        entreActivo = false; // la barra delgada todavía no corre
        ultimoSegundo = 99;
        vista.setNivel(secuencia.getNivel(), secuencia.getCasillas());
        vista.setInfo("Pulsá empezar");
        vista.setTiempoTotal(total, TIEMPO_TOTAL);
        vista.setTiempoEntre(entre, TIEMPO_ENTRE);
        vista.setJugable(false);  // casillas bloqueadas
        vista.setBotonEmpezar(true); // aparece el botón "Empezar"
    }

    private void salir() {  // Al salir de la pantalla se cancela todo lo pendiente.
        partida++; // invalida los temporizadores que quedaban
        reloj.stop();
        fase = Fase.TERMINADO;
    }

    // Crea el patrón del nivel y lo muestra casilla por casilla.
    private void empezar() {
        if (fase != Fase.ESPERANDO) {  // solo se puede empezar si estamos esperando
            return;
        }
        secuencia.nuevaRonda(); // sortea el patrón del nivel
        fase = Fase.MOSTRANDO; // ahora se enseña el patrón
        vista.setBotonEmpezar(false); // desaparece el botón "Empezar"
        vista.setJugable(false); // casillas bloqueadas
        vista.setInfo("Memoriza el patrón");
        vista.setTiempoTotal(TIEMPO_TOTAL, TIEMPO_TOTAL); // la barra grande se llena
        vista.setTiempoEntre(TIEMPO_ENTRE, TIEMPO_ENTRE); // la barra delgada se llena
        SonidosPartidaPatron.empezar();
        despues(500, () -> mostrarPaso(0));
    }

    private void mostrarPaso(int k) { // Enseña la casilla número k del patrón y luego pasa a la siguiente (recursivo con pausas).
        List<Integer> patron = secuencia.getPatron();
        if (k >= patron.size()) { // ya se mostraron todas
            comenzarTurno();
            return;
        }
        int casilla = patron.get(k); // la casilla que toca mostrar
        vista.encender(casilla, true);  // se enciende
        SonidosPartidaPatron.casilla(casilla);   // suena su nota
        despues(ENCENDIDA_MS, () -> {
            vista.encender(casilla, false); // se apaga
            despues(PAUSA_MS, () -> mostrarPaso(k + 1)); // y se pasa a la siguiente
        });
    }

    // Ya se mostró el patrón: ahora le toca al jugador.
    private void comenzarTurno() {
        fase = Fase.JUGANDO; // el jugador puede tocar casillas
        total = TIEMPO_TOTAL; // la barra grande se recarga
        entre = TIEMPO_ENTRE; // la barra delgada se recarga
        entreActivo = false; // la barra delgada todavía no corre   
        ultimoSegundo = 99;    // para que suene el aviso de los últimos 3 segundos
        vista.setInfo("¡Tu turno!"); // mensaje en la pantalla
        vista.setTiempoTotal(total, TIEMPO_TOTAL); // la barra grande se recarga
        vista.setTiempoEntre(entre, TIEMPO_ENTRE); // la barra delgada se recarga
        vista.setJugable(true); // se pueden tocar las casillas
        reloj.restart();  // empiezan a correr los 12 segundos
    }

    // El jugador toca una casilla.
    private void clic(int casilla) {
        if (fase != Fase.JUGANDO) { // fuera de turno se ignora el clic
            return;
        }
        SecuenciaPatron.Resultado resultado = secuencia.pulsar(casilla); // CORRECTA, NIVEL_COMPLETO o INCORRECTA
        if (resultado == SecuenciaPatron.Resultado.INCORRECTA) { // se equivocó: pierde
            SonidosPartidaPatron.fallo();
            terminar("Esa casilla no era la del patrón");
            return;
        }
        SonidosPartidaPatron.casilla(casilla);   // Acertó: suena su nota y se enciende un momento
        vista.encender(casilla, true); // se enciende 
        despues(240, () -> vista.encender(casilla, false)); // se apaga
        entre = TIEMPO_ENTRE; // la barra delgada se recarga con cada acierto
        entreActivo = true;  // desde el primer acierto corre la barra delgada
        vista.setTiempoEntre(entre, TIEMPO_ENTRE); // la barra delgada se recarga
        if (resultado == SecuenciaPatron.Resultado.NIVEL_COMPLETO) {
            nivelSuperado(); // sube el nivel y empieza la ronda siguiente
        }
    }

    // Repitió todo el patrón: sube el nivel y empieza la ronda siguiente.
    private void nivelSuperado() {
        reloj.stop(); // el tiempo se detiene
        fase = Fase.ESPERANDO;
        vista.setJugable(false);
        secuencia.subirNivel(); // un nivel más (patrón con una casilla extra)
        SonidosPartidaPatron.nivelSuperado();
        vista.mostrarNivelSuperado(secuencia.getNivel(), secuencia.getCasillas()); // muestra el nivel y cuántas casillas tiene
        vista.setInfo("¡Muy bien!");
        despues(ANIMACION_NIVEL_MS, this::empezar); // cuando termina el aviso, empieza la siguiente ronda
    }

    // Corre cada 0.1 s: baja las dos barras y avisa los últimos 3 segundos.
    private void tick() {
        if (fase != Fase.JUGANDO) { // el tiempo solo corre mientras juega el usuario
            return;
        }
        total = Math.max(0, total - 0.1); // resta 0.1 s (sin bajar de 0)
        vista.setTiempoTotal(total, TIEMPO_TOTAL);
        if (entreActivo) { // la barra delgada solo baja después del primer acierto
            entre = Math.max(0, entre - 0.1);
            vista.setTiempoEntre(entre, TIEMPO_ENTRE);
        }
        int s = (int) Math.ceil(total); // segundo actual redondeado hacia arriba
         // En los últimos 3 segundos suena un aviso por segundo
        if (total <= 3 && total > 0 && s != ultimoSegundo) {
            ultimoSegundo = s;
            if (s == 3) { // en el segundo 3 suena la cuenta regresiva completa
                SonidosPartidaPatron.cuentaRegresiva();
            } else {
                SonidosPartidaPatron.tick();
            }
        }
        if (total <= 0) { // se acabó el tiempo: pierde
            terminar("Se acabaron los 12 segundos");
        } else if (entreActivo && entre <= 0) { // pasaron 2 s sin tocar casilla
            terminar("Pasaron más de 2 segundos entre casillas");
        }
    }

    // El jugador perdió: suena la derrota y sale la ventana final.
   // motivo = por qué terminó (se muestra en la ventana final

    private void terminar(String motivo) {
        reloj.stop();
        fase = Fase.TERMINADO;
        vista.setJugable(false);
        vista.setInfo("Fin del juego");
        SonidosPartidaPatron.derrota();
        int nivel = secuencia.getNivel(); // nivel en el que perdió
        int casillas = secuencia.getCasillas(); // casillas de ese patrón
        String premio = premioFinal(nivel);  // texto del premio
        // Espera 0.7 s (para que se oiga la derrota) y abre la ventana final
        despues(700, () -> VentanaFinPatron.mostrar(vista, Sesion.getJugador1(), nivel, casillas, motivo, premio,
                () -> irA.accept("premios"), this::iniciar));  // botón "Ver premios" y jugar otra vez
    }

    // Premio con la fórmula del equipo: 1 / intentos x 100 x dólar del día (CalculoPremio).
    // Aquí cada nivel que el jugador intentó cuenta como un intento. Si no superó ni el nivel 1, no hay premio.
    private static String premioFinal(int nivelAlcanzado) {
        if (nivelAlcanzado <= 1) {  // no superó ni el primer nivel
            return "ninguno (¡inténtalo otra vez!)";
        }
        return CalculoPremio.textoPremio(nivelAlcanzado);
    }

    // Hace algo una sola vez después de unos milisegundos (si la partida sigue siendo la misma).
    private void despues(int ms, Runnable accion) {
        final int actual = partida; // guarda en qué partida estamos
        Timer t = new Timer(ms, e -> {
            if (actual == partida) { // solo si sigue siendo la misma partida
                accion.run();
            }
        });
        t.setRepeats(false);
        t.start();
    }
}
