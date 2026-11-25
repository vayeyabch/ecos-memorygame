package ecos.pantallas;

// Pantalla es el molde del que hereda esta pantalla. Le da el id, la música y los ganchos de entrar y salir.
import ecos.app.Pantalla;
// Tema da el color de la raya que tacha el ojo de la contraseña.
import ecos.app.Tema;
// BotonMenu es el botón grande del juego, con estilos de color.
import ecos.componentes.BotonMenu;
// CampoClavePixel es el campo de contraseña con el estilo pixel del juego.
import ecos.componentes.CampoClavePixel;
// CampoTextoPixel es el campo de texto con el estilo pixel del juego.
import ecos.componentes.CampoTextoPixel;
// DesfileMascotas es la animación de las 9 mascotas saltando en fila.
import ecos.componentes.DesfileMascotas;
// Etiqueta es el texto del juego, con fuente, color de tema y traducción resueltos.
import ecos.componentes.Etiqueta;
// PanelPixel es el panel con el estilo pixel del juego. Aquí es la tarjeta del formulario.
import ecos.componentes.PanelPixel;
// Sonido reproduce los efectos de clic, error y registro correcto.
import ecos.recursos.Sonido;
// Sprites da la imagen del ojo.
import ecos.recursos.Sprites;

// GridBagLayout acomoda las piezas en una cuadrícula flexible. Aquí sirve para el formulario y para centrar la columna.
import java.awt.GridBagLayout;
// BorderFactory crea bordes. Aquí crea un borde vacío, que sirve de margen.
import javax.swing.BorderFactory;
// Box trae los espacios invisibles. Aquí separa las piezas hacia abajo.
import javax.swing.Box;
// BoxLayout acomoda las piezas una debajo de otra. Aquí se usa en la tarjeta y en la columna.
import javax.swing.BoxLayout;
// JButton es el botón de Swing. Aquí se usa para el ojo, que se dibuja a mano.
import javax.swing.JButton;
// JComponent es el tipo base de las piezas de Swing. Los métodos reciben y devuelven este tipo.
import javax.swing.JComponent;
// JPanel es un panel, un contenedor donde se meten otras piezas.
import javax.swing.JPanel;
// BorderLayout acomoda piezas en norte, sur, este, oeste y centro.
import java.awt.BorderLayout;
// Component trae las constantes de alineación, como CENTER_ALIGNMENT y LEFT_ALIGNMENT.
import java.awt.Component;
// Dimension es un tamaño, con ancho y alto.
import java.awt.Dimension;
// GridBagConstraints son las reglas de posición y tamaño de una pieza dentro de un GridBagLayout.
import java.awt.GridBagConstraints;
// Cursor es el puntero del mouse. Aquí se usa la manito sobre el ojo.
import java.awt.Cursor;
// Graphics es el pincel general con el que Swing pide dibujar. Llega como dato a paintComponent.
import java.awt.Graphics;
// Graphics2D es el pincel con más opciones. Es el que se usa para dibujar el ojo.
import java.awt.Graphics2D;
// BasicStroke define el grosor de una línea. Aquí es el de la raya que tacha el ojo.
import java.awt.BasicStroke;

// Es el formulario de crear cuenta, paso 1 de 2: nombre, apellidos, correo, usuario y contraseña.
// Después lleva al registro facial, que es opcional. Es una vista: dibuja y avisa, pero no valida nada.
// La conecta ControladorRegistro, que revisa los datos con ModeloRegistro y llena los ganchos de esta clase.
public class PantallaRegistro extends Pantalla {
    // Mensaje que se muestra cuando no hay ningún aviso.
    private static final String AYUDA = "Completá todos los campos para continuar.";

    // Las mascotas que desfilan arriba de la tarjeta.
    private final DesfileMascotas desfile = new DesfileMascotas();
    // Los campos del formulario. El primer dato es el texto guía que se ve mientras el campo está vacío,
    // y el 5 es un ajuste propio del campo. Más abajo se les pone el límite de caracteres.
    private final CampoTextoPixel campoNombre = new CampoTextoPixel("Tu nombre", 5);
    private final CampoTextoPixel campoApellidos = new CampoTextoPixel("Tus apellidos", 5);
    private final CampoTextoPixel campoCorreo = new CampoTextoPixel("tucorreo@ejemplo.com", 5);
    private final CampoTextoPixel campoUsuario = new CampoTextoPixel("Tu usuario", 5);
    // Los campos de contraseña y de repetir la contraseña.
    private final CampoClavePixel campoClave = new CampoClavePixel("6 o más", 5);
    private final CampoClavePixel campoClave2 = new CampoClavePixel("Otra vez", 5);
    // El espacio de avisos, por ejemplo "Ese usuario ya existe". Empieza con la ayuda en gris.
    private final Etiqueta mensaje = Etiqueta.texto(AYUDA, 18f, Etiqueta.Rol.SUAVE);
    // Acción de Siguiente, que pone el controlador. Empieza en null y, si nadie la llena, el botón va directo al registro facial.
    private Runnable accionSiguiente = null;
    // Acción de Crear cuenta, que pone el controlador. Empieza vacía para no fallar si nadie la llena.
    private Runnable accionCrearCuenta = () -> { };
    // Los dos botones se guardan en campos para poder apagarlos mientras se guarda la cuenta.
    private final BotonMenu siguiente = new BotonMenu("Mi rostro (opcional)", true);
    private final BotonMenu crearCuenta = new BotonMenu("Crear cuenta", BotonMenu.Estilo.SECUNDARIO);

