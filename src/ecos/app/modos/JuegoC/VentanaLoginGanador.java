package ecos.app.modos.JuegoC;

import ecos.componentes.BotonMenu; // Botones pixel.
import ecos.componentes.CampoClavePixel; // Campo de contraseña.
import ecos.componentes.CampoTextoPixel; // Campo de texto.
import ecos.componentes.CapaModal; // Cortina oscura con el panel al centro.
import ecos.componentes.Etiqueta; // Texto con estilo.
import ecos.componentes.PanelPixel; // Caja con borde pixelado.
import ecos.recursos.Sonido; // Efectos de sonido.
import java.awt.Component; // Tipo general de componente.
import java.awt.Dimension; // Ancho y alto.
import javax.swing.Box; // Espacios vacíos.
import javax.swing.BoxLayout; // Apila en vertical.
import javax.swing.JComponent; // Componente básico.
import javax.swing.JPanel; // Panel.

// Vista: ventana que le pide usuario y contraseña al jugador 2 cuando gana, para sumarle los puntos.
// Solo dibuja y avisa los clics; la revisión de la cuenta la hace ModeloLoginGanador por medio de ControladorLoginGanador.
public class VentanaLoginGanador {

    private final CampoTextoPixel campoUsuario = new CampoTextoPixel("Tu usuario", 16); // Usuario del jugador 2.
    private final CampoClavePixel campoClave = new CampoClavePixel("Tu contraseña", 16); // Contraseña del jugador 2.
    private final Etiqueta mensaje = Etiqueta.texto(" ", 20f, Etiqueta.Rol.PELIGRO); // Aquí salen los errores.
    private final BotonMenu guardar = new BotonMenu("Guardar puntos", BotonMenu.Estilo.ROSA); // Botón principal.
    private final BotonMenu omitir = new BotonMenu("Ahora no", BotonMenu.Estilo.SECUNDARIO); // Botón para seguir sin sumar.
    private final CapaModal capa; // La ventana en sí.

    public VentanaLoginGanador(String nombre, int puntos) { // Recibe el nombre del jugador 2 y cuántos puntos se van a sumar (solo para mostrarlos).
        PanelPixel panel = new PanelPixel(); // Caja con borde pixelado.
        panel.setLayout(new BoxLayout(panel, BoxLayout.Y_AXIS));
        panel.setPadding(20);
        panel.add(centrar(Etiqueta.titulo("¡GANASTE, " + nombre.toUpperCase() + "!", 16f, Etiqueta.Rol.TEXTO))); // Título.
        panel.add(Box.createVerticalStrut(8));
        JPanel frase = new JPanel(new java.awt.FlowLayout(java.awt.FlowLayout.CENTER, 6, 0)); // Frase con el número en la otra fuente (se lee mejor).
        frase.setOpaque(false);
        frase.add(Etiqueta.texto("Iniciá sesión para guardar tus", 22f, Etiqueta.Rol.ACENTO));
        frase.add(Etiqueta.titulo("" + puntos, 14f, Etiqueta.Rol.ACENTO));
        frase.add(Etiqueta.texto("puntos", 22f, Etiqueta.Rol.ACENTO));
        panel.add(centrar(frase));
        panel.add(Box.createVerticalStrut(14));
        panel.add(centrar(Etiqueta.titulo("USUARIO", 10f, Etiqueta.Rol.SUAVE)));
        campoUsuario.setMaximumSize(new Dimension(campoUsuario.getPreferredSize().width + 40, campoUsuario.getPreferredSize().height));
        panel.add(centrar(campoUsuario));
        panel.add(Box.createVerticalStrut(10));
        panel.add(centrar(Etiqueta.titulo("CONTRASEÑA", 10f, Etiqueta.Rol.SUAVE)));
                JComponent claveConOjo = ecos.pantallas.pantallaAcceso.conOjo(campoClave); // Contraseña con su ojito.
        claveConOjo.setMaximumSize(new Dimension(campoUsuario.getPreferredSize().width + 40, claveConOjo.getPreferredSize().height));
        panel.add(centrar(claveConOjo));
        panel.add(Box.createVerticalStrut(8));
        panel.add(centrar(mensaje));
        panel.add(Box.createVerticalStrut(10));
        JPanel botones = new JPanel(new java.awt.FlowLayout(java.awt.FlowLayout.CENTER, 10, 0)); // Los dos botones lado a lado.
        botones.setOpaque(false);
        botones.add(guardar);
        botones.add(omitir);
        panel.add(centrar(botones));
        panel.setPreferredSize(new Dimension(520, panel.getPreferredSize().height)); // Ancho fijo.
        panel.setMaximumSize(panel.getPreferredSize());
        campoUsuario.setLimite(20); // Máximo 20 letras.
        capa = new CapaModal(panel);
    }

    public void setAlGuardar(Runnable accion) { // Qué hacer al tocar "Guardar puntos" o al dar Enter.
        guardar.addActionListener(e -> { Sonido.efecto("click"); accion.run(); });
        campoClave.addActionListener(e -> accion.run());
    }

    public void setAlOmitir(Runnable accion) { // Qué hacer al tocar "Ahora no".
        omitir.addActionListener(e -> { Sonido.efecto("click"); accion.run(); });
    }

    public String getUsuario() { return campoUsuario.getText().trim(); } // Usuario escrito.

    public String getClave() { return new String(campoClave.getPassword()); } // Contraseña escrita.

    public void mostrarError(String texto) { // Muestra un error en rojo y limpia la contraseña.
        mensaje.setText(texto);
        campoClave.setText("");
    }

    public void mostrar(Component origen) { capa.mostrar(origen); campoUsuario.requestFocusInWindow(); } // Abre la ventana.

    public void cerrar() { capa.cerrar(); } // Cierra la ventana.

    private static JComponent centrar(JComponent c) { c.setAlignmentX(Component.CENTER_ALIGNMENT); return c; } // Centra.
}
