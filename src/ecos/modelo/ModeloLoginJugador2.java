package ecos.modelo;

// Clases del propio proyecto que se usan aquí.
import ecos.app.Sesion;

// Modelo de VentanaLoginJugador2, revisa los datos que escribe el jugador 2 antes de jugar.
// A cargo de Hellen.
public class ModeloLoginJugador2 {
    private final ModeloAcceso acceso;
    private Usuario encontrado;

    public ModeloLoginJugador2() {
        this(new ModeloAcceso());
    }

    public ModeloLoginJugador2(ModeloAcceso acceso) {
        this.acceso = acceso;
    }




    // Devuelve el mensaje de error, o null si todo está bien.
    public String validar(String usuario, String clave) {
        if (usuario.isEmpty() || clave.isEmpty()) {
            return "Escribí tu usuario y tu contraseña.";
        }
        // No puede ser la misma cuenta del jugador 1.
        if (usuario.equalsIgnoreCase(Sesion.getNombre())) {
            return "Esa cuenta ya es la del jugador 1. Usá otra.";
        }
        try {
            // La cuenta tiene que existir y la contraseña ser la correcta.
            encontrado = acceso.iniciarSesion(usuario, clave);
        } catch (IllegalArgumentException e) {
            return e.getMessage();
        } catch (RuntimeException e) {
            e.printStackTrace();
            return "No se pudo conectar con la base de datos.";
        }
        return null;
    }

    // Deja al jugador 2 con su cuenta para la partida.
    public void iniciarSesion(String usuario) {
        Sesion.setJugador2(usuario);
        Sesion.setInvitado2(false);
    }
}
