package ecos.app;

import java.awt.CardLayout;
import java.util.ArrayDeque;
import java.util.Deque;
import java.util.HashMap;
import java.util.Map;
import java.util.function.Consumer;
import javax.swing.JPanel;

// Cambia entre pantallas, guarda las registradas y recuerda el historial para poder volver.
public class Navegador {

    // Layout de pantallas apiladas, panel que las guarda, pantallas registradas por id y pila de ids para volver atrás.
    private final CardLayout cartas = new CardLayout();
    private final JPanel contenedor = new JPanel(cartas);
    private final Map<String, Pantalla> pantallas = new HashMap<>();
    private final Deque<String> historial = new ArrayDeque<>();
    // Pantalla que se muestra ahora y acciones que se ejecutan al cambiar de pantalla.
    private Pantalla actual = null;
    private final java.util.List<Consumer<String>> alCambiarPantalla = new java.util.ArrayList<>();

    public JPanel getContenedor() {
        return contenedor;
    }

    // Deja una pantalla disponible para navegar hacia ella.
    public void registrar(Pantalla pantalla) {
        pantalla.setNavegador(this);
        pantallas.put(pantalla.getId(), pantalla);
        contenedor.add(pantalla, pantalla.getId());
    }

    // Muestra una pantalla y guarda la actual en el historial.
    public void ir(String id) {
        if (actual != null) {
            historial.push(actual.getId());
        }
        mostrar(id);
    }

    // Vuelve a la pantalla anterior.
    public void atras() {
        if (!historial.isEmpty()) {
            mostrar(historial.pop());
        }
    }

    public boolean puedeVolver() {
        return !historial.isEmpty();
    }

    // Avisa cada vez que cambia la pantalla, la barra superior lo usa para ocultarse en la intro.
    public void alCambiarPantalla(Consumer<String> accion) {
        this.alCambiarPantalla.add(accion);
    }

    // Hace el cambio real de pantalla.
    private void mostrar(String id) {
        // Si el id no existe avisa en la consola y no cambia nada.
        Pantalla siguiente = pantallas.get(id);
        if (siguiente == null) {
            System.err.println("No existe la pantalla: " + id);
            return;
        }
        // Le avisa a la pantalla actual que se va a ocultar.
        if (actual != null) {
            actual.alSalir();
        }
        actual = siguiente;
        cartas.show(contenedor, id);
        actual.alEntrar();
        for (Consumer<String> accion : alCambiarPantalla) {
            accion.accept(id);
        }
    }
}
