package ecos.pantallas;

// Clases del propio proyecto que se usan aquí.
import ecos.app.Pantalla;
import ecos.app.Tema;
import ecos.componentes.ChipPixel;
import ecos.componentes.Etiqueta;
import ecos.componentes.ImagenPixel;
import ecos.componentes.PanelPixel;
import ecos.recursos.Sprites;
// Librería gráfica AWT, Color para los colores, Dimension para los tamaños, Graphics para dibujar, Graphics2D para
// dibujar con más opciones, GridBagConstraints para las reglas de la cuadrícula flexible, GridBagLayout para acomodar en
// una cuadrícula flexible y Insets para los márgenes.
import java.awt.Color;
import java.awt.Dimension;
import java.awt.Graphics;
import java.awt.Graphics2D;
import java.awt.GridBagConstraints;
import java.awt.GridBagLayout;
import java.awt.Insets;
// Librería Swing, BorderFactory para los bordes, JComponent para la base de los componentes y JPanel para los paneles.
import javax.swing.BorderFactory;
import javax.swing.JComponent;
import javax.swing.JPanel;

// Pantalla de premios, el top 5 con su premio en colones y dólares, el dólar del día y las monedas de quien juega.
// Pantalla con el top 5 de premios.
public class PantallaPremios extends Pantalla {

    // Un iconito por puesto.
    private static final String[] ICONOS = {"trophy", "star", "heart", "gem", "shield"};

    // La tarjeta con el título, el dólar y la tabla, las monedas acumuladas de quien juega, el dólar del día (lo escribe el
    // controlador) y la tabla actual, que se reemplaza cada vez que llegan datos.
    private final PanelPixel tarjeta;
    private final ChipPixel monedas = new ChipPixel("Monedas: 0", 11f, true, ChipPixel.Modo.CAJA, 3);
    private final Etiqueta valorDolar;
    private JPanel tabla = null;

    // Arma la tarjeta del centro con un ancho mínimo de 640 px.
    // Arma la pantalla.
    public PantallaPremios() {
        // Le pone id y música a la pantalla. Usa GridBagLayout para centrar la tarjeta. Deja el margen de la pantalla.
        super("premios", "premios");
        setLayout(new GridBagLayout());
        setBorder(BorderFactory.createEmptyBorder(MARGEN_SUPERIOR, 16, 24, 16));

        // Crea la tarjeta principal.
        tarjeta = new PanelPixel() {
            // Tamaño que necesita la tarjeta.
            @Override
            public Dimension getPreferredSize() {
                // Ancho mínimo de 640 px.
                return new Dimension(Math.max(640, super.getPreferredSize().width), super.getPreferredSize().height);
            }
        };
        // Acomoda lo de adentro con GridBagLayout. Deja un margen adentro de la tarjeta. Reglas de acomodo de las filas.
        // Empieza en la primera columna.
        tarjeta.setLayout(new GridBagLayout());
        tarjeta.setPadding(14);
        GridBagConstraints c = new GridBagConstraints();
        c.gridx = 0;
        // Se puede estirar a lo ancho. Todo queda a la izquierda. Se estira solo en horizontal.
        c.weightx = 1;
        c.anchor = GridBagConstraints.WEST;
        c.fill = GridBagConstraints.HORIZONTAL;

        // Fila del título sin fondo propio, con el trofeo y el texto del título.
        JPanel titulo = new JPanel(new java.awt.FlowLayout(java.awt.FlowLayout.LEFT, 10, 0));
        titulo.setOpaque(false);
        titulo.add(new ImagenPixel(Sprites.ui("trophy", 40)));
        titulo.add(Etiqueta.titulo("Mejores 5 jugadores", 16f, Etiqueta.Rol.TEXTO));
        // Deja un espacio abajo del título. Agrega el título a la tarjeta.
        c.gridy = 0;
        c.insets = new Insets(0, 0, 14, 0);
        tarjeta.add(titulo, c);

        // Caja del dólar del día, sin fondo propio, con sus elementos uno debajo del otro, que se llena cuando el
        // controlador manda los datos.
        JPanel dolar = new JPanel();
        dolar.setOpaque(false);
        dolar.setLayout(new javax.swing.BoxLayout(dolar, javax.swing.BoxLayout.Y_AXIS));
        valorDolar = Etiqueta.titulo(" ", 16f, Etiqueta.Rol.CARMESI);
        // Recorre el texto del dólar. Lo alinea a la izquierda. Lo agrega a la caja. Deja un espacio abajo.
        for (JComponent e : new JComponent[]{valorDolar}) {
            e.setAlignmentX(LEFT_ALIGNMENT);
            dolar.add(e);
            dolar.add(javax.swing.Box.createVerticalStrut(4));
        }
        // Agrega la caja del dólar.
        c.gridy = 1;
        tarjeta.add(dolar, c);

        // Las monedas acumuladas van abajo del top 5, con la moneda al lado y una nota que explica para qué sirven.
        // Moneda al lado del número. Fila de las monedas. El chip no se estira. Todo queda a la izquierda.
        monedas.setIcono(ecos.recursos.Sprites.ui("coin", 20));
        c.gridy = 3;
        c.fill = GridBagConstraints.NONE;
        c.anchor = GridBagConstraints.WEST;
        // Deja espacio arriba y abajo. Las monedas acumuladas van abajo del top 5. Fila de la nota. Sin espacios extra.
        c.insets = new Insets(14, 0, 4, 0);
        tarjeta.add(monedas, c);
        c.gridy = 4;
        c.insets = new Insets(0, 0, 0, 0);
        // Agrega la nota sobre las monedas.
        tarjeta.add(Etiqueta.texto("Las monedas no cambian el ranking: sirven para comprar el Ojo espía.", 18f, Etiqueta.Rol.SUAVE), c);

        // Agrega la tarjeta a la pantalla.
        add(tarjeta);
    }

