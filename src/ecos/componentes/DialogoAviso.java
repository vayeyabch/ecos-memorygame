package ecos.componentes;

// Clases del propio proyecto que se usan aquí.
import ecos.recursos.Sonido;
// Librería gráfica AWT, Component para cualquier elemento de la pantalla, Dimension para los tamaños y Image para las
// imágenes.
import java.awt.Component;
import java.awt.Dimension;
import java.awt.Image;
// Librería Swing, Box para los espacios entre componentes, BoxLayout para apilar en fila o columna y JComponent para la
// base de los componentes.
import javax.swing.Box;
import javax.swing.BoxLayout;
import javax.swing.JComponent;

// Ventana de aviso con un título, un mensaje y un botón (por ejemplo "te faltan monedas").
public class DialogoAviso {

    // La ventana en sí.
    private final CapaModal capa;

    // El icono puede ser null.
    public DialogoAviso(String titulo, String mensaje, Image icono) {
        // El panel de la ventana, mínimo 480 px de ancho y margen adentro.
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
        panel.setLayout(new BoxLayout(panel, BoxLayout.Y_AXIS));
        panel.setPadding(16);
        // Cierra el aviso.
        BotonMenu entendido = new BotonMenu("Entendido", BotonMenu.Estilo.ROSA);
        // Se agregan, de arriba hacia abajo, el ícono (si hay), el título, el mensaje con los números en la fuente clara y el botón.
        if (icono != null) {
            panel.add(centrar(new ImagenPixel(icono)));
            panel.add(Box.createVerticalStrut(10));
        }
        panel.add(centrar(Etiqueta.titulo(titulo, 14f, Etiqueta.Rol.TEXTO)));
        panel.add(Box.createVerticalStrut(10));
        panel.add(centrar(Etiqueta.texto("<html><div style='width:400px;text-align:center'>" + ecos.recursos.Fuentes.conNumeros(mensaje) + "</div></html>", 20f, Etiqueta.Rol.SUAVE)));
        panel.add(Box.createVerticalStrut(14));
        panel.add(centrar(entendido));
        // Se crea la capa con el panel y el botón solo cierra el aviso.
        capa = new CapaModal(panel);
        entendido.addActionListener(e -> { Sonido.efecto("click"); capa.cerrar(); });
    }

    // Abre el aviso.
    public void mostrar(Component origen) { capa.mostrar(origen); }

    private static JComponent centrar(JComponent c) { c.setAlignmentX(Component.CENTER_ALIGNMENT); return c; }
}
