package ecos.componentes;

import ecos.app.Tema;
import ecos.recursos.Sprites;
import java.awt.AlphaComposite;
import java.awt.Color;
import java.awt.Cursor;
import java.awt.Dimension;
import java.awt.Graphics;
import java.awt.Graphics2D;
import java.awt.Image;
import java.awt.event.MouseAdapter;
import java.awt.event.MouseEvent;
import javax.swing.JComponent;

// Una casilla del modo patrones: se ilumina en naranja cuando le toca.
public class BaldosaPixel extends JComponent {

    private int lado;
    private final Image original; // icono sin escalar
    private Image icono; // icono ya escalado al tamaño actual
    private boolean encendida = false;
    private boolean activa = false;
    private boolean encima = false;
    private Runnable alClic = () -> { };
    private final Runnable alCambiarTema = this::repaint;

    public BaldosaPixel(int lado, Image icono) {
        this.lado = lado;
        this.original = icono;
        escalarIcono();
        setOpaque(false);
        addMouseListener(new MouseAdapter() {
            @Override
            public void mouseEntered(MouseEvent e) {
                encima = true;
                repaint();
            }

            @Override
            public void mouseExited(MouseEvent e) {
                encima = false;
                repaint();
            }

            @Override
            public void mouseClicked(MouseEvent e) {
                if (activa) {
                    alClic.run();
                }
            }
        });
    }

    private void escalarIcono() {
        int ic = (int) (lado * 0.62);
        icono = original == null ? null : Sprites.escalar(original, ic, ic);
    }

    // Cambia el tamaño de la casilla (según el espacio de la ventana).
    public void setLado(int nuevo) {
        this.lado = nuevo;
        escalarIcono();
        revalidate();
        repaint();
    }

    public void setAlClic(Runnable accion) {
        this.alClic = accion;
    }

    public void setActiva(boolean activa) {
        this.activa = activa;
        setCursor(Cursor.getPredefinedCursor(activa ? Cursor.HAND_CURSOR : Cursor.DEFAULT_CURSOR));
    }

    public void setEncendida(boolean encendida) {
        this.encendida = encendida;
        repaint();
    }

    @Override
    public void addNotify() {
        super.addNotify();
        Tema.alCambiar(alCambiarTema);
    }

    @Override
    public void removeNotify() {
        Tema.quitar(alCambiarTema);
        super.removeNotify();
    }

    @Override
    public Dimension getPreferredSize() {
        return new Dimension(lado, lado);
    }

    @Override
    public Dimension getMinimumSize() {
        return getPreferredSize();
    }

    @Override
    public Dimension getMaximumSize() {
        return getPreferredSize();
    }

    @Override
    protected void paintComponent(Graphics g) {
        Graphics2D g2 = (Graphics2D) g.create();
        int s = lado - 14; // deja espacio para sombra y brillo
        g2.translate(getWidth() / 2.0, getHeight() / 2.0 - 3);
        if (encendida) {
            g2.scale(1.08, 1.08);
        } else if (encima && activa) {
            g2.translate(0, -2);
        }
        int x = -s / 2;
        int y = -s / 2;
        g2.setColor(Tema.sombra());
        g2.fillRect(x, y + 5, s, s);
        if (encendida) {
            g2.setColor(Tema.dorado());
            g2.fillRect(x - 4, y - 4, s + 8, s + 8);
        }
        g2.setColor(Tema.borde());
        g2.fillRect(x, y, s, s);
        g2.setColor(encendida ? Tema.naranja() : Tema.carta());
        g2.fillRect(x + 4, y + 4, s - 8, s - 8);
        g2.setColor(new Color(255, 255, 255, encendida ? 128 : 128));
        g2.fillRect(x + 4, y + 4, s - 8, 4);
        g2.setColor(new Color(0, 0, 0, 31));
        g2.fillRect(x + 4, y + s - 8, s - 8, 4);
        if (icono != null) {
            g2.setComposite(AlphaComposite.getInstance(AlphaComposite.SRC_OVER, encendida ? 1f : 0.4f));
            g2.drawImage(icono, -icono.getWidth(null) / 2, -icono.getHeight(null) / 2, null);
        }
        g2.dispose();
    }
}
