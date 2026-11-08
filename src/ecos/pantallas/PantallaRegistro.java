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


// Formulario de registro: nombre, apellidos, correo, usuario y contraseña. Después lleva al registro facial.
// Responsable: Valeria. Pantalla lista; falta conectarla a su controlador y al modelo.
public class PantallaRegistro extends Pantalla {
    private static final String AYUDA = "Completá todos los campos para continuar."; // Mensaje normal de la pantalla.

    private final DesfileMascotas desfile = new DesfileMascotas(); // Mascotas que desfilan arriba de la tarjeta.
    private final CampoTextoPixel campoNombre = new CampoTextoPixel("Tu nombre", 5); // Nombre (máx. 50, igual que la base de datos).
    private final CampoTextoPixel campoApellidos = new CampoTextoPixel("Tus apellidos", 5); // Apellidos (máx. 50).
    private final CampoTextoPixel campoCorreo = new CampoTextoPixel("tucorreo@ejemplo.com", 5); // Correo (máx. 100).
    private final CampoTextoPixel campoUsuario = new CampoTextoPixel("Tu usuario", 5); // Usuario (máx. 20).
    private final CampoClavePixel campoClave = new CampoClavePixel("6 o más", 5); // Contraseña.
    private final CampoClavePixel campoClave2 = new CampoClavePixel("Otra vez", 5); // Repetir la contraseña.
    private final Etiqueta mensaje = Etiqueta.texto(AYUDA, 18f, Etiqueta.Rol.SUAVE); // Aquí se muestran los avisos (por ejemplo "Ese usuario ya existe").
    private Runnable accionSiguiente = null; // Qué hace el botón; null = ir directo al registro facial.

    public PantallaRegistro() {
        super("registro", "acceso"); // Id de la pantalla y música que usa.
        setLayout(new GridBagLayout());
        setBorder(BorderFactory.createEmptyBorder(MARGEN_SUPERIOR, 16, 28, 16));

        campoNombre.setLimite(50); // Máximo 50 letras en el nombre.
        campoApellidos.setLimite(50); // Máximo 50 letras en los apellidos.
        campoCorreo.setLimite(100); // Máximo 100 letras en el correo.
        campoUsuario.setLimite(20); // Máximo 20 letras en el usuario.

        JPanel formulario = new JPanel(new GridBagLayout()); // Cuadrícula de dos columnas con los campos.
        formulario.setOpaque(false); // Transparente.
        agregar(formulario, bloque("NOMBRE", campoNombre), 0, 0, 1);
        agregar(formulario, bloque("APELLIDOS", campoApellidos), 0, 1, 1);
        agregar(formulario, bloque("CORREO ELECTRÓNICO", campoCorreo), 1, 0, 2);
        agregar(formulario, bloque("USUARIO", campoUsuario), 2, 0, 2);
        agregar(formulario, bloque("CONTRASEÑA", conOjo(campoClave)), 3, 0, 1);
        agregar(formulario, bloque("REPETIR CONTRASEÑA", conOjo(campoClave2)), 3, 1, 1);

        BotonMenu siguiente = new BotonMenu("Siguiente: mi rostro", true); // Botón que lleva al registro facial.
        siguiente.addActionListener(e -> { // Cuando se aprieta.
            Sonido.efecto("click"); // Suena el clic.
            if (accionSiguiente != null) { // Si el controlador puso su propia acción.
                accionSiguiente.run(); // La ejecuta.
            } else { // Si no, solo navega.
                irA("registro_facial"); // Va a la pantalla de captura del rostro.
            }
        });

          JPanel titulo = new JPanel(new BorderLayout());
        titulo.setOpaque(false); // Transparente.
        titulo.add(Etiqueta.texto("Crear cuenta", 32f, Etiqueta.Rol.TEXTO), BorderLayout.WEST); // Título.
        titulo.add(Etiqueta.titulo("PASO 1 DE 2", 10f, Etiqueta.Rol.SUAVE), BorderLayout.EAST); // Indicador de paso.
        titulo.setMaximumSize(new Dimension(Integer.MAX_VALUE, titulo.getPreferredSize().height)); // No se estira hacia abajo.

        PanelPixel tarjeta = new PanelPixel(); // Tarjeta del formulario.
        tarjeta.setLayout(new BoxLayout(tarjeta, BoxLayout.Y_AXIS)); // Apila todo en vertical.
        tarjeta.setPadding(22); // Margen interno (igual que en PantallaAcceso).
        tarjeta.add(alinear(titulo)); // Título.
        tarjeta.add(Box.createVerticalStrut(12)); // Espacio.
        formulario.setMaximumSize(new Dimension(Integer.MAX_VALUE, formulario.getPreferredSize().height)); // El formulario no se estira hacia abajo.
        tarjeta.add(alinear(formulario)); // Campos.
        tarjeta.add(Box.createVerticalStrut(2)); // Espacio.
        tarjeta.add(alinear(mensaje)); // Mensaje de avisos.
        tarjeta.add(Box.createVerticalStrut(10)); // Espacio.
        tarjeta.add(alinear(siguiente.anchoCompleto())); // Botón Siguiente.
        tarjeta.setMaximumSize(new Dimension(480, Integer.MAX_VALUE)); // Ancho máximo de la tarjeta (igual que en PantallaAcceso).

        JPanel columna = new JPanel(); // Columna: mascotas arriba y tarjeta abajo.
        columna.setOpaque(false); // Transparente.
        columna.setLayout(new BoxLayout(columna, BoxLayout.Y_AXIS)); // Apila en vertical.
        desfile.setAlignmentX(Component.CENTER_ALIGNMENT); // Mascotas al centro.
        tarjeta.setAlignmentX(Component.CENTER_ALIGNMENT); // Tarjeta al centro.
        columna.add(desfile); // Agrega las mascotas.
        columna.add(Box.createVerticalStrut(18)); // Espacio.
        columna.add(tarjeta); // Agrega la tarjeta.
        add(columna); // Agrega todo a la pantalla.



    }

