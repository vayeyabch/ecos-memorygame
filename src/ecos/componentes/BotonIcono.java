package ecos.componentes;

import ecos.app.Tema;
import java.awt.Cursor;
import java.awt.Dimension;
import java.awt.Graphics;
import java.awt.Graphics2D;
import java.awt.Rectangle;
import java.awt.event.MouseAdapter;
import java.awt.event.MouseEvent;
import java.util.function.BiConsumer;
import javax.swing.JButton;

// Botón cuadrado con un dibujo propio; baja al apretarlo y cambia de color con el tema.
public class BotonIcono extends JButton {

    // Lado del cuadrado, grosor del borde y alto de la sombra.
    private static final int LADO = 52;
    private static final int BORDE = 4;
    private static final int SOMBRA = 4;

    // Dibujo de adentro (empieza vacío), si está apretado y acción que lo repinta al cambiar el tema.
    private BiConsumer<Graphics2D, Rectangle> dibujo = (g, r) -> { };
    private boolean apretado = false;
    private final Runnable alCambiarTema = this::repaint;

    public BotonIcono() {
        // Se dibuja a mano, por eso es transparente y sin borde ni foco de Swing.
        setContentAreaFilled(false);
        setBorderPainted(false);
        setFocusPainted(false);
        setOpaque(false);
        setRolloverEnabled(false);
        setCursor(Cursor.getPredefinedCursor(Cursor.HAND_CURSOR));
        addMouseListener(new MouseAdapter() {
            @Override
            public void mousePressed(MouseEvent e) { apretado = true; repaint(); }
            @Override
            public void mouseReleased(MouseEvent e) { apretado = false; repaint(); }
            @Override
            public void mouseExited(MouseEvent e) { apretado = false; repaint(); }
        });
    }

    public void setDibujo(BiConsumer<Graphics2D, Rectangle> dibujo) {
        this.dibujo = dibujo;
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
        return new Dimension(LADO, LADO + SOMBRA);
    }

    @Override
    protected void paintComponent(Graphics g) {
        Graphics2D g2 = (Graphics2D) g.create();
        int y = apretado ? 3 : 0;
        int sombra = apretado ? 1 : SOMBRA;
        g2.setColor(Tema.sombra());
        g2.fillRect(0, y + sombra, LADO, LADO);
        g2.setColor(Tema.borde());
        g2.fillRect(0, y, LADO, LADO);
        g2.setColor(Tema.panelAlto());
        g2.fillRect(BORDE, y + BORDE, LADO - BORDE * 2, LADO - BORDE * 2);
        g2.setColor(Tema.panel());
        g2.fillRect(BORDE + 2, y + BORDE + 2, LADO - (BORDE + 2) * 2, LADO - (BORDE + 2) * 2);
        dibujo.accept(g2, new Rectangle(BORDE, y + BORDE, LADO - BORDE * 2, LADO - BORDE * 2));
        g2.dispose();
    }

    @Override
    public Dimension getMinimumSize() {
        return getPreferredSize();
    }

    @Override
    public Dimension getMaximumSize() {
        return getPreferredSize();
    }
}
