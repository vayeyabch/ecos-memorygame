package ecos.controlador;

// Clases del propio proyecto que se usan aquí.
import ecos.app.Idioma;                       // traduce los textos al idioma actual
import ecos.app.Sesion;                       // nombre del usuario que inició sesión
import ecos.modelo.ClienteJuego;              // sirve para conectarse a la IP del anfitrión
import ecos.modelo.ConexionJuego;             // la conexión ya abierta, para enviar y recibir mensajes
import ecos.modelo.ModeloMonedas;             // trae cuántas monedas da una victoria
import ecos.modelo.ModeloMultijugador;        // las reglas del juego (solo las usa el anfitrión)
import ecos.modelo.ModeloVictoria;            // guarda la partida y las monedas en la base de datos
import ecos.modelo.Partida;                   // lo que devuelve ModeloVictoria al guardar
import ecos.modelo.ServidorJuego;             // abre el puerto y espera al invitado

import ecos.pantallas.PantallaMultijugador;   // la pantalla (lobby y tablero)
// Utilidades de Java, List para las listas y StringJoiner para armar el texto de las cartas.
import java.util.List;
import java.util.StringJoiner;
// Librería Swing, SwingUtilities para volver al hilo de la interfaz y Timer para esperar un momento.
import javax.swing.SwingUtilities;
import javax.swing.Timer;

// Controlador de PantallaMultijugador. El anfitrión (jugador 0) tiene el ModeloMultijugador, decide cada jugada y manda
// los eventos al invitado (jugador 1); el invitado solo manda sus clics ("CLIC i") y aplica los eventos que recibe.
// Mensajes de la red (una línea cada uno):
//   invitado -> anfitrión:  HOLA nombre  |  CLIC i
//   anfitrión -> invitado:  HOLA nombre  |  TABLERO n c0,c1,...  |  eventos V, P, F, T, FIN (ver ModeloMultijugador)
// Al terminar, cada computadora guarda su partida en la base (modo "red") y el ganador recibe las monedas. Estas partidas
// no cuentan para el ranking.
// A cargo de Hellen.
public class ControladorMultijugador {

    // La pantalla que se controla.
    private final PantallaMultijugador vista;
    // Tamaño del tablero que elige el anfitrión (4x4 o 6x6).
    private int tamano = 4;
    // La conexión con el otro jugador (null mientras no hay).
    private ConexionJuego conexion = null;
    // El servidor que espera al invitado (solo el anfitrión lo tiene).
    private ServidorJuego servidor = null;
    // Solo el anfitrión tiene modelo. soyAnfitrion dice qué lado soy.
    private ModeloMultijugador modelo = null;
    private boolean soyAnfitrion = true;
    // Nombre del otro jugador, llega en el mensaje HOLA.
    private String nombreRival = "Rival";
    // True cuando ya terminó la partida o se cortó la conexión (ya no se aceptan jugadas).
    private boolean terminada = false;
    // Intentos (dos cartas volteadas) de cada jugador y de quién es el turno, se cuentan con los eventos.
    private final int[] intentos = new int[2];
    private int turnoActual = 0;
    // Puntos (parejas) de cada jugador, se actualizan con los eventos P.
    private int pts0 = 0;
    private int pts1 = 0;

    // Conecta los botones de la pantalla con los métodos de este controlador.
    public ControladorMultijugador(PantallaMultijugador vista) {
        this.vista = vista;
        // Botones 4x4 y 6x6, se guarda el tamaño y se resalta el botón.
        vista.setAccionTamano(n -> { tamano = n; vista.marcarTamano(n); });
        // Botón Crear partida (anfitrión).
        vista.setAccionCrear(this::crear);
        // Botón Unirse (invitado).
        vista.setAccionUnirse(this::unirse);
        // Clic sobre una carta del tablero.
        vista.setAccionCarta(this::alTocarCarta);
        // Botones Volver y Salir de la partida.
        vista.setAccionSalir(this::salir);
        // Al entrar a la pantalla se empieza de cero; al salir se cierra la red.
        vista.setAlEntrar(() -> { limpiar(); vista.marcarTamano(tamano); });
        vista.setAlSalir(this::limpiar);
    }

    // Cierra todo lo de la red y deja el controlador como nuevo.
    private void limpiar() {
        // Si se estaba esperando al invitado, se deja de esperar.
        if (servidor != null) {
            servidor.cerrar();
            servidor = null;
        }
        // Si había conexión abierta, se cierra.
        if (conexion != null) {
            conexion.cerrar();
            conexion = null;
        }
        modelo = null;
        terminada = false;
        reiniciarMarcador();
    }

