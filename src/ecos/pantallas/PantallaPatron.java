package ecos.pantallas;

// Clases del propio proyecto que se usan aquí.
import ecos.app.Pantalla;
import ecos.app.modos.Patrones.TableroPatron;
import ecos.app.modos.Patrones.AvisoDeslizante;
import ecos.app.modos.Patrones.BarraSegmentos;
import ecos.app.modos.JuegoC.BarraTiempo;
import ecos.componentes.BotonMenu;
import ecos.componentes.ChipPixel;
import ecos.componentes.Confeti;
import ecos.componentes.DialogoResultado;
import ecos.componentes.PanelPixel;
import ecos.recursos.Sprites;
// Librería gráfica AWT, Component para cualquier elemento de la pantalla, Dimension para los tamaños, GridBagConstraints
// para las reglas de la cuadrícula flexible, GridBagLayout para acomodar en una cuadrícula flexible y Insets para los
// márgenes.
import java.awt.Component;
import java.awt.Dimension;
import java.awt.GridBagConstraints;
import java.awt.GridBagLayout;
import java.awt.Insets;
// Acciones de Java, IntConsumer para una acción que recibe un número entero.
import java.util.function.IntConsumer;
// Librería Swing, BorderFactory para los bordes, Box para los espacios entre componentes, BoxLayout para apilar en fila
// o columna, JComponent para la base de los componentes y JPanel para los paneles.
import javax.swing.BorderFactory;
import javax.swing.Box;
import javax.swing.BoxLayout;
import javax.swing.JComponent;
import javax.swing.JPanel;

// Vista del modo patrones. No guarda el estado del juego, muestra lo que el controlador pide.
// Pantalla del modo patrones.
public class PantallaPatron extends Pantalla {

    // Los tres chips de arriba (nivel y casillas, mensaje para el jugador y tiempo total) y las dos barras de tiempo.
    private final ChipPixel chipNivel = new ChipPixel("Nivel 1 · 3 casillas", 8f, true, ChipPixel.Modo.CAJA, 3);
    private final ChipPixel chipInfo = new ChipPixel("Pulsá empezar", 8f, true, ChipPixel.Modo.CAJA, 3);
    private final ChipPixel chipTiempo = new ChipPixel("12 s", 8f, true, ChipPixel.Modo.CAJA, 3);
    private final BarraTiempo barraTotal = new BarraTiempo(false);
    // Barra del tiempo entre casillas.
    private final BarraTiempo barraEntre = new BarraSegmentos();
    // El tablero de 4x4, el botón de empezar y qué hacer cuando tocan una casilla, que lo define el controlador.
    private TableroPatron tablero;
    private final BotonMenu empezar = new BotonMenu("Empezar", true);
    private IntConsumer alClicCasilla = i -> { };

    // Arma la pantalla, primero el HUD con chips y barras, y abajo el tablero y el botón.
    // Arma la pantalla.
    public PantallaPatron() {
        // Le pone id y música a la pantalla. Usa GridBagLayout. Deja el margen de la pantalla.
        super("patrones", "patrones");
        setLayout(new GridBagLayout());
        setBorder(BorderFactory.createEmptyBorder(MARGEN_SUPERIOR - 6, 16, 12, 16));

        // HUD con los chips y las dos barras
        // La caja de arriba con un ancho fijo de 560 px.
        // Caja de arriba.
        PanelPixel hud = new PanelPixel() {
            // Tamaño que necesita la caja.
            @Override
            public Dimension getPreferredSize() {
                // Ancho fijo de 560 px.
                return new Dimension(560, super.getPreferredSize().height);
            }

            // Tamaño máximo de la caja.
            @Override
            public Dimension getMaximumSize() {
                // Es el mismo que el preferido.
                return getPreferredSize();
            }
        };
        // Pone sus elementos uno debajo del otro. Deja un margen adentro.
        hud.setLayout(new BoxLayout(hud, BoxLayout.Y_AXIS));
        hud.setPadding(6);
        // Los tres chips van en una fila sin fondo propio, separados por espacio flexible, uno al lado del otro.
        JPanel chips = new JPanel();
        chips.setOpaque(false);
        chips.setLayout(new BoxLayout(chips, BoxLayout.X_AXIS));
        chips.add(chipNivel);
        // Deja un espacio flexible. Agrega el chip del mensaje. Agrega el chip del tiempo.
        chips.add(Box.createHorizontalGlue());
        chips.add(chipInfo);
        chips.add(Box.createHorizontalGlue());
        chips.add(chipTiempo);
        // Se agregan los chips y las dos barras centrados, con un espacio entre cada uno.
        // Piezas de la caja de arriba. Recorre las piezas. Las agrega a la caja.
        JComponent[] piezas = {chips, barraTotal, barraEntre};
        for (JComponent c : piezas) {
            c.setAlignmentX(Component.CENTER_ALIGNMENT);
            hud.add(c);
            // Deja un espacio entre ellas.
            hud.add(Box.createVerticalStrut(10));
        }

        // Se calcula el tamaño de las casillas según el ancho de la ventana y se crea el tablero.
        // matriz de 4x4
        // Calcula el tamaño de las casillas. Crea el tablero y avisa al controlador de cada clic.
        int ts = Math.max(56, Math.min(92, (int) ((Math.max(800, getWidth()) - 120) / 4.4)));
        tablero = new TableroPatron(ts + 10, casilla -> alClicCasilla.accept(casilla));

        // Se acomodan el HUD, el tablero y el botón en columna, de arriba hacia abajo, empezando en la primera columna y
        // pegados arriba.
        GridBagConstraints c = new GridBagConstraints();
        c.gridx = 0;
        c.anchor = GridBagConstraints.NORTH;
        c.gridy = 0;
        // Agrega la caja de arriba. Deja un espacio arriba del tablero. Agrega el tablero.
        add(hud, c);
        c.gridy = 1;
        c.insets = new Insets(14, 0, 0, 0);
        add(tablero, c);
        // Usa el espacio sobrante de abajo. Agrega el botón de empezar.
        c.gridy = 2;
        c.weighty = 1;
        add(empezar, c);
    }

