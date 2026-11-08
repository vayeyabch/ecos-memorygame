package ecos.recursos;

// Clases del propio proyecto que se usan aquí.
import ecos.app.Tema;
// Librería gráfica AWT, Color para los colores, Graphics2D para dibujar con más opciones, Image para las imágenes y
// RenderingHints para la calidad del dibujo.
import java.awt.Color;
import java.awt.Graphics2D;
import java.awt.Image;
import java.awt.RenderingHints;
// Imágenes de AWT, BufferedImage para imágenes que se pueden editar.
import java.awt.image.BufferedImage;
// Lectura de archivos, File para los archivos y InputStream para leer archivos.
import java.io.File;
import java.io.InputStream;
// Direcciones web, URL para las direcciones web.
import java.net.URL;
// Utilidades de Java, HashMap para diccionarios rápidos y Map para los diccionarios.
import java.util.HashMap;
import java.util.Map;
// Lectura de imágenes, ImageIO para leer imágenes.
import javax.imageio.ImageIO;
// Librería Swing, ImageIcon para los iconos.
import javax.swing.ImageIcon;

// Carga y escala las imágenes del juego (mascotas, íconos, logos y cartas) sin suavizarlas para mantener el pixel art.
public class Sprites {

    // Las imágenes gigantes (2067 px) se achican a este lado al cargarlas para gastar menos memoria, y hay mapas de imágenes
    // ya cargadas, ya escaladas (por ruta y tamaño) y de gifs ya cargados.
    private static final int LADO_MAXIMO = 512;
    private static final Map<String, Image> IMAGENES = new HashMap<>();
    private static final Map<String, Image> ESCALADAS = new HashMap<>();
    private static final Map<String, ImageIcon> GIFS = new HashMap<>();

    // Imagen estática (png, jpg). Si falta devuelve un cuadro magenta para que se note.
    // Recibe la ruta relativa a src/recursos.
    public static Image cargar(String ruta) {
        // Busca si ya se cargó antes y, si no estaba en memoria, abre el archivo (se cierra solo al terminar) y lee la
        // imagen.
        Image img = IMAGENES.get(ruta);
        if (img == null) {
            try (InputStream in = Recursos.abrir(ruta)) {
                img = ImageIO.read(in);
            // Si algo falla al leer. Avisa en la consola cuál falló.
            } catch (Exception e) {
                System.err.println("No se pudo cargar la imagen " + ruta + ": " + e.getMessage());
            }
            // Si no se pudo leer. Usa el cuadro magenta. Si la imagen es gigante. La achica una sola vez para no gastar memoria.
            if (img == null) {
                img = faltante();
            } else if (Math.max(img.getWidth(null), img.getHeight(null)) > 1500) {
                img = reducir((BufferedImage) img, LADO_MAXIMO);
            }
            // La guarda en el diccionario para la próxima vez.
            IMAGENES.put(ruta, img);
        }
        // Entrega la imagen.
        return img;
    }

    // Imagen ya escalada a un tamaño. Se guarda en memoria, así dibujarla en cada cuadro no cuesta nada.
    // Recibe la ruta y el tamaño deseado.
    public static Image cargarEscalada(String ruta, int ancho, int alto) {
        // Arma el nombre único de esta versión, busca si ya existe y, si no, la crea escalando la original.
        String clave = ruta + "|" + ancho + "x" + alto;
        Image img = ESCALADAS.get(clave);
        if (img == null) {
            img = escalar(cargar(ruta), ancho, alto);
            // La guarda para la próxima vez.
            ESCALADAS.put(clave, img);
        }
        // Entrega la imagen escalada.
        return img;
    }

    // Gif animado. Se dibuja con g.drawImage(icono.getImage(), x, y, w, h, this) y se anima solo.
    // Recibe la ruta relativa a src/recursos.
    public static ImageIcon gif(String ruta) {
        // Busca si ya se cargó antes y, si no estaba en memoria, primero busca el archivo en el disco.
        ImageIcon icono = GIFS.get(ruta);
        if (icono == null) {
            File archivo = new File("src/recursos/" + ruta);
            if (archivo.exists()) {
                // Lo carga desde ahí. Si no, lo busca dentro del proyecto compilado. Dirección dentro del .jar. Si no está, usa el
                // cuadro magenta.
                icono = new ImageIcon(archivo.getPath());
            } else {
                URL url = Sprites.class.getResource("/recursos/" + ruta);
                icono = url != null ? new ImageIcon(url) : new ImageIcon(faltante());
            }
            // Lo guarda para la próxima vez.
            GIFS.put(ruta, icono);
        }
        // Entrega el gif.
        return icono;
    }

    // Escala una imagen. Al agrandar no suaviza (pixel art nítido); al achicar mucho suaviza un poco para que no se vea rota.
    // Recibe la imagen y el tamaño nuevo.
    public static Image escalar(Image origen, int ancho, int alto) {
        // El ancho y el alto nunca pueden ser menores a 1, se asegura de tener una imagen editable y repite mientras la
        // imagen sea más del doble de lo que se pide.
        int an = Math.max(1, ancho);
        int al = Math.max(1, alto);
        BufferedImage actual = aBuffered(origen);
        while (actual.getWidth() > an * 2 && actual.getHeight() > al * 2) {
            // La achica a la mitad suavizando.
            actual = pasar(actual, actual.getWidth() / 2, actual.getHeight() / 2, true);
        }
        // Dice si todavía hay que achicar un poco. Último paso, suave al achicar, nítido al agrandar.
        boolean achicando = actual.getWidth() > an || actual.getHeight() > al;
        return pasar(actual, an, al, achicando);
    }

    // Atajos para los recursos del proyecto.

