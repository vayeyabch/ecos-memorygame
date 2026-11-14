package ecos.pantallas;

import ecos.app.Pantalla;
import ecos.app.Tema;
import ecos.componentes.ChipPixel;
import ecos.componentes.Etiqueta;
import ecos.componentes.ImagenPixel;
import ecos.componentes.PanelPixel;
import ecos.recursos.Sprites;
import java.awt.Color;
import java.awt.Dimension;
import java.awt.Graphics;
import java.awt.Graphics2D;
import java.awt.GridBagConstraints;
import java.awt.GridBagLayout;
import java.awt.Insets;
import javax.swing.BorderFactory;
import javax.swing.JComponent;
import javax.swing.JPanel;

// Pantalla de premios: el top 5 con su premio en colones y dólares, el dólar del día y las monedas de quien juega.
public class PantallaPremios extends Pantalla {

    // Un iconito por puesto.
    private static final String[] ICONOS = {"trophy", "star", "heart", "gem", "shield"};

    // Tarjeta con título, dólar y tabla; monedas de quien juega; dólar del día (lo escribe el controlador) y tabla
    // actual, que se reemplaza cuando llegan datos.
    private final PanelPixel tarjeta;
    private final ChipPixel monedas = new ChipPixel("Monedas: 0", 11f, true, ChipPixel.Modo.CAJA, 3);
    private final Etiqueta valorDolar;
    private JPanel tabla = null;

