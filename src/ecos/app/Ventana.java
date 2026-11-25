package ecos.app;

// Dimension guarda un ancho y un alto juntos. Se usa para los tamaños de la ventana y de los botones.
import java.awt.Dimension;
// ComponentAdapter ya trae vacíos los métodos para reaccionar a eventos de un componente. Solo se sobrescribe el que importa.
import java.awt.event.ComponentAdapter;
// ComponentEvent es el dato del evento que Swing entrega al método que reacciona. Aquí no se usa, pero el método lo pide.
import java.awt.event.ComponentEvent;
// JComponent es la clase base de todo lo visual en Swing. Sirve para aceptar la barra, el botón o cualquier pieza.
import javax.swing.JComponent;
// JFrame es la ventana de Swing, de la que hereda Ventana.
import javax.swing.JFrame;
// JLayeredPane es el panel con capas, que permite poner cosas encima de otras.
import javax.swing.JLayeredPane;

// Ventana principal. Es el marco que se ve en pantalla y tiene la barra de arriba,
// el navegador con las pantallas y el botón de la esquina.
public class Ventana extends JFrame {

    // El Navegador se crea junto con la ventana, antes de que corra el constructor.
    // Es private, por eso desde afuera se obtiene con getNavegador.
    private final Navegador navegador = new Navegador();

    // Deja la ventana lista: título, cierre, tamaños, posición, y conecta el Navegador como su contenido.
    public Ventana() {
        // Pone el título de la ventana. Siempre va de primero en el constructor.
        super("ECOS Memory Game");
        // Al darle a la X se cierra todo el programa y no solo la ventana.
        setDefaultCloseOperation(EXIT_ON_CLOSE);
        // La ventana no se puede achicar más que esto.
        setMinimumSize(new Dimension(1000, 640));
        // Tamaño inicial. Es el que toma si el usuario la des-maximiza.
        setSize(1280, 760);
        // Con null se centra en la pantalla.
        setLocationRelativeTo(null);
        // Se abre maximizada, a lo ancho y a lo alto.
        setExtendedState(MAXIMIZED_BOTH);
        // Lo que muestre el Navegador es lo que se ve dentro de la ventana.
        setContentPane(navegador.getContenedor());
    }

    // Entrega el mismo Navegador de la ventana. Main lo necesita para registrar pantallas
    // y dárselo a la barra y al botón i. No se puede acceder directo porque el atributo es private.
    public Navegador getNavegador() {
        return navegador;
    }

    // Pone la barra de arriba flotando sobre las pantallas y hace que se reajuste cuando cambia el tamaño de la ventana.
    public void setBarraSuperior(JComponent barra) {
        // Panel con capas que todo JFrame trae.
        JLayeredPane capas = getLayeredPane();
        // La barra va en la capa de arriba, por eso queda encima de las pantallas.
        capas.add(barra, JLayeredPane.PALETTE_LAYER);
        // Se acomoda ahora.
        acomodar(barra);
        // Escucha que Swing ejecuta cada vez que cambia el tamaño de las capas.
        // Es una clase sin nombre que solo sobrescribe componentResized, el evento de cambio de tamaño.
        capas.addComponentListener(new ComponentAdapter() {
            @Override
            public void componentResized(ComponentEvent e) {
                // Vuelve a acomodar la barra con el tamaño nuevo.
                acomodar(barra);
            }
        });
    }

    // Le da a la barra su posición y tamaño: arriba a la izquierda, tan ancha como la ventana
    // y con la altura que la propia barra pide. Se llama al inicio y cada vez que cambia el tamaño.
    private void acomodar(JComponent barra) {
        barra.setBounds(0, 0, getLayeredPane().getWidth(), barra.getPreferredSize().height);
    }

    // Pone una pieza, como el botón i, en la capa de arriba y la mantiene pegada a la esquina
    // inferior derecha aunque cambie el tamaño de la ventana.
    public void setEsquinaInferior(JComponent boton) {
        JLayeredPane capas = getLayeredPane();
        // En la capa de arriba para que quede encima de las pantallas.
        capas.add(boton, JLayeredPane.PALETTE_LAYER);
        // Se ubica ahora.
        ubicarEsquina(boton);
        // Cada vez que cambia el tamaño de la ventana, se vuelve a ubicar.
        capas.addComponentListener(new ComponentAdapter() {
            @Override
            public void componentResized(ComponentEvent e) {
                ubicarEsquina(boton);
            }
        });
    }

    // Calcula y asigna la posición del botón para dejarlo abajo a la derecha con 16 px de margen.
    private void ubicarEsquina(JComponent boton) {
        // Tamaño que el botón pide por sí mismo.
        Dimension d = boton.getPreferredSize();
        // x es el ancho de la ventana menos el ancho del botón menos el margen.
        // y es el alto de la ventana menos el alto del botón menos el margen.
        boton.setBounds(getLayeredPane().getWidth() - d.width - 16, getLayeredPane().getHeight() - d.height - 16, d.width, d.height);
    }

    // Abre la ventana y muestra la primera pantalla. Main la llama con intro.
    public void iniciar(String idPrimeraPantalla) {
        // Hace visible la ventana.
        setVisible(true);
        // Le pide al Navegador que muestre esa pantalla.
        navegador.ir(idPrimeraPantalla);
    }
}