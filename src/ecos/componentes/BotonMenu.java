package ecos.componentes;

import ecos.app.Tema;
import ecos.recursos.Fuentes;
import java.awt.Color;
import java.awt.Graphics;
import java.awt.Graphics2D;
import javax.swing.BorderFactory;
import javax.swing.JButton;

/**
 * Boton pixel para las pantallas de Persona 3. Usa solo los colores de Tema,
 * asi que cambia solo con el modo claro/oscuro. No depende de BotonPixel, Paleta ni Utilidades.
 *   new BotonMenu("Jugar", true)   -> boton principal (color del tema)
 *   new BotonMenu("Reglas", false) -> boton secundario (dorado)
 */
public class BotonMenu extends JButton {

    private static final int BORDE = 4;
    private static final int SOMBRA = 4;
    private final boolean principal;

    public BotonMenu(String texto, boolean principal) {
        super(texto);
        this.principal = principal;
        setFont(Fuentes.titulo(14f));
        setContentAreaFilled(false);
        setBorderPainted(false);
        setFocusPainted(false);
        setOpaque(false);
        setRolloverEnabled(false);
        setBorder(BorderFactory.createEmptyBorder(10, 22, 16, 22));
        Tema.alCambiar(this::colorear);
        colorear();
    }

    private void colorear() {
        setForeground(principal ? Tema.textoBoton() : new Color(91, 37, 56)); // sobre el dorado siempre texto oscuro
        repaint();
    }

    @Override
    protected void paintComponent(Graphics g) {
        Graphics2D g2 = (Graphics2D) g.create();
        int w = getWidth();
        int h = getHeight();
        Color base = principal ? Tema.boton() : Tema.dorado();

        g2.setColor(Tema.borde());
        g2.fillRect(0, 0, w, h);
        g2.setColor(base);
        g2.fillRect(BORDE, BORDE, w - BORDE * 2, h - BORDE * 2);
        g2.setColor(mezclar(base, Color.WHITE, 0.25f));          // franja clara arriba
        g2.fillRect(BORDE, BORDE, w - BORDE * 2, SOMBRA);
        g2.setColor(mezclar(base, Color.BLACK, 0.25f));          // franja oscura abajo
        g2.fillRect(BORDE, h - BORDE - SOMBRA * 2, w - BORDE * 2, SOMBRA * 2);
        g2.dispose();

        super.paintComponent(g); // el texto
    }

    private static Color mezclar(Color a, Color b, float t) {
        return new Color(
                Math.round(a.getRed() + (b.getRed() - a.getRed()) * t),
                Math.round(a.getGreen() + (b.getGreen() - a.getGreen()) * t),
                Math.round(a.getBlue() + (b.getBlue() - a.getBlue()) * t));
    }
}
