package ecos.componentes;

import java.awt.Component;
import java.awt.event.ComponentAdapter;
import java.awt.event.ComponentEvent;
import javax.swing.JComponent;
import javax.swing.JLayeredPane;
import javax.swing.SwingUtilities;

public class Capas {

    // Pone un componente cubriendo toda la ventana, en una capa por encima de las pantallas.
    public static void agregar(Component origen, JComponent capa, Integer nivel) {
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

    // Saca el componente de la ventana.
    public static void quitar(JComponent capa) {
        java.awt.Container padre = capa.getParent();
        if (padre == null) {
            return;
        }
        Object ajuste = capa.getClientProperty("ajuste");
        if (ajuste instanceof ComponentAdapter a) {
            padre.removeComponentListener(a);
        }
        padre.remove(capa);
        padre.repaint();
    }
}
