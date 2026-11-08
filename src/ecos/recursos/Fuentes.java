package ecos.recursos;

// Librería gráfica AWT, Font para las fuentes y GraphicsEnvironment para registrar las fuentes en el sistema.
import java.awt.Font;
import java.awt.GraphicsEnvironment;
// Lectura de archivos, InputStream para leer archivos.
import java.io.InputStream;
// Utilidades de Java, HashMap para diccionarios rápidos y Map para los diccionarios.
import java.util.HashMap;
import java.util.Map;
// Búsqueda de texto con patrones, Matcher para buscar con ese patrón y Pattern para definir el patrón de búsqueda.
import java.util.regex.Matcher;
import java.util.regex.Pattern;

// Carga las dos fuentes del juego, Press Start 2P para títulos y números, y Pixelify Sans para el texto normal.
public class Fuentes {

    // Fuentes ya cargadas (ruta y fuente), para no leer el archivo dos veces.
    private static final Map<String, Font> BASE = new HashMap<>();

    // Fuente para títulos y botones; recibe el tamaño.
    public static Font titulo(float tamano) {
        // Press Start 2P, la letra pixel art de los videojuegos.
        return obtener("fuentes/PressStart2P-Regular.ttf", tamano);
    }

    // Fuente para el texto normal; recibe el tamaño.
    public static Font texto(float tamano) {
        // Pixelify Sans, una letra pixel art más fácil de leer.
        return obtener("fuentes/PixelifySans.ttf", tamano);
    }

    // Una etiqueta HTML o un número.
    private static final Pattern NUMEROS = Pattern.compile("<[^>]*>|\\d+(?:[.,]\\d+)?");

    // En un texto con HTML pone los números con la fuente de títulos, porque con la de texto se confunden (el 5 parece S).
    // Recibe el texto con o sin etiquetas.
    public static String conNumeros(String html) {
        // Se asegura de que la fuente de títulos ya esté registrada. Busca etiquetas y números. Aquí se arma el texto nuevo.
        // Por cada coincidencia.
        titulo(1f);
        Matcher m = NUMEROS.matcher(html);
        StringBuilder sb = new StringBuilder();
        while (m.find()) {
            // Lo que encontró. Las etiquetas quedan igual; los números cambian de fuente.
            String t = m.group();
            m.appendReplacement(sb, Matcher.quoteReplacement(t.startsWith("<") ? t : "<span style=\"font-family:'Press Start 2P'; font-size:12px\">" + t + "</span>"));
        }
        // Copia el resto.
        m.appendTail(sb);
        return sb.toString();
    }

    // Entrega la fuente de ese archivo en el tamaño pedido.
    private static Font obtener(String ruta, float tamano) {
        // Busca si ya se cargó antes y, si todavía no estaba en memoria, abre el archivo de la fuente (se cierra solo al
        // terminar) y crea la fuente a partir del .ttf.
        Font base = BASE.get(ruta);
        if (base == null) {
            try (InputStream in = Recursos.abrir(ruta)) {
                base = Font.createFont(Font.TRUETYPE_FONT, in);
                // La registra para que Java la pueda usar. Si algo falla al leer o crear la fuente. Avisa en la consola cuál falló.
                // Usa una letra normal para que el juego no se caiga.
                GraphicsEnvironment.getLocalGraphicsEnvironment().registerFont(base);
            } catch (Exception e) {
                System.err.println("No se pudo cargar la fuente " + ruta + ": " + e.getMessage());
                base = new Font(Font.MONOSPACED, Font.BOLD, 12);
            }
            // La guarda en el diccionario para la próxima vez.
            BASE.put(ruta, base);
        }
        // Entrega una copia de la fuente con el tamaño pedido.
        return base.deriveFont(tamano);
    }
}