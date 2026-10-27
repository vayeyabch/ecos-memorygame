package ecos;

import ecos.app.Pantalla;
import ecos.app.Tema;
import ecos.app.Ventana;
import ecos.recursos.Sonido;
import java.awt.GridBagLayout;
import javax.swing.JButton;
import javax.swing.SwingUtilities;

/** Prueba de la base: se abre una ventana y se queda abierta. Se puede borrar despues. */
public class PruebaBase {

    static class Prueba extends Pantalla {
        Prueba(String id, String musica, String texto, String destino) {
            super(id, musica);
            setLayout(new GridBagLayout());
            JButton ir = new JButton(texto);
            ir.addActionListener(e -> irA(destino));
            add(ir);
            JButton tema = new JButton("Cambiar tema");
            tema.addActionListener(e -> Tema.alternar());
            add(tema);
            JButton musicaBtn = new JButton("Musica on/off");
            musicaBtn.addActionListener(e -> Sonido.alternar());
            add(musicaBtn);
            JButton efecto = new JButton("Efecto moneda");
            efecto.addActionListener(e -> Sonido.efecto("moneda"));
            add(efecto);
        }
    }

    public static void main(String[] args) {
        Ventana ventana = new Ventana();
        ventana.getNavegador().registrar(new Prueba("a", "menu", "Ir a pantalla B (clasico)", "b"));
        ventana.getNavegador().registrar(new Prueba("b", "clasico", "Ir a pantalla A (menu)", "a"));
        SwingUtilities.invokeLater(() -> ventana.iniciar("a"));
    }
}