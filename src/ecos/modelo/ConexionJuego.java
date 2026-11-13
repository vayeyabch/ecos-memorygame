package ecos.modelo;

// Entrada y salida de Java, para leer y escribir texto por el socket.
import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.OutputStreamWriter;
import java.io.PrintWriter;
// Red de Java, Socket para la conexión entre las dos computadoras.
import java.net.Socket;
import java.nio.charset.StandardCharsets;
// Acciones de Java, Consumer para una acción que recibe un dato.
import java.util.function.Consumer;

// Una conexión ya establecida entre los dos jugadores. Envía y recibe mensajes de texto, una línea por mensaje.
// A cargo de Hellen.
public class ConexionJuego {

    private final Socket socket;
    private final BufferedReader entrada;
    private final PrintWriter salida;
    private volatile boolean cerrada = false;

    public ConexionJuego(Socket socket) throws IOException {
        this.socket = socket;
        this.entrada = new BufferedReader(new InputStreamReader(socket.getInputStream(), StandardCharsets.UTF_8));
        this.salida = new PrintWriter(new OutputStreamWriter(socket.getOutputStream(), StandardCharsets.UTF_8), true);
    }

    // Manda un mensaje al otro jugador.
    public synchronized void enviar(String mensaje) {
        if (!cerrada) {
            salida.println(mensaje);
        }
    }

    // Empieza a escuchar en otro hilo (para no congelar la pantalla). Cada mensaje llega a alMensaje; si se corta la
    // conexión se llama alCerrar. Ojo, ambas se ejecutan en el hilo de la red, no en el de la interfaz.
    public void escuchar(Consumer<String> alMensaje, Runnable alCerrar) {
        Thread t = new Thread(() -> {
            try {
                String linea;
                while ((linea = entrada.readLine()) != null) {
                    alMensaje.accept(linea);
                }
            } catch (IOException e) {
                // Se cortó la conexión, se avisa abajo.
            }
            boolean avisar = !cerrada;
            cerrar();
            if (avisar) {
                alCerrar.run();
            }
        }, "escucha-red");
        t.setDaemon(true);
        t.start();
    }

    // Cierra la conexión (no avisa a alCerrar).
    public void cerrar() {
        cerrada = true;
        try {
            socket.close();
        } catch (IOException e) {
            // Ya estaba cerrada.
        }
    }
}
