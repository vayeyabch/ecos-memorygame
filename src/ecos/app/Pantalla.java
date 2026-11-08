package ecos.app;

// Clases del propio proyecto que se usan aquí.
import ecos.recursos.Sonido;
import ecos.recursos.Sprites;
// Librería gráfica AWT, Graphics para dibujar, Graphics2D para dibujar con más opciones y RenderingHints para la calidad
// del dibujo.
import java.awt.Graphics;
import java.awt.Graphics2D;
import java.awt.RenderingHints;
// Librería Swing, ImageIcon para los iconos y JPanel para los paneles.
import javax.swing.ImageIcon;
import javax.swing.JPanel;

// Clase base de todas las pantallas, guarda su id, su música y qué hacer al entrar y al salir.
// Clase base abstracta, no se crea directamente, solo se hereda.
public abstract class Pantalla extends JPanel {

    // Espacio que dejan las pantallas arriba para que no las tape la barra superior.
    public static final int MARGEN_SUPERIOR = 72;

    // Nombre único de la pantalla, que sirve para navegar hacia ella, y nombre de la canción que suena en ella. También
    // guarda el navegador que la controla, que se asigna al registrarla, y qué hacer al entrar, que empieza sin hacer nada.
    private final String id;
    private final String musica;
    private Navegador navegador;
    private Runnable accionEntrar = () -> { };
    // Qué hacer al salir (empieza sin hacer nada). true = dibuja el fondo animado; false = no lo dibuja.
    private Runnable accionSalir = () -> { };
    private boolean conFondo = true;

    // Constructor que las hijas llaman con super("id", "musica").
    protected Pantalla(String id, String musica) {
        // Guarda el id de la pantalla. Guarda el nombre de su música. Indica que el panel pinta todo su fondo. Pide repintar
        // la pantalla cada vez que cambie el tema.
        this.id = id;
        this.musica = musica;
        setOpaque(true);
        Tema.alCambiar(this::repaint);
    }

    // Devuelve el id de la pantalla.
    public String getId() {
        // Entrega el nombre guardado.
        return id;
    }

    // Lo llama el Navegador al registrar la pantalla.
    // Sin "public", solo lo ve el paquete app.
    void setNavegador(Navegador navegador) {
        // Guarda el navegador para poder moverse entre pantallas.
        this.navegador = navegador;
    }

    // Va a otra pantalla por su id.
    protected void irA(String idPantalla) {
        // Solo si la pantalla ya está registrada en un navegador. Le pide al navegador que muestre esa pantalla.
        if (navegador != null) {
            navegador.ir(idPantalla);
        }
    }

    // Regresa a la pantalla anterior.
    protected void volver() {
        // Solo si ya tiene navegador. Le pide al navegador que retroceda una pantalla.
        if (navegador != null) {
            navegador.atras();
        }
    }

    // Si se pone false, la pantalla no dibuja el fondo animado.
    // Activa o desactiva el fondo.
    protected void setConFondo(boolean conFondo) {
        // Guarda la decisión.
        this.conFondo = conFondo;
    }

    // Se ejecuta cada vez que la pantalla se muestra.
    // Las hijas pueden sobrescribirlo para hacer cosas al entrar.
    public void alEntrar() {
        // Pone la música de esta pantalla. Repinta la pantalla. Avisa a quien quiera enterarse (por ejemplo, un controlador).
        Sonido.musica(musica);
        repaint();
        accionEntrar.run();
    }

    // El controlador usa esto para arrancar cuando la pantalla se muestra.
    // Recibe lo que se quiere ejecutar al entrar.
    public void setAlEntrar(Runnable accion) {
        this.accionEntrar = accion;
    }

    // El controlador usa esto para parar sus relojes cuando la pantalla se oculta.
    // Recibe lo que se quiere ejecutar al salir.
    public void setAlSalir(Runnable accion) {
        this.accionSalir = accion;
    }

    // Se ejecuta cuando la pantalla se oculta.
    // Las hijas lo usan si necesitan limpiar algo.
    public void alSalir() {
        // Cierra cualquier ventana abierta encima. Avisa a quien quiera enterarse.
        ecos.componentes.CapaModal.cerrarTodas();
        accionSalir.run();
    }

    // Swing lo llama cada vez que hay que dibujar el panel.
    @Override
    protected void paintComponent(Graphics g) {
        // Dibuja primero lo básico del panel. Solo si la pantalla tiene fondo activado. Pide el fondo del tema actual
        // (montaña o noche). Ancho original del gif.
        super.paintComponent(g);
        if (conFondo) {
            ImageIcon fondo = Sprites.fondo();
            int ancho = fondo.getIconWidth();
            // Si el gif todavía no cargó no dibuja nada, y si cargó toma su alto original.
            int alto = fondo.getIconHeight();
            if (ancho <= 0 || alto <= 0) {
                return;
            }
            // Calcula la escala mínima para cubrir toda la pantalla sin deformar, con el ancho y el alto finales del fondo,
            // y usa una copia del pincel para no afectar al original.
            double escala = Math.max(getWidth() / (double) ancho, getHeight() / (double) alto);
            int w = (int) Math.ceil(ancho * escala);
            int h = (int) Math.ceil(alto * escala);
            Graphics2D g2 = (Graphics2D) g.create();
            // Sin suavizar, se mantiene el pixel art. Dibuja el fondo centrado y pegado abajo (lo que sobra se corta arriba).
            // Libera el pincel.
            g2.setRenderingHint(RenderingHints.KEY_INTERPOLATION, RenderingHints.VALUE_INTERPOLATION_NEAREST_NEIGHBOR);
            g2.drawImage(fondo.getImage(), (getWidth() - w) / 2, getHeight() - h, w, h, this);
            g2.dispose();
        }
    }
}
