package ecos.controlador;

import ecos.pantallas.PantallaReglasPatrones;
import java.util.function.Consumer;

// Controlador de PantallaReglasPatrones: al tocar Comenzar abre el modo patrones.
// Esta pantalla solo muestra reglas fijas, no tiene datos, por eso no necesita modelo.
// Responsable: Hellen.
public class ControladorReglasPatrones {

    public ControladorReglasPatrones(PantallaReglasPatrones vista, Consumer<String> irA) {
        vista.setAlComenzar(() -> irA.accept("patrones")); // al tocar Comenzar -> va a la partida de patrones
    }
}
