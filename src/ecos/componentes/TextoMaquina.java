package ecos.componentes;

import ecos.recursos.Fuentes; // Para usar la fuente pixel del juego.
import ecos.recursos.Sonido; // Para el sonido de teclear.
import java.awt.Color; // Clase de Java para representar un color.
import javax.swing.JLabel; // Etiqueta de Swing: la clase de la que hereda esta.
import javax.swing.Timer; // Reloj que ejecuta una acción cada cierto tiempo.

public class TextoMaquina extends JLabel {

    private static final int MS_POR_LETRA = 48; // Tiempo en milisegundos que tarda en aparecer cada letra.

    private final Timer timer; // Reloj que va agregando una letra a la vez.
    private String textoCompleto = ""; // Texto que se quiere escribir completo.
    private int letras = 0; // Cuántas letras se han escrito hasta ahora.
    private Runnable alTerminar; // Qué hacer cuando se escribe la última letra.

    public TextoMaquina(float tamano) { // Recibe el tamaño de la letra.
        setFont(Fuentes.titulo(tamano)); // Usa la fuente de títulos (Press Start 2P).
        setForeground(new Color(0xFFF0E4)); // Letras color crema (la intro siempre es oscura).
        setHorizontalAlignment(CENTER); // Centra el texto.
        timer = new Timer(MS_POR_LETRA, e -> alTic()); // Cada tic escribe una letra más.
    }

    // Empieza a escribir un texto letra por letra; al terminar ejecuta la acción.
    public void escribir(String texto, Runnable alTerminar) { // Recibe el texto y qué hacer al final.
        this.textoCompleto = texto; // Guarda el texto completo.
        this.letras = 0; // Empieza desde cero letras.
        this.alTerminar = alTerminar; // Guarda qué hacer al final.
        setText(""); // Limpia lo que hubiera escrito.
        if (textoCompleto.isEmpty()) { // Si no hay nada que escribir.
            terminar(); // Termina de inmediato.
            return; // Sale del método.
        }
        timer.start(); // Arranca el reloj.
    }

    // Borra todo y deja el texto listo para empezar de nuevo.
    public void reiniciar() { // No recibe nada.
        timer.stop(); // Detiene el reloj.
        textoCompleto = ""; // Olvida el texto.
        letras = 0; // Vuelve a cero letras.
        alTerminar = null; // Olvida qué hacer al final.
        setText(""); // Deja la etiqueta vacía.
    }

    // Detiene la escritura donde esté, sin ejecutar nada al final.
    public void detener() { // No recibe nada.
        timer.stop(); // Detiene el reloj.
        alTerminar = null; // Olvida qué hacer al final.
    }

    private void alTic() { // Se ejecuta en cada tic del reloj.
        if (letras >= textoCompleto.length()) { // Si ya se escribieron todas las letras.
            timer.stop(); // Detiene el reloj.
            terminar(); // Ejecuta lo que sigue.
        } else { // Si faltan letras.
            letras++; // Cuenta una letra más.
            setText(textoCompleto.substring(0, letras)); // Muestra el texto hasta esa letra.
            if (letras % 2 == 1) { // En una de cada dos letras.
                Sonido.efecto("teclear"); // Suena el tecleo.
            }
        }
    }

    private void terminar() { // Ejecuta la acción final una sola vez.
        Runnable r = alTerminar; // Guarda la acción.
        alTerminar = null; // La borra para que no se repita.
        if (r != null) { // Si había una acción.
            r.run(); // La ejecuta.
        }
    }
}
