package ecos.modelo;

// BufferedReader: lee texto por líneas de forma eficiente (readLine).
import java.io.BufferedReader;
// IOException: error de entrada/salida; se lanza si la red o el socket fallan.
import java.io.IOException;
// InputStreamReader: convierte los bytes que llegan por la red en caracteres.
import java.io.InputStreamReader;
// OutputStreamWriter: convierte los caracteres a bytes para mandarlos por la red.
import java.io.OutputStreamWriter;
// PrintWriter: escribe texto fácil con println; aquí manda cada mensaje como una línea.
import java.io.PrintWriter;
// Socket: la conexión de red entre las dos computadoras.
import java.net.Socket;
// StandardCharsets: codificación de texto; se usa UTF-8 para no perder tildes ni ñ.
import java.nio.charset.StandardCharsets;
// Consumer: función que recibe un dato; aquí recibe cada mensaje que llega.
import java.util.function.Consumer;

// Una conexión ya establecida entre los dos jugadores. Envía y recibe mensajes de texto, una línea por mensaje.
// La usan igual el anfitrión (ServidorJuego) y el invitado (ClienteJuego).
public class ConexionJuego {

    // El socket, por donde viaja todo; entrada para leer lo que manda el otro; salida para escribirle.
    private final Socket socket;
    private final BufferedReader entrada;
    private final PrintWriter salida;
    // volatile: todos los hilos ven siempre el valor más reciente. Dice si la conexión ya se cerró.
    private volatile boolean cerrada = false;

    // Envuelve el socket en un lector y un escritor de texto UTF-8.
    // throws IOException: obtener los flujos del socket puede fallar y quien crea la conexión debe manejarlo.
    public ConexionJuego(Socket socket) throws IOException {
        this.socket = socket;
        this.entrada = new BufferedReader(new InputStreamReader(socket.getInputStream(), StandardCharsets.UTF_8));
        // El true del final activa el autoflush: cada println se envía de inmediato, sin esperar a llenar un buffer.
        this.salida = new PrintWriter(new OutputStreamWriter(socket.getOutputStream(), StandardCharsets.UTF_8), true);
    }

    // Manda un mensaje al otro jugador.
    // synchronized: si dos hilos envían a la vez, uno espera al otro y los mensajes no se mezclan.
    public synchronized void enviar(String mensaje) {
        // Si ya se cerró, no se manda nada.
        if (!cerrada) {
            salida.println(mensaje);
        }
    }

    // Escucha en otro hilo para no congelar la pantalla. Cada mensaje llega a alMensaje y, si se corta la conexión, se
    // llama alCerrar. Ambas corren en el hilo de la red, no en el de la interfaz.
    public void escuchar(Consumer<String> alMensaje, Runnable alCerrar) {
        // La lambda es el trabajo del hilo: leer líneas hasta que se corte la conexión.
        Thread t = new Thread(() -> {
            try {
                String linea;
                // readLine espera hasta que llegue una línea; devuelve null cuando el otro lado cierra la conexión.
                while ((linea = entrada.readLine()) != null) {
                    // Entrega el mensaje a quien escucha (el controlador).
                    alMensaje.accept(linea);
                }
            // Si la conexión falla al leer, se sale del ciclo; no hace falta hacer nada más aquí.
            } catch (IOException e) {
            }
            // Se avisa solo si la conexión se cortó sola, no si la cerramos nosotros con cerrar().
            boolean avisar = !cerrada;
            cerrar();
            if (avisar) {
                alCerrar.run();
            }
        }, "escucha-red");
        // Hilo daemon: no impide que el programa se cierre.
        t.setDaemon(true);
        t.start();
    }

    // Cierra la conexión sin avisar a alCerrar.
    public void cerrar() {
        // Se marca como cerrada primero, para que enviar y escuchar sepan que no deben avisar ni escribir.
        cerrada = true;
        try {
            socket.close();
        // Si falla al cerrar, no hay nada más que hacer.
        } catch (IOException e) {
        }
    }
}