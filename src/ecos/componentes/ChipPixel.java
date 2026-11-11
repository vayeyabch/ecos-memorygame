package ecos.componentes;

// Clases del propio proyecto que se usan aquí.
import ecos.app.Idioma;
import ecos.app.Tema;
import ecos.recursos.Fuentes;
// Librería gráfica AWT, Color para los colores, Dimension para los tamaños, Font para las fuentes, FontMetrics para
// medir el texto, Graphics para dibujar, Graphics2D para dibujar con más opciones y Image para las imágenes.
import java.awt.Color;
import java.awt.Dimension;
import java.awt.Font;
import java.awt.FontMetrics;
import java.awt.Graphics;
import java.awt.Graphics2D;
import java.awt.Image;
// Librería Swing, JComponent para la base de los componentes.
import javax.swing.JComponent;

// Cajita pequeña con un texto y, si se quiere, un ícono (se usa para intentos, parejas, premios y monedas).
public class ChipPixel extends JComponent {

    // CAJA = fondo de caja del tema; DURAZNO = fondo durazno con letras oscuras (premios).
    public enum Modo { CAJA, DURAZNO }

    // Texto que se dibuja (ya en el idioma actual), el texto en español para traducirlo otra vez, modo de colores, grosor
    // del borde en píxeles y si usa la fuente de títulos o la de texto.
    private String texto;
    private String clave;
    private final Modo modo;
    private final int borde;
    private final boolean tituloFuente;
    // Icono opcional a la izquierda del texto. Acción que repinta cuando cambia el tema.
    private Image icono = null;
    private final Runnable alCambiarTema = this::repaint;
    private final Runnable alCambiarIdioma = this::traducir;

    // Recibe todo lo necesario para dibujar.
    public ChipPixel(String texto, float tamano, boolean tituloFuente, Modo modo, int borde) {
        // Guarda el texto y lo traduce. Guarda qué fuente usar. Guarda el modo de colores. Guarda el grosor del borde.
        this.clave = texto;
        this.texto = Idioma.t(texto);
        this.tituloFuente = tituloFuente;
        this.modo = modo;
        this.borde = borde;
        // Es transparente, el dibujo se hace a mano. Elige la fuente.
        setOpaque(false);
        setFont(tituloFuente ? Fuentes.titulo(tamano) : Fuentes.texto(tamano).deriveFont(java.awt.Font.BOLD));
    }

    // Cambia el texto.
    // Recibe el texto en español.
    public void setTexto(String clave) {
        // Guarda el texto para poder traducirlo otra vez. Pone el texto traducido.
        this.clave = clave;
        traducir();
    }

    // Vuelve a poner el texto en el idioma actual.
    private void traducir() {
        // Traduce el texto guardado. Avisa que el tamaño pudo cambiar.
        texto = Idioma.t(clave);
        revalidate();
        repaint();
    }

    // Entrega el texto actual.
    public String getTexto() {
        // Devuelve el texto.
        return texto;
    }

    // Pone un icono a la izquierda del texto.
    public void setIcono(Image icono) {
        // Avisa que el tamaño cambió.
        this.icono = icono;
        revalidate();
        repaint();
    }

    // Swing lo llama cuando la cajita entra a una ventana.
    @Override
    public void addNotify() {
        // Deja que Swing haga lo suyo primero. Empieza a escuchar los cambios de tema y de idioma. Se pone al día con el
        // idioma actual.
        super.addNotify();
        Tema.alCambiar(alCambiarTema);
        Idioma.alCambiar(alCambiarIdioma);
        traducir();
    }

    // Swing lo llama cuando la cajita sale de la ventana.
    @Override
    public void removeNotify() {
        // Deja de escuchar los cambios de tema y de idioma. Deja que Swing haga lo suyo.
        Tema.quitar(alCambiarTema);
        Idioma.quitar(alCambiarIdioma);
        super.removeNotify();
    }

