package ecos.app;

// ArrayList es la clase concreta de la lista. Se usa para la lista de plantillas.
import java.util.ArrayList;
// HashMap es la clase concreta del diccionario donde se guardan los textos fijos.
import java.util.HashMap;
// List es el tipo de la lista de plantillas, o sea los textos que llevan datos.
import java.util.List;
// Map es el tipo del diccionario, con una clave en español y su valor en inglés.
import java.util.Map;
// Matcher recorre un texto buscando coincidencias con un patrón y entrega los pedazos encontrados.
import java.util.regex.Matcher;
// Pattern es el patrón de búsqueda, o sea la expresión regular.
import java.util.regex.Pattern;

// Es el diccionario de español a inglés del juego. Idioma decide cuándo traducir y Textos sabe cómo.
// Hay dos tipos de texto. Los fijos van directo a una tabla. Los que llevan datos, como "Hola, %s!", van a una lista
// de plantillas, donde el %s marca el lugar del dato.
// Si un texto no está en ningún lado, se queda en español, así una traducción que falte no rompe nada.
public class Textos {

    // Tabla de textos fijos. La clave es el texto en español y el valor es el texto en inglés.
    private static final Map<String, String> TABLA = new HashMap<>();
    // Lista de textos con datos. Cada uno guarda su patrón de búsqueda y su versión en inglés.
    private static final List<Plantilla> PLANTILLAS = new ArrayList<>();
    // Patrón que encuentra el texto que está entre etiquetas HTML.
    // Se lee así: el símbolo >, un grupo con uno o más caracteres que no sean < ni >, y el símbolo <.
    // Los paréntesis forman el grupo, que es lo que se saca después con group(1).
    private static final Pattern ENTRE_ETIQUETAS = Pattern.compile(">([^<>]+)<");

    // Un record es una clase corta que solo guarda datos. Java crea solo el constructor y los métodos patron() e ingles().
    // Cada plantilla guarda el patrón que reconoce el texto en español y cómo queda en inglés, con sus %s.
    private record Plantilla(Pattern patron, String ingles) { }

    // Devuelve la traducción al inglés de un texto en español. Lo llama Idioma.t cuando el idioma es inglés.
    // Prueba en orden: texto completo, plantillas con datos, HTML, y si nada coincide devuelve el español.
    public static String ingles(String es) {
        // Primero busca el texto completo en la tabla. Si está, lo devuelve y termina.
        String directo = TABLA.get(es);
        if (directo != null) {
            return directo;
        }
        // Si no está, prueba las plantillas una por una.
        for (Plantilla pl : PLANTILLAS) {
            // Prepara la búsqueda de ese patrón dentro del texto.
            Matcher m = pl.patron().matcher(es);
            // matches pregunta si el texto completo encaja con el patrón.
            if (m.matches()) {
                // groupCount es cuántos datos hay en el texto, o sea cuántos %s tenía.
                Object[] datos = new Object[m.groupCount()];
                for (int i = 0; i < datos.length; i++) {
                    // group(i + 1) es el dato encontrado, el grupo 1 es el primero.
                    // Se traduce también con ingles, por si es una palabra conocida como Invitado, que pasa a Guest.
                    datos[i] = ingles(m.group(i + 1));
                }
                // format reemplaza cada %s del texto en inglés por su dato, en orden.
                return String.format(pl.ingles(), datos);
            }
        }
        // Si es un texto HTML, traduce solo lo que está entre etiquetas. Si no, devuelve el español sin tocar.
        // El signo de pregunta es un if corto.
        return es.startsWith("<html>") ? traducirHtml(es) : es;
    }

    // Traduce solo las partes de un texto HTML que están entre etiquetas, y deja las etiquetas igual.
    // Resuelve que los textos con <html>, <b> o <br> también se traduzcan sin romper su formato.
    private static String traducirHtml(String html) {
        // Prepara la búsqueda de los textos entre etiquetas.
        Matcher m = ENTRE_ETIQUETAS.matcher(html);
        // Un texto que se va armando por pedazos. Aquí se arma el resultado.
        StringBuilder sb = new StringBuilder();
        // find busca la siguiente coincidencia y devuelve true si la encontró. El while repite hasta que no haya más.
        while (m.find()) {
            // group(1) es el texto de adentro, sin los símbolos > y <.
            String parte = m.group(1);
            // strip quita los espacios de los extremos, y se traduce ese texto limpio.
            String traducida = ingles(parte.strip());
            // Reemplaza solo la parte limpia, para conservar los espacios de los extremos.
            String nueva = parte.replace(parte.strip(), traducida);
            // Copia lo que había antes de la coincidencia y agrega el texto nuevo entre sus símbolos.
            // quoteReplacement protege los símbolos $ y \, que si no tendrían un significado especial.
            m.appendReplacement(sb, Matcher.quoteReplacement(">" + nueva + "<"));
        }
        // Copia lo que quedó después de la última coincidencia.
        m.appendTail(sb);
        return sb.toString();
    }

    // Agrega un par español e inglés. Es el método que se usa en cada línea del bloque static de abajo.
    // Si el texto no tiene %s, va directo a la tabla. Si lo tiene, arma un patrón que reconoce el texto con cualquier dato.
    private static void p(String es, String en) {
        // Sin %s es un texto fijo. Lo guarda en la tabla y termina con return.
        if (!es.contains("%s")) {
            TABLA.put(es, en);
            return;
        }
        // Con %s, va armando el patrón por pedazos.
        StringBuilder regex = new StringBuilder();
        // Parte el texto por cada %s. El -1 hace que no se pierdan los pedazos vacíos del final.
        // Ejemplo: "Hola, %s!" queda en dos partes, "Hola, " y "!".
        String[] partes = es.split("%s", -1);
        for (int i = 0; i < partes.length; i++) {
            // quote hace que ese pedazo se busque tal cual, aunque tenga símbolos especiales como ? o .
            regex.append(Pattern.quote(partes[i]));
            // Entre un pedazo y el siguiente va el hueco del dato.
            // (.+?) es un grupo que captura cualquier carácter, al menos uno, y el ? hace que tome lo mínimo posible.
            if (i < partes.length - 1) {
                regex.append("(.+?)");
            }
        }
        // DOTALL hace que el punto también capture los saltos de línea, para textos largos de varias líneas.
        // Guarda la plantilla con su patrón y su versión en inglés.
        PLANTILLAS.add(new Plantilla(Pattern.compile(regex.toString(), Pattern.DOTALL), en));
    }

    // Todas las traducciones, en orden de pantalla.
    // Un bloque static se ejecuta una sola vez, cuando la clase se usa por primera vez. Aquí llena la tabla y las plantillas.
    static {
        // Nombres y palabras sueltas que se repiten en varias pantallas.
        p("Jugador 1", "Player 1");
        p("Invitado", "Guest");
        // ... aquí siguen todas tus líneas p(...) tal cual las tienes, con sus comentarios de sección.

        // Ejemplo de texto con un dato: el %s es el lugar del nombre.
        p("Hola, %s!", "Hi, %s!");
        // Ejemplo con dos datos: se reemplazan en orden.
        p("Parejas %s/%s", "Pairs %s/%s");
        // ... y así hasta el final del bloque.
    }
}