    public PantallaPremios() {
        super("premios", "premios");
        setLayout(new GridBagLayout());
        setBorder(BorderFactory.createEmptyBorder(MARGEN_SUPERIOR, 16, 24, 16));

        tarjeta = new PanelPixel() {
            @Override
            public Dimension getPreferredSize() {
                // Ancho mínimo de 640 px.
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

        JPanel titulo = new JPanel(new java.awt.FlowLayout(java.awt.FlowLayout.LEFT, 10, 0));
        titulo.setOpaque(false);
        titulo.add(new ImagenPixel(Sprites.ui("trophy", 40)));
        titulo.add(Etiqueta.titulo("Mejores 5 jugadores", 16f, Etiqueta.Rol.TEXTO));
        c.gridy = 0;
        c.insets = new Insets(0, 0, 14, 0);
        tarjeta.add(titulo, c);

        // Caja del dólar del día, que se llena cuando el controlador manda los datos.
        JPanel dolar = new JPanel();
        dolar.setOpaque(false);
        dolar.setLayout(new javax.swing.BoxLayout(dolar, javax.swing.BoxLayout.Y_AXIS));
        valorDolar = Etiqueta.titulo(" ", 16f, Etiqueta.Rol.CARMESI);
        for (JComponent e : new JComponent[]{valorDolar}) {
            e.setAlignmentX(LEFT_ALIGNMENT);
            dolar.add(e);
            dolar.add(javax.swing.Box.createVerticalStrut(4));
        }
        c.gridy = 1;
        tarjeta.add(dolar, c);

        // Las monedas van abajo del top 5, con la moneda al lado y una nota de para qué sirven.
        monedas.setIcono(ecos.recursos.Sprites.ui("coin", 20));
        c.gridy = 3;
        c.fill = GridBagConstraints.NONE;
        c.anchor = GridBagConstraints.WEST;
        c.insets = new Insets(14, 0, 4, 0);
        tarjeta.add(monedas, c);
        c.gridy = 4;
        c.insets = new Insets(0, 0, 0, 0);
        tarjeta.add(Etiqueta.texto("Las monedas no cambian el ranking: sirven para comprar el Ojo espía.", 18f, Etiqueta.Rol.SUAVE), c);

        add(tarjeta);
    }

    public void setMonedas(String nombre, int cantidad) {
        monedas.setTexto("Monedas de " + nombre + ": " + cantidad);
    }

    // El controlador llama esto con los datos listos: dólar del día y, por puesto, jugador, intentos y premio.
    public void mostrar(String dolar, String[] nombres, int[] intentos, String[] colones, String[] dolares) {
        // El ₡ va como imagen porque la fuente de los números no lo tiene.
        valorDolar.setIcon(new javax.swing.ImageIcon(simboloColon(ecos.app.Tema.carmesi())));
        valorDolar.setIconTextGap(8);
        valorDolar.setText(dolar.replace("₡", "").trim());
        // Si ya había una tabla se quita y se pone la nueva.
        if (tabla != null) {
            tarjeta.remove(tabla);
        }
        GridBagConstraints c = new GridBagConstraints();
        c.gridx = 0;
        c.gridy = 2;
        c.weightx = 1;
        c.anchor = GridBagConstraints.WEST;
        c.fill = GridBagConstraints.HORIZONTAL;
        tabla = crearTabla(nombres, intentos, colones, dolares);
        tarjeta.add(tabla, c);
        tarjeta.revalidate();
        tarjeta.repaint();
    }

    private JPanel crearTabla(String[] nombres, int[] intentos, String[] colones, String[] dolares) {
        JPanel tabla = new JPanel(new GridBagLayout());
        tabla.setOpaque(false);
        GridBagConstraints c = new GridBagConstraints();
        c.fill = GridBagConstraints.BOTH;
        c.anchor = GridBagConstraints.WEST;
        // Primero los encabezados de las columnas y después una fila por jugador.
        String[] cab = {"", "JUGADOR", "INTENTOS", "PREMIO"};
        double[] pesos = {0, 1, 0.4, 0.8};
        for (int col = 0; col < 4; col++) {
            c.gridx = col;
            c.gridy = 0;
            c.weightx = pesos[col];
            c.insets = new Insets(0, 6, 4, 6);
            Etiqueta titulo = Etiqueta.titulo(cab[col], 11f, Etiqueta.Rol.SUAVE);
            if (col == 2) {
                titulo.setHorizontalAlignment(Etiqueta.CENTER);
            }
            tabla.add(titulo, c);
        }
        for (int i = 0; i < nombres.length; i++) {
            c.gridy = i + 1;
            c.insets = new Insets(0, 0, 0, 0);
            c.gridx = 0;
            c.weightx = 0;
            tabla.add(new Celda(false, new ImagenPixel(Sprites.ui(ICONOS[i], 26))), c);
            c.gridx = 1;
            c.weightx = 1;
            // El número va con la fuente de títulos, que se lee mejor, y centrado.
            tabla.add(new Celda(false, Etiqueta.texto(nombres[i], 20f, Etiqueta.Rol.TEXTO)), c);
            c.gridx = 2;
            c.weightx = 0.4;
            tabla.add(new Celda(false, Etiqueta.titulo("" + intentos[i], 15f, Etiqueta.Rol.TEXTO), true), c);
            c.gridx = 3;
            c.weightx = 0.8;
            tabla.add(new Celda(false, crearPremio(colones[i], dolares[i])), c);
        }
        return tabla;
    }

    // Chip durazno con los colones y los dólares abajo.
    private JPanel crearPremio(String colones, String dolares) {
        JPanel p = new JPanel();
        p.setOpaque(false);
        p.setLayout(new javax.swing.BoxLayout(p, javax.swing.BoxLayout.Y_AXIS));
        ChipPixel chip = new ChipPixel(colones, 14f, true, ChipPixel.Modo.DURAZNO, 2);
        // El ₡ va aparte porque esa fuente no lo tiene.
        chip.setIcono(simboloColon(new Color(91, 35, 51)));
        Etiqueta usd = Etiqueta.titulo(dolares, 10f, Etiqueta.Rol.SUAVE);
        chip.setAlignmentX(LEFT_ALIGNMENT);
        usd.setAlignmentX(LEFT_ALIGNMENT);
        p.add(chip);
        p.add(javax.swing.Box.createVerticalStrut(6));
        p.add(usd);
        return p;
    }

    private static java.awt.Image simboloColon(Color color) {
        java.awt.image.BufferedImage img = new java.awt.image.BufferedImage(18, 22, java.awt.image.BufferedImage.TYPE_INT_ARGB);
        Graphics2D g = img.createGraphics();
        g.setFont(ecos.recursos.Fuentes.texto(24f).deriveFont(java.awt.Font.BOLD));
        g.setColor(color);
        g.drawString("₡", 0, 18);
        g.dispose();
        return img;
    }

    // Una celda de la tabla, con línea gruesa arriba y, si se pide, fondo durazno suave.
    private static class Celda extends JPanel {
        private final boolean resaltada;

        Celda(boolean resaltada, JComponent contenido) {
            this(resaltada, contenido, false);
        }

        Celda(boolean resaltada, JComponent contenido, boolean centrada) {
            super(new GridBagLayout());
            this.resaltada = resaltada;
            setOpaque(false);
            setBorder(BorderFactory.createEmptyBorder(10, 6, 10, 6));
            GridBagConstraints g = new GridBagConstraints();
            g.anchor = centrada ? GridBagConstraints.CENTER : GridBagConstraints.WEST;
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
