package ecos.app.modos.JuegoC;

import java.awt.Graphics;
import java.awt.Image;
import javax.swing.ImageIcon;
import javax.swing.JPanel;


/**
 * Panel que dibuja una imagen como fondo.
 */
public class PanelFondo extends JPanel {

    private final Image fondo;

    public PanelFondo(String rutaImagen) {
        ImageIcon icono = new ImageIcon(rutaImagen);
        fondo = icono.getImage();
    }

    @Override
    protected void paintComponent(Graphics g) {
        super.paintComponent(g);
        g.drawImage(fondo, 0, 0, getWidth(), getHeight(), this);
    }
}


