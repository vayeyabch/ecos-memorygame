package ecos.controlador;

// Clases del propio proyecto que se usan aquí.
import ecos.modelo.ModeloRegistro;
import ecos.pantallas.PantallaRegistro;

// Controlador de PantallaRegistro (paso 1 de 2), al tocar "Siguiente" revisa el formulario con ModeloRegistro y, si está
// bien, pasa al registro del rostro. La cuenta se crea en el paso 2.
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
        // Lo que hace el botón "Siguiente: mi rostro".
        vista.setAccionSiguiente(this::siguiente);
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
}
