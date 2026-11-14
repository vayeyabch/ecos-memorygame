package ecos.controlador;

import ecos.pantallas.PantallaInicio;

import java.util.function.Consumer;

// Controlador de PantallaInicio: al tocar la pantalla pasa al acceso. Solo muestra el mensaje parpadeante, así que no
// necesita modelo.
public class ControladorInicio {

    public ControladorInicio(PantallaInicio vista, Consumer<String> irA) {
        vista.setAlContinuar(() -> irA.accept("acceso"));
    }
}
