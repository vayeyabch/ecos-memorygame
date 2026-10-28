package ecos.recursos;
<<<<<<< HEAD
import java.io.BufferedInputStream;
import java.io.InputStream;
import javax.sound.sampled.AudioInputStream;
import javax.sound.sampled.AudioSystem;
import javax.sound.sampled.Clip;
<<<<<<< Updated upstream
import javax.sound.sampled.FloatControl;
import javax.sound.sampled.LineEvent;
=======

import java.io.BufferedInputStream; // Envuelve un flujo para que se lea más rápido y permita "volver atrás" (lo necesita el audio).
import java.io.InputStream; // Flujo de lectura con el que se abre el archivo de audio.
import javax.sound.sampled.AudioInputStream; // Flujo de audio ya interpretado (sabe su formato).
import javax.sound.sampled.AudioSystem; // Punto de entrada de Java para leer audio y crear Clips.
import javax.sound.sampled.Clip; // Un sonido cargado en memoria que se puede reproducir, repetir y parar.
import javax.sound.sampled.FloatControl; // Control con valores decimales, aquí se usa para el volumen.
import javax.sound.sampled.LineEvent; // Evento que avisa cuando un sonido empieza o termina.
>>>>>>> origin/main

public class Sonido {

    private static Clip musicaActual = null; // La canción que está sonando ahora (null = ninguna).
    private static String nombreMusica = null; // Nombre de la canción actual; sirve para recordarla aunque esté silenciada.
    private static boolean activo = true; // true = el sonido está encendido; false = silenciado.

    // Empieza una canción en bucle. Si ya está sonando esa misma, no la reinicia.
    public static void musica(String nombre) { // Pide una canción por su nombre.
        if (nombre == null) { // Si el nombre es null, significa "que no haya música".
            detenerMusica(); // Para la canción actual.
            nombreMusica = null; // Olvida cuál era.
            return; // Termina el método.
        }
        if (nombre.equals(nombreMusica) && musicaActual != null && musicaActual.isRunning()) { // Si ya está sonando esa misma canción
            return; // no hace nada, para que no se reinicie al cambiar de pantalla.
        }
        detenerMusica(); // Para la canción anterior antes de poner la nueva.
        nombreMusica = nombre; // Recuerda el nombre de la nueva canción.
        if (!activo) { // Si el sonido está silenciado
            return; // no la reproduce (pero queda recordada por si se reactiva).
        }
        Clip clip = abrir("audio/musica/" + nombre + ".wav"); // Carga el archivo .wav de la canción.
        if (clip != null) { // Si se cargó bien (no falló)
            volumen(clip, -6f); // baja un poco el volumen (-6 dB) para que no sea muy fuerte.
            clip.loop(Clip.LOOP_CONTINUOUSLY); // La repite sin parar.
            musicaActual = clip; // Guarda esta canción como la actual.
        }
    }

    // Toca un efecto una sola vez.
    public static void efecto(String nombre) { // Pide un efecto por su nombre (ej: "click").
        if (!activo) { // Si el sonido está silenciado
            return; // no suena nada.
        }
        Clip clip = abrir("audio/efectos/" + nombre + ".wav"); // Carga el archivo .wav del efecto.
        if (clip != null) { // Si se cargó bien
            clip.addLineListener(e -> { // queda atento a los eventos de ese sonido.
                if (e.getType() == LineEvent.Type.STOP) { // Cuando el efecto termina
                    clip.close(); // libera la memoria que usaba.
                }
            });
            clip.start(); // Reproduce el efecto una vez.
        }
    }

    // Silencia o reactiva todo el sonido. Devuelve true si quedó activo.
    public static boolean alternar() { // Es lo que usa el botón de música.
        activo = !activo; // Invierte el estado: si estaba encendido lo apaga, y al revés.
        if (activo) { // Si ahora quedó encendido
            String volver = nombreMusica; // guarda el nombre de la canción que estaba antes.
            nombreMusica = null; // Lo borra para que musica() no crea que ya está sonando.
            musica(volver); // Vuelve a poner esa canción.
        } else { // Si ahora quedó apagado
            detenerMusica(); // para la música.
        }
        return activo; // Devuelve el estado final (true = encendido).
    }

    public static boolean estaActivo() { // Pregunta si el sonido está encendido.
        return activo; // Devuelve true o false.
    }

    private static void detenerMusica() { // Método interno: para y libera la música actual.
        if (musicaActual != null) { // Solo si hay una canción sonando.
            musicaActual.stop(); // La detiene.
            musicaActual.close(); // Libera la memoria y la salida de audio.
            musicaActual = null; // Queda sin canción actual.
        }
    }

    private static Clip abrir(String ruta) { // Método interno: carga un .wav y lo deja listo para reproducir.
        try (InputStream in = new BufferedInputStream(Recursos.abrir(ruta)); // Abre el archivo con la clase Recursos y lo envuelve en un BufferedInputStream.
             AudioInputStream audio = AudioSystem.getAudioInputStream(in)) { // Convierte el archivo en un flujo de audio que Java entiende.
            Clip clip = AudioSystem.getClip(); // Pide a Java un Clip vacío (reproductor).
            clip.open(audio); // Carga el audio dentro del Clip.
            return clip; // Devuelve el Clip listo para usar.
        } catch (Exception e) { // Si algo falla (sin parlantes, archivo faltante o formato malo)
            System.err.println("No se pudo reproducir " + ruta + ": " + e.getMessage()); // avisa en la consola, sin romper el juego.
            return null; // Devuelve null para que quien lo llamó sepa que no hay sonido.
        }
    }

    private static void volumen(Clip clip, float decibeles) { // Método interno: cambia el volumen de un Clip.
        try {
<<<<<<< HEAD
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
=======
            FloatControl control = (FloatControl) clip.getControl(FloatControl.Type.MASTER_GAIN); // Pide el control de volumen general del Clip.
            control.setValue(decibeles); // Pone el volumen en decibeles (negativo = más bajo).
        } catch (Exception e) { // Si ese Clip no tiene control de volumen
            // si no tiene control de volumen, se deja como está
>>>>>>> origin/main
        }
    }
}