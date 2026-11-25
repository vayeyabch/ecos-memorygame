package ecos.componentes;

// Idioma da el idioma actual y traduce los textos. La etiqueta se suscribe para retraducirse cuando cambia.
import ecos.app.Idioma;
// Tema da los colores y avisa cuando cambia el modo. La etiqueta se suscribe para recolorearse.
import ecos.app.Tema;
// Fuentes da las dos tipografías del juego y arregla los números dentro de textos HTML.
import ecos.recursos.Fuentes;
// Color es el tipo de un color. Se usa en colorear para guardar el color elegido.
import java.awt.Color;
// JLabel es la etiqueta de texto de Swing. Etiqueta hereda de ella y le agrega fuente, color de tema y traducción.
import javax.swing.JLabel;

// Es el texto del juego. Las pantallas la usan en vez de un JLabel común porque ya resuelve sola
// la fuente, el color según el tema y la traducción según el idioma.
public class Etiqueta extends JLabel {

    // Un enum es una lista de opciones fijas. Cada rol es un tipo de color de texto.
    // TEXTO es el normal, SUAVE el apagado, ACENTO el de resalte, CARMESI el rojo carmesí,
    // PELIGRO el de errores y ACIERTO el verde de aciertos.
    public enum Rol { TEXTO, SUAVE, ACENTO, CARMESI, PELIGRO, ACIERTO }

    // El rol actual de esta etiqueta, que decide su color.
    private Rol rol;
    // Acción que vuelve a colorear al cambiar el tema. Se guarda en un campo para suscribirse y darse de baja
    // con el mismo objeto. Si se escribiera this::colorear dos veces, serían dos objetos distintos y quitar no encontraría el primero.
    private final Runnable alCambiarTema = this::colorear;
    // El texto en español, que se usa como clave para traducirlo cada vez que cambia el idioma.
    // No lleva valor inicial a propósito: el constructor de JLabel llama a setText antes de crear los campos,
    // y un valor inicial borraría la clave que setText acaba de guardar.
    private String clave;
    // Estilo de la caja HTML, para textos largos que se parten en varias líneas. Es null si no se usa HTML.
    private String estiloHtml = null;
    // Si es true, los números van con la fuente de títulos para que se lean claros.
    private boolean numerosClaros = false;
    // Acción que vuelve a traducir al cambiar el idioma. Se guarda en un campo por la misma razón que alCambiarTema.
    private final Runnable alCambiarIdioma = this::traducir;

    // Crea la etiqueta con su texto en español y su rol. Resuelve que nazca ya con el color del tema.
    public Etiqueta(String texto, Rol rol) {
        // super llama al constructor de JLabel con el texto.
        super(texto);
        // this.rol es el campo y rol es el parámetro, que se llaman igual.
        this.rol = rol;
        colorear();
    }

    // Crea una etiqueta con la fuente de títulos, Press Start 2P. Es static, así que se llama con Etiqueta.titulo(...).
    public static Etiqueta titulo(String texto, float tamano, Rol rol) {
        Etiqueta e = new Etiqueta(texto, rol);
        e.setFont(Fuentes.titulo(tamano));
        return e;
    }

    // Crea una etiqueta con la fuente de texto, Pixelify Sans.
    public static Etiqueta texto(String texto, float tamano, Rol rol) {
        Etiqueta e = new Etiqueta(texto, rol);
        e.setFont(Fuentes.texto(tamano));
        return e;
    }

    // Reemplaza el setText de JLabel. Guarda el texto en español como clave y muestra su traducción.
    // Así, al cambiar el idioma, se puede volver a traducir desde el original.
    @Override
    public void setText(String texto) {
        clave = texto;
        // Como es texto normal, ya no se usa la caja HTML.
        estiloHtml = null;
        // super.setText es el de JLabel, el que realmente muestra el texto.
        super.setText(Idioma.t(texto));
    }

    // Para texto largo que se parte en varias líneas. Lo mete en una caja HTML con el estilo dado.
    // Si numeros es true, los números se escriben con la fuente de títulos.
    public void setTextoHtml(String texto, String estilo, boolean numeros) {
        clave = texto;
        estiloHtml = estilo;
        numerosClaros = numeros;
        traducir();
    }

    // Muestra el texto en el idioma actual. Lo llama Idioma al cambiar y también setTextoHtml.
    private void traducir() {
        // Traduce desde la clave en español.
        String texto = Idioma.t(clave);
        // Si es texto HTML, lo envuelve en una caja con el estilo. El signo de pregunta es un if corto:
        // si numerosClaros es true, pasa el texto por conNumeros, y si no, lo deja igual.
        if (estiloHtml != null && texto != null) {
            texto = "<html><div style='" + estiloHtml + "'>" + (numerosClaros ? Fuentes.conNumeros(texto, getFont().getSize2D()) : texto) + "</div></html>";
        }
        // Usa super.setText y no setText, porque setText guardaría el texto ya traducido como clave.
        super.setText(texto);
    }

    // Cambia el rol de la etiqueta y la recolorea.
    public void setRol(Rol rol) {
        this.rol = rol;
        colorear();
    }

    // Pone el color del texto según el rol y el tema actual. Lo llama Tema al cambiar el modo.
    private void colorear() {
        // Este switch devuelve un valor. Según el rol, elige el color que da Tema.
        Color c = switch (rol) {
            case TEXTO -> Tema.texto();
            case SUAVE -> Tema.textoSuave();
            case ACENTO -> Tema.acento();
            case CARMESI -> Tema.carmesi();
            case PELIGRO -> Tema.peligro();
            case ACIERTO -> Tema.acierto();
        };
        setForeground(c);
    }

    // Swing lo llama solo cuando la etiqueta entra a una pantalla. Aquí se suscribe a Tema e Idioma
    // para recolorearse y retraducirse cuando cambien. Se hace aquí y no en el constructor para darse de baja al salir.
    @Override
    public void addNotify() {
        // super.addNotify hace lo normal de JLabel primero.
        super.addNotify();
        Tema.alCambiar(alCambiarTema);
        Idioma.alCambiar(alCambiarIdioma);
        // Se actualiza de una vez, por si el tema o el idioma cambiaron mientras no estaba en pantalla.
        colorear();
        traducir();
    }

    // Swing lo llama solo cuando la etiqueta sale de la pantalla. Se da de baja de Tema e Idioma
    // para que no queden repintando algo que nadie ve ni gastando memoria.
    @Override
    public void removeNotify() {
        Tema.quitar(alCambiarTema);
        Idioma.quitar(alCambiarIdioma);
        super.removeNotify();
    }
}