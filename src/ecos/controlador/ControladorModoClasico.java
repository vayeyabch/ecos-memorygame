package ecos.controlador;

// Clases del propio proyecto que se usan aquí.
import ecos.pantallas.PantallaModoClasico;
// Acciones de Java, Consumer para una acción que recibe un dato.
import java.util.function.Consumer;

// Controlador de PantallaModoClasico, el juego clásico va a las reglas de siempre y el multijugador en red va a la
// pantalla de crear o unirse.
// A cargo de Hellen.
public class ControladorModoClasico {

    public ControladorModoClasico(PantallaModoClasico vista, Consumer<String> irA) {
        vista.setAlLocal(() -> irA.accept("reglas_clasico"));
        vista.setAlRed(() -> irA.accept("multijugador"));
    }
}
