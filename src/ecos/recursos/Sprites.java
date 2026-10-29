package ecos.recursos;

import ecos.app.Tema; // Para saber si el tema es claro u oscuro (elige el fondo).
import java.awt.Color; // Clase de Java para representar un color.
import java.awt.Graphics2D; // Herramienta para dibujar imágenes y figuras.
import java.awt.Image; // Tipo general de imagen.
import java.awt.RenderingHints; // Opciones de calidad al dibujar (aquí: sin suavizar).
import java.awt.image.BufferedImage; // Imagen que se puede dibujar y modificar en memoria.
import java.io.File; // Representa un archivo del disco.
import java.io.InputStream; // Flujo para leer los bytes de un archivo.
import java.net.URL; // Dirección de un recurso dentro del proyecto compilado.
import java.util.HashMap; // Diccionario concreto (clave -> valor).
import java.util.Map; // Tipo general del diccionario.
import javax.imageio.ImageIO; // Lector de imágenes (png, jpg).
import javax.swing.ImageIcon; // Imagen de Swing; también sirve para gifs animados.

public class Sprites {

    private static final int LADO_MAXIMO = 512; // Las imágenes gigantes (2067 px) se achican a este lado al cargarlas, para gastar menos memoria.
    private static final Map<String, Image> IMAGENES = new HashMap<>(); // Imágenes ya cargadas (ruta -> imagen).
    private static final Map<String, Image> ESCALADAS = new HashMap<>(); // Imágenes ya escaladas (ruta + tamaño -> imagen).
    private static final Map<String, ImageIcon> GIFS = new HashMap<>(); // Gifs ya cargados (ruta -> gif).

    // Imagen estática (png, jpg). Si falta devuelve un cuadro magenta para que se note.
    public static Image cargar(String ruta) { // Recibe la ruta relativa a src/recursos.
        Image img = IMAGENES.get(ruta); // Busca si ya se cargó antes.
        if (img == null) { // Si todavía no estaba en memoria.
            try (InputStream in = Recursos.abrir(ruta)) { // Abre el archivo (se cierra solo al terminar).
                img = ImageIO.read(in); // Lee la imagen del archivo.
            } catch (Exception e) { // Si algo falla al leer.
                System.err.println("No se pudo cargar la imagen " + ruta + ": " + e.getMessage()); // Avisa en la consola cuál falló.
            }
            if (img == null) { // Si no se pudo leer.
                img = faltante(); // Usa el cuadro magenta.
            } else if (Math.max(img.getWidth(null), img.getHeight(null)) > 1500) { // Si la imagen es gigante.
                img = reducir((BufferedImage) img, LADO_MAXIMO); // La achica una sola vez para no gastar memoria.
            }
            IMAGENES.put(ruta, img); // La guarda en el diccionario para la próxima vez.
        }
        return img; // Entrega la imagen.
    }

    // Imagen ya escalada a un tamaño. Se guarda en memoria, así dibujarla en cada cuadro no cuesta nada.
    public static Image cargarEscalada(String ruta, int ancho, int alto) { // Recibe la ruta y el tamaño deseado.
        String clave = ruta + "|" + ancho + "x" + alto; // Arma el nombre único de esta versión.
        Image img = ESCALADAS.get(clave); // Busca si ya existe esa versión.
        if (img == null) { // Si no existe todavía.
            img = escalar(cargar(ruta), ancho, alto); // La crea escalando la original.
            ESCALADAS.put(clave, img); // La guarda para la próxima vez.
        }
        return img; // Entrega la imagen escalada.
    }

    // Gif animado. Se dibuja con g.drawImage(icono.getImage(), x, y, w, h, this) y se anima solo.
    public static ImageIcon gif(String ruta) { // Recibe la ruta relativa a src/recursos.
        ImageIcon icono = GIFS.get(ruta); // Busca si ya se cargó antes.
        if (icono == null) { // Si todavía no estaba en memoria.
            File archivo = new File("src/recursos/" + ruta); // Primero busca el archivo en el disco.
            if (archivo.exists()) { // Si existe en el disco.
                icono = new ImageIcon(archivo.getPath()); // Lo carga desde ahí.
            } else { // Si no, lo busca dentro del proyecto compilado.
                URL url = Sprites.class.getResource("/recursos/" + ruta); // Dirección dentro del .jar.
                icono = url != null ? new ImageIcon(url) : new ImageIcon(faltante()); // Si no está, usa el cuadro magenta.
            }
            GIFS.put(ruta, icono); // Lo guarda para la próxima vez.
        }
        return icono; // Entrega el gif.
    }

    // Escala una imagen. Al agrandar no suaviza (pixel art nítido); al achicar mucho suaviza un poco para que no se vea rota.
    public static Image escalar(Image origen, int ancho, int alto) { // Recibe la imagen y el tamaño nuevo.
        int an = Math.max(1, ancho); // El ancho nunca puede ser menor a 1.
        int al = Math.max(1, alto); // El alto nunca puede ser menor a 1.
        BufferedImage actual = aBuffered(origen); // Se asegura de tener una imagen editable.
        while (actual.getWidth() > an * 2 && actual.getHeight() > al * 2) { // Mientras la imagen sea más del doble de lo que se pide.
            actual = pasar(actual, actual.getWidth() / 2, actual.getHeight() / 2, true); // La achica a la mitad suavizando.
        }
        boolean achicando = actual.getWidth() > an || actual.getHeight() > al; // Dice si todavía hay que achicar un poco.
        return pasar(actual, an, al, achicando); // Último paso: suave al achicar, nítido al agrandar.
    }

    // ---------- atajos para los recursos del proyecto ----------

