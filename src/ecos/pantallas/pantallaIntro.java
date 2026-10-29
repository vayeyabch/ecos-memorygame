package ecos.pantallas;

import ecos.app.Pantalla;
import ecos.componentes.BarraCarga;
import ecos.componentes.BotonMenu;
import ecos.componentes.ImagenPixel;
import ecos.componentes.TextoMaquina;
import ecos.recursos.Fuentes;
import ecos.recursos.Sonido;
import ecos.recursos.Sprites;
import java.awt.BorderLayout;
import java.awt.Color;
import java.awt.Component;
import java.awt.Dimension;
import java.awt.FlowLayout;
import java.awt.Graphics;
import java.awt.Graphics2D;
import java.awt.GridBagLayout;
import java.awt.RadialGradientPaint;
import java.awt.event.ComponentAdapter;
import java.awt.event.ComponentEvent;
import java.awt.geom.AffineTransform;
import javax.swing.Box;
import javax.swing.BoxLayout;
import javax.swing.JComponent;
import javax.swing.JLabel;
import javax.swing.JPanel;
import javax.swing.Timer;

public class pantallaIntro extends Pantalla {

    private static final String TEXTO_1 = "De los creadores de AvatarsVsRooks";
    private static final String TEXTO_2 = "Una colaboración entre Gravita y Eva Solutions";
    private static final String MENSAJE = "Estamos despertando a las mascotas";

    private final ImagenPixel logo1 = new ImagenPixel(Sprites.logo("logo_intro_1_x4")); // logo del primer estudio
    private final ImagenPixel logo2 = new ImagenPixel(Sprites.logo("logo_intro_2_x4")); // logo del segundo estudio
    private final TextoMaquina texto1 = new TextoMaquina(16f); // texto que se escribe letra por letra
    private final TextoMaquina texto2 = new TextoMaquina(16f);
    private final BarraCarga barra = new BarraCarga();
    private final JLabel mensaje = new JLabel(MENSAJE);
    private final JPanel cajaCarga = new JPanel();
    private final Timer relojPasos = new Timer(1, null); // reloj de los pasos de la intro (se reconfigura en cada paso)
    private final Timer relojFundido = new Timer(180, null); // reloj del fundido de los logos (5 escalones)
    private final Timer relojPuntos = new Timer(300, null); // reloj de los puntitos del mensaje de carga
    private int puntos;
    private boolean terminado;

    public pantallaIntro() {
        super("intro", "intro");
        setConFondo(false); // la intro dibuja su propio fondo oscuro
        setLayout(new BorderLayout());

        // textos con altura fija (dos renglones) para que nada salte de lugar
        texto1.setPreferredSize(new Dimension(900, 40));
        texto2.setPreferredSize(new Dimension(900, 40));

        mensaje.setFont(Fuentes.titulo(14f));
        mensaje.setForeground(new Color(0xFFCC80));

        cajaCarga.setOpaque(false);
        cajaCarga.setLayout(new BoxLayout(cajaCarga, BoxLayout.Y_AXIS));
        centrar(barra);
        centrar(mensaje);
        cajaCarga.add(barra);
        cajaCarga.add(Box.createVerticalStrut(12));
        cajaCarga.add(mensaje);
        cajaCarga.setVisible(false);

        JPanel centro = new JPanel();
        centro.setOpaque(false);
        centro.setLayout(new BoxLayout(centro, BoxLayout.Y_AXIS));
        for (JComponent c : new JComponent[]{logo1, texto1, logo2, texto2, cajaCarga}) {
            centrar(c);
            centro.add(c);
            centro.add(Box.createVerticalStrut(10));
        }
        JPanel medio = new JPanel(new GridBagLayout());
        medio.setOpaque(false);
        medio.add(centro);
        add(medio, BorderLayout.CENTER);

        // botón Saltar abajo a la derecha
        BotonMenu saltar = new BotonMenu("Saltar >>", BotonMenu.Estilo.SECUNDARIO);
        saltar.setPequeno(true);
        saltar.addActionListener(e -> { Sonido.efecto("click"); terminar(); });
        JPanel abajo = new JPanel(new FlowLayout(FlowLayout.RIGHT, 16, 12));
        abajo.setOpaque(false);
        abajo.add(saltar);
        add(abajo, BorderLayout.SOUTH);

        // los logos se achican o agrandan según la altura de la ventana (entre 70 y 150 px)
        addComponentListener(new ComponentAdapter() {
            @Override
            public void componentResized(ComponentEvent e) {
                int alto = Math.max(70, Math.min(150, (int) (getHeight() * 0.16)));
                logo1.setAlto(alto);
                logo2.setAlto(alto);
            }
        });
        logo1.setAlto(120);
        logo2.setAlto(120);

        relojPuntos.addActionListener(e -> {
            puntos = (puntos + 1) % 4;
            mensaje.setText(MENSAJE + ".".repeat(puntos));
        });
    }

