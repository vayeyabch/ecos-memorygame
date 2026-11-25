package ecos.recursos;

// Tema se usa en fondo() para saber si el modo es claro u oscuro y elegir el gif correcto.
import ecos.app.Tema;
// Color se usa para pintar de magenta el cuadro que avisa que falta una imagen.
import java.awt.Color;
// Graphics2D es el pincel con el que se dibuja sobre una imagen en memoria.
import java.awt.Graphics2D;
// Image es el tipo general de imagen. Casi todos los métodos devuelven este tipo.
import java.awt.Image;
// RenderingHints sirve para dar instrucciones al pincel, aquí si debe suavizar o no al cambiar de tamaño.
import java.awt.RenderingHints;
// BufferedImage es una imagen en memoria hecha de píxeles. Permite leer su ancho y alto y dibujar sobre ella.
import java.awt.image.BufferedImage;
// File representa un archivo del disco. En gif() sirve para preguntar si el gif existe.
import java.io.File;
// InputStream es de donde se leen los bytes de un archivo. Es lo que devuelve Recursos.abrir.
import java.io.InputStream;
// URL es la dirección de un archivo dentro del jar. En gif() se usa cuando el archivo no está en el disco.
import java.net.URL;
// HashMap es la clase concreta del diccionario donde se guardan las imágenes ya cargadas.
import java.util.HashMap;
// Map es el tipo de esos diccionarios: una clave y su valor.
import java.util.Map;
// ImageIO lee un archivo de imagen y lo convierte en una imagen de Java.
import javax.imageio.ImageIO;
// ImageIcon es una imagen lista para Swing. Es el tipo que anima los gif solos.
import javax.swing.ImageIcon;

// Es la bodega de imágenes del juego. Las pantallas piden una imagen por su nombre y la reciben lista.
// Carga las imágenes, las escala sin suavizar para mantener el pixel art y las guarda en caché.
// Todo es static, así que se usa escribiendo Sprites.mascota(3, 96), sin crear un objeto.
public class Sprites {

    // Si una imagen es gigante, se achica a este lado una sola vez al cargarla.
    // Es final porque nunca cambia. Tener el nombre en vez del número suelto deja claro qué significa.
    private static final int LADO_MAXIMO = 512;
    // Los tres siguientes son cachés, o sea diccionarios que guardan lo ya hecho para no repetir el trabajo.
    // Imágenes ya cargadas. La clave es la ruta del archivo.
    private static final Map<String, Image> IMAGENES = new HashMap<>();
    // Imágenes ya escaladas. La clave es la ruta más el tamaño, porque una misma imagen se puede pedir en varios tamaños.
    private static final Map<String, Image> ESCALADAS = new HashMap<>();
    // Gifs ya cargados.
    private static final Map<String, ImageIcon> GIFS = new HashMap<>();

    // Entrega una imagen a partir de su ruta dentro de src/recursos, en su tamaño original.
    // Resuelve que cada imagen se lea del disco una sola vez y que el juego no se caiga si una falta.
    public static Image cargar(String ruta) {
        // Primero pregunta al caché. Si ya estaba cargada, salta hasta el return del final.
        Image img = IMAGENES.get(ruta);
        if (img == null) {
            // El archivo abierto va dentro del paréntesis del try para que se cierre solo al terminar el bloque.
            try (InputStream in = Recursos.abrir(ruta)) {
                // Lee los bytes del archivo y los convierte en una imagen.
                img = ImageIO.read(in);
            // Si algo falla, avisa en la consola cuál archivo fue y el programa sigue.
            } catch (Exception e) {
                System.err.println("No se pudo cargar la imagen " + ruta + ": " + e.getMessage());
            }
            // Si no se pudo leer, usa el cuadro magenta para que el error se note en pantalla.
            if (img == null) {
                img = faltante();
            // Si sí se leyó y su lado mayor mide más de 1500, la achica una sola vez a LADO_MAXIMO.
            } else if (Math.max(img.getWidth(null), img.getHeight(null)) > 1500) {
                img = reducir((BufferedImage) img, LADO_MAXIMO);
            }
            // La guarda en el caché para que la próxima vez no se lea otra vez.
            IMAGENES.put(ruta, img);
        }
        return img;
    }

    // Entrega una imagen ya escalada al tamaño pedido. La guarda para que dibujarla en cada cuadro no cueste nada.
    public static Image cargarEscalada(String ruta, int ancho, int alto) {
        // La clave junta la ruta y el tamaño, por ejemplo "Mascotas/mascota1.png|96x96".
        String clave = ruta + "|" + ancho + "x" + alto;
        Image img = ESCALADAS.get(clave);
        if (img == null) {
            // Se lee de adentro hacia afuera: primero consigue la original con cargar y luego la escala.
            img = escalar(cargar(ruta), ancho, alto);
            ESCALADAS.put(clave, img);
        }
        return img;
    }

