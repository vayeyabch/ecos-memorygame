package ecos.app.modos.JuegoC;

// Clases del propio proyecto que se usan aquí.
import ecos.app.Sesion;
import ecos.modelo.CalculoPremio;
import ecos.modelo.ModeloMonedas;
import ecos.pantallas.PantallaClasico;
// Acciones de Java, Consumer para una acción que recibe un dato.
import java.util.function.Consumer;
// Librería Swing, Timer para los relojes.
import javax.swing.Timer;

// Controlador del modo clásico, une el de interfaz (cartas, barra, sonidos) con las reglas del juego.
// Cada jugada dura 10 segundos, si acierta suma 7 y sigue, y si falla pierde el turno.
// Gana el primero que encuentra todas las parejas y ahí se abre la ventana de victoria.
public class ControladorJuegoClasico {

    // Constantes con los valores fijos del juego.

    // Segundos que tiene cada jugador por jugada, segundos extra que suma al encontrar una pareja, precio del ojo espía (30
    // monedas) y tiempo que se esperan las dos cartas antes de resolver.
    private static final double TIEMPO_MAX = 10.0;
    private static final double BONUS = 7.0;
    private static final int PRECIO_OJO = ModeloMonedas.PRECIO_OJO;
    private static final int ESPERA_PAR_MS = 700;
    // Lo que dura el sonido 3, 2, 1 (1.9 s).
    private static final int CUENTA_REGRESIVA_MS = 2000;

    // Variables que cambian durante la partida.
    // La pantalla (lo que se ve). sirve para cambiar de pantalla, por ejemplo irA.accept("premios"). cada 100 ms (0.1 s)
    // llama a tick(). monedas de cada cuenta (el jugador sin cuenta siempre tiene 0).
    private final PantallaClasico vista;
    private final Consumer<String> irA;
    private final Timer reloj = new Timer(100, e -> tick());
    private final ModeloMonedas monedas = new ModeloMonedas();
    // Las reglas del juego (cartas, turnos, intentos), los segundos que le quedan al jugador del turno y un bloqueo que
    // congela clics y tiempo mientras se resuelve un intento. También si ya sonó la música de poco tiempo.
    private PartidaClasica modelo;
    private double tiempo = TIEMPO_MAX;
    private boolean esperando = false;
    private boolean urgente = false;
    // Último segundo en que sonó el tick (para no repetirlo). sirve para ignorar temporizadores de una partida anterior.
    private int ultimoSegundo = 99;
    private int partida = 0;

    // Constructor que guarda la pantalla y le dice qué método llamar en cada evento.
    public ControladorJuegoClasico(PantallaClasico vista, Consumer<String> irA) {
        this.vista = vista;
        this.irA = irA;
        // Al tocar una carta se llama clic(), al tocar el ojo espía clicOjo(), al entrar a la pantalla iniciar() y al salir salir().
        vista.setAlClicCarta(this::clic);
        vista.setAlClicOjo(this::clicOjo);
        vista.setAlEntrar(this::iniciar);
        vista.setAlSalir(this::salir);
    }

    // Prepara una partida nueva desde cero.
    private void iniciar() {
        // Nueva partida, los temporizadores viejos dejan de valer. Tamaño del tablero (6 = 6x6). crea las reglas y baraja las
        // cartas. imprime las parejas.
        partida++;
        int n = Sesion.getTamano();
        modelo = new PartidaClasica(n);
        ImpresorParejasClasico.imprimir(modelo);
        // Qué imagen tiene cada carta de cada jugador.
        int[][] caras = new int[2][];
        for (int p = 0; p < 2; p++) {
            // aplanar, convierte la matriz 6x6 en una lista de 36 que entiende la pantalla
            caras[p] = MezclaCartasClasico.aplanar(modelo.getCaras(p));
        }
        // Congelado mientras suena el 3, 2, 1.
        esperando = true;
        urgente = false;
        ultimoSegundo = 99;
        tiempo = TIEMPO_MAX;
        // invokeLater, los cambios a la pantalla se hacen en el hilo de Swing
        javax.swing.SwingUtilities.invokeLater(() -> {
            vista.configurar(n, Sesion.getJugador1(), Sesion.getJugador2(), 2, 7, PRECIO_OJO, caras);
            // Empieza el jugador 1.
            vista.setTurno(0);
            for (int p = 0; p < 2; p++) {
                // Monedas acumuladas de su cuenta.
                vista.setMonedas(p, monedas.monedas(cuenta(p)));
                vista.setIntentos(p, 0);
                vista.setParejas(p, 0);
            }
            // No se pueden tocar cartas todavía. Suena el 3, 2, 1.
            vista.setTiempo(tiempo, TIEMPO_MAX, false);
            vista.bloquear(true);
            SonidosPartidaClasica.cuentaRegresiva();
            // El reloj y los clics arrancan cuando termina el 3, 2, 1.
            despues(CUENTA_REGRESIVA_MS, () -> {
                // Ya se puede jugar. Se habilitan las cartas. el tiempo empieza a correr desde cero.
                esperando = false;
                vista.bloquear(false);
                reloj.restart();
            });
        });
    }

