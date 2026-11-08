package ecos.componentes;

// Clases del propio proyecto que se usan aquí.
import ecos.app.Tema;
import ecos.recursos.Sonido;
// Librería gráfica AWT, Component para cualquier elemento de la pantalla, Cursor para el cursor del mouse y Image para
// las imágenes.
import java.awt.Component;
import java.awt.Cursor;
import java.awt.Image;
// Eventos del mouse y el teclado de AWT, ActionEvent para las acciones, MouseAdapter para reaccionar al mouse y
// MouseEvent para los datos de un clic.
import java.awt.event.ActionEvent;
import java.awt.event.MouseAdapter;
import java.awt.event.MouseEvent;
// Librería Swing, AbstractAction para definir acciones, Box para los espacios entre componentes, BoxLayout para apilar
// en fila o columna, JComponent para la base de los componentes y KeyStroke para los atajos de teclado.
import javax.swing.AbstractAction;
import javax.swing.Box;
import javax.swing.BoxLayout;
import javax.swing.JComponent;
import javax.swing.KeyStroke;

// Tarjeta de un modo de juego en el menú, imagen, título y subtítulo; se puede elegir con el mouse o con el teclado.
public class TarjetaModo extends PanelPixel {

    // Qué pasa al tocar la tarjeta y si el mouse está encima.
    private final Runnable alElegir;
    private boolean encima = false;

    // Recibe el título, el subtítulo, la imagen y qué hacer al elegirla.
    public TarjetaModo(String titulo, String subtitulo, Image icono, Runnable alElegir) {
        super();
        this.alElegir = alElegir;
        setLayout(new BoxLayout(this, BoxLayout.Y_AXIS));
        setPadding(14);
        setCursor(Cursor.getPredefinedCursor(Cursor.HAND_CURSOR));
        setFocusable(true);

        // Se arman la imagen y los dos textos y se agregan centrados, con espacio flexible arriba y abajo.
        // Imagen, título y subtítulo, todos centrados y con espacio flexible arriba y abajo.
        ImagenPixel imagen = new ImagenPixel(icono);
        Etiqueta lblTitulo = Etiqueta.titulo(titulo, 16f, Etiqueta.Rol.TEXTO);
        Etiqueta lblSub = Etiqueta.texto(subtitulo, 18f, Etiqueta.Rol.SUAVE);
        for (JComponent c : new JComponent[]{imagen, lblTitulo, lblSub}) {
            c.setAlignmentX(Component.CENTER_ALIGNMENT);
        }
        add(Box.createVerticalGlue());
        add(imagen);
        add(Box.createVerticalStrut(10));
        add(lblTitulo);
        add(Box.createVerticalStrut(8));
        add(lblSub);
        add(Box.createVerticalGlue());

        // Clic con el mouse, elige la tarjeta y al pasar el mouse resalta el borde.
        // clic con el mouse
        addMouseListener(new MouseAdapter() {
            @Override
            public void mouseClicked(MouseEvent e) {
                elegir();
            }

            @Override
            public void mouseEntered(MouseEvent e) {
                encima = true;
                resaltar();
            }

            @Override
            public void mouseExited(MouseEvent e) {
                encima = false;
                resaltar();
            }
        });
        // Enter o Espacio cuando la tarjeta tiene el foco
        getActionMap().put("elegir", new AbstractAction() {
            @Override
            public void actionPerformed(ActionEvent e) {
                elegir();
            }
        });
        getInputMap(WHEN_FOCUSED).put(KeyStroke.getKeyStroke("ENTER"), "elegir");
        getInputMap(WHEN_FOCUSED).put(KeyStroke.getKeyStroke("SPACE"), "elegir");
    }

    // Agrega algo más abajo (por ejemplo, el selector de tamaño del tablero).
    public void agregarExtra(JComponent extra) {
        extra.setAlignmentX(Component.CENTER_ALIGNMENT);
        add(Box.createVerticalStrut(10));
        add(extra);
    }

    // Suena el clic y avisa que se eligió.
    private void elegir() {
        Sonido.efecto("click");
        alElegir.run();
    }

    // El borde se vuelve naranja cuando el mouse está encima.
    private void resaltar() {
        // El borde se vuelve naranja al pasar el mouse.
        setColorBorde(encima ? Tema.naranja() : null);
    }

}
