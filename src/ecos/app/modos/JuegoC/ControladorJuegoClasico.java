package ecos.app.modos.JuegoC;

// Sesion: datos compartidos (tamaño del tablero, jugador 1 y 2, si el 2 es invitado).
import ecos.app.Sesion;
// CalculoPremio: da el texto del premio (colones y dólares) según los intentos.
import ecos.modelo.CalculoPremio;
// ModeloMonedas: saldo de monedas, precio del Ojo espía y monedas por victoria.
import ecos.modelo.ModeloMonedas;
// ModeloVictoria: guarda la partida ganada (y las monedas) en la base de datos.
import ecos.modelo.ModeloVictoria;
// Partida: objeto de la partida guardada; es null si no se pudo guardar.
import ecos.modelo.Partida;
// PantallaClasico: la vista que este controlador maneja.
import ecos.pantallas.PantallaClasico;
// Consumer: función que recibe un dato; aquí recibe el id de la pantalla a la que ir.
import java.util.function.Consumer;
// Timer (de Swing): reloj que ejecuta algo cada cierto tiempo sin congelar la ventana.
import javax.swing.Timer;

// Controlador del modo clásico: une la pantalla (cartas, barra, sonidos) con las reglas. Cada jugada dura 10 segundos,
// acertar suma 7 y sigue, y fallar pierde el turno. Gana quien encuentra todas las parejas primero.
public class ControladorJuegoClasico {

    // Segundos por jugada, segundos que suma una pareja, precio del Ojo espía (30 monedas) y tiempo que se ven las dos
    // cartas antes de resolver.
    private static final double TIEMPO_MAX = 10.0;
    private static final double BONUS = 7.0;
    private static final int PRECIO_OJO = ModeloMonedas.PRECIO_OJO;
    private static final int ESPERA_PAR_MS = 700;
    // Lo que dura el 3, 2, 1.
    private static final int CUENTA_REGRESIVA_MS = 2000;

    // Pantalla, cómo cambiar de pantalla, reloj que llama a tick() cada 100 ms y monedas de cada cuenta (sin cuenta
    // siempre 0).
    private final PantallaClasico vista;
    private final Consumer<String> irA;
    // La lambda e -> tick() es lo que hace el reloj cada 100 ms.
    private final Timer reloj = new Timer(100, e -> tick());
    private final ModeloMonedas monedas = new ModeloMonedas();
    // Guarda la victoria (partida y 15 monedas) en la base.
    private final ModeloVictoria victoria = new ModeloVictoria();
    // Reglas del juego, segundos que le quedan al turno, bloqueo de clics y tiempo mientras se resuelve un intento, y si
    // ya sonó la música urgente.
    private PartidaClasica modelo;
    private double tiempo = TIEMPO_MAX;
    private boolean esperando = false;
    private boolean urgente = false;
    // Último segundo en que sonó el tick, para no repetirlo, y número de partida, para ignorar temporizadores viejos.
    private int ultimoSegundo = 99;
    private int partida = 0;
    // Aciertos seguidos del jugador con el turno.
    private int racha = 0;

    // Conecta la vista con este controlador por medio de ganchos (referencias a métodos con ::).
    public ControladorJuegoClasico(PantallaClasico vista, Consumer<String> irA) {
        this.vista = vista;
        this.irA = irA;
        // Cuando se toca una carta, la pantalla llama a clic.
        vista.setAlClicCarta(this::clic);
        // Cuando se toca el Ojo espía, llama a clicOjo.
        vista.setAlClicOjo(this::clicOjo);
        // Al entrar a la pantalla arranca una partida; al salir se detiene.
        vista.setAlEntrar(this::iniciar);
        vista.setAlSalir(this::salir);
    }

