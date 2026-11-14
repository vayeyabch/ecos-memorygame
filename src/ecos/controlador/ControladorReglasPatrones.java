package ecos.controlador;

import ecos.pantallas.PantallaReglasPatrones;
import java.util.function.Consumer;

// Controlador de PantallaReglasPatrones: al tocar Comenzar abre el modo patrones. Solo muestra reglas fijas, así que no
// necesita modelo.
public class ControladorReglasPatrones {

    public ControladorReglasPatrones(PantallaReglasPatrones vista, Consumer<String> irA) {
        vista.setAlComenzar(() -> irA.accept("patrones"));
    }
}
