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
    private final CartaPixel[] cartas;
    private final Runnable alCambiarTema = this::repaint;

    public TableroJugador(int n, int ladoCarta) {
        this.n = n;
        this.cartas = new CartaPixel[n * n];
        int hueco = Math.round(ladoCarta * 0.08f);
        int pad = Math.round(ladoCarta * 0.12f) + 4 + 3;
        setLayout(new GridLayout(n, n, hueco, hueco));
        setBorder(BorderFactory.createEmptyBorder(pad, pad, pad, pad));
        setOpaque(false);
        for (int i = 0; i < cartas.length; i++) {
            cartas[i] = new CartaPixel(ladoCarta);
            cartas[i].setCara(Sprites.icono(i % (n * n / 2) % 18 + 1, 64)); // cara de ejemplo hasta que el controlador ponga las suyas
            add(cartas[i]);
        }
    }

    public int getCantidad() {
        return cartas.length;
    }

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