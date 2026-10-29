package ecos.componentes;

import ecos.app.Tema; // Para usar los colores del tema actual.
import ecos.recursos.Fuentes; // Fuentes pixel del juego.
import ecos.recursos.Sonido; // Para los efectos de sonido.
import ecos.recursos.Sprites; // Para la imagen de la mascota.
import java.awt.Color; // Clase de Java para representar un color.
import java.awt.FlowLayout; // Layout para poner los botones en fila.
import java.awt.GridBagConstraints; // Reglas de posición dentro del GridBagLayout.
import java.awt.GridBagLayout; // Layout en cuadrícula para acomodar el contenido.
import java.awt.Insets; // Márgenes entre componentes.
import java.awt.Point; // Posición de la ventana en la pantalla.
import java.awt.Window; // Ventana dueña del diálogo.
import javax.swing.ImageIcon; // Imagen lista para ponerse en un JLabel.
import javax.swing.JDialog; // Ventana secundaria de Swing.
import javax.swing.JLabel; // Etiqueta para texto o imágenes.
import javax.swing.JPanel; // Panel simple para agrupar piezas.
import javax.swing.Timer; // Temporizador para la animación de sacudida.

public class DialogoComprarOjo extends JDialog {

    private static final int TAM_MASCOTA = 80; // Tamaño en píxeles de la mascota.
    private static final int[] SACUDIDA = {-10, 10, -8, 8, -4, 4, 0}; // Desplazamientos horizontales de la sacudida, en píxeles.

    private final int costo; // Monedas que cuesta el ojo espía.
    private final int monedas; // Monedas que tiene el jugador.
    private final JLabel mensaje = new JLabel(" ", JLabel.CENTER); // Mensaje de aviso, por ejemplo cuando no alcanzan las monedas.
    private Runnable alComprar = () -> { }; // Acción que se ejecuta al comprar; empieza sin hacer nada.
    private boolean comprado = false; // true si la compra se hizo.

    public DialogoComprarOjo(Window padre, int costo, int monedas) { // Recibe el costo y las monedas del jugador.
        super(padre, ModalityType.APPLICATION_MODAL); // El diálogo bloquea la ventana de atrás hasta cerrarse.
        setUndecorated(true); // Quita la barra de título del sistema.
        activarTransparencia(); // Intenta dejar transparentes las esquinas.
        this.costo = costo; // Guarda el costo.
        this.monedas = monedas; // Guarda las monedas del jugador.

        PanelPixel raiz = new PanelPixel(new GridBagLayout()); // Panel principal con borde y sombra pixelados.
        raiz.setPadding(26); // Espacio interno de 26 píxeles.

        JLabel imagen = new JLabel(new ImageIcon(Sprites.escalar(Sprites.mascota(3), TAM_MASCOTA, TAM_MASCOTA))); // Mascota decorativa, escalada sin borrosidad.
        JLabel titulo = new JLabel("Ojo espía", JLabel.CENTER); // Título del diálogo.
        titulo.setFont(Fuentes.titulo(24f)); // El título usa la fuente de títulos.
        titulo.setForeground(Tema.acento()); // El título usa el color de acento.
        JLabel lblCosto = new JLabel("Costo: " + costo + " monedas", JLabel.CENTER); // Texto con el costo.
        lblCosto.setFont(Fuentes.texto(24f)); // Usa la fuente de texto normal.
        lblCosto.setForeground(Tema.texto()); // Usa el color de texto del tema.
        JLabel lblMonedas = new JLabel("Tus monedas: " + monedas, JLabel.CENTER); // Texto con las monedas del jugador.
        lblMonedas.setFont(Fuentes.texto(24f)); // Usa la fuente de texto normal.
        lblMonedas.setForeground(Tema.texto()); // Usa el color de texto del tema.
        mensaje.setFont(Fuentes.texto(20f)); // El mensaje usa la fuente de texto normal.
        mensaje.setForeground(Tema.peligro()); // El mensaje usa el color de peligro.

        BotonMenu btnComprar = new BotonMenu("Comprar", true); // Botón principal para comprar.
        BotonMenu btnCancelar = new BotonMenu("Cancelar", false); // Botón secundario para cancelar.
        btnComprar.addActionListener(e -> comprar()); // Al hacer clic intenta comprar el ojo.
        btnCancelar.addActionListener(e -> { Sonido.efecto("click"); dispose(); }); // Suena el click y cierra el diálogo.

        JPanel botones = new JPanel(new FlowLayout(FlowLayout.CENTER, 16, 0)); // Fila con los dos botones separados 16 píxeles.
        botones.setOpaque(false); // La hace transparente para que se vea el panel de atrás.
        botones.add(btnComprar); // Agrega el botón Comprar.
        botones.add(btnCancelar); // Agrega el botón Cancelar.

        GridBagConstraints c = new GridBagConstraints(); // Objeto que guarda la posición de cada pieza.
        c.gridx = 0; // Todo va en una sola columna.
        c.insets = new Insets(6, 8, 6, 8); // Margen alrededor de cada pieza.
        c.gridy = 0; raiz.add(imagen, c); // Primera fila: la mascota.
        c.gridy = 1; raiz.add(titulo, c); // Segunda fila: el título.
        c.gridy = 2; raiz.add(lblCosto, c); // Tercera fila: el costo.
        c.gridy = 3; raiz.add(lblMonedas, c); // Cuarta fila: las monedas del jugador.
        c.gridy = 4; raiz.add(mensaje, c); // Quinta fila: el mensaje de aviso.
        c.gridy = 5; c.insets = new Insets(14, 8, 6, 8); raiz.add(botones, c); // Sexta fila: los botones, con más espacio arriba.

        setContentPane(raiz); // Usa el panel principal como contenido del diálogo.
        pack(); // Ajusta el tamaño del diálogo a su contenido.
        setLocationRelativeTo(padre); // Lo centra respecto a la ventana de atrás.
    }

