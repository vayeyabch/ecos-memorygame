package ecos.recursos;

import java.io.File; // Representa un archivo del disco.
import java.io.FileInputStream; // Lee los bytes de un archivo del disco.
import java.io.IOException; // Error que ocurre al leer o abrir archivos.
import java.io.InputStream; // Flujo para leer los bytes de un archivo.

public class Recursos {

    private static final String CARPETA = "src/recursos/"; // Carpeta del disco donde están las imágenes, fuentes y audios.

    public static InputStream abrir(String ruta) throws IOException { // Abre un archivo; la ruta es relativa a src/recursos.
        File archivo = new File(CARPETA + ruta); // Arma la ruta completa en el disco.
        if (archivo.exists()) { // Si el archivo existe en el disco (cuando se corre desde VS Code).
            return new FileInputStream(archivo); // Lo abre desde ahí.
        }
        InputStream dentro = Recursos.class.getResourceAsStream("/recursos/" + ruta); // Si no, lo busca dentro del proyecto compilado (.jar).
        if (dentro == null) { // Si tampoco está ahí.
            throw new IOException("No se encontro el recurso: " + ruta); // Lanza un error que dice cuál archivo falta.
        }
        return dentro; // Entrega el archivo encontrado.
    }

    public static boolean existe(String ruta) { // Dice si un archivo existe.
        if (new File(CARPETA + ruta).exists()) { // Si está en el disco.
            return true; // Existe.
        }
        return Recursos.class.getResource("/recursos/" + ruta) != null; // Si no, revisa si está dentro del proyecto compilado.
    }
}