    // Prepara una partida nueva desde cero.
    private void iniciar() {
        // Sube el número de partida: así los temporizadores de la partida anterior se ignoran.
        partida++;
        int n = Sesion.getTamano();
        modelo = new PartidaClasica(n);
        // Herramienta de pruebas: imprime las cartas en la consola.
        ImpresorParejasClasico.imprimir(modelo);
        // Imagen de cada carta de cada jugador, en una lista que entiende la pantalla.
        int[][] caras = new int[2][];
        for (int p = 0; p < 2; p++) {
            caras[p] = MezclaCartasClasico.aplanar(modelo.getCaras(p));
        }
        // Todo queda congelado mientras suena el 3, 2, 1.
        esperando = true;
        urgente = false;
        ultimoSegundo = 99;
        racha = 0;
        tiempo = TIEMPO_MAX;
        // Los cambios a la pantalla se hacen en el hilo de Swing.
        javax.swing.SwingUtilities.invokeLater(() -> {
            vista.configurar(n, Sesion.getJugador1(), Sesion.getJugador2(), 2, 7, PRECIO_OJO, caras);
            vista.setTurno(0);
            for (int p = 0; p < 2; p++) {
                vista.setMonedas(p, monedas.monedas(cuenta(p)));
                vista.setIntentos(p, 0);
                vista.setParejas(p, 0);
            }
            vista.setTiempo(tiempo, TIEMPO_MAX, false);
            vista.bloquear(true);
            SonidosPartidaClasica.cuentaRegresiva();
            // El reloj y los clics arrancan cuando termina el 3, 2, 1.
            despues(CUENTA_REGRESIVA_MS, () -> {
                esperando = false;
                vista.bloquear(false);
                reloj.restart();
            });
        });
    }

    // Al salir de la pantalla: invalida los temporizadores pendientes y detiene el reloj.
    private void salir() {
        partida++;
        reloj.stop();
    }

    // Se tocó una carta (p = jugador, indice = posición de la carta).
    private void clic(int p, int indice) {
        // Se ignora el clic si no hay partida, si se espera o si ya terminó.
        if (modelo == null || esperando || modelo.terminado()) {
            return;
        }
        // El modelo dice qué pasó con esa carta.
        PartidaClasica.Jugada jugada = modelo.seleccionar(p, indice);
        if (jugada == PartidaClasica.Jugada.IGNORADA) {
            return;
        }
        SonidosPartidaClasica.voltear();
        vista.voltearCarta(p, indice, true);
        if (jugada == PartidaClasica.Jugada.SEGUNDA) {
            // Con dos cartas se bloquea y se espera 0.7 s para que se vean.
            esperando = true;
            vista.bloquear(true);
            despues(ESPERA_PAR_MS, () -> resolver(p));
        }
    }

    // Decide si las dos cartas volteadas fueron pareja o no.
    private void resolver(int p) {
        // Marca acierto o fallo y actualiza los intentos.
        int a = modelo.getCartaA();
        int b = modelo.getCartaB();
        PartidaClasica.Jugada resultado = modelo.resolver();
        vista.setIntentos(p, modelo.getIntentos(p));

        if (resultado == PartidaClasica.Jugada.ACIERTO) {
            vista.marcarPareja(p, a, b);
            vista.setParejas(p, modelo.getParejas(p));
            // Racha: aciertos seguidos; con 2 o más suena el sonido de racha.
            racha++;
            if (racha >= 2) {
                SonidosPartidaClasica.racha();
            }
            // Si ya están las parejas completas, termina.
            if (modelo.terminado()) {
                terminar();
                return;
            }
            // Acertar suma 7 segundos y el mismo jugador sigue.
            tiempo += BONUS;
            quitarUrgencia();
            vista.setTiempo(tiempo, TIEMPO_MAX, false);
            vista.mostrarBonus("+" + (int) BONUS + "s");
            esperando = false;
            vista.bloquear(false);
        } else {
            // Fallo: se tapan las cartas y pasa el turno.
            racha = 0;
            vista.voltearCarta(p, a, false);
            vista.voltearCarta(p, b, false);
            esperando = false;
            cambiarTurno();
        }
    }

    // Pasa el turno al otro jugador y reinicia sus 10 segundos.
    private void cambiarTurno() {
        racha = 0;
        boolean cambio = modelo.pasarTurno();
        tiempo = TIEMPO_MAX;
        quitarUrgencia();
        if (cambio) {
            SonidosPartidaClasica.cambioTurno();
            vista.setTurno(modelo.getTurno());
            vista.mostrarBannerTurno(modelo.getTurno());
        } else {
            // Si el otro ya terminó sigue el mismo jugador.
            vista.bloquear(false);
        }
        vista.setTiempo(tiempo, TIEMPO_MAX, true);
    }

