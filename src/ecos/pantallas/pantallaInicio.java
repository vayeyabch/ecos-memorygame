package ecos.pantallas;

import ecos.app.Pantalla;
import ecos.app.Tema;
import ecos.recursos.Fuentes;
import ecos.recursos.Sonido;
import ecos.recursos.Sprites;

import javax.swing.*;
import java.awt.*;
import java.awt.event.ActionEvent;
import java.awt.event.MouseAdapter;
import java.awt.event.MouseEvent;

public class pantallaInicio extends Pantalla {

    private static final int PARPADEO_MS = 550;

    private final JLabel start = new JLabel("START", SwingConstants.CENTER);
    private final Timer parpadeo;
    private boolean encendido;
    private boolean saliendo;

    public pantallaInicio() {
        super("inicio", "inicio");
        setLayout(new GridBagLayout());

        JLabel logo = new JLabel(new ImageIcon(Sprites.logo("logo_ecos")));
        logo.setAlignmentX(Component.CENTER_ALIGNMENT);

        start.setFont(Fuentes.titulo(24f));
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
        centro.add(Box.createVerticalStrut(40));
        centro.add(start);
        add(centro);

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
            pintarStart();
        });
    }

    @Override
    public void alEntrar() {
        super.alEntrar();
        saliendo = false;
        encendido = true;
        pintarStart();
        parpadeo.start();
    }

    @Override
    public void alSalir() {
        parpadeo.stop();
    }

    private void pintarStart() {
        Color c = Tema.texto();
        start.setForeground(encendido ? c : new Color(c.getRed(), c.getGreen(), c.getBlue(), 90));
    }

    private void irAAcceso() {
        if (saliendo) {
            return;
        }
        saliendo = true;
        Sonido.efecto("click");
        irA("acceso");
    }
}
