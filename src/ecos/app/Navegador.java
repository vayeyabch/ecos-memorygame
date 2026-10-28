package ecos.app;

import java.awt.CardLayout; // Layout que apila paneles y muestra solo uno a la vez.
import java.util.ArrayDeque; // Estructura que sirve como pila (último en entrar, primero en salir).
import java.util.Deque; // Tipo general de la pila.
import java.util.HashMap; // Diccionario concreto (clave -> valor).
import java.util.Map; // Tipo general del diccionario.
import java.util.function.Consumer; // Representa una acción que recibe un dato (aquí el id de la pantalla).
import javax.swing.JPanel; // Panel de Swing que contiene las pantallas.

public class Navegador {

    private final CardLayout cartas = new CardLayout(); // El layout que cambia entre pantallas, como cartas apiladas.
    private final JPanel contenedor = new JPanel(cartas); // Panel que guarda todas las pantallas y usa ese layout.
    private final Map<String, Pantalla> pantallas = new HashMap<>(); // Diccionario de pantallas registradas (id -> pantalla).
    private final Deque<String> historial = new ArrayDeque<>(); // Pila con los ids de las pantallas por donde se pasó, para volver atrás.
    private Pantalla actual = null; // La pantalla que se está mostrando ahora (null = ninguna).
    private Consumer<String> alCambiarPantalla = id -> { }; // Acción que se ejecuta al cambiar de pantalla; empieza sin hacer nada.

    public JPanel getContenedor() { // Entrega el panel que guarda todas las pantallas.
        return contenedor; // La Ventana lo usa para ponerlo en el centro.
    }

    public void registrar(Pantalla pantalla) { // Deja una pantalla disponible para navegar hacia ella.
        pantalla.setNavegador(this); // Le avisa a la pantalla quién es su navegador.
        pantallas.put(pantalla.getId(), pantalla); // La guarda en el diccionario usando su id como clave.
        contenedor.add(pantalla, pantalla.getId()); // La agrega al contenedor con su id como nombre de la carta.
    }

    public void ir(String id) { // Muestra una pantalla y guarda la actual en el historial.
        if (actual != null) { // Si ya hay una pantalla mostrándose.
            historial.push(actual.getId()); // guarda su id en la pila para poder volver.
        }
        mostrar(id); // Muestra la pantalla pedida.
    }

    public void atras() { // Vuelve a la pantalla anterior.
        if (!historial.isEmpty()) { // Solo si hay alguna pantalla en el historial.
            mostrar(historial.pop()); // Saca la última de la pila y la muestra.
        }
    }

    public boolean puedeVolver() { // Pregunta si hay una pantalla anterior a la que volver.
        return !historial.isEmpty(); // true si el historial tiene algo, false si está vacío.
    }

    public String getIdActual() { // Devuelve el id de la pantalla actual.
        return actual == null ? null : actual.getId(); // Si no hay pantalla devuelve null; si hay, su id.
    }

    // Avisa cada vez que cambia la pantalla (la barra superior lo usa para ocultarse en la intro).
    public void alCambiarPantalla(Consumer<String> accion) { // Recibe la acción que se quiere ejecutar en cada cambio.
        this.alCambiarPantalla = accion; // La guarda para usarla después.
    }

    private void mostrar(String id) { // Método interno que hace el cambio real de pantalla.
        Pantalla siguiente = pantallas.get(id); // Busca la pantalla por su id.
        if (siguiente == null) { // Si no existe ninguna con ese id.
            System.err.println("No existe la pantalla: " + id); // avisa en la consola cuál id falló.
            return; // Termina sin cambiar nada.
        }
        if (actual != null) { // Si había una pantalla mostrándose.
            actual.alSalir(); // le avisa que se va a ocultar.
        }
        actual = siguiente; // La nueva pantalla pasa a ser la actual.
        cartas.show(contenedor, id); // Le pide al CardLayout que muestre esa carta.
        actual.alEntrar(); // Le avisa a la pantalla que ya se está mostrando (pone su música).
        alCambiarPantalla.accept(id); // Ejecuta la acción registrada, pasándole el id nuevo.
    }
}