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

import java.awt.GridBagLayout;
import javax.swing.BorderFactory;
import javax.swing.Box;
import javax.swing.BoxLayout;
import javax.swing.JButton;
import javax.swing.JComponent;
import javax.swing.JPanel;
import java.awt.BorderLayout;
import java.awt.Component;
import java.awt.Dimension;
import java.awt.GridBagConstraints;
import java.awt.Cursor;
import java.awt.Graphics;
import java.awt.Graphics2D;
import java.awt.BasicStroke;

// Formulario de registro: nombre, apellidos, correo, usuario y contraseña. Después lleva al registro facial. La conecta
// ControladorRegistro, que revisa el formulario con ModeloRegistro.
public class PantallaRegistro extends Pantalla {
    private static final String AYUDA = "Completá todos los campos para continuar.";

    // Mascotas que desfilan arriba de la tarjeta y campos de nombre (máx. 50, igual que la base), apellidos (máx. 50) y
    // correo (máx. 100).
    private final DesfileMascotas desfile = new DesfileMascotas();
    private final CampoTextoPixel campoNombre = new CampoTextoPixel("Tu nombre", 5);
    private final CampoTextoPixel campoApellidos = new CampoTextoPixel("Tus apellidos", 5);
    private final CampoTextoPixel campoCorreo = new CampoTextoPixel("tucorreo@ejemplo.com", 5);
    // Campos de usuario (máx. 20) y de repetir la contraseña, y espacio de avisos, por ejemplo "Ese usuario ya existe".
    private final CampoTextoPixel campoUsuario = new CampoTextoPixel("Tu usuario", 5);
    private final CampoClavePixel campoClave = new CampoClavePixel("6 o más", 5);
    private final CampoClavePixel campoClave2 = new CampoClavePixel("Otra vez", 5);
    private final Etiqueta mensaje = Etiqueta.texto(AYUDA, 18f, Etiqueta.Rol.SUAVE);
    // Acciones de Siguiente y Crear cuenta, que pone el controlador; los botones se guardan para apagarlos mientras se
    // guarda.
    private Runnable accionSiguiente = null;
    private Runnable accionCrearCuenta = () -> { };
    private final BotonMenu siguiente = new BotonMenu("Mi rostro (opcional)", true);
    private final BotonMenu crearCuenta = new BotonMenu("Crear cuenta", BotonMenu.Estilo.SECUNDARIO);

    public PantallaRegistro() {
        super("registro", "acceso");
        setLayout(new GridBagLayout());
        setBorder(BorderFactory.createEmptyBorder(MARGEN_SUPERIOR, 16, 10, 16));

        campoNombre.setLimite(50);
        campoApellidos.setLimite(50);
        campoCorreo.setLimite(100);
        campoUsuario.setLimite(20);

        JPanel formulario = new JPanel(new GridBagLayout());
        formulario.setOpaque(false);
        agregar(formulario, bloque("NOMBRE", campoNombre), 0, 0, 1);
        agregar(formulario, bloque("APELLIDOS", campoApellidos), 0, 1, 1);
        agregar(formulario, bloque("CORREO ELECTRÓNICO", campoCorreo), 1, 0, 2);
        agregar(formulario, bloque("USUARIO", campoUsuario), 2, 0, 2);
        agregar(formulario, bloque("CONTRASEÑA", conOjo(campoClave)), 3, 0, 1);
        agregar(formulario, bloque("REPETIR CONTRASEÑA", conOjo(campoClave2)), 3, 1, 1);

        // Siguiente suena y ejecuta la acción del controlador; Crear cuenta suena y avisa al controlador.
        crearCuenta.addActionListener(e -> { Sonido.efecto("click"); accionCrearCuenta.run(); });
        siguiente.addActionListener(e -> {
            Sonido.efecto("click");
            if (accionSiguiente != null) {
                accionSiguiente.run();
            } else {
                irA("registro_facial");
            }
        });

          JPanel titulo = new JPanel(new BorderLayout());
        titulo.setOpaque(false);
        titulo.add(Etiqueta.texto("Crear cuenta", 32f, Etiqueta.Rol.TEXTO), BorderLayout.WEST);
        titulo.add(Etiqueta.titulo("PASO 1 DE 2", 10f, Etiqueta.Rol.SUAVE), BorderLayout.EAST);
        titulo.setMaximumSize(new Dimension(Integer.MAX_VALUE, titulo.getPreferredSize().height));

        PanelPixel tarjeta = new PanelPixel();
        tarjeta.setLayout(new BoxLayout(tarjeta, BoxLayout.Y_AXIS));
        tarjeta.setPadding(22);
        tarjeta.add(alinear(titulo));
        tarjeta.add(Box.createVerticalStrut(12));
        formulario.setMaximumSize(new Dimension(Integer.MAX_VALUE, formulario.getPreferredSize().height));
        tarjeta.add(alinear(formulario));
        tarjeta.add(Box.createVerticalStrut(2));
        tarjeta.add(alinear(mensaje));
        tarjeta.add(Box.createVerticalStrut(6));
        tarjeta.add(alinear(siguiente.anchoCompleto()));
        tarjeta.add(Box.createVerticalStrut(6));
        tarjeta.add(alinear(crearCuenta.anchoCompleto()));
        tarjeta.setMaximumSize(new Dimension(480, Integer.MAX_VALUE));

        JPanel columna = new JPanel();
        columna.setOpaque(false);
        columna.setLayout(new BoxLayout(columna, BoxLayout.Y_AXIS));
        desfile.setAlignmentX(Component.CENTER_ALIGNMENT);
        tarjeta.setAlignmentX(Component.CENTER_ALIGNMENT);
        columna.add(desfile);
        columna.add(Box.createVerticalStrut(4));
        columna.add(tarjeta);
        add(columna);

    }