    // Lo que pide el controlador.

    // Al entrar a la pantalla se ajusta el tamaño de las casillas al alto y ancho que haya disponible.
    // Ajusta el tamaño de las casillas al espacio disponible.
    // Se ejecuta al entrar a la pantalla.
    @Override
    public void alEntrar() {
        // Hace lo normal de la clase base. Espera a que la ventana tenga su tamaño. Tamaño de casilla que cabe según el alto.
        // Tamaño de casilla que cabe según el ancho.
        super.alEntrar();
        javax.swing.SwingUtilities.invokeLater(() -> {
            int porAlto = (getHeight() - 390) / 4 - 10;
            int porAncho = (int) ((getWidth() - 120) / 4.4);
            // Usa el tamaño más chico, con un mínimo y un máximo, cambia el tamaño de las casillas y vuelve a calcular el
            // acomodo.
            int ts = Math.max(50, Math.min(92, Math.min(porAlto, porAncho)));
            tablero.setLado(ts + 10);
            revalidate();
        });
    }

    // Estos métodos son los que usa el controlador para cambiar lo que se ve, se conectan los clics y se actualizan
    // chips, barras y botón.
    // Guarda qué hacer al tocar una casilla.
    public void setAlClicCasilla(IntConsumer accion) {
        // Guarda la acción.
        this.alClicCasilla = accion;
    }

    // Guarda qué hacer al tocar Empezar.
    public void setAlEmpezar(Runnable accion) {
        // Conecta el botón con la acción.
        empezar.addActionListener(e -> accion.run());
    }

    // Cambia el chip del nivel.
    public void setNivel(int nivel, int casillas) {
        // Escribe el nivel y las casillas.
        chipNivel.setTexto("Nivel " + nivel + " · " + casillas + " casillas");
    }

    // Cambia el chip del mensaje.
    public void setInfo(String texto) {
        // Escribe el texto.
        chipInfo.setTexto(texto);
    }

    // Cambia la barra y el chip del tiempo total.
    public void setTiempoTotal(double t, double max) {
        // Le pasa el tiempo a la barra. Escribe los segundos que quedan.
        barraTotal.setTiempo(t, max, false);
        chipTiempo.setTexto(String.format(java.util.Locale.US, "%.0f s", Math.ceil(t)));
    }

    // Cambia la barra del tiempo entre casillas.
    public void setTiempoEntre(double t, double max) {
        // Le pasa el tiempo a la barra.
        barraEntre.setTiempo(t, max, false);
    }

    // Habilita o deshabilita el botón de empezar.
    public void setBotonEmpezar(boolean habilitado) {
        // Cambia su estado.
        empezar.setEnabled(habilitado);
    }

    // Estos métodos controlan el tablero y los avisos, se habilitan las casillas, se encienden y se muestra el nivel
    // superado o el fin.
    // true = se pueden tocar las casillas.
    // Permite o no tocar las casillas.
    public void setJugable(boolean jugable) {
        // Se lo avisa al tablero.
        tablero.setJugable(jugable);
    }

    // Enciende o apaga una casilla.
    public void encender(int casilla, boolean encendida) {
        // Se lo avisa al tablero.
        tablero.encender(casilla, encendida);
    }

    // Cuando se supera un nivel sale confeti y un aviso con una estrella.
    // Muestra que se superó un nivel.
    public void mostrarNivelSuperado(int nivel, int casillas) {
        // Lanza el confeti. Muestra el aviso con la estrella. Actualiza el chip del nivel.
        Confeti.lanzar(this, 70, 2000);
        AvisoDeslizante.mostrar(this, "¡NIVEL " + nivel + "!", AvisoDeslizante.Estilo.NIVEL, Sprites.ui("star", 44));
        setNivel(nivel, casillas);
    }

    // Al terminar se abre la ventana con los niveles superados, el mejor nivel y los botones de reintentar y menú.
    // Muestra la ventana de fin de la partida.
    public void mostrarFin(String motivo, int niveles, int mejor, Runnable reintentar, Runnable menu) {
        // Arma las dos cajitas con los niveles y abre la ventana con el motivo y los datos, con los botones reintentar y
        // menú.
        JComponent cuerpo = DialogoResultado.filaDatos("Niveles superados", "" + niveles, "Mejor nivel", "" + mejor);
        new DialogoResultado("Fin del patrón", motivo, cuerpo, null,
                new String[]{"Reintentar", "Menú"}, new Runnable[]{reintentar, menu}).mostrar(this);
    }
}