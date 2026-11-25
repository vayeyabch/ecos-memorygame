package ecos.componentes;

// Navegador avisa cada vez que cambia la pantalla. El botón lo usa para esconderse en las pantallas donde estorba.
import ecos.app.Navegador;
// Tema da los colores del botón y avisa cuando cambia el modo, para que se repinte.
import ecos.app.Tema;
// Sonido reproduce el efecto de clic.
import ecos.recursos.Sonido;
// Color es el tipo de un color. Se usa para pintar la "i".
import java.awt.Color;
// Cursor es el puntero del mouse. Aquí se usa la manito al pasar sobre el botón.
import java.awt.Cursor;
// Dimension es un tamaño, con ancho y alto. Se usa para decirle a Swing cuánto mide el botón.
import java.awt.Dimension;
// Graphics es el pincel general con el que Swing pide dibujar. Llega como dato a paintComponent.
import java.awt.Graphics;
// Graphics2D es el pincel con más opciones. Es el que se usa para dibujar el botón.
import java.awt.Graphics2D;
// MouseAdapter es una clase con los eventos del mouse ya vacíos, para escribir solo los que se necesitan.
import java.awt.event.MouseAdapter;
// MouseEvent es el aviso que llega cuando el mouse hace algo, como apretar o soltar.
import java.awt.event.MouseEvent;
// Set es un conjunto, o sea una colección de elementos sin repetir. Aquí guarda los ids de las pantallas donde se oculta.
import java.util.Set;
// JButton es el botón de Swing. BotonAcercaDe hereda de él para conservar los clics, pero se dibuja a mano.
import javax.swing.JButton;

// Es el botón cuadrado con una "i" que abre la ventana de créditos del equipo. Va en la esquina inferior derecha.
// Se dibuja a mano igual que BotonIcono, con sombra, borde y fondo rosa, y baja al apretarlo.
// Se esconde en la intro y en las pantallas de juego para no estorbar.
// VentanaAcercaDe no se importa porque está en el mismo paquete, ecos.componentes.
public class BotonAcercaDe extends JButton {

    // Ids de las pantallas donde el botón no se muestra. Set.of crea un conjunto que no se puede modificar.
    // Si se crea una pantalla de juego nueva y no se agrega aquí, el botón estorbaría en ella.
    private static final Set<String> OCULTO_EN = Set.of("intro", "clasico", "patrones");
    // Medidas fijas, por eso son final. Lado del cuadrado, grosor del borde y alto de la sombra.
    private static final int LADO = 40;
    private static final int BORDE = 4;
    private static final int SOMBRA = 4;

    // Dice si el botón está apretado ahora mismo. Cambia lo que se dibuja.
    private boolean apretado = false;
    // Acción que repinta el botón cuando cambia el tema. Se guarda en un campo para suscribirse y darse de baja con el mismo objeto.
    private final Runnable alCambiarTema = this::repaint;

    // Deja el botón listo, conecta su clic y lo suscribe al Navegador.
    public BotonAcercaDe(Navegador navegador) {
        // Se dibuja todo a mano, por eso se apaga lo que Swing dibuja por defecto: fondo, borde y marco de foco.
        setContentAreaFilled(false);
        setBorderPainted(false);
        setFocusPainted(false);
        setOpaque(false);
        // Pone la manito como puntero cuando el mouse está sobre el botón.
        setCursor(Cursor.getPredefinedCursor(Cursor.HAND_CURSOR));
        // Escucha al mouse para que el botón baje al apretarlo y suba al soltarlo o al salir.
        // Es una clase anónima: se crea y se usa en el mismo lugar. MouseAdapter ya trae los eventos vacíos.
        addMouseListener(new MouseAdapter() {
            @Override
            public void mousePressed(MouseEvent e) { apretado = true; repaint(); }
            @Override
            public void mouseReleased(MouseEvent e) { apretado = false; repaint(); }
            @Override
            public void mouseExited(MouseEvent e) { apretado = false; repaint(); }
        });
        // Al hacer clic suena y abre la ventana de créditos. La flecha es la función que se ejecuta, y e es el aviso del clic.
        // this es este botón, y sirve para saber sobre qué ventana abrirla.
        addActionListener(e -> { Sonido.efecto("click"); VentanaAcercaDe.mostrar(this); });
        // Se suscribe al Navegador: cada vez que cambia la pantalla, recibe su id.
        // El signo ! significa no, así que se muestra solo si el id no está en la lista de ocultas.
        navegador.alCambiarPantalla(id -> setVisible(!OCULTO_EN.contains(id)));
        // Empieza oculto, porque la primera pantalla es la intro.
        setVisible(false);
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

    // Dibuja el botón completo a mano, de atrás hacia adelante, en capas. Swing lo llama solo.
    // Es casi igual al de BotonIcono. La diferencia es que aquí el fondo siempre es rosa y el dibujo es siempre la "i".
    @Override
    protected void paintComponent(Graphics g) {
        // Copia el pincel para no afectar el original, y la convierte a Graphics2D.
        Graphics2D g2 = (Graphics2D) g.create();
        // Si está apretado, baja 3 píxeles y la sombra se achica a 1. El signo de pregunta es un if corto.
        int y = apretado ? 3 : 0;
        int sombra = apretado ? 1 : SOMBRA;
        // Capa 1, la sombra dura, un cuadrado corrido hacia abajo.
        g2.setColor(Tema.sombra());
        g2.fillRect(0, y + sombra, LADO, LADO);
        // Capa 2, el borde.
        g2.setColor(Tema.borde());
        g2.fillRect(0, y, LADO, LADO);
        // Capa 3, el fondo rosa, un cuadrado más chico que el borde.
        g2.setColor(Tema.rosa());
        g2.fillRect(BORDE, y + BORDE, LADO - BORDE * 2, LADO - BORDE * 2);
        // Capa 4, la "i" encima. Se le pasa y para que baje junto con el botón.
        dibujarI(g2, y);
        // Libera el pincel copiado.
        g2.dispose();
    }

    // Dibuja la "i" con una matriz de texto, como en pixel art. Cada carácter es un cuadrito.
    // El parámetro y es cuánto bajó el botón, para que la "i" lo acompañe al apretarlo.
    private void dibujarI(Graphics2D g, int y) {
        // La i se dibuja con # (celda pintada) y . (vacía), cada celda de 3 píxeles.
        String[] filas = {"..#..", ".....", ".##..", "..#..", "..#..", "..#..", "..#..", "..#..", ".###."};
        int celda = 3;
        // La x y la y iniciales centran el dibujo en el botón. Tiene 5 cuadritos de ancho y tantos de alto como filas.
        int x0 = (LADO - 5 * celda) / 2;
        int y0 = y + (LADO - filas.length * celda) / 2;
        // Color fijo, un vino oscuro que no cambia con el tema.
        g.setColor(new Color(91, 35, 51));
        // El for de afuera recorre las filas y el de adentro las columnas de cada fila.
        for (int f = 0; f < filas.length; f++) {
            for (int c = 0; c < 5; c++) {
                // charAt saca el carácter de esa posición. Solo dibuja donde hay un #.
                if (filas[f].charAt(c) == '#') {
                    g.fillRect(x0 + c * celda, y0 + f * celda, celda, celda);
                }
            }
        }
    }
}