    // El controlador llama esto con las monedas de quien juega.
    // Cambia el texto de las monedas.
    public void setMonedas(String nombre, int cantidad) {
        // Escribe el nombre y la cantidad.
        monedas.setTexto("Monedas de " + nombre + ": " + cantidad);
    }

    // El controlador llama esto con los datos listos, el dólar del día y, por puesto, jugador, intentos y premio.
    // Muestra los datos del ranking.
    public void mostrar(String dolar, String[] nombres, int[] intentos, String[] colones, String[] dolares) {
        // El ₡ va como imagen porque la fuente de los números no lo tiene. Deja un espacio entre el símbolo y el número.
        // Escribe el dólar del día (solo el número).
        valorDolar.setIcon(new javax.swing.ImageIcon(simboloColon(ecos.app.Tema.carmesi())));
        valorDolar.setIconTextGap(8);
        valorDolar.setText(dolar.replace("₡", "").trim());
        // Si ya había una tabla se quita y se pone la nueva con los datos frescos.
        // Si ya había una tabla. Quita la tabla vieja.
        if (tabla != null) {
            tarjeta.remove(tabla);
        }
        // Reglas de acomodo de la tabla, que empieza en la primera columna, va en la tercera fila y se puede estirar a lo
        // ancho.
        GridBagConstraints c = new GridBagConstraints();
        c.gridx = 0;
        c.gridy = 2;
        c.weightx = 1;
        // Todo queda a la izquierda. Se estira solo en horizontal. Crea la tabla nueva con los datos. Pone la tabla nueva.
        c.anchor = GridBagConstraints.WEST;
        c.fill = GridBagConstraints.HORIZONTAL;
        tabla = crearTabla(nombres, intentos, colones, dolares);
        tarjeta.add(tabla, c);
        // Vuelve a calcular el acomodo. Vuelve a dibujar la tarjeta.
        tarjeta.revalidate();
        tarjeta.repaint();
    }

    // Tabla de filas, icono, jugador, intentos y premio.
    // Arma la tabla del ranking.
    private JPanel crearTabla(String[] nombres, int[] intentos, String[] colones, String[] dolares) {
        // Panel de la tabla sin fondo propio, con reglas de acomodo para que las celdas llenen todo su espacio.
        JPanel tabla = new JPanel(new GridBagLayout());
        tabla.setOpaque(false);
        GridBagConstraints c = new GridBagConstraints();
        c.fill = GridBagConstraints.BOTH;
        // Todo queda a la izquierda.
        c.anchor = GridBagConstraints.WEST;
        // Primero los encabezados de las columnas y después una fila por cada jugador.
        // Nombres de las columnas. Qué tan ancha es cada columna. Recorre las cuatro columnas. Columna actual.
        String[] cab = {"", "JUGADOR", "INTENTOS", "PREMIO"};
        double[] pesos = {0, 1, 0.4, 0.8};
        for (int col = 0; col < 4; col++) {
            c.gridx = col;
            // Primera fila, la de los títulos. Ancho de la columna. Deja un margen a los lados. Texto del título de la columna.
            c.gridy = 0;
            c.weightx = pesos[col];
            c.insets = new Insets(0, 6, 4, 6);
            Etiqueta titulo = Etiqueta.titulo(cab[col], 11f, Etiqueta.Rol.SUAVE);
            // Si es la columna de intentos. La columna de intentos va centrada.
            if (col == 2) {
                titulo.setHorizontalAlignment(Etiqueta.CENTER);
            }
            // Agrega el título a la tabla.
            tabla.add(titulo, c);
        }
        // Recorre los jugadores. Fila del jugador. Sin espacios extra. Primera columna, la del ícono.
        for (int i = 0; i < nombres.length; i++) {
            c.gridy = i + 1;
            c.insets = new Insets(0, 0, 0, 0);
            c.gridx = 0;
            // Agrega el ícono del puesto. Segunda columna, la del nombre. Se estira para ocupar el espacio.
            c.weightx = 0;
            tabla.add(new Celda(false, new ImagenPixel(Sprites.ui(ICONOS[i], 26))), c);
            c.gridx = 1;
            c.weightx = 1;
            // Agrega el nombre del jugador. Tercera columna, la de intentos. Es la más angosta. Número con la fuente de títulos
            // (se lee mejor) y centrado.
            tabla.add(new Celda(false, Etiqueta.texto(nombres[i], 20f, Etiqueta.Rol.TEXTO)), c);
            c.gridx = 2;
            c.weightx = 0.4;
            tabla.add(new Celda(false, Etiqueta.titulo("" + intentos[i], 15f, Etiqueta.Rol.TEXTO), true), c);
            // Cuarta columna, la del premio. Ancho mediano. Agrega el premio con colones y dólares.
            c.gridx = 3;
            c.weightx = 0.8;
            tabla.add(new Celda(false, crearPremio(colones[i], dolares[i])), c);
        }
        // Entrega la tabla ya armada.
        return tabla;
    }

