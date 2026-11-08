package ecos.modelo;

import java.nio.charset.StandardCharsets; // Para convertir texto a bytes con la codificación UTF-8.
import java.security.MessageDigest; // Clase de Java que calcula hashes (aquí, SHA-256).

// Convierte la contraseña en un código que no se puede devolver (hash).
// Así, en la base de datos nunca se guarda la contraseña real, solo su hash.
// Responsable: Valeria. Capa: Modelo.
public class Seguridad {

    // Recibe la contraseña escrita y entrega su hash en texto (64 caracteres hexadecimales).
    // La misma contraseña siempre da el mismo hash, por eso sirve para compararlas.
    public static String hash(String clave) { // Recibe la contraseña.
        try { // Intenta calcular el hash.
            MessageDigest md = MessageDigest.getInstance("SHA-256"); // Pide el algoritmo SHA-256.
            byte[] bytes = md.digest(clave.getBytes(StandardCharsets.UTF_8)); // Calcula el hash de la contraseña.
            StringBuilder sb = new StringBuilder(); // Aquí se va armando el texto del hash.
            for (byte b : bytes) { // Recorre cada byte del hash.
                sb.append(String.format("%02x", b)); // Lo convierte en 2 caracteres hexadecimales.
            }
            return sb.toString(); // Entrega el hash completo.
        } catch (Exception e) { // Solo pasaría si el computador no tuviera SHA-256 (no pasa).
            throw new RuntimeException(e); // Avisa del error.
        }
    }
}
