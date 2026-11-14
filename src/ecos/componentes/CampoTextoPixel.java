package ecos.componentes;

import ecos.app.Idioma;
import ecos.app.Tema;
import ecos.recursos.Fuentes;
import ecos.recursos.Sonido;
import java.awt.Color;
import java.awt.Dimension;
import java.awt.Graphics;
import java.awt.Graphics2D;
import java.awt.event.KeyAdapter;
import java.awt.event.KeyEvent;
import javax.swing.BorderFactory;
import javax.swing.JTextField;
import javax.swing.text.AbstractDocument;
import javax.swing.text.AttributeSet;
import javax.swing.text.BadLocationException;
import javax.swing.text.DocumentFilter;

// Campo de texto pixel con texto de ayuda y límite de letras.
public class CampoTextoPixel extends JTextField {

    // Grosor del borde, texto de ayuda mientras está vacío y acción al cambiar el tema.
    private static final int BORDE = 4;
    private final String ayuda;
    private final Runnable alCambiarTema = this::actualizarColores;
    private final Runnable alCambiarIdioma = this::repaint;

    public CampoTextoPixel(String ayuda, int columnas) {
        super(columnas);
        this.ayuda = ayuda;
        setOpaque(false);
        setFont(Fuentes.texto(22f));
        setBorder(BorderFactory.createEmptyBorder(10, 14, 10, 14));
        addKeyListener(new KeyAdapter() {
            @Override
            public void keyTyped(KeyEvent e) {
                // Solo suena con letras, números y símbolos, no con Enter ni Ctrl.
                if (!e.isControlDown() && e.getKeyChar() >= 32) {
                    Sonido.efecto("teclear");
                }
            }
        });
        actualizarColores();
    }

    // Cambia la letra por la de títulos, que tiene los números más claros; se usa en el campo de la IP. El alto y el
    // texto de ayuda quedan igual.
    public void usarNumerosClaros() {
        Dimension d = getPreferredSize();
        setFont(Fuentes.titulo(14f));
        setPreferredSize(d);
    }

    public void setLimite(int maximo) {
        ((AbstractDocument) getDocument()).setDocumentFilter(new DocumentFilter() {
            @Override
            public void replace(FilterBypass fb, int desde, int largo, String texto, AttributeSet atributos) throws BadLocationException {
                // Calcula cuántas letras caben todavía y mete el texto, cortado si es largo.
                String nuevo = texto == null ? "" : texto;
                int cabe = maximo - (fb.getDocument().getLength() - largo);
                if (cabe > 0) {
                    super.replace(fb, desde, largo, nuevo.length() > cabe ? nuevo.substring(0, cabe) : nuevo, atributos);
                }
            }
        });
    }

    @Override
    public void addNotify() {
        super.addNotify();
        Tema.alCambiar(alCambiarTema);
        Idioma.alCambiar(alCambiarIdioma);
        actualizarColores();
    }

    @Override
    public void removeNotify() {
        Tema.quitar(alCambiarTema);
        Idioma.quitar(alCambiarIdioma);
        super.removeNotify();
    }

    private void actualizarColores() {
        setForeground(Tema.texto());
        setCaretColor(Tema.texto());
        repaint();
    }

    @Override
    protected void paintComponent(Graphics g) {
        // Dibuja el borde con el color de acento si el campo está seleccionado y el relleno con el color de caja.
        Graphics2D g2 = (Graphics2D) g.create();
        g2.setColor(hasFocus() ? Tema.acento() : Tema.borde());
        g2.fillRect(0, 0, getWidth(), getHeight());
        g2.setColor(Tema.caja());
        // Si está vacío y hay texto de ayuda lo dibuja a la izquierda con la fuente de texto y el color suave.
        g2.fillRect(BORDE, BORDE, getWidth() - BORDE * 2, getHeight() - BORDE * 2);
        if (getText().isEmpty() && ayuda != null) {
            g2.setFont(Fuentes.texto(22f));
            Color c = Tema.textoSuave();
            g2.setColor(c);
            g2.drawString(Idioma.t(ayuda), getInsets().left, (getHeight() + g2.getFontMetrics().getAscent()) / 2 - 2);
        }
        // Después deja que JTextField dibuje el texto escrito y el cursor.
        g2.dispose();
        super.paintComponent(g);
    }
}
