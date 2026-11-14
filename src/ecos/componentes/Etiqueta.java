package ecos.componentes;

import ecos.app.Idioma;
import ecos.app.Tema;
import ecos.recursos.Fuentes;
import java.awt.Color;
import javax.swing.JLabel;

// Texto del juego con una de las dos fuentes pixel y un color del tema según su rol.
public class Etiqueta extends JLabel {

    public enum Rol { TEXTO, SUAVE, ACENTO, CARMESI, PELIGRO, ACIERTO }

    // Rol actual y acción que vuelve a colorear al cambiar el tema.
    private Rol rol;
    private final Runnable alCambiarTema = this::colorear;
    // Texto en español para traducirlo al cambiar el idioma, estilo de la caja HTML y si los números llevan la fuente
    // clara. Sin valor inicial porque JLabel pone el texto antes de crear los campos.
    private String clave;
    private String estiloHtml = null;
    private boolean numerosClaros = false;
    private final Runnable alCambiarIdioma = this::traducir;

    public Etiqueta(String texto, Rol rol) {
        super(texto);
        this.rol = rol;
        colorear();
    }

    // Etiqueta con la fuente de títulos (Press Start 2P).
    public static Etiqueta titulo(String texto, float tamano, Rol rol) {
        Etiqueta e = new Etiqueta(texto, rol);
        e.setFont(Fuentes.titulo(tamano));
        return e;
    }

    // Etiqueta con la fuente de texto (Pixelify Sans).
    public static Etiqueta texto(String texto, float tamano, Rol rol) {
        Etiqueta e = new Etiqueta(texto, rol);
        e.setFont(Fuentes.texto(tamano));
        return e;
    }

    @Override
    public void setText(String texto) {
        clave = texto;
        estiloHtml = null;
        super.setText(Idioma.t(texto));
    }

    // Para texto largo que se parte en varias líneas, lo mete en una caja HTML con ese estilo.
    public void setTextoHtml(String texto, String estilo, boolean numeros) {
        clave = texto;
        estiloHtml = estilo;
        numerosClaros = numeros;
        traducir();
    }

    private void traducir() {
        String texto = Idioma.t(clave);
        if (estiloHtml != null && texto != null) {
            texto = "<html><div style='" + estiloHtml + "'>" + (numerosClaros ? Fuentes.conNumeros(texto, getFont().getSize2D()) : texto) + "</div></html>";
        }
        super.setText(texto);
    }

    public void setRol(Rol rol) {
        this.rol = rol;
        colorear();
    }

    private void colorear() {
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

    @Override
    public void addNotify() {
        super.addNotify();
        Tema.alCambiar(alCambiarTema);
        Idioma.alCambiar(alCambiarIdioma);
        colorear();
        traducir();
    }

    @Override
    public void removeNotify() {
        Tema.quitar(alCambiarTema);
        Idioma.quitar(alCambiarIdioma);
        super.removeNotify();
    }
}
