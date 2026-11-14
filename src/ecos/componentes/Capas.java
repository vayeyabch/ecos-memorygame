package ecos.componentes;

import java.awt.Component;
import java.awt.event.ComponentAdapter;
import java.awt.event.ComponentEvent;
import javax.swing.JComponent;
import javax.swing.JLayeredPane;
import javax.swing.SwingUtilities;

// Pone un componente encima de toda la ventana (diálogos, avisos y confeti) y lo quita después.
public class Capas {

    public static void agregar(Component origen, JComponent capa, Integer nivel) {
        // Busca el panel de capas de la ventana de origen y hace que el componente la cubra, también cuando cambia de
        // tamaño.
        JLayeredPane panel = SwingUtilities.getRootPane(origen).getLayeredPane();
        capa.setBounds(0, 0, panel.getWidth(), panel.getHeight());
        ComponentAdapter ajuste = new ComponentAdapter() {
            @Override
            public void componentResized(ComponentEvent e) {
                capa.setBounds(0, 0, panel.getWidth(), panel.getHeight());
            }
        };
        panel.addComponentListener(ajuste);
        capa.putClientProperty("ajuste", ajuste);
        panel.add(capa, nivel);
        panel.repaint();
    }

    public static void quitar(JComponent capa) {
        java.awt.Container padre = capa.getParent();
        if (padre == null) {
            return;
        }
        // Recupera el escucha de tamaño guardado y lo quita.
        Object ajuste = capa.getClientProperty("ajuste");
        if (ajuste instanceof ComponentAdapter a) {
            padre.removeComponentListener(a);
        }
        padre.remove(capa);
        padre.repaint();
    }
}
