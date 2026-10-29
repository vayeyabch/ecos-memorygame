package ecos.pantallas;

import ecos.app.Pantalla;
<<<<<<< HEAD
<<<<<<< HEAD
import ecos.app.modos.JuegoC.BotonPixel;
import ecos.app.modos.JuegoC.Paleta;
import ecos.componentes.TarjetaModo;
=======
import ecos.componentes.BotonMenu;
>>>>>>> origin/main
import ecos.recursos.Sonido;
import ecos.recursos.Sprites;
=======
import ecos.app.Sesion;
import ecos.componentes.BotonMenu;
import ecos.componentes.Etiqueta;
import ecos.componentes.ImagenPixel;
import ecos.componentes.PanelPixel;
import ecos.componentes.TarjetaModo;
import ecos.recursos.Fuentes;
import ecos.recursos.Sonido;
import ecos.recursos.Sprites;
import java.awt.Component;
>>>>>>> origin/main
import java.awt.FlowLayout;
import java.awt.GridBagLayout;
import javax.swing.BorderFactory;
import javax.swing.Box;
import javax.swing.BoxLayout;
import javax.swing.JPanel;

public class PantallaMenu extends Pantalla {

    private final Etiqueta saludo = Etiqueta.titulo("Hola, Jugador 1!", 14f, Etiqueta.Rol.TEXTO); // "Hola, nombre!"
    private final BotonMenu btn4 = new BotonMenu("4x4", BotonMenu.Estilo.PESTANA); // opción de tablero 4x4
    private final BotonMenu btn6 = new BotonMenu("6x6", BotonMenu.Estilo.PRINCIPAL); // opción de tablero 6x6

    public PantallaMenu() {
        super("menu", "menu");
        setLayout(new GridBagLayout());
        setBorder(BorderFactory.createEmptyBorder(MARGEN_SUPERIOR, 16, 24, 16));

        // logo
        ImagenPixel logo = new ImagenPixel(Sprites.logo("logo_ecos"));
        logo.setAncho(300);

        // caja con el saludo
        PanelPixel caja = new PanelPixel(new FlowLayout(FlowLayout.CENTER, 0, 0)) {
            @Override
            public java.awt.Dimension getMaximumSize() {
                return getPreferredSize();
            }
        };
        caja.setPadding(8);
        caja.add(saludo);

        // tarjeta de Clásico con el selector de tablero
        TarjetaModo clasico = new TarjetaModo("Clásico", "2 jugadores · parejas", Sprites.mascota(1, 80), () -> irA("reglas_clasico"));
        clasico.agregarExtra(crearSelector());
        TarjetaModo patrones = new TarjetaModo("Patrones", "1 jugador · memoriza", Sprites.mascota(2, 80), () -> irA("reglas_patrones"));
        TarjetaModo premios = new TarjetaModo("Premios", "Top 5", Sprites.ui("trophy", 72), () -> irA("premios"));

        JPanel tarjetas = new JPanel(new java.awt.GridLayout(1, 3, 18, 0)); // las 3 tarjetas con la misma altura
        tarjetas.setOpaque(false);
        tarjetas.add(clasico);
        tarjetas.add(patrones);
        tarjetas.add(premios);

<<<<<<< HEAD
        // ----- Boton de premios -----
        BotonPixel premios = new BotonPixel("Premios", Paleta.DORADO, Paleta.TEXTO_NARANJA, 16f);
        premios.addActionListener(e -> {
            Sonido.efecto("click");
            irA("premios");
        });
=======
        // botón Salir: vuelve a la pantalla de acceso
        BotonMenu salir = new BotonMenu("Salir", BotonMenu.Estilo.SECUNDARIO);
        salir.addActionListener(e -> { Sonido.efecto("click"); irA("acceso"); });
>>>>>>> origin/main

        JPanel columna = new JPanel();
        columna.setOpaque(false);
        columna.setLayout(new BoxLayout(columna, BoxLayout.Y_AXIS));
        for (Component c : new Component[]{logo, caja, tarjetas, salir}) {
            ((javax.swing.JComponent) c).setAlignmentX(Component.CENTER_ALIGNMENT);
            columna.add(c);
            columna.add(Box.createVerticalStrut(14));
        }
        add(columna);
    }

    // Selector "Tablero [4x4] [6x6]" dentro de la tarjeta de Clásico.
    private JPanel crearSelector() {
        JPanel fila = new JPanel(new FlowLayout(FlowLayout.CENTER, 8, 0));
        fila.setOpaque(false);
        btn4.setPequeno(true);
        btn6.setPequeno(true);
        btn4.addActionListener(e -> elegirTamano(4));
        btn6.addActionListener(e -> elegirTamano(6));
        fila.add(Etiqueta.titulo("Tablero", 10f, Etiqueta.Rol.SUAVE));
        fila.add(btn4);
        fila.add(btn6);
        return fila;
    }

    private void elegirTamano(int n) {
        Sonido.efecto("click");
        Sesion.setTamano(n);
        marcarTamano();
    }

    private void marcarTamano() {
        btn4.setEstilo(Sesion.getTamano() == 4 ? BotonMenu.Estilo.PRINCIPAL : BotonMenu.Estilo.PESTANA);
        btn6.setEstilo(Sesion.getTamano() == 6 ? BotonMenu.Estilo.PRINCIPAL : BotonMenu.Estilo.PESTANA);
    }

    @Override
    public void alEntrar() {
        super.alEntrar();
        saludo.setText("Hola, " + Sesion.getNombre() + "!");
        marcarTamano();
    }
}
