package ecos.pantallas;

import ecos.app.Pantalla;
import ecos.app.Sesion;
import ecos.app.Tema;
import ecos.componentes.ChipPixel;
import ecos.componentes.Etiqueta;
import ecos.componentes.ImagenPixel;
import ecos.componentes.PanelPixel;
import ecos.recursos.Sprites;
import java.awt.Color;
import java.awt.Dimension;
import java.awt.Graphics;
import java.awt.GridBagConstraints;
import java.awt.GridBagLayout;
import java.awt.Insets;
import javax.swing.BorderFactory;
import javax.swing.JComponent;
import javax.swing.JPanel;

public class PantallaPremios extends Pantalla {

    private static final String[] ICONOS = {"trophy", "star", "heart", "gem", "shield"}; // Un iconito por puesto.

    public PantallaPremios() {
        super("premios", "premios");
        setLayout(new GridBagLayout());
        setBorder(BorderFactory.createEmptyBorder(MARGEN_SUPERIOR, 16, 24, 16));

        PanelPixel tarjeta = new PanelPixel() {
            @Override
            public Dimension getPreferredSize() {
                return new Dimension(Math.max(640, super.getPreferredSize().width), super.getPreferredSize().height);
            }
        };
        tarjeta.setLayout(new GridBagLayout());
        tarjeta.setPadding(14);
        GridBagConstraints c = new GridBagConstraints();
        c.gridx = 0;
        c.weightx = 1;
        c.anchor = GridBagConstraints.WEST;
        c.fill = GridBagConstraints.HORIZONTAL;

        // título con el trofeo
        JPanel titulo = new JPanel(new java.awt.FlowLayout(java.awt.FlowLayout.LEFT, 10, 0));
        titulo.setOpaque(false);
        titulo.add(new ImagenPixel(Sprites.ui("trophy", 40)));
        titulo.add(Etiqueta.titulo("Mejores 5 jugadores", 16f, Etiqueta.Rol.TEXTO));
        c.gridy = 0;
        c.insets = new Insets(0, 0, 14, 0);
        tarjeta.add(titulo, c);

        // caja con el dólar
        JPanel dolar = new JPanel();
        dolar.setOpaque(false);
        dolar.setLayout(new javax.swing.BoxLayout(dolar, javax.swing.BoxLayout.Y_AXIS));
        Etiqueta etDolar = Etiqueta.titulo("DÓLAR BCCR (VENTA)", 9f, Etiqueta.Rol.SUAVE);
        Etiqueta valor = Etiqueta.texto("₡ 487,20", 26f, Etiqueta.Rol.CARMESI);
        Etiqueta nota = Etiqueta.texto("Valor de ejemplo. En Java se consulta el API del BCCR.", 16f, Etiqueta.Rol.SUAVE);
        for (JComponent e : new JComponent[]{etDolar, valor, nota}) {
            e.setAlignmentX(LEFT_ALIGNMENT);
            dolar.add(e);
            dolar.add(javax.swing.Box.createVerticalStrut(4));
        }
        c.gridy = 1;
        tarjeta.add(dolar, c);

        // tabla
        c.gridy = 2;
        c.insets = new Insets(0, 0, 14, 0);
        tarjeta.add(crearTabla(), c);

        // fórmula (la misma del diseño)
        c.gridy = 3;
        c.insets = new Insets(0, 0, 0, 0);
        tarjeta.add(Etiqueta.texto("<html>premio = 1 / puntaje × 100 × CambioDólar. El puntaje es la cantidad de intentos para ganar. Datos de ejemplo.</html>", 16f, Etiqueta.Rol.SUAVE), c);
        add(tarjeta);
    }

    // Tabla de 5 filas: icono, jugador, intentos y premio.
    private JPanel crearTabla() {
        JPanel tabla = new JPanel(new GridBagLayout());
        tabla.setOpaque(false);
        GridBagConstraints c = new GridBagConstraints();
        c.fill = GridBagConstraints.BOTH;
        c.anchor = GridBagConstraints.WEST;
        String[] cab = {"", "JUGADOR", "INTENTOS", "PREMIO"};
        double[] pesos = {0, 1, 0.4, 0.8};
        for (int col = 0; col < 4; col++) {
            c.gridx = col;
            c.gridy = 0;
            c.weightx = pesos[col];
            c.insets = new Insets(0, 6, 4, 6);
            tabla.add(Etiqueta.titulo(cab[col], 8f, Etiqueta.Rol.SUAVE), c);
        }
        for (int i = 0; i < 5; i++) {
            int intentos = Sesion.PUNTAJES[i];
            c.gridy = i + 1;
            c.insets = new Insets(0, 0, 0, 0);
            c.gridx = 0;
            c.weightx = 0;
            tabla.add(new Celda(i == 1, new ImagenPixel(Sprites.ui(ICONOS[i], 26))), c);
            c.gridx = 1;
            c.weightx = 1;
            tabla.add(new Celda(i == 1, Etiqueta.texto(Sesion.NOMBRES[i], 20f, Etiqueta.Rol.TEXTO)), c);
            c.gridx = 2;
            c.weightx = 0.4;
            tabla.add(new Celda(i == 1, Etiqueta.texto("" + intentos, 20f, Etiqueta.Rol.TEXTO)), c);
            c.gridx = 3;
            c.weightx = 0.8;
            tabla.add(new Celda(i == 1, crearPremio(intentos)), c);
        }
        return tabla;
    }

    // Chip durazno con los colones y los dólares abajo.
    private JPanel crearPremio(int intentos) {
        JPanel p = new JPanel();
        p.setOpaque(false);
        p.setLayout(new javax.swing.BoxLayout(p, javax.swing.BoxLayout.Y_AXIS));
        ChipPixel chip = new ChipPixel(Sesion.colones(Sesion.premioColones(intentos)), 19f, false, ChipPixel.Modo.DURAZNO, 2);
        Etiqueta usd = Etiqueta.texto("$" + String.format(java.util.Locale.US, "%.2f", Sesion.premioDolares(intentos)), 14f, Etiqueta.Rol.SUAVE);
        chip.setAlignmentX(LEFT_ALIGNMENT);
        usd.setAlignmentX(LEFT_ALIGNMENT);
        p.add(chip);
        p.add(usd);
        return p;
    }

    // Una celda de la tabla: línea gruesa arriba y, si se pide, fondo durazno suave.
    private static class Celda extends JPanel {
        private final boolean resaltada;

        Celda(boolean resaltada, JComponent contenido) {
            super(new GridBagLayout());
            this.resaltada = resaltada;
            setOpaque(false);
            setBorder(BorderFactory.createEmptyBorder(10, 6, 10, 6));
            GridBagConstraints g = new GridBagConstraints();
            g.anchor = GridBagConstraints.WEST;
            g.weightx = 1;
            add(contenido, g);
        }

        @Override
        protected void paintComponent(Graphics g) {
            if (resaltada) {
                Color d = Tema.dorado();
                g.setColor(new Color(d.getRed(), d.getGreen(), d.getBlue(), 70));
                g.fillRect(0, 0, getWidth(), getHeight());
            }
            g.setColor(Tema.caja());
            g.fillRect(0, 0, getWidth(), 3);
            super.paintComponent(g);
        }
    }
}
