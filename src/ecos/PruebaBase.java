package ecos;

import ecos.app.Pantalla;
import ecos.app.Tema;
import ecos.app.Ventana;
import ecos.componentes.BarraSuperior;
import ecos.componentes.CampoTextoPixel;
import ecos.componentes.EncabezadoJugador;
import ecos.componentes.PanelPixel;
import ecos.recursos.Sonido;
import java.awt.GridBagLayout;
import javax.swing.JButton;
import javax.swing.SwingUtilities;

/** Prueba de la base y los componentes: se abre una ventana y se queda abierta. Se puede borrar despues. */
public class PruebaBase {

    static class Prueba extends Pantalla {
        Prueba(String id, String musica, String texto, String destino) {
            super(id, musica);
            setLayout(new GridBagLayout());
            PanelPixel panel = new PanelPixel();
            EncabezadoJugador jugador = new EncabezadoJugador("Jugador 1", 3);
            jugador.setActivo(true);
            panel.add(jugador);
            panel.add(new CampoTextoPixel("Escribe tu nombre", 12));
            JButton ir = new JButton(texto);
            ir.addActionListener(e -> irA(destino));
            panel.add(ir);
            JButton efecto = new JButton("Efecto moneda");
            efecto.addActionListener(e -> Sonido.efecto("moneda"));
            panel.add(efecto);
            add(panel);
        }
    }

    public static void main(String[] args) {
        Ventana ventana = new Ventana();
        ventana.setBarraSuperior(new BarraSuperior(ventana.getNavegador()));
        ventana.getNavegador().registrar(new Prueba("a", "menu", "Ir a pantalla B (clasico)", "b"));
        ventana.getNavegador().registrar(new Prueba("b", "clasico", "Ir a pantalla A (menu)", "a"));
        SwingUtilities.invokeLater(() -> ventana.iniciar("a"));
    }
}
