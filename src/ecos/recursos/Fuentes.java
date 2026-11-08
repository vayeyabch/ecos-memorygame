package ecos.recursos;

import java.awt.Font; // Clase de Java que representa una letra (tipo, estilo y tamaño).
import java.awt.GraphicsEnvironment; // Da acceso al sistema gráfico, donde se registran las fuentes.
import java.io.InputStream; // Flujo para leer los bytes de un archivo.
import java.util.HashMap; // Diccionario concreto (clave -> valor).
import java.util.Map; // Tipo general del diccionario.
import java.util.regex.Matcher; // Para recorrer los números de un texto.
import java.util.regex.Pattern; // Para buscar los números de un texto.

public class Fuentes {

    private static final Map<String, Font> BASE = new HashMap<>(); // Fuentes ya cargadas (ruta -> fuente), para no leer el archivo dos veces.

    public static Font titulo(float tamano) { // Fuente para títulos y botones; recibe el tamaño.
        return obtener("fuentes/PressStart2P-Regular.ttf", tamano); // Press Start 2P: la letra pixel art de los videojuegos.
    }

    public static Font texto(float tamano) { // Fuente para el texto normal; recibe el tamaño.
        return obtener("fuentes/PixelifySans.ttf", tamano); // Pixelify Sans: una letra pixel art más fácil de leer.
    }

    private static final Pattern NUMEROS = Pattern.compile("<[^>]*>|\\d+(?:[.,]\\d+)?"); // Una etiqueta HTML o un número.

    // En un texto con HTML pone los números con la fuente de títulos, porque con la de texto se confunden (el 5 parece S).
    public static String conNumeros(String html) { // Recibe el texto con o sin etiquetas.
        titulo(1f); // Se asegura de que la fuente de títulos ya esté registrada.
        Matcher m = NUMEROS.matcher(html); // Busca etiquetas y números.
        StringBuilder sb = new StringBuilder(); // Aquí se arma el texto nuevo.
        while (m.find()) { // Por cada coincidencia.
            String t = m.group(); // Lo que encontró.
            m.appendReplacement(sb, Matcher.quoteReplacement(t.startsWith("<") ? t : "<span style=\"font-family:'Press Start 2P'; font-size:12px\">" + t + "</span>")); // Las etiquetas quedan igual; los números cambian de fuente.
        }
        m.appendTail(sb); // Copia el resto.
        return sb.toString();
    }

    private static Font obtener(String ruta, float tamano) { // Entrega la fuente de ese archivo en el tamaño pedido.
        Font base = BASE.get(ruta); // Busca si ya se cargó antes.
        if (base == null) { // Si todavía no estaba en memoria.
            try (InputStream in = Recursos.abrir(ruta)) { // Abre el archivo de la fuente (se cierra solo al terminar).
                base = Font.createFont(Font.TRUETYPE_FONT, in); // Crea la fuente a partir del archivo .ttf.
                GraphicsEnvironment.getLocalGraphicsEnvironment().registerFont(base); // La registra para que Java la pueda usar.
            } catch (Exception e) { // Si algo falla al leer o crear la fuente.
                System.err.println("No se pudo cargar la fuente " + ruta + ": " + e.getMessage()); // Avisa en la consola cuál falló.
                base = new Font(Font.MONOSPACED, Font.BOLD, 12); // Usa una letra normal para que el juego no se caiga.
            }
            BASE.put(ruta, base); // La guarda en el diccionario para la próxima vez.
        }
        return base.deriveFont(tamano); // Entrega una copia de la fuente con el tamaño pedido.
    }
}