package ecos.recursos;

import java.io.BufferedInputStream; // Lee archivos por bloques, lo que hace falta para audio.
import java.io.InputStream; // Flujo para leer los bytes de un archivo.
import javax.sound.sampled.AudioInputStream; // Audio ya interpretado a partir de un archivo WAV.
import javax.sound.sampled.AudioSystem; // Herramienta de Java para abrir y reproducir audio.
import javax.sound.sampled.Clip; // Sonido cargado en memoria que se puede reproducir y repetir.
import javax.sound.sampled.FloatControl; // Control de un valor decimal, aquí el volumen.
import javax.sound.sampled.LineEvent; // Aviso de que el sonido empezó o terminó.

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

    private static void volumen(Clip clip, float decibeles) { // Cambia el volumen de un clip, en decibeles.
        try { // Intenta cambiarlo.
            FloatControl control = (FloatControl) clip.getControl(FloatControl.Type.MASTER_GAIN); // Pide el control de volumen del clip.
            control.setValue(decibeles); // Aplica el volumen.
        } catch (Exception e) { // Si el clip no tiene control de volumen.
            // si no tiene control de volumen, se deja como esta
        }
    }
}