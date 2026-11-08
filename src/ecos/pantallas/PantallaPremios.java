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

public class PantallaPremios extends Pantalla {

    private static final String[] ICONOS = {"trophy", "star", "heart", "gem", "shield"}; // Un iconito por puesto.

    private final PanelPixel tarjeta; // La tarjeta que contiene el título, el dólar y la tabla.
    private final ChipPixel monedas = new ChipPixel("Monedas: 0", 11f, true, ChipPixel.Modo.CAJA, 3); // monedas acumuladas de quien juega
    private final Etiqueta valorDolar; // El dólar del día (lo escribe el controlador).
    private JPanel tabla = null; // La tabla actual (se reemplaza cada vez que llegan datos).

    public PantallaPremios() {
        super("premios", "premios");
        setLayout(new GridBagLayout());
        setBorder(BorderFactory.createEmptyBorder(MARGEN_SUPERIOR, 16, 24, 16));

        tarjeta = new PanelPixel() {
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
        valorDolar = Etiqueta.titulo(" ", 16f, Etiqueta.Rol.CARMESI); // Se llena cuando el controlador manda los datos.
        for (JComponent e : new JComponent[]{valorDolar}) {
            e.setAlignmentX(LEFT_ALIGNMENT);
            dolar.add(e);
            dolar.add(javax.swing.Box.createVerticalStrut(4));
        }
        c.gridy = 1;
        tarjeta.add(dolar, c);

        monedas.setIcono(ecos.recursos.Sprites.ui("coin", 20)); // Moneda al lado del número.
        c.gridy = 3;
        c.fill = GridBagConstraints.NONE; // El chip no se estira.
        c.anchor = GridBagConstraints.WEST;
        c.insets = new Insets(14, 0, 4, 0);
        tarjeta.add(monedas, c); // Las monedas acumuladas van abajo del top 5.
        c.gridy = 4;
        c.insets = new Insets(0, 0, 0, 0);
        tarjeta.add(Etiqueta.texto("Las monedas no cambian el ranking: sirven para comprar el Ojo espía.", 18f, Etiqueta.Rol.SUAVE), c);

        add(tarjeta);
    }

    // El controlador llama esto con las monedas de quien juega.
    public void setMonedas(String nombre, int cantidad) {
        monedas.setTexto("Monedas de " + nombre + ": " + cantidad);
    }

    // El controlador llama esto con los datos listos: el dólar del día y, por puesto, jugador, intentos y premio.
    public void mostrar(String dolar, String[] nombres, int[] intentos, String[] colones, String[] dolares) {
        valorDolar.setIcon(new javax.swing.ImageIcon(simboloColon(ecos.app.Tema.carmesi()))); // El ₡ va como imagen porque la fuente de los números no lo tiene.
        valorDolar.setIconTextGap(8);
        valorDolar.setText(dolar.replace("₡", "").trim()); // Escribe el dólar del día (solo el número).
        if (tabla != null) {
            tarjeta.remove(tabla); // Quita la tabla vieja.
        }
        GridBagConstraints c = new GridBagConstraints();
        c.gridx = 0;
        c.gridy = 2;
        c.weightx = 1;
        c.anchor = GridBagConstraints.WEST;
        c.fill = GridBagConstraints.HORIZONTAL;
        tabla = crearTabla(nombres, intentos, colones, dolares);
        tarjeta.add(tabla, c); // Pone la tabla nueva.
        tarjeta.revalidate();
        tarjeta.repaint();
    }

    // Tabla de filas: icono, jugador, intentos y premio.
    private JPanel crearTabla(String[] nombres, int[] intentos, String[] colones, String[] dolares) {
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
            Etiqueta titulo = Etiqueta.titulo(cab[col], 11f, Etiqueta.Rol.SUAVE);
            if (col == 2) {
                titulo.setHorizontalAlignment(Etiqueta.CENTER); // La columna de intentos va centrada.
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
            tabla.add(new Celda(false, Etiqueta.texto(nombres[i], 20f, Etiqueta.Rol.TEXTO)), c);
            c.gridx = 2;
            c.weightx = 0.4;
            tabla.add(new Celda(false, Etiqueta.titulo("" + intentos[i], 15f, Etiqueta.Rol.TEXTO), true), c); // Número con la fuente de títulos (se lee mejor) y centrado.
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
        ChipPixel chip = new ChipPixel(colones, 14f, true, ChipPixel.Modo.DURAZNO, 2); // Los números con la fuente de títulos (más claros).
        chip.setIcono(simboloColon(new Color(91, 35, 51))); // El símbolo ₡ va aparte porque esa fuente no lo tiene.
        Etiqueta usd = Etiqueta.titulo(dolares, 10f, Etiqueta.Rol.SUAVE);
        chip.setAlignmentX(LEFT_ALIGNMENT);
        usd.setAlignmentX(LEFT_ALIGNMENT);
        p.add(chip);
        p.add(javax.swing.Box.createVerticalStrut(6)); // Espacio entre los colones y los dólares.
        p.add(usd);
        return p;
    }

    // Dibuja el símbolo ₡ en una imagen pequeña (la fuente de los números no lo trae).
    private static java.awt.Image simboloColon(Color color) {
        java.awt.image.BufferedImage img = new java.awt.image.BufferedImage(18, 22, java.awt.image.BufferedImage.TYPE_INT_ARGB);
        Graphics2D g = img.createGraphics();
        g.setFont(ecos.recursos.Fuentes.texto(24f).deriveFont(java.awt.Font.BOLD));
        g.setColor(color);
        g.drawString("₡", 0, 18);
        g.dispose();
        return img;
    }

    // Una celda de la tabla: línea gruesa arriba y, si se pide, fondo durazno suave.
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
            g.anchor = centrada ? GridBagConstraints.CENTER : GridBagConstraints.WEST; // Centrada o a la izquierda.
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