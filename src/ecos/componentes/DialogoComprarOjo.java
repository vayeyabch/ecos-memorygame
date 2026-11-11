package ecos.componentes;

// Clases del propio proyecto que se usan aquí.
import ecos.recursos.Sonido;
import ecos.recursos.Sprites;
// Librería gráfica AWT, Component para cualquier elemento de la pantalla, Dimension para los tamaños y FlowLayout para
// acomodar en fila.
import java.awt.Component;
import java.awt.Dimension;
import java.awt.FlowLayout;
// Librería Swing, Box para los espacios entre componentes, BoxLayout para apilar en fila o columna, JComponent para la
// base de los componentes y JPanel para los paneles.
import javax.swing.Box;
import javax.swing.BoxLayout;
import javax.swing.JComponent;
import javax.swing.JPanel;

// Ventana para comprar el comodín "Ojo espía".
public class DialogoComprarOjo {

    // La capa oscura con la ventana encima; se usa para mostrarla y cerrarla.
    private final CapaModal capa;

    // Recibe el precio del ojo y qué hacer si el jugador confirma la compra.
    public DialogoComprarOjo(int precio, Runnable alComprar) {
        // El panel de la ventana, mínimo 480 px de ancho y no se estira más de lo que necesita.
        PanelPixel panel = new PanelPixel() {
            @Override
            public Dimension getPreferredSize() {
                return new Dimension(Math.max(480, super.getPreferredSize().width), super.getPreferredSize().height);
            }

            @Override
            public Dimension getMaximumSize() {
                return getPreferredSize();
            }
        };
        // Se acomodan los elementos uno debajo del otro con un margen adentro.
        panel.setLayout(new BoxLayout(panel, BoxLayout.Y_AXIS));
        panel.setPadding(14);
        // El precio va en una cajita con el ícono de la moneda.
        ChipPixel chip = new ChipPixel("" + precio, 11f, true, ChipPixel.Modo.CAJA, 3);
        chip.setIcono(Sprites.ui("coin", 16));
        // Los dos botones, comprar (rosa) y cancelar.
        BotonMenu comprar = new BotonMenu("Comprar", BotonMenu.Estilo.ROSA);
        BotonMenu cancelar = new BotonMenu("Cancelar", BotonMenu.Estilo.SECUNDARIO);
        JPanel botones = new JPanel(new FlowLayout(FlowLayout.CENTER, 10, 0));
        botones.setOpaque(false);
        botones.add(comprar);
        botones.add(cancelar);
        // Las piezas de la ventana en orden, imagen del ojo, título, explicación (con los números en la fuente clara), precio y botones.
        JComponent[] piezas = {
            new ImagenPixel(Sprites.ui("eye", 64)),
            Etiqueta.titulo("Ojo espía", 16f, Etiqueta.Rol.TEXTO),
            Etiqueta.texto("<html>" + ecos.recursos.Fuentes.conNumeros(ecos.app.Idioma.t("Voltea todas las cartas de tu tablero durante 1.5 segundos.")) + "</html>", 18f, Etiqueta.Rol.SUAVE),
            chip, botones};
        // Se centran todas y se les deja un espacio entre una y otra.
        for (JComponent c : piezas) {
            c.setAlignmentX(Component.CENTER_ALIGNMENT);
            panel.add(c);
            panel.add(Box.createVerticalStrut(12));
        }
        // Se crea la capa con el panel y se conectan los botones, comprar cierra y ejecuta la compra, cancelar solo cierra.
        capa = new CapaModal(panel);
        comprar.addActionListener(e -> { capa.cerrar(); alComprar.run(); });
        cancelar.addActionListener(e -> { Sonido.efecto("click"); capa.cerrar(); });
    }

    // Abre la ventana con su sonido; recibe cualquier componente para saber dónde dibujarse.
    public void mostrar(Component origen) {
        Sonido.efecto("comprar");
        capa.mostrar(origen);
    }
}
