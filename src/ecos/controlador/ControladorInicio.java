package ecos.controlador;

import ecos.pantallas.PantallaInicio;
import java.util.function.Consumer;

// Controlador de PantallaInicio: al tocar la pantalla pasa al acceso.
// Esta pantalla solo muestra el mensaje parpadeante, no tiene datos, por eso no necesita modelo.
// Responsable: Hellen.
public class ControladorInicio {

    public ControladorInicio(PantallaInicio vista, Consumer<String> irA) {
        vista.setAlContinuar(() -> irA.accept("acceso")); // al tocar la pantalla -> va al acceso
    }
}
