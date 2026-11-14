package ecos.app.modos.JuegoC;

import ecos.componentes.BotonMenu;
import ecos.componentes.CampoClavePixel;
import ecos.componentes.CampoTextoPixel;
import ecos.componentes.CapaModal;
import ecos.componentes.Etiqueta;
import ecos.componentes.PanelPixel;
import ecos.pantallas.PantallaAcceso;
import ecos.recursos.Sonido;
import java.awt.Component;
import java.awt.Dimension;
import javax.swing.Box;
import javax.swing.BoxLayout;
import javax.swing.JComponent;
import javax.swing.JPanel;

// Ventana donde el jugador 2 inicia sesión antes de jugar, para sumar monedas, entrar al ranking y comprar el Ojo espía.
// Solo dibuja y avisa los clics; la revisión la hace ModeloLoginJugador2 por medio de ControladorLoginJugador2.
public class VentanaLoginJugador2 {

    // Campos de usuario y contraseña, y etiqueta de errores.
    private final CampoTextoPixel campoUsuario = new CampoTextoPixel("Tu usuario", 16);
    private final CampoClavePixel campoClave = new CampoClavePixel("Tu contraseña", 16);
    private final Etiqueta mensaje = Etiqueta.texto(" ", 20f, Etiqueta.Rol.PELIGRO);
    private final BotonMenu entrar = new BotonMenu("Entrar", BotonMenu.Estilo.ROSA);
    private final BotonMenu cancelar = new BotonMenu("Cancelar", BotonMenu.Estilo.SECUNDARIO);
    private final CapaModal capa;

    public VentanaLoginJugador2() {
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
        JComponent claveConOjo = PantallaAcceso.conOjo(campoClave);
        claveConOjo.setMaximumSize(new Dimension(campoUsuario.getPreferredSize().width + 40, claveConOjo.getPreferredSize().height));
        panel.add(centrar(claveConOjo));
        panel.add(Box.createVerticalStrut(8));
        panel.add(centrar(mensaje));
        panel.add(Box.createVerticalStrut(10));
        JPanel botones = new JPanel(new java.awt.FlowLayout(java.awt.FlowLayout.CENTER, 10, 0));
        botones.setOpaque(false);
        botones.add(entrar);
        botones.add(cancelar);
        panel.add(centrar(botones));
        panel.setPreferredSize(new Dimension(520, panel.getPreferredSize().height));
        panel.setMaximumSize(panel.getPreferredSize());
        campoUsuario.setLimite(20);
        capa = new CapaModal(panel);
    }

    public void setAlEntrar(Runnable accion) {
        entrar.addActionListener(e -> { Sonido.efecto("click"); accion.run(); });
        campoClave.addActionListener(e -> accion.run());
    }

    public void setAlCancelar(Runnable accion) {
        cancelar.addActionListener(e -> { Sonido.efecto("click"); accion.run(); });
    }

    public String getUsuario() { return campoUsuario.getText().trim(); }

    public String getClave() { return new String(campoClave.getPassword()); }

    // Muestra un error en rojo y limpia la contraseña.
    public void mostrarError(String texto) {
        Sonido.efecto("error");
        mensaje.setText(texto);
        campoClave.setText("");
    }

    public void mostrar(Component origen) { capa.mostrar(origen); campoUsuario.requestFocusInWindow(); }

    public void cerrar() { capa.cerrar(); }

    private static JComponent centrar(JComponent c) { c.setAlignmentX(Component.CENTER_ALIGNMENT); return c; }
}
