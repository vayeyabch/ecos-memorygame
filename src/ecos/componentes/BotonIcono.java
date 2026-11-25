package ecos.componentes;

// Tema da los colores del botón y avisa cuando cambia el modo, para que se repinte.
import ecos.app.Tema;
// Cursor es el puntero del mouse. Aquí se usa la manito al pasar sobre el botón.
import java.awt.Cursor;
// Dimension es un tamaño, con ancho y alto. Se usa para decirle a Swing cuánto mide el botón.
import java.awt.Dimension;
// Graphics es el pincel general con el que Swing pide dibujar. Llega como dato a paintComponent.
import java.awt.Graphics;
// Graphics2D es el pincel con más opciones. Es el que se usa para dibujar el botón.
import java.awt.Graphics2D;
// Rectangle es un rectángulo con posición y tamaño. Aquí marca el espacio donde va el dibujo de adentro.
import java.awt.Rectangle;
// MouseAdapter es una clase con los eventos del mouse ya vacíos, para escribir solo los que se necesitan.
import java.awt.event.MouseAdapter;
// MouseEvent es el aviso que llega cuando el mouse hace algo, como apretar o soltar.
import java.awt.event.MouseEvent;
// BiConsumer es una función que recibe dos datos y no devuelve nada. Aquí recibe el pincel y el rectángulo del dibujo.
import java.util.function.BiConsumer;
// JButton es el botón de Swing. BotonIcono hereda de él para conservar los clics, pero se dibuja a mano.
import javax.swing.JButton;

// Es el botón cuadrado con un dibujo adentro, que se usa en la barra superior para idioma, tema y música.
// Se reutiliza: siempre es el mismo botón y solo cambia el dibujo que se le pone con setDibujo.
// Baja al apretarlo, como un botón físico, y cambia de color con el tema.
public class BotonIcono extends JButton {

    // Medidas fijas, por eso son final. Lado del cuadrado, grosor del borde y alto de la sombra.
    private static final int LADO = 52;
    private static final int BORDE = 4;
    private static final int SOMBRA = 4;

    // Función que dibuja lo de adentro. Recibe el pincel y el rectángulo donde puede dibujar.
    // Empieza vacía, así que sin setDibujo el botón sale con el cuadro vacío y sin errores.
    private BiConsumer<Graphics2D, Rectangle> dibujo = (g, r) -> { };
    // Dice si el botón está apretado ahora mismo. Cambia lo que se dibuja.
    private boolean apretado = false;
    // Acción que repinta el botón cuando cambia el tema. Se guarda en un campo para suscribirse y darse de baja con el mismo objeto.
    private final Runnable alCambiarTema = this::repaint;

    // Deja el botón listo. Apaga todo lo que Swing dibuja por defecto, porque el botón se dibuja completo a mano.
    public BotonIcono() {
        // Sin fondo, sin borde y sin marco de foco de Swing, y transparente.
        setContentAreaFilled(false);
        setBorderPainted(false);
        setFocusPainted(false);
        setOpaque(false);
        // Sin efecto especial al pasar el mouse por encima.
        setRolloverEnabled(false);
        // Pone la manito como puntero cuando el mouse está sobre el botón.
        setCursor(Cursor.getPredefinedCursor(Cursor.HAND_CURSOR));
        // Deja a un escucha atento al mouse. Es una clase anónima: se crea y se usa en el mismo lugar.
        // MouseAdapter ya trae todos los eventos vacíos, así que solo se escriben los tres que importan.
        addMouseListener(new MouseAdapter() {
            // Al apretar, marca el botón como apretado y lo repinta para que baje.
            @Override
            public void mousePressed(MouseEvent e) { apretado = true; repaint(); }
            // Al soltar, vuelve a su posición.
            @Override
            public void mouseReleased(MouseEvent e) { apretado = false; repaint(); }
            // Si el mouse sale del botón con el clic apretado, también vuelve a su posición.
            @Override
            public void mouseExited(MouseEvent e) { apretado = false; repaint(); }
        });
    }

    // Guarda la función que dibuja lo de adentro y repinta. Lo usa BarraSuperior para poner el globo, la luna o la nota.
    public void setDibujo(BiConsumer<Graphics2D, Rectangle> dibujo) {
        this.dibujo = dibujo;
        repaint();
    }

    // Swing lo llama solo cuando el botón entra a la pantalla. Se suscribe a Tema para repintarse al cambiar el modo.
    @Override
    public void addNotify() {
        super.addNotify();
        Tema.alCambiar(alCambiarTema);
    }

    // Swing lo llama solo cuando el botón sale de la pantalla. Se da de baja para no repintarse sin que nadie lo vea.
    @Override
    public void removeNotify() {
        Tema.quitar(alCambiarTema);
        super.removeNotify();
    }

    // Dice cuánto mide el botón: el cuadrado más el espacio de la sombra de abajo.
    @Override
    public Dimension getPreferredSize() {
        return new Dimension(LADO, LADO + SOMBRA);
    }

    // Dibuja el botón completo a mano, de atrás hacia adelante, en capas de rectángulos. Swing lo llama solo.
    // No llama a super.paintComponent a propósito: no se quiere nada del dibujo normal del botón.
    @Override
    protected void paintComponent(Graphics g) {
        // Copia el pincel para no afectar el original, y la convierte a Graphics2D.
        Graphics2D g2 = (Graphics2D) g.create();
        // Si está apretado, el botón baja 3 píxeles y la sombra se achica a 1. Eso da el efecto de botón físico.
        // El signo de pregunta es un if corto: si apretado es true usa el primer valor, y si no el segundo.
        int y = apretado ? 3 : 0;
        int sombra = apretado ? 1 : SOMBRA;
        // Capa 1, la sombra dura, un cuadrado corrido hacia abajo.
        g2.setColor(Tema.sombra());
        g2.fillRect(0, y + sombra, LADO, LADO);
        // Capa 2, el borde, un cuadrado completo encima de la sombra.
        g2.setColor(Tema.borde());
        g2.fillRect(0, y, LADO, LADO);
        // Capa 3, la línea clara de adentro, un cuadrado más chico que el borde.
        g2.setColor(Tema.panelAlto());
        g2.fillRect(BORDE, y + BORDE, LADO - BORDE * 2, LADO - BORDE * 2);
        // Capa 4, el fondo del botón, todavía más chico.
        g2.setColor(Tema.panel());
        g2.fillRect(BORDE + 2, y + BORDE + 2, LADO - (BORDE + 2) * 2, LADO - (BORDE + 2) * 2);
        // Capa 5, el dibujo de adentro. Se ejecuta la función guardada, pasándole el pincel
        // y el rectángulo donde puede dibujar. Con accept se ejecuta una función que recibe datos.
        dibujo.accept(g2, new Rectangle(BORDE, y + BORDE, LADO - BORDE * 2, LADO - BORDE * 2));
        // Libera el pincel copiado.
        g2.dispose();
    }

    // El tamaño mínimo es el mismo que el preferido, para que el botón nunca se encoja.
    @Override
    public Dimension getMinimumSize() {
        return getPreferredSize();
    }

    // El tamaño máximo es el mismo que el preferido, para que el botón nunca se estire.
    @Override
    public Dimension getMaximumSize() {
        return getPreferredSize();
    }
}