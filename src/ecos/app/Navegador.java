package ecos.app;

// Librería gráfica AWT, CardLayout para cambiar entre pantallas apiladas.
import java.awt.CardLayout;
// Utilidades de Java, ArrayDeque para la pila del historial, Deque para las pilas, HashMap para diccionarios rápidos y
// Map para los diccionarios.
import java.util.ArrayDeque;
import java.util.Deque;
import java.util.HashMap;
import java.util.Map;
// Acciones de Java, Consumer para una acción que recibe un dato.
import java.util.function.Consumer;
// Librería Swing, JPanel para los paneles.
import javax.swing.JPanel;

// Maneja el cambio de pantallas, guarda todas las pantallas registradas, muestra una a la vez y recuerda el historial
// para poder volver.
public class Navegador {

    // Layout que cambia entre pantallas como cartas apiladas, panel que guarda todas las pantallas con ese layout,
    // diccionario de pantallas registradas por id y pila de ids por donde se pasó, para poder volver atrás.
    private final CardLayout cartas = new CardLayout();
    private final JPanel contenedor = new JPanel(cartas);
    private final Map<String, Pantalla> pantallas = new HashMap<>();
    private final Deque<String> historial = new ArrayDeque<>();
    // La pantalla que se está mostrando ahora (null = ninguna). Acciones que se ejecutan al cambiar de pantalla (la barra
    // de arriba, el botón "i", etc.).
    private Pantalla actual = null;
    private final java.util.List<Consumer<String>> alCambiarPantalla = new java.util.ArrayList<>();

    // Entrega el panel que guarda todas las pantallas.
    public JPanel getContenedor() {
        // La Ventana lo usa para ponerlo en el centro.
        return contenedor;
    }

    // Deja una pantalla disponible para navegar hacia ella.
    public void registrar(Pantalla pantalla) {
        // Le avisa a la pantalla quién es su navegador. La guarda en el diccionario usando su id como clave. La agrega al
        // contenedor con su id como nombre de la carta.
        pantalla.setNavegador(this);
        pantallas.put(pantalla.getId(), pantalla);
        contenedor.add(pantalla, pantalla.getId());
    }

    // Muestra una pantalla y guarda la actual en el historial.
    public void ir(String id) {
        // Si ya hay una pantalla mostrándose. Guarda su id en la pila para poder volver.
        if (actual != null) {
            historial.push(actual.getId());
        }
        // Muestra la pantalla pedida.
        mostrar(id);
    }

    // Vuelve a la pantalla anterior.
    public void atras() {
        // Solo si hay alguna pantalla en el historial. Saca la última de la pila y la muestra.
        if (!historial.isEmpty()) {
            mostrar(historial.pop());
        }
    }

    // Pregunta si hay una pantalla anterior a la que volver.
    public boolean puedeVolver() {
        // True si el historial tiene algo, false si está vacío.
        return !historial.isEmpty();
    }

    // Avisa cada vez que cambia la pantalla (la barra superior lo usa para ocultarse en la intro).
    // Recibe la acción que se quiere ejecutar en cada cambio.
    public void alCambiarPantalla(Consumer<String> accion) {
        // La agrega a la lista para usarla después.
        this.alCambiarPantalla.add(accion);
    }

    // Método interno que hace el cambio real de pantalla.
    private void mostrar(String id) {
        // Busca la pantalla por su id, y si no existe ninguna avisa en la consola cuál id falló y termina sin cambiar nada.
        Pantalla siguiente = pantallas.get(id);
        if (siguiente == null) {
            System.err.println("No existe la pantalla: " + id);
            return;
        }
        // Si había una pantalla mostrándose. Le avisa que se va a ocultar.
        if (actual != null) {
            actual.alSalir();
        }
        // La nueva pantalla pasa a ser la actual. Le pide al CardLayout que muestre esa carta. Le avisa a la pantalla que ya
        // se está mostrando (pone su música). Recorre todas las acciones registradas.
        actual = siguiente;
        cartas.show(contenedor, id);
        actual.alEntrar();
        for (Consumer<String> accion : alCambiarPantalla) {
            // Ejecuta cada una, pasándole el id nuevo.
            accion.accept(id);
        }
    }
}