package ecos.recursos;

// File representa un archivo o ruta del disco. Aquí sirve para preguntar si el archivo existe.
import java.io.File;
// FileInputStream abre un archivo del disco para poder leer sus bytes.
import java.io.FileInputStream;
// IOException es el error que Java lanza cuando falla abrir o leer un archivo.
import java.io.IOException;
// InputStream es el tipo general de algo de donde se leen bytes. Es lo que devuelve abrir, venga del disco o del jar.
import java.io.InputStream;

// Es la puerta para abrir los archivos del proyecto (imágenes, sonidos y fuentes).
// Sprites, Sonido y Fuentes la usan en vez de abrir archivos cada una por su cuenta.
public class Recursos {

    // Carpeta donde están los archivos cuando se corre desde el disco. Es final porque nunca cambia.
    private static final String CARPETA = "src/recursos/";

    // Abre un archivo y devuelve algo de donde leerlo. Resuelve que el juego encuentre sus archivos
    // tanto corriendo desde VS Code como empaquetado en un jar. Si no lo encuentra, lanza un error.
    // La ruta es relativa a src/recursos. throws IOException avisa que puede fallar y quien la llame debe manejarlo.
    public static InputStream abrir(String ruta) throws IOException {
        // Primero lo busca en el disco, que es lo que pasa al correr desde VS Code.
        // Crear el File solo guarda la ruta, todavía no abre nada.
        File archivo = new File(CARPETA + ruta);
        if (archivo.exists()) {
            return new FileInputStream(archivo);
        }
        // Si no está en el disco, lo busca dentro del jar. Si no lo encuentra, dentro queda en null.
        InputStream dentro = Recursos.class.getResourceAsStream("/recursos/" + ruta);
        // Si no estaba en ningún lado, lanza un error con el nombre del archivo que falta.
        if (dentro == null) {
            throw new IOException("No se encontro el recurso: " + ruta);
        }
        return dentro;
    }

    // Dice si un archivo existe, en el disco o dentro del jar, sin abrirlo y sin lanzar error.
    // Sirve para revisar antes de intentar abrir, por ejemplo si hay una foto.
    public static boolean existe(String ruta) {
        // Primero revisa el disco.
        if (new File(CARPETA + ruta).exists()) {
            return true;
        }
        // Si no está en el disco, revisa el jar. getResource devuelve null si no lo encuentra,
        // y el != null lo convierte en true si lo encontró y false si no.
        return Recursos.class.getResource("/recursos/" + ruta) != null;
    }
}