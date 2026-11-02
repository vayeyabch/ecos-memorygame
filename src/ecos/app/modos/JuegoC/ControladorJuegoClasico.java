package ecos.app.modos.JuegoC;

import ecos.app.Sesion;
import ecos.pantallas.PantallaClasico;
import java.util.function.Consumer;
import javax.swing.Timer;

// Controlador del modo clásico: une el de interfaz (cartas, barra, sonidos) con las reglas del juego.
//  - 10 segundos por jugada; si acierta suma 7 segundos y sigue; si falla pierde el turno.
//  - Gana el primero que encuentra todas las parejas; ahí se abre la ventana de victoria.
public class ControladorJuegoClasico {

    private static final double TIEMPO_MAX = 10.0; // segundos por jugada
    private static final double BONUS = 7.0; // segundos extra por acierto
    private static final int MONEDAS_INICIO = 30; // monedas iniciales de cada jugador
    private static final int PRECIO_OJO = 20; // monedas que cuesta el ojo espía
    private static final int ESPERA_PAR_MS = 700; // para ver las dos cartas antes de resolver
    private static final String PREMIO = "Premio del modo clásico"; // el premio que se da al ganador

    private final PantallaClasico vista; // la vista de la partida
    private final Consumer<String> irA; // para cambiar de pantalla 
    private final Timer reloj = new Timer(100, e -> tick()); // baja la barra de tiempo cada 0.1 s
    private final int[] monedas = new int[2]; // las monedas de cada jugador
    private PartidaClasica modelo; // las reglas del juego (parejas, turnos, intentos, etc.)
    private double tiempo = TIEMPO_MAX; // el tiempo restante del jugador actual
    private boolean esperando = false; // congela clics y tiempo mientras se resuelve un intento
    private boolean urgente = false;  // avisa con sonido y música cuando quedan 3 segundos
    private int ultimoSegundo = 99; // para avisar solo una vez por segundo de los últimos 3 segundos
    private int partida = 0; // sirve para ignorar temporizadores de una partida anterior

    public ControladorJuegoClasico(PantallaClasico vista, Consumer<String> irA) {
        this.vista = vista;
        this.irA = irA;
        vista.setAlClicCarta(this::clic); //Toque en una carta
        vista.setAlComprarOjo(this::comprarOjo); //Compra de ojo espía
        vista.setAlEntrar(this::iniciar); //Cuando se entra a la pantalla, inicia la partida
        vista.setAlSalir(this::salir); //Cuando se sale de la pantalla, termina la partida
    }

    private void iniciar() {
        partida++;
        int n = Sesion.getTamano(); // número de cartas por jugador (2, 4, 6 o 8)
        modelo = new PartidaClasica(n); // crea un nuevo modelo de juego con n parejas
        int[][] caras = new int[2][]; // las caras de las cartas de cada jugador
        for (int p = 0; p < 2; p++) { // prepara las caras y las monedas de cada jugador
            caras[p] = MezclaCartasClasico.aplanar(modelo.getCaras(p)); // matriz 6 por 6 a fila de 36
            monedas[p] = MONEDAS_INICIO; // cada jugador empieza con 30 monedas
        }
        esperando = false; //pueden tocar cartas.
        urgente = false; //no suena musica urgente.
        ultimoSegundo = 99; //para avisar solo una vez por segundo de los últimos 3 segundos
        tiempo = TIEMPO_MAX; // cada jugador empieza con 10 segundos
        javax.swing.SwingUtilities.invokeLater(() -> {
            vista.configurar(n, Sesion.getJugador1(), Sesion.getJugador2(), 2, 7, PRECIO_OJO, caras);
            vista.setTurno(0); //Arma dos tableros 
            for (int p = 0; p < 2; p++) { //monedas, parejas e intentos de cada jugador
                vista.setMonedas(p, monedas[p]);
                vista.setIntentos(p, 0);
                vista.setParejas(p, 0);
            }
            vista.setTiempo(tiempo, TIEMPO_MAX, false); //pone la barra de tiempo en 10 segundos
            SonidosPartidaClasica.cuentaRegresiva();
            reloj.restart(); // empieza a bajar la barra de tiempo cada 0.1 s
        });
    }

