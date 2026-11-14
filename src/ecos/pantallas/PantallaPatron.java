package ecos.pantallas;

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
import java.awt.Component;
import java.awt.Dimension;
import java.awt.GridBagConstraints;
import java.awt.GridBagLayout;
import java.awt.Insets;
import java.util.function.IntConsumer;
import javax.swing.BorderFactory;
import javax.swing.Box;
import javax.swing.BoxLayout;
import javax.swing.JComponent;
import javax.swing.JPanel;

// Vista del modo patrones. No guarda el estado del juego, solo muestra lo que pide el controlador.
public class PantallaPatron extends Pantalla {

    // Los tres chips de arriba (nivel y casillas, mensaje y tiempo total) y las dos barras de tiempo.
    private final ChipPixel chipNivel = new ChipPixel("Nivel 1 · 3 casillas", 8f, true, ChipPixel.Modo.CAJA, 3);
    private final ChipPixel chipInfo = new ChipPixel("Pulsá empezar", 8f, true, ChipPixel.Modo.CAJA, 3);
    private final ChipPixel chipTiempo = new ChipPixel("12 s", 8f, true, ChipPixel.Modo.CAJA, 3);
    private final BarraTiempo barraTotal = new BarraTiempo(false);
    // Barra del tiempo entre casillas.
    private final BarraTiempo barraEntre = new BarraSegmentos();
    // Tablero de 4x4, botón de empezar y qué hacer al tocar una casilla, que define el controlador.
    private TableroPatron tablero;
    private final BotonMenu empezar = new BotonMenu("Empezar", true);
    private IntConsumer alClicCasilla = i -> { };

    public PantallaPatron() {
        super("patrones", "patrones");
        setLayout(new GridBagLayout());
        setBorder(BorderFactory.createEmptyBorder(MARGEN_SUPERIOR - 6, 16, 12, 16));

        // Los chips y las dos barras, dentro de una caja de ancho fijo (560 px).
        PanelPixel hud = new PanelPixel() {
            @Override
            public Dimension getPreferredSize() {
                return new Dimension(560, super.getPreferredSize().height);
            }

            @Override
            public Dimension getMaximumSize() {
                return getPreferredSize();
            }
        };
        hud.setLayout(new BoxLayout(hud, BoxLayout.Y_AXIS));
        hud.setPadding(6);
        // Los tres chips van en una fila, separados por espacio flexible.
        JPanel chips = new JPanel();
        chips.setOpaque(false);
        chips.setLayout(new BoxLayout(chips, BoxLayout.X_AXIS));
        chips.add(chipNivel);
        chips.add(Box.createHorizontalGlue());
        chips.add(chipInfo);
        chips.add(Box.createHorizontalGlue());
        chips.add(chipTiempo);
        JComponent[] piezas = {chips, barraTotal, barraEntre};
        for (JComponent c : piezas) {
            c.setAlignmentX(Component.CENTER_ALIGNMENT);
            hud.add(c);
            hud.add(Box.createVerticalStrut(10));
        }

        // Se calcula el tamaño de las casillas según el ancho de la ventana y se crea el tablero, que avisa al
        // controlador de cada clic.
        int ts = Math.max(56, Math.min(92, (int) ((Math.max(800, getWidth()) - 120) / 4.4)));
        tablero = new TableroPatron(ts + 10, casilla -> alClicCasilla.accept(casilla));

        // HUD, tablero y botón en columna, pegados arriba.
        GridBagConstraints c = new GridBagConstraints();
        c.gridx = 0;
        c.anchor = GridBagConstraints.NORTH;
        c.gridy = 0;
        add(hud, c);
        c.gridy = 1;
        c.insets = new Insets(14, 0, 0, 0);
        add(tablero, c);
        c.gridy = 2;
        c.weighty = 1;
        add(empezar, c);
    }

    // Al entrar se ajusta el tamaño de las casillas al espacio disponible.
    @Override
    public void alEntrar() {
        // Se toma el menor entre lo que cabe a lo alto y a lo ancho.
        super.alEntrar();
        javax.swing.SwingUtilities.invokeLater(() -> {
            int porAlto = (getHeight() - 390) / 4 - 10;
            int porAncho = (int) ((getWidth() - 120) / 4.4);
            // Usa el tamaño más chico, con un mínimo y un máximo, y vuelve a calcular el acomodo.
            int ts = Math.max(50, Math.min(92, Math.min(porAlto, porAncho)));
            tablero.setLado(ts + 10);
            revalidate();
        });
    }

    public void setAlClicCasilla(IntConsumer accion) {
        this.alClicCasilla = accion;
    }

    public void setAlEmpezar(Runnable accion) {
        empezar.addActionListener(e -> accion.run());
    }

    public void setNivel(int nivel, int casillas) {
        chipNivel.setTexto("Nivel " + nivel + " · " + casillas + " casillas");
    }

    public void setInfo(String texto) {
        chipInfo.setTexto(texto);
    }

    public void setTiempoTotal(double t, double max) {
        barraTotal.setTiempo(t, max, false);
        chipTiempo.setTexto(String.format(java.util.Locale.US, "%.0f s", Math.ceil(t)));
    }

    public void setTiempoEntre(double t, double max) {
        barraEntre.setTiempo(t, max, false);
    }

    public void setBotonEmpezar(boolean habilitado) {
        empezar.setEnabled(habilitado);
    }

    public void setJugable(boolean jugable) {
        tablero.setJugable(jugable);
    }

    public void encender(int casilla, boolean encendida) {
        tablero.encender(casilla, encendida);
    }

    // Al superar un nivel sale confeti y un aviso con una estrella.
    public void mostrarNivelSuperado(int nivel, int casillas) {
        Confeti.lanzar(this, 70, 2000);
        AvisoDeslizante.mostrar(this, "¡NIVEL " + nivel + "!", AvisoDeslizante.Estilo.NIVEL, Sprites.ui("star", 44));
        setNivel(nivel, casillas);
    }

    // Al terminar se abre la ventana con los niveles superados, el mejor nivel y los botones de reintentar y menú.
    public void mostrarFin(String motivo, int niveles, int mejor, Runnable reintentar, Runnable menu) {
        JComponent cuerpo = DialogoResultado.filaDatos("Niveles superados", "" + niveles, "Mejor nivel", "" + mejor);
        new DialogoResultado("Fin del patrón", motivo, cuerpo, null,
                new String[]{"Reintentar", "Menú"}, new Runnable[]{reintentar, menu}).mostrar(this);
    }
}
