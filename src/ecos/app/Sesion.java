package ecos.app;

import java.util.Locale; // Para escribir los números con el formato de Costa Rica.

public class Sesion {

    public static final double CAMBIO_DOLAR = 487.20; // Dólar BCCR (venta): valor de ejemplo hasta que se consulte el API del BCCR.
    public static final int[] PUNTAJES = {21, 24, 27, 31, 36}; // Intentos de los 5 mejores jugadores (datos de ejemplo).
    public static final String[] NOMBRES = {"Valeria", "Mateo", "Sofía", "Dani", "Kenneth"}; // Nombres de los 5 mejores jugadores (datos de ejemplo).

    private static String nombre = "Jugador 1"; // Nombre de quien inició sesión.
    private static String jugador1 = "Jugador 1"; // Nombre del jugador 1 en la partida clásica.
    private static String jugador2 = "Invitado"; // Nombre del jugador 2 en la partida clásica.
    private static int tamano = 6; // Tamaño del tablero clásico: 4 (4x4) o 6 (6x6).
    private static int mejorNivel = 0; // Mejor nivel alcanzado en el modo patrones.

    public static String getNombre() { return nombre; } // Entrega el nombre de quien inició sesión.
    public static void setNombre(String n) { nombre = n; } // Guarda el nombre de quien inició sesión.
    public static String getJugador1() { return jugador1; } // Entrega el nombre del jugador 1.
    public static void setJugador1(String n) { jugador1 = n; } // Guarda el nombre del jugador 1.
    public static String getJugador2() { return jugador2; } // Entrega el nombre del jugador 2.
    public static void setJugador2(String n) { jugador2 = n; } // Guarda el nombre del jugador 2.
    public static int getTamano() { return tamano; } // Entrega el tamaño del tablero (4 o 6).
    public static void setTamano(int t) { tamano = t; } // Guarda el tamaño del tablero (4 o 6).
    public static int getMejorNivel() { return mejorNivel; } // Entrega el mejor nivel de patrones.
    public static void setMejorNivel(int n) { mejorNivel = Math.max(mejorNivel, n); } // Guarda el mejor nivel si supera al anterior.

    // Premio en colones según la fórmula del diseño: 1 / puntaje x 100 x cambio del dólar.
    public static double premioColones(int intentos) { // Recibe los intentos (el puntaje).
        return 1.0 / Math.max(1, intentos) * 100 * CAMBIO_DOLAR; // Aplica la fórmula.
    }

    // Premio en dólares: 1 / puntaje x 100.
    public static double premioDolares(int intentos) { // Recibe los intentos (el puntaje).
        return 1.0 / Math.max(1, intentos) * 100; // Aplica la fórmula sin el cambio.
    }

    // Texto de colones con separador de miles, por ejemplo "₡ 2 320".
    public static String colones(double monto) { // Recibe el monto en colones.
        return "₡ " + String.format(Locale.US, "%,d", Math.round(monto)).replace(',', ' '); // Redondea y cambia las comas por espacios.
    }

    // Texto del premio completo, por ejemplo "₡ 2 320 ($4.76)".
    public static String textoPremio(int intentos) { // Recibe los intentos.
        return colones(premioColones(intentos)) + " ($" + String.format(Locale.US, "%.2f", premioDolares(intentos)) + ")"; // Junta colones y dólares.
    }
}
