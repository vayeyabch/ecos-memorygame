package ecos.modelo;

// Librería de texto de Java, StandardCharsets para escribir el texto en UTF-8.
import java.nio.charset.StandardCharsets;
// Librería de seguridad de Java, MessageDigest para calcular el hash.
import java.security.MessageDigest;

// Convierte la contraseña en un código que no se puede devolver (hash), así en la base de datos nunca se guarda la real.
// A cargo de Valeria.
public class Seguridad {

    // Recibe la contraseña escrita y entrega su hash SHA-256 en texto (64 caracteres hexadecimales).
    public static String hash(String clave) {
        // La misma contraseña siempre da el mismo hash, por eso sirve para compararlas. Cada byte se escribe con dos
        // caracteres hexadecimales.
        try {
            MessageDigest md = MessageDigest.getInstance("SHA-256");
            byte[] bytes = md.digest(clave.getBytes(StandardCharsets.UTF_8));
            StringBuilder sb = new StringBuilder();
            for (byte b : bytes) {
                sb.append(String.format("%02x", b));
            }
            return sb.toString();
        } catch (Exception e) {
            // Solo pasaría si el computador no tuviera SHA-256, y eso no pasa.
            throw new RuntimeException(e);
        }
    }
}
