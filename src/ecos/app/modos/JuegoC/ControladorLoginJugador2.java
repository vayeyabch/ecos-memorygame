package ecos.app.modos.JuegoC;

// Clases del propio proyecto que se usan aquí.
import ecos.modelo.ModeloLoginJugador2;
// Acciones de Java, Consumer para una acción que recibe un dato.
import java.util.function.Consumer;

// Controlador de VentanaLoginJugador2, pide al modelo revisar los datos y, si están bien, avisa con alEntrar.
// A cargo de Hellen.
public class ControladorLoginJugador2 {

    // Revisa los datos. Recibe el usuario cuando el login salió bien.
    private final VentanaLoginJugador2 vista;
    private final ModeloLoginJugador2 modelo;
    private final Consumer<String> alEntrar;

    public ControladorLoginJugador2(VentanaLoginJugador2 vista, ModeloLoginJugador2 modelo, Consumer<String> alEntrar) {
        this.vista = vista;
        this.modelo = modelo;
        this.alEntrar = alEntrar;
        // Al entrar se llama entrar() y al cancelar se cierra la ventana.
        vista.setAlEntrar(this::entrar);
        vista.setAlCancelar(vista::cerrar);
    }

    private void entrar() {
        // Lo que escribió. Null si está bien.
        String usuario = vista.getUsuario();
        String error = modelo.validar(usuario, vista.getClave());
        if (error != null) {
            vista.mostrarError(error);
            return;
        }
        // Guarda al jugador 2.
        modelo.iniciarSesion(usuario);
        vista.cerrar();
        alEntrar.accept(usuario);
    }
}
