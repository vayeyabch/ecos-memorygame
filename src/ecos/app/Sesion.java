package ecos.app;

public class Sesion {

    private static String nombre = "Jugador 1"; // Nombre de quien inició sesión.
    private static String jugador1 = "Jugador 1"; // Nombre del jugador 1 en la partida clásica.
    private static String jugador2 = "Invitado"; // Nombre del jugador 2 en la partida clásica.
    private static boolean invitado2 = true; // true = el jugador 2 juega sin cuenta (no se guardan monedas ni ranking).
    private static int tamano = 6; // Tamaño del tablero clásico: 4 (4x4) o 6 (6x6).
    private static int mejorNivel = 0; // Mejor nivel alcanzado en el modo patrones.

    public static String getNombre() { return nombre; } // Entrega el nombre de quien inició sesión.
    public static void setNombre(String n) { nombre = n; } // Guarda el nombre de quien inició sesión.
    public static String getJugador1() { return jugador1; } // Entrega el nombre del jugador 1.
    public static void setJugador1(String n) { jugador1 = n; } // Guarda el nombre del jugador 1.
    public static String getJugador2() { return jugador2; } // Entrega el nombre del jugador 2.
    public static void setJugador2(String n) { jugador2 = n; } // Guarda el nombre del jugador 2.
    public static boolean isInvitado2() { return invitado2; } // Dice si el jugador 2 juega sin cuenta.
    public static void setInvitado2(boolean i) { invitado2 = i; } // Guarda si el jugador 2 juega sin cuenta.
    public static int getTamano() { return tamano; } // Entrega el tamaño del tablero (4 o 6).
    public static void setTamano(int t) { tamano = t; } // Guarda el tamaño del tablero (4 o 6).
    public static int getMejorNivel() { return mejorNivel; } // Entrega el mejor nivel de patrones.
    public static void setMejorNivel(int n) { mejorNivel = Math.max(mejorNivel, n); } // Guarda el mejor nivel si supera al anterior.
}
