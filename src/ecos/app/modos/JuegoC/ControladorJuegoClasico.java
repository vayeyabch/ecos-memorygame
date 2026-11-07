package ecos.app.modos.JuegoC;

import ecos.app.Sesion;
import ecos.modelo.CalculoPremio;
import ecos.pantallas.PantallaClasico;
import java.util.function.Consumer;
import javax.swing.Timer;

// Controlador del modo clásico: une el de interfaz (cartas, barra, sonidos) con las reglas del juego.
//  - 10 segundos por jugada; si acierta suma 7 segundos y sigue; si falla pierde el turno.
//  - Gana el primero que encuentra todas las parejas; ahí se abre la ventana de victoria.
public class ControladorJuegoClasico {

    // ===== CONSTANTES (valores fijos del juego) =====

    private static final double TIEMPO_MAX = 10.0; // segundos que tiene cada jugador por jugada
    private static final double BONUS = 7.0; // segundos extra que se suman al encontrar una pareja
    private static final int MONEDAS_INICIO = 30; // monedas con las que empieza cada jugador
    private static final int PRECIO_OJO = 20; // lo que cuesta comprar el ojo espía
    private static final int ESPERA_PAR_MS = 700; // para ver las dos cartas antes de resolver
    private static final int CUENTA_REGRESIVA_MS = 2000; // lo que dura el sonido 3, 2, 1 (1.9 s)

    // ===== VARIABLES =====
    private final PantallaClasico vista; // la pantalla (lo que se ve)
    private final Consumer<String> irA; // sirve para cambiar de pantalla, por ejemplo irA.accept("premios")
    private final Timer reloj = new Timer(100, e -> tick()); // cada 100 ms (0.1 s) llama a tick()
    private final int[] monedas = new int[2]; // monedas de cada jugador: [0] = jugador 1, [1] = jugador 2
    private PartidaClasica modelo; // las reglas del juego (cartas, turnos, intentos)
    private double tiempo = TIEMPO_MAX; // segundos que le quedan al jugador del turno
    private boolean esperando = false; // congela clics y tiempo mientras se resuelve un intento
    private boolean urgente = false; // true cuando ya sonó la música de poco tiempo
    private int ultimoSegundo = 99; // último segundo en que sonó el tick (para no repetirlo)
    private int partida = 0; // sirve para ignorar temporizadores de una partida anterior

    // Constructor: guarda la pantalla y le dice qué método llamar en cada evento.
    public ControladorJuegoClasico(PantallaClasico vista, Consumer<String> irA) {
        this.vista = vista;
        this.irA = irA;
        vista.setAlClicCarta(this::clic);           // al tocar una carta -> clic()
        vista.setAlComprarOjo(this::comprarOjo);    // al comprar el ojo -> comprarOjo()
        vista.setAlEntrar(this::iniciar);           // al entrar a la pantalla -> iniciar()
        vista.setAlSalir(this::salir);              // al salir de la pantalla -> salir()
    }