    // ---- Para el controlador (ControladorRegistro) ----

    public String getNombre() { return campoNombre.getText().trim(); } // Entrega el nombre escrito.
    public String getApellidos() { return campoApellidos.getText().trim(); } // Entrega los apellidos escritos.
    public String getCorreo() { return campoCorreo.getText().trim(); } // Entrega el correo escrito.
    public String getUsuario() { return campoUsuario.getText().trim(); } // Entrega el usuario escrito.
    public String getClave() { return new String(campoClave.getPassword()); } // Entrega la contraseña escrita.
    public String getClave2() { return new String(campoClave2.getPassword()); } // Entrega la contraseña repetida.

    public void setAccionSiguiente(Runnable accion) { // El controlador puede cambiar lo que hace el botón (por ejemplo, validar primero).
        this.accionSiguiente = accion; // Guarda la acción.
    }

    public void irARegistroFacial() { // El controlador la llama cuando los datos están bien.
        irA("registro_facial"); // Va a la captura del rostro.
    }

    public void mostrarError(String texto) { // Muestra un aviso de error en rojo.
        mensaje.setText(texto); // Pone el texto.
        mensaje.setRol(Etiqueta.Rol.PELIGRO); // Lo pinta de rojo.
        Sonido.efecto("error"); // Suena el error.
    }

    public void limpiarMensaje() { // Vuelve al mensaje normal.
        mensaje.setText(AYUDA); // Pone el texto normal.
        mensaje.setRol(Etiqueta.Rol.SUAVE); // Lo pinta apagado.
    }



    @Override
    public void alEntrar() { // Se ejecuta cada vez que se entra a esta pantalla.
        super.alEntrar(); // Pone la música.
        campoClave.setText(""); // Borra la contraseña anterior.
        campoClave2.setText(""); // Borra la contraseña repetida anterior.
        limpiarMensaje(); // Quita avisos viejos.
        desfile.iniciar(); // Empieza el desfile de mascotas.
    }

    @Override
    public void alSalir() { // Se ejecuta cuando se sale de esta pantalla.
        desfile.detener(); // Detiene el desfile.
        super.alSalir(); // Cierra ventanas abiertas y avisa al controlador.
    }

