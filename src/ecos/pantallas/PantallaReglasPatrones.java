package ecos.pantallas;

// Clases del propio proyecto que se usan aquí.
import ecos.app.Pantalla;
import ecos.componentes.BotonMenu;
import ecos.componentes.Etiqueta;
import ecos.componentes.ListaReglas;
import ecos.componentes.PanelPixel;
import ecos.recursos.Sonido;
// Librería gráfica AWT, Component para cualquier elemento de la pantalla, Dimension para los tamaños y GridBagLayout
// para acomodar en una cuadrícula flexible.
import java.awt.Component;
import java.awt.Dimension;
import java.awt.GridBagLayout;
// Librería Swing, BorderFactory para los bordes, Box para los espacios entre componentes, BoxLayout para apilar en fila
// o columna y JComponent para la base de los componentes.
import javax.swing.BorderFactory;
import javax.swing.Box;
import javax.swing.BoxLayout;
import javax.swing.JComponent;

// Pantalla con las reglas del modo patrones y el botón para comenzar.
public class PantallaReglasPatrones extends Pantalla {

    // Qué hacer al tocar Comenzar (lo pone el controlador).
    private Runnable alComenzar = () -> { };

    public PantallaReglasPatrones() {
        super("reglas_patrones", "menu");
        setLayout(new GridBagLayout());
        // Se arma la lista de reglas y el botón Comenzar, que solo avisa al controlador.
        setBorder(BorderFactory.createEmptyBorder(MARGEN_SUPERIOR, 16, 24, 16));

        ListaReglas reglas = new ListaReglas(460,
                "Mirá cómo se iluminan las casillas de la matriz de 4x4 y memorizá el orden.",
                "Empezás con 3 casillas. Cada nivel que superás suma 1 casilla más.",
                "Tenés 12 segundos en total para repetir el patrón y 2 segundos entre casilla y casilla.",
                "Si te equivocás o se acaba el tiempo, termina la partida.");
        BotonMenu comenzar = new BotonMenu("¡Comenzar!", true);
        comenzar.addActionListener(e -> { Sonido.efecto("click"); alComenzar.run(); });

        // La tarjeta del centro, ancho mínimo de 560 px y margen adentro.
        PanelPixel tarjeta = new PanelPixel() {
            @Override
            public Dimension getPreferredSize() {
                return new Dimension(Math.max(560, super.getPreferredSize().width), super.getPreferredSize().height);
            }
        };
        tarjeta.setLayout(new BoxLayout(tarjeta, BoxLayout.Y_AXIS));
        tarjeta.setPadding(18);
        // Se agregan el título, las reglas y el botón, alineados a la izquierda y con espacio entre ellos.
        JComponent[] piezas = {Etiqueta.titulo("Modo patrones", 16f, Etiqueta.Rol.TEXTO), reglas, comenzar.anchoCompleto()};
        for (JComponent c : piezas) {
            c.setAlignmentX(Component.LEFT_ALIGNMENT);
            tarjeta.add(c);
            tarjeta.add(Box.createVerticalStrut(14));
        }
        add(tarjeta);
    }

    // Para el controlador, se ejecuta al tocar Comenzar.
    public void setAlComenzar(Runnable accion) { alComenzar = accion; }
}
