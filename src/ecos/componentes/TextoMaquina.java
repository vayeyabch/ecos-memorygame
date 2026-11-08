package ecos.componentes;

// Clases del propio proyecto que se usan aquí.
import ecos.recursos.Fuentes;
import ecos.recursos.Sonido;
// Librería gráfica AWT, Color para los colores.
import java.awt.Color;
// Librería Swing, JLabel para los textos y Timer para los relojes.
import javax.swing.JLabel;
import javax.swing.Timer;

// Texto que se escribe letra por letra, como una máquina de escribir.
public class TextoMaquina extends JLabel {

    // Tiempo en milisegundos que tarda en aparecer cada letra.
    private static final int MS_POR_LETRA = 48;

    // Reloj que va agregando una letra a la vez, texto completo que se quiere escribir, cuántas letras van y qué hacer al
    // escribir la última.
    private final Timer timer;
    private String textoCompleto = "";
    private int letras = 0;
    private Runnable alTerminar;

    // Recibe el tamaño de la letra.
    public TextoMaquina(float tamano) {
        // Usa la fuente de títulos (Press Start 2P). Letras color crema (la intro siempre es oscura). Centra el texto. Cada
        // tic escribe una letra más.
        setFont(Fuentes.titulo(tamano));
        setForeground(new Color(0xFFF0E4));
        setHorizontalAlignment(CENTER);
        timer = new Timer(MS_POR_LETRA, e -> alTic());
    }

    // Empieza a escribir un texto letra por letra; al terminar ejecuta la acción.
    // Recibe el texto y qué hacer al final.
    public void escribir(String texto, Runnable alTerminar) {
        // Guarda el texto completo. Empieza desde cero letras. Guarda qué hacer al final. Limpia lo que hubiera escrito.
        this.textoCompleto = texto;
        this.letras = 0;
        this.alTerminar = alTerminar;
        setText("");
        // Si no hay nada que escribir. Termina de inmediato. Sale del método.
        if (textoCompleto.isEmpty()) {
            terminar();
            return;
        }
        // Arranca el reloj.
        timer.start();
    }

    // Borra todo y deja el texto listo para empezar de nuevo.
    // No recibe nada.
    public void reiniciar() {
        // Detiene el reloj. Olvida el texto. Vuelve a cero letras. Olvida qué hacer al final.
        timer.stop();
        textoCompleto = "";
        letras = 0;
        alTerminar = null;
        // Deja la etiqueta vacía.
        setText("");
    }

    // Detiene la escritura donde esté, sin ejecutar nada al final.
    // No recibe nada.
    public void detener() {
        // Detiene el reloj. Olvida qué hacer al final.
        timer.stop();
        alTerminar = null;
    }

    // Se ejecuta en cada tic del reloj.
    private void alTic() {
        // Si ya se escribieron todas las letras. Detiene el reloj. Ejecuta lo que sigue. Si faltan letras.
        if (letras >= textoCompleto.length()) {
            timer.stop();
            terminar();
        } else {
            // Cuenta una letra más. Muestra el texto hasta esa letra. En una de cada dos letras. Suena el tecleo.
            letras++;
            setText(textoCompleto.substring(0, letras));
            if (letras % 2 == 1) {
                Sonido.efecto("teclear");
            }
        }
    }

    // Ejecuta la acción final una sola vez.
    private void terminar() {
        // Guarda la acción y la borra para que no se repita, si es que había una.
        Runnable r = alTerminar;
        alTerminar = null;
        if (r != null) {
            r.run();
        }
    }
}