    // Prepara una partida nueva desde cero.
    private void iniciar() {
        partida++; // nueva partida: los temporizadores viejos dejan de valer
        int n = Sesion.getTamano(); // tamaño del tablero (6 = 6x6)
        modelo = new PartidaClasica(n); // crea las reglas y baraja las cartas
        ImpresorParejasClasico.imprimir(modelo); // imprime las parejas
        int[][] caras = new int[2][]; // qué imagen tiene cada carta de cada jugador
        for (int p = 0; p < 2; p++) {
            // aplanar: convierte la matriz 6x6 en una lista de 36 que entiende la pantalla
            caras[p] = MezclaCartasClasico.aplanar(modelo.getCaras(p));
            monedas[p] = MONEDAS_INICIO; // cada jugador empieza con 30 monedas
        }
        esperando = true; // congelado mientras suena el 3, 2, 1
        urgente = false;
        ultimoSegundo = 99;
        tiempo = TIEMPO_MAX;
        // invokeLater: los cambios a la pantalla se hacen en el hilo de Swing
        javax.swing.SwingUtilities.invokeLater(() -> {
            vista.configurar(n, Sesion.getJugador1(), Sesion.getJugador2(), 2, 7, PRECIO_OJO, caras);
            vista.setTurno(0); // empieza el jugador 1
            for (int p = 0; p < 2; p++) {
                vista.setMonedas(p, monedas[p]);
                vista.setIntentos(p, 0);
                vista.setParejas(p, 0);
            }
            vista.setTiempo(tiempo, TIEMPO_MAX, false); // barra llena
            vista.bloquear(true); // no se pueden tocar cartas todavía
            SonidosPartidaClasica.cuentaRegresiva(); // suena el 3, 2, 1
            // El reloj y los clics arrancan cuando termina el 3, 2, 1.
            despues(CUENTA_REGRESIVA_MS, () -> {
                esperando = false;       // ya se puede jugar
                vista.bloquear(false);   // se habilitan las cartas
                reloj.restart();         // el tiempo empieza a correr desde cero
            });
        });
    }

    // Al salir de la pantalla: se cancela todo lo pendiente.
    private void salir() {
        partida++;    // invalida los temporizadores que quedaban
        reloj.stop(); // para el reloj
    }

    // El jugador toca una carta.
    private void clic(int p, int indice) {
        // Si no hay partida, estamos esperando o ya terminó, se ignora el clic.
        if (modelo == null || esperando || modelo.terminado()) {
            return;
        }
        PartidaClasica.Jugada jugada = modelo.seleccionar(p, indice);
        if (jugada == PartidaClasica.Jugada.IGNORADA) {
            return; // clic no válido (carta ya volteada, turno ajeno...)
        }
        SonidosPartidaClasica.voltear();
        vista.voltearCarta(p, indice, true); // se muestra la carta
        if (jugada == PartidaClasica.Jugada.SEGUNDA) {
            // Ya son dos cartas: se bloquea y se espera 0.7 s para que se vean.
            esperando = true;
            vista.bloquear(true);
            despues(ESPERA_PAR_MS, () -> resolver(p));
        }
    }

    // Ya están las dos cartas arriba: ¿iguales o diferentes?
    private void resolver(int p) {
        int a = modelo.getCartaA(); // posición de la primera carta
        int b = modelo.getCartaB(); // posición de la segunda carta
        PartidaClasica.Jugada resultado = modelo.resolver(); // ACIERTO o FALLO
        vista.setIntentos(p, modelo.getIntentos(p)); // actualiza el contador de intentos

        if (resultado == PartidaClasica.Jugada.ACIERTO) {
            vista.marcarPareja(p, a, b); // las deja marcadas como pareja
            vista.setParejas(p, modelo.getParejas(p));
            if (modelo.terminado()) { // ¿ya encontró las 18 parejas?
                terminar();
                return;
            }
            tiempo += BONUS; // acierto: +7 segundos y sigue jugando
            quitarUrgencia(); // con más tiempo ya no hay urgencia
            vista.setTiempo(tiempo, TIEMPO_MAX, false);
            vista.mostrarBonus("+" + (int) BONUS + "s");
            esperando = false;     // sigue jugando el mismo
            vista.bloquear(false);
        } else {
            vista.voltearCarta(p, a, false); // fallo: se tapan y pierde el turno
            vista.voltearCarta(p, b, false);
            esperando = false;
            cambiarTurno(); // el turno pasa al otro jugador
        }
    }

