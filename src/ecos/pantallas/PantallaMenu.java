package ecos.pantallas;


import ecos.app.Pantalla;
import ecos.componentes.BotonMenu;
import ecos.recursos.Sonido;
import ecos.recursos.Sprites;
import java.awt.Color;
import java.awt.FlowLayout;
import java.awt.GridBagConstraints;
import java.awt.GridBagLayout;
import java.awt.Image;
import java.awt.Insets;
import javax.swing.BorderFactory;
import javax.swing.ImageIcon;
import javax.swing.JButton;
import javax.swing.JLabel;
import javax.swing.JPanel;

/**
 * Menu principal: el logo, una tarjeta por modo (Clasico y Patrones) y el boton de Premios.
 * No sabe nada de las otras pantallas: solo conoce sus ids con irA(...).
 *   "clasico", "patrones"                  -> los modos de juego (otras personas)
 *   "reglas_clasico", "reglas_patrones"    -> PantallaReglasClasico / PantallaReglasPatrones
 *   "premios"                              -> PantallaPremios
 */
public class PantallaMenu extends Pantalla {

    public PantallaMenu() {
        super("menu", "menu");
        setLayout(new GridBagLayout());

        // ----- Logo -----
        Image logoOriginal = Sprites.logo("logo_ecos");
        int ancho = 420;
        int alto = ancho * logoOriginal.getHeight(null) / logoOriginal.getWidth(null);
        JLabel logo = new JLabel(new ImageIcon(Sprites.escalar(logoOriginal, ancho, alto)));

        // ----- Tarjetas de los modos -----
        TarjetaModo clasico = new TarjetaModo("Clasico",
                "Dos jugadores, un tablero cada uno. Gana quien encuentre todas las parejas con menos intentos.",
                Sprites.mascota(1));
        clasico.setAlJugar(() -> irA("clasico"));
        clasico.setAlVerReglas(() -> irA("reglas_clasico"));

        TarjetaModo patrones = new TarjetaModo("Patrones",
                "Mira la secuencia de casillas que se iluminan y repitela sin equivocarte.",
                Sprites.mascota(2));
        patrones.setAlJugar(() -> irA("patrones"));
        patrones.setAlVerReglas(() -> irA("reglas_patrones"));

        JPanel tarjetas = new JPanel(new FlowLayout(FlowLayout.CENTER, 40, 0));
        tarjetas.setOpaque(false);
        tarjetas.add(clasico);
        tarjetas.add(patrones);

        // ----- Boton de premios -----
        BotonMenu premios = new BotonMenu("Premios", false);
        premios.addActionListener(e -> {
            Sonido.efecto("click");
            irA("premios");
        });

        // ----- Todo en una columna, centrado -----
        GridBagConstraints c = new GridBagConstraints();
        c.gridx = 0;
        c.insets = new Insets(8, 8, 8, 8);
        c.gridy = 0; add(logo, c);
        c.gridy = 1; add(tarjetas, c);
        c.gridy = 2; add(premios, c);
    }

    private static class TarjetaModo extends JPanel {
        private Runnable alJugar = () -> {};
        private Runnable alVerReglas = () -> {};

        TarjetaModo(String titulo, String descripcion, Image mascota) {
            setLayout(new GridBagLayout());
            setOpaque(false);
            setBorder(BorderFactory.createCompoundBorder(
                    BorderFactory.createLineBorder(new Color(115, 145, 105), 2),
                    BorderFactory.createEmptyBorder(12, 16, 12, 16)));

            GridBagConstraints c = new GridBagConstraints();
            c.gridx = 0;
            c.insets = new Insets(5, 5, 5, 5);
            c.gridy = 0;
            add(new JLabel(titulo), c);
            c.gridy = 1;
            add(new JLabel(new ImageIcon(Sprites.escalar(mascota, 110, 110))), c);
            c.gridy = 2;
            add(new JLabel("<html><div style='text-align:center;width:260px'>"
                    + descripcion + "</div></html>"), c);

            JPanel acciones = new JPanel(new FlowLayout(FlowLayout.CENTER, 10, 0));
            acciones.setOpaque(false);
            JButton jugar = new JButton("Jugar");
            JButton reglas = new JButton("Reglas");
            jugar.addActionListener(e -> alJugar.run());
            reglas.addActionListener(e -> alVerReglas.run());
            acciones.add(jugar);
            acciones.add(reglas);
            c.gridy = 3;
            add(acciones, c);
        }

        void setAlJugar(Runnable accion) {
            alJugar = accion;
        }

        void setAlVerReglas(Runnable accion) {
            alVerReglas = accion;
        }
    }
}
