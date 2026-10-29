package ecos.pantallas;

import ecos.app.Pantalla;
import ecos.componentes.BotonMenu;
import ecos.componentes.Etiqueta;
import ecos.componentes.ListaReglas;
import ecos.componentes.PanelPixel;
import ecos.recursos.Sonido;
import java.awt.Component;
import java.awt.Dimension;
import java.awt.GridBagLayout;
import javax.swing.BorderFactory;
import javax.swing.Box;
import javax.swing.BoxLayout;
import javax.swing.JComponent;

public class PantallaReglasPatrones extends Pantalla {

    public PantallaReglasPatrones() {
        super("reglas_patrones", "menu");
        setLayout(new GridBagLayout());
        setBorder(BorderFactory.createEmptyBorder(MARGEN_SUPERIOR, 16, 24, 16));

        ListaReglas reglas = new ListaReglas(460,
                "Mirá cómo se iluminan las casillas de la matriz de 4x4 y memorizá el orden.",
                "Empezás con 3 casillas. Cada nivel que superás suma 1 casilla más.",
                "Tenés 12 segundos en total para repetir el patrón y 2 segundos entre casilla y casilla.",
                "Si te equivocás o se acaba el tiempo, termina la partida.");
        BotonMenu comenzar = new BotonMenu("¡Comenzar!", true);
        comenzar.addActionListener(e -> { Sonido.efecto("click"); irA("patrones"); });

        PanelPixel tarjeta = new PanelPixel() {
            @Override
            public Dimension getPreferredSize() {
                return new Dimension(Math.max(560, super.getPreferredSize().width), super.getPreferredSize().height);
            }
        };
        tarjeta.setLayout(new BoxLayout(tarjeta, BoxLayout.Y_AXIS));
        tarjeta.setPadding(18);
        JComponent[] piezas = {Etiqueta.titulo("Modo patrones", 16f, Etiqueta.Rol.TEXTO), reglas, comenzar.anchoCompleto()};
        for (JComponent c : piezas) {
            c.setAlignmentX(Component.LEFT_ALIGNMENT);
            tarjeta.add(c);
            tarjeta.add(Box.createVerticalStrut(14));
        }
        add(tarjeta);
    }
}
