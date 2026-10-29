package ecos.componentes;

import ecos.recursos.Sonido;
import ecos.recursos.Sprites;
import java.awt.Component;
import java.awt.Dimension;
import java.awt.FlowLayout;
import javax.swing.Box;
import javax.swing.BoxLayout;
import javax.swing.JComponent;
import javax.swing.JPanel;

// Ventana para comprar el comodín "Ojo espía".
public class DialogoComprarOjo {

    private final CapaModal capa;

    public DialogoComprarOjo(int precio, Runnable alComprar) {
        PanelPixel panel = new PanelPixel() {
            @Override
            public Dimension getPreferredSize() {
                return new Dimension(Math.max(480, super.getPreferredSize().width), super.getPreferredSize().height);
            }

            @Override
            public Dimension getMaximumSize() {
                return getPreferredSize();
            }
        };
        panel.setLayout(new BoxLayout(panel, BoxLayout.Y_AXIS));
        panel.setPadding(14);
        ChipPixel chip = new ChipPixel("" + precio, 11f, true, ChipPixel.Modo.CAJA, 3);
        chip.setIcono(Sprites.ui("coin", 16));
        BotonMenu comprar = new BotonMenu("Comprar", BotonMenu.Estilo.ROSA);
        BotonMenu cancelar = new BotonMenu("Cancelar", BotonMenu.Estilo.SECUNDARIO);
        JPanel botones = new JPanel(new FlowLayout(FlowLayout.CENTER, 10, 0));
        botones.setOpaque(false);
        botones.add(comprar);
        botones.add(cancelar);
        JComponent[] piezas = {
            new ImagenPixel(Sprites.ui("eye", 64)),
            Etiqueta.titulo("Ojo espía", 16f, Etiqueta.Rol.TEXTO),
            Etiqueta.texto("Voltea todas las cartas de tu tablero durante 1.5 segundos.", 18f, Etiqueta.Rol.SUAVE),
            chip, botones};
        for (JComponent c : piezas) {
            c.setAlignmentX(Component.CENTER_ALIGNMENT);
            panel.add(c);
            panel.add(Box.createVerticalStrut(12));
        }
        capa = new CapaModal(panel);
        comprar.addActionListener(e -> { capa.cerrar(); alComprar.run(); });
        cancelar.addActionListener(e -> { Sonido.efecto("click"); capa.cerrar(); });
    }

    public void mostrar(Component origen) {
        Sonido.efecto("comprar");
        capa.mostrar(origen);
    }
}
