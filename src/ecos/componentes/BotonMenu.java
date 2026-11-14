package ecos.componentes;

import ecos.app.Idioma;
import ecos.app.Tema;
import ecos.recursos.Fuentes;
import java.awt.AlphaComposite;
import java.awt.Color;
import java.awt.Cursor;
import java.awt.Dimension;
import java.awt.FontMetrics;
import java.awt.Graphics;
import java.awt.Graphics2D;
import java.awt.event.MouseAdapter;
import java.awt.event.MouseEvent;
import javax.swing.JButton;

// Botón pixel con varios estilos (principal, secundario, rosa, pestaña); sube al pasar el mouse y baja al apretarlo.
public class BotonMenu extends JButton {

    public enum Estilo { PRINCIPAL, SECUNDARIO, ROSA, PESTANA }

    // Grosor del borde, alto de la sombra y cuánto sube con el mouse o baja al apretar.
    private static final int BORDE = 4;
    private static final int SOMBRA = 5;
    private static final int SUBIDA = 2;
    private static final int BAJADA = 4;

    // Estilo actual, si es la versión chica, si el mouse está encima y si está apretado.
    private Estilo estilo;
    private boolean pequeno = false;
    private boolean encima = false;
    private boolean apretado = false;
    // Texto en español, la clave para traducirlo al cambiar el idioma. Sin valor inicial porque JButton pone el texto
    // antes de crear los campos.
    private final Runnable alCambiarTema = this::repaint;
    private String clave;
    private final Runnable alCambiarIdioma = this::traducir;

    // Si es principal usa el color durazno; si no, el secundario.
    public BotonMenu(String texto, boolean principal) {
        this(texto, principal ? Estilo.PRINCIPAL : Estilo.SECUNDARIO);
    }

    public BotonMenu(String texto, Estilo estilo) {
        super(texto);
        this.estilo = estilo;
        setFont(Fuentes.titulo(14f));
        setContentAreaFilled(false);
        setBorderPainted(false);
        setFocusPainted(false);
        setOpaque(false);
        setRolloverEnabled(false);
        setCursor(Cursor.getPredefinedCursor(Cursor.HAND_CURSOR));
        addMouseListener(new MouseAdapter() {
            @Override
            public void mouseEntered(MouseEvent e) { encima = true; repaint(); }
            @Override
            public void mouseExited(MouseEvent e) { encima = false; apretado = false; repaint(); }
            @Override
            public void mousePressed(MouseEvent e) { apretado = true; repaint(); }
            @Override
            public void mouseReleased(MouseEvent e) { apretado = false; repaint(); }
        });
    }

    @Override
    public void setText(String texto) {
        clave = texto;
        super.setText(Idioma.t(texto));
    }

    private void traducir() {
        super.setText(Idioma.t(clave));
    }

    public void setEstilo(Estilo estilo) {
        this.estilo = estilo;
        repaint();
    }

    public void setPequeno(boolean pequeno) {
        this.pequeno = pequeno;
        setFont(Fuentes.titulo(pequeno ? 12f : 14f));
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

    private int relleno() {
        return pequeno ? 12 : 20;
    }

    @Override
    public Dimension getPreferredSize() {
        FontMetrics fm = getFontMetrics(getFont());
        int alto = fm.getHeight() + (pequeno ? 14 : 22) + BORDE * 2;
        return new Dimension(fm.stringWidth(getText()) + relleno() * 2 + BORDE * 2, alto + SOMBRA + SUBIDA + 1);
    }

    private Color colorFondo() {
        return switch (estilo) {
            case PRINCIPAL -> Tema.boton();
            case SECUNDARIO -> Tema.boton2();
            case ROSA -> Tema.rosa();
            case PESTANA -> Tema.caja();
        };
    }

    private Color colorTexto() {
        return switch (estilo) {
            case PRINCIPAL -> Tema.textoBoton();
            case SECUNDARIO -> Tema.textoBoton2();
            case ROSA -> new Color(91, 22, 38);
            case PESTANA -> Tema.textoSuave();
        };
    }

    @Override
    protected void paintComponent(Graphics g) {
        Graphics2D g2 = (Graphics2D) g.create();
        if (!isEnabled()) {
            g2.setComposite(AlphaComposite.getInstance(AlphaComposite.SRC_OVER, 0.5f));
        }
        // Calcula el cuerpo del botón (sin sombra) y su posición vertical: sube con el mouse y baja al apretar.
        int w = getWidth();
        int cuerpo = getHeight() - SOMBRA - SUBIDA - 1;
        int y = apretado ? SUBIDA + BAJADA : (encima ? 0 : SUBIDA);
        int sombra = apretado ? 1 : SOMBRA;
        g2.setColor(Tema.sombra());
        g2.fillRect(0, y + cuerpo - BORDE, w, BORDE + sombra);
        g2.setColor(Tema.borde());
        g2.fillRect(0, y, w, cuerpo);
        g2.setColor(colorFondo());
        g2.fillRect(BORDE, y + BORDE, w - BORDE * 2, cuerpo - BORDE * 2);
        g2.setColor(new Color(255, 255, 255, 100));
        g2.fillRect(BORDE, y + BORDE, w - BORDE * 2, BORDE);
        g2.setColor(new Color(0, 0, 0, 41));
        g2.fillRect(BORDE, y + cuerpo - BORDE * 2, w - BORDE * 2, BORDE);
        g2.setFont(getFont());
        FontMetrics fm = g2.getFontMetrics();
        g2.setColor(colorTexto());
        int tx = (w - fm.stringWidth(getText())) / 2;
        int ty = y + (cuerpo - fm.getHeight()) / 2 + fm.getAscent();
        g2.drawString(getText(), tx, ty);
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

    // Devuelve un panel que estira este botón a todo el ancho disponible.
    public javax.swing.JPanel anchoCompleto() {
        javax.swing.JPanel panel = new javax.swing.JPanel(new java.awt.BorderLayout());
        panel.setOpaque(false);
        panel.add(this, java.awt.BorderLayout.CENTER);
        panel.setMaximumSize(new Dimension(Integer.MAX_VALUE, getPreferredSize().height));
        panel.setAlignmentX(java.awt.Component.LEFT_ALIGNMENT);
        return panel;
    }
}