    // Puntos e intentos en cero (partida nueva).
    private void reiniciarMarcador() {
        pts0 = 0;
        pts1 = 0;
        intentos[0] = 0;
        intentos[1] = 0;
        turnoActual = 0;
    }

    // Cierra la red y vuelve a la pantalla donde se elige entre clásico y multijugador.
    private void salir() {
        limpiar();
        vista.irAModo();
    }

    // Mi nombre de usuario (si por alguna razón no hay, "Jugador").
    private static String miNombre() {
        String n = Sesion.getNombre();
        return n == null || n.isEmpty() ? "Jugador" : n;
    }

    // ---------- Anfitrión (el que crea la partida) ----------

    // Botón Crear partida: abre el puerto, muestra las IP y espera al invitado.
    private void crear() {
        limpiar();
        soyAnfitrion = true;
        // Se desactivan los botones mientras se espera.
        vista.setLobbyOcupado(true);
        vista.setEstadoLobby("Esperando al otro jugador… Buscando tu IP pública…", false);
        servidor = new ServidorJuego();
        ServidorJuego este = servidor;
        // La IP pública se pregunta en otro hilo (necesita internet y puede tardar).
        new Thread(() -> {
            String publica = ServidorJuego.ipPublica();
            SwingUtilities.invokeLater(() -> {
                // Solo se muestra si todavía se está esperando en esta misma partida.
                if (servidor == este && conexion == null) {
                    vista.setEstadoLobby(textoIps(publica), false);
                }
            });
        }, "ip-publica").start();
        // Se abre el puerto. Si llega alguien se llama alLlegarInvitado; si falla se muestra el error en el lobby.
        servidor.iniciar(c -> SwingUtilities.invokeLater(() -> alLlegarInvitado(c)),
                msg -> SwingUtilities.invokeLater(() -> {
                    vista.setLobbyOcupado(false);
                    vista.setEstadoLobby(msg, true);
                }));
    }

    // Texto con las IP para decirle al otro jugador. Por internet se usa la pública, pero solo funciona si el router tiene
    // reenviado el puerto (si no, no llega). En la misma wifi sirve la local.
    private static String textoIps(String publica) {
        String pub = publica == null ? "no se pudo averiguar (¿hay internet?)" : "<b>" + publica + "</b>";
        return "Esperando al otro jugador…<br>IP pública (por internet): " + pub
                + "<br>IP local (misma wifi): <b>" + ServidorJuego.ipLocal() + "</b>"
                + "<br>Por internet hay que reenviar el puerto " + ServidorJuego.PUERTO + " (TCP) del router a esta computadora.";
    }

    // Llegó el invitado, esperamos su "HOLA nombre" para empezar.
    private void alLlegarInvitado(ConexionJuego c) {
        conexion = c;
        // Cada mensaje del invitado llega a alMensajeAnfitrion (en el hilo de la pantalla); si se corta, alDesconectar.
        c.escuchar(m -> SwingUtilities.invokeLater(() -> alMensajeAnfitrion(m, c)),
                () -> SwingUtilities.invokeLater(() -> alDesconectar(c)));
    }

    // Mensajes que recibe el anfitrión: HOLA (empieza la partida) y CLIC (el invitado tocó una carta).
    private void alMensajeAnfitrion(String m, ConexionJuego c) {
        // Si es de una conexión vieja, se ignora.
        if (c != conexion) {
            return;
        }
        if (m.startsWith("HOLA ")) {
            // El invitado se presentó, se crea el modelo con el tablero mezclado y se reinicia el marcador.
            nombreRival = m.substring(5);
            modelo = new ModeloMultijugador(tamano);
            terminada = false;
            reiniciarMarcador();
            // Se presenta el anfitrión y se manda el tablero (las caras separadas por comas) para que los dos vean lo mismo.
            c.enviar("HOLA " + miNombre());
            StringJoiner sj = new StringJoiner(",");
            for (int cara : modelo.getCaras()) {
                sj.add(String.valueOf(cara));
            }
            c.enviar("TABLERO " + tamano + " " + sj);
            // Se arma el tablero en esta pantalla y empieza el turno del anfitrión (jugador 0).
            vista.mostrarTablero(tamano, modelo.getCaras(), miNombre(), nombreRival, 0);
            enviarYAplicar("T 0");
        } else if (m.startsWith("CLIC ") && modelo != null) {
            // El invitado tocó la carta i, se juega como jugador 1.
            try {
                jugar(1, Integer.parseInt(m.substring(5).trim()));
            } catch (NumberFormatException e) {
                // Mensaje raro, se ignora.
            }
        }
    }

