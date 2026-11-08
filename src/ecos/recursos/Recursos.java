package ecos.recursos;

// Lectura de archivos, File para los archivos, FileInputStream para leer un archivo del disco, IOException para los
// Lectura de archivos, File para los archivos, FileInputStream para leer un archivo del disco, IOException para los
// errores de lectura y InputStream para leer archivos.
import java.io.File;
import java.io.FileInputStream;
import java.io.IOException;
import java.io.InputStream;

// Abre los archivos del proyecto (imágenes, sonidos y fuentes) desde la carpeta de recursos.
public class Recursos {

    // Carpeta del disco donde están las imágenes, fuentes y audios.
    private static final String CARPETA = "src/recursos/";

    // Abre un archivo; la ruta es relativa a src/recursos.
    public static InputStream abrir(String ruta) throws IOException {
        // Arma la ruta completa en el disco y, si el archivo existe ahí (cuando se corre desde VS Code), lo abre desde ahí.
        File archivo = new File(CARPETA + ruta);
        if (archivo.exists()) {
            return new FileInputStream(archivo);
        }
        // Si no, lo busca dentro del proyecto compilado (.jar), y si tampoco está ahí lanza un error que dice cuál archivo
        // falta.
        InputStream dentro = Recursos.class.getResourceAsStream("/recursos/" + ruta);
        if (dentro == null) {
            throw new IOException("No se encontro el recurso: " + ruta);
        }
        // Entrega el archivo encontrado.
        return dentro;
    }

    // Dice si un archivo existe.
    public static boolean existe(String ruta) {
        // Si está en el disco.
        if (new File(CARPETA + ruta).exists()) {
            return true;
        }
        // Si no, revisa si está dentro del proyecto compilado.
        return Recursos.class.getResource("/recursos/" + ruta) != null;
    }
}