    // Define qué pasa cuando se compra el ojo (por ejemplo, restar las monedas).
    public void setAlComprar(Runnable accion) { // Recibe la acción que se quiere ejecutar.
        this.alComprar = accion; // La guarda para usarla cuando se compre.
    }

    public boolean fueComprado() { // Pregunta si la compra se hizo.
        return comprado; // Devuelve true si se compró.
    }

    private void comprar() { // Método interno: intenta comprar el ojo.
        if (monedas >= costo) { // Si le alcanzan las monedas
            comprado = true; // marca la compra como hecha.
            Sonido.efecto("comprar"); // Suena el efecto de compra.
            Sonido.efecto("ojo_espia"); // Suena el efecto del ojo espía al activarlo.
            dispose(); // Cierra el diálogo.
            alComprar.run(); // Avisa que se compró.
        } else { // Si no le alcanzan
            Sonido.efecto("sin_monedas"); // suena el efecto de sin monedas.
            mensaje.setText("No te alcanzan las monedas"); // Muestra el aviso.
            sacudir(); // Sacude el diálogo para llamar la atención.
        }
    }

    private void sacudir() { // Método interno: mueve el diálogo de lado a lado.
        Point origen = getLocation(); // Guarda la posición original.
        int[] paso = {0}; // Contador de pasos de la animación.
        Timer timer = new Timer(35, null); // Temporizador que mueve el diálogo cada 35 milisegundos.
        timer.addActionListener(e -> { // En cada cuadro de la animación
            setLocation(origen.x + SACUDIDA[paso[0]], origen.y); // mueve el diálogo al desplazamiento siguiente.
            paso[0]++; // Avanza al siguiente paso.
            if (paso[0] >= SACUDIDA.length) { // Si ya terminó todos los pasos
                timer.stop(); // detiene el temporizador.
            }
        });
        timer.start(); // Arranca la animación.
    }

    private void activarTransparencia() { // Método interno: deja transparentes las esquinas del diálogo.
        try {
            setBackground(new Color(0, 0, 0, 0)); // Fondo totalmente transparente.
        } catch (UnsupportedOperationException e) { // Si el sistema no lo permite
            // se deja el fondo normal, no pasa nada.
        }
    }
}