package ecos.modelo;

import ecos.app.Sesion; // Aquí se guardan los nombres para la partida.

// Modelo de PantallaReglasClasico: prepara los nombres de los dos jugadores y el texto de la regla del tablero.
// Responsable: Hellen.
public class ModeloReglasClasico {

    private static final String NOMBRE1_POR_DEFECTO = "Jugador 1"; // Si el jugador 1 deja el campo vacío.
    private static final String NOMBRE2_POR_DEFECTO = "Invitado"; // Si el jugador 2 deja el campo vacío.

    public String nombreInicial1() { // Nombre que aparece al entrar en el campo del jugador 1.
        return Sesion.getNombre(); // PENDIENTE (BD): cuando exista la base, aquí va el nombre de usuario de quien inició sesión, ya no "Jugador 1".
    }

    public String nombreInicial2() { // Nombre que aparece al entrar en el campo del jugador 2.
        return NOMBRE2_POR_DEFECTO; // Siempre empieza como invitado.
    }

    public String reglaTablero() { // Primera regla, con el tamaño que se eligió en el menú.
        int n = Sesion.getTamano(); // 4 o 6.
        return "Cada jugador tiene su propio tablero (<b>" + n + "x" + n + "</b>) con las parejas en otro orden.";
    }

    // Guarda los nombres escritos; si alguno está vacío usa el de por defecto.
    public void prepararPartida(String nombre1, String nombre2) {
        String n1 = nombre1.trim(); // Quita espacios de los lados.
        String n2 = nombre2.trim();
        Sesion.setJugador1(n1.isEmpty() ? NOMBRE1_POR_DEFECTO : n1); // Guarda el jugador 1.
        Sesion.setJugador2(n2.isEmpty() ? NOMBRE2_POR_DEFECTO : n2); // Guarda el jugador 2.
    }
}
