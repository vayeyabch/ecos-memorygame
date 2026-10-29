package ecos.componentes;

import ecos.app.Tema; // Para usar los colores del tema actual.
import ecos.recursos.Fuentes; // Para usar las fuentes pixel del juego.
import java.awt.Color; // Clase de Java para representar un color.
import java.awt.Dimension; // Representa un tamaño (ancho y alto).
import java.awt.FontMetrics; // Medidas de una fuente.
import java.awt.Graphics; // Herramienta básica para dibujar.
import java.awt.Graphics2D; // Versión más completa de Graphics.
import java.awt.Image; // Tipo general de imagen.
import javax.swing.JComponent; // Componente básico de Swing: la clase de la que hereda esta.

public class ChipPixel extends JComponent {

    public enum Modo { CAJA, DURAZNO } // CAJA = fondo de caja del tema; DURAZNO = fondo durazno con letras oscuras (premios).

    private String texto; // Texto de la cajita.
    private final Modo modo; // Modo de colores.
    private final int borde; // Grosor del borde, en píxeles.
    private final boolean tituloFuente; // true = usa la fuente de títulos; false = la de texto.
    private final float tamano; // Tamaño de la letra.
    private Image icono = null; // Icono opcional a la izquierda del texto.
    private final Runnable alCambiarTema = this::repaint; // Acción que repinta cuando cambia el tema.

    public ChipPixel(String texto, float tamano, boolean tituloFuente, Modo modo, int borde) { // Recibe todo lo necesario para dibujar.
        this.texto = texto; // Guarda el texto.
        this.tamano = tamano; // Guarda el tamaño de letra.
        this.tituloFuente = tituloFuente; // Guarda qué fuente usar.
        this.modo = modo; // Guarda el modo de colores.
        this.borde = borde; // Guarda el grosor del borde.
        setOpaque(false); // Es transparente: el dibujo se hace a mano.
        setFont(tituloFuente ? Fuentes.titulo(tamano) : Fuentes.texto(tamano).deriveFont(java.awt.Font.BOLD)); // Elige la fuente.
    }

    public void setTexto(String texto) { // Cambia el texto.
        this.texto = texto; // Lo guarda.
        revalidate(); // Avisa que el tamaño pudo cambiar.
        repaint(); // Repinta.
    }

    public String getTexto() { // Entrega el texto actual.
        return texto; // Devuelve el texto.
    }

    public void setIcono(Image icono) { // Pone un icono a la izquierda del texto.
        this.icono = icono; // Lo guarda.
        revalidate(); // Avisa que el tamaño cambió.
        repaint(); // Repinta.
    }

    @Override // Indica que se reemplaza un método de JComponent.
    public void addNotify() { // Swing lo llama cuando la cajita entra a una ventana.
        super.addNotify(); // Deja que Swing haga lo suyo primero.
        Tema.alCambiar(alCambiarTema); // Empieza a escuchar los cambios de tema.
    }

    @Override // Indica que se reemplaza un método de JComponent.
    public void removeNotify() { // Swing lo llama cuando la cajita sale de la ventana.
        Tema.quitar(alCambiarTema); // Deja de escuchar los cambios de tema.
        super.removeNotify(); // Deja que Swing haga lo suyo.
    }

    @Override // Indica que se reemplaza un método de JComponent.
    public Dimension getPreferredSize() { // Tamaño que necesita la cajita.
        FontMetrics fm = getFontMetrics(getFont()); // Medidas de la fuente.
        int ancho = fm.stringWidth(texto) + borde * 2 + (tituloFuente ? 16 : 20); // Ancho del texto más borde y relleno.
        if (icono != null) { // Si hay icono.
            ancho += icono.getWidth(null) + 8; // Suma el icono y su separación.
        }
        int alto = Math.max(fm.getHeight(), icono != null ? icono.getHeight(null) : 0) + borde * 2 + (tituloFuente ? 12 : 8); // Alto del texto más borde y relleno.
        return new Dimension(ancho, alto); // Entrega el tamaño.
    }

    @Override // Indica que se reemplaza un método de JComponent.
    public Dimension getMinimumSize() { // Tamaño mínimo.
        return getPreferredSize(); // Nunca se achica más de lo que necesita.
    }

    @Override // Indica que se reemplaza un método de JComponent.
    public Dimension getMaximumSize() { // Tamaño máximo.
        return getPreferredSize(); // Nunca se estira más de lo que necesita.
    }

    @Override // Indica que se reemplaza el método de dibujo de JComponent.
    protected void paintComponent(Graphics g) { // Swing lo llama cada vez que hay que dibujar.
        Graphics2D g2 = (Graphics2D) g.create(); // Copia del pincel para no afectar al original.
        int w = getWidth(); // Ancho de la cajita.
        int h = getHeight(); // Alto de la cajita.
        g2.setColor(Tema.borde()); // Color del borde.
        g2.fillRect(0, 0, w, h); // Dibuja el borde.
        g2.setColor(modo == Modo.DURAZNO ? Tema.dorado() : Tema.caja()); // Color del relleno según el modo.
        g2.fillRect(borde, borde, w - borde * 2, h - borde * 2); // Dibuja el relleno.
        g2.setFont(getFont()); // Usa la fuente de la cajita.
        FontMetrics fm = g2.getFontMetrics(); // Medidas de la fuente.
        int ancho = fm.stringWidth(texto) + (icono != null ? icono.getWidth(null) + 8 : 0); // Ancho del contenido (icono + texto).
        int x = (w - ancho) / 2; // Posición para centrar el contenido.
        if (icono != null) { // Si hay icono.
            g2.drawImage(icono, x, (h - icono.getHeight(null)) / 2, null); // Dibuja el icono.
            x += icono.getWidth(null) + 8; // Mueve la posición para escribir el texto después.
        }
        g2.setColor(modo == Modo.DURAZNO ? new Color(91, 35, 51) : Tema.texto()); // Color de las letras según el modo.
        g2.drawString(texto, x, (h - fm.getHeight()) / 2 + fm.getAscent()); // Escribe el texto centrado verticalmente.
        g2.dispose(); // Libera el pincel.
    }
}
