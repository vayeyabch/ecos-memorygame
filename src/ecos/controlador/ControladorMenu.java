package ecos.controlador;

import ecos.modelo.ModeloMenu;
import ecos.pantallas.PantallaMenu;
import java.util.function.Consumer;

// Controlador de PantallaMenu, conecta las tarjetas y el selector de tablero con ModeloMenu.
public class ControladorMenu {

    // La pantalla y el modelo con el nombre y el tablero elegido.
    private final PantallaMenu vista;
    private final ModeloMenu modelo = new ModeloMenu();

    public ControladorMenu(PantallaMenu vista, Consumer<String> irA) {
        this.vista = vista;
        vista.setAlEntrar(this::alEntrar);
        vista.setAlElegirTamano(this::elegirTamano);
        vista.setAlElegirModo(id -> irA.accept(id));
        vista.setAlSalirAlAcceso(() -> irA.accept("acceso"));
    }

    private void alEntrar() {
        vista.setSaludo(modelo.saludo());
        vista.marcarTamano(modelo.getTamano());
    }

    private void elegirTamano(int n) {
        modelo.elegirTamano(n);
        vista.marcarTamano(modelo.getTamano());
    }
}
