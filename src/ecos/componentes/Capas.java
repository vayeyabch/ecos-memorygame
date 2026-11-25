package ecos.componentes;

// Component: tipo base de cualquier elemento visual; aquí es el "origen" que sirve para encontrar la ventana.
import java.awt.Component;
// ComponentAdapter: clase lista para escuchar cambios de un componente; se sobrescribe solo lo que se necesita.
import java.awt.event.ComponentAdapter;
// ComponentEvent: datos de un evento de componente (por ejemplo, cambió de tamaño).
import java.awt.event.ComponentEvent;
// JComponent: tipo de la capa que se agrega.
import javax.swing.JComponent;
// JLayeredPane: el panel de capas de Swing, que permite apilar componentes uno encima del otro.
import javax.swing.JLayeredPane;
// SwingUtilities: ayudante; aquí se usa para encontrar la ventana a partir de un componente.
import javax.swing.SwingUtilities;

// Pone un componente encima de toda la ventana (diálogos, avisos y confeti) y lo quita después.
public class Capas {

    // Agrega "capa" encima de la ventana de "origen", en el nivel pedido (por ejemplo POPUP_LAYER o DRAG_LAYER).
    public static void agregar(Component origen, JComponent capa, Integer nivel) {
        // Busca el panel de capas de la ventana de origen y hace que el componente la cubra, también cuando cambia de
        // tamaño.
        JLayeredPane panel = SwingUtilities.getRootPane(origen).getLayeredPane();
        // La capa ocupa todo el panel (desde 0,0 con el ancho y alto del panel).
        capa.setBounds(0, 0, panel.getWidth(), panel.getHeight());
        // Clase anónima: cuando la ventana cambia de tamaño, la capa se ajusta de nuevo.
        ComponentAdapter ajuste = new ComponentAdapter() {
            @Override
            public void componentResized(ComponentEvent e) {
                capa.setBounds(0, 0, panel.getWidth(), panel.getHeight());
            }
        };
        panel.addComponentListener(ajuste);
        // Guarda el escucha dentro de la capa misma, para encontrarlo después al quitarla.
        capa.putClientProperty("ajuste", ajuste);
        panel.add(capa, nivel);
        panel.repaint();
    }

    // Quita la capa de la ventana y limpia su escucha de tamaño.
    public static void quitar(JComponent capa) {
        java.awt.Container padre = capa.getParent();
        // Si ya no está en ninguna ventana, no hay nada que quitar.
        if (padre == null) {
            return;
        }
        // Recupera el escucha de tamaño guardado y lo quita.
        Object ajuste = capa.getClientProperty("ajuste");
        // instanceof con patrón: si es un ComponentAdapter, lo guarda en la variable "a" ya convertido.
        if (ajuste instanceof ComponentAdapter a) {
            padre.removeComponentListener(a);
        }
        padre.remove(capa);
        padre.repaint();
    }
}