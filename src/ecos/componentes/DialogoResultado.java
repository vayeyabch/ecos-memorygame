package ecos.componentes;

import ecos.app.Tema; // Para usar los colores del tema actual.
import ecos.recursos.Fuentes; // Fuentes pixel del juego.
import ecos.recursos.Sonido; // Para los efectos de sonido.
import ecos.recursos.Sprites; // Para las mascotas y el gif de confeti.
import java.awt.Color; // Clase de Java para representar un color.
import java.awt.FlowLayout; // Layout para poner piezas en fila.
import java.awt.Graphics; // Herramienta básica para dibujar.
import java.awt.GridBagConstraints; // Reglas de posición dentro del GridBagLayout.
import java.awt.GridBagLayout; // Layout en cuadrícula para acomodar el contenido.
import java.awt.Insets; // Márgenes entre componentes.
import java.awt.Window; // Ventana dueña del diálogo.
import javax.swing.ImageIcon; // Imagen lista para ponerse en un JLabel.
import javax.swing.JDialog; // Ventana secundaria de Swing.
import javax.swing.JLabel; // Etiqueta para texto o imágenes.
import javax.swing.JPanel; // Panel simple para agrupar piezas.

public class DialogoResultado extends JDialog {

    private static final Color TEXTO_OSCURO = new Color(91, 37, 56); // Color de texto sobre fondo dorado.
    private static final int TAM_MASCOTA = 64; // Tamaño en píxeles de la mascota de cada jugador.

    private Runnable alVerPremios = () -> { }; // Acción del botón "Ver premios"; empieza sin hacer nada.

    // ganador: 1 o 2 si ganó ese jugador, 0 si nadie ganó (derrota).
    // nombre2 puede ser null cuando juega una sola persona.
    public DialogoResultado(Window padre, int ganador, String titulo,
            String nombre1, int intentos1, String nombre2, int intentos2, String premio) { // Recibe quién ganó, los textos y los intentos.
        super(padre, ModalityType.APPLICATION_MODAL); // El diálogo bloquea la ventana de atrás hasta cerrarse.
        setUndecorated(true); // Quita la barra de título del sistema.
        activarTransparencia(); // Intenta dejar transparentes las esquinas.

        boolean victoria = ganador != 0; // true si alguien ganó.
        Sonido.efecto(victoria ? "victoria" : "derrota"); // Suena victoria o derrota según el resultado.

        PanelPixel raiz = new PanelPixel(new GridBagLayout()); // Panel principal con borde y sombra pixelados.
        raiz.setPadding(26); // Espacio interno de 26 píxeles.

        JLabel lblTitulo = new JLabel(titulo, JLabel.CENTER); // Título del resultado.
        lblTitulo.setFont(Fuentes.titulo(24f)); // El título usa la fuente de títulos.
        lblTitulo.setForeground(Tema.acento()); // El título usa el color de acento.

        JPanel tarjetas = new JPanel(new FlowLayout(FlowLayout.CENTER, 18, 0)); // Fila con las tarjetas de los jugadores.
        tarjetas.setOpaque(false); // La hace transparente para que se vea el panel de atrás.
        tarjetas.add(crearTarjeta(1, nombre1, intentos1, ganador == 1)); // Tarjeta del jugador 1.
        if (nombre2 != null) { // Solo si hay un segundo jugador.
            tarjetas.add(crearTarjeta(2, nombre2, intentos2, ganador == 2)); // Tarjeta del jugador 2.
        }

        BotonMenu btnPremios = new BotonMenu("Ver premios", true); // Botón principal para ir a la pantalla de premios.
        BotonMenu btnCerrar = new BotonMenu("Cerrar", false); // Botón secundario para cerrar el diálogo.
        btnPremios.addActionListener(e -> { Sonido.efecto("click"); dispose(); alVerPremios.run(); }); // Suena el click, cierra y avisa que se quiere ver los premios.
        btnCerrar.addActionListener(e -> { Sonido.efecto("click"); dispose(); }); // Suena el click y cierra el diálogo.

        JPanel botones = new JPanel(new FlowLayout(FlowLayout.CENTER, 16, 0)); // Fila con los dos botones separados 16 píxeles.
        botones.setOpaque(false); // La hace transparente para que se vea el panel de atrás.
        botones.add(btnPremios); // Agrega el botón Ver premios.
        botones.add(btnCerrar); // Agrega el botón Cerrar.

        GridBagConstraints c = new GridBagConstraints(); // Objeto que guarda la posición de cada pieza.
        c.gridx = 0; // Todo va en una sola columna.
        c.insets = new Insets(8, 8, 8, 8); // Margen alrededor de cada pieza.
        c.gridy = 0; raiz.add(lblTitulo, c); // Primera fila: el título.
        c.gridy = 1; raiz.add(tarjetas, c); // Segunda fila: las tarjetas.
        if (victoria && premio != null) { // El premio solo se muestra cuando alguien ganó.
            PanelPixel panelPremio = new PanelPixel(new FlowLayout(FlowLayout.CENTER, 0, 0)); // Panel que resalta el premio.
            panelPremio.setPadding(10); // Espacio interno de 10 píxeles.
            panelPremio.setColorFondo(Tema.dorado()); // Fondo dorado para destacarlo.
            JLabel lblPremio = new JLabel("Premio: " + premio); // Texto del premio.
            lblPremio.setFont(Fuentes.texto(26f)); // El premio usa la fuente de texto normal.
            lblPremio.setForeground(TEXTO_OSCURO); // Texto oscuro para leerse sobre el dorado.
            panelPremio.add(lblPremio); // Mete el texto dentro del panel.
            c.gridy = 2; raiz.add(panelPremio, c); // Tercera fila: el premio.
        }
        c.gridy = 3; c.insets = new Insets(18, 8, 8, 8); raiz.add(botones, c); // Cuarta fila: los botones, con más espacio arriba.

        setContentPane(raiz); // Usa el panel principal como contenido del diálogo.
        if (victoria) { // El confeti solo aparece cuando alguien ganó.
            setGlassPane(new CapaConfeti()); // Pone el confeti como capa transparente encima de todo.
            getGlassPane().setVisible(true); // Hace visible esa capa.
        }
        pack(); // Ajusta el tamaño del diálogo a su contenido.
        setLocationRelativeTo(padre); // Lo centra respecto a la ventana de atrás.
    }

