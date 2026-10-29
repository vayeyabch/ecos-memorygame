package ecos.pantallas;

import ecos.app.Pantalla;
import ecos.app.Tema;
import ecos.componentes.BarraCarga;
import ecos.componentes.TextoMaquina;
import ecos.recursos.Sprites;

import javax.swing.*;
import java.awt.*;

public class pantallaIntro extends Pantalla {

    private static final String TEXTO_1 = "De los creadores de AvatarsVsRooks";
    private static final String TEXTO_2 = "Una colaboración entre Gravita y Eva Solutions";

    private final JLabel logo1 = crearLogo("logo_intro_1");
    private final JLabel logo2 = crearLogo("logo_intro_2");
    private final TextoMaquina texto1 = new TextoMaquina();
    private final TextoMaquina texto2 = new TextoMaquina();
    private final BarraCarga barra = new BarraCarga();
    private final JLabel mensaje = new JLabel("Estamos despertando a las mascotas");
    private final JPanel cajaCarga = new JPanel();
    private Timer timer;
    private boolean terminado;

    public pantallaIntro() {
        super("intro", "intro");
        setLayout(new BorderLayout());

        texto1.setPreferredSize(new Dimension(700, 70));
        texto2.setPreferredSize(new Dimension(700, 70));
        cajaCarga.setOpaque(false);
        cajaCarga.setLayout(new BoxLayout(cajaCarga, BoxLayout.Y_AXIS));
        agregar(cajaCarga, barra, mensaje);
        cajaCarga.setVisible(false);

        JPanel centro = new JPanel();
        centro.setOpaque(false);
        centro.setLayout(new BoxLayout(centro, BoxLayout.Y_AXIS));
        agregar(centro, logo1, texto1, logo2, texto2, cajaCarga);

        JPanel medio = new JPanel(new GridBagLayout());
        medio.setOpaque(false);
        medio.add(centro);
        add(medio, BorderLayout.CENTER);

        JButton botonSaltar = new JButton("Saltar >>");
        botonSaltar.addActionListener(e -> terminar());
        JPanel abajo = new JPanel(new FlowLayout(FlowLayout.RIGHT));
        abajo.setOpaque(false);
        abajo.add(botonSaltar);
        add(abajo, BorderLayout.SOUTH);
    }

    @Override
    public void alEntrar() {
        super.alEntrar();
        terminado = false;
        logo1.setVisible(false);
        logo2.setVisible(false);
        cajaCarga.setVisible(false);
        texto1.setForeground(Tema.texto());
        texto2.setForeground(Tema.texto());
        mensaje.setForeground(Tema.acento());
        texto1.reiniciar();
        texto2.reiniciar();
        barra.setEncendidos(0);
        despues(500, this::paso1);
    }

    @Override
    public void alSalir() {
        detener();
    }

    private void paso1() {
        logo1.setVisible(true);
        texto1.escribir(TEXTO_1, () -> despues(800, this::paso2));
    }

    private void paso2() {
        logo2.setVisible(true);
        texto2.escribir(TEXTO_2, () -> despues(700, this::paso3));
    }

    private void paso3() {
        cajaCarga.setVisible(true);
        llenarBarra(0);
    }

    private void llenarBarra(int i) {
        if (i >= BarraCarga.SEGMENTOS) {
            despues(600, this::terminar);
            return;
        }
        barra.setEncendidos(i + 1);
        despues(i < 18 ? 100 : 180, () -> llenarBarra(i + 1));
    }

    private void terminar() {
        if (terminado) {
            return;
        }
        terminado = true;
        detener();
        irA("inicio");
    }

    private void despues(int ms, Runnable accion) {
        detener();
        timer = new Timer(ms, e -> accion.run());
        timer.setRepeats(false);
        timer.start();
    }

    private void detener() {
        if (timer != null) {
            timer.stop();
        }
        texto1.detener();
        texto2.detener();
    }

    private static void agregar(JPanel panel, JComponent... componentes) {
        for (JComponent c : componentes) {
            c.setAlignmentX(Component.CENTER_ALIGNMENT);
            panel.add(c);
            panel.add(Box.createVerticalStrut(8));
        }
    }

    private static JLabel crearLogo(String nombre) {
        JLabel logo = new JLabel(new ImageIcon(Sprites.logo(nombre)));
        logo.setVisible(false);
        return logo;
    }

}