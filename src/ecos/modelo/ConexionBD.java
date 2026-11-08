package ecos.modelo;

// Lectura de archivos, errores de lectura.
import java.io.FileInputStream;
import java.io.IOException;
// Base de datos, la conexión, el administrador de conexiones y el error de SQL.
import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.SQLException;
// Utilidades de Java, Properties para leer archivos clave=valor.
import java.util.Properties;

// Abre las conexiones con la base de datos compartida (Supabase, PostgreSQL). Es el único lugar que conoce la dirección y
// la clave de la base; los repositorios solo le piden una conexión.
// Lee config.properties (url, usuario, clave), que está en .gitignore para no subirlo a Git.
// Se busca primero en la carpeta donde corre el programa, o en la ruta que diga la propiedad -Decos.config=...
// A cargo de Valeria.
public class ConexionBD {

    // Nombre del archivo de configuración. Datos leídos (null hasta la primera vez).
    private static final String ARCHIVO = "config.properties";
    private static Properties datos;

    // Abre una conexión nueva. El que la pide la cierra (try-with-resources).
    public static Connection abrir() throws SQLException {
        Properties p = leer();
        return DriverManager.getConnection(p.getProperty("url", "").trim(), p.getProperty("usuario", "").trim(),
                p.getProperty("clave", "").trim());
    }

    // Lee el archivo una sola vez.
    private static synchronized Properties leer() {
        if (datos == null) {
            String ruta = System.getProperty("ecos.config", ARCHIVO);
            Properties p = new Properties();
            try (FileInputStream in = new FileInputStream(ruta)) {
                p.load(in);
            } catch (IOException e) {
                // Mensaje claro con la carpeta desde donde corre el programa.
                throw new RuntimeException("No se pudo leer " + ruta + " (carpeta actual: "
                        + System.getProperty("user.dir") + ")", e);
            }
            datos = p;
        }
        return datos;
    }
}
