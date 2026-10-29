package ecos.componentes;

import java.awt.Component; // Tipo general de cualquier componente visual.
import java.awt.event.ComponentAdapter; // Clase para escuchar cambios de tamaño sin implementar todos los métodos.
import java.awt.event.ComponentEvent; // Datos de un evento de un componente.
import javax.swing.JComponent; // Componente básico de Swing.
import javax.swing.JLayeredPane; // Panel con capas: permite poner cosas encima de otras.
import javax.swing.SwingUtilities; // Herramientas de ayuda de Swing.

public class Capas {

    // Pone un componente cubriendo toda la ventana, en una capa por encima de las pantallas.
    public static void agregar(Component origen, JComponent capa, Integer nivel) { // Recibe cualquier componente de la ventana, lo que se quiere poner y la capa.
        JLayeredPane panel = SwingUtilities.getRootPane(origen).getLayeredPane(); // Busca el panel de capas de la ventana donde está "origen".
        capa.setBounds(0, 0, panel.getWidth(), panel.getHeight()); // Hace que el componente cubra toda la ventana.
        ComponentAdapter ajuste = new ComponentAdapter() { // Crea un escucha para cuando cambie el tamaño de la ventana.
            @Override // Indica que se reemplaza un método de ComponentAdapter.
            public void componentResized(ComponentEvent e) { // Se ejecuta cada vez que cambia el tamaño.
                capa.setBounds(0, 0, panel.getWidth(), panel.getHeight()); // Vuelve a cubrir toda la ventana con el tamaño nuevo.
            }
        };
        panel.addComponentListener(ajuste); // Empieza a escuchar los cambios de tamaño.
        capa.putClientProperty("ajuste", ajuste); // Guarda el escucha dentro del componente para poder quitarlo después.
        panel.add(capa, nivel); // Agrega el componente en la capa pedida.
        panel.repaint(); // Repinta para que se vea.
    }

    // Saca el componente de la ventana.
    public static void quitar(JComponent capa) { // Recibe el componente que se quiere quitar.
        java.awt.Container padre = capa.getParent(); // Pide el contenedor donde está puesto.
        if (padre == null) { // Si no está en ninguna parte.
            return; // No hay nada que quitar.
        }
        Object ajuste = capa.getClientProperty("ajuste"); // Recupera el escucha de tamaño que se guardó.
        if (ajuste instanceof ComponentAdapter a) { // Si de verdad es ese escucha.
            padre.removeComponentListener(a); // Deja de escuchar los cambios de tamaño.
        }
        padre.remove(capa); // Saca el componente del contenedor.
        padre.repaint(); // Repinta para que desaparezca.
    }
}