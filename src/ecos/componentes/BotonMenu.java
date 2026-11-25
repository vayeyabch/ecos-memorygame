package ecos.componentes;

// Idioma: traduce el texto del botón y avisa cuando cambia el idioma.
import ecos.app.Idioma;
// Tema: da los colores del botón según claro u oscuro y avisa cuando cambia el modo.
import ecos.app.Tema;
// Fuentes: da la letra de los botones.
import ecos.recursos.Fuentes;
// AlphaComposite: permite dibujar con transparencia (botón apagado se ve medio transparente).
import java.awt.AlphaComposite;
// Color: guarda colores.
import java.awt.Color;
// Cursor: cambia el puntero del mouse (manito) al pasar por el botón.
import java.awt.Cursor;
// Dimension: ancho y alto; se usa para el tamaño del botón.
import java.awt.Dimension;
// FontMetrics: mide el texto para calcular el tamaño y centrarlo.
import java.awt.FontMetrics;
// Graphics: el "pincel" para dibujar.
import java.awt.Graphics;
// Graphics2D: versión del pincel con más opciones (transparencia).
import java.awt.Graphics2D;
// MouseAdapter: clase lista para escuchar el mouse y sobrescribir solo lo que se necesita.
import java.awt.event.MouseAdapter;
// MouseEvent: datos de un evento del mouse.
import java.awt.event.MouseEvent;
// JButton: clase base de la que hereda BotonMenu; da el clic y el texto.
import javax.swing.JButton;

// Botón pixel con varios estilos (principal, secundario, rosa, pestaña); sube al pasar el mouse y baja al apretarlo.
public class BotonMenu extends JButton {

    // Los cuatro estilos posibles. Cada uno cambia los colores.
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
    // Los dos Runnable se guardan en campos para poder des-suscribir exactamente el mismo en removeNotify.
    private final Runnable alCambiarTema = this::repaint;
    private String clave;
    private final Runnable alCambiarIdioma = this::traducir;

    // Si es principal usa el color durazno; si no, el secundario.
    public BotonMenu(String texto, boolean principal) {
        // this(...) llama al otro constructor con el estilo que corresponde.
        this(texto, principal ? Estilo.PRINCIPAL : Estilo.SECUNDARIO);
    }

