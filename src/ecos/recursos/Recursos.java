package ecos.recursos;

import java.io.File;
import java.io.FileInputStream;
import java.io.IOException;
import java.io.InputStream;

// Abre los archivos del proyecto (imágenes, sonidos y fuentes) desde la carpeta de recursos.
public class Recursos {

    private static final String CARPETA = "src/recursos/";

    // Abre un archivo; la ruta es relativa a src/recursos.
    public static InputStream abrir(String ruta) throws IOException {
        // Primero lo busca en el disco, que es lo que pasa al correr desde VS Code.
        File archivo = new File(CARPETA + ruta);
        if (archivo.exists()) {
            return new FileInputStream(archivo);
        }
        // Si no, lo busca dentro del .jar; si tampoco está, lanza un error con el nombre del archivo que falta.
        InputStream dentro = Recursos.class.getResourceAsStream("/recursos/" + ruta);
        if (dentro == null) {
            throw new IOException("No se encontro el recurso: " + ruta);
        }
        return dentro;
    }

    public static boolean existe(String ruta) {
        if (new File(CARPETA + ruta).exists()) {
            return true;
        }
        return Recursos.class.getResource("/recursos/" + ruta) != null;
    }
}
