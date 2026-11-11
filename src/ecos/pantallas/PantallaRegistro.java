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

// Librería gráfica AWT, GridBagLayout para acomodar en una cuadrícula flexible.
import java.awt.GridBagLayout;
// Librería Swing, BorderFactory para los bordes, Box para los espacios entre componentes, BoxLayout para apilar en fila
// o columna, JButton para los botones, JComponent para la base de los componentes y JPanel para los paneles.
import javax.swing.BorderFactory;
import javax.swing.Box;
import javax.swing.BoxLayout;
import javax.swing.JButton;
import javax.swing.JComponent;
import javax.swing.JPanel;
// Librería gráfica AWT, BorderLayout para acomodar por zonas, Component para cualquier elemento de la pantalla,
// Dimension para los tamaños, GridBagConstraints para las reglas de la cuadrícula flexible, Cursor para el cursor del
// mouse, Graphics para dibujar, Graphics2D para dibujar con más opciones y BasicStroke para el grosor de las líneas.
import java.awt.BorderLayout;
import java.awt.Component;
import java.awt.Dimension;
import java.awt.GridBagConstraints;
import java.awt.Cursor;
import java.awt.Graphics;
import java.awt.Graphics2D;
import java.awt.BasicStroke;


// Formulario de registro, nombre, apellidos, correo, usuario y contraseña. Después lleva al registro facial.
// A cargo de Valeria. Pantalla lista; falta conectarla a su controlador y al modelo.
public class PantallaRegistro extends Pantalla {
    // Mensaje normal de la pantalla.
    private static final String AYUDA = "Completá todos los campos para continuar.";

    // Mascotas que desfilan arriba de la tarjeta y campos del nombre (máx. 50, igual que la base de datos), los apellidos
    // (máx. 50) y el correo (máx. 100).
    private final DesfileMascotas desfile = new DesfileMascotas();
    private final CampoTextoPixel campoNombre = new CampoTextoPixel("Tu nombre", 5);
    private final CampoTextoPixel campoApellidos = new CampoTextoPixel("Tus apellidos", 5);
    private final CampoTextoPixel campoCorreo = new CampoTextoPixel("tucorreo@ejemplo.com", 5);
    // Campos del usuario (máx. 20) y de repetir la contraseña, y el espacio de avisos, por ejemplo "Ese usuario ya existe".
    private final CampoTextoPixel campoUsuario = new CampoTextoPixel("Tu usuario", 5);
    private final CampoClavePixel campoClave = new CampoClavePixel("6 o más", 5);
    private final CampoClavePixel campoClave2 = new CampoClavePixel("Otra vez", 5);
    private final Etiqueta mensaje = Etiqueta.texto(AYUDA, 18f, Etiqueta.Rol.SUAVE);
    // Qué hace el botón Siguiente; null = ir directo al registro facial. Qué hace el botón Crear cuenta, que lo pone el
    // controlador. Los dos botones, para poder apagarlos mientras se guarda.
    private Runnable accionSiguiente = null;
    private Runnable accionCrearCuenta = () -> { };
    private final BotonMenu siguiente = new BotonMenu("Mi rostro (opcional)", true);
    private final BotonMenu crearCuenta = new BotonMenu("Crear cuenta", BotonMenu.Estilo.SECUNDARIO);