    // Entrega un gif animado, que se anima solo porque es un ImageIcon.
    // No usa Recursos.abrir porque ImageIO.read daría una foto fija, sin animación.
    // Lo busca en este orden: caché, disco, jar y, si no está en ninguno, el cuadro magenta.
    public static ImageIcon gif(String ruta) {
        ImageIcon icono = GIFS.get(ruta);
        if (icono == null) {
            File archivo = new File("src/recursos/" + ruta);
            if (archivo.exists()) {
                // Está en el disco, que es lo que pasa al correr desde VS Code.
                icono = new ImageIcon(archivo.getPath());
            } else {
                // No está en el disco, así que lo busca dentro del jar. Si no lo encuentra, url es null.
                URL url = Sprites.class.getResource("/recursos/" + ruta);
                // El signo de pregunta es un if corto. Si url no es null crea el gif, y si es null usa el cuadro magenta.
                icono = url != null ? new ImageIcon(url) : new ImageIcon(faltante());
            }
            GIFS.put(ruta, icono);
        }
        return icono;
    }

    // Cambia el tamaño de una imagen. Al agrandar no suaviza, para dejar el pixel art nítido.
    // Al achicar mucho suaviza un poco, y lo hace reduciendo a la mitad varias veces para que no se vea dentado.
    // Este método solo decide si suavizar. Quien dibuja de verdad es el método pasar.
    public static Image escalar(Image origen, int ancho, int alto) {
        // Math.max devuelve el mayor de los dos números. Así el tamaño nunca es menor que 1, porque 0 daría error.
        int an = Math.max(1, ancho);
        int al = Math.max(1, alto);
        // Convierte la imagen a BufferedImage, que es la que permite leer su ancho y alto y dibujar sobre ella.
        BufferedImage actual = aBuffered(origen);
        // Mientras la imagen sea más del doble de grande que lo pedido, la reduce a la mitad con suavizado.
        // El signo && significa y, o sea las dos condiciones deben cumplirse.
        while (actual.getWidth() > an * 2 && actual.getHeight() > al * 2) {
            actual = pasar(actual, actual.getWidth() / 2, actual.getHeight() / 2, true);
        }
        // Es true si todavía queda más grande que lo pedido. El signo || significa o.
        boolean achicando = actual.getWidth() > an || actual.getHeight() > al;
        // Hace el paso final al tamaño exacto. Si está agrandando, achicando es false y no suaviza.
        return pasar(actual, an, al, achicando);
    }

    // Atajos para los recursos del proyecto. No tienen lógica propia: arman la ruta del archivo y llaman a cargar
    // o a cargarEscalada. Así las pantallas escriben Sprites.mascota(3) y no necesitan saber las carpetas.
    // Hay dos versiones con el mismo nombre. Java elige cuál usar según los datos que se le pasen.
    // Sin lado da la imagen en su tamaño original, y con lado la da escalada a un cuadrado de ese tamaño.

    // Mascota del 1 al 9 en su tamaño original.
    public static Image mascota(int numero) {
        return cargar("Mascotas/mascota" + numero + ".png");
    }

    // Mascota del 1 al 9 escalada a un cuadrado.
    public static Image mascota(int numero, int lado) {
        return cargarEscalada("Mascotas/mascota" + numero + ".png", lado, lado);
    }

    // Icono de carta del 1 al 18 en su tamaño original.
    public static Image icono(int numero) {
        return cargar("Iconos/icono" + numero + ".png");
    }

    // Icono de carta del 1 al 18 escalado a un cuadrado.
    public static Image icono(int numero, int lado) {
        return cargarEscalada("Iconos/icono" + numero + ".png", lado, lado);
    }

    // Dorso de las cartas en su tamaño original. Siempre es el mismo archivo.
    public static Image dorso() {
        return cargar("Iconos/iconoTapa.jpeg");
    }

    // Dorso de las cartas escalado a un cuadrado.
    public static Image dorso(int lado) {
        return cargarEscalada("Iconos/iconoTapa.jpeg", lado, lado);
    }

    // Ícono de interfaz por su nombre, como coin, eye, moon, sun, trophy, star, heart, gem o shield. Va escalado a un cuadrado.
    public static Image ui(String nombre, int lado) {
        return cargarEscalada("ui/" + nombre + ".png", lado, lado);
    }

    // Logo por nombre de archivo sin extensión, por ejemplo "logo_ecos", en su tamaño original.
    public static Image logo(String nombre) {
        return cargar("logos/" + nombre + ".png");
    }