    // Al salir de la pantalla, se cancela todo lo pendiente.
    private void salir() {
        // Invalida los temporizadores que quedaban. Para el reloj.
        partida++;
        reloj.stop();
    }

    // El jugador toca una carta.
    private void clic(int p, int indice) {
        // Si no hay partida, estamos esperando o ya terminó, se ignora el clic.
        if (modelo == null || esperando || modelo.terminado()) {
            return;
        }
        PartidaClasica.Jugada jugada = modelo.seleccionar(p, indice);
        if (jugada == PartidaClasica.Jugada.IGNORADA) {
            // Clic no válido (carta ya volteada, turno ajeno...).
            return;
        }
        SonidosPartidaClasica.voltear();
        // Se muestra la carta.
        vista.voltearCarta(p, indice, true);
        if (jugada == PartidaClasica.Jugada.SEGUNDA) {
            // Ya son dos cartas, se bloquea y se espera 0.7 s para que se vean.
            esperando = true;
            vista.bloquear(true);
            despues(ESPERA_PAR_MS, () -> resolver(p));
        }
    }

    // Ya están las dos cartas arriba. ¿Iguales o diferentes?
    private void resolver(int p) {
        // Toma la posición de la primera y la segunda carta, guarda si fue ACIERTO o FALLO y actualiza el contador de
        // intentos.
        int a = modelo.getCartaA();
        int b = modelo.getCartaB();
        PartidaClasica.Jugada resultado = modelo.resolver();
        vista.setIntentos(p, modelo.getIntentos(p));

        if (resultado == PartidaClasica.Jugada.ACIERTO) {
            // Las deja marcadas como pareja.
            vista.marcarPareja(p, a, b);
            vista.setParejas(p, modelo.getParejas(p));
            // ¿ya encontró las 18 parejas?.
            if (modelo.terminado()) {
                terminar();
                return;
            }
            // Si acierta, +7 segundos y sigue jugando. Con más tiempo ya no hay urgencia.
            tiempo += BONUS;
            quitarUrgencia();
            vista.setTiempo(tiempo, TIEMPO_MAX, false);
            vista.mostrarBonus("+" + (int) BONUS + "s");
            // Sigue jugando el mismo.
            esperando = false;
            vista.bloquear(false);
        } else {
            // Fallo, se tapan y pierde el turno.
            vista.voltearCarta(p, a, false);
            vista.voltearCarta(p, b, false);
            esperando = false;
            // El turno pasa al otro jugador.
            cambiarTurno();
        }
    }

    // Pasa el turno al otro jugador y reinicia sus 10 segundos.
    private void cambiarTurno() {
        boolean cambio = modelo.pasarTurno();
        // El nuevo turno empieza con 10 s.
        tiempo = TIEMPO_MAX;
        quitarUrgencia();
        if (cambio) {
            SonidosPartidaClasica.cambioTurno();
            vista.setTurno(modelo.getTurno());
            vista.mostrarBannerTurno(modelo.getTurno());
        } else {
            // El otro ya terminó, sigue el mismo jugador.
            vista.bloquear(false);
        }
        vista.setTiempo(tiempo, TIEMPO_MAX, true);
    }

