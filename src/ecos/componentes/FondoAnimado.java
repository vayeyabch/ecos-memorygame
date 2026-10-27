package ecos.componentes;

import ecos.app.Tema;
import ecos.recursos.Sprites;
import java.awt.Graphics;
import javax.swing.JPanel;

/**
 * Panel que dibuja el fondo animado (montana de dia o noche segun el tema).
 * Sirve para dialogos u otras ventanas; las pantallas ya traen su propio fondo.
 */
public class FondoAnimado extends JPanel {

    public FondoAnimado() {
        Tema.alCambiar(this::repaint);
    }

    @Override
    protected void paintComponent(Graphics g) {
        super.paintComponent(g);
        g.drawImage(Sprites.fondo().getImage(), 0, 0, getWidth(), getHeight(), this);
    }
}
