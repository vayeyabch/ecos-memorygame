package ecos.recursos;

import java.io.BufferedInputStream;
import java.io.InputStream;
import javax.sound.sampled.AudioInputStream;
import javax.sound.sampled.AudioSystem;
import javax.sound.sampled.Clip;
<<<<<<< Updated upstream
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
=======
import javax.sound.sampled.LineEvent;

/**
 * Sonidos del juego (archivos WAV en /recursos/audio/).
 *
 *   Sonido.efecto("click");   // toca  /recursos/audio/efectos/click.wav  (una vez)
 *   Sonido.musica("menu");    // toca  /recursos/audio/musica/menu.wav   (en bucle)
 *   Sonido.alternarMusica();  // boton de musica de la barra superior
 *
 * Si el computador no tiene salida de audio, no pasa nada: simplemente no suena.
 */
public class Sonido {
    private static boolean musicaActiva = true;
    private static Clip clipMusica;
    private static String musicaActual;          // nombre de la musica que "deberia" sonar

    /** Toca un efecto una vez. El nombre va sin ".wav", por ejemplo "click" o "teclear". */
    public static void efecto(String nombre) {
        try {
            Clip clip = abrir("/recursos/audio/efectos/" + nombre + ".wav");
            if (clip == null) {
                return;
            }
            clip.addLineListener(e -> {
                if (e.getType() == LineEvent.Type.STOP) {
                    clip.close();                // libera el sonido cuando termina
                }
            });
            clip.start();
        } catch (Exception e) {
            // sin audio disponible: se ignora
        }
    }

    /** Cambia la musica de fondo. Si ya esta sonando esa misma, no la reinicia. */
    public static void musica(String nombre) {
        if (nombre.equals(musicaActual) && clipMusica != null) {
            return;
        }
        musicaActual = nombre;
        detenerClip();
        if (musicaActiva) {
            reproducirMusica();
        }
    }

    /** Activa o apaga la musica (los efectos siguen sonando). */
    public static void alternarMusica() {
        musicaActiva = !musicaActiva;
        if (musicaActiva) {
            reproducirMusica();
        } else {
            detenerClip();
        }
    }

    public static boolean estaMusicaActiva() {
        return musicaActiva;
    }

    private static void reproducirMusica() {
        if (musicaActual == null) {
            return;
        }
        try {
            clipMusica = abrir("/recursos/audio/musica/" + musicaActual + ".wav");
            if (clipMusica != null) {
                clipMusica.loop(Clip.LOOP_CONTINUOUSLY);
            }
        } catch (Exception e) {
            clipMusica = null;
        }
    }

    private static void detenerClip() {
        if (clipMusica != null) {
            clipMusica.stop();
            clipMusica.close();
            clipMusica = null;
        }
    }

    private static Clip abrir(String ruta) throws Exception {
        InputStream in = Sonido.class.getResourceAsStream(ruta);
        if (in == null) {
            return null;                          // el archivo no existe
        }
        try (AudioInputStream audio = AudioSystem.getAudioInputStream(new BufferedInputStream(in))) {
            Clip clip = AudioSystem.getClip();
            clip.open(audio);
            return clip;
>>>>>>> Stashed changes
        }
    }
}
