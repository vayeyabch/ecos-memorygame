package ecos.componentes;

// Librería gráfica AWT, AlphaComposite para la transparencia, Dimension para los tamaños, Graphics para el pincel para
// dibujar, Graphics2D para el pincel con más opciones, Image para las imágenes y RenderingHints para la calidad del
// Librería gráfica AWT, AlphaComposite para la transparencia, Dimension para los tamaños, Graphics para dibujar,
// Graphics2D para dibujar con más opciones, Image para las imágenes y RenderingHints para la calidad del dibujo.
import java.awt.AlphaComposite;
import java.awt.Dimension;
import java.awt.Graphics;
import java.awt.Graphics2D;
import java.awt.Image;
import java.awt.RenderingHints;
// Librería Swing, JComponent para la base de los componentes.
import javax.swing.JComponent;

// Imagen que se dibuja sin suavizar para que conserve el estilo pixel art; se puede cambiar su tamaño y su opacidad.
public class ImagenPixel extends JComponent {

    // Imagen que se dibuja, el ancho y el alto con que se dibuja y su opacidad, donde 1 es completa y 0 invisible.
    private Image imagen;
    private int ancho;
    private int alto;
    private float opacidad = 1f;

    // Recibe la imagen a mostrar.
    public ImagenPixel(Image imagen) {
        // Es transparente, solo se ve la imagen. Guarda la imagen y calcula su tamaño.
        setOpaque(false);
        setImagen(imagen);
    }

    // Cambia la imagen mostrada.
    public void setImagen(Image imagen) {
        // Si todavía no tiene un tamaño elegido. Usa el ancho original. Usa el alto original.
        this.imagen = imagen;
        if (ancho == 0) {
            ancho = imagen.getWidth(null);
            alto = imagen.getHeight(null);
        }
        // Avisa que el tamaño pudo cambiar.
        revalidate();
        repaint();
    }

    // Dibuja la imagen con este ancho, manteniendo la proporción.
    // Recibe el ancho deseado.
    public void setAncho(int nuevoAncho) {
        // Guarda el ancho. Calcula el alto para que no se deforme. Avisa que el tamaño cambió.
        ancho = Math.max(1, nuevoAncho);
        alto = Math.max(1, Math.round(ancho * imagen.getHeight(null) / (float) imagen.getWidth(null)));
        revalidate();
        repaint();
    }

    // Dibuja la imagen con este alto, manteniendo la proporción.
    // Recibe el alto deseado.
    public void setAlto(int nuevoAlto) {
        // Guarda el alto. Calcula el ancho para que no se deforme. Avisa que el tamaño cambió.
        alto = Math.max(1, nuevoAlto);
        ancho = Math.max(1, Math.round(alto * imagen.getWidth(null) / (float) imagen.getHeight(null)));
        revalidate();
        repaint();
    }

    // Cambia qué tan visible es la imagen (para aparecer de a poco).
    public void setOpacidad(float opacidad) {
        this.opacidad = opacidad;
        repaint();
    }

    // Tamaño que necesita el componente.
    @Override
    public Dimension getPreferredSize() {
        // El tamaño con el que se dibuja la imagen.
        return new Dimension(ancho, alto);
    }

    // Swing lo llama cada vez que hay que dibujar.
    @Override
    protected void paintComponent(Graphics g) {
        // Usa una copia del pincel, aplica la opacidad y dibuja la imagen ocupando todo el componente, sin suavizar, para
        // mantener el pixel art.
        Graphics2D g2 = (Graphics2D) g.create();
        g2.setComposite(AlphaComposite.getInstance(AlphaComposite.SRC_OVER, opacidad));
        g2.setRenderingHint(RenderingHints.KEY_INTERPOLATION, RenderingHints.VALUE_INTERPOLATION_NEAREST_NEIGHBOR);
        g2.drawImage(imagen, 0, 0, getWidth(), getHeight(), null);
        // Libera el pincel.
        g2.dispose();
    }

    // Tamaño mínimo del componente.
    @Override
    public Dimension getMinimumSize() {
        // Nunca se achica más de lo que necesita.
        return getPreferredSize();
    }

    // Tamaño máximo del componente.
    @Override
    public Dimension getMaximumSize() {
        // Nunca se estira más de lo que necesita.
        return getPreferredSize();
    }
}