    private static void centrar(JComponent c) {
        c.setAlignmentX(Component.CENTER_ALIGNMENT);
    }

    @Override
    public void alEntrar() {
        super.alEntrar();
        terminado = false;
        detener();
        logo1.setOpacidad(0f);
        logo2.setOpacidad(0f);
        cajaCarga.setVisible(false);
        texto1.reiniciar();
        texto2.reiniciar();
        barra.setEncendidos(0);
        mensaje.setText(MENSAJE);
        despues(500, () -> fundir(logo1, () -> despues(1000 - 900, this::paso1)));
    }

    @Override
    public void alSalir() {
        detener();
    }

    private void paso1() {
        texto1.escribir(TEXTO_1, () -> despues(800, () -> fundir(logo2, () -> despues(100, this::paso2))));
    }

    private void paso2() {
        texto2.escribir(TEXTO_2, () -> despues(700, this::paso3));
    }

    private void paso3() {
        cajaCarga.setVisible(true);
        relojPuntos.start();
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

    // Hace aparecer un logo en 5 escalones (como el fundido del diseño) y después ejecuta la acción.
    private void fundir(ImagenPixel logo, Runnable alTerminar) {
        int[] paso = {0};
        for (var l : relojFundido.getActionListeners()) {
            relojFundido.removeActionListener(l);
        }
        relojFundido.addActionListener(e -> {
            paso[0]++;
            logo.setOpacidad(paso[0] / 5f);
            if (paso[0] >= 5) {
                relojFundido.stop();
                alTerminar.run();
            }
        });
        relojFundido.start();
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
        for (var l : relojPasos.getActionListeners()) {
            relojPasos.removeActionListener(l);
        }
        relojPasos.setInitialDelay(ms);
        relojPasos.setRepeats(false);
        relojPasos.addActionListener(e -> accion.run());
        relojPasos.restart();
    }

    private void detener() {
        relojPasos.stop();
        relojFundido.stop();
        relojPuntos.stop();
        texto1.detener();
        texto2.detener();
    }

    // Fondo: degradado radial oscuro (violeta al centro, casi negro en las orillas).
    @Override
    protected void paintComponent(Graphics g) {
        super.paintComponent(g);
        Graphics2D g2 = (Graphics2D) g.create();
        double w = getWidth();
        double h = getHeight();
        AffineTransform estirar = new AffineTransform();
        estirar.translate(w / 2, h / 2);
        estirar.scale(w / h, 1);
        float radio = (float) (Math.hypot(w / 2, h / 2) * 0.85);
        g2.setPaint(new RadialGradientPaint(new java.awt.geom.Point2D.Double(0, 0), radio,
                new float[]{0f, 1f}, new Color[]{new Color(0x33203F), new Color(0x0B0710)}));
        g2.transform(estirar);
        g2.fillRect((int) -w, (int) -h, (int) (w * 2), (int) (h * 2));
        g2.dispose();
    }
}
