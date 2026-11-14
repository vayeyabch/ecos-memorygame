package ecos.controlador;

import ecos.pantallas.PantallaModoClasico;
import java.util.function.Consumer;

// Controlador de PantallaModoClasico, el juego clásico va a las reglas de siempre y el multijugador en red va a la
// pantalla de crear o unirse.
public class ControladorModoClasico {

    public ControladorModoClasico(PantallaModoClasico vista, Consumer<String> irA) {
        vista.setAlLocal(() -> irA.accept("reglas_clasico"));
        vista.setAlRed(() -> irA.accept("multijugador"));
    }
}
