package ecos.pantallas;

// Pantalla es el molde del que hereda esta pantalla. Le da el id, la música y los ganchos de entrar y salir.
import ecos.app.Pantalla;
// ImagenPixel es la pieza que muestra una imagen sin suavizarla. Aquí muestra el logo.
import ecos.componentes.ImagenPixel;
// TextoPixel es la pieza que dibuja un texto con sombra y se le puede cambiar la opacidad. Aquí es el texto START.
import ecos.componentes.TextoPixel;
// Fuentes da la letra de títulos para el texto START.
import ecos.recursos.Fuentes;
// Sonido reproduce el efecto de clic.
import ecos.recursos.Sonido;
// Sprites da la imagen del logo.
import ecos.recursos.Sprites;
// Color es el tipo de un color. Se usa para el color del texto y de su sombra.
import java.awt.Color;
// Component trae las constantes de alineación, como CENTER_ALIGNMENT.
import java.awt.Component;
// Cursor es el puntero del mouse. Aquí se usa la manito sobre START.
import java.awt.Cursor;
// GridBagLayout acomoda las piezas en una cuadrícula flexible. Con una sola pieza, la deja centrada.
import java.awt.GridBagLayout;
// ActionEvent es el aviso que llega cuando se activa una acción, como al apretar una tecla enlazada.
import java.awt.event.ActionEvent;
// ComponentAdapter es una clase con los eventos de un componente ya vacíos, para escribir solo el que se necesita.
import java.awt.event.ComponentAdapter;
// ComponentEvent es el aviso que llega cuando un componente cambia, como cuando cambia de tamaño.
import java.awt.event.ComponentEvent;
// MouseAdapter es una clase con los eventos del mouse ya vacíos, para escribir solo los que se necesitan.
import java.awt.event.MouseAdapter;
// MouseEvent es el aviso que llega cuando el mouse hace algo, como un clic.
import java.awt.event.MouseEvent;
// AbstractAction es una acción de Swing casi lista. Solo hay que escribir qué hace.
import javax.swing.AbstractAction;
// Action es el tipo de una acción de Swing. Se puede enlazar a una tecla.
import javax.swing.Action;
// Box trae los espacios invisibles. Aquí separa el logo de START.
import javax.swing.Box;
// BoxLayout acomoda las piezas una debajo de otra. Aquí se usa en el panel central.
import javax.swing.BoxLayout;
// BorderFactory crea bordes. Aquí crea un borde vacío, que sirve de margen.
import javax.swing.BorderFactory;
// JPanel es un panel, un contenedor donde se meten otras piezas.
import javax.swing.JPanel;
// KeyStroke representa una tecla, como Enter o Espacio, para enlazarla a una acción.
import javax.swing.KeyStroke;
// Timer ejecuta una acción cada cierto tiempo. Aquí hace parpadear el texto START.
import javax.swing.Timer;

// Es la pantalla de bienvenida, con el logo y el texto START que parpadea. Desde aquí se pasa al acceso.
// Es una vista: dibuja y avisa. No decide a dónde ir, eso lo hace su controlador con setAlContinuar.
// Hereda de Pantalla, así que ya trae su id, su música y el fondo animado.
public class PantallaInicio extends Pantalla {

    // Cada cuántos milisegundos cambia el parpadeo de START.
    private static final int PARPADEO_MS = 550;

    // El logo, tomado de Sprites por su nombre.
    private final ImagenPixel logo = new ImagenPixel(Sprites.logo("logo_ecos"));
    // El texto START con la letra de títulos, tamaño 32, color blanco y una sombra oscura. El último número, 4, es el de la sombra.
    private final TextoPixel start = new TextoPixel("START", Fuentes.titulo(32f), Color.WHITE, new Color(0x0B0710), 4);
    // El reloj que hace parpadear a START. Se crea al final del constructor porque necesita usar start.
    private final Timer parpadeo;
    // Dice si START está encendido ahora mismo, para alternar entre fuerte y apagado.
    private boolean encendido;
    // Dice si ya se está saliendo de la pantalla, para no avisar dos veces con un doble clic.
    private boolean saliendo;

