package ecos.pantallas;

// Clases del propio proyecto que se usan aquí.
import ecos.app.Pantalla;
import ecos.app.modos.JuegoC.TableroJugador;
import ecos.app.modos.Patrones.AvisoDeslizante;
import ecos.componentes.BotonMenu;
import ecos.componentes.CampoTextoPixel;
import ecos.componentes.DialogoAviso;
import ecos.componentes.Etiqueta;
import ecos.componentes.PanelPixel;
import ecos.recursos.Sonido;
import ecos.recursos.Sprites;
// Librería gráfica AWT, para acomodar y medir.
import java.awt.BorderLayout;
import java.awt.CardLayout;
import java.awt.Component;
import java.awt.GridBagLayout;
// Acciones de Java, IntConsumer para una acción que recibe un número.
import java.util.function.IntConsumer;
// Librería Swing, para bordes, espacios, paneles y alineaciones.
import javax.swing.BorderFactory;
import javax.swing.Box;
import javax.swing.BoxLayout;
import javax.swing.JComponent;
import javax.swing.JPanel;
import javax.swing.SwingConstants;
import javax.swing.SwingUtilities;
import javax.swing.Timer;

// Multijugador en red, dos jugadores en computadoras distintas, un solo tablero y turnos alternados. Tiene dos vistas:
// el lobby (crear partida o unirse con la IP) y el tablero. No guarda el estado del juego, muestra lo que el controlador
// pide (ControladorMultijugador).
// A cargo de Hellen.
public class PantallaMultijugador extends Pantalla {

    private static final String LOBBY = "lobby";
    private static final String JUEGO = "juego";

    // Las dos vistas, una encima de la otra (se muestra una).
    private final CardLayout cartas = new CardLayout();
    private final JPanel raiz = new JPanel(cartas);

    // Lobby.
    private final Etiqueta estadoLobby = Etiqueta.texto(" ", 18f, Etiqueta.Rol.SUAVE);
    private final CampoTextoPixel campoIp = new CampoTextoPixel("IP del anfitrión (o IP:puerto)", 16);
    private final BotonMenu btn4 = new BotonMenu("4x4", BotonMenu.Estilo.PRINCIPAL);
    private final BotonMenu btn6 = new BotonMenu("6x6", BotonMenu.Estilo.PESTANA);
    private final BotonMenu crear = new BotonMenu("Crear partida", true);
    private final BotonMenu unirse = new BotonMenu("Unirse", BotonMenu.Estilo.SECUNDARIO);
    private final BotonMenu cancelar = new BotonMenu("Volver", BotonMenu.Estilo.SECUNDARIO);

    // Juego.
    private final JPanel zonaTablero = new JPanel(new GridBagLayout());
    private final Etiqueta marcador = Etiqueta.texto(" ", 22f, Etiqueta.Rol.TEXTO);
    private final Etiqueta turnoEtiqueta = Etiqueta.texto(" ", 18f, Etiqueta.Rol.SUAVE);
    private TableroJugador tablero = null;
    private String[] nombres = {"", ""};
    // Quién soy yo (0 = anfitrión, 1 = invitado) y de quién es el turno.
    private int yo = 0;
    private int turno = 0;

    // Qué hace cada botón lo pone el controlador.
    private Runnable accionCrear = () -> { };
    private Runnable accionUnirse = () -> { };
    private Runnable accionSalir = () -> { };
    private IntConsumer accionTamano = n -> { };
    private IntConsumer accionCarta = i -> { };

    public PantallaMultijugador() {
        // Id de esta pantalla y la pantalla a la que vuelve (donde se elige clásico o red).
        super("multijugador", "modo_clasico");
        setLayout(new GridBagLayout());
        setBorder(BorderFactory.createEmptyBorder(MARGEN_SUPERIOR, 16, 24, 16));
        // Las dos vistas se agregan al panel con CardLayout, solo se ve una a la vez.
        raiz.setOpaque(false);
        raiz.add(crearLobby(), LOBBY);
        raiz.add(crearJuego(), JUEGO);
        add(raiz);
    }