    // Tamaño que necesita la cajita.
    @Override
    public Dimension getPreferredSize() {
        // Toma las medidas de la fuente y calcula el ancho del texto más borde y relleno, sumando el icono y su separación.
        FontMetrics fm = getFontMetrics(getFont());
        int ancho = anchoTexto(fm) + borde * 2 + (tituloFuente ? 16 : 20);
        if (icono != null) {
            ancho += icono.getWidth(null) + 8;
        }
        // Alto del texto más borde y relleno. Entrega el tamaño.
        int alto = Math.max(fm.getHeight(), icono != null ? icono.getHeight(null) : 0) + borde * 2 + (tituloFuente ? 12 : 8);
        return new Dimension(ancho, alto);
    }

    // Tamaño mínimo.
    @Override
    public Dimension getMinimumSize() {
        // Nunca se achica más de lo que necesita.
        return getPreferredSize();
    }

    // Tamaño máximo.
    @Override
    public Dimension getMaximumSize() {
        // Nunca se estira más de lo que necesita.
        return getPreferredSize();
    }

    // Swing lo llama cada vez que hay que dibujar.
    @Override
    protected void paintComponent(Graphics g) {
        // Usa una copia del pincel y calcula el ancho y el alto de la cajita y el color del borde.
        Graphics2D g2 = (Graphics2D) g.create();
        int w = getWidth();
        int h = getHeight();
        g2.setColor(Tema.borde());
        // Dibuja el borde. Color del relleno según el modo. Dibuja el relleno. Usa la fuente de la cajita.
        g2.fillRect(0, 0, w, h);
        g2.setColor(modo == Modo.DURAZNO ? Tema.dorado() : Tema.caja());
        g2.fillRect(borde, borde, w - borde * 2, h - borde * 2);
        g2.setFont(getFont());
        // Toma las medidas de la fuente, calcula el ancho del contenido (icono y texto) y la posición para centrarlo.
        FontMetrics fm = g2.getFontMetrics();
        int ancho = anchoTexto(fm) + (icono != null ? icono.getWidth(null) + 8 : 0);
        int x = (w - ancho) / 2;
        if (icono != null) {
            // Dibuja el icono. Mueve la posición para escribir el texto después.
            g2.drawImage(icono, x, (h - icono.getHeight(null)) / 2, null);
            x += icono.getWidth(null) + 8;
        }
        // Color de las letras según el modo. Escribe el texto centrado verticalmente. Libera el pincel.
        g2.setColor(modo == Modo.DURAZNO ? new Color(91, 35, 51) : Tema.texto());
        dibujarTexto(g2, x, (h - fm.getHeight()) / 2 + fm.getAscent());
        g2.dispose();
    }

    // La fuente de títulos no trae el símbolo ₡, se dibuja con la fuente de texto, que sí lo tiene.
    // Fuente solo para el ₡.
    private Font fuenteColon() {
        return Fuentes.texto(getFont().getSize2D() * 1.5f).deriveFont(Font.BOLD);
    }

    // Ancho del texto sumando el ₡ con su fuente.
    private int anchoTexto(FontMetrics fm) {
        int ancho = 0;
        // Separa el ₡ del resto.
        for (String parte : texto.split("(?<=₡)|(?=₡)")) {
            ancho += parte.equals("₡") ? getFontMetrics(fuenteColon()).stringWidth(parte) : fm.stringWidth(parte);
        }
        return ancho;
    }

    // Escribe el texto pieza por pieza.
    private void dibujarTexto(Graphics2D g2, int x, int y) {
        Font normal = g2.getFont();
        for (String parte : texto.split("(?<=₡)|(?=₡)")) {
            boolean colon = parte.equals("₡");
            g2.setFont(colon ? fuenteColon() : normal);
            g2.drawString(parte, x, y);
            x += g2.getFontMetrics().stringWidth(parte);
        }
        g2.setFont(normal);
    }
}
