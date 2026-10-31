package ecos.componentes;

import ecos.recursos.Sonido; // Para el sonido de los clics.
import java.awt.Component; // Tipo general de cualquier componente.
import javax.swing.Box; // Para dejar espacios vacíos.
import javax.swing.BoxLayout; // Layout que apila en vertical.
import javax.swing.JComponent; // Componente básico de Swing.

// Ventana que se abre con el botón del globo: muestra los idiomas disponibles. Todavía sin lógica (solo se cierra).
public class VentanaIdiomas {

    private static final String TITULO = "IDIOMAS DISPONIBLES"; // EN: "AVAILABLE LANGUAGES".
    private static final String ESPANOL = "Español"; // Se queda igual en inglés.
    private static final String INGLES = "English"; // Se queda igual en español.
    private static final String CERRAR = "Cerrar"; // EN: "Close".

    // Abre la ventana encima de la pantalla actual. Recibe cualquier componente de la ventana.
    public static void mostrar(Component origen) { // "origen" sirve para saber en qué ventana se dibuja.
        PanelPixel panel = new PanelPixel(); // Caja con borde pixelado.
        panel.setLayout(new BoxLayout(panel, BoxLayout.Y_AXIS)); // Apila todo en vertical.
        panel.setPadding(18); // Margen interno.
        CapaModal capa = new CapaModal(panel); // Cortina oscura con el panel al centro.
        panel.add(centrar(Etiqueta.titulo(TITULO, 15f, Etiqueta.Rol.TEXTO))); // Título.
        panel.add(Box.createVerticalStrut(16)); // Espacio.
        panel.add(centrar(opcion(ESPANOL, BotonMenu.Estilo.PRINCIPAL, capa))); // Botón de español (el idioma actual se ve resaltado).
        panel.add(Box.createVerticalStrut(10)); // Espacio.
        panel.add(centrar(opcion(INGLES, BotonMenu.Estilo.SECUNDARIO, capa))); // Botón de inglés.
        panel.add(Box.createVerticalStrut(16)); // Espacio.
        BotonMenu cerrar = new BotonMenu(CERRAR, BotonMenu.Estilo.ROSA); // Botón para cerrar.
        cerrar.setPequeno(true); // Versión chica.
        cerrar.addActionListener(e -> { Sonido.efecto("click"); capa.cerrar(); }); // Al hacer clic suena y se cierra.
        panel.add(centrar(cerrar)); // Agrega el botón.
        capa.mostrar(origen); // Muestra la ventana.
    }

    // Un botón de idioma. Por ahora solo suena y cierra la ventana: falta cambiar el idioma de verdad.
    private static JComponent opcion(String texto, BotonMenu.Estilo estilo, CapaModal capa) { // Recibe el texto, el estilo y la ventana.
        BotonMenu boton = new BotonMenu(texto, estilo); // Crea el botón.
        boton.addActionListener(e -> { Sonido.efecto("click"); capa.cerrar(); }); // Suena y cierra.
        return boton.anchoCompleto(); // Lo estira al ancho de la ventana.
    }

    private static JComponent centrar(JComponent c) { // Centra un componente dentro de la caja.
        c.setAlignmentX(Component.CENTER_ALIGNMENT); // Lo alinea al centro.
        return c; // Lo entrega.
    }
}
