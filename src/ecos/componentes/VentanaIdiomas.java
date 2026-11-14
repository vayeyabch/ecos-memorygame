package ecos.componentes;

import ecos.app.Idioma;
import ecos.recursos.Sonido;
import java.awt.Component;
import javax.swing.Box;
import javax.swing.BoxLayout;
import javax.swing.JComponent;

// Ventana del botón del globo: muestra los idiomas disponibles y cambia el idioma de todo el juego.
public class VentanaIdiomas {

    // Título de la ventana y nombres de los idiomas, que no se traducen.
    private static final String TITULO = "IDIOMAS DISPONIBLES";
    private static final String ESPANOL = "Español";
    private static final String INGLES = "English";
    private static final String CERRAR = "Cerrar";

    // Abre la ventana encima de la pantalla actual; origen sirve para saber en qué ventana se dibuja.
    public static void mostrar(Component origen) {
        PanelPixel panel = new PanelPixel();
        panel.setLayout(new BoxLayout(panel, BoxLayout.Y_AXIS));
        panel.setPadding(18);
        CapaModal capa = new CapaModal(panel);
        // Título y botones de español e inglés, con el idioma actual resaltado.
        panel.add(centrar(Etiqueta.titulo(TITULO, 15f, Etiqueta.Rol.TEXTO)));
        panel.add(Box.createVerticalStrut(16));
        panel.add(centrar(opcion(ESPANOL, Idioma.Lengua.ESPANOL, capa)));
        panel.add(Box.createVerticalStrut(10));
        panel.add(centrar(opcion(INGLES, Idioma.Lengua.INGLES, capa)));
        panel.add(Box.createVerticalStrut(16));
        BotonMenu cerrar = new BotonMenu(CERRAR, BotonMenu.Estilo.ROSA);
        cerrar.setPequeno(true);
        cerrar.addActionListener(e -> { Sonido.efecto("click"); capa.cerrar(); });
        panel.add(centrar(cerrar));
        capa.mostrar(origen);
    }

    // Un botón por idioma: al hacer clic suena, cambia el idioma y cierra la ventana.
    private static JComponent opcion(String texto, Idioma.Lengua lengua, CapaModal capa) {
        BotonMenu boton = new BotonMenu(texto, lengua == Idioma.actual() ? BotonMenu.Estilo.PRINCIPAL : BotonMenu.Estilo.SECUNDARIO);
        boton.addActionListener(e -> { Sonido.efecto("click"); capa.cerrar(); Idioma.cambiar(lengua); });
        return boton.anchoCompleto();
    }

    private static JComponent centrar(JComponent c) {
        c.setAlignmentX(Component.CENTER_ALIGNMENT);
        return c;
    }
}