    public PantallaRegistro() {
        // Id de la pantalla y música que usa.
        super("registro", "acceso");
        setLayout(new GridBagLayout());
        setBorder(BorderFactory.createEmptyBorder(MARGEN_SUPERIOR, 16, 10, 16));

        // Máximo 50 letras en el nombre. Máximo 50 letras en los apellidos. Máximo 100 letras en el correo. Máximo 20 letras
        // en el usuario.
        campoNombre.setLimite(50);
        campoApellidos.setLimite(50);
        campoCorreo.setLimite(100);
        campoUsuario.setLimite(20);

        // Cuadrícula de dos columnas con los campos.
        JPanel formulario = new JPanel(new GridBagLayout());
        formulario.setOpaque(false);
        agregar(formulario, bloque("NOMBRE", campoNombre), 0, 0, 1);
        agregar(formulario, bloque("APELLIDOS", campoApellidos), 0, 1, 1);
        agregar(formulario, bloque("CORREO ELECTRÓNICO", campoCorreo), 1, 0, 2);
        agregar(formulario, bloque("USUARIO", campoUsuario), 2, 0, 2);
        agregar(formulario, bloque("CONTRASEÑA", conOjo(campoClave)), 3, 0, 1);
        agregar(formulario, bloque("REPETIR CONTRASEÑA", conOjo(campoClave2)), 3, 1, 1);

        // Botón que lleva al registro facial, que al apretarse suena el clic y, si el controlador puso su propia acción, la
        // ejecuta. El botón Crear cuenta suena y avisa al controlador.
        crearCuenta.addActionListener(e -> { Sonido.efecto("click"); accionCrearCuenta.run(); });
        siguiente.addActionListener(e -> {
            Sonido.efecto("click");
            if (accionSiguiente != null) {
                // Si no, solo navega. Va a la pantalla de captura del rostro.
                accionSiguiente.run();
            } else {
                irA("registro_facial");
            }
        });

          JPanel titulo = new JPanel(new BorderLayout());
        // Indicador de paso. No se estira hacia abajo.
        titulo.setOpaque(false);
        titulo.add(Etiqueta.texto("Crear cuenta", 32f, Etiqueta.Rol.TEXTO), BorderLayout.WEST);
        titulo.add(Etiqueta.titulo("PASO 1 DE 2", 10f, Etiqueta.Rol.SUAVE), BorderLayout.EAST);
        titulo.setMaximumSize(new Dimension(Integer.MAX_VALUE, titulo.getPreferredSize().height));

        // Tarjeta del formulario que apila todo en vertical, con el mismo margen interno que PantallaAcceso.
        PanelPixel tarjeta = new PanelPixel();
        tarjeta.setLayout(new BoxLayout(tarjeta, BoxLayout.Y_AXIS));
        tarjeta.setPadding(22);
        tarjeta.add(alinear(titulo));
        // El formulario no se estira hacia abajo.
        tarjeta.add(Box.createVerticalStrut(12));
        formulario.setMaximumSize(new Dimension(Integer.MAX_VALUE, formulario.getPreferredSize().height));
        tarjeta.add(alinear(formulario));
        tarjeta.add(Box.createVerticalStrut(2));
        // Mensaje de avisos. Ancho máximo de la tarjeta (igual que en PantallaAcceso).
        tarjeta.add(alinear(mensaje));
        tarjeta.add(Box.createVerticalStrut(6));
        tarjeta.add(alinear(siguiente.anchoCompleto()));
        tarjeta.add(Box.createVerticalStrut(6));
        tarjeta.add(alinear(crearCuenta.anchoCompleto()));
        tarjeta.setMaximumSize(new Dimension(480, Integer.MAX_VALUE));

        // Columna con las mascotas arriba y la tarjeta abajo, apiladas en vertical y centradas.
        JPanel columna = new JPanel();
        columna.setOpaque(false);
        columna.setLayout(new BoxLayout(columna, BoxLayout.Y_AXIS));
        desfile.setAlignmentX(Component.CENTER_ALIGNMENT);
        // Tarjeta al centro. Agrega las mascotas. Agrega la tarjeta.
        tarjeta.setAlignmentX(Component.CENTER_ALIGNMENT);
        columna.add(desfile);
        columna.add(Box.createVerticalStrut(4));
        columna.add(tarjeta);
        // Agrega todo a la pantalla.
        add(columna);



    }

    // Métodos que usa el controlador (ControladorRegistro).

    // Entrega el nombre escrito. Entrega los apellidos escritos. Entrega el correo escrito. Entrega el usuario escrito.
    public String getNombre() { return campoNombre.getText().trim(); }
    public String getApellidos() { return campoApellidos.getText().trim(); }
    public String getCorreo() { return campoCorreo.getText().trim(); }
    public String getUsuario() { return campoUsuario.getText().trim(); }
    // Entrega la contraseña escrita. Entrega la contraseña repetida.
    public String getClave() { return new String(campoClave.getPassword()); }
    public String getClave2() { return new String(campoClave2.getPassword()); }

    // El controlador puede cambiar lo que hace el botón (por ejemplo, validar primero).
    public void setAccionSiguiente(Runnable accion) {
        // Guarda la acción.
        this.accionSiguiente = accion;
    }

