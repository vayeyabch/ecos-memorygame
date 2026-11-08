package ecos.componentes;

// Clases del propio proyecto que se usan aquí.
import ecos.app.Tema;
import ecos.recursos.Sprites;
// Librería gráfica AWT, AlphaComposite para la transparencia, Color para los colores, Cursor para el cursor del mouse,
// Dimension para los tamaños, Graphics para dibujar, Graphics2D para dibujar con más opciones y Image para las imágenes.
import java.awt.AlphaComposite;
import java.awt.Color;
import java.awt.Cursor;
import java.awt.Dimension;
import java.awt.Graphics;
import java.awt.Graphics2D;
import java.awt.Image;
// Eventos del mouse y el teclado de AWT, MouseAdapter para reaccionar al mouse y MouseEvent para los datos de un clic.
import java.awt.event.MouseAdapter;
import java.awt.event.MouseEvent;
// Librería Swing, JComponent para la base de los componentes.
import javax.swing.JComponent;

// Una casilla del modo patrones, se ilumina en naranja cuando le toca.
public class BaldosaPixel extends JComponent {

    // Tamaño de la casilla y su ícono, original y ya escalado.
    private int lado;
    // Icono sin escalar. Icono ya escalado al tamaño actual.
    private final Image original;
    private Image icono;
    // Estados de la casilla, encendida (le toca), activa (se puede tocar) y encima (el mouse está sobre ella).
    private boolean encendida = false;
    private boolean activa = false;
    private boolean encima = false;
    private Runnable alClic = () -> { };
    private final Runnable alCambiarTema = this::repaint;

    // Recibe el tamaño y el ícono; el mouse cambia el estado y solo avisa del clic si la casilla está activa.
    public BaldosaPixel(int lado, Image icono) {
        this.lado = lado;
        this.original = icono;
        escalarIcono();
        setOpaque(false);
        // El mouse cambia el estado de "encima" y el clic solo avisa si la casilla está activa.
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

    // Escala el ícono al 62 % de la casilla para que quede con margen.
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

    // Setters que usa el tablero para conectar el clic, activar o encender la casilla.
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

    // Se suscribe al cambio de tema mientras está en pantalla para repintarse con los colores nuevos.
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

    // La casilla siempre es cuadrada, del tamaño del lado.
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

    // Dibuja la casilla, sombra, borde, fondo (naranja si está encendida), brillo arriba y el ícono semitransparente.
    @Override
    protected void paintComponent(Graphics g) {
        Graphics2D g2 = (Graphics2D) g.create();
        // Deja espacio para sombra y brillo.
        int s = lado - 14;
        // Si está encendida crece un poquito; si el mouse está encima sube dos píxeles.
        g2.translate(getWidth() / 2.0, getHeight() / 2.0 - 3);
        if (encendida) {
            g2.scale(1.08, 1.08);
        } else if (encima && activa) {
            g2.translate(0, -2);
        }
        // Se dibujan las capas de la casilla de atrás hacia adelante, sombra, resplandor dorado (si está encendida), borde,
        // fondo y brillos.
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
        // El ícono va opaco si la casilla está encendida y apagado si no.
        if (icono != null) {
            g2.setComposite(AlphaComposite.getInstance(AlphaComposite.SRC_OVER, encendida ? 1f : 0.4f));
            g2.drawImage(icono, -icono.getWidth(null) / 2, -icono.getHeight(null) / 2, null);
        }
        g2.dispose();
    }
}
