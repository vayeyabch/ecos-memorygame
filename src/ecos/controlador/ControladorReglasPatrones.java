package ecos.controlador;

// Clases del propio proyecto que se usan aquí.
import ecos.pantallas.PantallaReglasPatrones;
// Acciones de Java, Consumer para una acción que recibe un dato.
import java.util.function.Consumer;

// Controlador de PantallaReglasPatrones, al tocar Comenzar abre el modo patrones.
// Esta pantalla solo muestra reglas fijas, no tiene datos, por eso no necesita modelo.
// A cargo de Hellen.
public class ControladorReglasPatrones {

    public ControladorReglasPatrones(PantallaReglasPatrones vista, Consumer<String> irA) {
        // Al tocar Comenzar va a la partida de patrones.
        vista.setAlComenzar(() -> irA.accept("patrones"));
    }
}
