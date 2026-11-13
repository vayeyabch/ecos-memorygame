package ecos.modelo;

// Entrada y salida de Java.
import java.io.IOException;
// Red de Java, para conectarse a una dirección y puerto.
import java.net.InetSocketAddress;
import java.net.Socket;

// El invitado, se conecta a la IP del anfitrión.
// A cargo de Hellen.
public class ClienteJuego {

    // Se conecta (espera máximo 4 segundos). Lanza IOException si no se pudo. Hay que llamarlo fuera del hilo de la
    // interfaz porque puede tardar.
    // El texto puede ser solo la IP (usa el puerto de siempre) o IP:puerto (por si se usa un túnel con otro puerto).
    public static ConexionJuego conectar(String direccion) throws IOException {
        String ip = direccion.trim();
        int puerto = ServidorJuego.PUERTO;
        int dos = ip.lastIndexOf(':');
        if (dos > 0) {
            try {
                puerto = Integer.parseInt(ip.substring(dos + 1).trim());
                ip = ip.substring(0, dos).trim();
            } catch (NumberFormatException e) {
                throw new IOException("Puerto inválido");
            }
        }
        Socket s = new Socket();
        try {
            s.connect(new InetSocketAddress(ip, puerto), 4000);
            return new ConexionJuego(s);
        } catch (IOException e) {
            s.close();
            throw e;
        }
    }
}
