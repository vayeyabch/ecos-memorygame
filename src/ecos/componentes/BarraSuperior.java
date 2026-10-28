package ecos.componentes;

import ecos.app.Navegador;
import ecos.app.Tema;
import ecos.app.modos.JuegoC.BotonPixel;
import ecos.app.modos.JuegoC.Paleta;
import ecos.recursos.Sonido;
import java.awt.BorderLayout;
import java.awt.FlowLayout;
import java.awt.Graphics;
import javax.swing.BorderFactory;
import javax.swing.JPanel;

/**
 * Barra de arriba: boton Atras (izquierda) y botones de tema y musica (derecha).
 * Usa los botones pixel de Isa. Se oculta sola en la pantalla "intro".
 * Se pone con: ventana.setBarraSuperior(new BarraSuperior(ventana.getNavegador()));
 */
public class BarraSuperior extends JPanel {

    private final BotonPixel atras = new BotonPixel(Paleta.DORADO, "< Atrás");
    private final BotonPixel tema = BotonPixel.crearLuna();
    private final BotonPixel musica = BotonPixel.crearSinBarra();
    private final Navegador navegador;

    public BarraSuperior(Navegador navegador) {
        super(new BorderLayout());
        this.navegador = navegador;
        setBorder(BorderFactory.createEmptyBorder(8, 14, 12, 14));

        JPanel derecha = new JPanel(new FlowLayout(FlowLayout.RIGHT, 10, 0));
        derecha.setOpaque(false);
        derecha.add(tema);
        derecha.add(musica);
        add(atras, BorderLayout.WEST);
        add(derecha, BorderLayout.EAST);

        atras.addActionListener(e -> { Sonido.efecto("click"); navegador.atras(); });
        tema.addActionListener(e -> { Tema.alternar(); Sonido.efecto("cambio_tema"); });
        musica.addActionListener(e -> { if (Sonido.alternar()) Sonido.efecto("musica_on_off"); });

        navegador.alCambiarPantalla(this::actualizar);
        Tema.alCambiar(this::repaint);
        setVisible(false);
    }

    /** Se llama sola cada vez que cambia la pantalla. */
    private void actualizar(String idPantalla) {
        setVisible(!"intro".equals(idPantalla));
        atras.setVisible(navegador.puedeVolver());
    }

    @Override
    protected void paintComponent(Graphics g) {
        g.setColor(Tema.panel());
        g.fillRect(0, 0, getWidth(), getHeight());
        g.setColor(Tema.borde());
        g.fillRect(0, getHeight() - 4, getWidth(), 4);
    }
}
