package ecos.app;

// Librería gráfica AWT, Dimension para los tamaños.
import java.awt.Dimension;
// Eventos del mouse y el teclado de AWT, ComponentAdapter para reaccionar a cambios de tamaño y ComponentEvent para los
// datos de esos cambios.
import java.awt.event.ComponentAdapter;
import java.awt.event.ComponentEvent;
// Librería Swing, JComponent para la base de los componentes, JFrame para la ventana y JLayeredPane para las capas.
import javax.swing.JComponent;
import javax.swing.JFrame;
import javax.swing.JLayeredPane;

// Ventana principal del juego, tiene la barra de arriba, el navegador con las pantallas y el botón de la esquina.
public class Ventana extends JFrame {

    // El navegador que cambia entre pantallas dentro de esta ventana.
    private final Navegador navegador = new Navegador();

    // Constructor que arma la ventana.
    public Ventana() {
        // Pone el título de la ventana. Al cerrar la ventana, se cierra todo el programa. Tamaño mínimo al que se puede
        // achicar. Tamaño inicial (por si el usuario la saca de pantalla completa).
        super("ECOS Memory Game");
        setDefaultCloseOperation(EXIT_ON_CLOSE);
        setMinimumSize(new Dimension(1000, 640));
        setSize(1280, 760);
        // La centra en la pantalla de la computadora. Abre la ventana en pantalla completa (maximizada). Las pantallas ocupan
        // toda la ventana.
        setLocationRelativeTo(null);
        setExtendedState(MAXIMIZED_BOTH);
        setContentPane(navegador.getContenedor());
    }

    // Entrega el navegador de la ventana.
    public Navegador getNavegador() {
        // Sirve para registrar pantallas desde el Main.
        return navegador;
    }

    // Pone la barra superior (Atrás, tema, música) flotando arriba de todas las pantallas.
    // Recibe la barra que se quiere mostrar.
    public void setBarraSuperior(JComponent barra) {
        // Pide el panel de capas de la ventana, pone la barra en una capa encima de las pantallas con su posición y tamaño,
        // y escucha cuando la ventana cambia de tamaño.
        JLayeredPane capas = getLayeredPane();
        capas.add(barra, JLayeredPane.PALETTE_LAYER);
        acomodar(barra);
        capas.addComponentListener(new ComponentAdapter() {
            // Se ejecuta cada vez que cambia el tamaño.
            @Override
            public void componentResized(ComponentEvent e) {
                // Vuelve a acomodar la barra para que siga ocupando todo el ancho.
                acomodar(barra);
            }
        });
    }

    // Pone la barra arriba, ocupando todo el ancho.
    private void acomodar(JComponent barra) {
        // Posición (0, 0), todo el ancho y su alto natural.
        barra.setBounds(0, 0, getLayeredPane().getWidth(), barra.getPreferredSize().height);
    }

    // Pone un componente (por ejemplo el botón "i") pegado a la esquina inferior derecha de la ventana.
    // Recibe el componente que se quiere poner.
    public void setEsquinaInferior(JComponent boton) {
        // Panel de capas de la ventana, que va encima de las pantallas y escucha cuando cambia su tamaño.
        JLayeredPane capas = getLayeredPane();
        capas.add(boton, JLayeredPane.PALETTE_LAYER);
        ubicarEsquina(boton);
        capas.addComponentListener(new ComponentAdapter() {
            // Se ejecuta cada vez que cambia el tamaño.
            @Override
            public void componentResized(ComponentEvent e) {
                // Lo vuelve a acomodar.
                ubicarEsquina(boton);
            }
        });
    }

    // Lo pega a la esquina inferior derecha, con 16 px de margen.
    private void ubicarEsquina(JComponent boton) {
        // Tamaño del componente.
        Dimension d = boton.getPreferredSize();
        boton.setBounds(getLayeredPane().getWidth() - d.width - 16, getLayeredPane().getHeight() - d.height - 16, d.width, d.height);
    }


    // Muestra la pantalla indicada y abre la ventana.
    // Recibe el id de la primera pantalla.
    public void iniciar(String idPrimeraPantalla) {
        // Hace visible la ventana. Muestra la primera pantalla.
        setVisible(true);
        navegador.ir(idPrimeraPantalla);
    }
}