    // Corre cada 0.1 s, baja la barra y avisa los últimos 3 segundos.
    private void tick() {
        if (modelo == null || esperando) {
            return;
        }
        // Resta 0.1 s (sin bajar de 0).
        tiempo = Math.max(0, tiempo - 0.1);
        vista.setTiempo(tiempo, TIEMPO_MAX, false);
        // Segundo actual redondeado hacia arriba.
        int s = (int) Math.ceil(tiempo);
        // En los últimos 3 s suena un tick por segundo
        if (tiempo <= 3 && tiempo > 0 && s != ultimoSegundo) {
            ultimoSegundo = s;
            SonidosPartidaClasica.tick();
            // La música urgente suena solo una vez.
            if (!urgente) {
                urgente = true;
                SonidosPartidaClasica.musicaUrgente();
            }
        }
        // Se acabó el tiempo, pierde el turno. Si había una carta volteada, se tapa.
        if (tiempo <= 0) {
            int carta = modelo.cancelarSeleccion();
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

    // Alguien encontró todas las parejas, se calcula el premio, se suman las monedas y se abre la ventana de victoria.
    private void terminar() {
        reloj.stop();
        quitarUrgencia();
        // Nadie puede tocar nada.
        esperando = true;
        int ganador = modelo.ganador();
        // Puntaje = intentos del ganador (menos intentos, más premio). null si el ganador juega sin cuenta.
        int puntaje = modelo.getIntentos(ganador);
        String cuenta = cuenta(ganador);
        // Premio solo con cuenta (fórmula de Allison, 1 / intentos x 100 x dólar); sin cuenta no hay premio
        String premio = cuenta == null ? null : CalculoPremio.textoPremio(puntaje);
        // +15 monedas (si no tiene cuenta, no se guarda nada).
        monedas.sumarVictoria(cuenta);
        String extra = cuenta == null ? "Sin cuenta no hay premio ni monedas." : "+" + ModeloMonedas.MONEDAS_POR_VICTORIA + " monedas para " + cuenta;
        int i0 = modelo.getIntentos(0);
        int i1 = modelo.getIntentos(1);
        // Espera 0.9 s y abre VentanaVictoria con los datos y los dos botones
        despues(900, () -> vista.mostrarVictoria(ganador, i0, i1, premio, extra,
                // Botón "Ver premios". botón "Jugar otra vez".
                () -> irA.accept("premios"),
                this::iniciar));
    }

    // Cuenta de cada jugador, el jugador 1 es quien inició sesión; el 2 puede jugar sin cuenta (null).
    private String cuenta(int p) {
        if (p == 0) {
            return Sesion.getNombre();
        }
        return Sesion.isInvitado2() ? null : Sesion.getJugador2();
    }

    // El jugador toca el Ojo espía, avisa si no se puede o no alcanzan las monedas; si todo está bien pide confirmar.
    private void clicOjo(int p) {
        if (modelo == null || esperando || p != modelo.getTurno() || modelo.hayPrimera()) {
            // No es su turno o ya volteó una carta.
            vista.avisarOjoNoDisponible();
            return;
        }
        if (!monedas.alcanzaParaOjo(cuenta(p))) {
            vista.avisarSinMonedas(monedas.monedas(cuenta(p)), PRECIO_OJO, cuenta(p) == null);
            return;
        }
        // Pregunta y, si acepta, compra.
        vista.confirmarCompraOjo(PRECIO_OJO, () -> comprarOjo(p));
    }

    // Con el Ojo espía se cobra y se muestran las cartas un momento.
    private void comprarOjo(int p) {
        if (modelo == null || esperando || p != modelo.getTurno() || modelo.hayPrimera() || !monedas.comprarOjo(cuenta(p))) {
            return;
        }
        // Muestra las monedas que quedan. La pantalla muestra las cartas un momento. El tiempo se congela mientras se ven las
        // cartas. después de 2.1 s se vuelve a jugar.
        vista.setMonedas(p, monedas.monedas(cuenta(p)));
        vista.ojoEspia(p);
        esperando = true;
        despues(2100, () -> {
            esperando = false;
            vista.bloquear(false);
        });
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
        // Una sola vez, no se repite.
        t.setRepeats(false);
        t.start();
    }
}