    // El anfitrión (o el invitado, por la red) quiere voltear la carta i. Se pregunta al modelo qué pasó.
    private void jugar(int jugador, int i) {
        if (modelo == null || terminada) {
            return;
        }
        // El modelo devuelve los eventos que pasaron (vacío si la jugada no valía, por ejemplo si no era su turno).
        List<String> eventos = modelo.voltear(jugador, i);
        for (String ev : eventos) {
            // Cada evento se manda al invitado y se aplica aquí.
            enviarYAplicar(ev);
            // Después de un fallo se espera un momento (se ven las dos cartas) y pasa el turno.
            if (ev.startsWith("F ")) {
                ModeloMultijugador mm = modelo;
                Timer t = new Timer(1300, e -> {
                    // Solo si la partida sigue siendo la misma y no terminó.
                    if (modelo == mm && !terminada) {
                        String sig = mm.continuar();
                        if (sig != null) {
                            enviarYAplicar(sig);
                        }
                    }
                });
                t.setRepeats(false);
                t.start();
            }
        }
    }

    // Manda el evento al invitado y lo aplica aquí.
    private void enviarYAplicar(String ev) {
        if (conexion != null) {
            conexion.enviar(ev);
        }
        aplicar(ev);
    }

    // ---------- Invitado (el que se une) ----------

    // Botón Unirse: se conecta a la IP escrita.
    private void unirse() {
        String ip = vista.getIp();
        if (ip.isEmpty()) {
            vista.setEstadoLobby("Escribí la IP del anfitrión.", true);
            return;
        }
        limpiar();
        soyAnfitrion = false;
        vista.setLobbyOcupado(true);
        vista.setEstadoLobby("Conectando…", false);
        // Conectarse puede tardar, se hace en otro hilo para que la pantalla no se congele.
        new Thread(() -> {
            try {
                ConexionJuego c = ClienteJuego.conectar(ip);
                SwingUtilities.invokeLater(() -> alConectar(c));
            } catch (Exception e) {
                SwingUtilities.invokeLater(() -> {
                    vista.setLobbyOcupado(false);
                    vista.setEstadoLobby("No se pudo conectar. Revisá la IP, el puerto y que el anfitrión haya creado la partida.", true);
                });
            }
        }, "conectar-red").start();
    }

    // Ya se conectó, se presenta con su nombre y espera que el anfitrión mande el tablero.
    private void alConectar(ConexionJuego c) {
        conexion = c;
        c.escuchar(m -> SwingUtilities.invokeLater(() -> alMensajeInvitado(m, c)),
                () -> SwingUtilities.invokeLater(() -> alDesconectar(c)));
        c.enviar("HOLA " + miNombre());
        vista.setEstadoLobby("Conectado, esperando que empiece la partida…", false);
    }

    // Mensajes que recibe el invitado: HOLA (nombre del anfitrión), TABLERO (las caras) y los eventos del juego.
    private void alMensajeInvitado(String m, ConexionJuego c) {
        if (c != conexion) {
            return;
        }
        if (m.startsWith("HOLA ")) {
            nombreRival = m.substring(5);
        } else if (m.startsWith("TABLERO ")) {
            // "TABLERO n c0,c1,c2,..." se convierte en el tamaño n y la lista de caras.
            String[] partes = m.split(" ");
            int n = Integer.parseInt(partes[1]);
            String[] txt = partes[2].split(",");
            int[] caras = new int[txt.length];
            for (int i = 0; i < caras.length; i++) {
                caras[i] = Integer.parseInt(txt[i]);
            }
            terminada = false;
            reiniciarMarcador();
            // El invitado es el jugador 1, el nombre del anfitrión va primero en el marcador.
            vista.mostrarTablero(n, caras, nombreRival, miNombre(), 1);
        } else {
            // Cualquier otra cosa es un evento del juego (V, P, F, T, FIN).
            aplicar(m);
        }
    }

    // ---------- Los dos lados ----------

