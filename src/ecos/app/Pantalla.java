package ecos.app;

import ecos.recursos.Sonido; // Para poner la música de la pantalla al entrar.
import ecos.recursos.Sprites; // Para obtener el fondo animado.
import java.awt.Graphics; // Herramienta para dibujar en pantalla.
import java.awt.Graphics2D; // Versión más completa de Graphics.
import java.awt.RenderingHints; // Opciones de calidad al dibujar.
import javax.swing.ImageIcon; // Imagen de Swing (aquí el gif del fondo).
import javax.swing.JPanel; // Panel de Swing: el contenedor del que heredan las pantallas.

public abstract class Pantalla extends JPanel { // Clase base abstracta: no se crea directamente, solo se hereda.

    public static final int MARGEN_SUPERIOR = 72; // Espacio que dejan las pantallas arriba para que no las tape la barra superior.

    private final String id; // Nombre único de la pantalla; sirve para navegar hacia ella.
    private final String musica; // Nombre de la canción que suena en esta pantalla.
    private Navegador navegador; // El navegador que la controla; se asigna cuando se registra.
    private Runnable accionEntrar = () -> { }; // Qué hacer al entrar (empieza sin hacer nada).
    private Runnable accionSalir = () -> { }; // Qué hacer al salir (empieza sin hacer nada).
    private boolean conFondo = true; // true = dibuja el fondo animado; false = no lo dibuja.

    protected Pantalla(String id, String musica) { // Constructor: las hijas lo llaman con super("id", "musica").
        this.id = id; // Guarda el id de la pantalla.
        this.musica = musica; // Guarda el nombre de su música.
        setOpaque(true); // Indica que el panel pinta todo su fondo.
        Tema.alCambiar(this::repaint); // Pide repintar la pantalla cada vez que cambie el tema.
    }

    public String getId() { // Devuelve el id de la pantalla.
        return id; // Entrega el nombre guardado.
    }

    // Lo llama el Navegador al registrar la pantalla.
    void setNavegador(Navegador navegador) { // Sin "public": solo lo ve el paquete app.
        this.navegador = navegador; // Guarda el navegador para poder moverse entre pantallas.
    }

    protected void irA(String idPantalla) { // Va a otra pantalla por su id.
        if (navegador != null) { // Solo si la pantalla ya está registrada en un navegador.
            navegador.ir(idPantalla); // Le pide al navegador que muestre esa pantalla.
        }
    }

    protected void volver() { // Regresa a la pantalla anterior.
        if (navegador != null) { // Solo si ya tiene navegador.
            navegador.atras(); // Le pide al navegador que retroceda una pantalla.
        }
    }

    // Si se pone false, la pantalla no dibuja el fondo animado.
    protected void setConFondo(boolean conFondo) { // Activa o desactiva el fondo.
        this.conFondo = conFondo; // Guarda la decisión.
    }

    // Se ejecuta cada vez que la pantalla se muestra.
    public void alEntrar() { // Las hijas pueden sobrescribirlo para hacer cosas al entrar.
        Sonido.musica(musica); // Pone la música de esta pantalla.
        repaint(); // Repinta la pantalla.
        accionEntrar.run(); // Avisa a quien quiera enterarse (por ejemplo, un controlador).
    }

    // El controlador usa esto para arrancar cuando la pantalla se muestra.
    public void setAlEntrar(Runnable accion) { // Recibe lo que se quiere ejecutar al entrar.
        this.accionEntrar = accion; // La guarda.
    }

    // El controlador usa esto para parar sus relojes cuando la pantalla se oculta.
    public void setAlSalir(Runnable accion) { // Recibe lo que se quiere ejecutar al salir.
        this.accionSalir = accion; // La guarda.
    }

    // Se ejecuta cuando la pantalla se oculta.
    public void alSalir() { // Las hijas lo usan si necesitan limpiar algo.
        ecos.componentes.CapaModal.cerrarTodas(); // Cierra cualquier ventana abierta encima.
        accionSalir.run(); // Avisa a quien quiera enterarse.
    }

    @Override // Indica que se reemplaza el método de dibujo de JPanel.
    protected void paintComponent(Graphics g) { // Swing lo llama cada vez que hay que dibujar el panel.
        super.paintComponent(g); // Dibuja primero lo básico del panel.
        if (conFondo) { // Solo si la pantalla tiene fondo activado.
            ImageIcon fondo = Sprites.fondo(); // Pide el fondo del tema actual (montaña o noche).
            int ancho = fondo.getIconWidth(); // Ancho original del gif.
            int alto = fondo.getIconHeight(); // Alto original del gif.
            if (ancho <= 0 || alto <= 0) { // Si el gif todavía no cargó.
                return; // No dibuja nada.
            }
            double escala = Math.max(getWidth() / (double) ancho, getHeight() / (double) alto); // Escala mínima para cubrir toda la pantalla sin deformar.
            int w = (int) Math.ceil(ancho * escala); // Ancho final del fondo.
            int h = (int) Math.ceil(alto * escala); // Alto final del fondo.
            Graphics2D g2 = (Graphics2D) g.create(); // Copia del pincel para no afectar al original.
            g2.setRenderingHint(RenderingHints.KEY_INTERPOLATION, RenderingHints.VALUE_INTERPOLATION_NEAREST_NEIGHBOR); // Sin suavizar: se mantiene el pixel art.
            g2.drawImage(fondo.getImage(), (getWidth() - w) / 2, getHeight() - h, w, h, this); // Dibuja el fondo centrado y pegado abajo (lo que sobra se corta arriba).
            g2.dispose(); // Libera el pincel.
        }
    }
}
