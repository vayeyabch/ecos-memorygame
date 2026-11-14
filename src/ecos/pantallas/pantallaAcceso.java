package ecos.pantallas;

import ecos.app.Pantalla;
import ecos.app.Tema;
import ecos.componentes.BotonMenu;
import ecos.componentes.CampoClavePixel;
import ecos.componentes.CampoTextoPixel;
import ecos.componentes.DesfileMascotas;
import ecos.componentes.Etiqueta;
import ecos.componentes.PanelPixel;
import ecos.recursos.Sonido;
import ecos.recursos.Sprites;

import java.awt.Component;
import java.awt.Dimension;
import java.awt.FlowLayout;
import javax.swing.BorderFactory;
import javax.swing.Box;
import javax.swing.BoxLayout;
import javax.swing.JButton;
import javax.swing.JComponent;
import javax.swing.JPanel;
import java.awt.GridBagLayout;
import java.awt.BorderLayout;
import java.awt.Graphics;
import java.awt.Graphics2D;
import java.awt.BasicStroke;
import java.awt.Cursor;

// Pantalla para iniciar sesión con usuario y contraseña o con Face ID; también tiene el botón para registrarse.
public class PantallaAcceso extends Pantalla {

    // Mascotas que desfilan arriba de la tarjeta, y campos de usuario y contraseña.
    private final DesfileMascotas desfile = new DesfileMascotas();
    private final CampoTextoPixel campoUsuario = new CampoTextoPixel("Tu usuario", 16);
    private final CampoClavePixel campoClave = new CampoClavePixel("Tu contraseña", 16);
    // Avisos de error y acción del botón Entrar, que define el controlador.
    private final Etiqueta mensaje = Etiqueta.texto(" ", 18f, Etiqueta.Rol.SUAVE);
    private Runnable accionEntrar = () -> { };
    // Acciones del botón Face ID y del botón Registrar mi rostro, que solo aparece si la cuenta no tiene rostro.
    private Runnable accionFaceId = () -> irA("login_facial");
    private Runnable accionRegistrarRostro = () -> { };
    private final BotonMenu registrarRostro = new BotonMenu("Registrar mi rostro", BotonMenu.Estilo.ROSA);

