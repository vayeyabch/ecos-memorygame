package ecos.recursos;

import ecos.app.Tema;
import java.awt.Color;
import java.awt.Graphics2D;
import java.awt.Image;
import java.awt.RenderingHints;
import java.awt.image.BufferedImage;
import java.io.File;
import java.io.InputStream;
import java.net.URL;
import java.util.HashMap;
import java.util.Map;
import javax.imageio.ImageIO;
import javax.swing.ImageIcon;

// Carga y escala las imágenes del juego sin suavizarlas, para mantener el pixel art.
public class Sprites {

    // Las imágenes gigantes se achican a este lado al cargarlas. Mapas de imágenes ya cargadas, ya escaladas y de gifs.
    private static final int LADO_MAXIMO = 512;
    private static final Map<String, Image> IMAGENES = new HashMap<>();
    private static final Map<String, Image> ESCALADAS = new HashMap<>();
    private static final Map<String, ImageIcon> GIFS = new HashMap<>();

    // Imagen estática a partir de su ruta dentro de src/recursos. Si falta devuelve un cuadro magenta para que se note.
    public static Image cargar(String ruta) {
        Image img = IMAGENES.get(ruta);
        if (img == null) {
            try (InputStream in = Recursos.abrir(ruta)) {
                img = ImageIO.read(in);
            // Avisa en la consola cuál archivo falló.
            } catch (Exception e) {
                System.err.println("No se pudo cargar la imagen " + ruta + ": " + e.getMessage());
            }
            // Si no se pudo leer usa el cuadro magenta; si es gigante, la achica una sola vez.
            if (img == null) {
                img = faltante();
            } else if (Math.max(img.getWidth(null), img.getHeight(null)) > 1500) {
                img = reducir((BufferedImage) img, LADO_MAXIMO);
            }
            IMAGENES.put(ruta, img);
        }
        return img;
    }

    // Imagen escalada. Se guarda para que dibujarla en cada cuadro no cueste nada.
    public static Image cargarEscalada(String ruta, int ancho, int alto) {
        String clave = ruta + "|" + ancho + "x" + alto;
        Image img = ESCALADAS.get(clave);
        if (img == null) {
            img = escalar(cargar(ruta), ancho, alto);
            ESCALADAS.put(clave, img);
        }
        return img;
    }

    // Gif animado, se anima solo.
    public static ImageIcon gif(String ruta) {
        ImageIcon icono = GIFS.get(ruta);
        if (icono == null) {
            File archivo = new File("src/recursos/" + ruta);
            if (archivo.exists()) {
                icono = new ImageIcon(archivo.getPath());
            } else {
                URL url = Sprites.class.getResource("/recursos/" + ruta);
                icono = url != null ? new ImageIcon(url) : new ImageIcon(faltante());
            }
            GIFS.put(ruta, icono);
        }
        return icono;
    }

    // Escala una imagen. Al agrandar no suaviza, para dejar el pixel art nítido; al achicar mucho suaviza un poco.
    public static Image escalar(Image origen, int ancho, int alto) {
        int an = Math.max(1, ancho);
        int al = Math.max(1, alto);
        BufferedImage actual = aBuffered(origen);
        while (actual.getWidth() > an * 2 && actual.getHeight() > al * 2) {
            actual = pasar(actual, actual.getWidth() / 2, actual.getHeight() / 2, true);
        }
        boolean achicando = actual.getWidth() > an || actual.getHeight() > al;
        return pasar(actual, an, al, achicando);
    }

    // Atajos para los recursos del proyecto.

    public static Image mascota(int numero) {
        return cargar("Mascotas/mascota" + numero + ".png");
    }

    // Mascota del 1 al 9 escalada a un cuadrado.
    public static Image mascota(int numero, int lado) {
        return cargarEscalada("Mascotas/mascota" + numero + ".png", lado, lado);
    }

    public static Image icono(int numero) {
        return cargar("Iconos/icono" + numero + ".png");
    }

    // Icono de carta del 1 al 18 escalado a un cuadrado.
    public static Image icono(int numero, int lado) {
        return cargarEscalada("Iconos/icono" + numero + ".png", lado, lado);
    }

    public static Image dorso() {
        return cargar("Iconos/iconoTapa.jpeg");
    }

    // Dorso de las cartas escalado a un cuadrado.
    public static Image dorso(int lado) {
        return cargarEscalada("Iconos/iconoTapa.jpeg", lado, lado);
    }

    // Ícono de interfaz (coin, eye, moon, sun, trophy, star, heart, gem, shield) escalado a un cuadrado.
    public static Image ui(String nombre, int lado) {
        return cargarEscalada("ui/" + nombre + ".png", lado, lado);
    }

    // Logo por nombre de archivo sin extensión, por ejemplo "logo_ecos".
    public static Image logo(String nombre) {
        return cargar("logos/" + nombre + ".png");
    }

    // Fondo según el tema: montaña (claro) o noche (oscuro).
    public static ImageIcon fondo() {
        return gif(Tema.esOscuro() ? "fondos/fondo_noche.gif" : "fondos/fondo_montana.gif");
    }

    // Carga en segundo plano las imágenes pesadas para que las pantallas abran rápido.
    public static void precargar() {
        Thread hilo = new Thread(() -> {
            for (int i = 1; i <= 9; i++) {
                mascota(i, 96);
            }
            for (int i = 1; i <= 18; i++) {
                icono(i, 64);
            }
            dorso(64);
            fondo();
        });
        hilo.setDaemon(true);
        hilo.start();
    }

    // Métodos internos.

    // Achica una imagen gigante para que su lado mayor sea "lado".
    private static BufferedImage reducir(BufferedImage origen, int lado) {
        double factor = lado / (double) Math.max(origen.getWidth(), origen.getHeight());
        return (BufferedImage) escalar(origen, (int) Math.round(origen.getWidth() * factor), (int) Math.round(origen.getHeight() * factor));
    }

    private static BufferedImage aBuffered(Image origen) {
        if (origen instanceof BufferedImage b) {
            return b;
        }
        BufferedImage copia = new BufferedImage(origen.getWidth(null), origen.getHeight(null), BufferedImage.TYPE_INT_ARGB);
        Graphics2D g = copia.createGraphics();
        g.drawImage(origen, 0, 0, null);
        g.dispose();
        return copia;
    }

    private static BufferedImage pasar(BufferedImage origen, int ancho, int alto, boolean suave) {
        BufferedImage destino = new BufferedImage(Math.max(1, ancho), Math.max(1, alto), BufferedImage.TYPE_INT_ARGB);
        Graphics2D g = destino.createGraphics();
        g.setRenderingHint(RenderingHints.KEY_INTERPOLATION, suave ? RenderingHints.VALUE_INTERPOLATION_BILINEAR : RenderingHints.VALUE_INTERPOLATION_NEAREST_NEIGHBOR);
        g.drawImage(origen, 0, 0, destino.getWidth(), destino.getHeight(), null);
        g.dispose();
        return destino;
    }

    private static Image faltante() {
        BufferedImage img = new BufferedImage(16, 16, BufferedImage.TYPE_INT_ARGB);
        Graphics2D g = img.createGraphics();
        g.setColor(Color.MAGENTA);
        g.fillRect(0, 0, 16, 16);
        g.dispose();
        return img;
    }
}
