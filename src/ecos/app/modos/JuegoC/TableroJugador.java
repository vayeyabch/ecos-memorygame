package ecos.app.modos.JuegoC;

import ecos.app.Tema;
import ecos.componentes.CartaPixel;
import ecos.recursos.Sprites;
import java.awt.Dimension;
import java.awt.Graphics;
import java.awt.Graphics2D;
import java.awt.GridLayout;
import javax.swing.BorderFactory;
import javax.swing.JPanel;

public class TableroJugador extends JPanel {

    private final int n; // cartas por lado
    private final CartaPixel[] cartas; // arreglo de cartas
    private final Runnable alCambiarTema = this::repaint; // repinta el tablero cuando cambia el tema

    public TableroJugador(int n, int ladoCarta) {
        this.n = n;
        this.cartas = new CartaPixel[n * n]; //Tamaño del tablero (n x n)
        int hueco = Math.round(ladoCarta * 0.08f); //espacio entre cartas 
        int pad = Math.round(ladoCarta * 0.12f) + 4 + 3; //espacio entre el borde del tablero y las cartas
        setLayout(new GridLayout(n, n, hueco, hueco)); // acomodar las cartas en una matriz de n x n con hueco entre ellas
        setBorder(BorderFactory.createEmptyBorder(pad, pad, pad, pad)); //espacio entre el borde del tablero y las cartas
        setOpaque(false);
        for (int i = 0; i < cartas.length; i++) { // crear las cartas y agregarlas al tablero
            cartas[i] = new CartaPixel(ladoCarta); //Le pone una imagen, en el controlador se le pondrá la imagen correcta
            cartas[i].setCara(Sprites.icono(i % (n * n / 2) % 18 + 1, 64)); // cara de ejemplo hasta que el controlador ponga las suyas
            add(cartas[i]); //Agrega la carta al tablero
        }
    }

    public int getCantidad() { // devuelve la cantidad de cartas en el tablero
        return cartas.length;
    }

    public CartaPixel getCarta(int i) { // devuelve la carta en la posición i
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
            javax.swing.Timer t = new javax.swing.Timer(i * 10, e -> c.voltear(arriba)); // crea un temporizador para voltear la carta
            t.setRepeats(false); // solo se ejecuta una vez
            t.start(); // inicia el temporizador para voltear la carta
        }
    }

    @Override
    public Dimension getMaximumSize() { // devuelve el tamaño máximo del tablero
        return getPreferredSize();
    }

    @Override
    public void addNotify() { // se llama cuando el tablero se agrega a un contenedor
        super.addNotify();
        Tema.alCambiar(alCambiarTema);
    }

    @Override
    public void removeNotify() { // se llama cuando el tablero se quita de un contenedor
        Tema.quitar(alCambiarTema);
        super.removeNotify();
    }

    @Override
    protected void paintComponent(Graphics g) { // pinta el fondo del tablero con un borde y un color de panel
        Graphics2D g2 = (Graphics2D) g.create(); // crea una copia del objeto Graphics para no afectar el original
        g2.setColor(Tema.borde()); // color del borde del tablero
        g2.fillRect(0, 0, getWidth(), getHeight()); // pinta el borde del tablero
        g2.setColor(Tema.panelAlto()); // color del panel del tablero
        g2.fillRect(4, 4, getWidth() - 8, getHeight() - 8); // pinta el panel del tablero
        g2.setColor(Tema.caja()); // color de la caja del tablero
        g2.fillRect(7, 7, getWidth() - 14, getHeight() - 14); // pinta la caja del tablero
        g2.dispose(); 
        super.paintComponent(g);
    }
}