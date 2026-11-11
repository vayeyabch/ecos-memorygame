package ecos.componentes;

// Clases del propio proyecto que se usan aquí.
import ecos.app.Idioma;
import ecos.app.Tema;
import ecos.recursos.Fuentes;
// Librería gráfica AWT, Color para los colores.
import java.awt.Color;
// Librería Swing, JLabel para los textos.
import javax.swing.JLabel;

// Texto del juego, usa una de las dos fuentes pixel y un color del tema según su rol.
public class Etiqueta extends JLabel {

    // Qué color del tema usa la etiqueta (FIJO = un color propio que no cambia).
    public enum Rol { TEXTO, SUAVE, ACENTO, CARMESI, PELIGRO, ACIERTO, FIJO }

    // Rol actual de la etiqueta, color que se usa solo cuando el rol es FIJO y acción que vuelve a colorear cuando cambia el
    // tema.
    private Rol rol;
    private Color colorFijo = Color.BLACK;
    private final Runnable alCambiarTema = this::colorear;
    // Texto en español (la clave) y acción que vuelve a traducirlo cuando cambia el idioma. Si el texto va dentro de HTML,
    // el estilo de su caja y si sus números llevan la fuente clara. La clave no lleva valor inicial porque JLabel pone el
    // texto antes de que se creen los campos.
    private String clave;
    private String estiloHtml = null;
    private boolean numerosClaros = false;
    private final Runnable alCambiarIdioma = this::traducir;

    // Recibe el texto y el rol (de qué color es).
    public Etiqueta(String texto, Rol rol) {
        // Le pasa el texto al JLabel. Guarda el rol. Pone el color inicial.
        super(texto);
        this.rol = rol;
        colorear();
    }

    // Etiqueta con la fuente de títulos (Press Start 2P).
    // Recibe el texto, el tamaño y el rol.
    public static Etiqueta titulo(String texto, float tamano, Rol rol) {
        // Crea la etiqueta. Le pone la fuente de títulos.
        Etiqueta e = new Etiqueta(texto, rol);
        e.setFont(Fuentes.titulo(tamano));
        return e;
    }

    // Etiqueta con la fuente de texto normal (Pixelify Sans).
    // Recibe el texto, el tamaño y el rol.
    public static Etiqueta texto(String texto, float tamano, Rol rol) {
        // Crea la etiqueta. Le pone la fuente de texto.
        Etiqueta e = new Etiqueta(texto, rol);
        e.setFont(Fuentes.texto(tamano));
        return e;
    }

    // Cambia el texto, que se muestra en el idioma actual.
    // Recibe el texto en español.
    @Override
    public void setText(String texto) {
        // Guarda el texto original como clave. Es un texto simple, sin HTML propio. Pone el texto traducido.
        clave = texto;
        estiloHtml = null;
        super.setText(Idioma.t(texto));
    }

    // Cambia el texto cuando es largo y se tiene que partir en varias líneas, lo mete en una caja HTML con ese estilo.
    // Recibe el texto en español, el estilo de la caja (por ejemplo, el ancho) y si los números van con la fuente clara.
    public void setTextoHtml(String texto, String estilo, boolean numeros) {
        // Guarda el texto original como clave junto con cómo se arma la caja. Pone el texto traducido.
        clave = texto;
        estiloHtml = estilo;
        numerosClaros = numeros;
        traducir();
    }

    // Vuelve a poner el texto en el idioma actual.
    private void traducir() {
        // Si el texto es simple, solo lo traduce. Si va en una caja HTML, traduce el texto y arma la caja de nuevo.
        String texto = Idioma.t(clave);
        if (estiloHtml != null && texto != null) {
            texto = "<html><div style='" + estiloHtml + "'>" + (numerosClaros ? Fuentes.conNumeros(texto) : texto) + "</div></html>";
        }
        super.setText(texto);
    }

    // Cambia el rol (el color) de la etiqueta.
    public void setRol(Rol rol) {
        // Guarda el rol nuevo. Aplica el color.
        this.rol = rol;
        colorear();
    }

    // Pone un color propio que no depende del tema.
    public void setColorFijo(Color color) {
        // Guarda el color. Cambia el rol a FIJO. Aplica el color.
        this.colorFijo = color;
        this.rol = Rol.FIJO;
        colorear();
    }

    // Pone el color que corresponde al rol y al tema.
    private void colorear() {
        // Elige el color según el rol, con letras normales, apagadas o de resalte.
        Color c = switch (rol) {
            case TEXTO -> Tema.texto();
            case SUAVE -> Tema.textoSuave();
            case ACENTO -> Tema.acento();
            case CARMESI -> Tema.carmesi();
            case PELIGRO -> Tema.peligro();
            case ACIERTO -> Tema.acierto();
            case FIJO -> colorFijo;
        };
        // Aplica el color a las letras.
        setForeground(c);
    }

    // Swing lo llama cuando la etiqueta entra a una ventana.
    @Override
    public void addNotify() {
        // Deja que Swing haga lo suyo primero. Empieza a escuchar los cambios de tema y de idioma. Se pone al día con el tema
        // y el idioma actuales.
        super.addNotify();
        Tema.alCambiar(alCambiarTema);
        Idioma.alCambiar(alCambiarIdioma);
        colorear();
        traducir();
    }

    // Swing lo llama cuando la etiqueta sale de la ventana.
    @Override
    public void removeNotify() {
        // Deja de escuchar los cambios de tema y de idioma. Deja que Swing haga lo suyo.
        Tema.quitar(alCambiarTema);
        Idioma.quitar(alCambiarIdioma);
        super.removeNotify();
    }
}
