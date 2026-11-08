package ecos.modelo;

import ecos.app.Sesion; // Para saber quién es el jugador 1.

// Modelo de VentanaLoginJugador2: revisa los datos que escribe el jugador 2 antes de jugar.
// Responsable: Hellen.
public class ModeloLoginJugador2 {

    // Devuelve el mensaje de error, o null si todo está bien.
    public String validar(String usuario, String clave) {
        if (usuario.isEmpty() || clave.isEmpty()) { // Faltan datos.
            return "Escribí tu usuario y tu contraseña.";
        }
        if (usuario.equalsIgnoreCase(Sesion.getNombre())) { // No puede ser la misma cuenta del jugador 1.
            return "Esa cuenta ya es la del jugador 1. Usá otra.";
        }
        // PENDIENTE (BD): aquí se revisa en la base que el usuario exista y la contraseña sea correcta.
        return null;
    }

    public void iniciarSesion(String usuario) { // Deja al jugador 2 con su cuenta para la partida.
        Sesion.setJugador2(usuario);
        Sesion.setInvitado2(false);
    }
}
