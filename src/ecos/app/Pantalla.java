package ecos.app;

// Sonido: reproduce la música de cada pantalla al entrar.
import ecos.recursos.Sonido;
// Sprites: entrega el fondo animado (gif) que se dibuja detrás de la pantalla.
import ecos.recursos.Sprites;
// Graphics: el "pincel" con el que se dibuja el fondo en paintComponent.
import java.awt.Graphics;
// Graphics2D: versión del pincel con más opciones; se usa para desactivar el suavizado.
import java.awt.Graphics2D;
// RenderingHints: ajustes de dibujo; aquí sirve para escalar sin suavizar y mantener el pixel art.
import java.awt.RenderingHints;
// ImageIcon: guarda el fondo y mantiene animado el gif.
import javax.swing.ImageIcon;
// JPanel: clase base de la que hereda Pantalla; es un panel donde se ponen los componentes.
import javax.swing.JPanel;

// Base de todas las pantallas. Guarda su id, su música y qué hacer al entrar y al salir.
// abstract: no se puede crear con new, solo sirve para que otras clases hereden de ella.
public abstract class Pantalla extends JPanel {

    // Espacio que dejan las pantallas arriba para que no las tape la barra superior.
    // public static final: constante que se lee como Pantalla.MARGEN_SUPERIOR.
    public static final int MARGEN_SUPERIOR = 72;

    // Id de la pantalla, su música, el navegador (se asigna al registrarla) y qué hacer al entrar.
    private final String id;
    private final String musica;
    private Navegador navegador;
    // Runnable vacío por defecto ( () -> { } ): así accionEntrar.run() nunca falla aunque nadie lo configure.
    private Runnable accionEntrar = () -> { };
    // Qué hacer al salir, y si dibuja el fondo animado.
    private Runnable accionSalir = () -> { };
    private boolean conFondo = true;

    // Constructor protegido: solo lo llaman las pantallas hijas con super(id, musica).
    protected Pantalla(String id, String musica) {
        this.id = id;
        this.musica = musica;
        // Opaco: la pantalla pinta todo su rectángulo, no deja ver lo de atrás.
        setOpaque(true);
        // Se suscribe a Tema para repintarse (y cambiar el fondo) cuando cambia el modo claro/oscuro.
        Tema.alCambiar(this::repaint);
    }

    // Devuelve el id; el Navegador lo usa para registrarla.
    public String getId() {
        return id;
    }

    // Lo llama el Navegador al registrar la pantalla. Sin modificador: solo se ve dentro del paquete ecos.app.
    void setNavegador(Navegador navegador) {
        this.navegador = navegador;
    }

    // Pide al Navegador ir a otra pantalla. El if evita un error si todavía no tiene navegador (null).
    protected void irA(String idPantalla) {
        if (navegador != null) {
            navegador.ir(idPantalla);
        }
    }

    // Pide al Navegador volver a la pantalla anterior.
    protected void volver() {
        if (navegador != null) {
            navegador.atras();
        }
    }

    // Con false la pantalla no dibuja el fondo animado.
    protected void setConFondo(boolean conFondo) {
        this.conFondo = conFondo;
    }

    // Se ejecuta cada vez que se muestra la pantalla. Las hijas lo sobrescriben si necesitan algo al entrar.
    public void alEntrar() {
        // Pone la música de esta pantalla (si ya suena la misma, no se reinicia).
        Sonido.musica(musica);
        repaint();
        // Ejecuta lo que el controlador dejó con setAlEntrar.
        accionEntrar.run();
    }

    // El controlador lo usa para arrancar cuando la pantalla se muestra.
    public void setAlEntrar(Runnable accion) {
        this.accionEntrar = accion;
    }

    // El controlador lo usa para parar sus relojes cuando la pantalla se oculta.
    public void setAlSalir(Runnable accion) {
        this.accionSalir = accion;
    }

    // Se ejecuta cuando la pantalla se oculta. Las hijas lo sobrescriben si necesitan limpiar algo.
    public void alSalir() {
        // Cierra los diálogos que hayan quedado abiertos encima.
        ecos.componentes.CapaModal.cerrarTodas();
        // Ejecuta lo que el controlador dejó con setAlSalir.
        accionSalir.run();
    }

    // Dibuja el fondo animado detrás de todo lo de la pantalla.
    @Override
    protected void paintComponent(Graphics g) {
        // Primero pinta lo normal del panel (el fondo sólido).
        super.paintComponent(g);
        if (conFondo) {
            ImageIcon fondo = Sprites.fondo();
            int ancho = fondo.getIconWidth();
            // Si el gif todavía no cargó no dibuja nada.
            int alto = fondo.getIconHeight();
            if (ancho <= 0 || alto <= 0) {
                return;
            }
            // Escala mínima para cubrir toda la pantalla sin deformar, con una copia del pincel para no afectar el
            // original. Math.max toma la escala mayor entre la del ancho y la del alto, así no quedan bordes vacíos.
            double escala = Math.max(getWidth() / (double) ancho, getHeight() / (double) alto);
            // Math.ceil redondea hacia arriba para que no quede una línea sin cubrir.
            int w = (int) Math.ceil(ancho * escala);
            int h = (int) Math.ceil(alto * escala);
            Graphics2D g2 = (Graphics2D) g.create();
            // Sin suavizar, para mantener el pixel art. El fondo va centrado y pegado abajo; lo que sobra se corta
            // arriba.
            g2.setRenderingHint(RenderingHints.KEY_INTERPOLATION, RenderingHints.VALUE_INTERPOLATION_NEAREST_NEIGHBOR);
            g2.drawImage(fondo.getImage(), (getWidth() - w) / 2, getHeight() - h, w, h, this);
            // Libera la copia del pincel.
            g2.dispose();
        }
    }
}