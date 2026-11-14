package ecos.componentes;

import ecos.app.Navegador;
import ecos.app.Tema;
import ecos.recursos.Sonido;
import java.awt.Color;
import java.awt.Cursor;
import java.awt.Dimension;
import java.awt.Graphics;
import java.awt.Graphics2D;
import java.awt.event.MouseAdapter;
import java.awt.event.MouseEvent;
import java.util.Set;
import javax.swing.JButton;

// Botón cuadrado con una "i" que abre la ventana Acerca de. Va en la esquina inferior derecha.
public class BotonAcercaDe extends JButton {

    // Pantallas donde no se muestra (la intro y los juegos), lado del cuadrado, grosor del borde y alto de la sombra.
    private static final Set<String> OCULTO_EN = Set.of("intro", "clasico", "patrones");
    private static final int LADO = 40;
    private static final int BORDE = 4;
    private static final int SOMBRA = 4;

    // Si está apretado y acción que lo repinta al cambiar el tema.
    private boolean apretado = false;
    private final Runnable alCambiarTema = this::repaint;

    public BotonAcercaDe(Navegador navegador) {
        // Se dibuja a mano, por eso es transparente y sin borde ni foco de Swing.
        setContentAreaFilled(false);
        setBorderPainted(false);
        setFocusPainted(false);
        setOpaque(false);
        setCursor(Cursor.getPredefinedCursor(Cursor.HAND_CURSOR));
        addMouseListener(new MouseAdapter() {
            @Override
            public void mousePressed(MouseEvent e) { apretado = true; repaint(); }
            @Override
            public void mouseReleased(MouseEvent e) { apretado = false; repaint(); }
            @Override
            public void mouseExited(MouseEvent e) { apretado = false; repaint(); }
        });
        // Al hacer clic abre Acerca de. Se esconde según la pantalla y empieza oculto.
        addActionListener(e -> { Sonido.efecto("click"); VentanaAcercaDe.mostrar(this); });
        navegador.alCambiarPantalla(id -> setVisible(!OCULTO_EN.contains(id)));
        setVisible(false);
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
        g2.setColor(Tema.rosa());
        g2.fillRect(BORDE, y + BORDE, LADO - BORDE * 2, LADO - BORDE * 2);
        dibujarI(g2, y);
        g2.dispose();
    }

    private void dibujarI(Graphics2D g, int y) {
        // La i se dibuja con # (celda pintada) y . (vacía), cada celda de 3 píxeles.
        String[] filas = {"..#..", ".....", ".##..", "..#..", "..#..", "..#..", "..#..", "..#..", ".###."};
        int celda = 3;
        int x0 = (LADO - 5 * celda) / 2;
        int y0 = y + (LADO - filas.length * celda) / 2;
        g.setColor(new Color(91, 35, 51));
        for (int f = 0; f < filas.length; f++) {
            for (int c = 0; c < 5; c++) {
                if (filas[f].charAt(c) == '#') {
                    g.fillRect(x0 + c * celda, y0 + f * celda, celda, celda);
                }
            }
        }
    }
}
