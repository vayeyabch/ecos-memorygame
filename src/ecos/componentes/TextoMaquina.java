package ecos.componentes;

import java.awt.Color;
import java.awt.Font;
import java.io.InputStream;
import javax.swing.Timer;

import javax.swing.JLabel;

public class TextoMaquina extends JLabel {
    private static final String rutaFuente = "src/ecos/recursos/fuentes/PressStart2P-Regular.ttf";
    private static final int ms_por_letra = 48; // Tiempo en milisegundos por letra

    private final Timer timer;
    private String textoCompleto = "";
    private int letras  = 0;
    private Runnable alTerminar;

    public TextoMaquina() {
        setFont(cargarFuente(8f));
        setForeground(new Color(0xFFF0E4));
        setHorizontalAlignment(CENTER);
        timer = new Timer(ms_por_letra, e -> alTic());

    }

    public void escribir(String texto, Runnable alTerminar) {
        this.textoCompleto = texto;
        this.letras = 0;
        this.alTerminar = alTerminar;
        setText("");
            if (textoCompleto.isEmpty()) {
                terminar();
                return;
            }
            avanzar();
            timer.start();

    }

    public void reiniciar() {
        timer.stop();
        textoCompleto = "";
        letras = 0;
        alTerminar = null;
        setText("");
    }
    
    public void detener() {
        timer.stop();
        alTerminar = null;
    }

    private void alTic() {
        if (letras >= textoCompleto.length()) {
            timer.stop();
            terminar();
        } else {
            avanzar();
        }
    }

    private void avanzar() {
        letras++;
        setText(textoCompleto.substring(0, letras));
    }

    private void terminar() {
        Runnable r = alTerminar;
        alTerminar = null;
        if (r != null) {
            r.run();
        }
    }

    private static Font cargarFuente(float tamano) {
        try (InputStream is = TextoMaquina.class.getResourceAsStream(rutaFuente)) {
            if (is != null) {
                return Font.createFont(Font.TRUETYPE_FONT, is).deriveFont(tamano);
            }
        } catch (Exception e) {
            System.err.println("Error al cargar la fuente: " + e.getMessage());
        }
        return new Font(Font.DIALOG, Font.PLAIN, 12);
    }
            

    

    
}