    // Chip durazno con los colones y los dólares abajo.
    // Arma el premio con colones y dólares.
    private JPanel crearPremio(String colones, String dolares) {
        // Panel del premio sin fondo propio, con colones y dólares uno debajo del otro y los números en la fuente de
        // títulos, que es más clara.
        JPanel p = new JPanel();
        p.setOpaque(false);
        p.setLayout(new javax.swing.BoxLayout(p, javax.swing.BoxLayout.Y_AXIS));
        ChipPixel chip = new ChipPixel(colones, 14f, true, ChipPixel.Modo.DURAZNO, 2);
        // El símbolo ₡ va aparte porque esa fuente no lo tiene. Texto de los dólares. Alinea el chip a la izquierda. Alinea
        // los dólares a la izquierda.
        chip.setIcono(simboloColon(new Color(91, 35, 51)));
        Etiqueta usd = Etiqueta.titulo(dolares, 10f, Etiqueta.Rol.SUAVE);
        chip.setAlignmentX(LEFT_ALIGNMENT);
        usd.setAlignmentX(LEFT_ALIGNMENT);
        // Agrega los colones. Espacio entre los colones y los dólares. Agrega los dólares. Entrega el panel.
        p.add(chip);
        p.add(javax.swing.Box.createVerticalStrut(6));
        p.add(usd);
        return p;
    }

    // Dibuja el símbolo ₡ en una imagen pequeña (la fuente de los números no lo trae).
    // Dibuja el símbolo ₡ en una imagen.
    private static java.awt.Image simboloColon(Color color) {
        // Crea la imagen vacía. Pincel para dibujar en la imagen. Fuente del símbolo. Color del símbolo.
        java.awt.image.BufferedImage img = new java.awt.image.BufferedImage(18, 22, java.awt.image.BufferedImage.TYPE_INT_ARGB);
        Graphics2D g = img.createGraphics();
        g.setFont(ecos.recursos.Fuentes.texto(24f).deriveFont(java.awt.Font.BOLD));
        g.setColor(color);
        // Dibuja el símbolo. Libera el pincel. Entrega la imagen.
        g.drawString("₡", 0, 18);
        g.dispose();
        return img;
    }

    // Una celda de la tabla, línea gruesa arriba y, si se pide, fondo durazno suave.
    // Una celda de la tabla.
    private static class Celda extends JPanel {
        // Dice si la celda va resaltada.
        private final boolean resaltada;

        // Celda normal, sin centrar. Usa el otro constructor.
        Celda(boolean resaltada, JComponent contenido) {
            this(resaltada, contenido, false);
        }

        // Celda que puede ir centrada. Usa GridBagLayout. Guarda si va resaltada. Sin fondo propio.
        Celda(boolean resaltada, JComponent contenido, boolean centrada) {
            super(new GridBagLayout());
            this.resaltada = resaltada;
            setOpaque(false);
            // Deja un margen adentro. Reglas de acomodo del contenido. Centrada o a la izquierda. Ocupa todo el ancho.
            setBorder(BorderFactory.createEmptyBorder(10, 6, 10, 6));
            GridBagConstraints g = new GridBagConstraints();
            g.anchor = centrada ? GridBagConstraints.CENTER : GridBagConstraints.WEST;
            g.weightx = 1;
            // Agrega el contenido.
            add(contenido, g);
        }

        // Dibuja la celda.
        @Override
        protected void paintComponent(Graphics g) {
            // Si va resaltada. Toma el color dorado. Lo pone con transparencia. Pinta el fondo.
            if (resaltada) {
                Color d = Tema.dorado();
                g.setColor(new Color(d.getRed(), d.getGreen(), d.getBlue(), 70));
                g.fillRect(0, 0, getWidth(), getHeight());
            }
            // Color de la línea de arriba. Pinta la línea gruesa de arriba. Pinta el contenido encima.
            g.setColor(Tema.caja());
            g.fillRect(0, 0, getWidth(), 3);
            super.paintComponent(g);
        }
    }
}