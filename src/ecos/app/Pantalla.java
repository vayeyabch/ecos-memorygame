package ecos.app;

import ecos.recursos.Sonido;
import ecos.recursos.Sprites;
import java.awt.Graphics;
import java.awt.Graphics2D;
import java.awt.RenderingHints;
import javax.swing.ImageIcon;
import javax.swing.JPanel;

// Base de todas las pantallas. Guarda su id, su música y qué hacer al entrar y al salir.
public abstract class Pantalla extends JPanel {

    // Espacio que dejan las pantallas arriba para que no las tape la barra superior.
    public static final int MARGEN_SUPERIOR = 72;

    // Id de la pantalla, su música, el navegador (se asigna al registrarla) y qué hacer al entrar.
    private final String id;
    private final String musica;
    private Navegador navegador;
    private Runnable accionEntrar = () -> { };
    // Qué hacer al salir, y si dibuja el fondo animado.
    private Runnable accionSalir = () -> { };
    private boolean conFondo = true;

    protected Pantalla(String id, String musica) {
        this.id = id;
        this.musica = musica;
        setOpaque(true);
        Tema.alCambiar(this::repaint);
    }

    public String getId() {
        return id;
    }

    // Lo llama el Navegador al registrar la pantalla.
    void setNavegador(Navegador navegador) {
        this.navegador = navegador;
    }

    protected void irA(String idPantalla) {
        if (navegador != null) {
            navegador.ir(idPantalla);
        }
    }

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
        Sonido.musica(musica);
        repaint();
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
        ecos.componentes.CapaModal.cerrarTodas();
        accionSalir.run();
    }

    @Override
    protected void paintComponent(Graphics g) {
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
            // original.
            double escala = Math.max(getWidth() / (double) ancho, getHeight() / (double) alto);
            int w = (int) Math.ceil(ancho * escala);
            int h = (int) Math.ceil(alto * escala);
            Graphics2D g2 = (Graphics2D) g.create();
            // Sin suavizar, para mantener el pixel art. El fondo va centrado y pegado abajo; lo que sobra se corta
            // arriba.
            g2.setRenderingHint(RenderingHints.KEY_INTERPOLATION, RenderingHints.VALUE_INTERPOLATION_NEAREST_NEIGHBOR);
            g2.drawImage(fondo.getImage(), (getWidth() - w) / 2, getHeight() - h, w, h, this);
            g2.dispose();
        }
    }
}
