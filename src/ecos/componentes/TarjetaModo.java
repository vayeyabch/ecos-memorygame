package ecos.componentes;

import ecos.app.Tema;
import ecos.recursos.Sonido;
import java.awt.Component;
import java.awt.Cursor;
import java.awt.Image;
import java.awt.event.ActionEvent;
import java.awt.event.MouseAdapter;
import java.awt.event.MouseEvent;
import javax.swing.AbstractAction;
import javax.swing.Box;
import javax.swing.BoxLayout;
import javax.swing.JComponent;
import javax.swing.KeyStroke;

public class TarjetaModo extends PanelPixel {

    private final Runnable alElegir; // qué pasa al tocar la tarjeta
    private boolean encima = false; // el mouse está sobre la tarjeta

    public TarjetaModo(String titulo, String subtitulo, Image icono, Runnable alElegir) {
        super();
        this.alElegir = alElegir;
        setLayout(new BoxLayout(this, BoxLayout.Y_AXIS));
        setPadding(14);
        setCursor(Cursor.getPredefinedCursor(Cursor.HAND_CURSOR));
        setFocusable(true);

        ImagenPixel imagen = new ImagenPixel(icono);
        Etiqueta lblTitulo = Etiqueta.titulo(titulo, 16f, Etiqueta.Rol.TEXTO);
        Etiqueta lblSub = Etiqueta.texto(subtitulo, 18f, Etiqueta.Rol.SUAVE);
        for (JComponent c : new JComponent[]{imagen, lblTitulo, lblSub}) {
            c.setAlignmentX(Component.CENTER_ALIGNMENT);
        }
        add(Box.createVerticalGlue());
        add(imagen);
        add(Box.createVerticalStrut(10));
        add(lblTitulo);
        add(Box.createVerticalStrut(8));
        add(lblSub);
        add(Box.createVerticalGlue());

        // clic con el mouse
        addMouseListener(new MouseAdapter() {
            @Override
            public void mouseClicked(MouseEvent e) {
                elegir();
            }

            @Override
            public void mouseEntered(MouseEvent e) {
                encima = true;
                resaltar();
            }

            @Override
            public void mouseExited(MouseEvent e) {
                encima = false;
                resaltar();
            }
        });
        // Enter o Espacio cuando la tarjeta tiene el foco
        getActionMap().put("elegir", new AbstractAction() {
            @Override
            public void actionPerformed(ActionEvent e) {
                elegir();
            }
        });
        getInputMap(WHEN_FOCUSED).put(KeyStroke.getKeyStroke("ENTER"), "elegir");
        getInputMap(WHEN_FOCUSED).put(KeyStroke.getKeyStroke("SPACE"), "elegir");
    }

    // Agrega algo más abajo (por ejemplo, el selector de tamaño del tablero).
    public void agregarExtra(JComponent extra) {
        extra.setAlignmentX(Component.CENTER_ALIGNMENT);
        add(Box.createVerticalStrut(10));
        add(extra);
    }

    private void elegir() {
        Sonido.efecto("click");
        alElegir.run();
    }

    private void resaltar() {
        setColorBorde(encima ? Tema.naranja() : null); // el borde se vuelve naranja al pasar el mouse
    }

}
