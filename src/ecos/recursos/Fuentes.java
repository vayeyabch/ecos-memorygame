package ecos.recursos;

import java.awt.Font;
import java.awt.GraphicsEnvironment;
import java.io.InputStream;
import java.util.HashMap;
import java.util.Map;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

// Carga las dos fuentes del juego: Press Start 2P para títulos y números, y Pixelify Sans para el texto normal.
public class Fuentes {

    // Fuentes ya cargadas, para no leer el archivo dos veces.
    private static final Map<String, Font> BASE = new HashMap<>();

    public static Font titulo(float tamano) {
        return obtener("fuentes/PressStart2P-Regular.ttf", tamano);
    }

    public static Font texto(float tamano) {
        return obtener("fuentes/PixelifySans.ttf", tamano);
    }

    // Una etiqueta HTML o un número, con sus puntos o comas para que una IP quede completa.
    private static final Pattern NUMEROS = Pattern.compile("<[^>]*>|\\d+(?:[.,]\\d+)*");

    // Pone los números de un texto HTML con la fuente de títulos, porque con la de texto se confunden (el 5 parece S).
    // Esta versión es para texto de tamaño 20, el de las listas y los avisos.
    public static String conNumeros(String html) {
        return conNumeros(html, 20f);
    }

    // Igual, para un texto de otro tamaño.
    public static String conNumeros(String html, float tamanoTexto) {
        // La fuente de títulos es más ancha, por eso los números van un poco más pequeños.
        int px = Math.round(tamanoTexto * 0.6f);
        titulo(1f);
        Matcher m = NUMEROS.matcher(html);
        StringBuilder sb = new StringBuilder();
        while (m.find()) {
            String t = m.group();
            m.appendReplacement(sb, Matcher.quoteReplacement(t.startsWith("<") ? t : "<span style=\"font-family:'Press Start 2P'; font-size:" + px + "px\">" + t + "</span>"));
        }
        m.appendTail(sb);
        return sb.toString();
    }

    private static Font obtener(String ruta, float tamano) {
        // Si ya se cargó antes la reutiliza; si no, lee el .ttf.
        Font base = BASE.get(ruta);
        if (base == null) {
            try (InputStream in = Recursos.abrir(ruta)) {
                base = Font.createFont(Font.TRUETYPE_FONT, in);
                // Si falla se avisa en la consola y se usa una letra normal para que el juego no se caiga.
                GraphicsEnvironment.getLocalGraphicsEnvironment().registerFont(base);
            } catch (Exception e) {
                System.err.println("No se pudo cargar la fuente " + ruta + ": " + e.getMessage());
                base = new Font(Font.MONOSPACED, Font.BOLD, 12);
            }
            BASE.put(ruta, base);
        }
        return base.deriveFont(tamano);
    }
}
