package ecos.pantallas;

// Clases del propio proyecto que se usan aquí.
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

// Librería gráfica AWT, Component para cualquier elemento de la pantalla, Dimension para los tamaños y FlowLayout para
// acomodar en fila.
import java.awt.Component;
import java.awt.Dimension;
import java.awt.FlowLayout;
// Librería Swing, BorderFactory para los bordes, Box para los espacios entre componentes, BoxLayout para apilar en fila
// o columna, JButton para los botones, JComponent para la base de los componentes y JPanel para los paneles.
import javax.swing.BorderFactory;
import javax.swing.Box;
import javax.swing.BoxLayout;
import javax.swing.JButton;
import javax.swing.JComponent;
import javax.swing.JPanel;
// Librería gráfica AWT, GridBagLayout para acomodar en una cuadrícula flexible, BorderLayout para acomodar por zonas,
// Graphics para dibujar, Graphics2D para dibujar con más opciones, BasicStroke para el grosor de las líneas y Cursor
// para el cursor del mouse.
import java.awt.GridBagLayout;
import java.awt.BorderLayout;
import java.awt.Graphics;
import java.awt.Graphics2D;
import java.awt.BasicStroke;
import java.awt.Cursor;

// Pantalla para iniciar sesión con usuario y contraseña o con Face ID; también tiene el botón para registrarse.
public class PantallaAcceso extends Pantalla {

    // Mascotas que desfilan arriba de la tarjeta y campos del usuario y la contraseña.
    private final DesfileMascotas desfile = new DesfileMascotas();
    private final CampoTextoPixel campoUsuario = new CampoTextoPixel("Tu usuario", 16);
    private final CampoClavePixel campoClave = new CampoClavePixel("Tu contraseña", 16);
    // Aquí salen los avisos de error (vacío mientras no haya) y lo que hace el botón Entrar cuando el controlador lo define.
    // Si no se define entra directo (temporal).
    private final Etiqueta mensaje = Etiqueta.texto(" ", 18f, Etiqueta.Rol.SUAVE);
    private Runnable accionEntrar = () -> { };

    // Arma la pantalla.
    public PantallaAcceso() {
        // Id de la pantalla y música que usa. Centra todo en la pantalla. Deja espacio para la barra de arriba. Máximo 20
        // letras en el usuario.
        super("acceso", "acceso");
        setLayout(new GridBagLayout());
        setBorder(BorderFactory.createEmptyBorder(MARGEN_SUPERIOR, 16, 28, 16));
        campoUsuario.setLimite(20);

        // Botón de entrar con usuario y contraseña, que suena y avisa al controlador, y botón para entrar con la cara, que va a
        // la pantalla del escaneo.
        BotonMenu entrar = new BotonMenu("Entrar", true);
        entrar.addActionListener(e -> { Sonido.efecto("click"); accionEntrar.run(); });
        BotonMenu faceId = new BotonMenu("Entrar con Face ID", BotonMenu.Estilo.SECUNDARIO);
        faceId.addActionListener(e -> { Sonido.efecto("click"); irA("login_facial"); });
        // Botón chico para crear una cuenta, que va a la pantalla de registro.
        BotonMenu registrarse = new BotonMenu("Registrarse", BotonMenu.Estilo.ROSA);
        registrarse.setPequeno(true);
        registrarse.addActionListener(e -> { Sonido.efecto("click"); irA("registro"); });

        // Fila, "¿No tenés cuenta?" + botón. Espacio entre el texto y el botón.
        JPanel filaRegistro = new JPanel(new FlowLayout(FlowLayout.LEFT, 0, 0));
        filaRegistro.setOpaque(false);
        filaRegistro.add(Etiqueta.texto("¿No tenés cuenta?", 20f, Etiqueta.Rol.SUAVE));
        filaRegistro.add(Box.createHorizontalStrut(12));
        filaRegistro.add(registrarse);

        // Tarjeta de acceso que apila todo en vertical, con margen interno.
        PanelPixel tarjeta = new PanelPixel();
        tarjeta.setLayout(new BoxLayout(tarjeta, BoxLayout.Y_AXIS));
        tarjeta.setPadding(22);
        tarjeta.add(alinear(Etiqueta.texto("Iniciar sesión", 34f, Etiqueta.Rol.TEXTO)));
        // Etiqueta del usuario. Campo del usuario.
        tarjeta.add(Box.createVerticalStrut(16));
        tarjeta.add(alinear(Etiqueta.titulo("USUARIO", 10f, Etiqueta.Rol.SUAVE)));
        tarjeta.add(alinear(campoUsuario));
        tarjeta.add(Box.createVerticalStrut(12));
        // Etiqueta de la contraseña. Campo de la contraseña con su ojo.
        tarjeta.add(alinear(Etiqueta.titulo("CONTRASEÑA", 10f, Etiqueta.Rol.SUAVE)));
        tarjeta.add(alinear(conOjo(campoClave)));
        tarjeta.add(Box.createVerticalStrut(8));
        tarjeta.add(alinear(mensaje));
        tarjeta.add(Box.createVerticalStrut(10));
        tarjeta.add(alinear(entrar.anchoCompleto()));
        // Botón Entrar con Face ID. "¿No tenés cuenta?" y Registrarse.
        tarjeta.add(Box.createVerticalStrut(10));
        tarjeta.add(alinear(faceId.anchoCompleto()));
        tarjeta.add(Box.createVerticalStrut(16));
        tarjeta.add(alinear(filaRegistro));
        // Ancho máximo de la tarjeta.
        tarjeta.setMaximumSize(new Dimension(480, Integer.MAX_VALUE));

        // Columna con las mascotas arriba y la tarjeta abajo, apiladas en vertical y centradas.
        JPanel columna = new JPanel();
        columna.setOpaque(false);
        columna.setLayout(new BoxLayout(columna, BoxLayout.Y_AXIS));
        desfile.setAlignmentX(Component.CENTER_ALIGNMENT);
        // Tarjeta al centro. Agrega las mascotas. Agrega la tarjeta.
        tarjeta.setAlignmentX(Component.CENTER_ALIGNMENT);
        columna.add(desfile);
        columna.add(Box.createVerticalStrut(18));
        columna.add(tarjeta);
        // Agrega todo a la pantalla.
        add(columna);
    }

