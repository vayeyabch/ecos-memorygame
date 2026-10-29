package ecos.componentes;

import ecos.app.Tema;
import ecos.recursos.Fuentes;
import ecos.recursos.Sonido;
import ecos.recursos.Sprites;
import java.awt.GridBagConstraints;
import java.awt.GridBagLayout;
import java.awt.Image;
import java.awt.Insets;
import javax.swing.ImageIcon;
import javax.swing.JLabel;
import javax.swing.JPanel;

/**
 * Tarjeta de un modo de juego para el menu: imagen, titulo, descripcion y
 * dos botones (Jugar y Reglas). Es un PanelPixel, asi que ya trae borde y sombra.
 *
 * Uso:
 *   TarjetaModo t = new TarjetaModo("Clasico", "Encuentra las parejas...", Sprites.mascota(1));
 *   t.setAlJugar(() -> irA("clasico"));
 *   t.setAlVerReglas(() -> irA("reglas_clasico"));
 */
public class TarjetaModo extends PanelPixel {

    private static final int TAM_IMAGEN = 120;
    private static final int ANCHO_TEXTO = 260;

    private final JLabel imagen = new JLabel();
    private final JLabel titulo = new JLabel("", JLabel.CENTER);
    private final JLabel descripcion = new JLabel("", JLabel.CENTER);
    private final BotonMenu btnJugar = new BotonMenu("Jugar", true);
    private final BotonMenu btnReglas = new BotonMenu("Reglas", false);

    public TarjetaModo(String textoTitulo, String textoDescripcion, Image dibujo) {
        super(new GridBagLayout());
        setPadding(16);

        imagen.setIcon(new ImageIcon(Sprites.escalar(dibujo, TAM_IMAGEN, TAM_IMAGEN)));
        titulo.setFont(Fuentes.titulo(20f));
        descripcion.setFont(Fuentes.texto(20f));
        titulo.setText(textoTitulo);
        // el <html> con un ancho fijo hace que el texto se parta en varias lineas
        descripcion.setText("<html><div style='text-align:center; width:" + ANCHO_TEXTO + "px'>"
                + textoDescripcion + "</div></html>");

        JPanel botones = new JPanel(new java.awt.FlowLayout(java.awt.FlowLayout.CENTER, 12, 0));
        botones.setOpaque(false);
        botones.add(btnJugar);
        botones.add(btnReglas);

        GridBagConstraints c = new GridBagConstraints();
        c.gridx = 0;
        c.insets = new Insets(6, 6, 6, 6);
        c.gridy = 0; add(imagen, c);
        c.gridy = 1; add(titulo, c);
        c.gridy = 2; add(descripcion, c);
        c.gridy = 3; c.insets = new Insets(14, 6, 6, 6); add(botones, c);

        Tema.alCambiar(this::colorear);
        colorear();
    }

    /** Que pasa al tocar "Jugar". */
    public void setAlJugar(Runnable accion) {
        btnJugar.addActionListener(e -> {
            Sonido.efecto("click");
            accion.run();
        });
    }

    /** Que pasa al tocar "Reglas". */
    public void setAlVerReglas(Runnable accion) {
        btnReglas.addActionListener(e -> {
            Sonido.efecto("click");
            accion.run();
        });
    }

    private void colorear() {
        titulo.setForeground(Tema.acento());
        descripcion.setForeground(Tema.texto());
    }
}