    public PantallaAcceso() {
        super("acceso", "acceso");
        setLayout(new GridBagLayout());
        setBorder(BorderFactory.createEmptyBorder(MARGEN_SUPERIOR, 16, 28, 16));
        campoUsuario.setLimite(20);

        // Entrar con usuario y contraseña suena y avisa al controlador; Face ID va a la pantalla del escaneo.
        BotonMenu entrar = new BotonMenu("Entrar", true);
        entrar.addActionListener(e -> { Sonido.efecto("click"); accionEntrar.run(); });
        BotonMenu faceId = new BotonMenu("Entrar con Face ID", BotonMenu.Estilo.SECUNDARIO);
        faceId.addActionListener(e -> { Sonido.efecto("click"); accionFaceId.run(); });
        // Botón escondido hasta que se intenta Face ID sin tener rostro; pide la contraseña para comprobar que la cuenta
        // es tuya.
        registrarRostro.setVisible(false);
        registrarRostro.addActionListener(e -> { Sonido.efecto("click"); accionRegistrarRostro.run(); });
        BotonMenu registrarse = new BotonMenu("Registrarse", BotonMenu.Estilo.ROSA);
        registrarse.setPequeno(true);
        registrarse.addActionListener(e -> { Sonido.efecto("click"); irA("registro"); });

        JPanel filaRegistro = new JPanel(new FlowLayout(FlowLayout.LEFT, 0, 0));
        filaRegistro.setOpaque(false);
        filaRegistro.add(Etiqueta.texto("¿No tenés cuenta?", 20f, Etiqueta.Rol.SUAVE));
        filaRegistro.add(Box.createHorizontalStrut(12));
        filaRegistro.add(registrarse);

        PanelPixel tarjeta = new PanelPixel();
        tarjeta.setLayout(new BoxLayout(tarjeta, BoxLayout.Y_AXIS));
        tarjeta.setPadding(22);
        tarjeta.add(alinear(Etiqueta.texto("Iniciar sesión", 34f, Etiqueta.Rol.TEXTO)));
        tarjeta.add(Box.createVerticalStrut(16));
        tarjeta.add(alinear(Etiqueta.titulo("USUARIO", 10f, Etiqueta.Rol.SUAVE)));
        tarjeta.add(alinear(campoUsuario));
        tarjeta.add(Box.createVerticalStrut(12));
        tarjeta.add(alinear(Etiqueta.titulo("CONTRASEÑA", 10f, Etiqueta.Rol.SUAVE)));
        tarjeta.add(alinear(conOjo(campoClave)));
        tarjeta.add(Box.createVerticalStrut(8));
        tarjeta.add(alinear(mensaje));
        tarjeta.add(Box.createVerticalStrut(10));
        tarjeta.add(alinear(entrar.anchoCompleto()));
        tarjeta.add(Box.createVerticalStrut(10));
        tarjeta.add(alinear(faceId.anchoCompleto()));
        tarjeta.add(Box.createVerticalStrut(10));
        tarjeta.add(alinear(registrarRostro.anchoCompleto()));
        tarjeta.add(Box.createVerticalStrut(16));
        tarjeta.add(alinear(filaRegistro));
        tarjeta.setMaximumSize(new Dimension(480, Integer.MAX_VALUE));

        // Mascotas arriba y tarjeta abajo, centradas.
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

    @Override
    public void alEntrar() {
        // Al entrar se borra la contraseña anterior y el aviso.
        super.alEntrar();
        campoClave.setText("");
        limpiarMensaje();
        desfile.iniciar();
    }

    @Override
    public void alSalir() {
        desfile.detener();
    }

    public String getUsuario() { return campoUsuario.getText().trim(); }
    public String getClave() { return new String(campoClave.getPassword()); }

    public void setAccionEntrar(Runnable accion) { accionEntrar = accion; }

    public void setAccionFaceId(Runnable accion) { accionFaceId = accion; }
    public void setAccionRegistrarRostro(Runnable accion) { accionRegistrarRostro = accion; }

    public void mostrarOpcionRostro(boolean ver) { registrarRostro.setVisible(ver); revalidate(); }

    public void irALoginFacial() { irA("login_facial"); }
    public void irARegistroFacial() { irA("registro_facial"); }

    public void irAMenu() { irA("menu"); }

    public void mostrarError(String texto) {
        mensaje.setText(texto);
        mensaje.setRol(Etiqueta.Rol.PELIGRO);
    }

    // Borra el aviso, dejando un espacio para que la tarjeta no cambie de alto.
    public void limpiarMensaje() {
        registrarRostro.setVisible(false);
        mensaje.setText(" ");
        mensaje.setRol(Etiqueta.Rol.SUAVE);
    }

    // Pone un botón de ojo al lado del campo de contraseña.
    public static JPanel conOjo(CampoClavePixel campo) {
        final char puntitos = campo.getEchoChar();
        JButton ojo = new JButton() {
            // true si la clave se ve.
            private boolean ver = false;
            {
                setPreferredSize(new Dimension(36, 10));
                setContentAreaFilled(false);
                setBorderPainted(false);
                setFocusPainted(false);
                setCursor(Cursor.getPredefinedCursor(Cursor.HAND_CURSOR));
                addActionListener(e -> {
                    Sonido.efecto("click");
                    ver = !ver;
                    campo.setEchoChar(ver ? (char) 0 : puntitos);
                    repaint();
                });
            }

            @Override
            protected void paintComponent(Graphics g) {
                Graphics2D g2 = (Graphics2D) g.create();
                int cx = getWidth() / 2;
                int cy = getHeight() / 2;
                g2.drawImage(Sprites.ui("eye", 24), cx - 12, cy - 12, null);
                if (!ver) {
                    g2.setColor(Tema.texto());
                    g2.setStroke(new BasicStroke(3f));
                    g2.drawLine(cx - 12, cy + 12, cx + 12, cy - 12);
                }
                g2.dispose();
            }
        };
        // Campo y ojo lado a lado: el campo ocupa casi todo y el ojo va a la derecha.
        JPanel p = new JPanel(new BorderLayout(4, 0));
        p.setOpaque(false);
        p.add(campo, BorderLayout.CENTER);
        p.add(ojo, BorderLayout.EAST);
        return p;
    }

    private static JComponent alinear(JComponent c) {
        c.setAlignmentX(Component.LEFT_ALIGNMENT);
        return c;
    }

    
}
