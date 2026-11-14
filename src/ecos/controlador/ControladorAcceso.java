package ecos.controlador;

import ecos.app.Sesion;
import ecos.modelo.ModeloAcceso;
import ecos.modelo.ModeloRegistroFacial;
import ecos.modelo.Usuario;
import ecos.pantallas.PantallaAcceso;
import ecos.recursos.Sonido;

// Controlador de PantallaAcceso: conecta el botón Entrar con ModeloAcceso y los botones de Face ID y de registrar el
// rostro.
public class ControladorAcceso {

    public ControladorAcceso(PantallaAcceso vista) {
        this(vista, new ModeloAcceso());
    }

    public ControladorAcceso(PantallaAcceso vista, ModeloAcceso modelo) {
        // Face ID: si escribió su usuario y la cuenta no tiene rostro, avisa y ofrece registrarlo; si no, va al escaneo.
        vista.setAccionFaceId(() -> {
            vista.limpiarMensaje();
            String usuario = vista.getUsuario();
            if (usuario.isEmpty()) {
                vista.irALoginFacial();
                return;
            }
            try {
                if (modelo.tieneRostro(usuario)) {
                    vista.irALoginFacial();
                } else {
                    Sonido.efecto("error");
                    vista.mostrarError("Esta cuenta no tiene rostro registrado.");
                    vista.mostrarOpcionRostro(true);
                }
            } catch (IllegalArgumentException ex) {
                vista.mostrarError(ex.getMessage());
            } catch (RuntimeException ex) {
                vista.mostrarError("No se pudo conectar con la base de datos.");
                ex.printStackTrace();
            }
        });

        // Registrar el rostro de una cuenta que ya existe: pide la contraseña para comprobar que es suya.
        vista.setAccionRegistrarRostro(() -> {
            try {
                Usuario u = modelo.iniciarSesion(vista.getUsuario(), vista.getClave());
                ModeloRegistroFacial.paraCuenta(u);
                vista.irARegistroFacial();
            } catch (IllegalArgumentException ex) {
                vista.mostrarError(ex.getMessage());
            } catch (RuntimeException ex) {
                vista.mostrarError("No se pudo conectar con la base de datos.");
                ex.printStackTrace();
            }
        });

        vista.setAccionEntrar(() -> {
            try {
                Usuario u = modelo.iniciarSesion(vista.getUsuario(), vista.getClave());
                // Guarda quién inició sesión y va al menú.
                Sesion.setNombre(u.getUsuario());
                Sonido.efecto(Sonido.EXITO);
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
