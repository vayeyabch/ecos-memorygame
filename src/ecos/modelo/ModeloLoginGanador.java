package ecos.modelo;

// Modelo de VentanaLoginGanador: revisa el usuario y la contraseña del jugador 2 y le suma los puntos de la partida.
// PENDIENTE: conectar con PostgreSQL. Por ahora no guarda nada, solo deja ver cómo funcionan las ventanas.
public class ModeloLoginGanador {

    // Dice si el usuario existe y la contraseña coincide.
    // TODO (PostgreSQL): buscar la cuenta en la base de datos. Por ahora acepta cualquier usuario y contraseña escritos.
    public boolean validar(String usuario, String clave) {
        return true;
    }

    // Suma los puntos a la cuenta.
    // TODO (PostgreSQL): UPDATE de los puntos del usuario. Por ahora no hace nada.
    public void sumarPuntos(String usuario, int puntos) {
    }
}
