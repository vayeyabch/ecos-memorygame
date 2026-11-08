package ecos.modelo;

// Clases del propio proyecto que se usan aquí.
import ecos.app.Sesion;

// Modelo de VentanaLoginJugador2, revisa los datos que escribe el jugador 2 antes de jugar.
// A cargo de Hellen.
public class ModeloLoginJugador2 {

    // Devuelve el mensaje de error, o null si todo está bien.
    public String validar(String usuario, String clave) {
        if (usuario.isEmpty() || clave.isEmpty()) {
            return "Escribí tu usuario y tu contraseña.";
        }
        // No puede ser la misma cuenta del jugador 1.
        if (usuario.equalsIgnoreCase(Sesion.getNombre())) {
            return "Esa cuenta ya es la del jugador 1. Usá otra.";
        }
        // PENDIENTE (BD), aquí se revisa en la base que el usuario exista y la contraseña sea correcta.
        return null;
    }

    // Deja al jugador 2 con su cuenta para la partida.
    public void iniciarSesion(String usuario) {
        Sesion.setJugador2(usuario);
        Sesion.setInvitado2(false);
    }
}
