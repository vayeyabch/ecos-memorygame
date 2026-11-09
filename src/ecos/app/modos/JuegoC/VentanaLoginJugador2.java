package ecos.app.modos.JuegoC;

// Clases del propio proyecto que se usan aquí.
import ecos.componentes.BotonMenu;
import ecos.componentes.CampoClavePixel;
import ecos.componentes.CampoTextoPixel;
import ecos.componentes.CapaModal;
import ecos.componentes.Etiqueta;
import ecos.componentes.PanelPixel;
import ecos.pantallas.pantallaAcceso;
import ecos.recursos.Sonido;
// Librería gráfica AWT, Component para cualquier elemento de la pantalla y Dimension para los tamaños.
import java.awt.Component;
import java.awt.Dimension;
// Librería Swing, Box para los espacios entre componentes, BoxLayout para apilar en fila o columna, JComponent para la
// base de los componentes y JPanel para los paneles.
import javax.swing.Box;
import javax.swing.BoxLayout;
import javax.swing.JComponent;
import javax.swing.JPanel;

// Vista, ventana donde el jugador 2 inicia sesión antes de jugar (para sumar monedas, entrar al ranking y comprar el Ojo espía).
// Solo dibuja y avisa los clics; la revisión la hace ModeloLoginJugador2 por medio de ControladorLoginJugador2.
public class VentanaLoginJugador2 {

    // Campos del usuario y la contraseña del jugador 2, y la etiqueta donde salen los errores.
    private final CampoTextoPixel campoUsuario = new CampoTextoPixel("Tu usuario", 16);
    private final CampoClavePixel campoClave = new CampoClavePixel("Tu contraseña", 16);
    private final Etiqueta mensaje = Etiqueta.texto(" ", 20f, Etiqueta.Rol.PELIGRO);
    private final BotonMenu entrar = new BotonMenu("Entrar", BotonMenu.Estilo.ROSA);
    // La ventana en sí.
    private final BotonMenu cancelar = new BotonMenu("Cancelar", BotonMenu.Estilo.SECUNDARIO);
    private final CapaModal capa;

    public VentanaLoginJugador2() {
        // Caja con borde pixelado.
        PanelPixel panel = new PanelPixel();
        panel.setLayout(new BoxLayout(panel, BoxLayout.Y_AXIS));
        panel.setPadding(20);
        panel.add(centrar(Etiqueta.titulo("JUGADOR 2, ¡INICIÁ SESIÓN!", 14f, Etiqueta.Rol.TEXTO)));
        panel.add(Box.createVerticalStrut(8));
        panel.add(centrar(Etiqueta.texto("Así sumás monedas y entrás al ranking", 22f, Etiqueta.Rol.ACENTO)));
        panel.add(Box.createVerticalStrut(14));
        panel.add(centrar(Etiqueta.titulo("USUARIO", 10f, Etiqueta.Rol.SUAVE)));
        campoUsuario.setMaximumSize(new Dimension(campoUsuario.getPreferredSize().width + 40, campoUsuario.getPreferredSize().height));
        panel.add(centrar(campoUsuario));
        panel.add(Box.createVerticalStrut(10));
        panel.add(centrar(Etiqueta.titulo("CONTRASEÑA", 10f, Etiqueta.Rol.SUAVE)));
        // Contraseña con su ojito.
        JComponent claveConOjo = pantallaAcceso.conOjo(campoClave);
        claveConOjo.setMaximumSize(new Dimension(campoUsuario.getPreferredSize().width + 40, claveConOjo.getPreferredSize().height));
        panel.add(centrar(claveConOjo));
        panel.add(Box.createVerticalStrut(8));
        panel.add(centrar(mensaje));
        panel.add(Box.createVerticalStrut(10));
        // Los dos botones lado a lado.
        JPanel botones = new JPanel(new java.awt.FlowLayout(java.awt.FlowLayout.CENTER, 10, 0));
        botones.setOpaque(false);
        botones.add(entrar);
        botones.add(cancelar);
        panel.add(centrar(botones));
        panel.setPreferredSize(new Dimension(520, panel.getPreferredSize().height));
        panel.setMaximumSize(panel.getPreferredSize());
        // Máximo 20 letras.
        campoUsuario.setLimite(20);
        capa = new CapaModal(panel);
    }

    // Qué hacer al tocar "Entrar" o al dar Enter.
    public void setAlEntrar(Runnable accion) {
        entrar.addActionListener(e -> { Sonido.efecto("click"); accion.run(); });
        campoClave.addActionListener(e -> accion.run());
    }

    // Qué hacer al tocar "Cancelar".
    public void setAlCancelar(Runnable accion) {
        cancelar.addActionListener(e -> { Sonido.efecto("click"); accion.run(); });
    }

    // Usuario escrito.
    public String getUsuario() { return campoUsuario.getText().trim(); }

    // Contraseña escrita.
    public String getClave() { return new String(campoClave.getPassword()); }

    // Muestra un error en rojo y limpia la contraseña.
    public void mostrarError(String texto) {
        Sonido.efecto("error");
        mensaje.setText(texto);
        campoClave.setText("");
    }

    // Abre la ventana.
    public void mostrar(Component origen) { capa.mostrar(origen); campoUsuario.requestFocusInWindow(); }

    // Cierra la ventana.
    public void cerrar() { capa.cerrar(); }

    private static JComponent centrar(JComponent c) { c.setAlignmentX(Component.CENTER_ALIGNMENT); return c; }
}
