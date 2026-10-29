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

<<<<<<< HEAD
<<<<<<< HEAD
import java.io.BufferedInputStream; // Envuelve un flujo para que se lea más rápido y permita "volver atrás" (lo necesita el audio).
import java.io.InputStream; // Flujo de lectura con el que se abre el archivo de audio.
import javax.sound.sampled.AudioInputStream; // Flujo de audio ya interpretado (sabe su formato).
import javax.sound.sampled.AudioSystem; // Punto de entrada de Java para leer audio y crear Clips.
import javax.sound.sampled.Clip; // Un sonido cargado en memoria que se puede reproducir, repetir y parar.
import javax.sound.sampled.FloatControl; // Control con valores decimales, aquí se usa para el volumen.
import javax.sound.sampled.LineEvent; // Evento que avisa cuando un sonido empieza o termina.
>>>>>>> origin/main
=======
import java.io.BufferedInputStream;
import java.io.InputStream;
import javax.sound.sampled.AudioInputStream;
import javax.sound.sampled.AudioSystem;
import javax.sound.sampled.Clip;
import javax.sound.sampled.FloatControl;
import javax.sound.sampled.LineEvent;
>>>>>>> origin/main
=======
import java.io.BufferedInputStream; // Lee archivos por bloques, lo que hace falta para audio.
import java.io.InputStream; // Flujo para leer los bytes de un archivo.
import javax.sound.sampled.AudioInputStream; // Audio ya interpretado a partir de un archivo WAV.
import javax.sound.sampled.AudioSystem; // Herramienta de Java para abrir y reproducir audio.
import javax.sound.sampled.Clip; // Sonido cargado en memoria que se puede reproducir y repetir.
import javax.sound.sampled.FloatControl; // Control de un valor decimal, aquí el volumen.
import javax.sound.sampled.LineEvent; // Aviso de que el sonido empezó o terminó.
>>>>>>> origin/main

public class Sonido {

    private static Clip musicaActual = null; // La canción que está sonando ahora (null si ninguna).
    private static String nombreMusica = null; // Nombre de esa canción, para no reiniciarla si ya suena.
    private static boolean activo = true; // true = el sonido está encendido; false = silenciado.

    public static void musica(String nombre) { // Empieza una canción en bucle; recibe su nombre (por ejemplo "menu").
        if (nombre == null) { // Si no se pide ninguna canción.
            detenerMusica(); // Para la que esté sonando.
            nombreMusica = null; // Olvida el nombre.
            return; // Termina.
        }
        if (nombre.equals(nombreMusica) && musicaActual != null && musicaActual.isRunning()) { // Si esa misma canción ya está sonando.
            return; // No la reinicia.
        }
        detenerMusica(); // Para la canción anterior.
        nombreMusica = nombre; // Guarda el nombre de la nueva canción.
        if (!activo) { // Si el sonido está silenciado.
            return; // No la reproduce (queda guardada por si se reactiva).
        }
        Clip clip = abrir("audio/musica/" + nombre + ".wav"); // Carga el archivo de la canción.
        if (clip != null) { // Si se pudo cargar.
            volumen(clip, -6f); // Baja un poco el volumen para que no tape los efectos.
            clip.loop(Clip.LOOP_CONTINUOUSLY); // La repite sin parar.
            musicaActual = clip; // Guarda la canción que quedó sonando.
        }
    }

    public static void efecto(String nombre) { // Toca un efecto una sola vez; recibe su nombre (por ejemplo "moneda").
        if (!activo) { // Si el sonido está silenciado.
            return; // No suena nada.
        }
        Clip clip = abrir("audio/efectos/" + nombre + ".wav"); // Carga el archivo del efecto.
        if (clip != null) { // Si se pudo cargar.
            clip.addLineListener(e -> { // Se queda escuchando lo que le pasa al sonido.
                if (e.getType() == LineEvent.Type.STOP) { // Cuando el efecto termina.
                    clip.close(); // Libera su memoria.
                }
            });
            clip.start(); // Lo reproduce.
        }
    }

    public static boolean alternar() { // Silencia o reactiva todo el sonido; devuelve true si quedó activo.
        activo = !activo; // Invierte el estado.
        if (activo) { // Si se reactivó.
            String volver = nombreMusica; // Recuerda la canción que debía sonar.
            nombreMusica = null; // La olvida un momento para que se vuelva a iniciar.
            musica(volver); // La vuelve a poner.
        } else { // Si se silenció.
            detenerMusica(); // Para la música.
        }
        return activo; // Devuelve cómo quedó.
    }

    public static boolean estaActivo() { // Pregunta si el sonido está encendido.
        return activo; // Devuelve true si está encendido.
    }

    private static void detenerMusica() { // Para la canción actual y libera su memoria.
        if (musicaActual != null) { // Solo si hay una sonando.
            musicaActual.stop(); // La detiene.
            musicaActual.close(); // Libera la memoria.
            musicaActual = null; // Ya no hay canción actual.
        }
    }

    private static Clip abrir(String ruta) { // Carga un archivo WAV y lo deja listo; devuelve null si falla.
        try (InputStream in = new BufferedInputStream(Recursos.abrir(ruta)); // Abre el archivo (se cierra solo).
             AudioInputStream audio = AudioSystem.getAudioInputStream(in)) { // Interpreta el audio del archivo.
            Clip clip = AudioSystem.getClip(); // Pide un clip vacío al sistema de audio.
            clip.open(audio); // Carga el audio en el clip.
            return clip; // Entrega el clip listo.
        } catch (Exception e) { // Si no hay parlantes o el archivo falta.
            System.err.println("No se pudo reproducir " + ruta + ": " + e.getMessage()); // Avisa en la consola, pero el juego sigue sin sonido.
            return null; // No hay clip.
        }
    }

<<<<<<< HEAD
    private static void volumen(Clip clip, float decibeles) {
        try {
<<<<<<< HEAD
<<<<<<< HEAD
=======
>>>>>>> origin/main
            FloatControl control = (FloatControl) clip.getControl(FloatControl.Type.MASTER_GAIN);
            control.setValue(decibeles);
        } catch (Exception e) {
=======
    private static void volumen(Clip clip, float decibeles) { // Cambia el volumen de un clip, en decibeles.
        try { // Intenta cambiarlo.
            FloatControl control = (FloatControl) clip.getControl(FloatControl.Type.MASTER_GAIN); // Pide el control de volumen del clip.
            control.setValue(decibeles); // Aplica el volumen.
        } catch (Exception e) { // Si el clip no tiene control de volumen.
>>>>>>> origin/main
            // si no tiene control de volumen, se deja como esta
<<<<<<< HEAD
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
=======
>>>>>>> origin/main
        }
    }
}