    public String getNombre() { return campoNombre.getText().trim(); }
    public String getApellidos() { return campoApellidos.getText().trim(); }
    public String getCorreo() { return campoCorreo.getText().trim(); }
    public String getUsuario() { return campoUsuario.getText().trim(); }
    public String getClave() { return new String(campoClave.getPassword()); }
    public String getClave2() { return new String(campoClave2.getPassword()); }

    // El controlador puede cambiar lo que hace el botón, por ejemplo validar primero.
    public void setAccionSiguiente(Runnable accion) {
        this.accionSiguiente = accion;
    }

    // El controlador define qué hace Crear cuenta: crearla sin rostro.
    public void setAccionCrearCuenta(Runnable accion) { accionCrearCuenta = accion; }

    // Apaga los dos botones mientras se guarda la cuenta y los vuelve a prender al terminar.
    public void setOcupado(boolean ocupado) {
        siguiente.setEnabled(!ocupado);
        crearCuenta.setEnabled(!ocupado);
    }

    // Aviso en verde y, un momento después, vuelve al acceso para iniciar sesión.
    public void mostrarCuentaCreada() {
        mensaje.setText("¡Cuenta creada! Ya podés iniciar sesión.");
        mensaje.setRol(Etiqueta.Rol.ACIERTO);
        Sonido.efecto("registro_ok");
        javax.swing.Timer espera = new javax.swing.Timer(1500, e -> irA("acceso"));
        espera.setRepeats(false);
        espera.start();
    }

    public void irARegistroFacial() {
        irA("registro_facial");
    }

    public void mostrarError(String texto) {
        // Aviso en rojo con sonido de error.
        mensaje.setText(texto);
        mensaje.setRol(Etiqueta.Rol.PELIGRO);
        Sonido.efecto("error");
    }

    public void limpiarMensaje() {
        mensaje.setText(AYUDA);
        mensaje.setRol(Etiqueta.Rol.SUAVE);
    }

    @Override
    public void alEntrar() {
        // Al entrar se borran las contraseñas y los avisos anteriores.
        super.alEntrar();
        campoClave.setText("");
        campoClave2.setText("");
        limpiarMensaje();
        desfile.iniciar();
    }

    @Override
    public void alSalir() {
        desfile.detener();
        super.alSalir();
    }

    private static JPanel bloque(String etiqueta, JComponent campo) {
        JPanel p = new JPanel(new BorderLayout(0, 4));
        p.setOpaque(false);
        p.add(Etiqueta.titulo(etiqueta, 10f, Etiqueta.Rol.SUAVE), BorderLayout.NORTH);
        p.add(campo, BorderLayout.CENTER);
        return p;
    }

    private static void agregar(JPanel formulario, JComponent bloque, int fila, int columna, int columnas) {
        GridBagConstraints c = new GridBagConstraints();
        c.gridx = columna;
        c.gridy = fila;
        c.gridwidth = columnas;
        c.weightx = columnas;
        c.fill = GridBagConstraints.HORIZONTAL;
        int izq = (columnas == 1 && columna == 1) ? 7 : 0;
        int der = (columnas == 1 && columna == 0) ? 7 : 0;
        c.insets = new java.awt.Insets(0, izq, 10, der);
        formulario.add(bloque, c);
    }

    private static JComponent alinear(JComponent c) {
        c.setAlignmentX(Component.LEFT_ALIGNMENT);
        return c;
    }

    // Pone un botón de ojo al lado del campo de contraseña.
    private static JPanel conOjo(CampoClavePixel campo) {
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

}
