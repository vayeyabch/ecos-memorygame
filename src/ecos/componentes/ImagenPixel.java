package ecos.componentes;

import java.awt.AlphaComposite; // Permite dibujar con transparencia.
import java.awt.Dimension; // Representa un tamaño (ancho y alto).
import java.awt.Graphics; // Herramienta básica para dibujar.
import java.awt.Graphics2D; // Versión más completa de Graphics.
import java.awt.Image; // Tipo general de imagen.
import java.awt.RenderingHints; // Opciones de calidad al dibujar.
import javax.swing.JComponent; // Componente básico de Swing: la clase de la que hereda esta.

public class ImagenPixel extends JComponent {

    private Image imagen; // Imagen que se dibuja.
    private int ancho; // Ancho con el que se dibuja.
    private int alto; // Alto con el que se dibuja.
    private float opacidad = 1f; // 1 = se ve completa; 0 = invisible.

    public ImagenPixel(Image imagen) { // Recibe la imagen a mostrar.
        setOpaque(false); // Es transparente: solo se ve la imagen.
        setImagen(imagen); // Guarda la imagen y calcula su tamaño.
    }

    public void setImagen(Image imagen) { // Cambia la imagen mostrada.
        this.imagen = imagen; // La guarda.
        if (ancho == 0) { // Si todavía no tiene un tamaño elegido.
            ancho = imagen.getWidth(null); // Usa el ancho original.
            alto = imagen.getHeight(null); // Usa el alto original.
        }
        revalidate(); // Avisa que el tamaño pudo cambiar.
        repaint(); // Repinta.
    }

    // Dibuja la imagen con este ancho, manteniendo la proporción.
    public void setAncho(int nuevoAncho) { // Recibe el ancho deseado.
        ancho = Math.max(1, nuevoAncho); // Guarda el ancho.
        alto = Math.max(1, Math.round(ancho * imagen.getHeight(null) / (float) imagen.getWidth(null))); // Calcula el alto para que no se deforme.
        revalidate(); // Avisa que el tamaño cambió.
        repaint(); // Repinta.
    }

    // Dibuja la imagen con este alto, manteniendo la proporción.
    public void setAlto(int nuevoAlto) { // Recibe el alto deseado.
        alto = Math.max(1, nuevoAlto); // Guarda el alto.
        ancho = Math.max(1, Math.round(alto * imagen.getWidth(null) / (float) imagen.getHeight(null))); // Calcula el ancho para que no se deforme.
        revalidate(); // Avisa que el tamaño cambió.
        repaint(); // Repinta.
    }

    public void setOpacidad(float opacidad) { // Cambia qué tan visible es la imagen (para aparecer de a poco).
        this.opacidad = opacidad; // La guarda.
        repaint(); // Repinta.
    }

    @Override // Indica que se reemplaza un método de JComponent.
    public Dimension getPreferredSize() { // Tamaño que necesita el componente.
        return new Dimension(ancho, alto); // El tamaño con el que se dibuja la imagen.
    }

    @Override // Indica que se reemplaza el método de dibujo de JComponent.
    protected void paintComponent(Graphics g) { // Swing lo llama cada vez que hay que dibujar.
        Graphics2D g2 = (Graphics2D) g.create(); // Copia del pincel para no afectar al original.
        g2.setComposite(AlphaComposite.getInstance(AlphaComposite.SRC_OVER, opacidad)); // Aplica la opacidad.
        g2.setRenderingHint(RenderingHints.KEY_INTERPOLATION, RenderingHints.VALUE_INTERPOLATION_NEAREST_NEIGHBOR); // Sin suavizar: se mantiene el pixel art.
        g2.drawImage(imagen, 0, 0, getWidth(), getHeight(), null); // Dibuja la imagen ocupando todo el componente.
        g2.dispose(); // Libera el pincel.
    }

    @Override // Indica que se reemplaza un método de JComponent.
    public Dimension getMinimumSize() { // Tamaño mínimo del componente.
        return getPreferredSize(); // Nunca se achica más de lo que necesita.
    }

    @Override // Indica que se reemplaza un método de JComponent.
    public Dimension getMaximumSize() { // Tamaño máximo del componente.
        return getPreferredSize(); // Nunca se estira más de lo que necesita.
    }
}
