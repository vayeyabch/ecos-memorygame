package ecos.app.modos.JuegoC;

import java.awt.Color;
import java.awt.Dimension;
import java.awt.Font;
import java.awt.Graphics;
import java.awt.Graphics2D;
import java.awt.Rectangle;
import java.util.function.BiConsumer;
import javax.swing.BorderFactory;
import javax.swing.JButton;

/**
 * Botón con estilo pixel (marco guinda, franja clara arriba y sombra abajo).
 * Puede llevar texto o un icono dibujado con código (luna, "sin barra").
 * Por defecto NO hace nada: ponle acción con addActionListener si la necesitas.
 */
public class BotonPixel extends JButton {

    private final Color base;
    private BiConsumer<Graphics2D, Rectangle> dibujoIcono = null;

    /** Botón de texto. */
    public BotonPixel(String texto, Color base, Color colorTexto, float tamanoFuente) {
        super(texto);
        this.base = base;
        setFont(Utilidades.cargarFuentePixel(tamanoFuente).deriveFont(Font.BOLD));
        setForeground(colorTexto);
        configurar();
        setBorder(BorderFactory.createEmptyBorder(10, 22, 16, 22));
    }

    /** Botón cuadrado con un icono dibujado. */
    private BotonPixel(Color base, BiConsumer<Graphics2D, Rectangle> dibujo) {
        super();
        this.base = base;
        this.dibujoIcono = dibujo;
        configurar();
        setPreferredSize(new Dimension(50, 50));
    }

    private void configurar() {
        setContentAreaFilled(false);
        setBorderPainted(false);
        setFocusPainted(false);
        setOpaque(false);
        setRolloverEnabled(false);
    }

    // ---------- Botones con icono ----------
    public static BotonPixel crearLuna() {
        return new BotonPixel(Paleta.CREMA, BotonPixel::dibujarLuna);
    }

    public static BotonPixel crearSinBarra() {
        return new BotonPixel(Paleta.CREMA, BotonPixel::dibujarSinBarra);
    }

    private static void dibujarLuna(Graphics2D g2, Rectangle r) {
        int d = Math.min(r.width, r.height) - 10;
        int x = r.x + (r.width - d) / 2;
        int y = r.y + (r.height - d) / 2;
        g2.setColor(new Color(240, 200, 60));
        g2.fillOval(x, y, d, d);
        g2.setColor(Paleta.CREMA);
        g2.fillOval(x + d / 3, y - d / 6, d, d);
    }

    private static void dibujarSinBarra(Graphics2D g2, Rectangle r) {
        int ancho = r.width - 12;
        int alto = 10;
        int x = r.x + 6;
        int y = r.y + (r.height - alto) / 2 + 4;
        g2.setColor(Paleta.GUINDA);
        g2.fillRect(x, y, ancho, alto);
        g2.setColor(Paleta.BEIGE);
        g2.fillRect(x + 2, y + 2, ancho - 4, alto - 4);
        g2.setColor(Paleta.ROJO_CARTA);
        g2.setStroke(new java.awt.BasicStroke(4f));
        g2.drawLine(x + 3, y + alto + 3, x + ancho - 3, r.y + 4);
    }

    // ---------- Dibujo ----------
    @Override
    protected void paintComponent(Graphics g) {
        Graphics2D g2 = (Graphics2D) g.create();
        int w = getWidth();
        int h = getHeight();
        int borde = 4;
        int sombra = 4;

        // Marco guinda
        g2.setColor(Paleta.GUINDA);
        g2.fillRect(0, 0, w, h);

        int ix = borde;
        int iy = borde;
        int iw = w - borde * 2;
        int ih = h - borde * 2 - sombra * 2;

        // Relleno
        g2.setColor(base);
        g2.fillRect(ix, iy, iw, ih + sombra);
        // Franja clara arriba
        g2.setColor(aclarar(base));
        g2.fillRect(ix, iy, iw, sombra);
        // Franja oscura abajo
        g2.setColor(oscurecer(base));
        g2.fillRect(ix, iy + ih, iw, sombra);

        if (dibujoIcono != null) {
            dibujoIcono.accept(g2, new Rectangle(ix, iy, iw, ih));
        }
        g2.dispose();

        super.paintComponent(g); // el texto
    }

    private static Color aclarar(Color c) {
        return new Color(Math.min(255, c.getRed() + 25), Math.min(255, c.getGreen() + 25),
                Math.min(255, c.getBlue() + 25));
    }

    private static Color oscurecer(Color c) {
        return new Color(Math.max(0, c.getRed() - 35), Math.max(0, c.getGreen() - 35),
                Math.max(0, c.getBlue() - 35));
    }
}