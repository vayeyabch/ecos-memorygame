package ecos.app.modos.JuegoC;

// ModeloLoginJugador2 es el modelo que revisa si los datos del jugador 2 son válidos y guarda al jugador 2 en la sesión.
import ecos.modelo.ModeloLoginJugador2;
// Consumer es una función que recibe un dato y no devuelve nada. Aquí recibe el usuario que entró.
import java.util.function.Consumer;

// Es el controlador del login del jugador 2. Conecta la ventana con su modelo: la ventana avisa que se tocó Entrar,
// el controlador le pide al modelo revisar los datos y, si están bien, avisa a quien lo creó con alEntrar.
// VentanaLoginJugador2 no se importa porque está en el mismo paquete, ecos.app.modos.JuegoC.
public class ControladorLoginJugador2 {

    // La vista, o sea la ventana. Solo dibuja y avisa los clics.
    private final VentanaLoginJugador2 vista;
    // El modelo, que contiene la lógica: revisar los datos y guardar al jugador 2.
    private final ModeloLoginJugador2 modelo;
    // Función que se ejecuta cuando el jugador 2 entró bien. Recibe el nombre de usuario.
    // Con ella el controlador avisa a ControladorReglasClasico sin conocerlo.
    private final Consumer<String> alEntrar;

    // Recibe las tres piezas y conecta la vista con este controlador.
    public ControladorLoginJugador2(VentanaLoginJugador2 vista, ModeloLoginJugador2 modelo, Consumer<String> alEntrar) {
        // this.vista es el campo y vista es el parámetro, que se llaman igual.
        this.vista = vista;
        this.modelo = modelo;
        this.alEntrar = alEntrar;
        // Le deja a la vista lo que debe pasar al tocar Entrar. this::entrar significa este método de este objeto.
        vista.setAlEntrar(this::entrar);
        // Le deja lo que debe pasar al tocar Cancelar. vista::cerrar significa el método cerrar de esa vista.
        vista.setAlCancelar(vista::cerrar);
    }

    // Se ejecuta al tocar Entrar o al apretar Enter. Hace el recorrido completo del login.
    private void entrar() {
        // Toma lo que el usuario escribió, preguntándole a la vista.
        String usuario = vista.getUsuario();
        // Le pide al modelo revisar el usuario y la clave. Devuelve un texto de error, o null si todo está bien.
        String error = modelo.validar(usuario, vista.getClave());
        // Si hay error, la vista lo muestra y el método termina con return, sin seguir.
        if (error != null) {
            vista.mostrarError(error);
            return;
        }
        // Si todo está bien, el modelo deja al jugador 2 en la sesión como jugador con cuenta.
        modelo.iniciarSesion(usuario);
        // Cierra la ventana.
        vista.cerrar();
        // Avisa a quien lo creó, con el usuario, que el jugador 2 ya entró. accept ejecuta la función que recibe un dato.
        alEntrar.accept(usuario);
    }
}