    // Arma la pantalla: los campos, los botones y la tarjeta con el desfile de mascotas.
    public PantallaRegistro() {
        // super llama al constructor de Pantalla con el id "registro" y la música "acceso".
        super("registro", "acceso");
        // Con GridBagLayout y una sola pieza, esa pieza queda centrada en la pantalla.
        setLayout(new GridBagLayout());
        // Margen vacío: arriba deja el espacio de la barra superior, 16 a los lados y 10 abajo.
        setBorder(BorderFactory.createEmptyBorder(MARGEN_SUPERIOR, 16, 10, 16));

        // Límites de caracteres. Son los mismos que tienen las columnas de la base de datos.
        campoNombre.setLimite(50);
        campoApellidos.setLimite(50);
        campoCorreo.setLimite(100);
        campoUsuario.setLimite(20);

        // El formulario es un panel con una cuadrícula flexible, transparente.
        JPanel formulario = new JPanel(new GridBagLayout());
        formulario.setOpaque(false);
        // Cada línea agrega un campo con su título. Los tres números son la fila, la columna y cuántas columnas ocupa.
        // Fila 0: nombre y apellidos lado a lado. Fila 1: correo, ancho completo. Fila 2: usuario, ancho completo.
        // Fila 3: contraseña y repetir contraseña lado a lado, cada una con su botón de ojo.
        agregar(formulario, bloque("NOMBRE", campoNombre), 0, 0, 1);
        agregar(formulario, bloque("APELLIDOS", campoApellidos), 0, 1, 1);
        agregar(formulario, bloque("CORREO ELECTRÓNICO", campoCorreo), 1, 0, 2);
        agregar(formulario, bloque("USUARIO", campoUsuario), 2, 0, 2);
        agregar(formulario, bloque("CONTRASEÑA", conOjo(campoClave)), 3, 0, 1);
        agregar(formulario, bloque("REPETIR CONTRASEÑA", conOjo(campoClave2)), 3, 1, 1);

        // Qué hace cada botón. La flecha es la función que se ejecuta, y e es el aviso del clic.
        // Crear cuenta: suena y ejecuta la acción que dejó el controlador.
        crearCuenta.addActionListener(e -> { Sonido.efecto("click"); accionCrearCuenta.run(); });
        // Siguiente: suena y, si el controlador dejó una acción, la ejecuta. Si no, va al registro facial.
        siguiente.addActionListener(e -> {
            Sonido.efecto("click");
            if (accionSiguiente != null) {
                accionSiguiente.run();
            } else {
                irA("registro_facial");
            }
        });

        // El título de la tarjeta: "Crear cuenta" a la izquierda y "PASO 1 DE 2" a la derecha.
        JPanel titulo = new JPanel(new BorderLayout());
        titulo.setOpaque(false);
        titulo.add(Etiqueta.texto("Crear cuenta", 32f, Etiqueta.Rol.TEXTO), BorderLayout.WEST);
        titulo.add(Etiqueta.titulo("PASO 1 DE 2", 10f, Etiqueta.Rol.SUAVE), BorderLayout.EAST);
        // Ancho máximo libre y alto máximo igual a su alto natural, para que no se estire hacia abajo en la tarjeta.
        titulo.setMaximumSize(new Dimension(Integer.MAX_VALUE, titulo.getPreferredSize().height));

        // La tarjeta con todo adentro, una pieza debajo de otra y con 22 píxeles de margen interior.
        PanelPixel tarjeta = new PanelPixel();
        tarjeta.setLayout(new BoxLayout(tarjeta, BoxLayout.Y_AXIS));
        tarjeta.setPadding(22);
        // Cada pieza se agrega alineada a la izquierda con alinear, y los struts son espacios invisibles entre ellas.
        tarjeta.add(alinear(titulo));
        tarjeta.add(Box.createVerticalStrut(12));
        // Igual que el título: el formulario no se estira hacia abajo.
        formulario.setMaximumSize(new Dimension(Integer.MAX_VALUE, formulario.getPreferredSize().height));
        tarjeta.add(alinear(formulario));
        tarjeta.add(Box.createVerticalStrut(2));
        tarjeta.add(alinear(mensaje));
        tarjeta.add(Box.createVerticalStrut(6));
        // anchoCompleto es un método de BotonMenu que deja el botón del ancho de la tarjeta.
        tarjeta.add(alinear(siguiente.anchoCompleto()));
        tarjeta.add(Box.createVerticalStrut(6));
        tarjeta.add(alinear(crearCuenta.anchoCompleto()));
        // La tarjeta mide como máximo 480 píxeles de ancho.
        tarjeta.setMaximumSize(new Dimension(480, Integer.MAX_VALUE));

        // Una columna con el desfile de mascotas arriba y la tarjeta debajo, las dos centradas.
        JPanel columna = new JPanel();
        columna.setOpaque(false);
        columna.setLayout(new BoxLayout(columna, BoxLayout.Y_AXIS));
        desfile.setAlignmentX(Component.CENTER_ALIGNMENT);
        tarjeta.setAlignmentX(Component.CENTER_ALIGNMENT);
        columna.add(desfile);
        columna.add(Box.createVerticalStrut(4));
        columna.add(tarjeta);
        // Se agrega a la pantalla, y el GridBagLayout la deja en el centro.
        add(columna);
    }

