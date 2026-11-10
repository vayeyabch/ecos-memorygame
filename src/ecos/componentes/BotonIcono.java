package ecos.componentes;

// Clases del propio proyecto que se usan aquí.
import ecos.app.Tema;
// Librería gráfica AWT, Cursor para el cursor del mouse, Dimension para los tamaños, Graphics para dibujar, Graphics2D
// para dibujar con más opciones y Rectangle para los rectángulos.
import java.awt.Cursor;
import java.awt.Dimension;
import java.awt.Graphics;
import java.awt.Graphics2D;
import java.awt.Rectangle;
// Eventos del mouse y el teclado de AWT, MouseAdapter para reaccionar al mouse y MouseEvent para los datos de un clic.
import java.awt.event.MouseAdapter;
import java.awt.event.MouseEvent;
// Acciones de Java, BiConsumer para una acción que recibe dos datos.
import java.util.function.BiConsumer;
// Librería Swing, JButton para los botones.
import javax.swing.JButton;

// Botón cuadrado con un dibujo propio; baja al apretarlo y cambia de color con el tema.
public class BotonIcono extends JButton {

    // Lado del cuadrado en píxeles, grosor del borde y alto de la sombra dura de abajo.
    private static final int LADO = 52;
    private static final int BORDE = 4;
    private static final int SOMBRA = 4;

    // Lo que se dibuja adentro del cuadrado, que empieza vacío, si el botón está apretado y la acción que lo repinta cuando
    // cambia el tema.
    private BiConsumer<Graphics2D, Rectangle> dibujo = (g, r) -> { };
    private boolean apretado = false;
    private final Runnable alCambiarTema = this::repaint;

    // Constructor sin parámetros.
    public BotonIcono() {
        // Quita el fondo normal de Swing. Quita el borde normal de Swing. Quita el recuadro de foco de Swing. Es
        // transparente, el dibujo se hace a mano.
        setContentAreaFilled(false);
        setBorderPainted(false);
        setFocusPainted(false);
        setOpaque(false);
        // Quita el cambio de color de Swing al pasar el mouse. Pone la manito al pasar el mouse. Escucha el mouse para animar el botón.
        setRolloverEnabled(false);
        setCursor(Cursor.getPredefinedCursor(Cursor.HAND_CURSOR));
        addMouseListener(new MouseAdapter() {
            // Al apretar, el botón baja.
            @Override
            public void mousePressed(MouseEvent e) { apretado = true; repaint(); }
            // Al soltar, vuelve a su lugar.
            @Override
            public void mouseReleased(MouseEvent e) { apretado = false; repaint(); }
            // Al salir el mouse, vuelve a su lugar.
            @Override
            public void mouseExited(MouseEvent e) { apretado = false; repaint(); }
        });
    }

    // Define qué se dibuja adentro del cuadrado.
    // Recibe la acción que dibuja.
    public void setDibujo(BiConsumer<Graphics2D, Rectangle> dibujo) {
        this.dibujo = dibujo;
        repaint();
    }

    // Swing lo llama cuando el botón entra a una ventana.
    @Override
    public void addNotify() {
        // Deja que Swing haga lo suyo primero. Empieza a escuchar los cambios de tema.
        super.addNotify();
        Tema.alCambiar(alCambiarTema);
    }

    // Swing lo llama cuando el botón sale de la ventana.
    @Override
    public void removeNotify() {
        // Deja de escuchar los cambios de tema. Deja que Swing haga lo suyo.
        Tema.quitar(alCambiarTema);
        super.removeNotify();
    }

    // Tamaño que necesita el botón.
    @Override
    public Dimension getPreferredSize() {
        // Un cuadrado más el espacio de la sombra.
        return new Dimension(LADO, LADO + SOMBRA);
    }

    // Swing lo llama cada vez que hay que dibujar el botón.
    @Override
    protected void paintComponent(Graphics g) {
        // Usa una copia del pincel para no afectar al original, baja 3 píxeles al apretar, achica la sombra y define su
        // color.
        Graphics2D g2 = (Graphics2D) g.create();
        int y = apretado ? 3 : 0;
        int sombra = apretado ? 1 : SOMBRA;
        g2.setColor(Tema.sombra());
        // Dibuja la sombra corrida hacia abajo. Color del borde. Dibuja el cuadrado del borde. Color de la línea clara interior.
        g2.fillRect(0, y + sombra, LADO, LADO);
        g2.setColor(Tema.borde());
        g2.fillRect(0, y, LADO, LADO);
        g2.setColor(Tema.panelAlto());
        // Dibuja la línea clara. Color de relleno. Dibuja el relleno. Dibuja el icono adentro.
        g2.fillRect(BORDE, y + BORDE, LADO - BORDE * 2, LADO - BORDE * 2);
        g2.setColor(Tema.panel());
        g2.fillRect(BORDE + 2, y + BORDE + 2, LADO - (BORDE + 2) * 2, LADO - (BORDE + 2) * 2);
        dibujo.accept(g2, new Rectangle(BORDE, y + BORDE, LADO - BORDE * 2, LADO - BORDE * 2));
        // Libera el pincel.
        g2.dispose();
    }

    // Tamaño mínimo del componente.
    @Override
    public Dimension getMinimumSize() {
        // Nunca se achica más de lo que necesita.
        return getPreferredSize();
    }

    // Tamaño máximo del componente.
    @Override
    public Dimension getMaximumSize() {
        // Nunca se estira más de lo que necesita.
        return getPreferredSize();
    }
}