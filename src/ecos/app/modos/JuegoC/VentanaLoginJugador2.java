package ecos.app.modos.JuegoC;

// BotonMenu es el botón grande del juego, con estilos de color.
import ecos.componentes.BotonMenu;
// CampoClavePixel es el campo de contraseña con el estilo pixel del juego.
import ecos.componentes.CampoClavePixel;
// CampoTextoPixel es el campo de texto con el estilo pixel del juego.
import ecos.componentes.CampoTextoPixel;
// CapaModal es la capa que muestra un panel encima de la pantalla actual y oscurece lo de atrás.
import ecos.componentes.CapaModal;
// Etiqueta es el texto del juego, con fuente, color de tema y traducción resueltos.
import ecos.componentes.Etiqueta;
// PanelPixel es el panel con el estilo pixel del juego.
import ecos.componentes.PanelPixel;
// PantallaAcceso tiene el método conOjo, que arma el campo de contraseña con el botón de ojo. Aquí se reutiliza.
import ecos.pantallas.PantallaAcceso;
// Sonido reproduce los efectos de clic y de error.
import ecos.recursos.Sonido;
// Component trae las constantes de alineación, como CENTER_ALIGNMENT, y es el tipo del origen de la capa.
import java.awt.Component;
// Dimension es un tamaño, con ancho y alto.
import java.awt.Dimension;
// Box trae los espacios invisibles. Aquí separa las piezas hacia abajo.
import javax.swing.Box;
// BoxLayout acomoda las piezas una debajo de otra.
import javax.swing.BoxLayout;
// JComponent es el tipo base de las piezas de Swing. Los métodos reciben y devuelven este tipo.
import javax.swing.JComponent;
// JPanel es un panel, un contenedor donde se meten otras piezas.
import javax.swing.JPanel;

// Es el diálogo donde el jugador 2 inicia sesión antes de una partida clásica.
// Con cuenta, el jugador 2 suma monedas, entra al ranking y puede comprar el Ojo espía.
// Es la vista del trío MVC: solo dibuja y avisa los clics. La revisión la hace ModeloLoginJugador2
// y los conecta ControladorLoginJugador2.
public class VentanaLoginJugador2 {

    // Campo de usuario. El texto es la guía que se ve mientras está vacío, y el 16 es un ajuste propio del campo.
    private final CampoTextoPixel campoUsuario = new CampoTextoPixel("Tu usuario", 16);
    // Campo de contraseña.
    private final CampoClavePixel campoClave = new CampoClavePixel("Tu contraseña", 16);
    // Etiqueta de errores, en rojo. Empieza con un espacio en blanco, y no vacía, para que reserve su altura
    // y la ventana no cambie de tamaño cuando aparezca un error.
    private final Etiqueta mensaje = Etiqueta.texto(" ", 20f, Etiqueta.Rol.PELIGRO);
    // Los dos botones, guardados en campos para conectarlos después con los ganchos.
    private final BotonMenu entrar = new BotonMenu("Entrar", BotonMenu.Estilo.ROSA);
    private final BotonMenu cancelar = new BotonMenu("Cancelar", BotonMenu.Estilo.SECUNDARIO);
    // La capa que muestra el panel encima de la pantalla. Se crea al final del constructor, cuando el panel ya está listo.
    private final CapaModal capa;