    // Los getters entregan lo que el usuario escribió. El controlador los usa para validar y guardar.
    // trim quita los espacios del principio y del final.
    public String getNombre() { return campoNombre.getText().trim(); }
    public String getApellidos() { return campoApellidos.getText().trim(); }
    public String getCorreo() { return campoCorreo.getText().trim(); }
    public String getUsuario() { return campoUsuario.getText().trim(); }
    // getPassword devuelve un arreglo de caracteres, y new String lo convierte en texto.
    public String getClave() { return new String(campoClave.getPassword()); }
    public String getClave2() { return new String(campoClave2.getPassword()); }

    // Es un gancho de MVC. El controlador cambia lo que hace Siguiente, por ejemplo para validar primero.
    public void setAccionSiguiente(Runnable accion) {
        this.accionSiguiente = accion;
    }

    // Es un gancho de MVC. El controlador define qué hace Crear cuenta: crearla sin rostro.
    public void setAccionCrearCuenta(Runnable accion) { accionCrearCuenta = accion; }

    // Apaga los dos botones mientras se guarda la cuenta y los vuelve a prender al terminar.
    // Evita que un doble clic cree la cuenta dos veces. El ! significa no.
    public void setOcupado(boolean ocupado) {
        siguiente.setEnabled(!ocupado);
        crearCuenta.setEnabled(!ocupado);
    }

    // Muestra el aviso en verde con sonido y, un momento después, vuelve al acceso para iniciar sesión.
    public void mostrarCuentaCreada() {
        mensaje.setText("¡Cuenta creada! Ya podés iniciar sesión.");
        mensaje.setRol(Etiqueta.Rol.ACIERTO);
        Sonido.efecto("registro_ok");
        // Un Timer de 1500 milisegundos, que es 1.5 segundos, y luego va a la pantalla de acceso.
        // Se escribe con el nombre completo porque Timer no está importado.
        javax.swing.Timer espera = new javax.swing.Timer(1500, e -> irA("acceso"));
        // setRepeats(false) hace que se ejecute una sola vez y no cada 1.5 segundos.
        espera.setRepeats(false);
        espera.start();
    }

    // Lleva a la pantalla del registro facial. La usa el controlador cuando los datos ya son válidos.
    public void irARegistroFacial() {
        irA("registro_facial");
    }

    // Muestra un aviso de error en rojo con su sonido. El texto lo decide el controlador.
    public void mostrarError(String texto) {
        mensaje.setText(texto);
        mensaje.setRol(Etiqueta.Rol.PELIGRO);
        Sonido.efecto("error");
    }

    // Vuelve el mensaje a la ayuda en gris.
    public void limpiarMensaje() {
        mensaje.setText(AYUDA);
        mensaje.setRol(Etiqueta.Rol.SUAVE);
    }

    // Se ejecuta cada vez que se muestra la pantalla. Borra las contraseñas y los avisos anteriores y arranca el desfile.
    @Override
    public void alEntrar() {
        // super.alEntrar pone la música, repinta y ejecuta la acción que dejó el controlador.
        super.alEntrar();
        campoClave.setText("");
        campoClave2.setText("");
        limpiarMensaje();
        desfile.iniciar();
    }

    // Se ejecuta cuando la pantalla se oculta. Detiene el desfile para que su reloj no siga animando una pantalla oculta.
    // Aquí sí llama a super.alSalir, que cierra los diálogos y ejecuta la acción de salida del controlador.
    @Override
    public void alSalir() {
        desfile.detener();
        super.alSalir();
    }

