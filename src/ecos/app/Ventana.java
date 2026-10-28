package ecos.app;

import java.awt.BorderLayout; // Layout que divide la ventana en norte, sur, este, oeste y centro.
import java.awt.Dimension; // Representa un tamaño (ancho y alto).
import javax.swing.JComponent; // Tipo general de cualquier componente de Swing.
import javax.swing.JFrame; // La ventana principal de Swing.

public class Ventana extends JFrame {

    private final Navegador navegador = new Navegador(); // El navegador que cambia entre pantallas dentro de esta ventana.

    public Ventana() { // Constructor: arma la ventana.
        super("ECOS Memory Game"); // Pone el título de la ventana.
        setDefaultCloseOperation(EXIT_ON_CLOSE); // Al cerrar la ventana, se cierra todo el programa.
        setMinimumSize(new Dimension(960, 600)); // Tamaño mínimo al que se puede achicar.
        setSize(1280, 760); // Tamaño inicial de la ventana.
        setLocationRelativeTo(null); // La centra en la pantalla de la computadora.
        setLayout(new BorderLayout()); // Usa BorderLayout para ubicar la barra arriba y las pantallas al centro.
        add(navegador.getContenedor(), BorderLayout.CENTER); // Pone el contenedor de pantallas en el centro.
    }

    public Navegador getNavegador() { // Entrega el navegador de la ventana.
        return navegador; // Sirve para registrar pantallas desde el Main.
    }

    // Pone la barra superior (Atrás, tema, música) arriba de todas las pantallas.
    public void setBarraSuperior(JComponent barra) { // Recibe la barra que se quiere mostrar.
        add(barra, BorderLayout.NORTH); // La agrega en la parte de arriba (norte).
    }

    // Muestra la pantalla indicada y abre la ventana.
    public void iniciar(String idPrimeraPantalla) { // Recibe el id de la primera pantalla.
        setVisible(true); // Hace visible la ventana.
        navegador.ir(idPrimeraPantalla); // Muestra la primera pantalla.
    }
}