    // Arma el panel con todas las piezas, una debajo de otra, y lo mete en la capa.
    public VentanaLoginJugador2() {
        PanelPixel panel = new PanelPixel();
        panel.setLayout(new BoxLayout(panel, BoxLayout.Y_AXIS));
        // 20 píxeles de margen interior.
        panel.setPadding(20);
        // Título y subtítulo. Cada strut es un espacio invisible entre las piezas.
        panel.add(centrar(Etiqueta.titulo("JUGADOR 2, ¡INICIÁ SESIÓN!", 14f, Etiqueta.Rol.TEXTO)));
        panel.add(Box.createVerticalStrut(8));
        panel.add(centrar(Etiqueta.texto("Así sumás monedas y entrás al ranking", 22f, Etiqueta.Rol.ACENTO)));
        panel.add(Box.createVerticalStrut(14));
        // Título y campo de usuario. El campo mide como máximo su ancho natural más 40 píxeles.
        panel.add(centrar(Etiqueta.titulo("USUARIO", 10f, Etiqueta.Rol.SUAVE)));
        campoUsuario.setMaximumSize(new Dimension(campoUsuario.getPreferredSize().width + 40, campoUsuario.getPreferredSize().height));
        panel.add(centrar(campoUsuario));
        panel.add(Box.createVerticalStrut(10));
        // Título y campo de contraseña. conOjo viene de PantallaAcceso y le agrega el botón de ojo para ver la contraseña.
        // Se le pone el mismo ancho máximo que al campo de usuario, para que queden alineados.
        panel.add(centrar(Etiqueta.titulo("CONTRASEÑA", 10f, Etiqueta.Rol.SUAVE)));
        JComponent claveConOjo = PantallaAcceso.conOjo(campoClave);
        claveConOjo.setMaximumSize(new Dimension(campoUsuario.getPreferredSize().width + 40, claveConOjo.getPreferredSize().height));
        panel.add(centrar(claveConOjo));
        panel.add(Box.createVerticalStrut(8));
        // El espacio donde aparecen los errores.
        panel.add(centrar(mensaje));
        panel.add(Box.createVerticalStrut(10));
        // Los dos botones lado a lado, centrados y separados 10 píxeles.
        // FlowLayout se escribe con el nombre completo porque no está importado.
        JPanel botones = new JPanel(new java.awt.FlowLayout(java.awt.FlowLayout.CENTER, 10, 0));
        botones.setOpaque(false);
        botones.add(entrar);
        botones.add(cancelar);
        panel.add(centrar(botones));
        // Fija el ancho del panel en 520 píxeles, con el alto que le salga. Y ese tamaño es también su máximo.
        panel.setPreferredSize(new Dimension(520, panel.getPreferredSize().height));
        panel.setMaximumSize(panel.getPreferredSize());
        // El usuario puede tener como máximo 20 caracteres, igual que en la base de datos.
        campoUsuario.setLimite(20);
        // Mete el panel en la capa. Todavía no se muestra, eso lo hace mostrar.
        capa = new CapaModal(panel);
    }

    // Es un gancho de MVC. El controlador llama a este método y le pasa lo que debe pasar al intentar entrar.
    // Se conecta al botón Entrar y también a la tecla Enter dentro del campo de contraseña.
    public void setAlEntrar(Runnable accion) {
        // La flecha es la función que se ejecuta, y e es el aviso del clic. Suena y ejecuta la acción del controlador.
        entrar.addActionListener(e -> { Sonido.efecto("click"); accion.run(); });
        // Al apretar Enter en el campo de contraseña, ejecuta la misma acción, sin sonido.
        campoClave.addActionListener(e -> accion.run());
    }

    // Es otro gancho de MVC. El controlador deja lo que debe pasar al cancelar.
    public void setAlCancelar(Runnable accion) {
        cancelar.addActionListener(e -> { Sonido.efecto("click"); accion.run(); });
    }

    // Entrega el usuario escrito. trim quita los espacios del principio y del final.
    public String getUsuario() { return campoUsuario.getText().trim(); }

    // Entrega la contraseña escrita. getPassword devuelve un arreglo de caracteres, y new String lo convierte en texto.
    public String getClave() { return new String(campoClave.getPassword()); }

    // Muestra un error en rojo con su sonido y limpia la contraseña. El texto del error lo decide el modelo.
    public void mostrarError(String texto) {
        Sonido.efecto("error");
        mensaje.setText(texto);
        campoClave.setText("");
    }

    // Abre la ventana encima de la pantalla actual y pone el cursor en el campo de usuario para empezar a escribir.
    // origen es una pieza de la pantalla desde la que se abre, y sirve para saber en qué ventana dibujar la capa.
    public void mostrar(Component origen) { capa.mostrar(origen); campoUsuario.requestFocusInWindow(); }

    // Cierra la ventana. Lo llama el controlador al cancelar o al entrar bien.
    public void cerrar() { capa.cerrar(); }

    // Alinea una pieza al centro. En un BoxLayout vertical, cada pieza se alinea por su cuenta,
    // y este método evita repetir la misma línea para cada una.
    private static JComponent centrar(JComponent c) { c.setAlignmentX(Component.CENTER_ALIGNMENT); return c; }
}