    // Mascota del 1 al 9.
    // Recibe el número de mascota.
    public static Image mascota(int numero) {
        // Carga la imagen de esa mascota.
        return cargar("Mascotas/mascota" + numero + ".png");
    }

    // Mascota del 1 al 9 ya escalada a un cuadrado.
    // Recibe el número y el lado del cuadrado.
    public static Image mascota(int numero, int lado) {
        // Entrega la versión escalada y guardada.
        return cargarEscalada("Mascotas/mascota" + numero + ".png", lado, lado);
    }

    // Icono de carta del 1 al 18.
    // Recibe el número de icono.
    public static Image icono(int numero) {
        // Carga la imagen de ese icono.
        return cargar("Iconos/icono" + numero + ".png");
    }

    // Icono de carta del 1 al 18 ya escalado a un cuadrado.
    // Recibe el número y el lado del cuadrado.
    public static Image icono(int numero, int lado) {
        // Entrega la versión escalada y guardada.
        return cargarEscalada("Iconos/icono" + numero + ".png", lado, lado);
    }

    // Dorso (tapa) de las cartas.
    // No recibe nada.
    public static Image dorso() {
        // Carga la imagen del dorso.
        return cargar("Iconos/iconoTapa.jpeg");
    }

    // Dorso de las cartas ya escalado a un cuadrado.
    // Recibe el lado del cuadrado.
    public static Image dorso(int lado) {
        // Entrega la versión escalada y guardada.
        return cargarEscalada("Iconos/iconoTapa.jpeg", lado, lado);
    }

    // Iconito de interfaz (coin, eye, moon, sun, trophy, star, heart, gem, shield) ya escalado a un cuadrado.
    // Recibe el nombre del icono y el lado.
    public static Image ui(String nombre, int lado) {
        // Entrega la versión escalada y guardada.
        return cargarEscalada("ui/" + nombre + ".png", lado, lado);
    }

    // Logo por nombre de archivo sin extensión, por ejemplo "logo_ecos".
    // Recibe el nombre del logo.
    public static Image logo(String nombre) {
        // Carga la imagen del logo.
        return cargar("logos/" + nombre + ".png");
    }

    // Fondo según el tema, montaña (claro) o noche (oscuro). Es un gif animado.
    // No recibe nada.
    public static ImageIcon fondo() {
        // Elige el gif según el tema.
        return gif(Tema.esOscuro() ? "fondos/fondo_noche.gif" : "fondos/fondo_montana.gif");
    }

    // Carga en segundo plano las imágenes pesadas para que las pantallas abran rápido.
    // No recibe nada.
    public static void precargar() {
        // Crea un hilo aparte para no congelar la pantalla, recorre las 9 mascotas y las carga y guarda.
        Thread hilo = new Thread(() -> {
            for (int i = 1; i <= 9; i++) {
                mascota(i, 96);
            }
            // Recorre los 18 iconos de carta. Los carga y los guarda.
            for (int i = 1; i <= 18; i++) {
                icono(i, 64);
            }
            // Carga el dorso de las cartas. Carga el fondo del tema actual.
            dorso(64);
            fondo();
        });
        // El hilo no impide que el programa se cierre.
        hilo.setDaemon(true);
        hilo.start();
    }

    // Métodos internos.

    // Achica una imagen gigante para que su lado mayor sea "lado".
    private static BufferedImage reducir(BufferedImage origen, int lado) {
        // Cuánto hay que achicar. La escala al tamaño pedido.
        double factor = lado / (double) Math.max(origen.getWidth(), origen.getHeight());
        return (BufferedImage) escalar(origen, (int) Math.round(origen.getWidth() * factor), (int) Math.round(origen.getHeight() * factor));
    }

    // Convierte cualquier imagen en una editable.
    private static BufferedImage aBuffered(Image origen) {
        // Si ya es editable. La devuelve tal cual.
        if (origen instanceof BufferedImage b) {
            return b;
        }
        // Crea una imagen vacía del mismo tamaño, con su pincel, copia la original y libera el pincel.
        BufferedImage copia = new BufferedImage(origen.getWidth(null), origen.getHeight(null), BufferedImage.TYPE_INT_ARGB);
        Graphics2D g = copia.createGraphics();
        g.drawImage(origen, 0, 0, null);
        g.dispose();
        // Entrega la copia.
        return copia;
    }

    // Dibuja la imagen en un tamaño nuevo.
    private static BufferedImage pasar(BufferedImage origen, int ancho, int alto, boolean suave) {
        // Crea una imagen vacía del tamaño nuevo con su pincel, suave al achicar y nítido al agrandar, y dibuja la imagen.
        BufferedImage destino = new BufferedImage(Math.max(1, ancho), Math.max(1, alto), BufferedImage.TYPE_INT_ARGB);
        Graphics2D g = destino.createGraphics();
        g.setRenderingHint(RenderingHints.KEY_INTERPOLATION, suave ? RenderingHints.VALUE_INTERPOLATION_BILINEAR : RenderingHints.VALUE_INTERPOLATION_NEAREST_NEIGHBOR);
        g.drawImage(origen, 0, 0, destino.getWidth(), destino.getHeight(), null);
        // Libera el pincel. Entrega la imagen nueva.
        g.dispose();
        return destino;
    }

    // Cuadro magenta que se usa cuando falta una imagen.
    private static Image faltante() {
        // Imagen de 16 x 16 con su pincel, elige el color magenta y pinta todo el cuadro.
        BufferedImage img = new BufferedImage(16, 16, BufferedImage.TYPE_INT_ARGB);
        Graphics2D g = img.createGraphics();
        g.setColor(Color.MAGENTA);
        g.fillRect(0, 0, 16, 16);
        // Libera el pincel. Entrega el cuadro.
        g.dispose();
        return img;
    }
}
