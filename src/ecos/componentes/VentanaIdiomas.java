package ecos.componentes;

// Clases del propio proyecto que se usan aquí.
import ecos.app.Idioma;
import ecos.recursos.Sonido;
// Librería gráfica AWT, Component para cualquier elemento de la pantalla.
import java.awt.Component;
// Librería Swing, Box para los espacios entre componentes, BoxLayout para apilar en fila o columna y JComponent para la
// base de los componentes.
import javax.swing.Box;
import javax.swing.BoxLayout;
import javax.swing.JComponent;

// Ventana que se abre con el botón del globo, muestra los idiomas disponibles y cambia el idioma de todo el juego.
public class VentanaIdiomas {

    // Título de la ventana, "AVAILABLE LANGUAGES" en inglés, y nombres de los idiomas, que se quedan igual en los dos
    // idiomas.
    private static final String TITULO = "IDIOMAS DISPONIBLES";
    private static final String ESPANOL = "Español";
    private static final String INGLES = "English";
    private static final String CERRAR = "Cerrar";

    // Abre la ventana encima de la pantalla actual. Recibe cualquier componente de la ventana.
    // "origen" sirve para saber en qué ventana se dibuja.
    public static void mostrar(Component origen) {
        // Caja con borde pixelado que apila todo en vertical, con margen interno, sobre una cortina oscura con el panel al
        // centro.
        PanelPixel panel = new PanelPixel();
        panel.setLayout(new BoxLayout(panel, BoxLayout.Y_AXIS));
        panel.setPadding(18);
        CapaModal capa = new CapaModal(panel);
        // Título, botón de español y botón de inglés (el idioma actual se ve resaltado).
        panel.add(centrar(Etiqueta.titulo(TITULO, 15f, Etiqueta.Rol.TEXTO)));
        panel.add(Box.createVerticalStrut(16));
        panel.add(centrar(opcion(ESPANOL, Idioma.Lengua.ESPANOL, capa)));
        panel.add(Box.createVerticalStrut(10));
        panel.add(centrar(opcion(INGLES, Idioma.Lengua.INGLES, capa)));
        panel.add(Box.createVerticalStrut(16));
        BotonMenu cerrar = new BotonMenu(CERRAR, BotonMenu.Estilo.ROSA);
        cerrar.setPequeno(true);
        // Al hacer clic suena y se cierra. Agrega el botón. Muestra la ventana.
        cerrar.addActionListener(e -> { Sonido.efecto("click"); capa.cerrar(); });
        panel.add(centrar(cerrar));
        capa.mostrar(origen);
    }

    // Un botón de idioma, al hacer clic suena, cambia el idioma y cierra la ventana.
    // Recibe el texto, el idioma que activa y la ventana.
    private static JComponent opcion(String texto, Idioma.Lengua lengua, CapaModal capa) {
        // Crea el botón, resaltado si es el idioma actual, que suena, cambia el idioma y cierra, y lo estira al ancho de la
        // ventana.
        BotonMenu boton = new BotonMenu(texto, lengua == Idioma.actual() ? BotonMenu.Estilo.PRINCIPAL : BotonMenu.Estilo.SECUNDARIO);
        boton.addActionListener(e -> { Sonido.efecto("click"); capa.cerrar(); Idioma.cambiar(lengua); });
        return boton.anchoCompleto();
    }

    // Centra un componente dentro de la caja.
    private static JComponent centrar(JComponent c) {
        // Lo alinea al centro.
        c.setAlignmentX(Component.CENTER_ALIGNMENT);
        return c;
    }
}
