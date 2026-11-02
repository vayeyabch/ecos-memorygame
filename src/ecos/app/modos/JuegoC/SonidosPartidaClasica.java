package ecos.app.modos.JuegoC;

import ecos.recursos.Sonido;

// Todos los sonidos del modo clásico en un solo lugar.
// (El de "par_encontrado", "moneda", "ojo_espia" y "victoria" ya los pone PantallaClasico.)
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

    // Música rápida cuando quedan 3 segundos.
    public static void musicaUrgente() {
        Sonido.musica("clasico_urgente");
    }

    public static void musicaNormal() {
        Sonido.musica("clasico");
    }
}
