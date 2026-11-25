package ecos.recursos;

// Font es el tipo de una letra, con su tipografía y su tamaño.
import java.awt.Font;
// GraphicsEnvironment es el entorno gráfico del sistema. Sirve para registrar la fuente y que Java la reconozca por su nombre.
import java.awt.GraphicsEnvironment;
// InputStream es de donde se leen los bytes del archivo .ttf que abre Recursos.
import java.io.InputStream;
// HashMap es la clase concreta del diccionario donde se guardan las fuentes ya cargadas.
import java.util.HashMap;
// Map es el tipo de ese diccionario, con clave y valor.
import java.util.Map;
// Matcher recorre un texto buscando las coincidencias de un patrón. Aquí encuentra etiquetas HTML y números.
import java.util.regex.Matcher;
// Pattern es el patrón de búsqueda, o sea la expresión regular.
import java.util.regex.Pattern;

// Entrega las dos fuentes del juego: Press Start 2P para títulos y números, y Pixelify Sans para el texto normal.
// También arregla un detalle de legibilidad: con Pixelify Sans el número 5 se parece a una S.
// Todo es static, así que se usa escribiendo Fuentes.titulo(24f), sin crear un objeto.
public class Fuentes {

    // Caché de fuentes ya cargadas. La clave es la ruta del archivo. Así cada .ttf se lee una sola vez.
    private static final Map<String, Font> BASE = new HashMap<>();

    // Entrega la fuente de títulos y números en el tamaño pedido. La f en 24f indica que es un decimal.
    // Es un atajo para que las pantallas no tengan que saber el nombre del archivo.
    public static Font titulo(float tamano) {
        return obtener("fuentes/PressStart2P-Regular.ttf", tamano);
    }

    // Entrega la fuente del texto normal en el tamaño pedido. Es igual que titulo, pero con otro archivo.
    public static Font texto(float tamano) {
        return obtener("fuentes/PixelifySans.ttf", tamano);
    }

    // Patrón de búsqueda, o expresión regular, con dos opciones separadas por el signo |, que significa o.
    // Primera opción, <[^>]*>, es una etiqueta HTML: el símbolo <, cualquier cantidad de caracteres que no sean >, y el símbolo >.
    // Segunda opción, \d+(?:[.,]\d+)*, es un número: uno o más dígitos, seguidos de cero o más grupos de punto o coma con más dígitos.
    // Así un número como 1,500 o una IP como 192.168.1.1 se encuentra completo y no en pedazos.
    // En Java la barra invertida se escribe doble, por eso aparece \\d.
    private static final Pattern NUMEROS = Pattern.compile("<[^>]*>|\\d+(?:[.,]\\d+)*");

    // Pone los números de un texto HTML con la fuente de títulos, porque con la de texto se confunden.
    // Esta versión usa el tamaño 20, que es el de las listas y los avisos. Llama a la versión de abajo.
    public static String conNumeros(String html) {
        return conNumeros(html, 20f);
    }

    // Hace lo mismo para un texto de cualquier tamaño. Devuelve el HTML con cada número envuelto en un span con la otra fuente.
    // Resuelve que los números se lean bien sin tener que cambiar la fuente de todo el texto.
    public static String conNumeros(String html, float tamanoTexto) {
        // La fuente de títulos es más ancha, por eso los números van al 60% del tamaño. Con 20 dan 12 px.
        int px = Math.round(tamanoTexto * 0.6f);
        // Fuerza que la fuente de títulos se cargue y se registre ahora, para que el HTML la encuentre por su nombre.
        titulo(1f);
        // Crea el buscador que va a recorrer el texto con el patrón.
        Matcher m = NUMEROS.matcher(html);
        // Un texto que se va armando por pedazos. Aquí se arma el resultado.
        StringBuilder sb = new StringBuilder();
        // find busca la siguiente coincidencia y devuelve true si la encontró. El while repite hasta que no haya más.
        while (m.find()) {
            // group es el texto exacto que se encontró, que puede ser una etiqueta o un número.
            String t = m.group();
            // appendReplacement copia a sb lo que había antes de la coincidencia y agrega el reemplazo.
            // quoteReplacement protege los símbolos $ y \, que si no tendrían un significado especial.
            // El signo de pregunta es un if corto. Si empieza con < es una etiqueta y se deja igual.
            // Si no, es un número y se envuelve en un span con la fuente de títulos y el tamaño px.
            m.appendReplacement(sb, Matcher.quoteReplacement(t.startsWith("<") ? t : "<span style=\"font-family:'Press Start 2P'; font-size:" + px + "px\">" + t + "</span>"));
        }
        // Copia a sb lo que quedó después de la última coincidencia.
        m.appendTail(sb);
        return sb.toString();
    }

    // Es el que hace el trabajo de verdad. Lee el archivo .ttf una sola vez, lo registra y devuelve la fuente en el tamaño pedido.
    // Si el archivo falla, usa una letra común para que el juego no se caiga. Es private, solo Fuentes lo usa.
    private static Font obtener(String ruta, float tamano) {
        // Pregunta al caché. Si ya se cargó antes, base tiene la fuente y se salta el if.
        Font base = BASE.get(ruta);
        if (base == null) {
            // Abre el archivo con Recursos. Se cierra solo al terminar el bloque.
            try (InputStream in = Recursos.abrir(ruta)) {
                // Lee el archivo y crea la fuente. TRUETYPE_FONT indica que es un .ttf.
                base = Font.createFont(Font.TRUETYPE_FONT, in);
                // La registra en el sistema para poder usarla por su nombre dentro del HTML.
                GraphicsEnvironment.getLocalGraphicsEnvironment().registerFont(base);
            // Si algo falla, como que el archivo no esté o esté dañado, salta aquí.
            } catch (Exception e) {
                // Avisa en la consola cuál fuente falló.
                System.err.println("No se pudo cargar la fuente " + ruta + ": " + e.getMessage());
                // Usa una letra común del sistema, para que el juego no se caiga. Solo se ve con otra letra.
                base = new Font(Font.MONOSPACED, Font.BOLD, 12);
            }
            // Guarda la fuente en el caché para no leer el archivo otra vez.
            BASE.put(ruta, base);
        }
        // createFont entrega la fuente en tamaño 1. deriveFont devuelve una copia con el tamaño pedido.
        // Por eso se guarda la base una vez y se sacan copias de los tamaños que se necesiten.
        return base.deriveFont(tamano);
    }
}