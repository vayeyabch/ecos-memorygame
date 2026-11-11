package ecos.controlador;

// Clases del propio proyecto que se usan aquí.
import ecos.modelo.ModeloRegistro;
import ecos.pantallas.PantallaRegistro;
// Librería Swing, SwingUtilities para volver al hilo de la pantalla.
import javax.swing.SwingUtilities;

// Controlador de PantallaRegistro (paso 1 de 2), al tocar Siguiente revisa el formulario con ModeloRegistro y, si está
// bien, pasa al registro del rostro, que es opcional. Al tocar Crear cuenta también lo revisa y crea la cuenta sin rostro.
// A cargo de Valeria.
public class ControladorRegistro {

    private final PantallaRegistro vista;
    private final ModeloRegistro modelo;

    public ControladorRegistro(PantallaRegistro vista) {
        this(vista, new ModeloRegistro());
    }

    public ControladorRegistro(PantallaRegistro vista, ModeloRegistro modelo) {
        this.vista = vista;
        this.modelo = modelo;
        // Lo que hacen los botones Siguiente y Crear cuenta.
        vista.setAccionSiguiente(this::siguiente);
        vista.setAccionCrearCuenta(this::crearCuenta);
    }

    private void siguiente() {
        String error = modelo.validar(vista.getNombre(), vista.getApellidos(), vista.getCorreo(), vista.getUsuario(),
                vista.getClave(), vista.getClave2());
        if (error != null) {
            vista.mostrarError(error);
            return;
        }
        vista.limpiarMensaje();
        vista.irARegistroFacial();
    }

    // Crea la cuenta sin rostro, se guarda en segundo plano porque usa la base de datos.
    private void crearCuenta() {
        String error = modelo.validar(vista.getNombre(), vista.getApellidos(), vista.getCorreo(), vista.getUsuario(),
                vista.getClave(), vista.getClave2());
        if (error != null) {
            vista.mostrarError(error);
            return;
        }
        vista.limpiarMensaje();
        vista.setOcupado(true);
        new Thread(() -> {
            String falla = modelo.guardarSinRostro();
            SwingUtilities.invokeLater(() -> {
                vista.setOcupado(false);
                if (falla != null) {
                    vista.mostrarError(falla);
                } else {
                    vista.mostrarCuentaCreada();
                }
            });
        }, "crear-cuenta-sin-rostro").start();
    }
}
