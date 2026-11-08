package ecos.recursos;

// Lectura de archivos, BufferedInputStream para leer más rápido y InputStream para leer archivos.
import java.io.BufferedInputStream;
import java.io.InputStream;
// Librería de sonido de Java, AudioInputStream para leer el archivo de audio, AudioSystem para cargar el audio, Clip
// para reproducir el sonido, FloatControl para el volumen y LineEvent para avisar cuando termina un sonido.
import javax.sound.sampled.AudioInputStream;
import javax.sound.sampled.AudioSystem;
import javax.sound.sampled.Clip;
import javax.sound.sampled.FloatControl;
import javax.sound.sampled.LineEvent;

// Reproduce la música de fondo y los efectos de sonido; también silencia o reactiva todo.
public class Sonido {

    // La canción que está sonando ahora (null si ninguna), su nombre para no reiniciarla si ya suena y si el sonido está
    // encendido o silenciado.
    private static Clip musicaActual = null;
    private static String nombreMusica = null;
    private static boolean activo = true;

    // Empieza una canción en bucle; recibe su nombre (por ejemplo "menu").
    public static void musica(String nombre) {
        // Si no se pide ninguna canción. Para la que esté sonando. Olvida el nombre.
        if (nombre == null) {
            detenerMusica();
            nombreMusica = null;
            return;
        }
        // Si esa misma canción ya está sonando. No la reinicia.
        if (nombre.equals(nombreMusica) && musicaActual != null && musicaActual.isRunning()) {
            return;
        }
        // Para la canción anterior. Guarda el nombre de la nueva canción. Si el sonido está silenciado. No la reproduce
        // (queda guardada por si se reactiva).
        detenerMusica();
        nombreMusica = nombre;
        if (!activo) {
            return;
        }
        // Carga el archivo de la canción y, si se pudo, baja un poco el volumen para que no tape los efectos y la repite sin
        // parar.
        Clip clip = abrir("audio/musica/" + nombre + ".wav");
        if (clip != null) {
            volumen(clip, -6f);
            clip.loop(Clip.LOOP_CONTINUOUSLY);
            // Guarda la canción que quedó sonando.
            musicaActual = clip;
        }
    }

    // Toca un efecto una sola vez; recibe su nombre (por ejemplo "moneda").
    public static void efecto(String nombre) {
        // Si el sonido está silenciado. No suena nada.
        if (!activo) {
            return;
        }
        // Carga el archivo del efecto y, si se pudo, se queda escuchando para saber cuándo termina.
        Clip clip = abrir("audio/efectos/" + nombre + ".wav");
        if (clip != null) {
            clip.addLineListener(e -> {
                if (e.getType() == LineEvent.Type.STOP) {
                    // Libera su memoria.
                    clip.close();
                }
            });
            clip.start();
        }
    }

    // Silencia o reactiva todo el sonido; devuelve true si quedó activo.
    public static boolean alternar() {
        // Invierte el estado. Si se reactivó. Recuerda la canción que debía sonar. La olvida un momento para que se vuelva a iniciar.
        activo = !activo;
        if (activo) {
            String volver = nombreMusica;
            nombreMusica = null;
            // La vuelve a poner. Si se silenció. Para la música.
            musica(volver);
        } else {
            detenerMusica();
        }
        // Devuelve cómo quedó.
        return activo;
    }

    // Pregunta si el sonido está encendido.
    public static boolean estaActivo() {
        // Devuelve true si está encendido.
        return activo;
    }

    // Para la canción actual y libera su memoria.
    private static void detenerMusica() {
        // Solo si hay una sonando. Libera la memoria. Ya no hay canción actual.
        if (musicaActual != null) {
            musicaActual.stop();
            musicaActual.close();
            musicaActual = null;
        }
    }

    // Carga un archivo WAV y lo deja listo; devuelve null si falla.
    private static Clip abrir(String ruta) {
        // Abre el archivo (se cierra solo). Interpreta el audio del archivo. Pide un clip vacío al sistema de audio. Carga el
        // audio en el clip.
        try (InputStream in = new BufferedInputStream(Recursos.abrir(ruta));
             AudioInputStream audio = AudioSystem.getAudioInputStream(in)) {
            Clip clip = AudioSystem.getClip();
            clip.open(audio);
            // Entrega el clip listo. Si no hay parlantes o el archivo falta. Avisa en la consola, pero el juego sigue sin sonido.
            return clip;
        } catch (Exception e) {
            System.err.println("No se pudo reproducir " + ruta + ": " + e.getMessage());
            return null;
        }
    }

    // Cambia el volumen de un clip, en decibeles.
    private static void volumen(Clip clip, float decibeles) {
        // Intenta cambiarlo. Pide el control de volumen del clip. Aplica el volumen. Si el clip no tiene control de volumen.
        try {
            FloatControl control = (FloatControl) clip.getControl(FloatControl.Type.MASTER_GAIN);
            control.setValue(decibeles);
        } catch (Exception e) {
            // si no tiene control de volumen, se deja como esta
        }
    }
}