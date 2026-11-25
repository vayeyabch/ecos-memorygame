package ecos.modelo;

// IOException: error de entrada/salida; se lanza si falla abrir el puerto o conectar.
import java.io.IOException;
// Inet4Address: dirección IP versión 4 (la de tipo 192.168.x.x).
import java.net.Inet4Address;
// InetAddress: una dirección IP cualquiera.
import java.net.InetAddress;
// NetworkInterface: una tarjeta de red de esta computadora (wifi, cable...).
import java.net.NetworkInterface;
// ServerSocket: abre un puerto y espera a que alguien se conecte.
import java.net.ServerSocket;
// Socket: la conexión ya establecida con el otro jugador.
import java.net.Socket;
// Enumeration: recorre colecciones antiguas, como las listas de tarjetas de red y direcciones.
import java.util.Enumeration;
// Consumer: función que recibe un dato; aquí recibe la conexión o el texto del error.
import java.util.function.Consumer;

// El anfitrión: abre un puerto y espera a que se conecte el otro jugador, solo uno.
public class ServidorJuego {

    // Puerto donde se espera la conexión; el invitado usa el mismo.
    public static final int PUERTO = 5050;

    // El que escucha el puerto; null hasta que se abre.
    private ServerSocket servidor;

    // Abre el puerto y espera en otro hilo. Al conectarse alguien llama a alConectar; si falla, por ejemplo con el
    // puerto en uso, llama a alError. Ambas corren en el hilo de la red.
    public void iniciar(Consumer<ConexionJuego> alConectar, Consumer<String> alError) {
        try {
            servidor = new ServerSocket(PUERTO);
        } catch (IOException e) {
            // Normalmente falla porque otra partida ya tiene ese puerto abierto.
            alError.accept("No se pudo abrir el puerto " + PUERTO + ". ¿Ya hay una partida creada?");
            return;
        }
        // La espera se hace en otro hilo, porque accept() se queda bloqueado hasta que llega alguien.
        Thread t = new Thread(() -> {
            try {
                // Espera al invitado; cuando llega, devuelve su socket.
                Socket s = servidor.accept();
                // Ya no hace falta escuchar: es una partida de solo dos jugadores.
                servidor.close();
                alConectar.accept(new ConexionJuego(s));
            } catch (IOException e) {
                // Si el servidor ya estaba cerrado, fue una cancelación a propósito y no se avisa error.
                if (!servidor.isClosed()) {
                    alError.accept("Se cortó la conexión.");
                }
            }
        }, "espera-jugador");
        // Hilo daemon: no impide que el programa se cierre.
        t.setDaemon(true);
        t.start();
    }

    // Deja de esperar, cancela la partida.
    public void cerrar() {
        try {
            if (servidor != null) {
                servidor.close();
            }
        // Si falla al cerrar, no hay nada más que hacer.
        } catch (IOException e) {
        }
    }

    // IP de esta computadora en la red, la que se le dice al otro jugador.
    public static String ipLocal() {
        try {
            Enumeration<NetworkInterface> redes = NetworkInterface.getNetworkInterfaces();
            while (redes.hasMoreElements()) {
                NetworkInterface r = redes.nextElement();
                // Salta las tarjetas apagadas, la interna (loopback) y las virtuales.
                if (!r.isUp() || r.isLoopback() || r.isVirtual()) {
                    continue;
                }
                Enumeration<InetAddress> dirs = r.getInetAddresses();
                while (dirs.hasMoreElements()) {
                    InetAddress d = dirs.nextElement();
                    // La buena es una IPv4 de red local (192.168.x.x, 10.x.x.x...).
                    if (d instanceof Inet4Address && d.isSiteLocalAddress()) {
                        return d.getHostAddress();
                    }
                }
            }
            // Si no encontró ninguna, usa la dirección que Java cree que es la de esta computadora.
            return InetAddress.getLocalHost().getHostAddress();
        } catch (IOException e) {
            return "(no se pudo averiguar)";
        }
    }
}