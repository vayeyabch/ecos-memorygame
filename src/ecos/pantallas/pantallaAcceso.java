package ecos.pantallas;

import ecos.app.Pantalla;
import ecos.app.Sesion;
import ecos.app.Tema;
import ecos.componentes.BotonMenu;
import ecos.componentes.CampoTextoPixel;
import ecos.componentes.DesfileMascotas;
import ecos.componentes.Etiqueta;
import ecos.componentes.PanelPixel;
import ecos.componentes.VisorEscaner;
import ecos.recursos.Fuentes;
import ecos.recursos.Sonido;
import java.awt.BorderLayout;
import java.awt.CardLayout;
import java.awt.Component;
import java.awt.Dimension;
import java.awt.Graphics;
import java.awt.GridBagConstraints;
import java.awt.GridBagLayout;
import java.awt.GridLayout;
import java.awt.Insets;
import javax.swing.BorderFactory;
import javax.swing.Box;
import javax.swing.BoxLayout;
import javax.swing.JComponent;
import javax.swing.JPanel;
import javax.swing.Timer;

public class pantallaAcceso extends Pantalla {

    private static final String MSG_LOGIN = "Colocá tu rostro dentro del marco.";
    private static final String MSG_REGISTRO = "Escribí tu nombre y registrá tu rostro.";
    private static final String MSG_ESCANEANDO = "Escaneando…";
    private static final String MSG_CAPTURANDO = "Capturando rostro…";
    private static final String MSG_REGISTRADO = "¡Rostro registrado! Ya podés iniciar sesión.";
    private static final String[] PASOS = {"Mirá al frente", "Girá a la izquierda", "Girá a la derecha"};
    private static final String[] FLECHAS = {"", "<<", ">>"};
    private static final int[] POSES = {0, -22, 22};

    private final DesfileMascotas desfile = new DesfileMascotas();
    private final VisorEscaner visor = new VisorEscaner();
    private final CardLayout cartas = new CardLayout();
    private final JPanel formularios = new JPanel(cartas) { // panel con las dos pestañas apiladas
        @Override
        public Dimension getPreferredSize() { // mide solo la pestaña que se está viendo
            for (Component c : getComponents()) {
                if (c.isVisible()) {
                    return c.getPreferredSize();
                }
            }
            return super.getPreferredSize();
        }
    };
    private final BotonMenu tabLogin = new BotonMenu("Iniciar sesión", BotonMenu.Estilo.PRINCIPAL);
    private final BotonMenu tabRegistro = new BotonMenu("Registrarse", BotonMenu.Estilo.PESTANA);
    private final CampoTextoPixel campoNombre = new CampoTextoPixel("Ej: Hellen", 14);
    private final Paso[] pasos = new Paso[PASOS.length];
    private final Etiqueta mensaje = Etiqueta.texto(MSG_LOGIN, 22f, Etiqueta.Rol.TEXTO);
    private Timer timer;
    private boolean animando;

    public pantallaAcceso() {
        super("acceso", "acceso");
        setLayout(new GridBagLayout());
        setBorder(BorderFactory.createEmptyBorder(MARGEN_SUPERIOR, 16, 28, 16));

        PanelPixel tarjeta = new PanelPixel(new GridBagLayout()) { // tarjeta de acceso (mínimo 820 px de ancho, como en el diseño)
            @Override
            public Dimension getPreferredSize() {
                Dimension d = super.getPreferredSize();
                return new Dimension(Math.max(820, d.width), d.height);
            }

            @Override
            public Dimension getMaximumSize() {
                return getPreferredSize();
            }
        };
        tarjeta.setPadding(20);
        GridBagConstraints c = new GridBagConstraints();
        c.gridx = 0;
        c.insets = new Insets(0, 0, 0, 26);
        c.anchor = GridBagConstraints.CENTER;
        tarjeta.add(visor, c);
        c.gridx = 1;
        c.insets = new Insets(0, 0, 0, 0);
        c.fill = GridBagConstraints.BOTH;
        c.weightx = 1;
        tarjeta.add(crearFormulario(), c);

        JPanel columna = new JPanel();
        columna.setOpaque(false);
        columna.setLayout(new BoxLayout(columna, BoxLayout.Y_AXIS));
        desfile.setAlignmentX(Component.CENTER_ALIGNMENT);
        tarjeta.setAlignmentX(Component.CENTER_ALIGNMENT);
        columna.add(desfile);
        columna.add(Box.createVerticalStrut(18));
        columna.add(tarjeta);
        add(columna);
    }

    private JPanel crearFormulario() {
        tabLogin.setFont(Fuentes.titulo(11f));
        tabRegistro.setFont(Fuentes.titulo(11f));
        tabLogin.addActionListener(e -> cambiarPestana(false));
        tabRegistro.addActionListener(e -> cambiarPestana(true));
        JPanel pestanas = new JPanel(new GridLayout(1, 2, 10, 0));
        pestanas.setOpaque(false);
        pestanas.add(tabLogin);
        pestanas.add(tabRegistro);

        // pestaña de iniciar sesión
        BotonMenu escanear = new BotonMenu("Escanear rostro", true);
        escanear.addActionListener(e -> reproducirEscaneo());
        JPanel login = columna(escanear);

        // pestaña de registro
        campoNombre.setLimite(14);
        BotonMenu registrar = new BotonMenu("Registrar mi rostro", true);
        registrar.addActionListener(e -> reproducirRegistro());
        JPanel registro = columna(Etiqueta.titulo("TU NOMBRE", 10f, Etiqueta.Rol.SUAVE), campoNombre);
        for (int i = 0; i < PASOS.length; i++) {
            pasos[i] = new Paso(PASOS[i], FLECHAS[i]);
            pasos[i].setAlignmentX(Component.LEFT_ALIGNMENT);
            registro.add(pasos[i]);
            registro.add(Box.createVerticalStrut(8));
        }
        registrar.setAlignmentX(Component.LEFT_ALIGNMENT);
        registro.add(registrar);

        formularios.setOpaque(false);
        formularios.add(login, "login");
        formularios.add(registro, "registro");

        Etiqueta nota = Etiqueta.texto("<html><div style='width:340px'>Solo te registrás una vez. Después entrás con tu cara las veces que quieras.</div></html>", 18f, Etiqueta.Rol.SUAVE);
        JPanel form = columna(pestanas, formularios, mensaje, nota);
        JComponent ancho = (JComponent) Box.createHorizontalStrut(440); // fuerza un ancho mínimo para el formulario
        ancho.setAlignmentX(Component.LEFT_ALIGNMENT);
        form.add(ancho);
        return form;
    }