    // Arma la pantalla: su disposición, el logo, START, las teclas y el parpadeo.
    public PantallaInicio() {
        // super llama al constructor de Pantalla con el id "inicio" y la música "inicio".
        super("inicio", "inicio");
        // Con GridBagLayout y una sola pieza, esa pieza queda centrada en la pantalla.
        setLayout(new GridBagLayout());
        // Margen vacío: arriba deja el espacio de la barra superior, 16 a los lados y 28 abajo.
        setBorder(BorderFactory.createEmptyBorder(MARGEN_SUPERIOR, 16, 28, 16));

        // Ancho inicial del logo y alineación al centro para los dos.
        logo.setAncho(560);
        logo.setAlignmentX(Component.CENTER_ALIGNMENT);
        start.setAlignmentX(Component.CENTER_ALIGNMENT);
        // Manito sobre START, para que parezca clicable.
        start.setCursor(Cursor.getPredefinedCursor(Cursor.HAND_CURSOR));
        // Al hacer clic en START, pasa al acceso. Es una clase anónima: se crea y se usa en el mismo lugar.
        start.addMouseListener(new MouseAdapter() {
            @Override
            public void mouseClicked(MouseEvent e) {
                irAAcceso();
            }
        });

        // Un panel transparente con el logo arriba y START abajo, una pieza debajo de otra.
        JPanel centro = new JPanel();
        centro.setOpaque(false);
        centro.setLayout(new BoxLayout(centro, BoxLayout.Y_AXIS));
        centro.add(logo);
        // Espacio invisible de 18 píxeles entre el logo y START.
        centro.add(Box.createVerticalStrut(18));
        centro.add(start);
        // Se agrega a la pantalla, y el GridBagLayout lo deja en el centro.
        add(centro);

        // El logo se adapta al tamaño de la ventana, con un máximo de 560 píxeles de ancho.
        // componentResized se ejecuta cada vez que la pantalla cambia de tamaño.
        addComponentListener(new ComponentAdapter() {
            @Override
            public void componentResized(ComponentEvent e) {
                // El ancho es el 86% de la pantalla, pero nunca más de 560.
                int ancho = Math.min(560, (int) (getWidth() * 0.86));
                // El alto máximo que puede ocupar el logo, que es el alto de la pantalla menos 260, y nunca menos de 120.
                int altoMax = Math.max(120, getHeight() - 260);
                // 1244 y 527 son las proporciones del logo. Se calcula el ancho que haría que su alto quepa, y se usa el menor.
                logo.setAncho(Math.min(ancho, (int) (altoMax * 1244f / 527f)));
            }
        });

        // Enter o Espacio también empiezan. Primero se crea una acción que llama a irAAcceso.
        Action accion = new AbstractAction() {
            @Override
            public void actionPerformed(ActionEvent e) {
                irAAcceso();
            }
        };
        // Se guarda la acción con el nombre "start", y luego se enlazan las dos teclas a ese nombre.
        // WHEN_IN_FOCUSED_WINDOW hace que funcione mientras la ventana esté activa, sin importar qué pieza tenga el foco.
        getActionMap().put("start", accion);
        getInputMap(WHEN_IN_FOCUSED_WINDOW).put(KeyStroke.getKeyStroke("ENTER"), "start");
        getInputMap(WHEN_IN_FOCUSED_WINDOW).put(KeyStroke.getKeyStroke("SPACE"), "start");

        // El reloj del parpadeo. Cada 550 milisegundos invierte encendido y cambia la opacidad de START:
        // 1 es opaco del todo y 0.35 es semitransparente. La flecha es la función que se ejecuta en cada tic.
        parpadeo = new Timer(PARPADEO_MS, e -> {
            encendido = !encendido;
            start.setOpacidad(encendido ? 1f : 0.35f);
        });
    }

    // Se ejecuta cada vez que se muestra la pantalla. Reinicia el estado y arranca el parpadeo.
    @Override
    public void alEntrar() {
        // super.alEntrar pone la música, repinta y ejecuta la acción que dejó el controlador.
        super.alEntrar();
        // Permite volver a salir si el usuario regresa a esta pantalla.
        saliendo = false;
        encendido = true;
        start.setOpacidad(1f);
        parpadeo.start();
    }

    // Se ejecuta cuando la pantalla se oculta. Para el reloj, para que no siga parpadeando una pantalla que nadie ve.
    // Este método no llama a super.alSalir, así que aquí no se cierran diálogos ni se ejecuta setAlSalir.
    // En esta pantalla no hace falta, porque no abre diálogos.
    @Override
    public void alSalir() {
        parpadeo.stop();
    }

    // La acción que el controlador deja para cuando el usuario quiere continuar. Empieza vacía para no fallar si nadie la llena.
    private Runnable alContinuar = () -> { };

    // Es el gancho de MVC: el controlador llama a este método y guarda aquí lo que debe pasar, o sea ir al acceso.
    public void setAlContinuar(Runnable accion) { alContinuar = accion; }

    // Avisa al controlador que el usuario quiere continuar. Se llama con el clic en START, con Enter o con Espacio.
    // Avisa una sola vez y solo si la pantalla se está viendo.
    private void irAAcceso() {
        // El || significa o. isShowing dice si la pantalla está visible ahora. El ! significa no.
        // Si ya se estaba saliendo, o la pantalla no se ve, no hace nada.
        if (saliendo || !isShowing()) {
            return;
        }
        // Marca que ya se está saliendo, para que un doble clic no avise dos veces.
        saliendo = true;
        Sonido.efecto("click");
        // Ejecuta lo que el controlador dejó guardado.
        alContinuar.run();
    }
}