    // Se ejecuta cada vez que se entra a esta pantalla.
    @Override
    public void alEntrar() {
        // Pone la música. Borra la contraseña anterior. Empieza el desfile de mascotas.
        super.alEntrar();
        campoClave.setText("");
        limpiarMensaje();
        desfile.iniciar();
    }

    // Se ejecuta cuando se sale de esta pantalla.
    @Override
    public void alSalir() {
        // Detiene el desfile.
        desfile.detener();
    }

    // Usuario escrito (sin espacios sobrantes) y contraseña escrita.
    public String getUsuario() { return campoUsuario.getText().trim(); }
    public String getClave() { return new String(campoClave.getPassword()); }

    // El controlador define qué hace el botón Entrar.
    public void setAccionEntrar(Runnable accion) { accionEntrar = accion; }

    // El controlador la llama cuando el login salió bien.
    public void irAMenu() { irA("menu"); }

    // Muestra un aviso de error en rojo.
    public void mostrarError(String texto) {
        mensaje.setText(texto);
        mensaje.setRol(Etiqueta.Rol.PELIGRO);
    }

    // Borra el aviso (deja un espacio para que la tarjeta no cambie de alto).
    public void limpiarMensaje() {
        mensaje.setText(" ");
        mensaje.setRol(Etiqueta.Rol.SUAVE);
    }

    // Pone un botón de ojo al lado del campo de contraseña.
    public static JPanel conOjo(CampoClavePixel campo) {
        // Guarda el puntito normal.
        final char puntitos = campo.getEchoChar();
        // Botón que dibuja el icono del ojo.
        JButton ojo = new JButton() {
            // True = la clave se ve.
            private boolean ver = false;
            {
                // Ancho del botón (el alto lo toma del campo). Sin fondo de botón. Sin marco de foco.
                setPreferredSize(new Dimension(36, 10));
                setContentAreaFilled(false);
                setBorderPainted(false);
                setFocusPainted(false);
                // Manito al pasar el mouse. Al hacer clic. Suena el clic. Cambia entre ver y esconder.
                setCursor(Cursor.getPredefinedCursor(Cursor.HAND_CURSOR));
                addActionListener(e -> {
                    Sonido.efecto("click");
                    ver = !ver;
                    // 0 = muestra el texto real. Redibuja el ojo.
                    campo.setEchoChar(ver ? (char) 0 : puntitos);
                    repaint();
                });
            }

            // Dibuja el icono.
            @Override
            protected void paintComponent(Graphics g) {
                // Copia del pincel. Icono eye.png del proyecto.
                Graphics2D g2 = (Graphics2D) g.create();
                int cx = getWidth() / 2;
                int cy = getHeight() / 2;
                g2.drawImage(Sprites.ui("eye", 24), cx - 12, cy - 12, null);
                // Si la clave está escondida. Color de la raya.
                if (!ver) {
                    g2.setColor(Tema.texto());
                    g2.setStroke(new BasicStroke(3f));
                    g2.drawLine(cx - 12, cy + 12, cx + 12, cy - 12);
                }
                // Libera el pincel.
                g2.dispose();
            }
        };
        // Campo y ojo lado a lado, el campo ocupa casi todo y el ojo va a la derecha.
        JPanel p = new JPanel(new BorderLayout(4, 0));
        p.setOpaque(false);
        p.add(campo, BorderLayout.CENTER);
        p.add(ojo, BorderLayout.EAST);
        return p;
    }

    // Alinea un componente a la izquierda de la tarjeta.
    private static JComponent alinear(JComponent c) {
        c.setAlignmentX(Component.LEFT_ALIGNMENT);
        return c;
    }

    
}