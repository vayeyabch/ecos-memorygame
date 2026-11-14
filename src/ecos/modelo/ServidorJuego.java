package ecos.modelo;

import java.io.IOException;
import java.net.Inet4Address;
import java.net.InetAddress;
import java.net.NetworkInterface;
import java.net.ServerSocket;
import java.net.Socket;
import java.util.Enumeration;
import java.util.function.Consumer;

// El anfitrión: abre un puerto y espera a que se conecte el otro jugador, solo uno.
public class ServidorJuego {

    // Puerto donde se espera la conexión; el invitado usa el mismo.
    public static final int PUERTO = 5050;

    private ServerSocket servidor;

    // Abre el puerto y espera en otro hilo. Al conectarse alguien llama a alConectar; si falla, por ejemplo con el
    // puerto en uso, llama a alError. Ambas corren en el hilo de la red.
    public void iniciar(Consumer<ConexionJuego> alConectar, Consumer<String> alError) {
        try {
            servidor = new ServerSocket(PUERTO);
        } catch (IOException e) {
            alError.accept("No se pudo abrir el puerto " + PUERTO + ". ¿Ya hay una partida creada?");
            return;
        }
        Thread t = new Thread(() -> {
            try {
                Socket s = servidor.accept();
                servidor.close();
                alConectar.accept(new ConexionJuego(s));
            } catch (IOException e) {
                if (!servidor.isClosed()) {
                    alError.accept("Se cortó la conexión.");
                }
            }
        }, "espera-jugador");
        t.setDaemon(true);
        t.start();
    }

    // Deja de esperar, cancela la partida.
    public void cerrar() {
        try {
            if (servidor != null) {
                servidor.close();
            }
        } catch (IOException e) {
        }
    }

    // IP de esta computadora en la red, la que se le dice al otro jugador.
    public static String ipLocal() {
        try {
            Enumeration<NetworkInterface> redes = NetworkInterface.getNetworkInterfaces();
            while (redes.hasMoreElements()) {
                NetworkInterface r = redes.nextElement();
                if (!r.isUp() || r.isLoopback() || r.isVirtual()) {
                    continue;
                }
                Enumeration<InetAddress> dirs = r.getInetAddresses();
                while (dirs.hasMoreElements()) {
                    InetAddress d = dirs.nextElement();
                    if (d instanceof Inet4Address && d.isSiteLocalAddress()) {
                        return d.getHostAddress();
                    }
                }
            }
            return InetAddress.getLocalHost().getHostAddress();
        } catch (IOException e) {
            return "(no se pudo averiguar)";
        }
    }
}
