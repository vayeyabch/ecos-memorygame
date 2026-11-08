package ecos.componentes;

import ecos.recursos.Sonido; // Efectos de sonido.
import java.awt.Component; // Tipo general de componente.
import java.awt.Dimension; // Ancho y alto.
import java.awt.Image; // Imagen opcional.
import javax.swing.Box; // Espacios vacíos.
import javax.swing.BoxLayout; // Apila en vertical.
import javax.swing.JComponent; // Componente básico.

// Ventana de aviso con un título, un mensaje y un botón (por ejemplo "te faltan monedas").
public class DialogoAviso {

    private final CapaModal capa; // La ventana en sí.

    public DialogoAviso(String titulo, String mensaje, Image icono) { // El icono puede ser null.
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
        BotonMenu entendido = new BotonMenu("Entendido", BotonMenu.Estilo.ROSA); // Cierra el aviso.
        if (icono != null) {
            panel.add(centrar(new ImagenPixel(icono)));
            panel.add(Box.createVerticalStrut(10));
        }
        panel.add(centrar(Etiqueta.titulo(titulo, 14f, Etiqueta.Rol.TEXTO)));
        panel.add(Box.createVerticalStrut(10));
        panel.add(centrar(Etiqueta.texto("<html><div style='width:400px;text-align:center'>" + ecos.recursos.Fuentes.conNumeros(mensaje) + "</div></html>", 20f, Etiqueta.Rol.SUAVE)));
        panel.add(Box.createVerticalStrut(14));
        panel.add(centrar(entendido));
        capa = new CapaModal(panel);
        entendido.addActionListener(e -> { Sonido.efecto("click"); capa.cerrar(); });
    }

    public void mostrar(Component origen) { capa.mostrar(origen); } // Abre el aviso.

    private static JComponent centrar(JComponent c) { c.setAlignmentX(Component.CENTER_ALIGNMENT); return c; } // Centra.
}
