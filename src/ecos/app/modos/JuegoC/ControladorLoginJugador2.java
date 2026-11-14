package ecos.app.modos.JuegoC;

import ecos.modelo.ModeloLoginJugador2;
import java.util.function.Consumer;

// Controlador de VentanaLoginJugador2: pide al modelo revisar los datos y, si están bien, avisa con alEntrar.
public class ControladorLoginJugador2 {

    private final VentanaLoginJugador2 vista;
    private final ModeloLoginJugador2 modelo;
    private final Consumer<String> alEntrar;

    public ControladorLoginJugador2(VentanaLoginJugador2 vista, ModeloLoginJugador2 modelo, Consumer<String> alEntrar) {
        this.vista = vista;
        this.modelo = modelo;
        this.alEntrar = alEntrar;
        vista.setAlEntrar(this::entrar);
        vista.setAlCancelar(vista::cerrar);
    }

    private void entrar() {
        String usuario = vista.getUsuario();
        String error = modelo.validar(usuario, vista.getClave());
        if (error != null) {
            vista.mostrarError(error);
            return;
        }
        modelo.iniciarSesion(usuario);
        vista.cerrar();
        alEntrar.accept(usuario);
    }
}