    @Override
    public void alEntrar() {
        super.alEntrar();
        animando = false;
        mostrarPestana(false);
        desfile.iniciar();
        visor.iniciar();
    }

    @Override
    public void alSalir() {
        detener();
        visor.detener();
        desfile.detener();
    }

    private void cambiarPestana(boolean registro) {
        if (animando) {
            return;
        }
        Sonido.efecto("click");
        mostrarPestana(registro);
    }

    private void mostrarPestana(boolean registro) {
        cartas.show(formularios, registro ? "registro" : "login");
        formularios.revalidate();
        visor.setEstado(VisorEscaner.Estado.NORMAL);
        visor.setDesplazamiento(0);
        for (Paso p : pasos) {
            p.setEstado(Paso.PENDIENTE);
        }
        setMensaje(registro ? MSG_REGISTRO : MSG_LOGIN, false);
        tabLogin.setEstilo(registro ? BotonMenu.Estilo.PESTANA : BotonMenu.Estilo.PRINCIPAL);
        tabRegistro.setEstilo(registro ? BotonMenu.Estilo.PRINCIPAL : BotonMenu.Estilo.PESTANA);
    }

    private void setMensaje(String texto, boolean error) {
        mensaje.setText(texto);
        mensaje.setRol(error ? Etiqueta.Rol.PELIGRO : Etiqueta.Rol.TEXTO);
    }

    private void reproducirEscaneo() {
        if (animando) {
            return;
        }
        animando = true;
        Sonido.efecto("escaneo");
        visor.setEstado(VisorEscaner.Estado.ESCANEANDO);
        setMensaje(MSG_ESCANEANDO, false);
        despues(1900, () -> {
            visor.setEstado(VisorEscaner.Estado.OK);
            setMensaje("¡Hola, " + Sesion.getNombre() + "!", false);
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
        String nombre = campoNombre.getText().trim();
        if (nombre.isEmpty()) {
            Sonido.efecto("error");
            setMensaje("Escribí tu nombre primero.", true);
            return;
        }
        animando = true;
        Sesion.setNombre(nombre);
        Sonido.efecto("escaneo");
        visor.setEstado(VisorEscaner.Estado.ESCANEANDO);
        setMensaje(MSG_CAPTURANDO, false);
        capturarPaso(0);
    }

    private void capturarPaso(int i) {
        if (i >= PASOS.length) {
            visor.setEstado(VisorEscaner.Estado.OK);
            visor.setDesplazamiento(0);
            setMensaje(MSG_REGISTRADO, false);
            Sonido.efecto("registro_ok");
            despues(1400, () -> {
                animando = false;
                mostrarPestana(false);
                setMensaje(MSG_REGISTRADO, false);
            });
            return;
        }
        pasos[i].setEstado(Paso.ACTIVO);
        visor.setDesplazamiento(POSES[i]);
        Sonido.efecto("tick");
        despues(1100, () -> {
            pasos[i].setEstado(Paso.HECHO);
            capturarPaso(i + 1);
        });
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
        for (JComponent c : componentes) {
            c.setAlignmentX(Component.LEFT_ALIGNMENT);
            panel.add(c);
            panel.add(Box.createVerticalStrut(10));
        }
        return panel;
    }

    // Una fila de la lista de pasos del registro: cuadradito de color + texto + flecha.
    private static class Paso extends JPanel {
        static final int PENDIENTE = 0;
        static final int ACTIVO = 1;
        static final int HECHO = 2;

        private int estado = PENDIENTE;
        private final Etiqueta texto;

        Paso(String texto, String flecha) {
            super(new BorderLayout(12, 0));
            setOpaque(false);
            this.texto = Etiqueta.texto(texto, 20f, Etiqueta.Rol.SUAVE);
            add(new JPanel() {
                { setOpaque(false); setPreferredSize(new Dimension(20, 20)); }
                @Override
                protected void paintComponent(Graphics g) {
                    g.setColor(Tema.borde());
                    g.fillRect(0, 2, 18, 18);
                    g.setColor(estado == HECHO ? Tema.acierto() : estado == ACTIVO ? Tema.dorado() : Tema.caja());
                    g.fillRect(3, 5, 12, 12);
                }
            }, BorderLayout.WEST);
            add(this.texto, BorderLayout.CENTER);
            add(Etiqueta.titulo(flecha, 12f, Etiqueta.Rol.CARMESI), BorderLayout.EAST);
        }

        void setEstado(int estado) {
            this.estado = estado;
            texto.setRol(estado == PENDIENTE ? Etiqueta.Rol.SUAVE : Etiqueta.Rol.TEXTO);
            repaint();
        }
    }
}