    // Corre cada 0.1 s: baja la barra y avisa los últimos 3 segundos con un tick por segundo.
    private void tick() {
        if (modelo == null || esperando) {
            return;
        }
        // Math.max(0, ...) evita que el tiempo quede negativo.
        tiempo = Math.max(0, tiempo - 0.1);
        vista.setTiempo(tiempo, TIEMPO_MAX, false);
        // Math.ceil redondea hacia arriba: con 2.3 s el "segundo actual" es 3.
        int s = (int) Math.ceil(tiempo);
        if (tiempo <= 3 && tiempo > 0 && s != ultimoSegundo) {
            ultimoSegundo = s;
            SonidosPartidaClasica.tick();
            // La música urgente y la alerta suenan una sola vez.
            if (!urgente) {
                urgente = true;
                SonidosPartidaClasica.alertaTiempo();
                SonidosPartidaClasica.musicaUrgente();
            }
        }
        // Se acabó el tiempo: se destapa la carta que había volteado y pasa el turno.
        if (tiempo <= 0) {
            int carta = modelo.cancelarSeleccion();
            if (carta >= 0) {
                vista.voltearCarta(modelo.getTurno(), carta, false);
            }
            cambiarTurno();
        }
    }

    // Vuelve a la música normal si estaba la urgente.
    private void quitarUrgencia() {
        if (urgente) {
            SonidosPartidaClasica.musicaNormal();
        }
        urgente = false;
        ultimoSegundo = 99;
    }

    // Alguien completó su tablero: se guarda la victoria (partida y monedas) y se abre la ventana de victoria.
    private void terminar() {
        reloj.stop();
        quitarUrgencia();
        esperando = true;
        // final: la lambda de abajo necesita una copia que no cambie.
        final int actual = partida;
        int ganador = modelo.ganador();
        // Puntaje: los intentos del ganador. Es null si juega sin cuenta.
        int puntaje = modelo.getIntentos(ganador);
        String cuenta = cuenta(ganador);
        // Premio solo con cuenta, con la fórmula 1 / intentos x 100 x dólar.
        String premio = cuenta == null ? null : CalculoPremio.textoPremio(puntaje);
        int i0 = modelo.getIntentos(0);
        int i1 = modelo.getIntentos(1);
        // Guardar usa la base, así que se hace en segundo plano. Sin cuenta no se guarda nada.
        Thread t = new Thread(() -> {
            Partida guardada = victoria.registrarClasico(cuenta, puntaje);
            // Texto que se muestra según lo que pasó al guardar.
            String extra = cuenta == null ? "Sin cuenta no hay premio ni monedas."
                    : guardada != null ? "+" + ModeloMonedas.MONEDAS_POR_VICTORIA + " monedas para " + cuenta
                    : "No se pudo guardar la victoria (revisá la conexión).";
            // Espera 0.9 s y abre la ventana de victoria si el jugador no salió de la pantalla.
            javax.swing.SwingUtilities.invokeLater(() -> {
                if (actual == partida) {
                    despues(900, () -> vista.mostrarVictoria(ganador, i0, i1, premio, extra,
                            () -> irA.accept("premios"),
                            this::iniciar));
                }
            });
        }, "guardar-victoria");
        // Hilo daemon: no impide que el programa se cierre.
        t.setDaemon(true);
        t.start();
    }

    // Cuenta de cada jugador. El 1 es quien inició sesión y el 2 puede jugar sin cuenta (null).
    private String cuenta(int p) {
        if (p == 0) {
            return Sesion.getNombre();
        }
        return Sesion.isInvitado2() ? null : Sesion.getJugador2();
    }

    // El jugador toca el Ojo espía: avisa si no puede o no alcanzan las monedas; si todo está bien pide confirmar.
    private void clicOjo(int p) {
        // Solo se puede en tu turno y antes de voltear la primera carta.
        if (modelo == null || esperando || p != modelo.getTurno() || modelo.hayPrimera()) {
            vista.avisarOjoNoDisponible();
            return;
        }
        if (!monedas.alcanzaParaOjo(cuenta(p))) {
            vista.avisarSinMonedas(monedas.monedas(cuenta(p)), PRECIO_OJO, cuenta(p) == null);
            return;
        }
        vista.confirmarCompraOjo(PRECIO_OJO, () -> comprarOjo(p));
    }

    // Cobra el Ojo espía y muestra las cartas un momento.
    private void comprarOjo(int p) {
        // Se vuelve a revisar todo y se cobra; si el cobro falla, no se hace nada.
        if (modelo == null || esperando || p != modelo.getTurno() || modelo.hayPrimera() || !monedas.comprarOjo(cuenta(p))) {
            return;
        }
        // El tiempo se congela y a los 2.1 s se vuelve a jugar.
        vista.setMonedas(p, monedas.monedas(cuenta(p)));
        vista.ojoEspia(p);
        esperando = true;
        despues(2100, () -> {
            esperando = false;
            vista.bloquear(false);
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
        // No repetir: se ejecuta una sola vez.
        t.setRepeats(false);
        t.start();
    }
}