package ecos.modelo;

import java.nio.charset.StandardCharsets;
import java.security.MessageDigest;

// Convierte la contraseña en un hash para no guardar nunca la real en la base.
public class Seguridad {

    public static String hash(String clave) {
        // La misma contraseña siempre da el mismo hash, por eso sirve para compararlas.
        try {
            MessageDigest md = MessageDigest.getInstance("SHA-256");
            byte[] bytes = md.digest(clave.getBytes(StandardCharsets.UTF_8));
            StringBuilder sb = new StringBuilder();
            for (byte b : bytes) {
                sb.append(String.format("%02x", b));
            }
            return sb.toString();
        } catch (Exception e) {
            throw new RuntimeException(e);
        }
    }
}
