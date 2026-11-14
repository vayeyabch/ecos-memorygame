package ecos.controlador;

import ecos.app.Idioma;
import ecos.app.Sesion;
import ecos.modelo.ClienteJuego;
import ecos.modelo.ConexionJuego;
import ecos.modelo.ModeloMonedas;
import ecos.modelo.ModeloMultijugador;
import ecos.modelo.ModeloVictoria;
import ecos.modelo.Partida;
import ecos.modelo.ServidorJuego;
import ecos.pantallas.PantallaMultijugador;
import java.util.List;
import java.util.StringJoiner;
import javax.swing.SwingUtilities;
import javax.swing.Timer;

// Controlador de PantallaMultijugador. El anfitrión (jugador 0) tiene el ModeloMultijugador, decide cada jugada y manda
// los eventos al invitado (jugador 1); el invitado solo manda sus clics y aplica los eventos que recibe. Por la red
// viaja una línea por mensaje: el invitado manda HOLA con su nombre y CLIC con la carta tocada; el anfitrión manda HOLA,
// TABLERO con el tamaño y las cartas, y los eventos de ModeloMultijugador. Al terminar, cada computadora guarda su
// partida en la base (modo "red") y el ganador recibe las monedas. Estas partidas no cuentan para el ranking.
public class ControladorMultijugador {

    private final PantallaMultijugador vista;
    private int tamano = 4;
    // La conexión con el otro jugador (null mientras no hay).
    private ConexionJuego conexion = null;
    // El servidor que espera al invitado (solo el anfitrión lo tiene).
    private ServidorJuego servidor = null;
    // Guarda la partida y las monedas en la base al terminar.
    private final ModeloVictoria victoria = new ModeloVictoria();
    // Solo el anfitrión tiene modelo. soyAnfitrion dice de qué lado estoy.
    private ModeloMultijugador modelo = null;
    private boolean soyAnfitrion = true;
    // Nombre del otro jugador, llega en el mensaje HOLA.
    private String nombreRival = "Rival";
    // True cuando ya terminó la partida o se cortó la conexión (ya no se aceptan jugadas).
    private boolean terminada = false;
    // Intentos de cada jugador y de quién es el turno, se cuentan con los eventos.
    private final int[] intentos = new int[2];
    private int turnoActual = 0;
    // Puntos (parejas) de cada jugador, se actualizan con los eventos P.
    private int pts0 = 0;
    private int pts1 = 0;

    public ControladorMultijugador(PantallaMultijugador vista) {
        this.vista = vista;
        // Botones 4x4 y 6x6.
        vista.setAccionTamano(n -> { tamano = n; vista.marcarTamano(n); });
        vista.setAccionCrear(this::crear);
        vista.setAccionUnirse(this::unirse);
        vista.setAccionCarta(this::alTocarCarta);
        vista.setAccionSalir(this::salir);
        // Al entrar se empieza de cero; al salir se cierra la red.
        vista.setAlEntrar(() -> { limpiar(); vista.marcarTamano(tamano); });
        vista.setAlSalir(this::limpiar);
    }

    // Cierra todo lo de la red y deja el controlador como nuevo.
    private void limpiar() {
        if (servidor != null) {
            servidor.cerrar();
            servidor = null;
        }
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

    private static String miNombre() {
        String n = Sesion.getNombre();
        return n == null || n.isEmpty() ? "Jugador" : n;
    }

    // Lado del anfitrión, el que crea la partida.

    // Botón Crear partida: abre el puerto, muestra las IP y espera al invitado.
    private void crear() {
        limpiar();
        soyAnfitrion = true;
        vista.setLobbyOcupado(true);
        vista.setEstadoLobby(textoIps(), false);
        servidor = new ServidorJuego();
        // Se abre el puerto. Si llega alguien se llama alLlegarInvitado; si falla se muestra el error en el lobby.
        servidor.iniciar(c -> SwingUtilities.invokeLater(() -> alLlegarInvitado(c)),
                msg -> SwingUtilities.invokeLater(() -> {
                    vista.setLobbyOcupado(false);
                    vista.setEstadoLobby(msg, true);
                }));
    }

    // Texto que se muestra mientras se espera al invitado.
    private static String textoIps() {
        return "Esperando al otro jugador…<br>IP local (misma wifi): <b>" + ServidorJuego.ipLocal() + "</b>";
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
        List<String> eventos = modelo.voltear(jugador, i);
        for (String ev : eventos) {
            // Cada evento se manda al invitado y se aplica aquí.
            enviarYAplicar(ev);
            // Después de un fallo se espera un momento (se ven las dos cartas) y pasa el turno.
            if (ev.startsWith("F ")) {
                ModeloMultijugador mm = modelo;
                Timer t = new Timer(1300, e -> {
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

    // Lado del invitado, el que se une.

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
            // "TABLERO n c0,c1,c2,..." se convierte en el tamaño n y la lista de caras. Si el mensaje llega mal se ignora.
            int n;
            int[] caras;
            try {
                String[] partes = m.split(" ");
                n = Integer.parseInt(partes[1]);
                String[] txt = partes[2].split(",");
                caras = new int[txt.length];
                for (int i = 0; i < caras.length; i++) {
                    caras[i] = Integer.parseInt(txt[i]);
                }
            } catch (RuntimeException e) {
                return;
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

    // Lo que comparten los dos lados.

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

    // Al terminar se guarda la partida en el historial y el que ganó recibe las monedas. No cuenta para el ranking. Cada
    // computadora guarda lo suyo con su cuenta, en otro hilo porque usa la base de datos.
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
            Partida guardada = victoria.registrarRed(usuario, misIntentos, gano);
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
            SwingUtilities.invokeLater(() -> vista.mostrarFin("¡Terminó la partida!",
                    resultado + extra + " " + Idioma.t("Usá «Salir de la partida» para volver.")));
        }, "guardar-partida-red").start();
    }

    // Se cortó la conexión (el otro jugador cerró el juego o se fue la red).
    private void alDesconectar(ConexionJuego c) {
        if (c != conexion) {
            return;
        }
        conexion = null;
        if (terminada) {
            return;
        }
        terminada = true;
        vista.mostrarAviso("Se cortó la conexión", "El otro jugador se desconectó. Usá «Salir de la partida» para volver.");
        vista.setLobbyOcupado(false);
    }
}
