package ecos.componentes;

import ecos.recursos.Sprites;

import javax.swing.BorderFactory;
import javax.swing.JLabel;
import javax.swing.SwingConstants;

public class DesfileMascotas extends JLabel {

    public DesfileMascotas() {
        super(Sprites.gif("gifs/mascotas_saltando.gif"), SwingConstants.CENTER);
        setBorder(BorderFactory.createEmptyBorder(16, 0, 0, 0));
    }
}