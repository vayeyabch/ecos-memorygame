package ecos.app.modos.Patrones;

// Sonido: la clase del equipo que reproduce efectos y música; todos los métodos de aquí le piden a ella que suene algo.
import ecos.recursos.Sonido;

// Sonidos del modo patrones en un solo lugar.
// Es una clase de utilidad: solo tiene métodos static, así que se usa sin crear objetos (SonidosPartidaPatron.fallo()).
// Sirve para que el resto del código no escriba nombres de archivos de sonido sueltos.
public class SonidosPartidaPatron {

    // Suena al empezar el patrón.
    public static void empezar() {
        Sonido.efecto("empezar_patron");
    }

    // Cada casilla tiene su propia nota, patron_casilla_01 a patron_casilla_16.
    // Recibe el número de casilla desde 0 y le suma 1 porque los archivos empiezan en 01.
    // String.format con %02d arma el número con dos dígitos: 1 queda "01", 12 queda "12".
    public static void casilla(int casilla) {
        Sonido.efecto(String.format("patron_casilla_%02d", casilla + 1));
    }

    // Suena cuando el jugador se equivoca.
    public static void fallo() {
        Sonido.efecto("fallo");
    }

    // Suena cuando el jugador pasa de nivel.
    public static void nivelSuperado() {
        Sonido.efecto("nivel_superado");
    }

    // Suena cuando el jugador pierde.
    public static void derrota() {
        Sonido.efecto("derrota");
    }

    // Suena en la cuenta regresiva del inicio.
    public static void cuentaRegresiva() {
        Sonido.efecto("cuenta_regresiva");
    }

    // Suena cada segundo del reloj.
    public static void tick() {
        Sonido.efecto("tick");
    }
}