package ecos.componentes;

import ecos.app.Idioma;
import ecos.recursos.Fuentes;
import ecos.recursos.Sonido;
import java.awt.Color;
import javax.swing.JLabel;
import javax.swing.Timer;

// Texto que se escribe letra por letra, como una máquina de escribir.
public class TextoMaquina extends JLabel {

    // Milisegundos que tarda en aparecer cada letra.
    private static final int MS_POR_LETRA = 48;

    // Reloj que agrega una letra a la vez, texto completo, cuántas letras van y qué hacer al escribir la última.
    private final Timer timer;
    private String textoCompleto = "";
    private int letras = 0;
    private Runnable alTerminar;

    public TextoMaquina(float tamano) {
        // Letra de títulos color crema, porque la intro siempre es oscura.
        setFont(Fuentes.titulo(tamano));
        setForeground(new Color(0xFFF0E4));
        setHorizontalAlignment(CENTER);
        timer = new Timer(MS_POR_LETRA, e -> alTic());
    }

    // Empieza a escribir un texto; al terminar ejecuta la acción.
    public void escribir(String texto, Runnable alTerminar) {
        this.textoCompleto = Idioma.t(texto);
        this.letras = 0;
        this.alTerminar = alTerminar;
        setText("");
        if (textoCompleto.isEmpty()) {
            terminar();
            return;
        }
        timer.start();
    }

    // Borra todo y deja el texto listo para empezar de nuevo.
    public void reiniciar() {
        timer.stop();
        textoCompleto = "";
        letras = 0;
        alTerminar = null;
        setText("");
    }

    // Detiene la escritura donde esté, sin ejecutar la acción final.
    public void detener() {
        timer.stop();
        alTerminar = null;
    }

    private void alTic() {
        if (letras >= textoCompleto.length()) {
            timer.stop();
            terminar();
        } else {
            letras++;
            setText(textoCompleto.substring(0, letras));
            if (letras % 2 == 1) {
                Sonido.efecto("teclear");
            }
        }
    }

    // Ejecuta la acción final una sola vez.
    private void terminar() {
        Runnable r = alTerminar;
        alTerminar = null;
        if (r != null) {
            r.run();
        }
    }
}
