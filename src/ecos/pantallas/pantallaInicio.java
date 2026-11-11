package ecos.pantallas;

// Clases del propio proyecto que se usan aquí.
import ecos.app.Pantalla;
import ecos.componentes.ImagenPixel;
import ecos.componentes.TextoPixel;
import ecos.recursos.Fuentes;
import ecos.recursos.Sonido;
import ecos.recursos.Sprites;
// Librería gráfica AWT, Color para los colores, Component para cualquier elemento de la pantalla, Cursor para el cursor
// del mouse y GridBagLayout para acomodar en una cuadrícula flexible.
import java.awt.Color;
import java.awt.Component;
import java.awt.Cursor;
import java.awt.GridBagLayout;
// Eventos del mouse y el teclado de AWT, ActionEvent para las acciones, ComponentAdapter para reaccionar a cambios de
// tamaño, ComponentEvent para los datos de esos cambios, MouseAdapter para reaccionar al mouse y MouseEvent para los
// datos de un clic.
import java.awt.event.ActionEvent;
import java.awt.event.ComponentAdapter;
import java.awt.event.ComponentEvent;
import java.awt.event.MouseAdapter;
import java.awt.event.MouseEvent;
// Librería Swing, AbstractAction para definir acciones, Action para las acciones de los botones, Box para los espacios
// entre componentes, BoxLayout para apilar en fila o columna, BorderFactory para los bordes, JPanel para los paneles,
// KeyStroke para los atajos de teclado y Timer para los relojes.
import javax.swing.AbstractAction;
import javax.swing.Action;
import javax.swing.Box;
import javax.swing.BoxLayout;
import javax.swing.BorderFactory;
import javax.swing.JPanel;
import javax.swing.KeyStroke;
import javax.swing.Timer;

// Pantalla de bienvenida con el logo y el texto START que parpadea; al tocarlo se pasa al acceso.
public class PantallaInicio extends Pantalla {

    // Cada cuánto cambia el parpadeo de START.
    private static final int PARPADEO_MS = 550;

    // El logo, el texto START con sombra, el reloj del parpadeo y si está encendido o ya se está saliendo.
    private final ImagenPixel logo = new ImagenPixel(Sprites.logo("logo_ecos"));
    private final TextoPixel start = new TextoPixel("START", Fuentes.titulo(32f), Color.WHITE, new Color(0x0B0710), 4);
    private final Timer parpadeo;
    private boolean encendido;
    private boolean saliendo;

    // Arma la pantalla, logo y START al centro, con manito al pasar el mouse y clic que avisa al controlador.
    public PantallaInicio() {
        super("inicio", "inicio");
        setLayout(new GridBagLayout());
        setBorder(BorderFactory.createEmptyBorder(MARGEN_SUPERIOR, 16, 28, 16));

        // Se configura el logo y el texto START (centrados y con manito); al tocar START se avisa al controlador.
        logo.setAncho(560);
        logo.setAlignmentX(Component.CENTER_ALIGNMENT);
        start.setAlignmentX(Component.CENTER_ALIGNMENT);
        start.setCursor(Cursor.getPredefinedCursor(Cursor.HAND_CURSOR));
        start.addMouseListener(new MouseAdapter() {
            @Override
            public void mouseClicked(MouseEvent e) {
                irAAcceso();
            }
        });

        // El logo y START van uno debajo del otro en el centro.
        JPanel centro = new JPanel();
        centro.setOpaque(false);
        centro.setLayout(new BoxLayout(centro, BoxLayout.Y_AXIS));
        centro.add(logo);
        centro.add(Box.createVerticalStrut(18));
        centro.add(start);
        add(centro);

        // el logo se adapta al ancho de la ventana (máximo 560 px, como en el diseño)
        addComponentListener(new ComponentAdapter() {
            @Override
            public void componentResized(ComponentEvent e) {
                int ancho = Math.min(560, (int) (getWidth() * 0.86));
                int altoMax = Math.max(120, getHeight() - 260);
                logo.setAncho(Math.min(ancho, (int) (altoMax * 1244f / 527f)));
            }
        });

        // Enter o Espacio también empiezan
        Action accion = new AbstractAction() {
            @Override
            public void actionPerformed(ActionEvent e) {
                irAAcceso();
            }
        };
        getActionMap().put("start", accion);
        getInputMap(WHEN_IN_FOCUSED_WINDOW).put(KeyStroke.getKeyStroke("ENTER"), "start");
        getInputMap(WHEN_IN_FOCUSED_WINDOW).put(KeyStroke.getKeyStroke("SPACE"), "start");

        // Cada 550 ms START cambia entre claro y apagado para que parpadee.
        parpadeo = new Timer(PARPADEO_MS, e -> {
            encendido = !encendido;
            start.setOpacidad(encendido ? 1f : 0.35f);
        });
    }

    // Al entrar se reinicia el parpadeo.
    @Override
    public void alEntrar() {
        super.alEntrar();
        saliendo = false;
        encendido = true;
        start.setOpacidad(1f);
        parpadeo.start();
    }

    // Al salir se detiene el parpadeo.
    @Override
    public void alSalir() {
        parpadeo.stop();
    }

    // Qué hacer al tocar la pantalla (lo pone el controlador).
    private Runnable alContinuar = () -> { };

    // Para el controlador.
    public void setAlContinuar(Runnable accion) { alContinuar = accion; }

    // Avisa al controlador una sola vez, y solo si la pantalla se está viendo.
    private void irAAcceso() {
        if (saliendo || !isShowing()) {
            return;
        }
        saliendo = true;
        Sonido.efecto("click");
        // Avisa al controlador, que lleva al acceso.
        alContinuar.run();
    }
}