    // Define qué hace el botón "Ver premios" (por ejemplo, ir a la pantalla de premios).
    public void setAlVerPremios(Runnable accion) { // Recibe la acción que se quiere ejecutar.
        this.alVerPremios = accion; // La guarda para usarla cuando se toque el botón.
    }

    private void activarTransparencia() { // Método interno: deja transparentes las esquinas del diálogo.
        try {
            setBackground(new Color(0, 0, 0, 0)); // Fondo totalmente transparente.
        } catch (UnsupportedOperationException e) { // Si el sistema no lo permite
            // se deja el fondo normal, no pasa nada.
        }
    }

    private PanelPixel crearTarjeta(int mascota, String nombre, int intentos, boolean destacada) { // Método interno: arma la tarjeta de un jugador.
        PanelPixel tarjeta = new PanelPixel(new GridBagLayout()); // Tarjeta con borde y sombra.
        tarjeta.setPadding(14); // Espacio interno de 14 píxeles.
        Color texto = destacada ? TEXTO_OSCURO : Tema.texto(); // Color de texto según si es la tarjeta ganadora.
        if (destacada) { // Si es la tarjeta del ganador
            tarjeta.setColorFondo(Tema.dorado()); // la pinta de dorado.
        }

        JLabel imagen = new JLabel(new ImageIcon(Sprites.escalar(Sprites.mascota(mascota), TAM_MASCOTA, TAM_MASCOTA))); // Mascota del jugador, escalada sin borrosidad.
        JLabel lblNombre = new JLabel(nombre, JLabel.CENTER); // Nombre del jugador.
        lblNombre.setFont(Fuentes.texto(24f)); // El nombre usa la fuente de texto normal.
        lblNombre.setForeground(texto); // Le pone el color calculado.
        JLabel lblIntentos = new JLabel(String.valueOf(intentos), JLabel.CENTER); // Cantidad de intentos.
        lblIntentos.setFont(Fuentes.titulo(30f)); // Los intentos se ven grandes con la fuente de títulos.
        lblIntentos.setForeground(destacada ? TEXTO_OSCURO : Tema.acento()); // Le pone el color calculado.
        JLabel lblTexto = new JLabel("intentos", JLabel.CENTER); // Etiqueta que acompaña al número.
        lblTexto.setFont(Fuentes.texto(20f)); // Usa la fuente de texto normal.
        lblTexto.setForeground(texto); // Le pone el color calculado.

        GridBagConstraints c = new GridBagConstraints(); // Objeto que guarda la posición de cada pieza de la tarjeta.
        c.gridx = 0; // Todo va en una sola columna.
        c.insets = new Insets(4, 20, 4, 20); // Margen alrededor de cada pieza.
        c.gridy = 0; tarjeta.add(imagen, c); // Primera fila: la mascota.
        c.gridy = 1; tarjeta.add(lblNombre, c); // Segunda fila: el nombre.
        c.gridy = 2; tarjeta.add(lblIntentos, c); // Tercera fila: el número de intentos.
        c.gridy = 3; tarjeta.add(lblTexto, c); // Cuarta fila: la palabra intentos.
        return tarjeta; // Devuelve la tarjeta ya armada.
    }

    private static class CapaConfeti extends JPanel { // Capa transparente que dibuja el confeti animado encima del diálogo.

        private final ImageIcon confeti = Sprites.gif("gifs/confeti.gif"); // El gif de confeti.

        CapaConfeti() { // Constructor de la capa.
            setOpaque(false); // La hace transparente para ver lo que hay debajo.
        }

        @Override // Indica que se reemplaza un método de JPanel.
        public boolean contains(int x, int y) { // Swing lo usa para saber si el mouse está sobre la capa.
            return false; // Siempre responde que no, así los clics pasan a los botones de abajo.
        }

        @Override // Indica que se reemplaza el método de dibujo de JPanel.
        protected void paintComponent(Graphics g) { // Swing lo llama cada vez que hay que dibujar la capa.
            g.drawImage(confeti.getImage(), 0, 0, getWidth(), getHeight(), this); // Dibuja el gif estirado sobre todo el diálogo; así se anima solo.
        }
    }
}