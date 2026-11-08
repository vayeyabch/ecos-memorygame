package ecos.componentes;

// Clases del propio proyecto que se usan aquí.
import ecos.app.Tema;
import ecos.recursos.Fuentes;
import ecos.recursos.Sonido;
// Librería gráfica AWT, Color para los colores, Graphics para dibujar y Graphics2D para dibujar con más opciones.
import java.awt.Color;
import java.awt.Graphics;
import java.awt.Graphics2D;
// Eventos del mouse y el teclado de AWT, KeyAdapter para reaccionar al teclado y KeyEvent para las teclas.
import java.awt.event.KeyAdapter;
import java.awt.event.KeyEvent;
// Librería Swing, BorderFactory para los bordes y JTextField para los campos de texto.
import javax.swing.BorderFactory;
import javax.swing.JTextField;
// Texto de Swing, AbstractDocument para el contenido de un campo de texto, AttributeSet para el formato del texto,
// BadLocationException para los errores al editar texto y DocumentFilter para filtrar lo que se escribe.
import javax.swing.text.AbstractDocument;
import javax.swing.text.AttributeSet;
import javax.swing.text.BadLocationException;
import javax.swing.text.DocumentFilter;

// Campo de texto pixel con texto de ayuda y límite de letras.
public class CampoTextoPixel extends JTextField {

    // Grosor del borde en píxeles, texto gris cuando el campo está vacío (placeholder) y acción que actualiza los colores al
    // cambiar el tema.
    private static final int BORDE = 4;
    private final String ayuda;
    private final Runnable alCambiarTema = this::actualizarColores;

    // Recibe el texto de ayuda y el ancho en columnas.
    public CampoTextoPixel(String ayuda, int columnas) {
        // Le pasa el ancho al JTextField. Guarda el texto de ayuda. Lo hace transparente, porque el fondo se dibuja a mano.
        // Usa la fuente de texto del juego en tamaño 22.
        super(columnas);
        this.ayuda = ayuda;
        setOpaque(false);
        setFont(Fuentes.texto(22f));
        // Margen interno para que el texto no toque el borde. Escucha las teclas que se presionan.
        setBorder(BorderFactory.createEmptyBorder(10, 14, 10, 14));
        addKeyListener(new KeyAdapter() {
            // Se ejecuta cada vez que se escribe un carácter.
            @Override
            public void keyTyped(KeyEvent e) {
                // Solo suena con letras, números y símbolos (no con Enter ni Ctrl). Reproduce el sonido de teclear.
                if (!e.isControlDown() && e.getKeyChar() >= 32) {
                    Sonido.efecto("teclear");
                }
            }
        });
        // Pone los colores iniciales.
        actualizarColores();
    }

    // Pone un máximo de letras que se pueden escribir.
    // Recibe el máximo de letras.
    public void setLimite(int maximo) {
        // Filtro que revisa lo que entra.
        ((AbstractDocument) getDocument()).setDocumentFilter(new DocumentFilter() {
            // Se ejecuta cuando se escribe o pega algo.
            @Override
            public void replace(FilterBypass fb, int desde, int largo, String texto, AttributeSet atributos) throws BadLocationException {
                // Toma el texto que quiere entrar, calcula cuántas letras caben todavía y, si cabe algo, lo mete (cortado si
                // es largo).
                String nuevo = texto == null ? "" : texto;
                int cabe = maximo - (fb.getDocument().getLength() - largo);
                if (cabe > 0) {
                    super.replace(fb, desde, largo, nuevo.length() > cabe ? nuevo.substring(0, cabe) : nuevo, atributos);
                }
            }
        });
    }

    // Swing lo llama cuando el campo entra a una ventana.
    @Override
    public void addNotify() {
        // Deja que Swing haga lo suyo primero. Empieza a escuchar los cambios de tema. Se pone al día con el tema actual.
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

    // Método interno, ajusta los colores según el tema.
    private void actualizarColores() {
        // Color de las letras que se escriben. Color del cursor que parpadea. Repinta para que se vea el cambio.
        setForeground(Tema.texto());
        setCaretColor(Tema.texto());
        repaint();
    }

    // Swing lo llama cada vez que hay que dibujar el campo.
    @Override
    protected void paintComponent(Graphics g) {
        // Crea una copia del pincel y dibuja el borde con el color de acento si el campo está seleccionado, y si no con el
        // normal. Luego elige el color de caja del tema para el relleno.
        Graphics2D g2 = (Graphics2D) g.create();
        g2.setColor(hasFocus() ? Tema.acento() : Tema.borde());
        g2.fillRect(0, 0, getWidth(), getHeight());
        g2.setColor(Tema.caja());
        // Dibuja el relleno dejando visible el borde. Si el campo está vacío y hay texto de ayuda, usa la misma fuente del
        // campo y el color de texto suave del tema.
        g2.fillRect(BORDE, BORDE, getWidth() - BORDE * 2, getHeight() - BORDE * 2);
        if (getText().isEmpty() && ayuda != null) {
            g2.setFont(getFont());
            Color c = Tema.textoSuave();
            // Lo usa para el texto de ayuda. Dibuja el texto de ayuda a la izquierda y centrado vertical.
            g2.setColor(c);
            g2.drawString(ayuda, getInsets().left, (getHeight() + g2.getFontMetrics().getAscent()) / 2 - 2);
        }
        // Libera el pincel. Deja que JTextField dibuje el texto escrito y el cursor.
        g2.dispose();
        super.paintComponent(g);
    }
}
