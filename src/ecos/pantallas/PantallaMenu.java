package ecos.pantallas;

import ecos.app.Pantalla;
import ecos.componentes.BotonMenu;
import ecos.componentes.Etiqueta;
import ecos.componentes.ImagenPixel;
import ecos.componentes.PanelPixel;
import ecos.componentes.TarjetaModo;
import ecos.recursos.Sonido;
import ecos.recursos.Sprites;
import java.awt.Component;
import java.awt.FlowLayout;
import java.awt.GridBagLayout;
import javax.swing.BorderFactory;
import javax.swing.Box;
import javax.swing.BoxLayout;
import java.util.function.Consumer;
import java.util.function.IntConsumer;
import javax.swing.JPanel;

// Menú principal: logo, saludo, las tres tarjetas de modo (clásico, patrones y premios) y el botón Salir.
public class PantallaMenu extends Pantalla {

    // Saludo "Hola, nombre!" y opciones de tablero 4x4 y 6x6.
    private final Etiqueta saludo = Etiqueta.titulo("Hola, Jugador 1!", 14f, Etiqueta.Rol.TEXTO);
    private final BotonMenu btn4 = new BotonMenu("4x4", BotonMenu.Estilo.PESTANA);
    private final BotonMenu btn6 = new BotonMenu("6x6", BotonMenu.Estilo.PRINCIPAL);

    // Acciones de las tarjetas, del tamaño de tablero y de Salir, que pone el controlador.
    private Consumer<String> alElegirModo = id -> { };
    private IntConsumer alElegirTamano = n -> { };
    private Runnable alSalirAlAcceso = () -> { };

    public PantallaMenu() {
        super("menu", "menu");
        setLayout(new GridBagLayout());
        setBorder(BorderFactory.createEmptyBorder(MARGEN_SUPERIOR, 16, 24, 16));

        ImagenPixel logo = new ImagenPixel(Sprites.logo("logo_ecos"));
        logo.setAncho(300);

        PanelPixel caja = new PanelPixel(new FlowLayout(FlowLayout.CENTER, 0, 0)) {
            @Override
            public java.awt.Dimension getMaximumSize() {
                return getPreferredSize();
            }
        };
        caja.setPadding(8);
        caja.add(saludo);

        TarjetaModo clasico = new TarjetaModo("Clásico", "Local o en red · parejas", Sprites.mascota(1, 80), () -> alElegirModo.accept("modo_clasico"));
        clasico.agregarExtra(crearSelector());
        TarjetaModo patrones = new TarjetaModo("Patrones", "1 jugador · memoriza", Sprites.mascota(2, 80), () -> alElegirModo.accept("reglas_patrones"));
        TarjetaModo premios = new TarjetaModo("Premios", "Top 5", Sprites.ui("trophy", 72), () -> alElegirModo.accept("premios"));

        // Las tres tarjetas en una fila, con la misma altura.
        JPanel tarjetas = new JPanel(new java.awt.GridLayout(1, 3, 18, 0));
        tarjetas.setOpaque(false);
        tarjetas.add(clasico);
        tarjetas.add(patrones);
        tarjetas.add(premios);

        BotonMenu salir = new BotonMenu("Salir", BotonMenu.Estilo.SECUNDARIO);
        salir.addActionListener(e -> { Sonido.efecto("click"); alSalirAlAcceso.run(); });

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

    private JPanel crearSelector() {
        JPanel fila = new JPanel(new FlowLayout(FlowLayout.CENTER, 8, 0));
        fila.setOpaque(false);
        // Botones 4x4 y 6x6; cada uno avisa al controlador qué tamaño se eligió.
        btn4.setPequeno(true);
        btn6.setPequeno(true);
        btn4.addActionListener(e -> { Sonido.efecto("click"); alElegirTamano.accept(4); });
        btn6.addActionListener(e -> { Sonido.efecto("click"); alElegirTamano.accept(6); });
        fila.add(Etiqueta.titulo("Tablero", 10f, Etiqueta.Rol.SUAVE));
        fila.add(btn4);
        fila.add(btn6);
        return fila;
    }

    public void setAlElegirModo(Consumer<String> accion) { alElegirModo = accion; }

    public void setAlElegirTamano(IntConsumer accion) { alElegirTamano = accion; }

    public void setAlSalirAlAcceso(Runnable accion) { alSalirAlAcceso = accion; }

    public void setSaludo(String texto) { saludo.setText(texto); }

    public void marcarTamano(int tamano) {
        btn4.setEstilo(tamano == 4 ? BotonMenu.Estilo.PRINCIPAL : BotonMenu.Estilo.PESTANA);
        btn6.setEstilo(tamano == 6 ? BotonMenu.Estilo.PRINCIPAL : BotonMenu.Estilo.PESTANA);
    }
}
