package ecos.pantallas;

// Clases del propio proyecto que se usan aquí.
import ecos.app.Pantalla;
import ecos.componentes.BotonMenu;
import ecos.componentes.Etiqueta;
import ecos.componentes.ImagenPixel;
import ecos.componentes.PanelPixel;
import ecos.componentes.TarjetaModo;
import ecos.recursos.Sonido;
import ecos.recursos.Sprites;
// Librería gráfica AWT, Component para cualquier elemento de la pantalla, FlowLayout para acomodar en fila y
// GridBagLayout para acomodar en una cuadrícula flexible.
import java.awt.Component;
import java.awt.FlowLayout;
import java.awt.GridBagLayout;
// Librería Swing, BorderFactory para los bordes, Box para los espacios entre componentes y BoxLayout para apilar en fila
// o columna.
import javax.swing.BorderFactory;
import javax.swing.Box;
import javax.swing.BoxLayout;
// Acciones de Java, Consumer para una acción que recibe un dato y IntConsumer para una acción que recibe un número
// entero.
import java.util.function.Consumer;
import java.util.function.IntConsumer;
// Librería Swing, JPanel para los paneles.
import javax.swing.JPanel;

// Menú principal, logo, saludo, las tres tarjetas de modo (clásico, patrones y premios) y el botón Salir.
public class PantallaMenu extends Pantalla {

    // "Hola, nombre!". opción de tablero 4x4. Opción de tablero 6x6.
    private final Etiqueta saludo = Etiqueta.titulo("Hola, Jugador 1!", 14f, Etiqueta.Rol.TEXTO);
    private final BotonMenu btn4 = new BotonMenu("4x4", BotonMenu.Estilo.PESTANA);
    private final BotonMenu btn6 = new BotonMenu("6x6", BotonMenu.Estilo.PRINCIPAL);

    // Qué hacer al tocar una tarjeta, 4x4 o 6x6, o Salir, todo lo pone el controlador.
    private Consumer<String> alElegirModo = id -> { };
    private IntConsumer alElegirTamano = n -> { };
    private Runnable alSalirAlAcceso = () -> { };

    // Arma la pantalla de arriba hacia abajo, logo, caja del saludo, tarjetas y botón Salir.
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
        TarjetaModo clasico = new TarjetaModo("Clásico", "2 jugadores · parejas", Sprites.mascota(1, 80), () -> alElegirModo.accept("reglas_clasico"));
        clasico.agregarExtra(crearSelector());
        TarjetaModo patrones = new TarjetaModo("Patrones", "1 jugador · memoriza", Sprites.mascota(2, 80), () -> alElegirModo.accept("reglas_patrones"));
        TarjetaModo premios = new TarjetaModo("Premios", "Top 5", Sprites.ui("trophy", 72), () -> alElegirModo.accept("premios"));

        // Las tres tarjetas en una fila, con la misma altura.
        // Las 3 tarjetas con la misma altura.
        JPanel tarjetas = new JPanel(new java.awt.GridLayout(1, 3, 18, 0));
        tarjetas.setOpaque(false);
        tarjetas.add(clasico);
        tarjetas.add(patrones);
        tarjetas.add(premios);

        // botón Salir, vuelve a la pantalla de acceso
        BotonMenu salir = new BotonMenu("Salir", BotonMenu.Estilo.SECUNDARIO);
        salir.addActionListener(e -> { Sonido.efecto("click"); alSalirAlAcceso.run(); });

        // Se agrega todo a una columna centrada con espacio entre cada pieza.
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
        // Botones pequeños 4x4 y 6x6; cada uno avisa al controlador qué tamaño se eligió.
        btn4.setPequeno(true);
        btn6.setPequeno(true);
        btn4.addActionListener(e -> { Sonido.efecto("click"); alElegirTamano.accept(4); });
        btn6.addActionListener(e -> { Sonido.efecto("click"); alElegirTamano.accept(6); });
        fila.add(Etiqueta.titulo("Tablero", 10f, Etiqueta.Rol.SUAVE));
        fila.add(btn4);
        fila.add(btn6);
        return fila;
    }

    // Métodos que usa el controlador para conectar los botones y cambiar el saludo y el tamaño marcado.
    // Métodos que usa el controlador (ControladorMenu).

    // Recibe el id de la pantalla a la que se va.
    public void setAlElegirModo(Consumer<String> accion) { alElegirModo = accion; }

    public void setAlElegirTamano(IntConsumer accion) { alElegirTamano = accion; }

    // Se ejecuta al tocar Salir.
    public void setAlSalirAlAcceso(Runnable accion) { alSalirAlAcceso = accion; }

    // Cambia el saludo.
    public void setSaludo(String texto) { saludo.setText(texto); }

    // Resalta el botón del tablero elegido.
    public void marcarTamano(int tamano) {
        btn4.setEstilo(tamano == 4 ? BotonMenu.Estilo.PRINCIPAL : BotonMenu.Estilo.PESTANA);
        btn6.setEstilo(tamano == 6 ? BotonMenu.Estilo.PRINCIPAL : BotonMenu.Estilo.PESTANA);
    }
}
