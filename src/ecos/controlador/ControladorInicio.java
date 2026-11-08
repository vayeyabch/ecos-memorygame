package ecos.controlador;

// Clases del propio proyecto que se usan aquí.
import ecos.pantallas.PantallaInicio;
// Acciones de Java, Consumer para una acción que recibe un dato.
import java.util.function.Consumer;

// Controlador de PantallaInicio, al tocar la pantalla pasa al acceso.
// Esta pantalla solo muestra el mensaje parpadeante, no tiene datos, por eso no necesita modelo.
// A cargo de Hellen.
public class ControladorInicio {

    public ControladorInicio(PantallaInicio vista, Consumer<String> irA) {
        // Al tocar la pantalla pasa al acceso.
        vista.setAlContinuar(() -> irA.accept("acceso"));
    }
}
