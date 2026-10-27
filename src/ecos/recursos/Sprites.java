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

/**
 * Carga imagenes y gifs, los guarda en memoria (cache) y los escala sin
 * volverlos borrosos (vecino mas cercano), para que se mantenga el pixel art.
 * Las rutas son relativas a src/recursos, por ejemplo "Mascotas/mascota1.png".
 */
public class Sprites {

    private static final Map<String, Image> IMAGENES = new HashMap<>();
    private static final Map<String, ImageIcon> GIFS = new HashMap<>();

    /** Imagen estatica (png, jpg). Si falta devuelve un cuadro magenta para que se note. */
    public static Image cargar(String ruta) {
        Image img = IMAGENES.get(ruta);
        if (img == null) {
            try (InputStream in = Recursos.abrir(ruta)) {
                img = ImageIO.read(in);
            } catch (Exception e) {
                System.err.println("No se pudo cargar la imagen " + ruta + ": " + e.getMessage());
            }
            if (img == null) {
                img = faltante();
            }
            IMAGENES.put(ruta, img);
        }
        return img;
    }

    /** Gif animado. Dibujalo con g.drawImage(icono.getImage(), x, y, w, h, this) y se anima solo. */
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

    /** Escala sin suavizar (pixel art nitido). */
    public static Image escalar(Image origen, int ancho, int alto) {
        BufferedImage destino = new BufferedImage(Math.max(1, ancho), Math.max(1, alto), BufferedImage.TYPE_INT_ARGB);
        Graphics2D g = destino.createGraphics();
        g.setRenderingHint(RenderingHints.KEY_INTERPOLATION, RenderingHints.VALUE_INTERPOLATION_NEAREST_NEIGHBOR);
        g.drawImage(origen, 0, 0, destino.getWidth(), destino.getHeight(), null);
        g.dispose();
        return destino;
    }

    // ---------- atajos para los recursos del proyecto ----------

    /** Mascota del 1 al 9. */
    public static Image mascota(int numero) {
        return cargar("Mascotas/mascota" + numero + ".png");
    }

    /** Icono de carta del 1 al 18. */
    public static Image icono(int numero) {
        return cargar("Iconos/icono" + numero + ".png");
    }

    /** Dorso (tapa) de las cartas. */
    public static Image dorso() {
        return cargar("Iconos/iconoTapa.jpeg");
    }

    /** Logo por nombre de archivo sin extension, por ejemplo "logo_ecos". */
    public static Image logo(String nombre) {
        return cargar("logos/" + nombre + ".png");
    }

    /** Fondo segun el tema: montana (claro) o noche (oscuro). Es un gif animado. */
    public static ImageIcon fondo() {
        return gif(Tema.esOscuro() ? "fondos/fondo_noche.gif" : "fondos/fondo_montana.gif");
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
