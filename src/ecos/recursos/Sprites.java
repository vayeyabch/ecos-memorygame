package ecos.recursos;

import ecos.app.Tema; // Para saber si el tema es claro u oscuro y elegir el fondo.
import java.awt.Color; // Para pintar el cuadro magenta de "imagen faltante".
import java.awt.Graphics2D; // Herramienta para dibujar sobre una imagen.
import java.awt.Image; // Tipo general de imagen en Java.
import java.awt.RenderingHints; // Para configurar cómo se dibuja (aquí: sin suavizar).
import java.awt.image.BufferedImage; // Imagen en memoria sobre la que se puede dibujar.
import java.io.File; // Para revisar si un archivo existe en el disco.
import java.io.InputStream; // Flujo de lectura para abrir la imagen.
import java.net.URL; // Dirección de un recurso dentro del proyecto compilado (.jar).
import java.util.HashMap; // Diccionario concreto (clave -> valor).
import java.util.Map; // Tipo general del diccionario.
import javax.imageio.ImageIO; // Lee archivos de imagen (png, jpg) y los convierte a Image.
import javax.swing.ImageIcon; // Imagen lista para Swing; también reproduce gifs animados.

public class Sprites { // Clase de utilidad: todo es static, no se crea con "new".

    private static final Map<String, Image> IMAGENES = new HashMap<>(); // Caché de imágenes fijas (ruta -> imagen) para no releer el archivo.
    private static final Map<String, ImageIcon> GIFS = new HashMap<>(); // Caché de gifs animados (ruta -> gif).

    // Imagen estática (png, jpg). Si falta devuelve un cuadro magenta para que se note.
    public static Image cargar(String ruta) { // Devuelve la imagen que está en esa ruta.
        Image img = IMAGENES.get(ruta); // Busca si ya se había cargado antes.
        if (img == null) { // Si no estaba en la caché, hay que cargarla.
            try (InputStream in = Recursos.abrir(ruta)) { // Abre el archivo con la clase Recursos; el try lo cierra solo.
                img = ImageIO.read(in); // Convierte el archivo en una imagen de Java.
            } catch (Exception e) { // Si falla (archivo faltante o dañado)
                System.err.println("No se pudo cargar la imagen " + ruta + ": " + e.getMessage()); // avisa en la consola cuál falló.
            }
            if (img == null) { // Si sigue sin haber imagen
                img = faltante(); // usa el cuadro magenta para que se note el error.
            }
            IMAGENES.put(ruta, img); // Guarda la imagen en la caché para la próxima vez.
        }
        return img; // Devuelve la imagen (real o el cuadro magenta).
    }

    // Gif animado. Dibujalo con g.drawImage(icono.getImage(), x, y, w, h, this) y se anima solo.
    public static ImageIcon gif(String ruta) { // Devuelve el gif de esa ruta.
        ImageIcon icono = GIFS.get(ruta); // Busca si ya estaba cargado.
        if (icono == null) { // Si no, hay que cargarlo.
            File archivo = new File("src/recursos/" + ruta); // Arma la ruta del gif en el disco.
            if (archivo.exists()) { // Si existe en el disco (corriendo desde VS Code)
                icono = new ImageIcon(archivo.getPath()); // lo carga desde ahí.
            } else { // Si no está en el disco
                URL url = Sprites.class.getResource("/recursos/" + ruta); // lo busca dentro del proyecto compilado (.jar).
                icono = url != null ? new ImageIcon(url) : new ImageIcon(faltante()); // Si lo encuentra lo carga; si no, usa el cuadro magenta.
            }
            GIFS.put(ruta, icono); // Lo guarda en la caché.
        }
        return icono; // Devuelve el gif.
    }

    // Escala sin suavizar (pixel art nítido).
    public static Image escalar(Image origen, int ancho, int alto) { // Cambia el tamaño de una imagen.
        BufferedImage destino = new BufferedImage(Math.max(1, ancho), Math.max(1, alto), BufferedImage.TYPE_INT_ARGB); // Crea una imagen vacía del tamaño nuevo (mínimo 1 px para evitar errores).
        Graphics2D g = destino.createGraphics(); // Prepara el "pincel" para dibujar en esa imagen nueva.
        g.setRenderingHint(RenderingHints.KEY_INTERPOLATION, RenderingHints.VALUE_INTERPOLATION_NEAREST_NEIGHBOR); // Pide no suavizar: así los píxeles quedan cuadrados y nítidos.
        g.drawImage(origen, 0, 0, destino.getWidth(), destino.getHeight(), null); // Dibuja la imagen original estirada al nuevo tamaño.
        g.dispose(); // Libera el pincel cuando termina.
        return destino; // Devuelve la imagen ya escalada.
    }

    // ---------- atajos para los recursos del proyecto ----------

    // Mascota del 1 al 9.
    public static Image mascota(int numero) { // Pide la mascota por su número.
        return cargar("Mascotas/mascota" + numero + ".png"); // Arma el nombre (ej: mascota3.png) y la carga.
    }

    // Ícono de carta del 1 al 18.
    public static Image icono(int numero) { // Pide el ícono (figura) de carta por su número.
        return cargar("Iconos/icono" + numero + ".png"); // Arma el nombre y lo carga.
    }

    // Dorso (tapa) de las cartas.
    public static Image dorso() { // Pide la imagen de la cara de atrás de las cartas.
        return cargar("Iconos/iconoTapa.jpeg"); // Carga la imagen de la tapa.
    }

    // Logo por nombre de archivo sin extensión, por ejemplo "logo_ecos".
    public static Image logo(String nombre) { // Pide un logo por su nombre.
        return cargar("logos/" + nombre + ".png"); // Arma la ruta (carpeta logos + nombre + .png) y lo carga.
    }

    // Fondo según el tema: montaña (claro) o noche (oscuro). Es un gif animado.
    public static ImageIcon fondo() { // Pide el fondo que corresponde al tema actual.
        return gif(Tema.esOscuro() ? "fondos/fondo_noche.gif" : "fondos/fondo_montana.gif"); // Si el tema es oscuro usa el de noche; si no, el de montaña.
    }

    private static Image faltante() { // Método interno: crea la imagen de "no se encontró".
        BufferedImage img = new BufferedImage(16, 16, BufferedImage.TYPE_INT_ARGB); // Crea una imagen vacía de 16x16 píxeles.
        Graphics2D g = img.createGraphics(); // Prepara el pincel.
        g.setColor(Color.MAGENTA); // Elige el color magenta (llamativo a propósito).
        g.fillRect(0, 0, 16, 16); // Pinta todo el cuadro de magenta.
        g.dispose(); // Libera el pincel.
        return img; // Devuelve el cuadro magenta.
    }
}