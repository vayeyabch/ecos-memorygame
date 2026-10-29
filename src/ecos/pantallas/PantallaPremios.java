package ecos.pantallas;

import ecos.app.Pantalla; // Clase padre de todas las pantallas.
import ecos.app.Tema; // Para usar los colores del tema actual.
import ecos.componentes.BotonMenu; // Botón estilo pixel.
import ecos.componentes.PanelPixel; // Panel con borde y sombra pixelados.
import ecos.recursos.Fuentes; // Fuentes pixel del juego.
import ecos.recursos.Sonido; // Para los efectos de sonido.
import ecos.recursos.Sprites; // Para las imágenes de las mascotas.
import java.awt.Dimension; // Para fijar el tamaño del texto de cada premio.
import java.awt.FlowLayout; // Layout para poner las tarjetas y botones en fila.
import java.awt.GridBagConstraints; // Reglas de posición dentro del GridBagLayout.
import java.awt.GridBagLayout; // Layout en cuadrícula para centrar el contenido.
import java.awt.Insets; // Márgenes entre componentes.
import java.util.ArrayList; // Lista para guardar las etiquetas que cambian de color.
import java.util.List; // Tipo general de lista.
import javax.swing.ImageIcon; // Imagen lista para ponerse en un JLabel.
import javax.swing.JLabel; // Etiqueta para texto o imágenes.
import javax.swing.JPanel; // Panel simple para agrupar piezas.

public class PantallaPremios extends Pantalla {

    private static final int TAM_MASCOTA = 96; // Tamaño en píxeles de la mascota de cada premio.
    private static final int ANCHO_TEXTO = 190; // Ancho en píxeles del texto de cada premio.

    private final JLabel titulo = new JLabel("Premios", JLabel.CENTER); // Título de la pantalla.
    private final List<JLabel> nombres = new ArrayList<>(); // Etiquetas con el nombre de cada premio.
    private final List<JLabel> montos = new ArrayList<>(); // Etiquetas con el monto de cada premio.

    public PantallaPremios() { // Constructor de la pantalla.
        super("premios", "premios"); // Su id es "premios" y suena la música de premios.
        setLayout(new GridBagLayout()); // Usa GridBagLayout para centrar el contenido.

        titulo.setFont(Fuentes.titulo(26f)); // El título usa la fuente de títulos.
        PanelPixel panelTitulo = new PanelPixel(new FlowLayout(FlowLayout.CENTER, 0, 0)); // Panel con borde pixelado para que el título se lea sobre el fondo.
        panelTitulo.setPadding(14); // Espacio interno de 14 píxeles.
        panelTitulo.add(titulo); // Mete el título dentro del panel.

        JPanel fila = new JPanel(new FlowLayout(FlowLayout.CENTER, 24, 0)); // Fila con las tarjetas de premios separadas 24 píxeles.
        fila.setOpaque(false); // La hace transparente para que se vea el fondo.
        fila.add(crearTarjeta(1, "Ganar una partida en Clásico", "₡ 2 320 ($4.76)")); // Primera tarjeta de premio.
        fila.add(crearTarjeta(2, "Subir de nivel en Patrones", "₡ 1 000 ($2.05)")); // Segunda tarjeta de premio.
        fila.add(crearTarjeta(3, "Hacer una racha de aciertos", "₡ 500 ($1.02)")); // Tercera tarjeta de premio.

        BotonMenu btnVolver = new BotonMenu("Volver", false); // Botón para volver a la pantalla anterior.
        btnVolver.addActionListener(e -> { Sonido.efecto("click"); volver(); }); // Suena el click y vuelve a la pantalla anterior.

        GridBagConstraints c = new GridBagConstraints(); // Objeto que guarda la posición de cada pieza.
        c.gridx = 0; // Todo va en una sola columna.
        c.insets = new Insets(10, 10, 10, 10); // Margen alrededor de cada pieza.
        c.gridy = 0; add(panelTitulo, c); // Primera fila: el título.
        c.gridy = 1; add(fila, c); // Segunda fila: las tarjetas.
        c.gridy = 2; c.insets = new Insets(24, 10, 10, 10); add(btnVolver, c); // Tercera fila: el botón, con más espacio arriba.

        Tema.alCambiar(this::colorear); // Vuelve a colorear los textos cuando cambie el tema.
        colorear(); // Pone los colores iniciales.
    }

    private PanelPixel crearTarjeta(int mascota, String texto, String monto) { // Método interno: arma una tarjeta de premio.
        PanelPixel tarjeta = new PanelPixel(new GridBagLayout()); // Tarjeta con borde y sombra.
        tarjeta.setPadding(18); // Espacio interno de 18 píxeles.

        JLabel imagen = new JLabel(new ImageIcon(Sprites.escalar(Sprites.mascota(mascota), TAM_MASCOTA, TAM_MASCOTA))); // Mascota del premio, escalada sin borrosidad.
        JLabel nombre = new JLabel("<html><div style='text-align:center; width:" + ANCHO_TEXTO + "px'>" + texto + "</div></html>", JLabel.CENTER); // Nombre del premio con ancho fijo para que se parta en líneas.
        nombre.setFont(Fuentes.texto(20f)); // El nombre usa la fuente de texto normal.
        nombre.setPreferredSize(new Dimension(ANCHO_TEXTO + 40, 60)); // Alto fijo para que todas las tarjetas queden del mismo tamaño.
        JLabel valor = new JLabel(monto, JLabel.CENTER); // Monto del premio.
        valor.setFont(Fuentes.texto(24f)); // El monto usa la fuente de texto, porque la de títulos no dibuja el símbolo ₡.
        nombres.add(nombre); // Guarda el nombre para colorearlo con el tema.
        montos.add(valor); // Guarda el monto para colorearlo con el tema.

        GridBagConstraints c = new GridBagConstraints(); // Objeto que guarda la posición de cada pieza de la tarjeta.
        c.gridx = 0; // Todo va en una sola columna.
        c.insets = new Insets(6, 6, 6, 6); // Margen alrededor de cada pieza.
        c.gridy = 0; tarjeta.add(imagen, c); // Primera fila: la mascota.
        c.gridy = 1; tarjeta.add(nombre, c); // Segunda fila: el nombre.
        c.gridy = 2; tarjeta.add(valor, c); // Tercera fila: el monto.
        return tarjeta; // Devuelve la tarjeta ya armada.
    }

    @Override // Indica que se reemplaza el método de la clase padre.
    public void alEntrar() { // Se ejecuta cada vez que se muestra la pantalla.
        super.alEntrar(); // Pone la música de premios.
        Sonido.efecto("moneda"); // Suena el efecto de moneda al entrar.
    }

    private void colorear() { // Método interno: ajusta los colores según el tema.
        titulo.setForeground(Tema.acento()); // El título usa el color de acento.
        for (JLabel nombre : nombres) { // Recorre los nombres de los premios.
            nombre.setForeground(Tema.texto()); // Les pone el color de texto.
        }
        for (JLabel monto : montos) { // Recorre los montos de los premios.
            monto.setForeground(Tema.acento()); // Les pone el color de acento.
        }
    }
}