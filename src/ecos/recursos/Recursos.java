package ecos.recursos;

import java.io.File; // Para representar un archivo del disco.
import java.io.FileInputStream; // Para leer el contenido de un archivo del disco.
import java.io.IOException; // Error que se lanza si algo falla o no se encuentra el archivo.
import java.io.InputStream; // Tipo general de "flujo de lectura" que devolvemos.

// Ayudante para abrir archivos de la carpeta src/recursos.
// Busca primero en disco (VS Code) y si no, dentro del proyecto compilado (.jar).
public class Recursos { 

    private static final String CARPETA = "src/recursos/"; // Ruta fija donde están las imágenes, fuentes y audio.

    // Abre un archivo. La ruta es relativa a src/recursos
    public static InputStream abrir(String ruta) throws IOException { // Devuelve un flujo para leer el archivo; avisa con IOException si falla.
        File archivo = new File(CARPETA + ruta); // Arma la ruta completa en disco (carpeta + nombre).
        if (archivo.exists()) { // Pregunta si el archivo está en el disco.
            return new FileInputStream(archivo); // Si está, lo abre y lo devuelve para que otra clase lo lea.
        }
        InputStream dentro = Recursos.class.getResourceAsStream("/recursos/" + ruta); // lo busca dentro del proyecto compilado (.jar).
        if (dentro == null) { // null significa que tampoco está ahí.
            throw new IOException("No se encontro el recurso: " + ruta); // Lanza un error con el nombre del archivo que falta.
        }
        return dentro; // Si lo encontró dentro del .jar, lo devuelve.
    }

    // Dice si el archivo existe.
    public static boolean existe(String ruta) { // Devuelve true o false, sin abrir el archivo.
        if (new File(CARPETA + ruta).exists()) { // Revisa primero en disco.
            return true; // Está en disco: existe.
        }
        return Recursos.class.getResource("/recursos/" + ruta) != null; // Si no, revisa dentro del .jar: existe si no es null.
    }
}