package ecos.componentes;

// Clases del propio proyecto que se usan aquí.
import ecos.app.Tema;
import ecos.recursos.Fuentes;
import ecos.recursos.Sonido;
// Librería gráfica AWT, Graphics para dibujar y Graphics2D para dibujar con más opciones.
import java.awt.Graphics;
import java.awt.Graphics2D;
// Eventos del mouse y el teclado de AWT, KeyAdapter para reaccionar al teclado y KeyEvent para las teclas.
import java.awt.event.KeyAdapter;
import java.awt.event.KeyEvent;
// Librería Swing, BorderFactory para los bordes y JPasswordField para los campos de contraseña.
import javax.swing.BorderFactory;
import javax.swing.JPasswordField;

// Igual que CampoTextoPixel, pero esconde lo que se escribe (para la contraseña).
public class CampoClavePixel extends JPasswordField {

    // Grosor del borde, texto gris mientras el campo está vacío y acción que se ejecuta cuando cambia el tema.
    private static final int BORDE = 4;
    private final String ayuda;
    private final Runnable alCambiarTema = this::actualizarColores;

    // Recibe el texto de ayuda y el ancho en columnas.
    public CampoClavePixel(String ayuda, int columnas) {
        // Crea el campo con ese ancho. Guarda el texto de ayuda. Transparente, el fondo se dibuja a mano. Fuente del texto.
        super(columnas);
        this.ayuda = ayuda;
        setOpaque(false);
        setFont(Fuentes.texto(22f));
        // Margen interno. Escucha el teclado.
        setBorder(BorderFactory.createEmptyBorder(10, 14, 10, 14));
        addKeyListener(new KeyAdapter() {
            // Se ejecuta al escribir una letra.
            @Override
            public void keyTyped(KeyEvent e) {
                // Si es un carácter normal. Suena la tecla.
                if (!e.isControlDown() && e.getKeyChar() >= 32) {
                    Sonido.efecto("teclear");
                }
            }
        });
        // Pone los colores del tema actual.
        actualizarColores();
    }

    // Swing lo llama cuando el campo entra a la ventana.
    @Override
    public void addNotify() {
        // Deja que Swing haga lo suyo. Empieza a escuchar los cambios de tema. Actualiza los colores.
        super.addNotify();
        Tema.alCambiar(alCambiarTema);
        actualizarColores();
    }

    // Swing lo llama cuando el campo sale de la ventana.
    @Override
    public void removeNotify() {
        // Deja de escuchar los cambios de tema. Deja que Swing haga lo suyo.
        Tema.quitar(alCambiarTema);
        super.removeNotify();
    }

    // Ajusta los colores según el tema.
    private void actualizarColores() {
        // Color de los puntitos. Color del cursor.
        setForeground(Tema.texto());
        setCaretColor(Tema.texto());
        repaint();
    }

    // Swing lo llama cada vez que hay que dibujar el campo.
    @Override
    protected void paintComponent(Graphics g) {
        // Usa una copia del pincel y dibuja el relleno y el borde, que usa el color de acento si el campo está seleccionado.
        Graphics2D g2 = (Graphics2D) g.create();
        g2.setColor(hasFocus() ? Tema.acento() : Tema.borde());
        g2.fillRect(0, 0, getWidth(), getHeight());
        g2.setColor(Tema.caja());
        // Dibuja el relleno. Si está vacío y hay texto de ayuda. Usa la misma fuente.
        g2.fillRect(BORDE, BORDE, getWidth() - BORDE * 2, getHeight() - BORDE * 2);
        if (getPassword().length == 0 && ayuda != null) {
            g2.setFont(getFont());
            g2.setColor(Tema.textoSuave());
            // Dibuja la ayuda centrada.
            g2.drawString(ayuda, getInsets().left, (getHeight() + g2.getFontMetrics().getAscent()) / 2 - 2);
        }
        // Libera el pincel. Dibuja los puntitos y el cursor.
        g2.dispose();
        super.paintComponent(g);
    }
}
