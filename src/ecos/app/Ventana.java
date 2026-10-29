package ecos.app;

import java.awt.Dimension; // Representa un tamaño (ancho y alto).
import java.awt.event.ComponentAdapter; // Clase lista para escuchar cambios de tamaño sin implementar todos los métodos.
import java.awt.event.ComponentEvent; // Datos del evento de cambio de tamaño.
import javax.swing.JComponent; // Tipo general de cualquier componente de Swing.
import javax.swing.JFrame; // La ventana principal de Swing.
import javax.swing.JLayeredPane; // Panel con capas: permite poner cosas encima de otras.

public class Ventana extends JFrame {

    private final Navegador navegador = new Navegador(); // El navegador que cambia entre pantallas dentro de esta ventana.

    public Ventana() { // Constructor: arma la ventana.
        super("ECOS Memory Game"); // Pone el título de la ventana.
        setDefaultCloseOperation(EXIT_ON_CLOSE); // Al cerrar la ventana, se cierra todo el programa.
        setMinimumSize(new Dimension(1000, 640)); // Tamaño mínimo al que se puede achicar.
        setSize(1280, 760); // Tamaño inicial (por si el usuario la saca de pantalla completa).
        setLocationRelativeTo(null); // La centra en la pantalla de la computadora.
        setExtendedState(MAXIMIZED_BOTH); // Abre la ventana en pantalla completa (maximizada).
        setContentPane(navegador.getContenedor()); // Las pantallas ocupan toda la ventana.
    }

    public Navegador getNavegador() { // Entrega el navegador de la ventana.
        return navegador; // Sirve para registrar pantallas desde el Main.
    }

    // Pone la barra superior (Atrás, tema, música) flotando arriba de todas las pantallas.
    public void setBarraSuperior(JComponent barra) { // Recibe la barra que se quiere mostrar.
        JLayeredPane capas = getLayeredPane(); // Pide el panel de capas de la ventana.
        capas.add(barra, JLayeredPane.PALETTE_LAYER); // Pone la barra en una capa que queda encima de las pantallas.
        acomodar(barra); // Le da su posición y tamaño.
        capas.addComponentListener(new ComponentAdapter() { // Escucha cuando la ventana cambia de tamaño.
            @Override // Indica que se reemplaza un método de ComponentAdapter.
            public void componentResized(ComponentEvent e) { // Se ejecuta cada vez que cambia el tamaño.
                acomodar(barra); // Vuelve a acomodar la barra para que siga ocupando todo el ancho.
            }
        });
    }

    private void acomodar(JComponent barra) { // Pone la barra arriba, ocupando todo el ancho.
        barra.setBounds(0, 0, getLayeredPane().getWidth(), barra.getPreferredSize().height); // Posición (0, 0), todo el ancho y su alto natural.
    }

    // Muestra la pantalla indicada y abre la ventana.
    public void iniciar(String idPrimeraPantalla) { // Recibe el id de la primera pantalla.
        setVisible(true); // Hace visible la ventana.
        navegador.ir(idPrimeraPantalla); // Muestra la primera pantalla.
    }
}
