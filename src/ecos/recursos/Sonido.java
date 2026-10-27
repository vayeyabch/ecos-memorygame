package ecos.recursos;

import java.io.BufferedInputStream;
import java.io.InputStream;
import javax.sound.sampled.AudioInputStream;
import javax.sound.sampled.AudioSystem;
import javax.sound.sampled.Clip;
import javax.sound.sampled.FloatControl;
import javax.sound.sampled.LineEvent;

/**
 * Musica de fondo y efectos de sonido (archivos WAV en src/recursos/audio).
 *   Sonido.musica("menu")      -> musica en bucle de audio/musica/menu.wav
 *   Sonido.efecto("moneda")    -> suena una vez audio/efectos/moneda.wav
 *   Sonido.alternar()          -> boton de musica: silencia o reactiva todo
 * Si algo falla (sin parlantes, archivo faltante) el juego sigue sin sonido.
 */
public class Sonido {

    private static Clip musicaActual = null;
    private static String nombreMusica = null;
    private static boolean activo = true;

    /** Empieza una cancion en bucle. Si ya esta sonando esa misma, no la reinicia. */
    public static void musica(String nombre) {
        if (nombre == null) {
            detenerMusica();
            nombreMusica = null;
            return;
        }
        if (nombre.equals(nombreMusica) && musicaActual != null && musicaActual.isRunning()) {
            return;
        }
        detenerMusica();
        nombreMusica = nombre;
        if (!activo) {
            return;
        }
        Clip clip = abrir("audio/musica/" + nombre + ".wav");
        if (clip != null) {
            volumen(clip, -6f);
            clip.loop(Clip.LOOP_CONTINUOUSLY);
            musicaActual = clip;
        }
    }

    /** Toca un efecto una sola vez. */
    public static void efecto(String nombre) {
        if (!activo) {
            return;
        }
        Clip clip = abrir("audio/efectos/" + nombre + ".wav");
        if (clip != null) {
            clip.addLineListener(e -> {
                if (e.getType() == LineEvent.Type.STOP) {
                    clip.close();
                }
            });
            clip.start();
        }
    }

    /** Silencia o reactiva todo el sonido. Devuelve true si quedo activo. */
    public static boolean alternar() {
        activo = !activo;
        if (activo) {
            String volver = nombreMusica;
            nombreMusica = null;
            musica(volver);
        } else {
            detenerMusica();
        }
        return activo;
    }

    public static boolean estaActivo() {
        return activo;
    }

    private static void detenerMusica() {
        if (musicaActual != null) {
            musicaActual.stop();
            musicaActual.close();
            musicaActual = null;
        }
    }

    private static Clip abrir(String ruta) {
        try (InputStream in = new BufferedInputStream(Recursos.abrir(ruta));
             AudioInputStream audio = AudioSystem.getAudioInputStream(in)) {
            Clip clip = AudioSystem.getClip();
            clip.open(audio);
            return clip;
        } catch (Exception e) {
            System.err.println("No se pudo reproducir " + ruta + ": " + e.getMessage());
            return null;
        }
    }

    private static void volumen(Clip clip, float decibeles) {
        try {
            FloatControl control = (FloatControl) clip.getControl(FloatControl.Type.MASTER_GAIN);
            control.setValue(decibeles);
        } catch (Exception e) {
            // si no tiene control de volumen, se deja como esta
        }
    }
}
