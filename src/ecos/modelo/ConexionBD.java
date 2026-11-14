package ecos.modelo;

import java.io.FileInputStream;
import java.io.IOException;
import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.SQLException;
import java.util.Properties;

// Abre las conexiones con la base compartida (Supabase, PostgreSQL). Es el único lugar que conoce la dirección y la
// clave; los repositorios solo piden una conexión. Lee config.properties (url, usuario, clave), que está en .gitignore,
// primero en la carpeta donde corre el programa o en la ruta de -Decos.config=...
public class ConexionBD {

    // Archivo de configuración y datos leídos (null hasta la primera vez).
    private static final String ARCHIVO = "config.properties";
    private static Properties datos;

    // Abre una conexión nueva. El que la pide la cierra (try-with-resources).
    public static Connection abrir() throws SQLException {
        Properties p = leer();
        return DriverManager.getConnection(p.getProperty("url", "").trim(), p.getProperty("usuario", "").trim(),
                p.getProperty("clave", "").trim());
    }

    private static synchronized Properties leer() {
        if (datos == null) {
            String ruta = System.getProperty("ecos.config", ARCHIVO);
            Properties p = new Properties();
            try (FileInputStream in = new FileInputStream(ruta)) {
                p.load(in);
            } catch (IOException e) {
                throw new RuntimeException("No se pudo leer " + ruta + " (carpeta actual: "
                        + System.getProperty("user.dir") + ")", e);
            }
            datos = p;
        }
        return datos;
    }
}
