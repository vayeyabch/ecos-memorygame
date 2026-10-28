package ecos.pantallas;

import ecos.app.Pantalla;
import ecos.app.modos.JuegoC.BotonPixel;
import ecos.app.modos.JuegoC.Paleta;
import ecos.componentes.TarjetaModo;
import ecos.recursos.Sonido;
import ecos.recursos.Sprites;
import java.awt.FlowLayout;
import java.awt.GridBagConstraints;
import java.awt.GridBagLayout;
import java.awt.Image;
import java.awt.Insets;
import javax.swing.ImageIcon;
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
        BotonPixel premios = new BotonPixel("Premios", Paleta.DORADO, Paleta.TEXTO_NARANJA, 16f);
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
}
