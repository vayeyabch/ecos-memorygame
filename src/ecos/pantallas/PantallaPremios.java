package ecos.pantallas;

// Pantalla: clase base de la que hereda; da id, música, fondo animado y ganchos de entrar y salir.
import ecos.app.Pantalla;
// Tema: colores (carmesí, dorado, caja) según claro u oscuro.
import ecos.app.Tema;
// ChipPixel: cajita con texto e ícono; se usa para las monedas y el premio.
import ecos.componentes.ChipPixel;
// Etiqueta: texto del juego con fuente, color del tema y traducción.
import ecos.componentes.Etiqueta;
// ImagenPixel: pinta imágenes sin suavizar; se usa para los íconos del trofeo y los puestos.
import ecos.componentes.ImagenPixel;
// PanelPixel: caja con borde duro y sombra; es la tarjeta principal.
import ecos.componentes.PanelPixel;
// Sprites: entrega los íconos de la interfaz (trofeo, estrella, moneda...).
import ecos.recursos.Sprites;
// Color: guarda colores.
import java.awt.Color;
// Dimension: ancho y alto; fija el ancho mínimo de la tarjeta.
import java.awt.Dimension;
// Graphics: el "pincel" para dibujar las celdas.
import java.awt.Graphics;
// Graphics2D: versión del pincel; se usa para dibujar el símbolo ₡ en una imagen.
import java.awt.Graphics2D;
// GridBagConstraints: reglas de posición y tamaño de cada elemento dentro de un GridBagLayout.
import java.awt.GridBagConstraints;
// GridBagLayout: layout de cuadrícula flexible, con filas y columnas de distinto tamaño.
import java.awt.GridBagLayout;
// Insets: márgenes (arriba, izquierda, abajo, derecha).
import java.awt.Insets;
// BorderFactory: crea bordes y márgenes vacíos.
import javax.swing.BorderFactory;
// JComponent: tipo base de los componentes Swing.
import javax.swing.JComponent;
// JPanel: panel contenedor.
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
        // id "premios" y música "premios".
        super("premios", "premios");
        setLayout(new GridBagLayout());
        // Margen: deja arriba espacio para la barra superior.
        setBorder(BorderFactory.createEmptyBorder(MARGEN_SUPERIOR, 16, 24, 16));

        // Clase anónima: PanelPixel con ancho mínimo de 640 px.
        tarjeta = new PanelPixel() {
            @Override
            public Dimension getPreferredSize() {
                // Ancho mínimo de 640 px.
                return new Dimension(Math.max(640, super.getPreferredSize().width), super.getPreferredSize().height);
            }
        };
        tarjeta.setLayout(new GridBagLayout());
        tarjeta.setPadding(14);
        // c: las reglas de posición que se reutilizan (y se van cambiando) para cada elemento de la tarjeta.
        GridBagConstraints c = new GridBagConstraints();
        c.gridx = 0;
        c.weightx = 1;
        c.anchor = GridBagConstraints.WEST;
        c.fill = GridBagConstraints.HORIZONTAL;

        // Fila 0: trofeo y título.
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
        // El texto vacío " " se llena después en mostrar(...).
        valorDolar = Etiqueta.titulo(" ", 16f, Etiqueta.Rol.CARMESI);
        for (JComponent e : new JComponent[]{valorDolar}) {
            e.setAlignmentX(LEFT_ALIGNMENT);
            dolar.add(e);
            dolar.add(javax.swing.Box.createVerticalStrut(4));
        }
        // Fila 1: dólar del día. (La fila 2 queda para la tabla, que se agrega en mostrar.)
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

    // El controlador la llama para poner las monedas de quien juega.
    public void setMonedas(String nombre, int cantidad) {
        monedas.setTexto("Monedas de " + nombre + ": " + cantidad);
    }

    // El controlador llama esto con los datos listos: dólar del día y, por puesto, jugador, intentos y premio.
    public void mostrar(String dolar, String[] nombres, int[] intentos, String[] colones, String[] dolares) {
        // El ₡ va como imagen porque la fuente de los números no lo tiene.
        valorDolar.setIcon(new javax.swing.ImageIcon(simboloColon(ecos.app.Tema.carmesi())));
        valorDolar.setIconTextGap(8);
        // Se quita el ₡ del texto, porque ya se muestra como ícono.
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
        // revalidate recalcula el layout y repaint vuelve a dibujar.
        tarjeta.revalidate();
        tarjeta.repaint();
    }

    // Arma la tabla del top 5: encabezados y una fila por jugador.
    private JPanel crearTabla(String[] nombres, int[] intentos, String[] colones, String[] dolares) {
        JPanel tabla = new JPanel(new GridBagLayout());
        tabla.setOpaque(false);
        GridBagConstraints c = new GridBagConstraints();
        c.fill = GridBagConstraints.BOTH;
        c.anchor = GridBagConstraints.WEST;
        // Primero los encabezados de las columnas y después una fila por jugador.
        // pesos: cuánto espacio sobrante recibe cada columna.
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
            // Columna 0: ícono del puesto.
            c.gridx = 0;
            c.weightx = 0;
            tabla.add(new Celda(false, new ImagenPixel(Sprites.ui(ICONOS[i], 26))), c);
            // Columna 1: nombre del jugador.
            c.gridx = 1;
            c.weightx = 1;
            // El número va con la fuente de títulos, que se lee mejor, y centrado.
            tabla.add(new Celda(false, Etiqueta.texto(nombres[i], 20f, Etiqueta.Rol.TEXTO)), c);
            // Columna 2: intentos, centrados.
            c.gridx = 2;
            c.weightx = 0.4;
            tabla.add(new Celda(false, Etiqueta.titulo("" + intentos[i], 15f, Etiqueta.Rol.TEXTO), true), c);
            // Columna 3: premio en colones y dólares.
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

    // Dibuja el símbolo ₡ en una imagen pequeña con otra fuente, del color pedido.
    private static java.awt.Image simboloColon(Color color) {
        // BufferedImage: imagen en memoria de 18x22 con transparencia (ARGB).
        java.awt.image.BufferedImage img = new java.awt.image.BufferedImage(18, 22, java.awt.image.BufferedImage.TYPE_INT_ARGB);
        Graphics2D g = img.createGraphics();
        g.setFont(ecos.recursos.Fuentes.texto(24f).deriveFont(java.awt.Font.BOLD));
        g.setColor(color);
        g.drawString("₡", 0, 18);
        // dispose libera el pincel.
        g.dispose();
        return img;
    }

    // Una celda de la tabla, con línea gruesa arriba y, si se pide, fondo durazno suave.
    // Clase anidada static: solo la usa PantallaPremios.
    private static class Celda extends JPanel {
        private final boolean resaltada;

        // Constructor corto: sin centrar. this(...) llama al otro constructor.
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

        // Dibuja el fondo dorado suave (si está resaltada) y la línea de arriba.
        @Override
        protected void paintComponent(Graphics g) {
            if (resaltada) {
                Color d = Tema.dorado();
                // El 70 al final es la transparencia.
                g.setColor(new Color(d.getRed(), d.getGreen(), d.getBlue(), 70));
                g.fillRect(0, 0, getWidth(), getHeight());
            }
            // Línea gruesa de 3 px arriba de cada celda, que separa las filas.
            g.setColor(Tema.caja());
            g.fillRect(0, 0, getWidth(), 3);
            super.paintComponent(g);
        }
    }
}