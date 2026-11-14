package ecos.modelo;

import java.io.IOException;
import java.net.InetSocketAddress;
import java.net.Socket;

// El invitado: se conecta a la IP del anfitrión.
public class ClienteJuego {

    // Se conecta con un máximo de 4 segundos y lanza IOException si no pudo; llamarlo fuera del hilo de la interfaz. El
    // texto puede ser solo la IP (puerto de siempre) o IP:puerto, por si se usa un túnel.
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
