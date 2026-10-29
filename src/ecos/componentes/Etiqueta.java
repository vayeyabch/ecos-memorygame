package ecos.componentes;

import ecos.app.Tema; // Para usar los colores del tema actual.
import ecos.recursos.Fuentes; // Para usar las fuentes pixel del juego.
import java.awt.Color; // Clase de Java para representar un color.
import javax.swing.JLabel; // Etiqueta de Swing: la clase de la que hereda esta.

public class Etiqueta extends JLabel {

    public enum Rol { TEXTO, SUAVE, ACENTO, CARMESI, PELIGRO, ACIERTO, FIJO } // Qué color del tema usa la etiqueta (FIJO = un color propio que no cambia).

    private Rol rol; // Rol actual de la etiqueta.
    private Color colorFijo = Color.BLACK; // Color que se usa solo cuando el rol es FIJO.
    private final Runnable alCambiarTema = this::colorear; // Acción que vuelve a colorear cuando cambia el tema.

    public Etiqueta(String texto, Rol rol) { // Recibe el texto y el rol (de qué color es).
        super(texto); // Le pasa el texto al JLabel.
        this.rol = rol; // Guarda el rol.
        colorear(); // Pone el color inicial.
    }

    // Etiqueta con la fuente de títulos (Press Start 2P).
    public static Etiqueta titulo(String texto, float tamano, Rol rol) { // Recibe el texto, el tamaño y el rol.
        Etiqueta e = new Etiqueta(texto, rol); // Crea la etiqueta.
        e.setFont(Fuentes.titulo(tamano)); // Le pone la fuente de títulos.
        return e; // La entrega.
    }

    // Etiqueta con la fuente de texto normal (Pixelify Sans).
    public static Etiqueta texto(String texto, float tamano, Rol rol) { // Recibe el texto, el tamaño y el rol.
        Etiqueta e = new Etiqueta(texto, rol); // Crea la etiqueta.
        e.setFont(Fuentes.texto(tamano)); // Le pone la fuente de texto.
        return e; // La entrega.
    }

    public void setRol(Rol rol) { // Cambia el rol (el color) de la etiqueta.
        this.rol = rol; // Guarda el rol nuevo.
        colorear(); // Aplica el color.
    }

    public void setColorFijo(Color color) { // Pone un color propio que no depende del tema.
        this.colorFijo = color; // Guarda el color.
        this.rol = Rol.FIJO; // Cambia el rol a FIJO.
        colorear(); // Aplica el color.
    }

    private void colorear() { // Pone el color que corresponde al rol y al tema.
        Color c = switch (rol) { // Elige el color según el rol.
            case TEXTO -> Tema.texto(); // Letras normales.
            case SUAVE -> Tema.textoSuave(); // Letras apagadas.
            case ACENTO -> Tema.acento(); // Letras de resalte.
            case CARMESI -> Tema.carmesi(); // Rojo carmesí.
            case PELIGRO -> Tema.peligro(); // Rojo de error.
            case ACIERTO -> Tema.acierto(); // Verde de acierto.
            case FIJO -> colorFijo; // Color propio.
        };
        setForeground(c); // Aplica el color a las letras.
    }

    @Override // Indica que se reemplaza un método de JLabel.
    public void addNotify() { // Swing lo llama cuando la etiqueta entra a una ventana.
        super.addNotify(); // Deja que Swing haga lo suyo primero.
        Tema.alCambiar(alCambiarTema); // Empieza a escuchar los cambios de tema.
        colorear(); // Se pone al día con el tema actual.
    }

    @Override // Indica que se reemplaza un método de JLabel.
    public void removeNotify() { // Swing lo llama cuando la etiqueta sale de la ventana.
        Tema.quitar(alCambiarTema); // Deja de escuchar los cambios de tema.
        super.removeNotify(); // Deja que Swing haga lo suyo.
    }
}