    public BotonMenu(String texto, Estilo estilo) {
        super(texto);
        this.estilo = estilo;
        setFont(Fuentes.titulo(14f));
        // Se apagan los dibujos normales de Swing, porque el botón se dibuja a mano en paintComponent.
        setContentAreaFilled(false);
        setBorderPainted(false);
        setFocusPainted(false);
        setOpaque(false);
        setRolloverEnabled(false);
        // Puntero de manito.
        setCursor(Cursor.getPredefinedCursor(Cursor.HAND_CURSOR));
        // Clase anónima: escucha el mouse para saber si está encima o apretado, y repinta en cada cambio.
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

    // Guarda el texto original (en español) como clave y muestra el traducido.
    @Override
    public void setText(String texto) {
        clave = texto;
        super.setText(Idioma.t(texto));
    }

    // Vuelve a poner el texto traducido al idioma actual.
    private void traducir() {
        super.setText(Idioma.t(clave));
    }

    // Cambia el estilo después de creado (el selector 4x4/6x6 lo usa).
    public void setEstilo(Estilo estilo) {
        this.estilo = estilo;
        repaint();
    }

    // Versión chica del botón (la usa el botón Atrás): letra más pequeña.
    public void setPequeno(boolean pequeno) {
        this.pequeno = pequeno;
        setFont(Fuentes.titulo(pequeno ? 12f : 14f));
        // revalidate recalcula el tamaño y repaint lo vuelve a dibujar.
        revalidate();
        repaint();
    }

    // Se llama cuando el botón entra a la pantalla: se suscribe a Tema e Idioma.
    @Override
    public void addNotify() {
        super.addNotify();
        Tema.alCambiar(alCambiarTema);
        Idioma.alCambiar(alCambiarIdioma);
        traducir();
    }

    // Se llama cuando el botón sale de la pantalla: se quita de las listas para no repintarse sin necesidad.
    @Override
    public void removeNotify() {
        Tema.quitar(alCambiarTema);
        Idioma.quitar(alCambiarIdioma);
        super.removeNotify();
    }

    // Espacio a los lados del texto: menos en la versión chica.
    private int relleno() {
        return pequeno ? 12 : 20;
    }

    // Calcula el tamaño del botón según el ancho del texto, más relleno, borde y sombra.
    @Override
    public Dimension getPreferredSize() {
        FontMetrics fm = getFontMetrics(getFont());
        int alto = fm.getHeight() + (pequeno ? 14 : 22) + BORDE * 2;
        return new Dimension(fm.stringWidth(getText()) + relleno() * 2 + BORDE * 2, alto + SOMBRA + SUBIDA + 1);
    }

    // Color de fondo según el estilo (switch con flecha: devuelve un valor).
    private Color colorFondo() {
        return switch (estilo) {
            case PRINCIPAL -> Tema.boton();
            case SECUNDARIO -> Tema.boton2();
            case ROSA -> Tema.rosa();
            case PESTANA -> Tema.caja();
        };
    }

    // Color del texto según el estilo.
    private Color colorTexto() {
        return switch (estilo) {
            case PRINCIPAL -> Tema.textoBoton();
            case SECUNDARIO -> Tema.textoBoton2();
            case ROSA -> new Color(91, 22, 38);
            case PESTANA -> Tema.textoSuave();
        };
    }

    // Dibuja el botón a mano: sombra, borde, fondo, brillo arriba, sombra abajo y el texto centrado.
    @Override
    protected void paintComponent(Graphics g) {
        // Copia del pincel para no afectar el original.
        Graphics2D g2 = (Graphics2D) g.create();
        // Si está desactivado, se dibuja al 50% de opacidad.
        if (!isEnabled()) {
            g2.setComposite(AlphaComposite.getInstance(AlphaComposite.SRC_OVER, 0.5f));
        }
        // Calcula el cuerpo del botón (sin sombra) y su posición vertical: sube con el mouse y baja al apretar.
        int w = getWidth();
        int cuerpo = getHeight() - SOMBRA - SUBIDA - 1;
        int y = apretado ? SUBIDA + BAJADA : (encima ? 0 : SUBIDA);
        int sombra = apretado ? 1 : SOMBRA;
        // 1) Sombra.
        g2.setColor(Tema.sombra());
        g2.fillRect(0, y + cuerpo - BORDE, w, BORDE + sombra);
        // 2) Borde (rectángulo completo, luego se pinta el fondo encima).
        g2.setColor(Tema.borde());
        g2.fillRect(0, y, w, cuerpo);
        // 3) Fondo del color del estilo.
        g2.setColor(colorFondo());
        g2.fillRect(BORDE, y + BORDE, w - BORDE * 2, cuerpo - BORDE * 2);
        // 4) Brillo blanco semitransparente arriba.
        g2.setColor(new Color(255, 255, 255, 100));
        g2.fillRect(BORDE, y + BORDE, w - BORDE * 2, BORDE);
        // 5) Franja oscura semitransparente abajo.
        g2.setColor(new Color(0, 0, 0, 41));
        g2.fillRect(BORDE, y + cuerpo - BORDE * 2, w - BORDE * 2, BORDE);
        // 6) Texto centrado horizontal y verticalmente.
        g2.setFont(getFont());
        FontMetrics fm = g2.getFontMetrics();
        g2.setColor(colorTexto());
        int tx = (w - fm.stringWidth(getText())) / 2;
        int ty = y + (cuerpo - fm.getHeight()) / 2 + fm.getAscent();
        g2.drawString(getText(), tx, ty);
        // Libera la copia del pincel.
        g2.dispose();
    }

    // Mínimo y máximo iguales al preferido: el layout no lo estira ni lo encoge.
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