package ecos.app;

// CardLayout apila componentes como cartas en el mismo lugar y muestra una sola a la vez. Se usa para mostrar una pantalla por vez.
import java.awt.CardLayout;
// ArrayDeque es la clase concreta que se usa como pila, donde se agrega y se saca por el mismo extremo.
import java.util.ArrayDeque;
// Deque es el tipo de la pila. Aquí se usa con push para agregar arriba y pop para sacar de arriba.
import java.util.Deque;
// HashMap es la clase concreta del diccionario. Guarda pares de clave y valor y los busca rápido por la clave.
import java.util.HashMap;
// Map es el tipo del diccionario de pantallas, donde la clave es el id y el valor es la pantalla.
import java.util.Map;
// Consumer es una función que recibe un dato y no devuelve nada. Aquí recibe el id de la pantalla nueva.
import java.util.function.Consumer;
// JPanel es un panel, un contenedor donde se meten otros componentes. Aquí guarda las pantallas apiladas.
import javax.swing.JPanel;

// Decide qué pantalla se ve. Guarda las pantallas por su id, cambia entre ellas
// y recuerda el historial para poder volver atrás.
public class Navegador {

    // Muestra una sola carta de las apiladas.
    private final CardLayout cartas = new CardLayout();
    // Panel que contiene las pantallas y usa cartas para acomodarlas. Ventana lo pone como su contenido.
    private final JPanel contenedor = new JPanel(cartas);
    // Diccionario de id a pantalla, para encontrar una pantalla por su nombre.
    private final Map<String, Pantalla> pantallas = new HashMap<>();
    // Pila con los ids de las pantallas por las que ya pasó el usuario. Sirve para volver atrás.
    private final Deque<String> historial = new ArrayDeque<>();
    // La pantalla que se ve ahora. Empieza en null porque todavía no hay ninguna, y cambia cada vez que se navega.
    private Pantalla actual = null;
    // Lista de funciones que alguien dejó guardadas para ejecutarlas cada vez que cambia la pantalla.
    // Cada una recibe el id de la pantalla nueva. Se escribe java.util.List completo porque no está importado.
    private final java.util.List<Consumer<String>> alCambiarPantalla = new java.util.ArrayList<>();

    // Entrega el panel donde están las pantallas. Ventana lo usa como su contenido.
    public JPanel getContenedor() {
        return contenedor;
    }

    // Deja una pantalla lista para poder navegar hacia ella. Main lo llama una vez por cada pantalla.
    public void registrar(Pantalla pantalla) {
        // Le entrega a la pantalla este Navegador, para que ella pueda pedir cambiar de pantalla.
        pantalla.setNavegador(this);
        // La guarda en el diccionario con su id, para encontrarla después por su nombre.
        pantallas.put(pantalla.getId(), pantalla);
        // La agrega al panel como una carta con su id como nombre, para poder mostrarla con show.
        contenedor.add(pantalla, pantalla.getId());
    }

    // Cambia a la pantalla con ese id. Antes anota la actual en el historial para poder volver a ella.
    public void ir(String id) {
        // La primera vez no hay pantalla actual, así que no hay nada que anotar.
        if (actual != null) {
            historial.push(actual.getId());
        }
        mostrar(id);
    }

    // Vuelve a la pantalla anterior, sacando la última del historial. Si no hay historial no hace nada.
    public void atras() {
        // El signo de exclamación significa no, o sea si el historial no está vacío.
        if (!historial.isEmpty()) {
            mostrar(historial.pop());
        }
    }

    // Dice si hay una pantalla a la que volver. La barra superior lo usa para mostrar u ocultar el botón Atrás.
    public boolean puedeVolver() {
        return !historial.isEmpty();
    }

    // Guarda una función para ejecutarla cada vez que cambie la pantalla. No la ejecuta aquí, solo la guarda.
    // La barra superior y el botón i la usan para ocultarse según la pantalla.
    public void alCambiarPantalla(Consumer<String> accion) {
        // This sirve para indicar la lista, porque el método y la lista se llaman igual.
        this.alCambiarPantalla.add(accion);
    }

    // Hace el cambio real de pantalla. Lo llaman ir y atras.
    private void mostrar(String id) {
        // Busca la pantalla por su id. Si no está registrada avisa en la consola y no cambia nada.
        Pantalla siguiente = pantallas.get(id);
        if (siguiente == null) {
            System.err.println("No existe la pantalla: " + id);
            return;
        }
        // A la pantalla que se va le avisa que sale, para que pare sus relojes y cierre sus diálogos.
        if (actual != null) {
            actual.alSalir();
        }
        // Ahora la pantalla actual es la nueva.
        actual = siguiente;
        // El CardLayout muestra la carta con ese nombre y oculta las demás.
        cartas.show(contenedor, id);
        // A la nueva le avisa que ya se ve, y ella pone su música y arranca lo suyo.
        actual.alEntrar();
        // Ejecuta cada función guardada, pasándole el id de la pantalla nueva.
        // Así la barra superior y el botón i deciden si se ocultan o se muestran.
        for (Consumer<String> accion : alCambiarPantalla) {
            accion.accept(id);
        }
    }
}