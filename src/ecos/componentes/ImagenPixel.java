package ecos.componentes;

import java.awt.AlphaComposite;
import java.awt.Dimension;
import java.awt.Graphics;
import java.awt.Graphics2D;
import java.awt.Image;
import java.awt.RenderingHints;
import javax.swing.JComponent;

// Imagen que se dibuja sin suavizar para conservar el pixel art; se puede cambiar su tamaño y su opacidad.
public class ImagenPixel extends JComponent {

    // Imagen, ancho y alto con que se dibuja y opacidad (1 completa, 0 invisible).
    private Image imagen;
    private int ancho;
    private int alto;
    private float opacidad = 1f;

    public ImagenPixel(Image imagen) {
        setOpaque(false);
        setImagen(imagen);
    }

    public void setImagen(Image imagen) {
        this.imagen = imagen;
        if (ancho == 0) {
            ancho = imagen.getWidth(null);
            alto = imagen.getHeight(null);
        }
        revalidate();
        repaint();
    }

    // Dibuja con este ancho manteniendo la proporción.
    public void setAncho(int nuevoAncho) {
        ancho = Math.max(1, nuevoAncho);
        alto = Math.max(1, Math.round(ancho * imagen.getHeight(null) / (float) imagen.getWidth(null)));
        revalidate();
        repaint();
    }

    // Dibuja con este alto manteniendo la proporción.
    public void setAlto(int nuevoAlto) {
        alto = Math.max(1, nuevoAlto);
        ancho = Math.max(1, Math.round(alto * imagen.getWidth(null) / (float) imagen.getHeight(null)));
        revalidate();
        repaint();
    }

    // Cambia qué tan visible es, para aparecer de a poco.
    public void setOpacidad(float opacidad) {
        this.opacidad = opacidad;
        repaint();
    }

    @Override
    public Dimension getPreferredSize() {
        return new Dimension(ancho, alto);
    }

    @Override
    protected void paintComponent(Graphics g) {
        Graphics2D g2 = (Graphics2D) g.create();
        g2.setComposite(AlphaComposite.getInstance(AlphaComposite.SRC_OVER, opacidad));
        g2.setRenderingHint(RenderingHints.KEY_INTERPOLATION, RenderingHints.VALUE_INTERPOLATION_NEAREST_NEIGHBOR);
        g2.drawImage(imagen, 0, 0, getWidth(), getHeight(), null);
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
