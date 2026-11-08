package ecos.controlador;

// Clases del propio proyecto que se usan aquí.
import ecos.modelo.ModeloMenu;
import ecos.pantallas.PantallaMenu;
// Acciones de Java, Consumer para una acción que recibe un dato.
import java.util.function.Consumer;

// Controlador de PantallaMenu, conecta las tarjetas y el selector de tablero con ModeloMenu.
// A cargo de Hellen.
public class ControladorMenu {

    // La pantalla (lo que se ve), el nombre y el tablero elegido, y el modo de cambiar de pantalla, por ejemplo
    // irA.accept("premios").
    private final PantallaMenu vista;
    private final ModeloMenu modelo = new ModeloMenu();
    private final Consumer<String> irA;

    public ControladorMenu(PantallaMenu vista, Consumer<String> irA) {
        this.vista = vista;
        this.irA = irA;
        // Al entrar se llama alEntrar(), al tocar 4x4 o 6x6 elegirTamano(), al tocar una tarjeta va a esa pantalla y al tocar
        // Salir vuelve al acceso.
        vista.setAlEntrar(this::alEntrar);
        vista.setAlElegirTamano(this::elegirTamano);
        vista.setAlElegirModo(id -> irA.accept(id));
        vista.setAlSalirAlAcceso(() -> irA.accept("acceso"));
    }

    private void alEntrar() {
        // Escribe "Hola, nombre!". Marca el tablero elegido.
        vista.setSaludo(modelo.saludo());
        vista.marcarTamano(modelo.getTamano());
    }

    private void elegirTamano(int n) {
        // Guarda el tamaño. Actualiza los botones.
        modelo.elegirTamano(n);
        vista.marcarTamano(modelo.getTamano());
    }
}
