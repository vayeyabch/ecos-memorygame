package ecos.app.modos.Patrones;

// Clases del propio proyecto que se usan aquí.
import ecos.recursos.Sonido;

// Todos los sonidos del modo patrones en un solo lugar.
public class SonidosPartidaPatron {

    // Sonidos del inicio y de cada casilla.
    public static void empezar() {
        Sonido.efecto("empezar_patron");
    }

    // Cada casilla tiene su propia nota, patron_casilla_01 ... patron_casilla_16.
    public static void casilla(int casilla) {
        Sonido.efecto(String.format("patron_casilla_%02d", casilla + 1));
    }

    // Sonidos de cuando se falla o se pasa de nivel.
    public static void fallo() {
        Sonido.efecto("fallo");
    }

    public static void nivelSuperado() {
        Sonido.efecto("nivel_superado");
    }

    // Sonidos de la derrota y del tiempo.
    public static void derrota() {
        Sonido.efecto("derrota");
    }

    public static void cuentaRegresiva() {
        Sonido.efecto("cuenta_regresiva");
    }

    public static void tick() {
        Sonido.efecto("tick");
    }
}
