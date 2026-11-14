package ecos.controlador;

import ecos.pantallas.PantallaIntro;
import java.util.function.Consumer;

// Controlador de PantallaIntro: cuando termina la carga, o se salta, pasa al inicio. Solo anima los logos, así que no
// necesita modelo.
public class ControladorIntro {

    public ControladorIntro(PantallaIntro vista, Consumer<String> irA) {
        vista.setAlTerminar(() -> irA.accept("inicio"));
    }
}
