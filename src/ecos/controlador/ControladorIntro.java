package ecos.controlador;

// Clases del propio proyecto que se usan aquí.
import ecos.pantallas.pantallaIntro;
// Acciones de Java, Consumer para una acción que recibe un dato.
import java.util.function.Consumer;

// Controlador de PantallaIntro, cuando termina la carga (o se salta) pasa a la pantalla de inicio.
// Esta pantalla solo anima los logos, no tiene datos, por eso no necesita modelo.
// A cargo de Hellen.
public class ControladorIntro {

    public ControladorIntro(pantallaIntro vista, Consumer<String> irA) {
        // Al terminar la intro pasa al inicio.
        vista.setAlTerminar(() -> irA.accept("inicio"));
    }
}
