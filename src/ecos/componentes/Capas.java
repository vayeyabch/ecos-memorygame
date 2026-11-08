package ecos.componentes;

// Librería gráfica AWT, Component para cualquier elemento de la pantalla.
import java.awt.Component;
// Eventos del mouse y el teclado de AWT, ComponentAdapter para reaccionar a cambios de tamaño y ComponentEvent para los
// datos de esos cambios.
import java.awt.event.ComponentAdapter;
import java.awt.event.ComponentEvent;
// Librería Swing, JComponent para la base de los componentes, JLayeredPane para las capas y SwingUtilities para ejecutar
// cosas en el hilo de Swing.
import javax.swing.JComponent;
import javax.swing.JLayeredPane;
import javax.swing.SwingUtilities;

// Ayudante para poner un componente encima de toda la ventana (diálogos, avisos y confeti) y quitarlo después.
public class Capas {

    // Pone un componente cubriendo toda la ventana, en una capa por encima de las pantallas.
    // Recibe cualquier componente de la ventana, lo que se quiere poner y la capa.
    public static void agregar(Component origen, JComponent capa, Integer nivel) {
        // Busca el panel de capas de la ventana donde está "origen", hace que el componente cubra toda la ventana y crea un
        // escucha para cuando cambie su tamaño.
        JLayeredPane panel = SwingUtilities.getRootPane(origen).getLayeredPane();
        capa.setBounds(0, 0, panel.getWidth(), panel.getHeight());
        ComponentAdapter ajuste = new ComponentAdapter() {
            // Se ejecuta cada vez que cambia el tamaño.
            @Override
            public void componentResized(ComponentEvent e) {
                // Vuelve a cubrir toda la ventana con el tamaño nuevo.
                capa.setBounds(0, 0, panel.getWidth(), panel.getHeight());
            }
        };
        // Empieza a escuchar los cambios de tamaño. Guarda el escucha dentro del componente para poder quitarlo después.
        // Agrega el componente en la capa pedida. Repinta para que se vea.
        panel.addComponentListener(ajuste);
        capa.putClientProperty("ajuste", ajuste);
        panel.add(capa, nivel);
        panel.repaint();
    }

    // Saca el componente de la ventana.
    // Recibe el componente que se quiere quitar.
    public static void quitar(JComponent capa) {
        // Pide el contenedor donde está puesto. Si no está en ninguna parte. No hay nada que quitar.
        java.awt.Container padre = capa.getParent();
        if (padre == null) {
            return;
        }
        // Recupera el escucha de tamaño guardado y, si de verdad es ese escucha, deja de escuchar los cambios de tamaño.
        Object ajuste = capa.getClientProperty("ajuste");
        if (ajuste instanceof ComponentAdapter a) {
            padre.removeComponentListener(a);
        }
        // Saca el componente del contenedor. Repinta para que desaparezca.
        padre.remove(capa);
        padre.repaint();
    }
}