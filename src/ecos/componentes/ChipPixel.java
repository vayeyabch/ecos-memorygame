package ecos.componentes;

import ecos.app.Idioma;
import ecos.app.Tema;
import ecos.recursos.Fuentes;
import java.awt.Color;
import java.awt.Dimension;
import java.awt.Font;
import java.awt.FontMetrics;
import java.awt.Graphics;
import java.awt.Graphics2D;
import java.awt.Image;
import javax.swing.JComponent;

// Cajita pequeña con un texto y, si se quiere, un ícono. Se usa para intentos, parejas, premios y monedas.
public class ChipPixel extends JComponent {

    // CAJA es el fondo de caja del tema; DURAZNO es fondo durazno con letras oscuras (premios).
    public enum Modo { CAJA, DURAZNO }

    // Texto ya en el idioma actual, texto en español para traducirlo otra vez, modo de colores, grosor del borde y
    // fuente.
    private String texto;
    private String clave;
    private final Modo modo;
    private final int borde;
    private final boolean tituloFuente;
    // Ícono opcional a la izquierda y acción que repinta al cambiar el tema.
    private Image icono = null;
    private final Runnable alCambiarTema = this::repaint;
    private final Runnable alCambiarIdioma = this::traducir;

    public ChipPixel(String texto, float tamano, boolean tituloFuente, Modo modo, int borde) {
        this.clave = texto;
        this.texto = Idioma.t(texto);
        this.tituloFuente = tituloFuente;
        this.modo = modo;
        this.borde = borde;
        setOpaque(false);
        setFont(tituloFuente ? Fuentes.titulo(tamano) : Fuentes.texto(tamano).deriveFont(java.awt.Font.BOLD));
    }

    public void setTexto(String clave) {
        this.clave = clave;
        traducir();
    }

    private void traducir() {
        texto = Idioma.t(clave);
        revalidate();
        repaint();
    }

    public void setIcono(Image icono) {
        this.icono = icono;
        revalidate();
        repaint();
    }

    @Override
    public void addNotify() {
        super.addNotify();
        Tema.alCambiar(alCambiarTema);
        Idioma.alCambiar(alCambiarIdioma);
        traducir();
    }

    @Override
    public void removeNotify() {
        Tema.quitar(alCambiarTema);
        Idioma.quitar(alCambiarIdioma);
        super.removeNotify();
    }

    @Override
    public Dimension getPreferredSize() {
        FontMetrics fm = getFontMetrics(getFont());
        int ancho = anchoTexto(fm) + borde * 2 + (tituloFuente ? 16 : 20);
        if (icono != null) {
            ancho += icono.getWidth(null) + 8;
        }
        int alto = Math.max(fm.getHeight(), icono != null ? icono.getHeight(null) : 0) + borde * 2 + (tituloFuente ? 12 : 8);
        return new Dimension(ancho, alto);
    }

    @Override
    public Dimension getMinimumSize() {
        return getPreferredSize();
    }

    @Override
    public Dimension getMaximumSize() {
        return getPreferredSize();
    }

    @Override
    protected void paintComponent(Graphics g) {
        Graphics2D g2 = (Graphics2D) g.create();
        int w = getWidth();
        int h = getHeight();
        g2.setColor(Tema.borde());
        g2.fillRect(0, 0, w, h);
        g2.setColor(modo == Modo.DURAZNO ? Tema.dorado() : Tema.caja());
        g2.fillRect(borde, borde, w - borde * 2, h - borde * 2);
        g2.setFont(getFont());
        FontMetrics fm = g2.getFontMetrics();
        int ancho = anchoTexto(fm) + (icono != null ? icono.getWidth(null) + 8 : 0);
        int x = (w - ancho) / 2;
        if (icono != null) {
            g2.drawImage(icono, x, (h - icono.getHeight(null)) / 2, null);
            x += icono.getWidth(null) + 8;
        }
        g2.setColor(modo == Modo.DURAZNO ? new Color(91, 35, 51) : Tema.texto());
        dibujarTexto(g2, x, (h - fm.getHeight()) / 2 + fm.getAscent());
        g2.dispose();
    }

    // La fuente de títulos no trae el símbolo ₡, así que se dibuja con la fuente de texto.
    private Font fuenteColon() {
        return Fuentes.texto(getFont().getSize2D() * 1.5f).deriveFont(Font.BOLD);
    }

    private int anchoTexto(FontMetrics fm) {
        int ancho = 0;
        for (String parte : texto.split("(?<=₡)|(?=₡)")) {
            ancho += parte.equals("₡") ? getFontMetrics(fuenteColon()).stringWidth(parte) : fm.stringWidth(parte);
        }
        return ancho;
    }

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
