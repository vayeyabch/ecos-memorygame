package ecos.app;

// Datos que se comparten entre pantallas mientras el programa está abierto: quién inició sesión, los jugadores de la
// partida y el tamaño del tablero.
public class Sesion {

    // Nombre de quien inició sesión y nombres de los jugadores 1 y 2 del modo clásico. Si el jugador 2 juega sin cuenta
    // no se guardan monedas ni ranking.
    private static String nombre = "Jugador 1";
    private static String jugador1 = "Jugador 1";
    private static String jugador2 = "Invitado";
    private static boolean invitado2 = true;
    // Tamaño del tablero clásico, 4 (4x4) o 6 (6x6).
    private static int tamano = 6;

    public static String getNombre() { return nombre; }
    public static void setNombre(String n) { nombre = n; }
    public static String getJugador1() { return jugador1; }
    public static void setJugador1(String n) { jugador1 = n; }
    public static String getJugador2() { return jugador2; }
    public static void setJugador2(String n) { jugador2 = n; }
    public static boolean isInvitado2() { return invitado2; }
    public static void setInvitado2(boolean i) { invitado2 = i; }
    // Tamaño del tablero y mejor nivel de patrones, que solo se guarda si supera al anterior.
    public static int getTamano() { return tamano; }
    public static void setTamano(int t) { tamano = t; }
}
