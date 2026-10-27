package ecos.app;

import java.awt.CardLayout;
import java.util.ArrayDeque;
import java.util.Deque;
import java.util.HashMap;
import java.util.Map;
import java.util.function.Consumer;
import javax.swing.JPanel;

/**
 * Cambia de pantalla y recuerda por donde se paso para el boton "Atras".
 *   registrar(pantalla)  -> la deja disponible
 *   ir("menu")           -> muestra esa pantalla
 *   atras()              -> vuelve a la anterior
 */
public class Navegador {

    private final CardLayout cartas = new CardLayout();
    private final JPanel contenedor = new JPanel(cartas);
    private final Map<String, Pantalla> pantallas = new HashMap<>();
    private final Deque<String> historial = new ArrayDeque<>();
    private Pantalla actual = null;
    private Consumer<String> alCambiarPantalla = id -> { };

    public JPanel getContenedor() {
        return contenedor;
    }

    public void registrar(Pantalla pantalla) {
        pantalla.setNavegador(this);
        pantallas.put(pantalla.getId(), pantalla);
        contenedor.add(pantalla, pantalla.getId());
    }

    public void ir(String id) {
        if (actual != null) {
            historial.push(actual.getId());
        }
        mostrar(id);
    }

    public void atras() {
        if (!historial.isEmpty()) {
            mostrar(historial.pop());
        }
    }

    public boolean puedeVolver() {
        return !historial.isEmpty();
    }

    public String getIdActual() {
        return actual == null ? null : actual.getId();
    }

    /** Avisa cada vez que cambia la pantalla (la barra superior lo usa para ocultarse en la intro). */
    public void alCambiarPantalla(Consumer<String> accion) {
        this.alCambiarPantalla = accion;
    }

    private void mostrar(String id) {
        Pantalla siguiente = pantallas.get(id);
        if (siguiente == null) {
            System.err.println("No existe la pantalla: " + id);
            return;
        }
        if (actual != null) {
            actual.alSalir();
        }
        actual = siguiente;
        cartas.show(contenedor, id);
        actual.alEntrar();
        alCambiarPantalla.accept(id);
    }
}
