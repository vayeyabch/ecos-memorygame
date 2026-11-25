package ecos.recursos;

// BufferedInputStream envuelve el archivo y le agrega un búfer. Java lo necesita para poder reconocer el formato del audio.
import java.io.BufferedInputStream;
// InputStream es de donde se leen los bytes del archivo que abre Recursos.
import java.io.InputStream;
// AudioInputStream es el audio ya reconocido como sonido, listo para cargarse en un Clip.
import javax.sound.sampled.AudioInputStream;
// AudioSystem es la puerta de Java al sonido. Lee el archivo WAV y entrega el Clip.
import javax.sound.sampled.AudioSystem;
// Clip es un sonido cargado en memoria. Se puede iniciar, detener, repetir y cerrar.
import javax.sound.sampled.Clip;
// FloatControl es un control con valor decimal. Aquí se usa para bajar el volumen.
import javax.sound.sampled.FloatControl;
// LineEvent es el aviso que manda el Clip cuando pasa algo, como empezar o terminar. Se usa para saber cuándo acabó un efecto.
import javax.sound.sampled.LineEvent;

// Es el responsable de todo el audio del juego: la música de fondo, los efectos, y silenciar o reactivar todo.
// Todo es static, así que se usa escribiendo Sonido.efecto("moneda"), sin crear un objeto.
public class Sonido {

    // La canción que suena ahora. Empieza en null porque todavía no suena ninguna.
    private static Clip musicaActual = null;
    // El nombre de esa canción. Sirve para no reiniciarla si se pide la misma, y para volver a ponerla al reactivar el sonido.
    private static String nombreMusica = null;
    // Dice si el sonido está encendido. Empieza en true.
    private static boolean activo = true;

    // Nombre del efecto de victoria, para que otras clases no escriban el texto suelto.
    public static final String EXITO = "victoria";

    // Pone una canción en bucle, por ejemplo "menu". Lo llama Pantalla.alEntrar con la música de cada pantalla.
    // Resuelve que cada pantalla tenga su música sin que se reinicie al volver a pedir la misma.
    public static void musica(String nombre) {
        // Si no se pide ninguna canción, detiene la que suene y olvida el nombre. El return termina el método aquí.
        if (nombre == null) {
            detenerMusica();
            nombreMusica = null;
            return;
        }
        // Si esa misma canción ya suena, no la reinicia. El && significa y, y las tres condiciones deben cumplirse.
        if (nombre.equals(nombreMusica) && musicaActual != null && musicaActual.isRunning()) {
            return;
        }
        // Detiene la canción anterior y guarda el nombre de la nueva.
        detenerMusica();
        nombreMusica = nombre;
        // Si el sonido está silenciado, solo guarda el nombre por si se reactiva después. El ! significa no.
        if (!activo) {
            return;
        }
        // Abre el archivo de la canción. Si falló, abrir devuelve null y no se hace nada.
        Clip clip = abrir("audio/musica/" + nombre + ".wav");
        if (clip != null) {
            // Baja un poco el volumen para que la música no tape los efectos.
            volumen(clip, -6f);
            // La repite sin parar hasta que alguien la detenga.
            clip.loop(Clip.LOOP_CONTINUOUSLY);
            musicaActual = clip;
        }
    }

    // Toca un efecto una sola vez, por ejemplo "moneda". Lo llaman los botones y las pantallas de juego.
    public static void efecto(String nombre) {
        // Si el sonido está silenciado, no hace nada.
        if (!activo) {
            return;
        }
        Clip clip = abrir("audio/efectos/" + nombre + ".wav");
        if (clip != null) {
            // Deja una función escuchando los avisos del clip. La flecha es esa función, y e es el aviso que llega.
            clip.addLineListener(e -> {
                // Cuando el aviso es STOP, el efecto terminó.
                if (e.getType() == LineEvent.Type.STOP) {
                    // Lo cierra para liberar su memoria. Sin esto, cada efecto quedaría guardado en memoria.
                    clip.close();
                }
            });
            // Empieza el efecto. Crear el clip no lo toca, start es lo que lo pone a sonar.
            clip.start();
        }
    }

    // Silencia o reactiva todo el sonido y devuelve true si quedó activo. Lo llama el botón de música de la barra superior.
    public static boolean alternar() {
        // Invierte el valor. Si estaba encendido pasa a apagado y al revés.
        activo = !activo;
        if (activo) {
            // Al reactivar, vuelve a poner la canción que sonaba. Se borra el nombre antes
            // para que musica no piense que esa canción ya está sonando y la ponga de verdad.
            String volver = nombreMusica;
            nombreMusica = null;
            musica(volver);
        } else {
            // Al silenciar, detiene la música. El nombre queda guardado para poder volver a ponerla.
            detenerMusica();
        }
        return activo;
    }

    // Dice si el sonido está encendido. Lo usa el botón de música para dibujarse activo o apagado.
    public static boolean estaActivo() {
        return activo;
    }

    // Detiene la música y libera su memoria. Es private, solo Sonido la usa.
    private static void detenerMusica() {
        if (musicaActual != null) {
            // stop la para y close libera el audio. Luego queda en null porque ya no hay canción.
            musicaActual.stop();
            musicaActual.close();
            musicaActual = null;
        }
    }

    // Carga un archivo WAV en un Clip y lo devuelve. Si falla devuelve null y el juego sigue sin ese sonido.
    private static Clip abrir(String ruta) {
        // Los dos recursos del paréntesis se cierran solos al terminar el bloque. Se separan con punto y coma.
        // Recursos.abrir abre el archivo. El BufferedInputStream lo envuelve para que Java pueda reconocer el formato.
        // AudioSystem.getAudioInputStream lo convierte en audio.
        try (InputStream in = new BufferedInputStream(Recursos.abrir(ruta));
             AudioInputStream audio = AudioSystem.getAudioInputStream(in)) {
            // Crea un clip vacío y carga el audio en él.
            Clip clip = AudioSystem.getClip();
            clip.open(audio);
            return clip;
        // Si falta el archivo o la computadora no tiene parlantes, avisa en la consola y devuelve null.
        } catch (Exception e) {
            System.err.println("No se pudo reproducir " + ruta + ": " + e.getMessage());
            return null;
        }
    }

    // Cambia el volumen de un clip. Los decibeles negativos lo bajan, como el -6 de la música.
    private static void volumen(Clip clip, float decibeles) {
        try {
            // MASTER_GAIN es el control del volumen general del clip. El cast lo convierte al tipo FloatControl.
            FloatControl control = (FloatControl) clip.getControl(FloatControl.Type.MASTER_GAIN);
            control.setValue(decibeles);
        } catch (Exception e) {
            // Si ese clip no tiene control de volumen, se deja con el volumen que trae. Está vacío a propósito.
        }
    }
}