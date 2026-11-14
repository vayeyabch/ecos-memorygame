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

// Tablero de un jugador del modo clásico: una cuadrícula de cartas que se pueden activar, voltear y hacer caer en
// cascada.
public class TableroJugador extends JPanel {

    // Cartas por lado, arreglo de cartas y repintado al cambiar el tema.
    private final int n;
    private final CartaPixel[] cartas;
    private final Runnable alCambiarTema = this::repaint;

    public TableroJugador(int n, int ladoCarta) {
        this.n = n;
        // Tamaño del tablero, espacio entre cartas, espacio hasta el borde y cuadrícula n x n.
        this.cartas = new CartaPixel[n * n];
        int hueco = Math.round(ladoCarta * 0.08f);
        int pad = Math.round(ladoCarta * 0.12f) + 4 + 3;
        setLayout(new GridLayout(n, n, hueco, hueco));
        setBorder(BorderFactory.createEmptyBorder(pad, pad, pad, pad));
        setOpaque(false);
        // Cartas con una cara de ejemplo hasta que el controlador ponga las suyas.
        for (int i = 0; i < cartas.length; i++) {
            cartas[i] = new CartaPixel(ladoCarta);
            cartas[i].setCara(Sprites.icono(i % (n * n / 2) % 18 + 1, 64));
            add(cartas[i]);
        }
    }

    public CartaPixel getCarta(int i) {
        return cartas[i];
    }

    // Activa o desactiva los clics de todo el tablero, solo el del turno actual.
    public void setActivo(boolean activo) {
        for (CartaPixel c : cartas) {
            c.setActiva(activo);
        }
    }

    // Las cartas caen en cascada, de arriba a la izquierda hacia abajo a la derecha.
    public void cascada(long retardoExtra) {
        for (int i = 0; i < cartas.length; i++) {
            cartas[i].reiniciar();
            cartas[i].caer((i / n + i % n) * 60L + retardoExtra);
        }
    }

    // Voltea varias cartas casi a la vez, con una ola de 10 ms entre cada una.
    public void voltearTodas(boolean arriba) {
        for (int i = 0; i < cartas.length; i++) {
            if (cartas[i].estaHecha()) {
                continue;
            }
            CartaPixel c = cartas[i];
            javax.swing.Timer t = new javax.swing.Timer(i * 10, e -> c.voltear(arriba));
            t.setRepeats(false);
            t.start();
        }
    }

    @Override
    public Dimension getMaximumSize() {
        return getPreferredSize();
    }

    @Override
    public void addNotify() {
        super.addNotify();
        Tema.alCambiar(alCambiarTema);
    }

    @Override
    public void removeNotify() {
        Tema.quitar(alCambiarTema);
        super.removeNotify();
    }

    @Override
    protected void paintComponent(Graphics g) {
        // Pinta el borde y el panel del tablero con una copia del Graphics.
        Graphics2D g2 = (Graphics2D) g.create();
        g2.setColor(Tema.borde());
        g2.fillRect(0, 0, getWidth(), getHeight());
        g2.setColor(Tema.panelAlto());
        g2.fillRect(4, 4, getWidth() - 8, getHeight() - 8);
        g2.setColor(Tema.caja());
        g2.fillRect(7, 7, getWidth() - 14, getHeight() - 14);
        g2.dispose(); 
        super.paintComponent(g);
    }
}
