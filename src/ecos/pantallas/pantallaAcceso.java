package ecos.pantallas;

import ecos.app.Pantalla;
import ecos.app.Tema;
import ecos.componentes.VisorEscaner;
import ecos.recursos.Sonido;
import ecos.recursos.Sprites;

import javax.swing.*;
import java.awt.*;

public class pantallaAcceso extends Pantalla {

    private static final String MSG_LOGIN = "Colocá tu rostro dentro del marco.";
    private static final String MSG_REGISTRO = "Escribí tu nombre y registrá tu rostro.";
    private static final String MSG_ESCANEANDO = "Escaneando…";
    private static final String MSG_CAPTURANDO = "Capturando rostro…";
    private static final String MSG_RECONOCIDO = "¡Rostro reconocido!";
    private static final String MSG_REGISTRADO = "¡Rostro registrado! Ya podés iniciar sesión.";
    private static final String[] PASOS = {"1. Mirá al frente", "2. Girá a la izquierda", "3. Girá a la derecha"};
    private static final int[] POSES = {0, -22, 22};

    private final VisorEscaner visor = new VisorEscaner();
    private final CardLayout cartas = new CardLayout();
    private final JPanel formularios = new JPanel(cartas);
    private final JPanel tarjeta = new JPanel(new BorderLayout(20, 0));
    private final JButton tabLogin = new JButton("Iniciar sesión");
    private final JButton tabRegistro = new JButton("Registrarse");
    private final JLabel etiquetaNombre = new JLabel("Tu nombre");
    private final JTextField campoNombre = new JTextField(12);
    private final JLabel[] pasos = new JLabel[PASOS.length];
    private final JLabel mensaje = new JLabel();
    private Timer timer;
    private boolean animando;
    private boolean enRegistro;

    public pantallaAcceso() {
        super("acceso", "acceso");
        setLayout(new BorderLayout());

        JLabel desfile = new JLabel(Sprites.gif("gifs/mascotas_saltando.gif"), SwingConstants.CENTER);
        desfile.setBorder(BorderFactory.createEmptyBorder(16, 0, 0, 0));
        add(desfile, BorderLayout.NORTH);

        tarjeta.add(visor, BorderLayout.WEST);
        tarjeta.add(crearFormulario(), BorderLayout.CENTER);
        JPanel medio = new JPanel(new GridBagLayout());
        medio.setOpaque(false);
        medio.add(tarjeta);
        add(medio, BorderLayout.CENTER);
    }

    private JPanel crearFormulario() {
        tabLogin.addActionListener(e -> cambiarPestana(false));
        tabRegistro.addActionListener(e -> cambiarPestana(true));
        JPanel pestanas = new JPanel(new GridLayout(1, 2, 8, 0));
        pestanas.setOpaque(false);
        pestanas.add(tabLogin);
        pestanas.add(tabRegistro);

        JButton escanear = new JButton("Escanear rostro");
        escanear.addActionListener(e -> reproducirEscaneo());
        JPanel login = columna(escanear);

        JButton registrar = new JButton("Registrar mi rostro");
        registrar.addActionListener(e -> reproducirRegistro());
        campoNombre.setMaximumSize(campoNombre.getPreferredSize());
        JPanel registro = columna(etiquetaNombre, campoNombre);
        for (int i = 0; i < PASOS.length; i++) {
            pasos[i] = new JLabel(PASOS[i]);
            agregar(registro, pasos[i]);
        }
        agregar(registro, registrar);

        formularios.setOpaque(false);
        formularios.add(login, "login");
        formularios.add(registro, "registro");

        return columna(pestanas, formularios, mensaje);
    }

    @Override
    public void alEntrar() {
        super.alEntrar();
        animando = false;
        tarjeta.setBackground(Tema.panel());
        tarjeta.setBorder(BorderFactory.createCompoundBorder(
                BorderFactory.createLineBorder(Tema.borde(), 4),
                BorderFactory.createEmptyBorder(16, 16, 16, 16)));
        mensaje.setForeground(Tema.texto());
        etiquetaNombre.setForeground(Tema.texto());
        mostrarPestana(false);
        visor.iniciar();
    }

    @Override
    public void alSalir() {
        detener();
        visor.detener();
    }

    private void cambiarPestana(boolean registro) {
        if (animando) {
            return;
        }
        Sonido.efecto("click");
        mostrarPestana(registro);
    }

    private void mostrarPestana(boolean registro) {
        enRegistro = registro;
        cartas.show(formularios, registro ? "registro" : "login");
        visor.setEstado(VisorEscaner.Estado.NORMAL);
        visor.setDesplazamiento(0);
        resaltarPaso(-1);
        mensaje.setText(registro ? MSG_REGISTRO : MSG_LOGIN);
        pintarPestana(tabLogin, !registro);
        pintarPestana(tabRegistro, registro);
    }

    private void pintarPestana(JButton boton, boolean seleccionada) {
        boton.setOpaque(true);
        boton.setBackground(seleccionada ? Tema.acento() : Tema.panel());
        boton.setForeground(seleccionada ? Color.BLACK : Tema.texto());
    }

    private void resaltarPaso(int actual) {
        for (int i = 0; i < pasos.length; i++) {
            pasos[i].setForeground(i == actual ? Tema.acento() : Tema.texto());
        }
    }

    private void reproducirEscaneo() {
        if (animando) {
            return;
        }
        animando = true;
        Sonido.efecto("escaneo");
        visor.setEstado(VisorEscaner.Estado.ESCANEANDO);
        mensaje.setText(MSG_ESCANEANDO);
        despues(1900, () -> {
            visor.setEstado(VisorEscaner.Estado.OK);
            mensaje.setText(MSG_RECONOCIDO);
            Sonido.efecto("registro_ok");
            despues(1000, () -> {
                animando = false;
                irA("menu");
            });
        });
    }

    private void reproducirRegistro() {
        if (animando) {
            return;
        }
        animando = true;
        Sonido.efecto("escaneo");
        visor.setEstado(VisorEscaner.Estado.ESCANEANDO);
        mensaje.setText(MSG_CAPTURANDO);
        capturarPaso(0);
    }

    private void capturarPaso(int i) {
        if (i >= PASOS.length) {
            visor.setEstado(VisorEscaner.Estado.OK);
            visor.setDesplazamiento(0);
            resaltarPaso(-1);
            mensaje.setText(MSG_REGISTRADO);
            Sonido.efecto("registro_ok");
            despues(1400, () -> {
                animando = false;
                mostrarPestana(false);
                mensaje.setText(MSG_REGISTRADO);
            });
            return;
        }
        resaltarPaso(i);
        visor.setDesplazamiento(POSES[i]);
        despues(1100, () -> capturarPaso(i + 1));
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
    }

    private static JPanel columna(JComponent... componentes) {
        JPanel panel = new JPanel();
        panel.setOpaque(false);
        panel.setLayout(new BoxLayout(panel, BoxLayout.Y_AXIS));
        agregar(panel, componentes);
        return panel;
    }

    private static void agregar(JPanel panel, JComponent... componentes) {
        for (JComponent c : componentes) {
            c.setAlignmentX(Component.LEFT_ALIGNMENT);
            panel.add(c);
            panel.add(Box.createVerticalStrut(8));
        }
    }
}