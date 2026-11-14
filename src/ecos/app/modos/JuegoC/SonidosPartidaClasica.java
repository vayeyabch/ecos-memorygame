package ecos.app.modos.JuegoC;

import ecos.recursos.Sonido;

// Todos los sonidos del modo clásico en un solo lugar. Los de par_encontrado, moneda, ojo_espia y victoria los pone
// PantallaClasico.
public class SonidosPartidaClasica {

    public static void voltear() {
        Sonido.efecto("voltear_carta");
    }

    public static void fallo() {
        Sonido.efecto("fallo");
    }

    public static void cambioTurno() {
        Sonido.efecto("cambio_turno");
    }

    public static void tick() {
        Sonido.efecto("tick");
    }

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

    public static void musicaNormal() {
        Sonido.musica("clasico");
    }
}