    // Vista del lobby.
    private JComponent crearLobby() {
        // Botones pequeños 4x4 y 6x6, avisan al controlador qué tamaño se eligió.
        btn4.setPequeno(true);
        btn6.setPequeno(true);
        btn4.addActionListener(e -> { Sonido.efecto("click"); accionTamano.accept(4); });
        btn6.addActionListener(e -> { Sonido.efecto("click"); accionTamano.accept(6); });
        // Cada botón suena y le avisa al controlador.
        crear.addActionListener(e -> { Sonido.efecto("click"); accionCrear.run(); });
        unirse.addActionListener(e -> { Sonido.efecto("click"); accionUnirse.run(); });
        cancelar.addActionListener(e -> { Sonido.efecto("click"); accionSalir.run(); });
        estadoLobby.setHorizontalAlignment(SwingConstants.CENTER);

        // Fila "TABLERO [4x4] [6x6]".
        JPanel filaTamano = new JPanel();
        filaTamano.setOpaque(false);
        filaTamano.setLayout(new BoxLayout(filaTamano, BoxLayout.X_AXIS));
        filaTamano.add(Etiqueta.titulo("TABLERO", 10f, Etiqueta.Rol.SUAVE));
        filaTamano.add(Box.createHorizontalStrut(10));
        filaTamano.add(btn4);
        filaTamano.add(Box.createHorizontalStrut(8));
        filaTamano.add(btn6);

        // Tarjeta con todo el lobby, de arriba hacia abajo: título, tablero, crear, IP, unirse, mensaje y volver.
        PanelPixel tarjeta = new PanelPixel();
        tarjeta.setLayout(new BoxLayout(tarjeta, BoxLayout.Y_AXIS));
        tarjeta.setPadding(22);
        tarjeta.add(izq(Etiqueta.texto("Multijugador en red", 32f, Etiqueta.Rol.TEXTO)));
        tarjeta.add(Box.createVerticalStrut(6));
        tarjeta.add(izq(Etiqueta.texto("<html><body style='width:380px'>Dos computadoras, un tablero. Ganás monedas si ganás la partida, no cuenta para el ranking.</body></html>", 16f, Etiqueta.Rol.SUAVE)));
        tarjeta.add(Box.createVerticalStrut(16));
        tarjeta.add(izq(Etiqueta.titulo("SI VAS A CREAR LA PARTIDA", 10f, Etiqueta.Rol.SUAVE)));
        tarjeta.add(Box.createVerticalStrut(6));
        tarjeta.add(izq(filaTamano));
        tarjeta.add(Box.createVerticalStrut(8));
        tarjeta.add(izq(crear.anchoCompleto()));
        tarjeta.add(Box.createVerticalStrut(16));
        tarjeta.add(izq(Etiqueta.titulo("SI TE VAS A UNIR", 10f, Etiqueta.Rol.SUAVE)));
        tarjeta.add(izq(campoIp));
        tarjeta.add(Box.createVerticalStrut(8));
        tarjeta.add(izq(unirse.anchoCompleto()));
        tarjeta.add(Box.createVerticalStrut(10));
        tarjeta.add(izq(estadoLobby));
        tarjeta.add(Box.createVerticalStrut(10));
        tarjeta.add(izq(cancelar.anchoCompleto()));

        // Se centra la tarjeta en la pantalla.
        JPanel centro = new JPanel(new GridBagLayout());
        centro.setOpaque(false);
        centro.add(tarjeta);
        return centro;
    }

