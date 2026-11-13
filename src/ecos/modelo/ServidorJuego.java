package ecos.modelo;

// Entrada y salida de Java.
import java.io.IOException;
// Red de Java, para buscar la IP de esta computadora, esperar conexiones y manejar el socket.
import java.net.Inet4Address;
import java.net.InetAddress;
import java.net.NetworkInterface;
import java.net.ServerSocket;
import java.net.Socket;
// Utilidades de Java, Enumeration para recorrer las tarjetas de red y Consumer para acciones con un dato.
import java.util.Enumeration;
import java.util.function.Consumer;

// El anfitrión, abre un puerto y espera a que se conecte el otro jugador (solo uno).
// A cargo de Hellen.
public class ServidorJuego {

    // Puerto donde se espera la conexión (el invitado usa el mismo).
    public static final int PUERTO = 5050;

    private ServerSocket servidor;

    // Abre el puerto y espera en otro hilo. Cuando alguien se conecta llama a alConectar; si falla, a alError (por ejemplo
    // si el puerto ya está en uso). Ambas se ejecutan en el hilo de la red.
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
                // Ya llegó el invitado, no se aceptan más.
                servidor.close();
                alConectar.accept(new ConexionJuego(s));
            } catch (IOException e) {
                // Si se canceló con cerrar(), no es un error.
                if (!servidor.isClosed()) {
                    alError.accept("Se cortó la conexión.");
                }
            }
        }, "espera-jugador");
        t.setDaemon(true);
        t.start();
    }

    // Deja de esperar (cancelar la partida).
    public void cerrar() {
        try {
            if (servidor != null) {
                servidor.close();
            }
        } catch (IOException e) {
            // Ya estaba cerrado.
        }
    }

    // IP pública (la que se ve desde internet), preguntándole a un servicio web. Puede tardar, llamarlo fuera del hilo de la
    // interfaz. Devuelve null si no hay internet o el servicio no responde.
    public static String ipPublica() {
        try {
            java.net.HttpURLConnection con = (java.net.HttpURLConnection) java.net.URI.create("https://api.ipify.org").toURL().openConnection();
            con.setConnectTimeout(3000);
            con.setReadTimeout(3000);
            try (java.io.BufferedReader r = new java.io.BufferedReader(new java.io.InputStreamReader(con.getInputStream()))) {
                String ip = r.readLine();
                return ip == null || ip.isBlank() ? null : ip.trim();
            }
        } catch (IOException e) {
            return null;
        }
    }

    // IP de esta computadora en la red (la que se le dice al otro jugador).
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
