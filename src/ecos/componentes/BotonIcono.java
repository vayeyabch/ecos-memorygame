package ecos.componentes;

import ecos.app.Tema; // Para usar los colores del tema actual.
import java.awt.Color; // Clase de Java para representar un color.
import java.awt.Cursor; // Forma del puntero del mouse.
import java.awt.Dimension; // Representa un tamaño (ancho y alto).
import java.awt.Graphics; // Herramienta básica para dibujar.
import java.awt.Graphics2D; // Versión más completa de Graphics.
import java.awt.Rectangle; // Rectángulo (x, y, ancho, alto).
import java.awt.event.MouseAdapter; // Clase lista para escuchar el mouse sin implementar todos los métodos.
import java.awt.event.MouseEvent; // Datos del evento del mouse.
import java.util.function.BiConsumer; // Acción que recibe dos datos (aquí: el pincel y el área).
import javax.swing.JButton; // Botón de Swing: la clase de la que hereda esta.

public class BotonIcono extends JButton {

    private static final int LADO = 52; // Lado del cuadrado, en píxeles.
    private static final int BORDE = 4; // Grosor del borde.
    private static final int SOMBRA = 4; // Alto de la sombra dura de abajo.

    private BiConsumer<Graphics2D, Rectangle> dibujo = (g, r) -> { }; // Lo que se dibuja adentro del cuadrado; empieza sin dibujar nada.
    private boolean apretado = false; // true = el botón está siendo apretado.
    private final Runnable alCambiarTema = this::repaint; // Acción que repinta el botón cuando cambia el tema.

    public BotonIcono() { // Constructor: no recibe nada.
        setContentAreaFilled(false); // Quita el fondo normal de Swing.
        setBorderPainted(false); // Quita el borde normal de Swing.
        setFocusPainted(false); // Quita el recuadro de foco de Swing.
        setOpaque(false); // Es transparente: el dibujo se hace a mano.
        setRolloverEnabled(false); // Quita el cambio de color de Swing al pasar el mouse.
        setCursor(Cursor.getPredefinedCursor(Cursor.HAND_CURSOR)); // Pone la manito al pasar el mouse.
        addMouseListener(new MouseAdapter() { // Escucha el mouse para animar el botón.
            @Override // Indica que se reemplaza un método de MouseAdapter.
            public void mousePressed(MouseEvent e) { apretado = true; repaint(); } // Al apretar, el botón baja.
            @Override // Indica que se reemplaza un método de MouseAdapter.
            public void mouseReleased(MouseEvent e) { apretado = false; repaint(); } // Al soltar, vuelve a su lugar.
            @Override // Indica que se reemplaza un método de MouseAdapter.
            public void mouseExited(MouseEvent e) { apretado = false; repaint(); } // Al salir el mouse, vuelve a su lugar.
        });
    }

    // Define qué se dibuja adentro del cuadrado.
    public void setDibujo(BiConsumer<Graphics2D, Rectangle> dibujo) { // Recibe la acción que dibuja.
        this.dibujo = dibujo; // La guarda.
        repaint(); // Repinta.
    }

    @Override // Indica que se reemplaza un método de JButton.
    public void addNotify() { // Swing lo llama cuando el botón entra a una ventana.
        super.addNotify(); // Deja que Swing haga lo suyo primero.
        Tema.alCambiar(alCambiarTema); // Empieza a escuchar los cambios de tema.
    }

    @Override // Indica que se reemplaza un método de JButton.
    public void removeNotify() { // Swing lo llama cuando el botón sale de la ventana.
        Tema.quitar(alCambiarTema); // Deja de escuchar los cambios de tema.
        super.removeNotify(); // Deja que Swing haga lo suyo.
    }

    @Override // Indica que se reemplaza un método de JButton.
    public Dimension getPreferredSize() { // Tamaño que necesita el botón.
        return new Dimension(LADO, LADO + SOMBRA); // Un cuadrado más el espacio de la sombra.
    }

    @Override // Indica que se reemplaza el método de dibujo de JButton.
    protected void paintComponent(Graphics g) { // Swing lo llama cada vez que hay que dibujar el botón.
        Graphics2D g2 = (Graphics2D) g.create(); // Copia del pincel para no afectar al original.
        int y = apretado ? 3 : 0; // Baja 3 píxeles al apretar.
        int sombra = apretado ? 1 : SOMBRA; // La sombra se achica al apretar.
        g2.setColor(Tema.sombra()); // Color de la sombra dura.
        g2.fillRect(0, y + sombra, LADO, LADO); // Dibuja la sombra corrida hacia abajo.
        g2.setColor(Tema.borde()); // Color del borde.
        g2.fillRect(0, y, LADO, LADO); // Dibuja el cuadrado del borde.
        g2.setColor(Tema.panelAlto()); // Color de la línea clara interior.
        g2.fillRect(BORDE, y + BORDE, LADO - BORDE * 2, LADO - BORDE * 2); // Dibuja la línea clara.
        g2.setColor(Tema.panel()); // Color de relleno.
        g2.fillRect(BORDE + 2, y + BORDE + 2, LADO - (BORDE + 2) * 2, LADO - (BORDE + 2) * 2); // Dibuja el relleno.
        dibujo.accept(g2, new Rectangle(BORDE, y + BORDE, LADO - BORDE * 2, LADO - BORDE * 2)); // Dibuja el icono adentro.
        g2.dispose(); // Libera el pincel.
    }

    @Override // Indica que se reemplaza un método de JComponent.
    public Dimension getMinimumSize() { // Tamaño mínimo del componente.
        return getPreferredSize(); // Nunca se achica más de lo que necesita.
    }

    @Override // Indica que se reemplaza un método de JComponent.
    public Dimension getMaximumSize() { // Tamaño máximo del componente.
        return getPreferredSize(); // Nunca se estira más de lo que necesita.
    }
}
