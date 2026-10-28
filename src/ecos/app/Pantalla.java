package ecos.app;

import ecos.recursos.Sonido; // Para poner la música de la pantalla al entrar.
import ecos.recursos.Sprites; // Para obtener el fondo animado.
import java.awt.Graphics; // Herramienta para dibujar en pantalla.
import javax.swing.ImageIcon; // Imagen de Swing (aquí el gif del fondo).
import javax.swing.JPanel; // Panel de Swing: el contenedor del que heredan las pantallas.

public abstract class Pantalla extends JPanel { // Clase base abstracta: no se crea directamente, solo se hereda.

    private final String id; // Nombre único de la pantalla; sirve para navegar hacia ella.
    private final String musica; // Nombre de la canción que suena en esta pantalla.
    private Navegador navegador; // El navegador que la controla; se asigna cuando se registra.
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
    }

    // Se ejecuta cuando la pantalla se oculta.
    public void alSalir() { // Vacío a propósito: las hijas lo usan si necesitan limpiar algo.
    }

    @Override // Indica que se reemplaza el método de dibujo de JPanel.
    protected void paintComponent(Graphics g) { // Swing lo llama cada vez que hay que dibujar el panel.
        super.paintComponent(g); // Dibuja primero lo básico del panel.
        if (conFondo) { // Solo si la pantalla tiene fondo activado.
            ImageIcon fondo = Sprites.fondo(); // Pide el fondo del tema actual (montaña o noche).
            g.drawImage(fondo.getImage(), 0, 0, getWidth(), getHeight(), this); // Lo dibuja estirado para cubrir todo el panel.
        }
    }
}