    // --- Métodos auxiliares para armar la pantalla ---

    private static JPanel bloque(String etiqueta, JComponent campo) { // Etiqueta chica arriba y el campo abajo.
        JPanel p = new JPanel(new BorderLayout(0, 4)); // Panel con 4 px de espacio entre la etiqueta y el campo.
        p.setOpaque(false); // Transparente.
        p.add(Etiqueta.titulo(etiqueta, 10f, Etiqueta.Rol.SUAVE), BorderLayout.NORTH); // Etiqueta arriba.
        p.add(campo, BorderLayout.CENTER); // Campo abajo (se estira a lo ancho).
        return p; // Lo entrega.
    }

    private static void agregar(JPanel formulario, JComponent bloque, int fila, int columna, int columnas) { // Pone un bloque en la cuadrícula.
        GridBagConstraints c = new GridBagConstraints(); // Reglas de posición.
        c.gridx = columna; // Columna.
        c.gridy = fila; // Fila.
        c.gridwidth = columnas; // Cuántas columnas ocupa.
        c.weightx = columnas; // Las dos columnas se reparten el ancho por igual.
        c.fill = GridBagConstraints.HORIZONTAL; // Se estira a lo ancho.
        int izq = (columnas == 1 && columna == 1) ? 7 : 0; // Espacio a la izquierda en la columna derecha.
        int der = (columnas == 1 && columna == 0) ? 7 : 0; // Espacio a la derecha en la columna izquierda.
        c.insets = new java.awt.Insets(0, izq, 10, der); // 10 px de espacio abajo entre filas.
        formulario.add(bloque, c); // Lo agrega.
    }

    private static JComponent alinear(JComponent c) { // Alinea un componente a la izquierda de la tarjeta.
        c.setAlignmentX(Component.LEFT_ALIGNMENT); // Lo alinea.
        return c; // Lo entrega.
    }

    private static JPanel conOjo(CampoClavePixel campo) { // Pone un botón de ojo al lado del campo de contraseña.
    final char puntitos = campo.getEchoChar(); // Guarda el puntito normal.
    JButton ojo = new JButton() { // Botón que dibuja el icono del ojo.
        private boolean ver = false; // true = la clave se ve.
        {
            setPreferredSize(new Dimension(36, 10)); // Ancho del botón (el alto lo toma del campo).
            setContentAreaFilled(false); // Sin fondo de botón.
            setBorderPainted(false); // Sin borde.
            setFocusPainted(false); // Sin marco de foco.
            setCursor(Cursor.getPredefinedCursor(Cursor.HAND_CURSOR)); // Manito al pasar el mouse.
            addActionListener(e -> { // Al hacer clic.
                Sonido.efecto("click"); // Suena el clic.
                ver = !ver; // Cambia entre ver y esconder.
                campo.setEchoChar(ver ? (char) 0 : puntitos); // 0 = muestra el texto real.
                repaint(); // Redibuja el ojo.
            });
        }

        @Override
        protected void paintComponent(Graphics g) { // Dibuja el icono.
            Graphics2D g2 = (Graphics2D) g.create(); // Copia del pincel.
            int cx = getWidth() / 2; // Centro en x.
            int cy = getHeight() / 2; // Centro en y.
            g2.drawImage(Sprites.ui("eye", 24), cx - 12, cy - 12, null); // Icono eye.png del proyecto.
            if (!ver) { // Si la clave está escondida.
                g2.setColor(Tema.texto()); // Color de la raya.
                g2.setStroke(new BasicStroke(3f)); // Línea gruesa.
                g2.drawLine(cx - 12, cy + 12, cx + 12, cy - 12); // Tacha el ojo.
            }
            g2.dispose(); // Libera el pincel.
        }
    };
    JPanel p = new JPanel(new BorderLayout(4, 0)); // Campo y ojo lado a lado.
    p.setOpaque(false); // Transparente.
    p.add(campo, BorderLayout.CENTER); // El campo ocupa casi todo.
    p.add(ojo, BorderLayout.EAST); // El ojo a la derecha.
    return p; // Lo entrega.
}


}