    // El controlador define qué hace el botón Crear cuenta (crear la cuenta sin rostro).
    public void setAccionCrearCuenta(Runnable accion) { accionCrearCuenta = accion; }

    // Apaga los dos botones mientras se guarda la cuenta, y los vuelve a prender al terminar.
    public void setOcupado(boolean ocupado) {
        siguiente.setEnabled(!ocupado);
        crearCuenta.setEnabled(!ocupado);
    }

    // Avisa que la cuenta quedó creada y, un momento después, va a iniciar sesión.
    public void mostrarCuentaCreada() {
        // Pone el aviso en verde. Suena el éxito. Espera un momento para que se lea y va a la pantalla de acceso.
        mensaje.setText("¡Cuenta creada! Ya podés iniciar sesión.");
        mensaje.setRol(Etiqueta.Rol.ACIERTO);
        Sonido.efecto(Sonido.EXITO);
        javax.swing.Timer espera = new javax.swing.Timer(1500, e -> irA("acceso"));
        espera.setRepeats(false);
        espera.start();
    }

    // El controlador la llama cuando los datos están bien.
    public void irARegistroFacial() {
        // Va a la captura del rostro.
        irA("registro_facial");
    }

    // Muestra un aviso de error en rojo.
    public void mostrarError(String texto) {
        // Pone el texto. Lo pinta de rojo. Suena el error.
        mensaje.setText(texto);
        mensaje.setRol(Etiqueta.Rol.PELIGRO);
        Sonido.efecto("error");
    }

    // Vuelve al mensaje normal.
    public void limpiarMensaje() {
        // Pone el texto normal. Lo pinta apagado.
        mensaje.setText(AYUDA);
        mensaje.setRol(Etiqueta.Rol.SUAVE);
    }



    // Se ejecuta cada vez que se entra a esta pantalla.
    @Override
    public void alEntrar() {
        // Pone la música. Borra la contraseña anterior. Borra la contraseña repetida anterior. Quita avisos viejos.
        super.alEntrar();
        campoClave.setText("");
        campoClave2.setText("");
        limpiarMensaje();
        // Empieza el desfile de mascotas.
        desfile.iniciar();
    }

    // Se ejecuta cuando se sale de esta pantalla.
    @Override
    public void alSalir() {
        // Detiene el desfile. Cierra ventanas abiertas y avisa al controlador.
        desfile.detener();
        super.alSalir();
    }

    // Métodos auxiliares para armar la pantalla.

    // Etiqueta chica arriba y el campo abajo.
    private static JPanel bloque(String etiqueta, JComponent campo) {
        // Panel con 4 px de espacio entre la etiqueta de arriba y el campo de abajo, que se estira a lo ancho.
        JPanel p = new JPanel(new BorderLayout(0, 4));
        p.setOpaque(false);
        p.add(Etiqueta.titulo(etiqueta, 10f, Etiqueta.Rol.SUAVE), BorderLayout.NORTH);
        p.add(campo, BorderLayout.CENTER);
        return p;
    }

    // Pone un bloque en la cuadrícula.
    private static void agregar(JPanel formulario, JComponent bloque, int fila, int columna, int columnas) {
        // Reglas de posición. Cuántas columnas ocupa.
        GridBagConstraints c = new GridBagConstraints();
        c.gridx = columna;
        c.gridy = fila;
        c.gridwidth = columnas;
        // Las dos columnas se reparten el ancho por igual. Se estira a lo ancho. Espacio a la izquierda en la columna
        // derecha. Espacio a la derecha en la columna izquierda.
        c.weightx = columnas;
        c.fill = GridBagConstraints.HORIZONTAL;
        int izq = (columnas == 1 && columna == 1) ? 7 : 0;
        int der = (columnas == 1 && columna == 0) ? 7 : 0;
        // 10 px de espacio abajo entre filas.
        c.insets = new java.awt.Insets(0, izq, 10, der);
        formulario.add(bloque, c);
    }

    // Alinea un componente a la izquierda de la tarjeta.
    private static JComponent alinear(JComponent c) {
        c.setAlignmentX(Component.LEFT_ALIGNMENT);
        return c;
    }

    // Pone un botón de ojo al lado del campo de contraseña.
    private static JPanel conOjo(CampoClavePixel campo) {
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


}
