package ecos.controlador;

import ecos.pantallas.pantallaIntro;
import java.util.function.Consumer;

// Controlador de pantallaIntro: cuando termina la carga (o se salta) pasa a la pantalla de inicio.
// Esta pantalla solo anima los logos, no tiene datos, por eso no necesita modelo.
// Responsable: Hellen.
public class ControladorIntro {

    public ControladorIntro(pantallaIntro vista, Consumer<String> irA) {
        vista.setAlTerminar(() -> irA.accept("inicio")); // al terminar la intro -> va al inicio
    }
}
