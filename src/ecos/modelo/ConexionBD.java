package ecos.modelo;

// FileInputStream: lee un archivo del disco; aquí se usa para leer config.properties.
import java.io.FileInputStream;
// IOException: error de entrada/salida; falla si no se puede leer el archivo.
import java.io.IOException;
// Connection: una conexión abierta con la base de datos.
import java.sql.Connection;
// DriverManager: abre conexiones con una base de datos a partir de su dirección.
import java.sql.DriverManager;
// SQLException: error al conectarse a la base o al hacer una consulta.
import java.sql.SQLException;
// Properties: lee archivos de pares clave=valor (url, usuario, clave).
import java.util.Properties;

// Abre las conexiones con la base compartida (Supabase, PostgreSQL). Es el único lugar que conoce la dirección y la
// clave; los repositorios solo piden una conexión. Lee config.properties (url, usuario, clave), que está en .gitignore,
// primero en la carpeta donde corre el programa o en la ruta de -Decos.config=...
public class ConexionBD {

    // Archivo de configuración y datos leídos (null hasta la primera vez).
    private static final String ARCHIVO = "config.properties";
    private static Properties datos;

    // Abre una conexión nueva. El que la pide la cierra (try-with-resources).
    // throws SQLException: si falla, quien la llama decide qué hacer.
    public static Connection abrir() throws SQLException {
        Properties p = leer();
        // trim() quita espacios sobrantes; el segundo parámetro de getProperty es el valor por defecto ("").
        return DriverManager.getConnection(p.getProperty("url", "").trim(), p.getProperty("usuario", "").trim(),
                p.getProperty("clave", "").trim());
    }

    // Lee el archivo de configuración una sola vez y guarda los datos. synchronized: dos hilos no lo leen a la vez.
    private static synchronized Properties leer() {
        if (datos == null) {
            // Si se pasó -Decos.config=ruta usa esa ruta; si no, usa config.properties.
            String ruta = System.getProperty("ecos.config", ARCHIVO);
            Properties p = new Properties();
            // try-with-resources: cierra el archivo solo al terminar.
            try (FileInputStream in = new FileInputStream(ruta)) {
                p.load(in);
            } catch (IOException e) {
                // El mensaje dice qué archivo faltó y desde qué carpeta se está corriendo, para encontrar el error rápido.
                throw new RuntimeException("No se pudo leer " + ruta + " (carpeta actual: "
                        + System.getProperty("user.dir") + ")", e);
            }
            datos = p;
        }
        return datos;
    }
}