    // El jugador tocó la carta i: el anfitrión juega directo; el invitado le manda el clic al anfitrión.
    private void alTocarCarta(int i) {
        if (soyAnfitrion) {
            jugar(0, i);
        } else if (conexion != null) {
            conexion.enviar("CLIC " + i);
        }
    }

    // Aplica un evento en la pantalla (igual para el anfitrión y el invitado).
    private void aplicar(String ev) {
        String[] p = ev.split(" ");
        switch (p[0]) {
            case "V":
                // Voltea la carta p[1].
                vista.voltear(Integer.parseInt(p[1]));
                break;
            case "P":
                // Pareja: p[1] y p[2] son las cartas, p[3] el jugador, p[4] y p[5] el marcador nuevo.
                vista.marcarPareja(Integer.parseInt(p[1]), Integer.parseInt(p[2]));
                intentos[Integer.parseInt(p[3])]++;
                pts0 = Integer.parseInt(p[4]);
                pts1 = Integer.parseInt(p[5]);
                vista.setPuntos(pts0, pts1);
                break;
            case "F":
                // Fallo: se cuenta el intento del que tenía el turno, nadie toca cartas y se tapan las dos.
                intentos[turnoActual]++;
                vista.bloquear();
                vista.taparFallo(Integer.parseInt(p[1]), Integer.parseInt(p[2]));
                break;
            case "T":
                // Turno del jugador p[1].
                turnoActual = Integer.parseInt(p[1]);
                vista.setTurno(turnoActual);
                break;
            case "FIN":
                // Ya no quedan cartas, se guarda la partida y se muestra el resultado.
                terminada = true;
                terminar();
                break;
            default:
                break;
        }
    }

    // Texto del aviso final, dice quién ganó o si empataron.
    private String textoFinal() {
        String n0 = soyAnfitrion ? miNombre() : nombreRival;
        String n1 = soyAnfitrion ? nombreRival : miNombre();
        String r = pts0 == pts1 ? Idioma.t("¡Empate!") : Idioma.t("Ganó " + (pts0 > pts1 ? n0 : n1) + ".");
        return r + " " + n0 + " " + pts0 + " - " + pts1 + " " + n1 + ".";
    }

    // Fin de la partida, se guarda en la base (historial) y el que ganó recibe las monedas. NO cuenta para el ranking. Cada
    // computadora guarda lo suyo con su propia cuenta. Se hace en otro hilo porque usa la base de datos.
    private void terminar() {
        vista.bloquear();
        // Quién soy (0 o 1), mis parejas, las del rival y si gané.
        int yoIdx = soyAnfitrion ? 0 : 1;
        int mios = yoIdx == 0 ? pts0 : pts1;
        int suyos = yoIdx == 0 ? pts1 : pts0;
        boolean gano = mios > suyos;
        int misIntentos = intentos[yoIdx];
        String usuario = Sesion.getNombre();
        String resultado = textoFinal();
        new Thread(() -> {
            // Guarda la partida (modo "red", sin premio) y suma las monedas solo si gané.
            Partida guardada = new ModeloVictoria().registrarRed(usuario, misIntentos, gano);
            // Texto que se agrega al resultado según lo que pasó.
            String extra;
            if (usuario == null || usuario.isBlank()) {
                extra = " " + Idioma.t("Sin cuenta no se guardan monedas.");
            } else if (guardada == null) {
                extra = " " + Idioma.t("No se pudo guardar la partida.");
            } else if (gano) {
                extra = " " + Idioma.t("¡Ganaste " + ModeloMonedas.MONEDAS_POR_VICTORIA + " monedas!");
            } else {
                extra = " " + Idioma.t("Esta vez no hay monedas.");
            }
            // El aviso se muestra en el hilo de la pantalla.
            SwingUtilities.invokeLater(() -> vista.mostrarFin("¡Terminó la partida!",
                    resultado + extra + " " + Idioma.t("Usá «Salir de la partida» para volver.")));
        }, "guardar-partida-red").start();
    }

    // Se cortó la conexión (el otro jugador cerró el juego o se fue la red).
    private void alDesconectar(ConexionJuego c) {
        // Si es una conexión vieja, se ignora.
        if (c != conexion) {
            return;
        }
        conexion = null;
        // Si la partida ya había terminado, no hay nada que avisar.
        if (terminada) {
            return;
        }
        terminada = true;
        vista.mostrarAviso("Se cortó la conexión", "El otro jugador se desconectó. Usá «Salir de la partida» para volver.");
        vista.setLobbyOcupado(false);
    }
}
