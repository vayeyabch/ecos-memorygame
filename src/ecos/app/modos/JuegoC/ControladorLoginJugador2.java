package ecos.app.modos.JuegoC;

import ecos.modelo.ModeloLoginJugador2;
import java.util.function.Consumer;

// Controlador de VentanaLoginJugador2: pide al modelo revisar los datos y, si están bien, avisa con alEntrar.
// Responsable: Hellen.
public class ControladorLoginJugador2 {

    private final VentanaLoginJugador2 vista; // La ventana.
    private final ModeloLoginJugador2 modelo; // Revisa los datos.
    private final Consumer<String> alEntrar; // Recibe el usuario cuando el login salió bien.

    public ControladorLoginJugador2(VentanaLoginJugador2 vista, ModeloLoginJugador2 modelo, Consumer<String> alEntrar) {
        this.vista = vista;
        this.modelo = modelo;
        this.alEntrar = alEntrar;
        vista.setAlEntrar(this::entrar);      // al entrar -> entrar()
        vista.setAlCancelar(vista::cerrar);   // al cancelar -> cierra la ventana
    }

    private void entrar() {
        String usuario = vista.getUsuario(); // Lo que escribió.
        String error = modelo.validar(usuario, vista.getClave()); // null si está bien.
        if (error != null) {
            vista.mostrarError(error);
            return;
        }
        modelo.iniciarSesion(usuario); // Guarda al jugador 2.
        vista.cerrar();
        alEntrar.accept(usuario);
    }
}
