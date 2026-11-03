package ecos.app.modos.Patrones;

import ecos.recursos.Sonido;

// Todos los sonidos del modo patrones en un solo lugar.
public class SonidosPartidaPatron {

    public static void empezar() {
        Sonido.efecto("empezar_patron");
    }

    // Cada casilla tiene su propia nota: patron_casilla_01 ... patron_casilla_16.
    public static void casilla(int casilla) {
        Sonido.efecto(String.format("patron_casilla_%02d", casilla + 1));
    }

    public static void fallo() {
        Sonido.efecto("fallo");
    }

    public static void nivelSuperado() {
        Sonido.efecto("nivel_superado");
    }

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
