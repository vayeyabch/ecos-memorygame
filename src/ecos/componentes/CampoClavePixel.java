package ecos.componentes;

import ecos.app.Idioma;
import ecos.app.Tema;
import ecos.recursos.Fuentes;
import ecos.recursos.Sonido;
import java.awt.Graphics;
import java.awt.Graphics2D;
import java.awt.event.KeyAdapter;
import java.awt.event.KeyEvent;
import javax.swing.BorderFactory;
import javax.swing.JPasswordField;

// Igual que CampoTextoPixel, pero esconde lo que se escribe, para la contraseña.
public class CampoClavePixel extends JPasswordField {

    // Grosor del borde, texto de ayuda mientras está vacío y acción al cambiar el tema.
    private static final int BORDE = 4;
    private final String ayuda;
    private final Runnable alCambiarTema = this::actualizarColores;
    private final Runnable alCambiarIdioma = this::repaint;

    public CampoClavePixel(String ayuda, int columnas) {
        super(columnas);
        this.ayuda = ayuda;
        setOpaque(false);
        setFont(Fuentes.texto(22f));
        setBorder(BorderFactory.createEmptyBorder(10, 14, 10, 14));
        addKeyListener(new KeyAdapter() {
            @Override
            public void keyTyped(KeyEvent e) {
                // Si es un carácter normal suena la tecla.
                if (!e.isControlDown() && e.getKeyChar() >= 32) {
                    Sonido.efecto("teclear");
                }
            }
        });
        actualizarColores();
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
        // Dibuja relleno y borde; el borde usa el color de acento si el campo está seleccionado.
        Graphics2D g2 = (Graphics2D) g.create();
        g2.setColor(hasFocus() ? Tema.acento() : Tema.borde());
        g2.fillRect(0, 0, getWidth(), getHeight());
        g2.setColor(Tema.caja());
        g2.fillRect(BORDE, BORDE, getWidth() - BORDE * 2, getHeight() - BORDE * 2);
        if (getPassword().length == 0 && ayuda != null) {
            g2.setFont(getFont());
            g2.setColor(Tema.textoSuave());
            g2.drawString(Idioma.t(ayuda), getInsets().left, (getHeight() + g2.getFontMetrics().getAscent()) / 2 - 2);
        }
        // Dibuja los puntitos y el cursor.
        g2.dispose();
        super.paintComponent(g);
    }
}
