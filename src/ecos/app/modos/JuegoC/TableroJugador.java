package ecos.app.modos.JuegoC;

// Clases del propio proyecto que se usan aquí.
import ecos.app.Tema;
import ecos.componentes.CartaPixel;
import ecos.recursos.Sprites;
// Librería gráfica AWT, Dimension para los tamaños, Graphics para dibujar, Graphics2D para dibujar con más opciones y
// GridLayout para acomodar en una cuadrícula fija.
import java.awt.Dimension;
import java.awt.Graphics;
import java.awt.Graphics2D;
import java.awt.GridLayout;
// Librería Swing, BorderFactory para los bordes y JPanel para los paneles.
import javax.swing.BorderFactory;
import javax.swing.JPanel;

// Tablero de un jugador del modo clásico, una cuadrícula de cartas que se pueden activar, voltear y hacer caer en cascada.
public class TableroJugador extends JPanel {

    // Cartas por lado. Arreglo de cartas. repinta el tablero cuando cambia el tema.
    private final int n;
    private final CartaPixel[] cartas;
    private final Runnable alCambiarTema = this::repaint;

    public TableroJugador(int n, int ladoCarta) {
        this.n = n;
        // Tamaño del tablero (n x n). espacio entre cartas. espacio entre el borde del tablero y las cartas. acomodar las
        // cartas en una matriz de n x n con hueco entre ellas.
        this.cartas = new CartaPixel[n * n];
        int hueco = Math.round(ladoCarta * 0.08f);
        int pad = Math.round(ladoCarta * 0.12f) + 4 + 3;
        setLayout(new GridLayout(n, n, hueco, hueco));
        // Espacio entre el borde del tablero y las cartas.
        setBorder(BorderFactory.createEmptyBorder(pad, pad, pad, pad));
        setOpaque(false);
        // Crear las cartas y agregarlas al tablero. Le pone una imagen, en el controlador se le pondrá la imagen correcta.
        // cara de ejemplo hasta que el controlador ponga las suyas. Agrega la carta al tablero.
        for (int i = 0; i < cartas.length; i++) {
            cartas[i] = new CartaPixel(ladoCarta);
            cartas[i].setCara(Sprites.icono(i % (n * n / 2) % 18 + 1, 64));
            add(cartas[i]);
        }
    }

    // Devuelve la cantidad de cartas en el tablero.
    public int getCantidad() {
        return cartas.length;
    }

    // Devuelve la carta en la posición i.
    public CartaPixel getCarta(int i) {
        return cartas[i];
    }

    // Activa o desactiva los clics de todo el tablero (solo el del turno actual).
    public void setActivo(boolean activo) {
        for (CartaPixel c : cartas) {
            c.setActiva(activo);
        }
    }

    // Todas las cartas caen en cascada, de arriba a la izquierda hacia abajo a la derecha.
    public void cascada(long retardoExtra) {
        for (int i = 0; i < cartas.length; i++) {
            cartas[i].reiniciar();
            cartas[i].caer((i / n + i % n) * 60L + retardoExtra);
        }
    }

    // Voltea varias cartas casi a la vez (efecto de ola de 10 ms entre cada una).
    public void voltearTodas(boolean arriba) {
        for (int i = 0; i < cartas.length; i++) {
            if (cartas[i].estaHecha()) {
                continue;
            }
            CartaPixel c = cartas[i];
            // Crea un temporizador para voltear la carta. Solo se ejecuta una vez. Inicia el temporizador para voltear la carta.
            javax.swing.Timer t = new javax.swing.Timer(i * 10, e -> c.voltear(arriba));
            t.setRepeats(false);
            t.start();
        }
    }

    // Devuelve el tamaño máximo del tablero.
    @Override
    public Dimension getMaximumSize() {
        return getPreferredSize();
    }

    // Se llama cuando el tablero se agrega a un contenedor.
    @Override
    public void addNotify() {
        super.addNotify();
        Tema.alCambiar(alCambiarTema);
    }

    // Se llama cuando el tablero se quita de un contenedor.
    @Override
    public void removeNotify() {
        Tema.quitar(alCambiarTema);
        super.removeNotify();
    }

    // Pinta el fondo del tablero con un borde y un color de panel.
    @Override
    protected void paintComponent(Graphics g) {
        // Crea una copia del Graphics para no afectar el original, y pinta el borde del tablero y su panel con sus colores.
        Graphics2D g2 = (Graphics2D) g.create();
        g2.setColor(Tema.borde());
        g2.fillRect(0, 0, getWidth(), getHeight());
        g2.setColor(Tema.panelAlto());
        // Pinta el panel del tablero. Color de la caja del tablero. Pinta la caja del tablero.
        g2.fillRect(4, 4, getWidth() - 8, getHeight() - 8);
        g2.setColor(Tema.caja());
        g2.fillRect(7, 7, getWidth() - 14, getHeight() - 14);
        g2.dispose(); 
        super.paintComponent(g);
    }
}