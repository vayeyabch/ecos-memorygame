package ecos.pantallas;

import ecos.app.Pantalla;
import ecos.componentes.ImagenPixel;
import ecos.componentes.TextoPixel;
import ecos.recursos.Fuentes;
import ecos.recursos.Sonido;
import ecos.recursos.Sprites;
import java.awt.Color;
import java.awt.Component;
import java.awt.Cursor;
import java.awt.GridBagLayout;
import java.awt.event.ActionEvent;
import java.awt.event.ComponentAdapter;
import java.awt.event.ComponentEvent;
import java.awt.event.MouseAdapter;
import java.awt.event.MouseEvent;
import javax.swing.AbstractAction;
import javax.swing.Action;
import javax.swing.Box;
import javax.swing.BoxLayout;
import javax.swing.BorderFactory;
import javax.swing.JPanel;
import javax.swing.KeyStroke;
import javax.swing.Timer;

public class pantallaInicio extends Pantalla {

    private static final int PARPADEO_MS = 550; // cada cuánto cambia el parpadeo de START

    private final ImagenPixel logo = new ImagenPixel(Sprites.logo("logo_ecos")); // logo grande del juego
    private final TextoPixel start = new TextoPixel("START", Fuentes.titulo(32f), Color.WHITE, new Color(0x0B0710), 4); // texto START con sombra
    private final Timer parpadeo; // reloj del parpadeo
    private boolean encendido;
    private boolean saliendo;

    public pantallaInicio() {
        super("inicio", "inicio");
        setLayout(new GridBagLayout());
        setBorder(BorderFactory.createEmptyBorder(MARGEN_SUPERIOR, 16, 28, 16));

        logo.setAncho(560);
        logo.setAlignmentX(Component.CENTER_ALIGNMENT);
        start.setAlignmentX(Component.CENTER_ALIGNMENT);
        start.setCursor(Cursor.getPredefinedCursor(Cursor.HAND_CURSOR));
        start.addMouseListener(new MouseAdapter() {
            @Override
            public void mouseClicked(MouseEvent e) {
                irAAcceso();
            }
        });

        JPanel centro = new JPanel();
        centro.setOpaque(false);
        centro.setLayout(new BoxLayout(centro, BoxLayout.Y_AXIS));
        centro.add(logo);
        centro.add(Box.createVerticalStrut(18));
        centro.add(start);
        add(centro);

        // el logo se adapta al ancho de la ventana (máximo 560 px, como en el diseño)
        addComponentListener(new ComponentAdapter() {
            @Override
            public void componentResized(ComponentEvent e) {
                int ancho = Math.min(560, (int) (getWidth() * 0.86));
                int altoMax = Math.max(120, getHeight() - 260);
                logo.setAncho(Math.min(ancho, (int) (altoMax * 1244f / 527f)));
            }
        });

        // Enter o Espacio también empiezan
        Action accion = new AbstractAction() {
            @Override
            public void actionPerformed(ActionEvent e) {
                irAAcceso();
            }
        };
        getActionMap().put("start", accion);
        getInputMap(WHEN_IN_FOCUSED_WINDOW).put(KeyStroke.getKeyStroke("ENTER"), "start");
        getInputMap(WHEN_IN_FOCUSED_WINDOW).put(KeyStroke.getKeyStroke("SPACE"), "start");

        parpadeo = new Timer(PARPADEO_MS, e -> {
            encendido = !encendido;
            start.setOpacidad(encendido ? 1f : 0.35f);
        });
    }

    @Override
    public void alEntrar() {
        super.alEntrar();
        saliendo = false;
        encendido = true;
        start.setOpacidad(1f);
        parpadeo.start();
    }

    @Override
    public void alSalir() {
        parpadeo.stop();
    }

    private void irAAcceso() {
        if (saliendo || !isShowing()) {
            return;
        }
        saliendo = true;
        Sonido.efecto("click");
        irA("acceso");
    }
}