    private void salir() { // cuando se sale de la pantalla, se detiene el reloj y se ignoran los temporizadores
        partida++;
        reloj.stop();
    }

    // El jugador toca una carta.
    private void clic(int p, int indice) {
        if (modelo == null || esperando || modelo.terminado()) {
            return;
        }
        PartidaClasica.Jugada jugada = modelo.seleccionar(p, indice);  // devuelve PRIMERA, SEGUNDA o IGNORADA
        if (jugada == PartidaClasica.Jugada.IGNORADA) {
            return;
        }
        SonidosPartidaClasica.voltear(); // sonido de voltear carta
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

        if (resultado == PartidaClasica.Jugada.ACIERTO) { // acierto: se marcan las cartas y sigue jugando
            vista.marcarPareja(p, a, b);
            vista.setParejas(p, modelo.getParejas(p));
            if (modelo.terminado()) {
                terminar();
                return;
            }
            tiempo += BONUS; // acierto: +7 segundos y sigue jugando
            quitarUrgencia();
            vista.setTiempo(tiempo, TIEMPO_MAX, false); // actualiza la barra de tiempo
            vista.mostrarBonus("+" + (int) BONUS + "s");
            esperando = false;
            vista.bloquear(false);
        } else {
            vista.voltearCarta(p, a, false); // fallo: se tapan y pierde el turno
            vista.voltearCarta(p, b, false); // fallo: se tapan y pierde el turno
            esperando = false; // ya se resolvió el intento, se puede tocar otra carta
            cambiarTurno(); // el otro jugador tiene su turno
        }
    }

    private void cambiarTurno() { // cambia el turno y reinicia el tiempo
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
        tiempo = Math.max(0, tiempo - 0.1); // baja el tiempo cada 0.1 s
        vista.setTiempo(tiempo, TIEMPO_MAX, false);
        int s = (int) Math.ceil(tiempo); // el segundo actual (redondeado hacia arriba)
        if (tiempo <= 3 && tiempo > 0 && s != ultimoSegundo) {
            ultimoSegundo = s;
            SonidosPartidaClasica.tick();
            if (!urgente) { // suena música urgente solo una vez
                urgente = true;
                SonidosPartidaClasica.musicaUrgente();
            }
        }
        if (tiempo <= 0) {
            int carta = modelo.cancelarSeleccion(); // si había una carta volteada, se tapa
            if (carta >= 0) {
                vista.voltearCarta(modelo.getTurno(), carta, false); // tapa la carta que estaba volteada
            }
            cambiarTurno();
        }
    }

    private void quitarUrgencia() { // quita la música urgente y vuelve a la normal
        if (urgente) {
            SonidosPartidaClasica.musicaNormal();
        }
        urgente = false;
        ultimoSegundo = 99;
    }

    private void terminar() { // el juego terminó: se muestra la ventana de victoria y se da el premio
        reloj.stop();
        quitarUrgencia();
        esperando = true;
        despues(900, () -> vista.mostrarVictoria(modelo.ganador(), modelo.getIntentos(0), modelo.getIntentos(1),
                PREMIO, () -> irA.accept("premios"), this::iniciar)); // muestra la ventana de victoria
    }

    // Ojo espía: solo se puede comprar si no hay una carta volteada.
    private void comprarOjo(int p) {
        if (modelo == null || esperando || p != modelo.getTurno() || modelo.hayPrimera() || monedas[p] < PRECIO_OJO) {
            return; // no se puede comprar ojo espía
        }
        monedas[p] -= PRECIO_OJO;
        vista.setMonedas(p, monedas[p]);
        vista.ojoEspia(p);
        esperando = true; // el tiempo se congela mientras se ven las cartas
        despues(2100, () -> {
            esperando = false;
            vista.bloquear(false); // ya se terminó de ver las cartas, se puede tocar otra carta
        });
    }

    // Hace algo una sola vez después de unos milisegundos (si la partida sigue siendo la misma).
    private void despues(int ms, Runnable accion) {
        final int actual = partida;
        Timer t = new Timer(ms, e -> { // se ejecuta después de ms milisegundos
            if (actual == partida) { // la partida sigue siendo la misma
                accion.run();
            }
        });
        t.setRepeats(false); // solo se ejecuta una vez
        t.start(); // se ejecuta solo una vez
    }
}