    // Vista del juego, marcador arriba, tablero al centro y botón Salir.
    private JComponent crearJuego() {
        marcador.setHorizontalAlignment(SwingConstants.CENTER);
        turnoEtiqueta.setHorizontalAlignment(SwingConstants.CENTER);
        marcador.setAlignmentX(Component.CENTER_ALIGNMENT);
        turnoEtiqueta.setAlignmentX(Component.CENTER_ALIGNMENT);
        zonaTablero.setOpaque(false);
        zonaTablero.setAlignmentX(Component.CENTER_ALIGNMENT);
        // Botón para abandonar la partida (cierra la conexión y vuelve).
        BotonMenu salir = new BotonMenu("Salir de la partida", BotonMenu.Estilo.SECUNDARIO);
        salir.addActionListener(e -> { Sonido.efecto("click"); accionSalir.run(); });
        salir.setAlignmentX(Component.CENTER_ALIGNMENT);

        // Columna centrada: marcador, de quién es el turno, tablero y botón Salir.
        JPanel col = new JPanel();
        col.setOpaque(false);
        col.setLayout(new BoxLayout(col, BoxLayout.Y_AXIS));
        col.add(Box.createVerticalStrut(10));
        col.add(marcador);
        col.add(Box.createVerticalStrut(4));
        col.add(turnoEtiqueta);
        col.add(Box.createVerticalStrut(8));
        col.add(zonaTablero);
        col.add(Box.createVerticalStrut(8));
        col.add(salir);
        return col;
    }

    // Se ejecuta cada vez que se entra a esta pantalla, siempre se empieza en el lobby.
    @Override
    public void alEntrar() {
        super.alEntrar();
        mostrarLobby();
    }

    // Métodos que usa el controlador (ControladorMultijugador).

    public void setAccionCrear(Runnable a) { accionCrear = a; }
    public void setAccionUnirse(Runnable a) { accionUnirse = a; }
    public void setAccionSalir(Runnable a) { accionSalir = a; }
    public void setAccionTamano(IntConsumer a) { accionTamano = a; }
    public void setAccionCarta(IntConsumer a) { accionCarta = a; }

    public String getIp() { return campoIp.getText().trim(); }

    // Vuelve a la pantalla donde se elige entre clásico y multijugador en red.
    public void irAModo() { irA("modo_clasico"); }

    // Resalta el tamaño elegido.
    public void marcarTamano(int n) {
        btn4.setEstilo(n == 4 ? BotonMenu.Estilo.PRINCIPAL : BotonMenu.Estilo.PESTANA);
        btn6.setEstilo(n == 6 ? BotonMenu.Estilo.PRINCIPAL : BotonMenu.Estilo.PESTANA);
    }

    // Muestra el lobby y lo deja listo para empezar.
    public void mostrarLobby() {
        cartas.show(raiz, LOBBY);
        setLobbyOcupado(false);
        setEstadoLobby(" ", false);
    }

    // Mensaje del lobby (en rojo si es error).
    public void setEstadoLobby(String texto, boolean error) {
        estadoLobby.setText("<html><body style='width:380px'>" + texto + "</body></html>");
        estadoLobby.setRol(error ? Etiqueta.Rol.PELIGRO : Etiqueta.Rol.SUAVE);
    }

    // Mientras se espera o se conecta, se desactivan los botones de crear y unirse.
    public void setLobbyOcupado(boolean ocupado) {
        crear.setEnabled(!ocupado);
        unirse.setEnabled(!ocupado);
        btn4.setEnabled(!ocupado);
        btn6.setEnabled(!ocupado);
    }

    // Arma el tablero de la partida con las caras que mandó el anfitrión y muestra la vista del juego.
    public void mostrarTablero(int n, int[] caras, String nombreAnfitrion, String nombreInvitado, int miIndice) {
        nombres = new String[]{nombreAnfitrion, nombreInvitado};
        yo = miIndice;
        // -1 para que el primer aviso de turno siempre se muestre.
        turno = -1;
        // Tamaño de carta según la altura de la ventana, igual que el clásico.
        int h = getHeight() > 0 ? getHeight() : 720;
        int lado = Math.max(40, Math.min(n == 4 ? 130 : 96, (h - 290) / n));
        // Se crea el tablero y a cada carta se le pone su dibujo y qué hacer al tocarla (avisar el número de carta).
        tablero = new TableroJugador(n, lado);
        for (int i = 0; i < caras.length; i++) {
            final int idx = i;
            tablero.getCarta(i).setCara(Sprites.icono(caras[i] + 1, 128));
            tablero.getCarta(i).setAlClic(() -> accionCarta.accept(idx));
        }
        // Nadie puede tocar hasta que llegue el primer turno.
        tablero.setActivo(false);
        zonaTablero.removeAll();
        zonaTablero.add(tablero);
        zonaTablero.revalidate();
        setPuntos(0, 0);
        // Se cambia a la vista del juego y las cartas caen con la animación.
        cartas.show(raiz, JUEGO);
        tablero.cascada(0);
        revalidate();
        repaint();
    }

