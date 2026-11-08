package ecos.pantallas;

// Clases del propio proyecto que se usan aquí.
import ecos.app.Pantalla;
import ecos.componentes.BotonMenu;
import ecos.componentes.Etiqueta;
import ecos.componentes.ListaReglas;
import ecos.componentes.PanelPixel;
import ecos.recursos.Sonido;
// Librería gráfica AWT, Component para cualquier elemento de la pantalla, Dimension para los tamaños, FlowLayout para
// acomodar en fila y GridBagLayout para acomodar en una cuadrícula flexible.
import java.awt.Component;
import java.awt.Dimension;
import java.awt.FlowLayout;
import java.awt.GridBagLayout;
// Librería Swing, BorderFactory para los bordes, Box para los espacios entre componentes, BoxLayout para apilar en fila
// o columna, JComponent para la base de los componentes y JPanel para los paneles.
import javax.swing.BorderFactory;
import javax.swing.Box;
import javax.swing.BoxLayout;
import javax.swing.JComponent;
import javax.swing.JPanel;

// Pantalla de nueva partida clásica, el jugador 1 es quien inició sesión y el jugador 2 puede iniciar sesión o jugar sin cuenta.
public class PantallaReglasClasico extends Pantalla {

    // Quién es el jugador 1 (quien inició sesión). cómo va a jugar el jugador 2.
    private final Etiqueta jugador1 = Etiqueta.texto(" ", 26f, Etiqueta.Rol.TEXTO);
    private final Etiqueta estado = Etiqueta.texto(" ", 20f, Etiqueta.Rol.SUAVE);
    // Las cuatro reglas del juego (la primera y la última las cambia el controlador).
    private final ListaReglas reglas = new ListaReglas(460,
            "Cada jugador tiene su tablero con las parejas en otro orden.",
            "Tenés 10 segundos por jugada. Si encontrás una pareja, se te suman 7 segundos y seguís jugando; si fallás, pasa el turno.",
            "Gana quien encuentre todas las parejas con menos intentos. El ranking y el premio en dinero salen de esos intentos (solo con cuenta).",
            "Cada victoria te da monedas para comprar el Ojo espía.");
    // Qué hacer al tocar Iniciar sesión, Jugar sin cuenta y Comenzar, que lo pone el controlador.
    private Runnable alIniciarSesion = () -> { };
    private Runnable alInvitado = () -> { };
    private Runnable alComenzar = () -> { };

    public PantallaReglasClasico() {
        super("reglas_clasico", "menu");
        setLayout(new GridBagLayout());
        setBorder(BorderFactory.createEmptyBorder(MARGEN_SUPERIOR, 16, 24, 16));

        // Los botones del jugador 2 (iniciar sesión o jugar sin cuenta) y el botón Comenzar; todos solo avisan al controlador.
        BotonMenu iniciar = new BotonMenu("Iniciar sesión", BotonMenu.Estilo.ROSA);
        iniciar.addActionListener(e -> { Sonido.efecto("click"); alIniciarSesion.run(); });
        BotonMenu invitado = new BotonMenu("Jugar sin cuenta", BotonMenu.Estilo.SECUNDARIO);
        invitado.addActionListener(e -> { Sonido.efecto("click"); alInvitado.run(); });
        // Los dos botones del jugador 2.
        JPanel opciones = new JPanel(new FlowLayout(FlowLayout.LEFT, 0, 0));
        opciones.setOpaque(false);
        opciones.add(iniciar);
        opciones.add(Box.createHorizontalStrut(10));
        opciones.add(invitado);
        BotonMenu comenzar = new BotonMenu("¡Comenzar!", true);
        comenzar.addActionListener(e -> { Sonido.efecto("click"); alComenzar.run(); });

        // La tarjeta del centro, ancho mínimo de 560 px y margen adentro.
        PanelPixel tarjeta = new PanelPixel() {
            @Override
            public Dimension getPreferredSize() {
                return new Dimension(Math.max(560, super.getPreferredSize().width), super.getPreferredSize().height);
            }
        };
        tarjeta.setLayout(new BoxLayout(tarjeta, BoxLayout.Y_AXIS));
        tarjeta.setPadding(18);
        // Se agregan los títulos, el nombre del jugador 1, las opciones del jugador 2, su mensaje, las reglas y Comenzar.
        JComponent[] piezas = {
            Etiqueta.titulo("Nueva partida clásica", 16f, Etiqueta.Rol.TEXTO),
            Etiqueta.titulo("JUGADOR 1", 10f, Etiqueta.Rol.SUAVE), jugador1,
            Etiqueta.titulo("JUGADOR 2", 10f, Etiqueta.Rol.SUAVE), opciones, estado,
            reglas, comenzar.anchoCompleto()};
        // Cada pieza va alineada a la izquierda con un espacio abajo (más chico entre los títulos y sus datos).
        for (JComponent c : piezas) {
            c.setAlignmentX(Component.LEFT_ALIGNMENT);
            tarjeta.add(c);
            tarjeta.add(Box.createVerticalStrut(c instanceof Etiqueta ? (c == piezas[0] ? 14 : 6) : 14));
        }
        add(tarjeta);
    }

    // Métodos que usa el controlador para conectar los botones y cambiar los textos.
    // Métodos que usa el controlador (ControladorReglasClasico).

    // Se ejecuta al tocar "Iniciar sesión".
    public void setAlIniciarSesion(Runnable accion) { alIniciarSesion = accion; }

    // Se ejecuta al tocar "Jugar sin cuenta".
    public void setAlInvitado(Runnable accion) { alInvitado = accion; }

    // Se ejecuta al tocar Comenzar.
    public void setAlComenzar(Runnable accion) { alComenzar = accion; }

    // Escribe el nombre del jugador 1.
    public void setJugador1(String nombre) { jugador1.setText(nombre); }

    // Cambia la primera regla.
    public void setReglaTablero(String texto) { reglas.setRegla(0, texto); }

    // Cambia la última regla.
    public void setReglaMonedas(String texto) { reglas.setRegla(3, texto); }

    // Mensaje de debajo de los botones; "rol" decide el color (SUAVE normal, ACIERTO ok, PELIGRO aviso).
    public void setEstadoJugador2(String texto, Etiqueta.Rol rol) {
        estado.setText("<html><div style='width:460px'>" + ecos.recursos.Fuentes.conNumeros(texto) + "</div></html>");
        estado.setRol(rol);
        revalidate();
    }

    // Suena cuando falta algo.
    public void sonarError() { Sonido.efecto("error"); }
}