    // Arma un campo con su título arriba. El título va en el norte del panel y el campo en el centro.
    private static JPanel bloque(String etiqueta, JComponent campo) {
        // BorderLayout(0, 4) deja 0 píxeles de separación horizontal y 4 vertical.
        JPanel p = new JPanel(new BorderLayout(0, 4));
        p.setOpaque(false);
        p.add(Etiqueta.titulo(etiqueta, 10f, Etiqueta.Rol.SUAVE), BorderLayout.NORTH);
        p.add(campo, BorderLayout.CENTER);
        return p;
    }

    // Agrega un bloque al formulario en la fila y columna indicadas. El parámetro columnas dice cuántas columnas ocupa.
    // Resuelve que cada campo se coloque con una sola línea en vez de repetir todas las reglas de la cuadrícula.
    private static void agregar(JPanel formulario, JComponent bloque, int fila, int columna, int columnas) {
        GridBagConstraints c = new GridBagConstraints();
        // gridx es la columna, gridy la fila y gridwidth cuántas columnas ocupa.
        c.gridx = columna;
        c.gridy = fila;
        c.gridwidth = columnas;
        // weightx reparte el ancho sobrante. Un bloque de dos columnas pesa el doble que uno de una.
        c.weightx = columnas;
        // El bloque se estira a lo ancho para llenar su espacio.
        c.fill = GridBagConstraints.HORIZONTAL;
        // Separación entre dos campos lado a lado. El de la izquierda deja 7 a su derecha y el de la derecha 7 a su izquierda.
        // El && significa y, y el signo de pregunta es un if corto.
        int izq = (columnas == 1 && columna == 1) ? 7 : 0;
        int der = (columnas == 1 && columna == 0) ? 7 : 0;
        // Insets son los márgenes: arriba 0, izquierda izq, abajo 10 y derecha der.
        c.insets = new java.awt.Insets(0, izq, 10, der);
        formulario.add(bloque, c);
    }

    // Alinea una pieza a la izquierda. En un BoxLayout vertical cada pieza se alinea por su cuenta,
    // y este método evita repetir la misma línea para cada una.
    private static JComponent alinear(JComponent c) {
        c.setAlignmentX(Component.LEFT_ALIGNMENT);
        return c;
    }

    // Pone un botón de ojo al lado del campo de contraseña, para mostrarla u ocultarla.
    private static JPanel conOjo(CampoClavePixel campo) {
        // Guarda el carácter con el que el campo oculta la contraseña, o sea los puntitos, para poder volver a ponerlo.
        final char puntitos = campo.getEchoChar();
        // Un botón con su propio dibujo. Es una clase anónima: se crea y se usa en el mismo lugar.
        JButton ojo = new JButton() {
            // True si la contraseña se está viendo.
            private boolean ver = false;
            // Este bloque con llaves sueltas se ejecuta una vez al crear el botón. Hace de constructor para una clase anónima.
            {
                // Tamaño y apariencia: sin fondo, sin borde, sin marco de foco, con la manito como puntero.
                setPreferredSize(new Dimension(36, 10));
                setContentAreaFilled(false);
                setBorderPainted(false);
                setFocusPainted(false);
                setCursor(Cursor.getPredefinedCursor(Cursor.HAND_CURSOR));
                // Al hacer clic, suena, invierte ver y cambia lo que muestra el campo.
                // El carácter (char) 0 hace que el campo muestre la contraseña. Con puntitos la oculta.
                addActionListener(e -> {
                    Sonido.efecto("click");
                    ver = !ver;
                    campo.setEchoChar(ver ? (char) 0 : puntitos);
                    repaint();
                });
            }

            // Dibuja el ojo a mano. Swing lo llama solo.
            @Override
            protected void paintComponent(Graphics g) {
                // Copia el pincel para no afectar el original, y la convierte a Graphics2D.
                Graphics2D g2 = (Graphics2D) g.create();
                // El centro del botón.
                int cx = getWidth() / 2;
                int cy = getHeight() / 2;
                // Dibuja la imagen del ojo, de 24 píxeles, centrada.
                g2.drawImage(Sprites.ui("eye", 24), cx - 12, cy - 12, null);
                // Si la contraseña está oculta, tacha el ojo con una raya diagonal de 3 píxeles de grosor.
                if (!ver) {
                    g2.setColor(Tema.texto());
                    g2.setStroke(new BasicStroke(3f));
                    g2.drawLine(cx - 12, cy + 12, cx + 12, cy - 12);
                }
                // Libera el pincel copiado.
                g2.dispose();
            }
        };
        // Campo y ojo lado a lado: el campo ocupa casi todo el espacio y el ojo va a la derecha.
        JPanel p = new JPanel(new BorderLayout(4, 0));
        p.setOpaque(false);
        p.add(campo, BorderLayout.CENTER);
        p.add(ojo, BorderLayout.EAST);
        return p;
    }
}