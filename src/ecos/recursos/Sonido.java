package ecos.recursos;

import java.io.BufferedInputStream;
import java.io.InputStream;
import javax.sound.sampled.AudioInputStream;
import javax.sound.sampled.AudioSystem;
import javax.sound.sampled.Clip;
import javax.sound.sampled.FloatControl;
import javax.sound.sampled.LineEvent;

// Reproduce la música de fondo y los efectos de sonido; también silencia o reactiva todo.
public class Sonido {

    // Canción actual, su nombre para no reiniciarla si ya suena, y si el sonido está encendido.
    private static Clip musicaActual = null;
    private static String nombreMusica = null;
    private static boolean activo = true;

    public static final String EXITO = "victoria";

    // Empieza una canción en bucle, por ejemplo "menu".
    public static void musica(String nombre) {
        if (nombre == null) {
            detenerMusica();
            nombreMusica = null;
            return;
        }
        // Si esa misma canción ya suena, no la reinicia.
        if (nombre.equals(nombreMusica) && musicaActual != null && musicaActual.isRunning()) {
            return;
        }
        // Si el sonido está silenciado solo guarda el nombre, por si se reactiva.
        detenerMusica();
        nombreMusica = nombre;
        if (!activo) {
            return;
        }
        // Baja un poco el volumen para que no tape los efectos y la repite sin parar.
        Clip clip = abrir("audio/musica/" + nombre + ".wav");
        if (clip != null) {
            volumen(clip, -6f);
            clip.loop(Clip.LOOP_CONTINUOUSLY);
            musicaActual = clip;
        }
    }

    // Toca un efecto una sola vez, por ejemplo "moneda".
    public static void efecto(String nombre) {
        if (!activo) {
            return;
        }
        // Se queda escuchando para saber cuándo termina.
        Clip clip = abrir("audio/efectos/" + nombre + ".wav");
        if (clip != null) {
            clip.addLineListener(e -> {
                if (e.getType() == LineEvent.Type.STOP) {
                    // Libera su memoria al terminar.
                    clip.close();
                }
            });
            clip.start();
        }
    }

    // Silencia o reactiva todo el sonido y devuelve true si quedó activo.
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

    // Carga un archivo WAV; devuelve null si falla.
    private static Clip abrir(String ruta) {
        try (InputStream in = new BufferedInputStream(Recursos.abrir(ruta));
             AudioInputStream audio = AudioSystem.getAudioInputStream(in)) {
            Clip clip = AudioSystem.getClip();
            clip.open(audio);
            // Si no hay parlantes o falta el archivo avisa en la consola y el juego sigue sin sonido.
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
            // Si no tiene control de volumen se deja igual.
        }
    }
}