    // Entrega el gif de fondo según el tema. Si es oscuro da la noche y si no la montaña.
    // Pantalla lo llama cada vez que se dibuja, así el fondo cambia solo al cambiar el tema.
    public static ImageIcon fondo() {
        return gif(Tema.esOscuro() ? "fondos/fondo_noche.gif" : "fondos/fondo_montana.gif");
    }

    // Carga en segundo plano las imágenes pesadas para que las pantallas abran rápido. La llama Main al arrancar.
    public static void precargar() {
        // Un hilo es un trabajo que corre aparte, al mismo tiempo que el resto del programa.
        // Las llaves con flecha son la función que el hilo ejecuta. Crearlo todavía no lo pone a correr.
        Thread hilo = new Thread(() -> {
            // Cada llamada carga la imagen y la deja guardada en el caché. El resultado no se usa aquí.
            for (int i = 1; i <= 9; i++) {
                mascota(i, 96);
            }
            for (int i = 1; i <= 18; i++) {
                icono(i, 64);
            }
            dorso(64);
            fondo();
        });
        // Daemon significa que el hilo no impide cerrar el programa si todavía no terminó.
        hilo.setDaemon(true);
        // start es lo que realmente lo pone a correr.
        hilo.start();
    }

    // Métodos internos. Son private, así que solo Sprites los usa.

    // Achica una imagen gigante para que su lado mayor mida "lado", sin deformarla.
    // Sirve para no guardar ni escalar imágenes enormes, que serían lentas y gastarían mucha memoria.
    // Ejemplo: una de 3000x2000 con lado 512 queda de 512x341.
    private static BufferedImage reducir(BufferedImage origen, int lado) {
        // El factor es el número por el que hay que multiplicar. El cast a double evita que la división se redondee a entero.
        double factor = lado / (double) Math.max(origen.getWidth(), origen.getHeight());
        // Multiplica ancho y alto por el mismo factor, y por eso no se deforma. Round redondea y el cast a int lo pasa a entero.
        return (BufferedImage) escalar(origen, (int) Math.round(origen.getWidth() * factor), (int) Math.round(origen.getHeight() * factor));
    }

    // Convierte cualquier imagen en BufferedImage. Sirve porque escalar necesita leer el tamaño y dibujar sobre ella.
    private static BufferedImage aBuffered(Image origen) {
        // Si ya es BufferedImage la devuelve igual. La b es la misma imagen ya reconocida con ese tipo.
        if (origen instanceof BufferedImage b) {
            return b;
        }
        // Si no lo es, crea una vacía del mismo tamaño. ARGB significa que guarda color y transparencia.
        BufferedImage copia = new BufferedImage(origen.getWidth(null), origen.getHeight(null), BufferedImage.TYPE_INT_ARGB);
        // Saca el pincel de esa imagen vacía.
        Graphics2D g = copia.createGraphics();
        // Dibuja la original encima, desde la esquina. Ahora la copia tiene el mismo contenido.
        g.drawImage(origen, 0, 0, null);
        // Libera el pincel porque ya no se usa.
        g.dispose();
        return copia;
    }

    // Es el que realmente cambia el tamaño. Dibuja la imagen original dentro de una nueva del tamaño pedido.
    // Con suave en true suaviza los colores. Con false copia el píxel más cercano, y los cuadritos quedan nítidos.
    private static BufferedImage pasar(BufferedImage origen, int ancho, int alto, boolean suave) {
        // Crea la imagen vacía del tamaño nuevo, nunca menor que 1.
        BufferedImage destino = new BufferedImage(Math.max(1, ancho), Math.max(1, alto), BufferedImage.TYPE_INT_ARGB);
        Graphics2D g = destino.createGraphics();
        // Aquí está el suavizado. El signo de pregunta elige entre BILINEAR, que mezcla colores vecinos,
        // y NEAREST_NEIGHBOR, que no mezcla y deja el pixel art nítido.
        g.setRenderingHint(RenderingHints.KEY_INTERPOLATION, suave ? RenderingHints.VALUE_INTERPOLATION_BILINEAR : RenderingHints.VALUE_INTERPOLATION_NEAREST_NEIGHBOR);
        // Dibuja la original estirada o encogida hasta llenar toda la imagen nueva.
        g.drawImage(origen, 0, 0, destino.getWidth(), destino.getHeight(), null);
        g.dispose();
        return destino;
    }

    // Crea el cuadro magenta de 16x16 que se usa cuando una imagen falta o falla, para que el error se vea en pantalla.
    private static Image faltante() {
        BufferedImage img = new BufferedImage(16, 16, BufferedImage.TYPE_INT_ARGB);
        Graphics2D g = img.createGraphics();
        // Elige el color magenta y rellena un rectángulo que cubre toda la imagen.
        g.setColor(Color.MAGENTA);
        g.fillRect(0, 0, 16, 16);
        g.dispose();
        return img;
    }
}