    // Mascota del 1 al 9.
    public static Image mascota(int numero) { // Recibe el número de mascota.
        return cargar("Mascotas/mascota" + numero + ".png"); // Carga la imagen de esa mascota.
    }

    // Mascota del 1 al 9 ya escalada a un cuadrado.
    public static Image mascota(int numero, int lado) { // Recibe el número y el lado del cuadrado.
        return cargarEscalada("Mascotas/mascota" + numero + ".png", lado, lado); // Entrega la versión escalada y guardada.
    }

    // Icono de carta del 1 al 18.
    public static Image icono(int numero) { // Recibe el número de icono.
        return cargar("Iconos/icono" + numero + ".png"); // Carga la imagen de ese icono.
    }

    // Icono de carta del 1 al 18 ya escalado a un cuadrado.
    public static Image icono(int numero, int lado) { // Recibe el número y el lado del cuadrado.
        return cargarEscalada("Iconos/icono" + numero + ".png", lado, lado); // Entrega la versión escalada y guardada.
    }

    // Dorso (tapa) de las cartas.
    public static Image dorso() { // No recibe nada.
        return cargar("Iconos/iconoTapa.jpeg"); // Carga la imagen del dorso.
    }

    // Dorso de las cartas ya escalado a un cuadrado.
    public static Image dorso(int lado) { // Recibe el lado del cuadrado.
        return cargarEscalada("Iconos/iconoTapa.jpeg", lado, lado); // Entrega la versión escalada y guardada.
    }

    // Iconito de interfaz (coin, eye, moon, sun, trophy, star, heart, gem, shield) ya escalado a un cuadrado.
    public static Image ui(String nombre, int lado) { // Recibe el nombre del icono y el lado.
        return cargarEscalada("ui/" + nombre + ".png", lado, lado); // Entrega la versión escalada y guardada.
    }

    // Logo por nombre de archivo sin extensión, por ejemplo "logo_ecos".
    public static Image logo(String nombre) { // Recibe el nombre del logo.
        return cargar("logos/" + nombre + ".png"); // Carga la imagen del logo.
    }

    // Fondo según el tema: montaña (claro) o noche (oscuro). Es un gif animado.
    public static ImageIcon fondo() { // No recibe nada.
        return gif(Tema.esOscuro() ? "fondos/fondo_noche.gif" : "fondos/fondo_montana.gif"); // Elige el gif según el tema.
    }

    // Carga en segundo plano las imágenes pesadas para que las pantallas abran rápido.
    public static void precargar() { // No recibe nada.
        Thread hilo = new Thread(() -> { // Crea un hilo aparte para no congelar la pantalla.
            for (int i = 1; i <= 9; i++) { // Recorre las 9 mascotas.
                mascota(i, 96); // Las carga y las guarda.
            }
            for (int i = 1; i <= 18; i++) { // Recorre los 18 iconos de carta.
                icono(i, 64); // Los carga y los guarda.
            }
            dorso(64); // Carga el dorso de las cartas.
            fondo(); // Carga el fondo del tema actual.
        });
        hilo.setDaemon(true); // El hilo no impide que el programa se cierre.
        hilo.start(); // Lo arranca.
    }

    // ---------- métodos internos ----------

    private static BufferedImage reducir(BufferedImage origen, int lado) { // Achica una imagen gigante para que su lado mayor sea "lado".
        double factor = lado / (double) Math.max(origen.getWidth(), origen.getHeight()); // Cuánto hay que achicar.
        return (BufferedImage) escalar(origen, (int) Math.round(origen.getWidth() * factor), (int) Math.round(origen.getHeight() * factor)); // La escala al tamaño pedido.
    }

    private static BufferedImage aBuffered(Image origen) { // Convierte cualquier imagen en una editable.
        if (origen instanceof BufferedImage b) { // Si ya es editable.
            return b; // La devuelve tal cual.
        }
        BufferedImage copia = new BufferedImage(origen.getWidth(null), origen.getHeight(null), BufferedImage.TYPE_INT_ARGB); // Crea una imagen vacía del mismo tamaño.
        Graphics2D g = copia.createGraphics(); // Pincel para dibujar en ella.
        g.drawImage(origen, 0, 0, null); // Copia la imagen original.
        g.dispose(); // Libera el pincel.
        return copia; // Entrega la copia.
    }

    private static BufferedImage pasar(BufferedImage origen, int ancho, int alto, boolean suave) { // Dibuja la imagen en un tamaño nuevo.
        BufferedImage destino = new BufferedImage(Math.max(1, ancho), Math.max(1, alto), BufferedImage.TYPE_INT_ARGB); // Imagen vacía del tamaño nuevo.
        Graphics2D g = destino.createGraphics(); // Pincel para dibujar en ella.
        g.setRenderingHint(RenderingHints.KEY_INTERPOLATION, suave ? RenderingHints.VALUE_INTERPOLATION_BILINEAR : RenderingHints.VALUE_INTERPOLATION_NEAREST_NEIGHBOR); // Suave al achicar, nítido al agrandar.
        g.drawImage(origen, 0, 0, destino.getWidth(), destino.getHeight(), null); // Dibuja la imagen al tamaño nuevo.
        g.dispose(); // Libera el pincel.
        return destino; // Entrega la imagen nueva.
    }

    private static Image faltante() { // Cuadro magenta que se usa cuando falta una imagen.
        BufferedImage img = new BufferedImage(16, 16, BufferedImage.TYPE_INT_ARGB); // Imagen de 16 x 16.
        Graphics2D g = img.createGraphics(); // Pincel para dibujar en ella.
        g.setColor(Color.MAGENTA); // Elige el color magenta.
        g.fillRect(0, 0, 16, 16); // Pinta todo el cuadro.
        g.dispose(); // Libera el pincel.
        return img; // Entrega el cuadro.
    }
}
