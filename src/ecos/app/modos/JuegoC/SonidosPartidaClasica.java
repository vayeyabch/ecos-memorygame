package ecos.app.modos.JuegoC;

// Sonido: la clase del equipo que reproduce efectos y música; todos los métodos de aquí le piden a ella que suene algo.
import ecos.recursos.Sonido;

// Todos los sonidos del modo clásico en un solo lugar. Los de par_encontrado, moneda, ojo_espia y victoria los pone
// PantallaClasico.
// Es una clase de utilidad: solo tiene métodos static, así que se usa sin crear objetos (SonidosPartidaClasica.voltear()).
// Sirve para que el resto del código no escriba nombres de archivos de sonido sueltos, y si uno cambia, se cambia solo aquí.
public class SonidosPartidaClasica {

    // Suena al voltear una carta.
    public static void voltear() {
        Sonido.efecto("voltear_carta");
    }

    // Suena cuando las dos cartas volteadas no son pareja.
    public static void fallo() {
        Sonido.efecto("fallo");
    }

    // Suena cuando pasa el turno al otro jugador.
    public static void cambioTurno() {
        Sonido.efecto("cambio_turno");
    }

    // Suena cada segundo del reloj de la partida.
    public static void tick() {
        Sonido.efecto("tick");
    }

    // Suena en la cuenta regresiva del inicio.
    public static void cuentaRegresiva() {
        Sonido.efecto("cuenta_regresiva");
    }

    // Aviso de poco tiempo, suena una vez cuando entra la música rápida.
    public static void alertaTiempo() {
        Sonido.efecto("alerta_tiempo");
    }

    // Suena al acertar dos o más parejas seguidas.
    public static void racha() {
        Sonido.efecto("racha");
    }

    // Cambia a la música más rápida cuando quedan pocos segundos.
    public static void musicaUrgente() {
        Sonido.musica("clasico_urgente");
    }

    // Vuelve a la música normal del modo clásico.
    public static void musicaNormal() {
        Sonido.musica("clasico");
    }
}