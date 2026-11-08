package ecos.controlador;

import ecos.modelo.ModeloReglasClasico;
import ecos.pantallas.PantallaReglasClasico;
import java.util.function.Consumer;

// Controlador de PantallaReglasClasico: llena los nombres al entrar y, al comenzar, guarda los nombres y abre la partida.
// Responsable: Hellen.
public class ControladorReglasClasico {

    private final PantallaReglasClasico vista; // La pantalla (lo que se ve).
    private final ModeloReglasClasico modelo = new ModeloReglasClasico(); // Nombres y regla del tablero.
    private final Consumer<String> irA; // Sirve para cambiar de pantalla.

    public ControladorReglasClasico(PantallaReglasClasico vista, Consumer<String> irA) {
        this.vista = vista;
        this.irA = irA;
        vista.setAlEntrar(this::alEntrar);       // al entrar -> alEntrar()
        vista.setAlComenzar(this::comenzar);     // al tocar Comenzar -> comenzar()
    }

    private void alEntrar() {
        vista.setNombres(modelo.nombreInicial1(), modelo.nombreInicial2()); // Pone los nombres iniciales.
        vista.setReglaTablero(modelo.reglaTablero()); // Pone la regla con el tamaño elegido.
    }

    private void comenzar() {
        modelo.prepararPartida(vista.getNombre1(), vista.getNombre2()); // Guarda los nombres.
        irA.accept("clasico"); // Empieza la partida.
    }
}
