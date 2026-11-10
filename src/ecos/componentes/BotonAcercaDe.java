package ecos.componentes;

// Clases del propio proyecto que se usan aquí.
import ecos.app.Navegador;
import ecos.app.Tema;
import ecos.recursos.Sonido;
// Librería gráfica AWT, Color para los colores, Cursor para el cursor del mouse, Dimension para los tamaños, Graphics
// para dibujar y Graphics2D para dibujar con más opciones.
import java.awt.Color;
import java.awt.Cursor;
import java.awt.Dimension;
import java.awt.Graphics;
import java.awt.Graphics2D;
// Eventos del mouse y el teclado de AWT, MouseAdapter para reaccionar al mouse y MouseEvent para los datos de un clic.
import java.awt.event.MouseAdapter;
import java.awt.event.MouseEvent;
// Utilidades de Java, Set para los conjuntos.
import java.util.Set;
// Librería Swing, JButton para los botones.
import javax.swing.JButton;

// Botón cuadrado con una "i" que abre la ventana "Acerca de". Va en la esquina inferior derecha.
public class BotonAcercaDe extends JButton {

    // Pantallas donde no se muestra (la intro y los juegos), lado del cuadrado en píxeles, grosor del borde y alto de la
    // sombra dura de abajo.
    private static final Set<String> OCULTO_EN = Set.of("intro", "clasico", "patrones");
    private static final int LADO = 40;
    private static final int BORDE = 4;
    private static final int SOMBRA = 4;

    // True = el botón está siendo apretado. Acción que repinta el botón cuando cambia el tema.
    private boolean apretado = false;
    private final Runnable alCambiarTema = this::repaint;

    // Recibe el navegador para saber cuándo cambia la pantalla.
    public BotonAcercaDe(Navegador navegador) {
        // Quita el fondo normal de Swing. Quita el borde normal de Swing. Quita el recuadro de foco de Swing. Es
        // transparente, el dibujo se hace a mano.
        setContentAreaFilled(false);
        setBorderPainted(false);
        setFocusPainted(false);
        setOpaque(false);
        // Pone la manito al pasar el mouse. Escucha el mouse para animar el botón.
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
        // Al hacer clic suena y abre la ventana "Acerca de". Se esconde o se muestra según la pantalla. Empieza oculto hasta
        // que se muestre la primera pantalla.
        addActionListener(e -> { Sonido.efecto("click"); VentanaAcercaDe.mostrar(this); });
        navegador.alCambiarPantalla(id -> setVisible(!OCULTO_EN.contains(id)));
        setVisible(false);
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
        // Dibuja la sombra corrida hacia abajo. Color del borde. Dibuja el cuadrado del borde. Color amigable de relleno (el
        // mismo rosa de los botones).
        g2.fillRect(0, y + sombra, LADO, LADO);
        g2.setColor(Tema.borde());
        g2.fillRect(0, y, LADO, LADO);
        g2.setColor(Tema.rosa());
        // Dibuja el relleno. Dibuja la "i" encima. Libera el pincel.
        g2.fillRect(BORDE, y + BORDE, LADO - BORDE * 2, LADO - BORDE * 2);
        dibujarI(g2, y);
        g2.dispose();
    }

    // Dibuja una "i" en pixel art, centrada en el cuadrado.
    // Recibe el pincel y cuánto bajó el botón.
    private void dibujarI(Graphics2D g, int y) {
        // La "i" dibujada con # (celda pintada) y . (vacía). Cada celda mide 3 píxeles. Posición horizontal inicial
        // (centrada). Posición vertical inicial (centrada).
        String[] filas = {"..#..", ".....", ".##..", "..#..", "..#..", "..#..", "..#..", "..#..", ".###."};
        int celda = 3;
        int x0 = (LADO - 5 * celda) / 2;
        int y0 = y + (LADO - filas.length * celda) / 2;
        // Color de la "i", ciruela oscuro, igual en claro y oscuro. Recorre las filas. Recorre las columnas. Si la celda está pintada.
        g.setColor(new Color(91, 35, 51));
        for (int f = 0; f < filas.length; f++) {
            for (int c = 0; c < 5; c++) {
                if (filas[f].charAt(c) == '#') {
                    // Dibuja la celda.
                    g.fillRect(x0 + c * celda, y0 + f * celda, celda, celda);
                }
            }
        }
    }
}
