package ecos.recursos;

import java.io.File;
import java.io.FileInputStream;
import java.io.IOException;
import java.io.InputStream;

/**
 * Ayudante para abrir archivos de la carpeta src/recursos.
 * Primero busca el archivo en disco (cuando se corre desde VS Code)
 * y si no lo encuentra lo busca dentro del proyecto compilado (.jar).
 */
public class Recursos {

    private static final String CARPETA = "src/recursos/";

    /** Abre un archivo. La ruta es relativa a src/recursos, por ejemplo "audio/musica/menu.wav". */
    public static InputStream abrir(String ruta) throws IOException {
        File archivo = new File(CARPETA + ruta);
        if (archivo.exists()) {
            return new FileInputStream(archivo);
        }
        InputStream dentro = Recursos.class.getResourceAsStream("/recursos/" + ruta);
        if (dentro == null) {
            throw new IOException("No se encontro el recurso: " + ruta);
        }
        return dentro;
    }

    /** Dice si el archivo existe. */
    public static boolean existe(String ruta) {
        if (new File(CARPETA + ruta).exists()) {
            return true;
        }
        return Recursos.class.getResource("/recursos/" + ruta) != null;
    }
}