    // Cambia el marcador.
    public void setPuntos(int p0, int p1) {
        marcador.setText(nombres[0] + ": " + p0 + "  -  " + nombres[1] + ": " + p1);
    }

    // Cambia de quién es el turno. Solo en mi turno se pueden tocar las cartas.
    public void setTurno(int j) {
        boolean cambio = j != turno;
        turno = j;
        // True si el turno es mío; solo en ese caso se pueden tocar las cartas.
        boolean mio = j == yo;
        turnoEtiqueta.setText(mio ? "¡Es tu turno!" : "Turno de " + nombres[j]);
        turnoEtiqueta.setRol(mio ? Etiqueta.Rol.ACIERTO : Etiqueta.Rol.SUAVE);
        if (tablero != null) {
            tablero.setActivo(mio);
        }
        // Solo si la pantalla ya está dentro de la ventana.
        if (cambio && SwingUtilities.getRootPane(this) != null) {
            Sonido.efecto("cambio_turno");
            AvisoDeslizante.mostrar(this, mio ? "TU TURNO" : "TURNO DE " + nombres[j].toUpperCase(),
                    j == 0 ? AvisoDeslizante.Estilo.JUGADOR1 : AvisoDeslizante.Estilo.JUGADOR2, Sprites.mascota(j + 1, 44));
        }
    }

    // Nadie puede tocar cartas (mientras se espera tras un fallo).
    public void bloquear() {
        if (tablero != null) {
            tablero.setActivo(false);
        }
    }

    // Voltea la carta i.
    public void voltear(int i) {
        if (tablero != null) {
            Sonido.efecto("voltear_carta");
            tablero.getCarta(i).voltear(true);
        }
    }

    // Marca la pareja a y b un momento después, para que se vea la segunda carta.
    public void marcarPareja(int a, int b) {
        Timer t = new Timer(600, e -> {
            if (tablero != null) {
                Sonido.efecto("par_encontrado");
                tablero.getCarta(a).marcarHecha();
                tablero.getCarta(b).marcarHecha();
            }
        });
        t.setRepeats(false);
        t.start();
    }

    // Tapa otra vez las cartas a y b después de un momento.
    public void taparFallo(int a, int b) {
        Sonido.efecto("fallo");
        Timer t = new Timer(1000, e -> {
            if (tablero != null) {
                tablero.getCarta(a).voltear(false);
                tablero.getCarta(b).voltear(false);
            }
        });
        t.setRepeats(false);
        t.start();
    }

    // Aviso final con el resultado. Al cerrarlo se vuelve al menú con el botón Salir.
    public void mostrarFin(String titulo, String mensaje) {
        bloquear();
        turnoEtiqueta.setText(" ");
        Sonido.efecto(Sonido.EXITO);
        if (SwingUtilities.getRootPane(this) != null) {
            new DialogoAviso(titulo, mensaje, Sprites.ui("trophy", 48)).mostrar(this);
        }
    }

    // Aviso cuando algo sale mal en plena partida (por ejemplo se desconectó el otro jugador).
    public void mostrarAviso(String titulo, String mensaje) {
        bloquear();
        Sonido.efecto("error");
        if (SwingUtilities.getRootPane(this) != null) {
            new DialogoAviso(titulo, mensaje, Sprites.ui("eye", 48)).mostrar(this);
        }
    }

    private static JComponent izq(JComponent c) {
        c.setAlignmentX(Component.LEFT_ALIGNMENT);
        return c;
    }
}
