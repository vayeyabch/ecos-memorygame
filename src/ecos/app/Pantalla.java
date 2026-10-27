package ecos.app;

import ecos.recursos.Sonido;
import ecos.recursos.Sprites;
import java.awt.Graphics;
import javax.swing.ImageIcon;
import javax.swing.JPanel;

/**
 * Clase base de todas las pantallas (intro, inicio, acceso, menu...).
 * Cada pantalla hija debe:
 *   1) llamar a super("id", "nombreDeMusica") en su constructor;
 *   2) agregar sus componentes;
 *   3) usar irA("otraPantalla") o volver() para navegar.
 * Al entrar suena sola la musica indicada (Sonido.musica).
 */
public abstract class Pantalla extends JPanel {

    private final String id;
    private final String musica;
    private Navegador navegador;
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

    /** Lo llama el Navegador al registrar la pantalla. */
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

    /** Si pones false la pantalla no dibuja el fondo animado. */
    protected void setConFondo(boolean conFondo) {
        this.conFondo = conFondo;
    }

    /** Se ejecuta cada vez que la pantalla se muestra. */
    public void alEntrar() {
        Sonido.musica(musica);
        repaint();
    }

    /** Se ejecuta cuando la pantalla se oculta. */
    public void alSalir() {
    }

    @Override
    protected void paintComponent(Graphics g) {
        super.paintComponent(g);
        if (conFondo) {
            ImageIcon fondo = Sprites.fondo();
            g.drawImage(fondo.getImage(), 0, 0, getWidth(), getHeight(), this);
        }
    }
}
