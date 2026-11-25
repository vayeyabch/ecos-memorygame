package ecos.componentes;

// Idioma da el idioma actual y permite cambiarlo. Aquí se usa para saber cuál botón resaltar y para cambiar al tocar uno.
import ecos.app.Idioma;
// Sonido reproduce el efecto de clic al tocar un botón.
import ecos.recursos.Sonido;
// Component es el tipo general de cualquier pieza visual de Swing. Se usa para el origen y para centrar piezas.
import java.awt.Component;
// Box trae los espacios invisibles. Aquí se usa para separar los elementos hacia abajo.
import javax.swing.Box;
// BoxLayout acomoda las piezas una debajo de otra, o una al lado de otra. Aquí van una debajo de otra.
import javax.swing.BoxLayout;
// JComponent es el tipo base de las piezas de Swing. Los métodos devuelven este tipo para poder agregarlas al panel.
import javax.swing.JComponent;

// Es la ventanita que se abre con el botón del globo para elegir el idioma de todo el juego.
// No es una Pantalla, es un diálogo que se muestra encima de la pantalla actual, dentro de una CapaModal.
// PanelPixel, CapaModal, BotonMenu y Etiqueta no se importan porque están en el mismo paquete, ecos.componentes.
// Todo es static, así que se usa escribiendo VentanaIdiomas.mostrar(...), sin crear un objeto.
public class VentanaIdiomas {

    // Textos fijos de la ventana. Son constantes porque no cambian.
    // Los nombres de los idiomas no se traducen, para que cada persona reconozca el suyo.
    private static final String TITULO = "IDIOMAS DISPONIBLES";
    private static final String ESPANOL = "Español";
    private static final String INGLES = "English";
    private static final String CERRAR = "Cerrar";

    // Arma y abre la ventana encima de la pantalla actual. La llama BarraSuperior cuando se toca el botón del globo.
    // El parámetro origen es una pieza de la pantalla desde la que se abre, y sirve para saber en qué ventana dibujar la capa.
    public static void mostrar(Component origen) {
        // Crea el panel con el estilo pixel del juego.
        PanelPixel panel = new PanelPixel();
        // Y_AXIS significa que las piezas se acomodan una debajo de otra.
        panel.setLayout(new BoxLayout(panel, BoxLayout.Y_AXIS));
        // Margen interior de 18 píxeles.
        panel.setPadding(18);
        // La capa que contiene el panel y lo muestra encima de la pantalla, oscureciendo lo de atrás.
        CapaModal capa = new CapaModal(panel);
        // Título arriba. Se agrega centrado, y el strut es un espacio invisible de 16 píxeles de alto.
        panel.add(centrar(Etiqueta.titulo(TITULO, 15f, Etiqueta.Rol.TEXTO)));
        panel.add(Box.createVerticalStrut(16));
        // Un botón por idioma, con el idioma actual resaltado.
        panel.add(centrar(opcion(ESPANOL, Idioma.Lengua.ESPANOL, capa)));
        panel.add(Box.createVerticalStrut(10));
        panel.add(centrar(opcion(INGLES, Idioma.Lengua.INGLES, capa)));
        panel.add(Box.createVerticalStrut(16));
        // Botón de cerrar, rosa y pequeño.
        BotonMenu cerrar = new BotonMenu(CERRAR, BotonMenu.Estilo.ROSA);
        cerrar.setPequeno(true);
        // Al hacer clic suena el clic y cierra la capa. La flecha es la función que se ejecuta, y e es el aviso del clic.
        cerrar.addActionListener(e -> { Sonido.efecto("click"); capa.cerrar(); });
        panel.add(centrar(cerrar));
        // Muestra la capa encima de la pantalla actual.
        capa.mostrar(origen);
    }

    // Crea el botón de un idioma. Al hacer clic suena, se cierra la ventana y se cambia el idioma.
    // Se llama una vez por idioma, así que no se repite el código del botón.
    private static JComponent opcion(String texto, Idioma.Lengua lengua, CapaModal capa) {
        // El signo de pregunta es un if corto. Si este es el idioma actual, el botón es PRINCIPAL, o sea resaltado.
        // Si no, es SECUNDARIO. Así se ve cuál idioma está activo.
        BotonMenu boton = new BotonMenu(texto, lengua == Idioma.actual() ? BotonMenu.Estilo.PRINCIPAL : BotonMenu.Estilo.SECUNDARIO);
        // Primero suena, luego cierra la ventana y al final cambia el idioma, que avisa a todas las piezas suscritas.
        boton.addActionListener(e -> { Sonido.efecto("click"); capa.cerrar(); Idioma.cambiar(lengua); });
        // anchoCompleto es un método de BotonMenu que deja el botón del ancho del panel. Se devuelve como JComponent.
        return boton.anchoCompleto();
    }

    // Alinea una pieza al centro. En un BoxLayout vertical, cada pieza se alinea por su cuenta,
    // y este método evita repetir la misma línea para cada una.
    private static JComponent centrar(JComponent c) {
        c.setAlignmentX(Component.CENTER_ALIGNMENT);
        return c;
    }
}