    // Pasa el turno al otro jugador y reinicia sus 10 segundos.
    private void cambiarTurno() {
        boolean cambio = modelo.pasarTurno();
        tiempo = TIEMPO_MAX; // el nuevo turno empieza con 10 s
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
        tiempo = Math.max(0, tiempo - 0.1); // resta 0.1 s (sin bajar de 0)
        vista.setTiempo(tiempo, TIEMPO_MAX, false);
        int s = (int) Math.ceil(tiempo); // segundo actual redondeado hacia arriba
        // En los últimos 3 s suena un tick por segundo
        if (tiempo <= 3 && tiempo > 0 && s != ultimoSegundo) {
            ultimoSegundo = s;
            SonidosPartidaClasica.tick();
            if (!urgente) { // la música urgente suena solo una vez
                urgente = true;
                SonidosPartidaClasica.musicaUrgente();
            }
        }
        if (tiempo <= 0) { // se acabó el tiempo: pierde el turno
            int carta = modelo.cancelarSeleccion(); // si había una carta volteada, se tapa
            if (carta >= 0) {
                vista.voltearCarta(modelo.getTurno(), carta, false);
            }
            cambiarTurno();
        }
    }

    // Vuelve a la música normal (si estaba la urgente) y reinicia el aviso.
    private void quitarUrgencia() {
        if (urgente) {
            SonidosPartidaClasica.musicaNormal();
        }
        urgente = false;
        ultimoSegundo = 99;
    }

    // Alguien encontró todas las parejas: se calcula el premio y se abre la ventana de victoria.
    private void terminar() {
        reloj.stop();
        quitarUrgencia();
        esperando = true; // nadie puede tocar nada
        int ganador = modelo.ganador();
        int puntaje = modelo.getIntentos(ganador); // puntaje = intentos del ganador (menos intentos, más premio)
        String premio = CalculoPremio.textoPremio(puntaje); // fórmula del equipo (Allison): 1 / intentos x 100 x dólar
        // Espera 0.9 s y abre VentanaVictoria con los datos y los dos botones
        int i0 = modelo.getIntentos(0);
        int i1 = modelo.getIntentos(1);
        if (ganador == 1) { // Ganó el jugador 2: primero inicia sesión para que se le sumen los puntos.
            int puntos = (int) Math.max(1, Math.round(CalculoPremio.premioDolares(puntaje))); // Puntos = premio en dólares redondeado.
            despues(900, () -> {
                VentanaLoginGanador ventana = new VentanaLoginGanador(Sesion.getJugador2(), puntos);
                new ControladorLoginGanador(ventana, puntos,
                        extra -> mostrarVictoria(ganador, i0, i1, extra == null ? premio : premio + "  ·  " + extra));
                ventana.mostrar(vista);
            });
            return;
        }
        despues(900, () -> mostrarVictoria(ganador, i0, i1, premio)); // Ganó el jugador 1: victoria directa.
    }

    // Abre la ventana de victoria con los dos botones.
    private void mostrarVictoria(int ganador, int i0, int i1, String premio) {
        vista.mostrarVictoria(ganador, i0, i1, premio,
                () -> irA.accept("premios"), // botón "Ver premios"
                this::iniciar);              // botón "Jugar otra vez"
    }

    // Ojo espía: solo se puede comprar si no hay una carta volteada.
    private void comprarOjo(int p) {
        if (modelo == null || esperando || p != modelo.getTurno() || modelo.hayPrimera() || monedas[p] < PRECIO_OJO) {
            return;
        }
        monedas[p] -= PRECIO_OJO; // se cobra
        vista.setMonedas(p, monedas[p]);
        vista.ojoEspia(p); // la pantalla muestra las cartas un momento
        esperando = true; // el tiempo se congela mientras se ven las cartas
        despues(2100, () -> { // después de 2.1 s se vuelve a jugar
            esperando = false;
            vista.bloquear(false);
        });
    }

    // Hace algo una sola vez después de unos milisegundos (si la partida sigue siendo la misma).
    private void despues(int ms, Runnable accion) {
        final int actual = partida; // guarda en qué partida estamos
        Timer t = new Timer(ms, e -> {
            if (actual == partida) { // solo si sigue siendo la misma partida
                accion.run();
            }
        });
        t.setRepeats(false); // una sola vez, no se repite
        t.start();
    }
}