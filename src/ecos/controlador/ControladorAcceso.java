package ecos.controlador;

// Clases del propio proyecto que se usan aquí.
import ecos.app.Sesion;
import ecos.modelo.ModeloAcceso;
import ecos.modelo.Usuario;
import ecos.pantallas.pantallaAcceso;

// Controlador de PantallaAcceso, conecta el botón Entrar con ModeloAcceso. Pendiente, conectar el login con Face ID.
// A cargo de Valeria.
public class ControladorAcceso {

    public ControladorAcceso(pantallaAcceso vista) {
        this(vista, new ModeloAcceso());
    }

    public ControladorAcceso(pantallaAcceso vista, ModeloAcceso modelo) {
        // Qué hacer cuando se aprieta Entrar.
        vista.setAccionEntrar(() -> {
            try {
                Usuario u = modelo.iniciarSesion(vista.getUsuario(), vista.getClave());
                // Guarda quién inició sesión y va al menú.
                Sesion.setNombre(u.getUsuario());
                vista.irAMenu();
            } catch (IllegalArgumentException ex) {
                vista.mostrarError(ex.getMessage());
            } catch (RuntimeException ex) {
                vista.mostrarError("No se pudo conectar con la base de datos.");
                ex.printStackTrace();
            }
        });
    }
}
