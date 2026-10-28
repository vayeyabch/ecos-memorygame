package ecos.componentes;

import ecos.app.Tema;
import ecos.recursos.Sprites;

import javax.swing.*;
import java.awt.*;

public class VisorEscaner extends JPanel {

    public enum Estado { NORMAL, ESCANEANDO, OK, ERROR }

    private static final int LADO = 240;
    private static final int CAMBIO_MASCOTA_MS = 1300;
    private static final int MASCOTAS = 9;

    private final JLabel mascota = new JLabel();
    private final JLabel haz = new JLabel(Sprites.gif("gifs/scanner_haz.gif"));
    private final Timer timer;
    private Estado estado = Estado.NORMAL;
    private int indice = 1;

    public VisorEscaner() {
        setPreferredSize(new Dimension(LADO, LADO));
        setLayout(new GridBagLayout());

        GridBagConstraints celda = new GridBagConstraints();
        celda.gridx = 0;
        celda.gridy = 0;
        haz.setVisible(false);
        add(haz, celda);
        add(mascota, celda);

        timer = new Timer(CAMBIO_MASCOTA_MS, e -> {
            indice = indice % MASCOTAS + 1;
            mascota.setIcon(new ImageIcon(Sprites.mascota(indice)));
        });
        mascota.setIcon(new ImageIcon(Sprites.mascota(indice)));
    }

    public void setEstado(Estado estado) {
        this.estado = estado;
        haz.setVisible(estado == Estado.ESCANEANDO);
        repaint();
    }

    public void setDesplazamiento(int px) {
        mascota.setBorder(BorderFactory.createEmptyBorder(0, Math.max(0, 2 * px), 0, Math.max(0, -2 * px)));
    }

    public void iniciar() {
        timer.start();
    }

    public void detener() {
        timer.stop();
    }

    @Override
    protected void paintComponent(Graphics g) {
        super.paintComponent(g);
        int w = getWidth();
        int h = getHeight();

        g.setColor(Tema.caja());
        g.fillRect(0, 0, w, h);

        Color marco = Tema.borde();
        if (estado == Estado.OK) {
            marco = Tema.acierto();
        } else if (estado == Estado.ERROR) {
            marco = Tema.peligro();
        }
        g.setColor(marco);
        for (int i = 0; i < 4; i++) {
            g.drawRect(i, i, w - 1 - 2 * i, h - 1 - 2 * i);
        }

        int m = 14, l = 28, t = 5;
        g.setColor(Tema.acento());
        g.fillRect(m, m, l, t);
        g.fillRect(m, m, t, l);
        g.fillRect(w - m - l, m, l, t);
        g.fillRect(w - m - t, m, t, l);
        g.fillRect(m, h - m - t, l, t);
        g.fillRect(m, h - m - l, t, l);
        g.fillRect(w - m - l, h - m - t, l, t);
        g.fillRect(w - m - t, h - m - l, t, l);
    }
}