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

// Vista del modo patrones. No guarda el estado del juego: muestra lo que el controlador pide.
public class PantallaPatron extends Pantalla {

    private final ChipPixel chipNivel = new ChipPixel("Nivel 1 · 3 casillas", 8f, true, ChipPixel.Modo.CAJA, 3);
    private final ChipPixel chipInfo = new ChipPixel("Pulsá empezar", 8f, true, ChipPixel.Modo.CAJA, 3);
    private final ChipPixel chipTiempo = new ChipPixel("12 s", 8f, true, ChipPixel.Modo.CAJA, 3);
    private final BarraTiempo barraTotal = new BarraTiempo(false);
    private final BarraTiempo barraEntre = new BarraSegmentos();
    private TableroPatron tablero; // matriz de 4x4
    private final BotonMenu empezar = new BotonMenu("Empezar", true);
    private IntConsumer alClicCasilla = i -> { };

    public PantallaPatron() {
        super("patrones", "patrones");
        setLayout(new GridBagLayout());
        setBorder(BorderFactory.createEmptyBorder(MARGEN_SUPERIOR - 6, 16, 12, 16));

        // HUD con los chips y las dos barras
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

        // matriz de 4x4
        int ts = Math.max(56, Math.min(92, (int) ((Math.max(800, getWidth()) - 120) / 4.4)));
        tablero = new TableroPatron(ts + 10, casilla -> alClicCasilla.accept(casilla));

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

    // ---------- lo que el controlador pide ----------

    // Ajusta el tamaño de las casillas al espacio disponible.
    @Override
    public void alEntrar() {
        super.alEntrar();
        javax.swing.SwingUtilities.invokeLater(() -> {
            int porAlto = (getHeight() - 390) / 4 - 10;
            int porAncho = (int) ((getWidth() - 120) / 4.4);
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

    // true = se pueden tocar las casillas.
    public void setJugable(boolean jugable) {
        tablero.setJugable(jugable);
    }

    public void encender(int casilla, boolean encendida) {
        tablero.encender(casilla, encendida);
    }

    public void mostrarNivelSuperado(int nivel, int casillas) {
        Confeti.lanzar(this, 70, 2000);
        AvisoDeslizante.mostrar(this, "¡NIVEL " + nivel + "!", AvisoDeslizante.Estilo.NIVEL, Sprites.ui("star", 44));
        setNivel(nivel, casillas);
    }

    public void mostrarFin(String motivo, int niveles, int mejor, Runnable reintentar, Runnable menu) {
        JComponent cuerpo = DialogoResultado.filaDatos("Niveles superados", "" + niveles, "Mejor nivel", "" + mejor);
        new DialogoResultado("Fin del patrón", motivo, cuerpo, null,
                new String[]{"Reintentar", "Menú"}, new Runnable[]{reintentar, menu}).mostrar(this);
    }
}