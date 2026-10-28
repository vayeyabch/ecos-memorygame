package ecos.recursos;

import java.awt.Font; // Clase de Java que representa una tipografía (letra).
import java.awt.GraphicsEnvironment; // Permite registrar la fuente en el sistema para poder usarla.
import java.io.InputStream; // Flujo de lectura con el que se abre el archivo .ttf.
import java.util.HashMap; // Estructura tipo "diccionario" (clave -> valor).
import java.util.Map; // Interfaz general del diccionario.

public class Fuentes {

    private static final Map<String, Font> BASE = new HashMap<>(); // Guarda las fuentes ya cargadas (ruta -> fuente) para no leer el archivo cada vez.

    public static Font titulo(float tamano) { // Pide la fuente de títulos en el tamaño que se quiera.
        return obtener("fuentes/PressStart2P-Regular.ttf", tamano); // Usa el archivo de Press Start 2P (letra pixel para títulos y botones).
    }

    public static Font texto(float tamano) { // Pide la fuente de texto normal en el tamaño que se quiera.
        return obtener("fuentes/PixelifySans.ttf", tamano); // Usa el archivo de Pixelify Sans (letra pixel más legible).
    }

    private static Font obtener(String ruta, float tamano) { // Método interno que hace el trabajo para las dos fuentes.
        Font base = BASE.get(ruta); // Busca si esa fuente ya se había cargado antes.
        if (base == null) { // Si no estaba, hay que cargarla del archivo.
            try (InputStream in = Recursos.abrir(ruta)) { // Abre el .ttf usando la clase Recursos; el try lo cierra solo al terminar.
                base = Font.createFont(Font.TRUETYPE_FONT, in); // Convierte el archivo .ttf en un objeto Font de Java.
                GraphicsEnvironment.getLocalGraphicsEnvironment().registerFont(base); // La registra en el sistema para que Swing pueda usarla.
            } catch (Exception e) { // Si algo falla (archivo faltante o dañado)
                System.err.println("No se pudo cargar la fuente " + ruta + ": " + e.getMessage()); // avisa en la consola cuál fuente falló.
                base = new Font(Font.MONOSPACED, Font.BOLD, 12); // Plan B: usa una fuente normal de Java para que el juego no se caiga.
            }
            BASE.put(ruta, base); // Guarda la fuente en el diccionario para reutilizarla.
        }
        return base.deriveFont(tamano); // Devuelve una copia de la fuente con el tamaño pedido.
    }
}