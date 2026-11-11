package ecos.componentes;

// Clases del propio proyecto que se usan aquí.
import ecos.app.Idioma;
import ecos.app.Tema;
import ecos.recursos.Fuentes;
import ecos.recursos.Sprites;
// Librería gráfica AWT, AlphaComposite para la transparencia, Cursor para el cursor del mouse, Dimension para los
// tamaños, FontMetrics para medir el texto, Graphics para dibujar y Graphics2D para dibujar con más opciones.
import java.awt.AlphaComposite;
import java.awt.Cursor;
import java.awt.Dimension;
import java.awt.FontMetrics;
import java.awt.Graphics;
import java.awt.Graphics2D;
// Eventos del mouse y el teclado de AWT, MouseAdapter para reaccionar al mouse y MouseEvent para los datos de un clic.
import java.awt.event.MouseAdapter;
import java.awt.event.MouseEvent;
// Librería Swing, JButton para los botones.
import javax.swing.JButton;

// Botón de un comodín (como el Ojo espía) con su precio en monedas; se ve apagado si no alcanzan las monedas.
public class BotonPoder extends JButton {

    // Nombre del comodín, por ejemplo "Ojo espía", cuántas monedas cuesta, si no alcanzan (se ve apagado pero se puede tocar
    // para ver el aviso) y si el mouse está encima.
    private final String texto;
    private final int precio;
    private boolean sinFondos = false;
    private boolean encima = false;
    // Acción que repinta el botón cuando cambia el tema.
    private final Runnable alCambiarTema = this::repaint;
    private final Runnable alCambiarIdioma = this::repaint;

    // Constructor que recibe el nombre y el precio.
    public BotonPoder(String texto, int precio) {
        // Guarda el nombre. Guarda el precio. Quita el fondo que Swing le pone a los botones. Quita el borde normal de Swing.
        this.texto = texto;
        this.precio = precio;
        setContentAreaFilled(false);
        setBorderPainted(false);
        // Quita el cuadro de foco. Lo hace transparente, porque el dibujo se hace a mano. Pone la manito al pasar el mouse.
        // Escucha cuando el mouse entra y sale.
        setFocusPainted(false);
        setOpaque(false);
        setCursor(Cursor.getPredefinedCursor(Cursor.HAND_CURSOR));
        addMouseListener(new MouseAdapter() {
            // Cuando el mouse entra al botón.
            @Override
            public void mouseEntered(MouseEvent e) {
                // Anota que está encima. Repinta para que el botón se vea levantado.
                encima = true;
                repaint();
            }

            // Cuando el mouse sale del botón.
            @Override
            public void mouseExited(MouseEvent e) {
                // Anota que ya no está encima. Repinta para que vuelva a su lugar.
                encima = false;
                repaint();
            }
        });
    }

    // Apaga o enciende el botón según las monedas.
    public void setSinFondos(boolean sinFondos) {
        // Guarda el estado.
        this.sinFondos = sinFondos;
        repaint();
    }

    // Swing lo llama cuando el botón entra a una ventana.
    @Override
    public void addNotify() {
        // Deja que Swing haga lo suyo primero. Empieza a escuchar los cambios de tema.
        super.addNotify();
        Tema.alCambiar(alCambiarTema);
        Idioma.alCambiar(alCambiarIdioma);
    }

    // Swing lo llama cuando el botón sale de la ventana.
    @Override
    public void removeNotify() {
        // Deja de escuchar los cambios de tema. Deja que Swing haga lo suyo.
        Tema.quitar(alCambiarTema);
        Idioma.quitar(alCambiarIdioma);
        super.removeNotify();
    }

    // Tamaño que pide el botón.
    @Override
    public Dimension getPreferredSize() {
        // 250 de ancho y 52 de alto.
        return new Dimension(250, 52);
    }

    // Tamaño mínimo.
    @Override
    public Dimension getMinimumSize() {
        // Igual al preferido, para que no se achique.
        return getPreferredSize();
    }

    // Tamaño máximo.
    @Override
    public Dimension getMaximumSize() {
        // Igual al preferido, para que no se estire.
        return getPreferredSize();
    }

    // Swing lo llama cada vez que hay que dibujar el botón.
    @Override
    protected void paintComponent(Graphics g) {
        // Crea una copia del pincel, toma el ancho total y el alto sin sombra, y si el mouse está encima y está activo el
        // botón sube 2 píxeles.
        Graphics2D g2 = (Graphics2D) g.create();
        int w = getWidth();
        int h = getHeight() - 8;
        int dy = encima && isEnabled() && !sinFondos ? -2 : 0;
        // Si el botón está desactivado (no alcanzan las monedas). Lo dibuja medio transparente.
        if (!isEnabled() || sinFondos) {
            g2.setComposite(AlphaComposite.getInstance(AlphaComposite.SRC_OVER, 0.45f));
        }
        // Mueve el dibujo para dejar lugar a la sombra y al levantado. Elige el color de la sombra. Dibuja la sombra, el
        // mismo rectángulo, corrido hacia abajo. Elige el color del borde.
        g2.translate(0, dy + 2);
        g2.setColor(Tema.sombra());
        g2.fillRect(0, 4, w, h);
        g2.setColor(Tema.borde());
        // Dibuja el rectángulo del borde. Elige el color de relleno del botón. Dibuja el relleno dejando visible el borde.
        // Dibuja el iconito del ojo a la izquierda.
        g2.fillRect(0, 0, w, h);
        g2.setColor(Tema.boton());
        g2.fillRect(3, 3, w - 6, h - 6);
        g2.drawImage(Sprites.ui("eye", 26), 9, (h - 26) / 2, null);
        // Elige la fuente pixel para el texto. Mide la fuente para poder centrar el texto. Elige el color de las letras del
        // botón. Dibuja el nombre del comodín, centrado en vertical.
        g2.setFont(Fuentes.titulo(9f));
        FontMetrics fm = g2.getFontMetrics();
        g2.setColor(Tema.textoBoton());
        g2.drawString(Idioma.t(texto), 44, h / 2 + fm.getAscent() / 2 - 1);
        // Calcula el ancho y la posición de la cajita del precio, pegada a la derecha, y elige y dibuja su borde.
        int pw = 66;
        int px = w - pw - 8;
        g2.setColor(Tema.borde());
        g2.fillRect(px, 8, pw, h - 16);
        // Elige el color de relleno de la cajita. Dibuja el relleno de la cajita. Dibuja el iconito de la moneda. Elige el
        // color de las letras normales.
        g2.setColor(Tema.panel());
        g2.fillRect(px + 2, 10, pw - 4, h - 20);
        g2.drawImage(Sprites.ui("coin", 14), px + 8, (h - 14) / 2, null);
        g2.setColor(Tema.texto());
        // Dibuja el precio al lado de la moneda. Libera el pincel.
        g2.drawString("" + precio, px + 28, h / 2 + fm.getAscent() / 2 - 1);
        g2.dispose();
    }
}