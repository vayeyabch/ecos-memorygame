package ecos.pantallas;

import ecos.app.Pantalla; // Clase padre de todas las pantallas.
import ecos.app.Tema; // Para usar los colores del tema actual.
import ecos.componentes.BotonMenu; // Botón estilo pixel.
import ecos.componentes.PanelPixel; // Panel con borde y sombra pixelados.
import ecos.recursos.Fuentes; // Fuentes pixel del juego.
import ecos.recursos.Sonido; // Para los efectos de sonido.
import ecos.recursos.Sprites; // Para la imagen de la mascota.
import java.awt.FlowLayout; // Layout para poner los botones en fila.
import java.awt.GridBagConstraints; // Reglas de posición dentro del GridBagLayout.
import java.awt.GridBagLayout; // Layout en cuadrícula para centrar el contenido.
import java.awt.Insets; // Márgenes entre componentes.
import javax.swing.ImageIcon; // Imagen lista para ponerse en un JLabel.
import javax.swing.JLabel; // Etiqueta para texto o imágenes.
import javax.swing.JPanel; // Panel simple para agrupar botones.

public class PantallaReglasClasico extends Pantalla {

    private static final int ANCHO_TEXTO = 560; // Ancho en píxeles del texto de las reglas.
    private static final int TAM_MASCOTA = 120; // Tamaño en píxeles de la mascota.

    private final JLabel titulo = new JLabel("Reglas: Clásico", JLabel.CENTER); // Título de la pantalla.
    private final JLabel reglas = new JLabel(); // Texto con las reglas del modo.

    public PantallaReglasClasico() { // Constructor de la pantalla.
        super("reglas_clasico", "menu"); // Su id es "reglas_clasico" y suena la música del menú.
        setLayout(new GridBagLayout()); // Usa GridBagLayout para centrar la tarjeta.

        JLabel mascota = new JLabel(new ImageIcon(Sprites.escalar(Sprites.mascota(1), TAM_MASCOTA, TAM_MASCOTA))); // Mascota del modo clásico, escalada sin borrosidad.
        titulo.setFont(Fuentes.titulo(22f)); // El título usa la fuente de títulos.
        reglas.setFont(Fuentes.texto(20f)); // Las reglas usan la fuente de texto normal.
        reglas.setText("<html><div style='width:" + ANCHO_TEXTO + "px'>" // Abre el bloque HTML con ancho fijo para que el texto se parta en líneas.
                + "1. Juegan dos personas y cada una tiene su propio tablero.<br><br>" // Regla 1.
                + "2. En tu turno volteas dos cartas.<br><br>" // Regla 2.
                + "3. Si son iguales, es una pareja y queda descubierta.<br><br>" // Regla 3.
                + "4. Si son distintas, se tapan de nuevo y pasa el turno.<br><br>" // Regla 4.
                + "5. Cada turno tiene un tiempo limitado, y lo marca la barra de arriba.<br><br>" // Regla 5.
                + "6. Gana quien encuentre todas las parejas con menos intentos." // Regla 6.
                + "</div></html>"); // Cierra el bloque HTML.

        BotonMenu btnJugar = new BotonMenu("Jugar", true); // Botón principal para ir al juego.
        BotonMenu btnVolver = new BotonMenu("Volver", false); // Botón secundario para volver al menú.
        btnJugar.addActionListener(e -> { Sonido.efecto("click"); irA("clasico"); }); // Suena el click y abre el modo clásico.
        btnVolver.addActionListener(e -> { Sonido.efecto("click"); volver(); }); // Suena el click y vuelve a la pantalla anterior.

        JPanel botones = new JPanel(new FlowLayout(FlowLayout.CENTER, 16, 0)); // Fila con los dos botones separados 16 píxeles.
        botones.setOpaque(false); // La hace transparente para que se vea el fondo de la tarjeta.
        botones.add(btnJugar); // Agrega el botón Jugar.
        botones.add(btnVolver); // Agrega el botón Volver.

        PanelPixel tarjeta = new PanelPixel(new GridBagLayout()); // Tarjeta con borde y sombra donde va todo el contenido.
        tarjeta.setPadding(24); // Espacio interno de 24 píxeles.
        GridBagConstraints c = new GridBagConstraints(); // Objeto que guarda la posición de cada pieza.
        c.gridx = 0; // Todo va en una sola columna.
        c.insets = new Insets(8, 8, 8, 8); // Margen alrededor de cada pieza.
        c.gridy = 0; tarjeta.add(mascota, c); // Primera fila: la mascota.
        c.gridy = 1; tarjeta.add(titulo, c); // Segunda fila: el título.
        c.gridy = 2; tarjeta.add(reglas, c); // Tercera fila: las reglas.
        c.gridy = 3; c.insets = new Insets(18, 8, 8, 8); tarjeta.add(botones, c); // Cuarta fila: los botones, con más espacio arriba.
        add(tarjeta); // Agrega la tarjeta al centro de la pantalla.

        Tema.alCambiar(this::colorear); // Vuelve a colorear los textos cuando cambie el tema.
        colorear(); // Pone los colores iniciales.
    }

    private void colorear() { // Método interno: ajusta los colores según el tema.
        titulo.setForeground(Tema.acento()); // El título usa el color de acento.
        reglas.setForeground(Tema.texto()); // Las reglas usan el color de texto.
    }
}