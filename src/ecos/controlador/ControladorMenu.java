package ecos.controlador;

import ecos.modelo.ModeloMenu;
import ecos.pantallas.PantallaMenu;
import java.util.function.Consumer;

// Controlador de PantallaMenu: conecta las tarjetas y el selector de tablero con ModeloMenu.
// Responsable: Hellen.
public class ControladorMenu {

    private final PantallaMenu vista; // La pantalla (lo que se ve).
    private final ModeloMenu modelo = new ModeloMenu(); // Nombre y tablero elegido.
    private final Consumer<String> irA; // Sirve para cambiar de pantalla, por ejemplo irA.accept("premios").

    public ControladorMenu(PantallaMenu vista, Consumer<String> irA) {
        this.vista = vista;
        this.irA = irA;
        vista.setAlEntrar(this::alEntrar);                         // al entrar -> alEntrar()
        vista.setAlElegirTamano(this::elegirTamano);               // al tocar 4x4 o 6x6 -> elegirTamano()
        vista.setAlElegirModo(id -> irA.accept(id));               // al tocar una tarjeta -> va a esa pantalla
        vista.setAlSalirAlAcceso(() -> irA.accept("acceso"));      // al tocar Salir -> vuelve al acceso
    }

    private void alEntrar() {
        vista.setSaludo(modelo.saludo()); // Escribe "Hola, nombre!".
        vista.marcarTamano(modelo.getTamano()); // Marca el tablero elegido.
    }

    private void elegirTamano(int n) {
        modelo.elegirTamano(n); // Guarda el tamaño.
        vista.marcarTamano(modelo.getTamano()); // Actualiza los botones.
    }
}
